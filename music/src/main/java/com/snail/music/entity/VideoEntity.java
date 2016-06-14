package com.snail.music.entity;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by ping on 2016/5/16.
 */
public class VideoEntity implements Serializable{

    private String title;
    private String path;
    private long size;
    private long duration;

    public static VideoEntity convertorFromCursor(Cursor cursor){
        VideoEntity entity=new VideoEntity();
        entity.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        entity.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        entity.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        entity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        return entity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
