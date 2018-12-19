package com.example.xumin.myplugindemo2;


import android.os.IBinder;

import com.example.computorlib.INormalComputor;

public class NormalComputor implements INormalComputor {
    private static volatile NormalComputor instatnce;

    private NormalComputor() {
    }

    public static NormalComputor getInstatnce() {
        if (instatnce == null) {
            synchronized (NormalComputor.class) {
                if (instatnce == null) {
                    instatnce = new NormalComputor();
                }
            }
        }
        return instatnce;
    }


    @Override
    public int add(int a, int b) {
        return ComputUtil.add(a, b);
    }

}
