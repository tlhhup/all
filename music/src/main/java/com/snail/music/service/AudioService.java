package com.snail.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.snail.music.R;
import com.snail.music.entity.Audio;
import com.snail.music.ui.activity.AudioPlayerActivity;
import com.snail.music.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

public class AudioService extends Service {

    public static final String ACION_MEDIA_PREPARED = "acion_media_prepared";
    public static final String ACION_MEDIA_COMPLED = "acion_media_compled";
    public static final String ACION_MEDIA_FIRST = "ACION_MEDIA_FIRST";
    public static final String ACION_MEDIA_LAST = "ACION_MEDIA_LAST";

    private static final int VIEW_PRE = 1;//通知栏的上一个
    private static final int VIEW_NEXT = 2;//通知栏的下一个
    private static final int VIEW_CONTAINER = 3;//通知栏的整体布局

    public static final int MODE_ORDER = 0;//顺序播放
    public static final int MODE_SINGLE_REPEAT = 1;//单曲循环
    public static final int MODE_ALL_REPEAT = 2;//循环播放
    public static int mCurrentPlayMode = MODE_ORDER;//默认是顺序播放

    private AudioServiceBinder mAudioServiceBinder;
    private int mCurrentPlay;
    private ArrayList<Audio> mAudios;

    private MediaPlayer mMediaPlayer;
    private SharedPreferences mSp;

    @Override
    public IBinder onBind(Intent intent) {
        return mAudioServiceBinder;
    }

    @Override
    public void onCreate() {
        mAudioServiceBinder = new AudioServiceBinder();
        mSp = getSharedPreferences("music.cfg", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            boolean isFromNotification = intent.getBooleanExtra("isFromNotification", false);
            if (isFromNotification) {
                int view_action = intent.getIntExtra("view_action", -1);
                switch (view_action) {
                    case VIEW_CONTAINER:
                        notifyPrepared();
                        break;
                    case VIEW_NEXT:
                        mAudioServiceBinder.playNext(false);
                        break;
                    case VIEW_PRE:
                        mAudioServiceBinder.playPre(false);
                        break;
                }
            } else {
                mCurrentPlay = intent.getExtras().getInt("index");
                mAudios = (ArrayList<Audio>) intent.getExtras().getSerializable("audios");
                //播放歌曲
                mAudioServiceBinder.openAudio();
            }
        }
        //获取播放模式
        mCurrentPlayMode = getPlayMode();
        return START_STICKY;
    }

    public class AudioServiceBinder extends Binder {

        public void openAudio() {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                //设置数据
                mMediaPlayer.setDataSource(mAudios.get(mCurrentPlay).getPath());
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isPlaying() {
            return mMediaPlayer != null ? mMediaPlayer.isPlaying() : false;
        }

        public void pause() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            stopForeground(true);
        }

        public void start() {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
            //发送播放状态通知
            sendNotification();
        }

        public int getCurrentPosition() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        public void playPre(boolean needNotify) {
            if (mCurrentPlay > 0) {
                mCurrentPlay--;
                openAudio();
            } else {
                if (needNotify)
                    notifyFirstAndLast(ACION_MEDIA_FIRST);
            }
        }

        public void playNext(boolean needNotify) {
            if (mCurrentPlay < mAudios.size() - 1) {
                mCurrentPlay++;
                openAudio();
            } else {
                if (needNotify)
                    notifyFirstAndLast(ACION_MEDIA_LAST);
            }
        }

        public long getDuration() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getDuration();
            }
            return 0;
        }

        public void seekTo(int progress) {
            if (mMediaPlayer != null) {
                mMediaPlayer.seekTo(progress);
            }
        }

        //顺序-->单曲-->循环
        public void switchPlayMode() {
            if (mCurrentPlayMode == MODE_ORDER) {
                mCurrentPlayMode = MODE_SINGLE_REPEAT;
            } else if (mCurrentPlayMode == MODE_SINGLE_REPEAT) {
                mCurrentPlayMode = MODE_ALL_REPEAT;
            } else if (mCurrentPlayMode == MODE_ALL_REPEAT) {
                mCurrentPlayMode = MODE_ORDER;
            }
            savePlayMode();
        }

        public int getCurrentPlayMode() {
            return mCurrentPlayMode;
        }
    }

    private void savePlayMode() {
        mSp.edit().putInt("playMode", mCurrentPlay).commit();
    }

    private int getPlayMode() {
        return mSp.getInt("playMode", mCurrentPlayMode);
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完成
            notifyCompletion();

            autoPlay();
        }
    };

    //根据模式自动播放
    private void autoPlay() {
        switch (mCurrentPlayMode) {
            case MODE_ORDER:
                mAudioServiceBinder.playNext(false);
                break;
            case MODE_SINGLE_REPEAT:
                mAudioServiceBinder.openAudio();
                break;
            case MODE_ALL_REPEAT:
                if (mCurrentPlay == (mAudios.size() - 1)) {
                    mCurrentPlay = 0;
                    mAudioServiceBinder.openAudio();
                } else {
                    mAudioServiceBinder.playNext(false);
                }
                break;
        }
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //开始播放
            mAudioServiceBinder.start();
            //通知更新播放信息
            notifyPrepared();
        }
    };

    //通知显示当前播放的音乐
    private void notifyPrepared() {
        Intent intent = new Intent();
        intent.setAction(ACION_MEDIA_PREPARED);
        Bundle bundle = new Bundle();
        bundle.putSerializable("audioItem", mAudios.get(mCurrentPlay));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 通知播放完成
     */
    private void notifyCompletion() {
        Intent intent = new Intent(ACION_MEDIA_COMPLED);
        Bundle bundle = new Bundle();
        bundle.putSerializable("audioItem", mAudios.get(mCurrentPlay));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 通知是否是第一个和最后一个
     */
    private void notifyFirstAndLast(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void sendNotification() {
        Audio audio = mAudios.get(mCurrentPlay);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(true)//设置不能被用户消除
                .setSmallIcon(R.mipmap.ic_launcher)//
                .setTicker("正在播放：" + StringUtils.formatAudioName(audio.getTitle()))//
                .setWhen(System.currentTimeMillis())//
                .setContent(getRemoteView());

        startForeground(1, builder.build());
    }

    private RemoteViews getRemoteView() {
        Audio audio = mAudios.get(mCurrentPlay);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tv_song_name, StringUtils.formatAudioName(audio.getTitle()));
        remoteViews.setTextViewText(R.id.tv_artist_name, audio.getArtist());

        //点击pre按钮执行的intent
        Intent preIntent = createNotificationIntent(VIEW_PRE);
        PendingIntent preContentIntent = PendingIntent.getService(this, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_pre, preContentIntent);
        //点击next按钮执行的intent
        Intent nextIntent = createNotificationIntent(VIEW_NEXT);
        PendingIntent nextContentIntent = PendingIntent.getService(this, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next, nextContentIntent);
        //点击整个通知的布局执行的intent
        Intent containerIntent = createNotificationIntent(VIEW_CONTAINER);
        PendingIntent containerContentIntent = PendingIntent.getActivity(this, 2, containerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_container, containerContentIntent);

        return remoteViews;
    }

    private Intent createNotificationIntent(int viewAction) {
        Intent intent = new Intent(this, viewAction == VIEW_CONTAINER
                ? AudioPlayerActivity.class : AudioService.class);
        intent.putExtra("isFromNotification", true);
        intent.putExtra("view_action", viewAction);
        return intent;
    }

}
