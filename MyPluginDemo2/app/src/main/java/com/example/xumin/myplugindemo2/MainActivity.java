package com.example.xumin.myplugindemo2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ComputeService","toString="+NormalComputor.getInstatnce().toString());
        setContentView(R.layout.activity_main);
    }
}
