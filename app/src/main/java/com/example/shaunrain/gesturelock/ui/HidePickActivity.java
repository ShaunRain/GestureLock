package com.example.shaunrain.gesturelock.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.adapter.ImagePickAdapter;
import com.example.shaunrain.gesturelock.model.ImageModel;
import com.example.shaunrain.gesturelock.utils.AudioUtil;
import com.example.shaunrain.gesturelock.utils.ImageUtil;
import com.example.shaunrain.gesturelock.utils.MediaUtil;
import com.example.shaunrain.gesturelock.utils.VideoUtil;

import java.util.ArrayList;

public class HidePickActivity extends AppCompatActivity {

    final int TYPE_IMAGE = 0;
    final int TYPE_VIDEO = 1;
    final int TYPE_MUSIC = 2;

    private int type;

    private Intent data;
    private Toolbar toolbar;
    public static MediaUtil mediaUtil;
    private RecyclerView rv_pick;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_pick);

        toolbar = (Toolbar) findViewById(R.id.pick_toolbar);

        setSupportActionBar(toolbar);

        rv_pick = (RecyclerView) findViewById(R.id.rv_pick);

        data = getIntent();
        type = data.getIntExtra("type", -1);
        ids = data.getIntegerArrayListExtra("ids");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnPick();
                setResult(RESULT_OK, data);
                finish();
            }
        });

        initContent();

    }

    public void initContent() {
        switch (type) {
            case TYPE_IMAGE:
                toolbar.setTitle("选择图片");
                mediaUtil = new ImageUtil(this);
                adapter = new ImagePickAdapter(this, ids, toolbar);
                layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                break;
            case TYPE_MUSIC:
                toolbar.setTitle("选择音频");
                mediaUtil = new AudioUtil(this);
                break;
            case TYPE_VIDEO:
                toolbar.setTitle("选择视频");
                mediaUtil = new VideoUtil(this);
                break;
        }
        rv_pick.setLayoutManager(layoutManager);
        rv_pick.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 10;
                outRect.right = 10;
                outRect.bottom = 10;
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = 10;
                }
            }
        });
        rv_pick.setAdapter(adapter);
    }


    public void returnPick() {
        switch (type) {
            case TYPE_IMAGE:
                ArrayList<ImageModel> list = ((ImagePickAdapter) adapter).getPickList();
                HideMediaActivity.fromPick = new ArrayList<>();
                for (ImageModel im : list)
                    HideMediaActivity.fromPick.add(im.thumbnail);
                data.putParcelableArrayListExtra("pickList", list);
                break;
            case TYPE_MUSIC:

                break;
            case TYPE_VIDEO:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        adapter.onDetachedFromRecyclerView(rv_pick);
        super.onDestroy();
    }
}
