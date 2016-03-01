package com.example.shaunrain.gesturelock.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.shaunrain.gesturelock.R;

public class AppFragment extends Fragment {

    public View fragment;
    public ListView appListView;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_app, container, false);
        appListView = (ListView) fragment.findViewById(R.id.applist);
        appListView.setAdapter(LockSettingActivity.adapter);
        fab = LockSettingActivity.fab;
        //onCreate 未结束 getWidth()/getHeight() 为 0,所以手动测量view
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        fab.measure(w, h);
        final int leave = fab.getMeasuredHeight() + fab.getPaddingBottom();
        final int back = fab.getPaddingBottom();
        appListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean isScroll = false;
            private int lastVisibleItem;
            private boolean isOut = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                    isScroll = true;
                else
                    isScroll = false;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (isScroll) {
                    if (firstVisibleItem > lastVisibleItem) {
                        if (!isOut) {
                            ObjectAnimator.ofFloat(fab, "translationY", leave).setDuration(500).start();
                            isOut = true;
                        }
                    }
                    if (firstVisibleItem < lastVisibleItem) {
                        if (isOut) {
                            ObjectAnimator.ofFloat(fab, "translationY", -back).setDuration(500).start();
                            isOut = false;
                        }
                    }
                    if (firstVisibleItem == lastVisibleItem)
                        return;
                    lastVisibleItem = firstVisibleItem;
                }

            }
        });
        return fragment;
    }
}
