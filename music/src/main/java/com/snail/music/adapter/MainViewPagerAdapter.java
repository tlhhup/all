package com.snail.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snail.music.ui.fragement.BaseFragement;

import java.util.List;

/**
 * Created by ping on 2016/5/16.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private final List<BaseFragement> mFragments;

    public MainViewPagerAdapter(FragmentManager fm, List<BaseFragement> fragements) {
        super(fm);
        this.mFragments=fragements;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        if(mFragments!=null){
            return mFragments.size();
        }
        return 0;
    }
}
