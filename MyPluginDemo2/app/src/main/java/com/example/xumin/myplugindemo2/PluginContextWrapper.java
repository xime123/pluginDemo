package com.example.xumin.myplugindemo2;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qihoo360.replugin.RePlugin;

public class PluginContextWrapper extends ContextWrapper {
    private Resources pluginResources;
    private Context base;
    private AssetManager pluginAssets;
    private Resources.Theme pluginTheme;
    private ClassLoader pluginClassLoader;
    private LayoutInflater mLayoutInflater;
    private String packageName;

    public PluginContextWrapper(Context base) {
        super(base);
        this.base = base;

        if (RePlugin.isHostInitialized()) {
            //作为插件运行
            this.packageName = RePlugin.getPluginInfo(SdkImpl.PLUGIN_NAME).getPackageName();
            this.pluginResources = RePlugin.fetchResources(SdkImpl.PLUGIN_NAME);
            this.pluginAssets = pluginResources.getAssets();
            this.pluginClassLoader = RePlugin.fetchClassLoader(SdkImpl.PLUGIN_NAME);
        } else {
            this.packageName = base.getPackageName();
            this.pluginResources = base.getResources();
            this.pluginAssets = pluginResources.getAssets();
            this.pluginClassLoader = base.getClassLoader();
        }

        Resources.Theme superTheme = base.getTheme();
        pluginTheme = pluginResources.newTheme();
        pluginTheme.setTo(superTheme);
    }

    @Override
    public Resources getResources() {
        return pluginResources;
    }

    @Override
    public AssetManager getAssets() {
        return pluginAssets;
    }

    @Override
    public Resources.Theme getTheme() {
        return pluginTheme;
    }

    @Override
    public ClassLoader getClassLoader() {
        return pluginClassLoader;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            //Log.d("sdkplugin","wrapper getLayoutInflater called");
            if (mLayoutInflater == null) {
                //mInflater = PolicyManager.makeNewLayoutInflater(this);
                /*try {
                    Class<?> cls = Class
                            .forName("com.android.internal.policy.PolicyManager");
                    Method m = cls.getMethod("makeNewLayoutInflater",
                            Context.class);
                    mLayoutInflater = (LayoutInflater) m.invoke(null, this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }*/
                mLayoutInflater = new PluginLayoutInflater(this, pluginResources);
            }
            if (mLayoutInflater != null) {
                return mLayoutInflater;
            }
        }
        return super.getSystemService(name);
    }

    private static class PluginLayoutInflater extends LayoutInflater {
        private Resources pluginResources;
        protected PluginLayoutInflater(Context context, Resources pluginResources) {
            super(context);
            this.pluginResources = pluginResources;
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return this;
        }


        @Override
        public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
            //final Resources res = getContext().getResources();
            final Resources res = pluginResources;

            final XmlResourceParser parser = res.getLayout(resource);
            try {
                return inflate(parser, root, attachToRoot);
            } finally {
                parser.close();
            }
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) {
            //Log.d("sdkplugin","inflater onCreateView " + name);
            View view = null;
            try {
                if (!name.contains(".")) {
                    if (name.equals("View") || name.equals("ViewGroup")) {
                        view = createView(name, "android.view.", attrs);
                    } else {
                        view = createView(name, "android.widget.", attrs);
                    }
                } else {
                    view = createView(name, null, attrs);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InflateException e) {
                e.printStackTrace();
                Log.e("sdkplugin", "inflate " + name + " failed");
                //XXX 在三星4.3、testin上很多4.4的设备上竟然inflate FrameLayout失败?
                if ("FrameLayout".equals(name)) {
                  //  view = new FrameLayout(ContextHelper.getHostContext(getContext()), attrs);
                }
            }

            return view;
        }
    }
}