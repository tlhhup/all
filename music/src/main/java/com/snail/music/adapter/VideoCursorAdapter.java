package com.snail.music.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.snail.music.R;
import com.snail.music.entity.VideoEntity;
import com.snail.music.utils.StringUtils;

/**
 * Created by ping on 2016/5/16.
 */
public class VideoCursorAdapter extends CursorAdapter {

    public VideoCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context,R.layout.video_item,null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = getHolder(view);
        //设置数据
        VideoEntity videoEntity = VideoEntity.convertorFromCursor(cursor);
        viewHolder.tv_title.setText(videoEntity.getTitle());
        viewHolder.tv_size.setText(Formatter.formatFileSize(context,videoEntity.getSize()));
        viewHolder.tv_duration.setText(StringUtils.formatDuration(videoEntity.getDuration()));
    }

    private ViewHolder getHolder(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder=new ViewHolder(view);
            view.setTag(holder);
        }
        return holder;
    }

    class ViewHolder {
        TextView tv_title, tv_size, tv_duration;

        public ViewHolder(View view) {
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }

}
