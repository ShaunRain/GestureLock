package com.example.shaunrain.gesturelock.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.adapter.ImageHideAdapter;
import com.example.shaunrain.gesturelock.model.ImageModel;
import com.example.shaunrain.gesturelock.model.MediaModel;
import com.example.shaunrain.gesturelock.utils.AudioUtil;
import com.example.shaunrain.gesturelock.utils.ImageUtil;
import com.example.shaunrain.gesturelock.utils.MediaUtil;
import com.example.shaunrain.gesturelock.utils.VideoUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HideMediaActivity extends SwipeBackActivity implements AppCompatCallback {

    final int TYPE_IMAGE = 0;
    final int TYPE_VIDEO = 1;
    final int TYPE_MUSIC = 2;

    AppCompatDelegate delegate;
    private SwipeBackLayout mSwipeBackLayout;

    private RecyclerView rv_hide;
    private List<MediaModel> hideList;
    private RecyclerView.Adapter adapter;
    private HideAdapterFactory factory;
    private RecyclerView.LayoutManager layoutManager;

    private int type;
    private String action;
    private String mimeType;
    public static MediaUtil mediaUtil;

    private boolean isEdit;

    private Intent pickIntent;
    private ProgressDialog progressDialog;

    public static List<Bitmap> fromPick;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                progressDialog.dismiss();
                pickIntent.putExtra("type", type);
                startActivityForResult(pickIntent, type);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_hide_media);

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        final Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        action = intent.getAction();
        mimeType = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && mimeType != null) {
            if (mimeType.startsWith("image/")) {
                type = TYPE_IMAGE;
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.media_toolbar);


        rv_hide = (RecyclerView) findViewById(R.id.rv_hide);
        switch (type) {
            case TYPE_IMAGE:
                toolbar.setTitle("隐藏图片");
                mediaUtil = new ImageUtil(this);
                adapter = new ImageHideAdapter(this, (List<ImageModel>) mediaUtil.getHideList());
                layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                break;
            case TYPE_MUSIC:
                toolbar.setTitle("隐藏音频");
                break;
            case TYPE_VIDEO:
                toolbar.setTitle("隐藏视频");
                break;
        }
        rv_hide.setItemAnimator(new DefaultItemAnimator());
        rv_hide.setLayoutManager(layoutManager);
        rv_hide.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        factory = new HideAdapterFactory(adapter);
        rv_hide.setAdapter(adapter);

        if (mimeType != null) {
            handleSingle(intent, TYPE_IMAGE);
        }

        delegate.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    factory.uneditMode();
                    isEdit = false;
                    invalidateOptionsMenu();
                } else
                    finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        isEdit = true;
                        factory.editMode();
                        break;
                    case R.id.action_delete:

                        break;
                    case R.id.action_recover:
                        factory.recovery();
                        break;
                }
                invalidateOptionsMenu();
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.media_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(HideMediaActivity.this, "稍等", "加载系统媒体库中...", false);
                pickIntent = new Intent(HideMediaActivity.this, HidePickActivity.class);
                if (type == TYPE_IMAGE) {

                    new Thread() {
                        @Override
                        public void run() {
                            //gallery = getBitmaps((List<ImageModel>) mediaUtil.getList());
                            pickIntent.putIntegerArrayListExtra("ids", getBitmaps((List<ImageModel>) mediaUtil.getList()));
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                } else {
                    handler.sendEmptyMessage(0);
                }
            }
        });

    }

    public ArrayList<Integer> getBitmaps(List<ImageModel> datas) {
        ContentResolver resolver = this.getContentResolver();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        for (ImageModel im : datas) {
            //bitmaps.add(ImageUtil.getThumb(resolver, im.getId()));
            ids.add(im.getId());
        }
        return ids;
    }

    public void handleSingle(Intent intent, int type) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            switch (type) {
                case TYPE_IMAGE:
                    mediaUtil = new ImageUtil(this);
                    break;
                case TYPE_VIDEO:
                    mediaUtil = new VideoUtil(this);
                    break;
                case TYPE_MUSIC:
                    mediaUtil = new AudioUtil(this);
                    break;
            }
            MediaModel item = mediaUtil.getMediaFromUri(uri);
            boolean success = mediaUtil.hideMedia(item);
            factory.addItem(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TYPE_IMAGE:
                    boolean success = true;
                    ArrayList<MediaModel> list = data.getParcelableArrayListExtra("pickList");
                    for (int i = 0; i < list.size(); i++) {
                        ImageModel im = (ImageModel) list.get(i);
                        im.thumbnail = fromPick.get(i);
                        success &= mediaUtil.hideMedia(im);
                        factory.addItem(im);
                    }
                    Toast.makeText(this, "" + success, Toast.LENGTH_LONG).show();
                    break;
                case TYPE_MUSIC:

                    break;
                case TYPE_VIDEO:

                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hide, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEdit) {
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_recover).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_recover).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        LockSettingActivity.openMedia = false;
        super.onDestroy();
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}

class HideAdapterFactory {
    RecyclerView.Adapter adapter;

    public HideAdapterFactory(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public void editMode() {

        try {
            adapter.getClass().getDeclaredMethod("editMode", null).invoke(adapter, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void uneditMode() {

        try {
            adapter.getClass().getDeclaredMethod("uneditMode", null).invoke(adapter, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void addItem(MediaModel item) {

        try {
            adapter.getClass().getDeclaredMethod("addItem", item.getClass().getSuperclass()).invoke(adapter, item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }


    public void recovery() {
        try {
            adapter.getClass().getDeclaredMethod("recoverItems", null).invoke(adapter, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
