package com.example.shaunrain.gesturelock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andexert.library.RippleView;
import com.example.shaunrain.gesturelock.R;

public class MediaFragment extends Fragment implements View.OnClickListener {

    public View fragment;
    public RippleView lockImage;
    public RippleView lockVideo;
    public RippleView lockMusic;
    final int TYPE_IMAGE = 0;
    final int TYPE_VIDEO = 1;
    final int TYPE_MUSIC = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_media, container, false);
        initItems();
        return fragment;
    }

    public void initItems() {
        lockImage = (RippleView) fragment.findViewById(R.id.lockimage);
        lockVideo = (RippleView) fragment.findViewById(R.id.lockvideo);
        lockMusic = (RippleView) fragment.findViewById(R.id.lockmusic);
        lockImage.setOnClickListener(this);
        lockVideo.setOnClickListener(this);
        lockMusic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent mediaIntent = null;
        switch (v.getId()) {
            case R.id.lockimage:
                mediaIntent = new Intent(getActivity(), HideMediaActivity.class);
                mediaIntent.putExtra("type", TYPE_IMAGE);
                break;
            case R.id.lockvideo:
                mediaIntent = new Intent(getActivity(), HideMediaActivity.class);
                mediaIntent.putExtra("type", TYPE_VIDEO);
                break;
            case R.id.lockmusic:
                mediaIntent = new Intent(getActivity(), HideMediaActivity.class);
                mediaIntent.putExtra("type", TYPE_MUSIC);
                break;
        }
        if (mediaIntent != null) {
            LockSettingActivity.openMedia = true;
            startActivity(mediaIntent);
        }
    }
}
