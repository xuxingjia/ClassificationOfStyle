package com.example.xuxingjia.classificationofstylelibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.example.xuxingjia.classificationofstyle.view.CustomChildView;
import com.example.xuxingjia.classificationofstyle.view.CustomDampView;
import com.example.xuxingjia.classificationofstyle.view.VerticalTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private VerticalTabLayout mTablayout;
    private CustomDampView mDamp_view;
    private List<String> strings;
    private List<CustomChildView> childViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTablayout = findViewById(R.id.vtl_tablayout);
        mDamp_view = findViewById(R.id.cdv_damp_view);
        initData();
    }

    /**
     *  初始化数据
     */
    private void initData() {
        if (strings==null){
            strings=new ArrayList<>();
        }else {
            strings.clear();
        }
        if (childViews==null){
            childViews=new ArrayList<>();
        }else {
            childViews.clear();
        }

        for (int i = 0; i < 40; i++) {
            strings.add("item"+i);
            CustomChildView customChildView = new CustomChildView(this);
            TextView textView = new TextView(this);
            textView.setText("item"+i);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(25f);
            customChildView.initChildView(textView);
            childViews.add(customChildView);
        }

        mDamp_view.setChildNumber(childViews);
        mTablayout.setData(strings);

        mTablayout.setLeftSelectListener(new VerticalTabLayout.LeftSelectListener() {
            @Override
            public void leftSelectListener(int postion) {
                mDamp_view.selectReightChild(postion);
            }
        });

        mDamp_view.setOnReightSelectListener(new CustomDampView.OnReightSelectListener() {
            @Override
            public void onReightSelectListener(int index) {
                mTablayout.setLeftSelect(index);
            }
        });
    }
}
