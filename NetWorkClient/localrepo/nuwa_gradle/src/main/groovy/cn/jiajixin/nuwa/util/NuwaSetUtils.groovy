package cn.jiajixin.nuwa.util

class NuwaSetUtils {
    public
    static boolean isExcluded(String path, Set<String> excludePackage, Set<String> excludeClass) {
        for (String exclude:excludeClass){
            if(path.equals(exclude)) {
                return  true;
            }
        }
        for (String exclude:excludePackage){
            if(path.startsWith(exclude)) {
                return  true;
            }
        }

        return false;
    }

    public static boolean isIncluded(String path, Set<String> includePackage) {
        if (includePackage.size() == 0) {
            return true
        }

        for (String include:includePackage){
//            if(path.startsWith(include)) {
            // 这里针对realm的gradle插件进行适配
            // 说明:在使用realm时,需要导入realm的gradle插件,这个插件也会在程序编译的时候对class进行修改,例如将
            // com.pingan.A.class修改成a.b.c.com.pingan.A.class,它会将原类放到一个自定义的包下,所以Nuwa之前
            // 的直接判断以包名起始来确定要处理的类是错误的,导致最终什么类都不会被处理,所以这里做了个正则匹配处理,匹
            // 配到以指定包名结尾的类,才认为是需要处理的类
            if(path.matches(".*" + include + ".*.class")) {
                return  true;
            }
        }

        return false;
    }
}
