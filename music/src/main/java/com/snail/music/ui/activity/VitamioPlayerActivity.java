package com.snail.music.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.snail.music.R;
import com.snail.music.entity.VideoEntity;
import com.snail.music.utils.StringUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class VitamioPlayerActivity  extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.vv_video)
    VideoView mVvVideo;
    @InjectView(R.id.tv_name)
    TextView mTvVideoName;
    @InjectView(R.id.iv_battery)
    ImageView mIvBattery;
    @InjectView(R.id.tv_time)
    TextView mTvSytemTime;
    @InjectView(R.id.volumn_seekbar)
    SeekBar mVolumnSeekbar;
    @InjectView(R.id.ll_top_control)
    LinearLayout mLlTopControl;
    @InjectView(R.id.tv_current_position)
    TextView mTvCurrentPosition;
    @InjectView(R.id.play_seekbar)
    SeekBar mPlaySeekbar;
    @InjectView(R.id.tv_total_time)
    TextView mTvTotalTime;
    @InjectView(R.id.btn_exit)
    ImageView mBtnExit;
    @InjectView(R.id.btn_pre)
    ImageView mBtnPre;
    @InjectView(R.id.btn_play)
    ImageView mBtnPlay;
    @InjectView(R.id.btn_next)
    ImageView mBtnNext;
    @InjectView(R.id.btn_screen)
    ImageView mBtnScreen;
    @InjectView(R.id.ll_bottom_control)
    LinearLayout mLlBottomControl;

    private int mCurrentPlay;
    private ArrayList<VideoEntity> mVideoInfos;
    private AudioManager mAudioManager;
    private int mCurrentVolume;
    private BatteryChangeRecerver mBatteryChangeRecerver;

    private static final int UPDATE_SYSTEM_TIME = 101;
    private static final int HIDE_CONTROLLER = 102;
    private static final int UPDATE_PLAY_PROCESS = 103;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SYSTEM_TIME:
                    updateSystemTime();
                    break;
                case HIDE_CONTROLLER:
                    hideController();
                    break;
                case UPDATE_PLAY_PROCESS:
                    updataPlayProcess();
                    break;
            }
        }
    };

    private int mTopControllerHeight;
    private int mBottomControllerHeight;
    private GestureDetector mGestureDetector;
    private boolean isMute;
    private Uri mUri;

    @Override
    protected void initView() {
        if(!Vitamio.isInitialized(this)) return;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vitamio_player);
        ButterKnife.inject(this);
        this.mPlaySeekbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTopControllerHeight = mLlTopControl.getMeasuredHeight();
                mBottomControllerHeight = mLlBottomControl.getMeasuredHeight();
                mPlaySeekbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                //设置为隐藏
                ViewPropertyAnimator.animate(mLlBottomControl).translationY(mBottomControllerHeight).setDuration(0);
                ViewPropertyAnimator.animate(mLlTopControl).translationY(-mTopControllerHeight).setDuration(0);
            }
        });
    }

    @Override
    protected void initEvent() {

        this.mBtnExit.setOnClickListener(this);
        this.mBtnPlay.setOnClickListener(this);
        this.mBtnNext.setOnClickListener(this);
        this.mBtnPre.setOnClickListener(this);
        this.mBtnScreen.setOnClickListener(this);

        mVvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVvVideo.start();
                //设置播放背景
                mBtnPlay.setBackgroundResource(R.drawable.selector_btn_pause);
                //设置视频进度条
                mPlaySeekbar.setMax((int) mVvVideo.getDuration());
                mTvCurrentPosition.setText("00:00");
                mTvTotalTime.setText(StringUtils.formatDuration(mVvVideo.getDuration()));
                //更新视频播放进度
                updataPlayProcess();
            }
        });

        mVvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlaySeekbar.setProgress((int) mVvVideo.getDuration());
                mBtnPlay.setBackgroundResource(R.drawable.selector_btn_play);
                mHandler.removeMessages(UPDATE_PLAY_PROCESS);
            }
        });

        //播放错误监听
        mVvVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(VitamioPlayerActivity.this);
                        dialog.setTitle("提示");
                        dialog.setMessage("播放出错,点击确定退出播放器");
                        dialog.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialog.create().show();
                        break;
                }
                return true;
            }
        });

        //播放进度条
        mPlaySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVvVideo.seekTo(progress);
                    mTvCurrentPosition.setText(StringUtils.formatDuration(mVvVideo.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(HIDE_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        //音量控制条
        mVolumnSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    isMute = false;//静音
                    mCurrentVolume = progress;
                    updateVolume();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                showController();
                //移除隐藏控制面板
                mHandler.removeMessages(HIDE_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });
    }

    @Override
    protected void initData() {
        mUri = getIntent().getData();
        if(mUri!=null){
            //其他应用
            mVvVideo.setVideoURI(mUri);
            mBtnNext.setEnabled(false);
            mBtnPre.setEnabled(false);
            mTvVideoName.setText(mUri.getPath());
        }else {
            //传入的视频列表
            Bundle extras = getIntent().getExtras();
            mCurrentPlay = extras.getInt("index");
            mVideoInfos = (ArrayList<VideoEntity>) extras.getSerializable("videoInfos");
            play(mCurrentPlay);
        }

        //初始化音量
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumnSeekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        updateVolume();
        //处理电池电量
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryChangeRecerver = new BatteryChangeRecerver();
        registerReceiver(mBatteryChangeRecerver, filter);
        //显示系统时间
        updateSystemTime();
        //设置手势适配
        mGestureDetector = new GestureDetector(this, new MyGestureDetectorListener());
    }

    //播放
    private void play(int position) {
        if (mVideoInfos == null || mVideoInfos.size() == 0) {
            finish();
            return;
        }

        mBtnNext.setEnabled(mCurrentPlay != mVideoInfos.size() - 1 ? true : false);
        mBtnPre.setEnabled(mCurrentPlay != 0 ? true : false);

        VideoEntity videoEntity = mVideoInfos.get(position);
        mTvVideoName.setText(videoEntity.getTitle());
        mVvVideo.setVideoURI(Uri.parse(videoEntity.getPath()));
    }

    private void playPre() {
        if (mCurrentPlay == 0) {
            return;
        }
        mCurrentPlay--;
        play(mCurrentPlay);
    }

    private void playNext() {
        if (mCurrentPlay == mVideoInfos.size() - 1) {
            return;
        }
        mCurrentPlay++;
        play(mCurrentPlay);
    }


    //更新系统时间
    private void updateSystemTime() {
        this.mTvSytemTime.setText(StringUtils.getCurrentTime());
        mHandler.sendEmptyMessageDelayed(UPDATE_SYSTEM_TIME, 1000);
    }

    //更新音量
    private void updateVolume() {
        if (isMute) {
            mVolumnSeekbar.setProgress(0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        } else {
            mVolumnSeekbar.setProgress(mCurrentVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0);
        }
    }

    //更新播放进度
    private void updataPlayProcess() {
        mTvCurrentPosition.setText(StringUtils.formatDuration(mVvVideo.getCurrentPosition()));
        mPlaySeekbar.setProgress((int) mVvVideo.getCurrentPosition());

        mHandler.sendEmptyMessageDelayed(UPDATE_PLAY_PROCESS, 1000);
    }

    //更新全屏按钮
    private void updateScreenBtnBg() {
        mBtnScreen.setBackgroundResource(!mVvVideo.isFullScreen() ? R.drawable.selector_btn_fullscreen : R.drawable.selector_btn_defaultscreen);
    }

    private boolean isShowController = false;

    //控制面板操作
    private void hideController() {
        ViewPropertyAnimator.animate(mLlBottomControl).translationY(mBottomControllerHeight).setDuration(800);
        ViewPropertyAnimator.animate(mLlTopControl).translationY(-mTopControllerHeight).setDuration(800);
        isShowController = false;
    }

    private void showController() {
        ViewPropertyAnimator.animate(mLlBottomControl).translationY(0).setDuration(800);
        ViewPropertyAnimator.animate(mLlTopControl).translationY(0).setDuration(800);
        isShowController = true;

        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBatteryChangeRecerver != null) {
            unregisterReceiver(mBatteryChangeRecerver);
        }
        //移除所有的消息通知
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_next:
                playNext();
                break;
            case R.id.btn_pre:
                playPre();
                break;
            case R.id.btn_play:
                if (mVvVideo.isPlaying()) {
                    mVvVideo.pause();
                    mHandler.removeMessages(UPDATE_PLAY_PROCESS);
                    mBtnPlay.setBackgroundResource(R.drawable.selector_btn_play);
                } else {
                    mVvVideo.start();
                    mHandler.sendEmptyMessageDelayed(UPDATE_PLAY_PROCESS, 1000);
                    mBtnPlay.setBackgroundResource(R.drawable.selector_btn_pause);
                }
                break;
            case R.id.btn_screen:
                mVvVideo.switchScreen();
                updateScreenBtnBg();
                break;
        }
    }

    private final class BatteryChangeRecerver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                if (level > 80) {
                    mIvBattery.setImageResource(R.drawable.ic_battery_100);
                } else if (level > 60) {
                    mIvBattery.setImageResource(R.drawable.ic_battery_80);
                } else if (level > 40) {
                    mIvBattery.setImageResource(R.drawable.ic_battery_60);
                } else if (level > 20) {
                    mIvBattery.setImageResource(R.drawable.ic_battery_40);
                } else if (level > 10) {
                    mIvBattery.setImageResource(R.drawable.ic_battery_20);
                } else if (level > 0) {
                    mIvBattery.setImageResource(R.drawable.ic_battery_10);
                } else {
                    mIvBattery.setImageResource(R.drawable.ic_battery_0);
                }
            }
        }
    }

    private final class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            System.out.println("onSingleTapConfirmed");
            if (isShowController) {
                hideController();
            } else {
                showController();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onClick(mBtnPlay);
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            System.out.println("onScroll:distanceY-->"+distanceY+"distanceX--->"+distanceX);
            if(Math.abs(distanceY)>15) {
                if (distanceY > 0) {
                    mCurrentVolume++;
                } else {
                    mCurrentVolume--;
                }
                updateVolume();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            System.out.println("onDoubleTap");
            onClick(mBtnScreen);
            return super.onDoubleTap(e);
        }
    }

}
