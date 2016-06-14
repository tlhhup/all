package com.snail.music.ui.fragement;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snail.music.R;
import com.snail.music.adapter.AudioCursorAdapter;
import com.snail.music.db.ResouceQueryHandler;
import com.snail.music.ui.activity.AudioPlayerActivity;
import com.snail.music.utils.DataUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AudioFragment extends BaseFragement {

    @InjectView(R.id.lv_audio)
    ListView mLvAudio;

    private AudioCursorAdapter mAdapter;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    protected void initEvent() {
        mLvAudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle=new Bundle();
                bundle.putInt("index",position);
                bundle.putSerializable("audios",DataUtils.convertorAllAudioInfos((Cursor) mAdapter.getItem(position)));
                enterActivity(AudioPlayerActivity.class,bundle);
            }
        });
    }

    @Override
    protected void initData() {
        //设置数据
        mAdapter = new AudioCursorAdapter(mContext, null);
        mLvAudio.setAdapter(mAdapter);

        ResouceQueryHandler queryHandler=new ResouceQueryHandler(mContext.getContentResolver());
        String[] projection={Media._ID, Media.DATA,Media.TITLE,Media.ARTIST,Media.DURATION};
        queryHandler.startQuery(0,mAdapter, Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
