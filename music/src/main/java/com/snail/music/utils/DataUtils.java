package com.snail.music.utils;

import android.database.Cursor;

import com.snail.music.entity.Audio;
import com.snail.music.entity.VideoEntity;

import java.util.ArrayList;

/**
 * Created by ping on 2016/5/16.
 */
public class DataUtils {

    public static ArrayList<VideoEntity> convertorAllVideoInfos(Cursor cursor){
        cursor.moveToPosition(-1);
        ArrayList<VideoEntity> result=new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(VideoEntity.convertorFromCursor(cursor));
        }
        return result;
    }

    public static ArrayList<Audio> convertorAllAudioInfos(Cursor cursor){
        cursor.moveToPosition(-1);
        ArrayList<Audio> result=new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(Audio.getAudioFromCuosor(cursor));
        }
        return result;
    }

}
