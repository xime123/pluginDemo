package com.example.xumin.myhostdemo;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.computorlib.INormalComputor;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.qihoo360.replugin.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private ProgressBar pb;
    private TextView tipTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = findViewById(R.id.pb);
        tipTV = findViewById(R.id.tips);
    }

    public void installOuterPlugin(View view) {
        showTips();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String outerApk = "outer.apk";
                String apkPath = "external" + File.separator + outerApk;

                // 文件是否已经存在？直接删除重来
                String pluginFilePath = getFilesDir().getAbsolutePath() + File.separator + outerApk;
                Log.e("plugin", "pluginFilePath=" + pluginFilePath);
                File pluginFile = new File(pluginFilePath);
                if (pluginFile.exists()) {
                    FileUtils.deleteQuietly(pluginFile);
                }

                // 开始复制
                copyAssetsFileToAppFiles(apkPath, outerApk);
                PluginInfo info = null;
                if (pluginFile.exists()) {
                    info = RePlugin.install(pluginFilePath);
                    hideTips();
                    if (info != null) {
                        showTip2("安装外置插件成功");
                        Log.e("plugin", "安装外置插件成功");
                    } else {
                        showTip2("安装外置插件失败");
                        Log.e("plugin", "安装外置插件失败");
                    }
                }


            }
        }).start();

    }

    private void showTips() {
        pb.setVisibility(View.VISIBLE);
        tipTV.setVisibility(View.VISIBLE);
    }

    private void showTip2(final String tips) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipTV.setText(tips);
                tipTV.setVisibility(View.VISIBLE);
            }
        });

    }

    private void hideTips() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.GONE);
            }
        });

    }


    public void gotoOuterPage(View view) {
        //ARouter.getInstance().build("/plug/test/router").navigation();
        RePlugin.startActivity(MainActivity.this, RePlugin.createIntent("outer",
                "com.example.xumin.myplugdemo.MainActivity"));
    }


    /**
     * 从assets目录中复制某文件内容
     *
     * @param assetFileName assets目录下的Apk源文件路径
     * @param newFileName   复制到/data/data/package_name/files/目录下文件名
     */
    private void copyAssetsFileToAppFiles(String assetFileName, String newFileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        int buffsize = 1024;

        try {
            is = this.getAssets().open(assetFileName);
            fos = this.openFileOutput(newFileName, Context.MODE_PRIVATE);
            int byteCount = 0;
            byte[] buffer = new byte[buffsize];
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getComputor(View view) {
        ClassLoader classLoader = RePlugin.fetchClassLoader("inner");

        String clzzName = "com.example.xumin.myplugindemo2.NormalComputor";
        String INSTANCE_METHOD = "getInstatnce";
        try {
            //Class aClass = Class.forName(clzzName);
            Class aClass = classLoader.loadClass(clzzName);
            Method getInstance = aClass.getMethod(INSTANCE_METHOD);
            Method add = aClass.getMethod("add", int.class, int.class);
            int result = (int) add.invoke(getInstance.invoke(aClass), 3, 5);
            Log.e("ComputeService", "宿主通过反射得到结果 result=" + result + "pid=" + android.os.Process.myPid());
//            Class [] interfaces=aClass.getInterfaces();
//            if(interfaces!=null&&interfaces.length>0){
//                Log.e("getComputor", "interface name=" + interfaces[0].getName());
//            }
            INormalComputor obj = (INormalComputor)getInstance.invoke(aClass);
            int newResult=obj.add(3,8);
            Log.e("ComputeService", "宿主通过创建插件对象得到计算结果 newResult=" + newResult + "toString=" + obj.toString());
            // int result = obj.add(3, 4);
            //Log.e("getComputor", "result=" + result);
        } catch (Exception e) {
            Log.e("getComputor", "find class error e=" + e.getMessage());
        }

    }

    public void gotoInnerPage(View view) {
        RePlugin.startActivity(MainActivity.this, RePlugin.createIntent("inner",
                "com.example.xumin.myplugindemo2.MainActivity"));
    }


    public void testAIDL(View view) {
        IBinder b = RePlugin.fetchBinder("inner", "ComputeService");
        if (b == null) {
            return;
        }
        com.example.xumin.myplugindemo2.IComputor computor = com.example.xumin.myplugindemo2.IComputor.Stub.asInterface(b);
        try {
            int result = computor.add(3, 5);
            Log.e("ComputeService", "宿主得到结果 result=" + result + "pid=" + android.os.Process.myPid());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void gotoFragment(View view) {
        Intent intent = new Intent(this, TestFragmentActivity.class);
        startActivity(intent);
    }
}
