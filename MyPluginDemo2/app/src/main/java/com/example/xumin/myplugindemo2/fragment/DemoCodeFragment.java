package com.example.xumin.myplugindemo2.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.xumin.myplugindemo2.R;


/**
 * 描述类作用
 * <p>
 * 作者 coder
 * 创建时间 2017/7/6
 */

public class DemoCodeFragment extends DemoFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText("来自插件的fragment2");
    }
}
