package com.snail.music.ui.fragement;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snail.music.R;
import com.snail.music.adapter.VideoCursorAdapter;
import com.snail.music.db.ResouceQueryHandler;
import com.snail.music.ui.activity.VideoPlayerActivity;
import com.snail.music.utils.DataUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VideoFragment extends BaseFragement {

    @InjectView(R.id.lv_video)
    ListView mLvVideo;
    private VideoCursorAdapter mAdapter;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    protected void initEvent() {
        this.mLvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                Bundle bundle=new Bundle();
                bundle.putInt("index",position);
                bundle.putSerializable("videoInfos", DataUtils.convertorAllVideoInfos(cursor));
                enterActivity(VideoPlayerActivity.class,bundle);
            }
        });
    }

    @Override
    protected void initData() {
        //设置数据
        mAdapter = new VideoCursorAdapter(mContext, null);
        mLvVideo.setAdapter(mAdapter);

        ResouceQueryHandler queryHandler = new ResouceQueryHandler(mContext.getContentResolver());
        String[] projection={MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
        queryHandler.startQuery(0,mAdapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
