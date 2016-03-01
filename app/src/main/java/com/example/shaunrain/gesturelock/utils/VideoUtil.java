package com.example.shaunrain.gesturelock.utils;

import android.content.Context;
import android.net.Uri;

import com.example.shaunrain.gesturelock.model.MediaModel;
import com.example.shaunrain.gesturelock.model.VideoModel;

import java.util.List;

/**
 * Created by ShaunRain on 16/2/18.
 */
public class VideoUtil extends MediaUtil {

    private Context mContext;

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public VideoModel getMediaFromUri(Uri contentUri) {
        return null;
    }

    @Override
    public List<VideoModel> getList() {
        return null;
    }

    @Override
    public List<? extends MediaModel> getHideList() {
        return null;
    }

    @Override
    public void delSysMedia(MediaModel mediaModel) {

    }

    @Override
    public void addSysMedia(MediaModel mediaModel) {

    }

    public VideoUtil(Context context) {
        this.mContext = context;
    }

}
