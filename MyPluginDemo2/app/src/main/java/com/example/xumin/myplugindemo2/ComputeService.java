package com.example.xumin.myplugindemo2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ComputeService extends IComputor.Stub {

    @Override
    public int add(int anInt, int aLong) throws RemoteException {
        Log.e("ComputeService", " 插件计算了并返回 了结果");
        return anInt + aLong;
    }
}
