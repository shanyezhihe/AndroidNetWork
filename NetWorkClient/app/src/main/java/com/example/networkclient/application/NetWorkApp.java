package com.example.networkclient.application;

import android.app.Application;
import android.content.Context;


/**
 * Created by pengdongyuan491 on 16/9/6.
 */
public class NetWorkApp extends Application{
    private String patchPath="/sdcard/patch.jar";
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
