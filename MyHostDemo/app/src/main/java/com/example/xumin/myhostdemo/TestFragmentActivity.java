package com.example.xumin.myhostdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qihoo360.replugin.RePlugin;

public class TestFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 注意：
         *
         * 如果一个插件是内置插件，那么这个插件的名字就是文件的前缀，比如：demo1.jar插件的名字就是demo1(host-gradle插件自动生成)，可以执行诸如RePlugin.fetchClassLoader("demo1")的操作；
         * 如果一个插件是外置插件，通过RePlugin.install("/sdcard/demo1.apk")安装的，则必须动态获取这个插件的名字来使用：
         * PluginInfo pluginInfo = RePlugin.install("/sdcard/demo1.apk");
         * RePlugin.preload(pluginInfo);//耗时
         * String name = pluginInfo != null ? pluginInfo.getName() : null;
         * ClassLoader classLoader = RePlugin.fetchClassLoader(name);
         * com.example.xumin.myplugindemo2.fragment.DemoFragment
         */

        String pluginName = "inner";

        //注册相关Fragment的类
        //注册一个全局Hook用于拦截系统对XX类的寻找定向到Demo1中的XX类主要是用于在xml中可以直接使用插件中的类
        //com.example.xumin.myplugindemo2.fragment.DemoFragment
        String fragmentName = "com.example.xumin.myplugindemo2.fragment.DemoFragment";
        String fragmentName1 = "com.example.xumin.myplugindemo2.fragment.DemoCodeFragment";
        RePlugin.registerHookingClass(fragmentName, RePlugin.createComponentName(pluginName, fragmentName1), null);
        setContentView(R.layout.activity_test_fragment);

        //代码使用插件Fragment
        ClassLoader d1ClassLoader = RePlugin.fetchClassLoader(pluginName);//获取插件的ClassLoader
        try {
            Fragment fragment = d1ClassLoader.loadClass(fragmentName).asSubclass(Fragment.class).newInstance();//使用插件的Classloader获取指定Fragment实例
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();//添加Fragment到UI
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
