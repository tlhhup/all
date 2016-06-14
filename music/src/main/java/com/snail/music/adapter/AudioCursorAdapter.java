package com.snail.music.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.snail.music.R;
import com.snail.music.entity.Audio;
import com.snail.music.utils.StringUtils;

/**
 * Created by ping on 2016/5/23.
 */
public class AudioCursorAdapter extends CursorAdapter {

    public AudioCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context, R.layout.audio_item,null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        AudioHolder holder=getViewHolder(view);
        //设置数据
        Audio audio = Audio.getAudioFromCuosor(cursor);
        holder.title.setText(StringUtils.formatAudioName(audio.getTitle()));
        holder.artist.setText(audio.getArtist());
    }

    private AudioHolder getViewHolder(View view){
        AudioHolder holder= (AudioHolder) view.getTag();
        if(holder==null){
            holder=new AudioHolder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class AudioHolder{
        TextView title,artist;

        public AudioHolder(View view){
            title= (TextView) view.findViewById(R.id.tv_name);
            artist= (TextView) view.findViewById(R.id.tv_artist);
        }

    }

}
