package com.snail.music.entity;

import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import java.io.Serializable;

/**
 * Created by ping on 2016/5/23.
 */
public class Audio implements Serializable{

    private String title;
    private String artist;
    private long duration;
    private String path;

    public static Audio getAudioFromCuosor(Cursor cursor){
        Audio audio=new Audio();
        audio.setTitle(cursor.getString(cursor.getColumnIndex(Media.TITLE)));
        audio.setArtist(cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
        audio.setDuration(cursor.getLong(cursor.getColumnIndex(Media.DURATION)));
        audio.setPath(cursor.getString(cursor.getColumnIndex(Media.DATA)));
        return  audio;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
