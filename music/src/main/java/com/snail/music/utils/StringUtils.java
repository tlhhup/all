package com.snail.music.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ping on 2016/5/16.
 */
public class StringUtils {

    public static String formatDuration(long duration) {
        long hour = duration / (60 * 60 * 1000);
        long min = duration % (60 * 60 * 1000) / (60 * 1000);
        long sec = duration % (60 * 1000) / 1000;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        } else {
            return String.format("%02d:%02d", min, sec);
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    public static String formatAudioName(String audioName) {
        if (audioName.lastIndexOf(".") > 0)
            return audioName.substring(0, audioName.lastIndexOf("."));
        else {
            return audioName;
        }
    }

}
