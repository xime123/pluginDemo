package com.example.xumin.myhostdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.qihoo360.replugin.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
}
