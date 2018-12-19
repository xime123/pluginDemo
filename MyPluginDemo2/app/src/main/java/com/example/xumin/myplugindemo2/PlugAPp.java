package com.example.xumin.myplugindemo2;

import android.app.Application;
import android.util.Log;

import com.qihoo360.replugin.RePlugin;

public class PlugAPp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ComputeService","插件application启动了  pid="+android.os.Process.myPid());
        RePlugin.registerPluginBinder("ComputeService",new ComputeService());
    }
}
