package com.snail.music.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.snail.music.R;
import com.snail.music.adapter.MainViewPagerAdapter;
import com.snail.music.ui.fragement.AudioFragment;
import com.snail.music.ui.fragement.BaseFragement;
import com.snail.music.ui.fragement.VideoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.tab_video)
    TextView mTabVideo;
    @InjectView(R.id.tab_audio)
    TextView mTabAudio;
    @InjectView(R.id.indicate_line)
    View mIndicateLine;
    @InjectView(R.id.viewPager)
    ViewPager mViewPager;
    private int mIndicatorWidth;

    private MainViewPagerAdapter mAdapter;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        //初始化指示器
        mIndicatorWidth = getResources().getDisplayMetrics().widthPixels/2;
        mIndicateLine.getLayoutParams().width=mIndicatorWidth;
        mIndicateLine.requestLayout();
    }

    @Override
    protected void initEvent() {
        this.mTabVideo.setOnClickListener(this);
        this.mTabAudio.setOnClickListener(this);
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ViewPropertyAnimator.animate(mIndicateLine).translationX((position+positionOffset)*mIndicatorWidth).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                lightTitleAndIndicator();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        List<BaseFragement> fragements=new ArrayList<>();
        fragements.add(new VideoFragment());
        fragements.add(new AudioFragment());
        mAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), fragements);
        this.mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_audio:
                mViewPager.setCurrentItem(1);
                lightTitleAndIndicator();
                break;
            case R.id.tab_video:
                mViewPager.setCurrentItem(0);
                lightTitleAndIndicator();
                break;
        }
    }

    private void lightTitleAndIndicator(){
        int currentItem = this.mViewPager.getCurrentItem();
        //设置字体大小
        ViewPropertyAnimator.animate(mTabAudio).scaleX(currentItem==1?1.2f:1f).setDuration(200);
        ViewPropertyAnimator.animate(mTabAudio).scaleY(currentItem==1?1.2f:1f).setDuration(200);
        ViewPropertyAnimator.animate(mTabVideo).scaleX(currentItem==0?1.2f:1f).setDuration(200);
        ViewPropertyAnimator.animate(mTabVideo).scaleY(currentItem==0?1.2f:1f).setDuration(200);
        //设置字体颜色
        mTabVideo.setTextColor(currentItem==0?getResources().getColor(R.color.indicate_line):getResources().getColor(R.color.gray_white));
        mTabAudio.setTextColor(currentItem==1?getResources().getColor(R.color.indicate_line):getResources().getColor(R.color.gray_white));
    }

}
