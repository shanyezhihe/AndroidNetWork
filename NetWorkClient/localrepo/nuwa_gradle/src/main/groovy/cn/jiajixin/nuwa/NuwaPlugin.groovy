package cn.jiajixin.nuwa

import cn.jiajixin.nuwa.util.NuwaAndroidUtils
import cn.jiajixin.nuwa.util.NuwaFileUtils
import cn.jiajixin.nuwa.util.NuwaMapUtils
import cn.jiajixin.nuwa.util.NuwaProcessor
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class NuwaPlugin implements Plugin<Project> {
    HashSet<String> includePackage = []
    HashSet<String> excludePackage = []
    HashSet<String> excludeClass = []
    def debugOn
    def lastBuildDir
    def patchFilePrefixName
    private static final String NUWA_DIR = "NuwaDir"

    private static final String MAPPING_TXT = "mapping.txt"
    private static final String HASH_TXT = "hash.txt"

    private static final String RELEASE = "Release"


    @Override
    void apply(Project project) {

        project.extensions.create("nuwa", NuwaExtension, project)

        project.afterEvaluate {
            def extension = project.extensions.findByName("nuwa") as NuwaExtension
            debugOn = extension.debugOn
            patchFilePrefixName = extension.patchFilePrefixName

            ////initial include classes and exclude classes
            extension.includePackage.each {
                includeName ->
                    includePackage.add(includeName.replace(".", "/"))
            }

            extension.excludePackage.add("android.support")
            extension.excludePackage.each {
                excludeName ->
                    excludePackage.add(excludeName.replace(".", "/"))
            }
            extension.excludeClass.each {
                excludeName ->
                    excludeClass.add(excludeName.replace(".", "/") + ".class")
            }

            println("include:" + includePackage)
            println("exclude:" + excludePackage)
            println("excludeClass:" + excludeClass)

            ////intial last build nuwa dir
            lastBuildDir = NuwaFileUtils.getFileFromProperty(project, NUWA_DIR)
            if (!lastBuildDir) {
                lastBuildDir = NuwaFileUtils.getFileDir(extension.patchPath)
            }
            println("lastBuildDir:" + lastBuildDir)

            ////
            project.android.applicationVariants.each { variant ->
                println("variant:" + variant.name)
                println("variant:" + variant.getDirName())

                if (variant.name.endsWith(RELEASE) || debugOn) {

                    ///////
                    def prepareTaskName = "check${variant.name.capitalize()}Manifest";
                    def prepareTask = project.tasks.findByName(prepareTaskName)
                    if (prepareTask) {
                        prepareTask.doFirst({
                            prepareBuild(project, variant);
                        })
                    } else {
                        println("not found task ${prepareTaskName}")
                    }

                    // 适配gradle1.5以上版本,之前一些类似dxTask等任务都集成到了gradle1.5引入的transformApi中
                    // 例如这里以transform开头的task.原版(Nuwa最初作者那个版本)无法使用的问题就是因为它之前是获取
                    // dxTask等任务获取不到导致的.
                    def dexTaskName = "transformClassesWithDexFor${variant.name.capitalize()}"
                    def dexTask = project.tasks.findByName(dexTaskName)
                    if (dexTask) {
                        def patchProcessBeforeDex = "patchProcessBeforeDex${variant.name.capitalize()}"
                        project.task(patchProcessBeforeDex) << {
                            patchProcess(project, variant, dexTask);
                        }
                        //insert task
                        def patchProcessBeforeDexTask = project.tasks[patchProcessBeforeDex]
                        patchProcessBeforeDexTask.dependsOn dexTask.taskDependencies.getDependencies(dexTask)
                        dexTask.dependsOn patchProcessBeforeDexTask
                    } else {
                        println("not found task:${dexTaskName}")
                    }

                }
            }

        }
    }

    void prepareBuild(def project, def variant) {
        //proguard map
        println("prepareBuild")

        //sign hack.apk
//        def projectDir = project.projectDir.absolutePath
//        projectDir = projectDir.subSequence(0, projectDir.lastIndexOf('/'));
//        def hackApkFile = new File(projectDir + '/extras/hack.apk');
//        def hackAssestApkFile = new File(projectDir + '/nuwa/src/main/assets/hack.apk');

//        FileUtils.copyFile(hackApkFile, hackAssestApkFile)
//        NuwaAndroidUtils.signedApk(variant, hackAssestApkFile)

        //apply mapping file
        File mappingProguardFile = new File(project.projectDir, "proguard-mapping.pro")
        mappingProguardFile.delete()
        mappingProguardFile.createNewFile()
        if (lastBuildDir) {
            def mappingFile = NuwaFileUtils.getVariantFile(lastBuildDir, variant, MAPPING_TXT)
            NuwaAndroidUtils.applymapping(mappingProguardFile, mappingFile)
        }

    }

    void patchProcess(def project, def variant, def dexTask) {
        println("nuwaJarBeforeDex")

        def dirName = variant.dirName
        def nuwaDir = new File("${project.buildDir}/outputs/nuwa")
        def outputDir = new File("${nuwaDir}/${dirName}")
//        FileUtils.deleteDirectory(outputDir)
        outputDir.mkdirs()


        //load last build class hash file
        Map hashMap
        def patchDir
        if (lastBuildDir) {
            patchDir = new File("${outputDir}/patch")
            patchDir.mkdirs()
            def oldhashFile = NuwaFileUtils.getVariantFile(lastBuildDir, variant, HASH_TXT)
            hashMap = NuwaMapUtils.parseMap(oldhashFile)
        }

        ////process jar or class file, generate class hash and find modified class
        def hashFile = new File(outputDir, HASH_TXT)
        hashFile.delete()
        hashFile.createNewFile()
        Set<File> inputFiles = dexTask.inputs.files.files
        inputFiles.each { inputFile ->
            def path = inputFile.absolutePath
            if (inputFile.isDirectory()) {
                NuwaProcessor.processClassPath(dirName, hashFile, inputFile, patchDir, hashMap, includePackage, excludePackage, excludeClass)
            } else if (path.endsWith(".jar")) {
                NuwaProcessor.processJar(hashFile, inputFile, patchDir, hashMap, includePackage, excludePackage, excludeClass)
            }

        }

        //dex and sign patch file
        if (patchDir && patchDir.exists()) {
            def patchFileName = "patch" + patchFilePrefixName + "-" + variant.name.toLowerCase() + ".apk"
//            File patchFile = new File(patchDir, patchFileName)
            File patchFile = new File(lastBuildDir.absolutePath + "/patches", patchFileName)
            patchFile.getParentFile().mkdirs()
            NuwaAndroidUtils.dex(project, patchDir, patchFile.absolutePath)
//            NuwaAndroidUtils.signedApk(variant, patchFile)
        }

        //copy mapping.txt for next proguard
        def mapFile = new File("${project.buildDir}/outputs/mapping/${variant.dirName}/mapping.txt")
        def newMapFile = new File("${outputDir}/mapping.txt");
        if (mapFile.exists()) {
            FileUtils.copyFile(mapFile, newMapFile)
        }

    }
}


