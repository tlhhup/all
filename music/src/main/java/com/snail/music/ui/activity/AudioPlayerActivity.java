package com.snail.music.ui.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.music.R;
import com.snail.music.entity.Audio;
import com.snail.music.entity.Lyric;
import com.snail.music.service.AudioService;
import com.snail.music.ui.view.LyricView;
import com.snail.music.utils.LyricLoader;
import com.snail.music.utils.LyricParser;
import com.snail.music.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ping on 2016/5/23.
 */
public class AudioPlayerActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.btn_back)
    ImageView mBtnBack;
    @InjectView(R.id.tv_title)
    TextView mTv_Audio_Title;
    @InjectView(R.id.iv_anim)
    ImageView mIvAnim;
    @InjectView(R.id.tv_artist)
    TextView mTv_Audio_Artist;
    @InjectView(R.id.lyricView)
    LyricView mLyricView;
    @InjectView(R.id.tv_time)
    TextView mTvTime;
    @InjectView(R.id.seekbar)
    SeekBar mSeekbar;
    @InjectView(R.id.btn_paly_mode)
    ImageView mBtnPalyMode;
    @InjectView(R.id.btn_pre)
    ImageView mBtnPre;
    @InjectView(R.id.btn_play)
    ImageView mBtnPlay;
    @InjectView(R.id.btn_next)
    ImageView mBtnNext;
    private AudioServiceConnnection mAudioServiceConnnection;
    private AudioService.AudioServiceBinder mAudioServiceBinder;
    private AudioBroadcastReceiver mAudioBroadcastReceiver;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_audio_player);
        ButterKnife.inject(this);
        //获取动画
        AnimationDrawable animationDrawable = (AnimationDrawable) mIvAnim.getBackground();
        animationDrawable.start();
    }

    @Override
    protected void initEvent() {
        this.mBtnBack.setOnClickListener(this);
        this.mBtnNext.setOnClickListener(this);
        this.mBtnPalyMode.setOnClickListener(this);
        this.mBtnPlay.setOnClickListener(this);
        this.mBtnPre.setOnClickListener(this);

        this.mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                //设置播放进度
                mAudioServiceBinder.seekTo(progress);
                mTvTime.setText(StringUtils.formatDuration(progress)+"/" + StringUtils.formatDuration(mAudioServiceBinder.getDuration()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
            }
        });
    }

    @Override
    protected void initData() {
        System.out.println("initData");
        //注册广播接受者
        registerAudioBroadcastReceiver();
        //获取数据
        Intent audioService = new Intent(this, AudioService.class);
        Intent intent = getIntent();
        if (intent != null) {
            boolean isFromNotification = intent.getBooleanExtra("isFromNotification", false);
            if(isFromNotification){
                //表面是通知开启的activity
                audioService.putExtra("isFromNotification", isFromNotification);
                audioService.putExtra("view_action", getIntent().getIntExtra("view_action", -1));
            }else {
                int currentIndex = getIntent().getExtras().getInt("index");
                ArrayList<Audio> audios = (ArrayList<Audio>) getIntent().getExtras().getSerializable("audios");
                Bundle bundle = new Bundle();
                bundle.putInt("index", currentIndex);
                bundle.putSerializable("audios", audios);
                audioService.putExtras(bundle);
            }
        }
        //创建连接对象
        mAudioServiceConnnection = new AudioServiceConnnection();
        //先启动后绑定
        startService(audioService);//-->该方法会调用onStartCommand方法
        bindService(audioService, mAudioServiceConnnection, Service.BIND_AUTO_CREATE);//-->该方法不会调用onStartCommand方法
    }

    private void registerAudioBroadcastReceiver() {
        mAudioBroadcastReceiver = new AudioBroadcastReceiver();
        IntentFilter filter = new IntentFilter(AudioService.ACION_MEDIA_PREPARED);
        filter.addAction(AudioService.ACION_MEDIA_COMPLED);
        filter.addAction(AudioService.ACION_MEDIA_LAST);
        filter.addAction(AudioService.ACION_MEDIA_FIRST);
        registerReceiver(mAudioBroadcastReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (mAudioServiceBinder.isPlaying()) {
                    mAudioServiceBinder.pause();
                } else {
                    mAudioServiceBinder.start();
                }
                updatePlayBtnBg();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                mAudioServiceBinder.playNext(true);
                break;
            case R.id.btn_pre:
                mAudioServiceBinder.playPre(true);
                break;
            case R.id.btn_paly_mode:
                mAudioServiceBinder.switchPlayMode();
                updatePlayModeBtnBg();
                break;
        }
    }

    /**
     * 更新播放模式按钮的背景
     */
    private void updatePlayModeBtnBg(){
        switch (mAudioServiceBinder.getCurrentPlayMode()) {
            case AudioService.MODE_ORDER:
                mBtnPalyMode.setBackgroundResource(R.drawable.selector_audio_mode_normal);
                break;
            case AudioService.MODE_SINGLE_REPEAT:
                mBtnPalyMode.setBackgroundResource(R.drawable.selector_audio_mode_single_repeat);
                break;
            case AudioService.MODE_ALL_REPEAT:
                mBtnPalyMode.setBackgroundResource(R.drawable.selector_audio_mode_all_repeat);
                break;
        }
    }

    private class AudioServiceConnnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioServiceBinder = (AudioService.AudioServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        if (mAudioServiceBinder != null) {
            unbindService(mAudioServiceConnnection);
        }
        if (mAudioBroadcastReceiver != null) {
            unregisterReceiver(mAudioBroadcastReceiver);
        }
        super.onDestroy();
    }

    private class AudioBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //开始播放
            if (intent.getAction().equals(AudioService.ACION_MEDIA_PREPARED)) {
                Audio audio = (Audio) intent.getExtras().getSerializable("audioItem");
                //设置数据
                mTv_Audio_Title.setText(audio.getTitle());
                mTv_Audio_Artist.setText(audio.getArtist());
                mSeekbar.setMax((int) audio.getDuration());
                mTvTime.setText("00:00/" + StringUtils.formatDuration(audio.getDuration()));
                mBtnPlay.setBackgroundResource(R.drawable.selector_btn_audio_pause);
                updatePlayBtnBg();
                //更新播放进度
                updatePlayProgress();

                updatePlayModeBtnBg();
                //更新歌词
                File lyricFile = LyricLoader.loadLyricFile(audio.getTitle());
                ArrayList<Lyric> lyrics = LyricParser.parseLyricFromFile(lyricFile);
                if (lyrics!=null&&lyrics.size()>0) {
                    mLyricView.setLyricList(lyrics);
                    updateLyrics();
                }
            } else if (intent.getAction().equals(AudioService.ACION_MEDIA_COMPLED)) {//播放完成
                Audio audioItem = (Audio) intent.getExtras().getSerializable("audioItem");

                mSeekbar.setProgress((int) audioItem.getDuration());
                mTvTime.setText(StringUtils.formatDuration(audioItem.getDuration())+"/"
                        +StringUtils.formatDuration(audioItem.getDuration()));
                mBtnPlay.setBackgroundResource(R.drawable.selector_btn_audio_play);
            } else if (intent.getAction().equals(AudioService.ACION_MEDIA_FIRST)) {
                Toast.makeText(AudioPlayerActivity.this, "已经是第一首", Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(AudioService.ACION_MEDIA_LAST)) {
                Toast.makeText(AudioPlayerActivity.this, "已经是最后一首", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateLyrics() {
        mLyricView.roll(mAudioServiceBinder.getCurrentPosition(),mAudioServiceBinder.getDuration());
        mHandler.sendEmptyMessage(MESSAGE_UPDATE_LYRIC);
    }

    private void updatePlayProgress() {
        mSeekbar.setProgress(mAudioServiceBinder.getCurrentPosition());
        String currentTime = StringUtils.formatDuration(mAudioServiceBinder.getCurrentPosition());
        mTvTime.setText(currentTime + "/" + StringUtils.formatDuration(mAudioServiceBinder.getDuration()));
        mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
    }

    private void updatePlayBtnBg() {
        mBtnPlay.setBackgroundResource(mAudioServiceBinder.isPlaying() ?
                R.drawable.selector_btn_audio_pause : R.drawable.selector_btn_audio_play);
    }

    private static final int MESSAGE_UPDATE_PROGRESS = 101;
    private static final int MESSAGE_UPDATE_LYRIC = 1;//更新歌词
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_PROGRESS:
                    updatePlayProgress();
                    break;
                case MESSAGE_UPDATE_LYRIC:
                    updateLyrics();
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("onNewIntent");
    }
}
