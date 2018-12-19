package com.example.xumin.myplugindemo2;

import android.util.Log;

public class ComputUtil {
    public static int add(int a, int b) {
        Log.e("ComputeService", "执行了ComputUtil函数");
        return a + b;
    }
}
