package com.snail.music.ui.fragement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snail.music.ui.activity.BaseActivity;

/**
 * Created by ping on 2016/5/16.
 */
public abstract class BaseFragement extends Fragment implements View.OnClickListener{

    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater,container,savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        initData();
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected void initEvent(){

    }

    protected void initData(){

    }

    protected void progressOnclick(View view){

    }

    public <T extends BaseActivity> void enterActivity(Class<T> clazz, Bundle bundle){
        Intent intent=new Intent(mContext,clazz);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        progressOnclick(v);
    }
}
