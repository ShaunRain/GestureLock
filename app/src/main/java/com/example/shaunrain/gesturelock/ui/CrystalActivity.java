package com.example.shaunrain.gesturelock.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.example.shaunrain.gesturelock.MyApplication;
import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.service.RunningTaskService;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CrystalActivity extends Activity {

    private Window window;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mediaProjectionManager = null;
    private MediaProjection mediaProjection = null;
    private WindowManager windowManager = null;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private DisplayMetrics displayMetrics = null;
    private ImageReader imageReader = null;
    private int screenDensity = 0;
    private VirtualDisplay virtualDisplay;
    private String path;
    private Bitmap back;
    private String lockApp;
    private boolean needBack;

    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent startLock = new Intent(CrystalActivity.this, LockMainActivity.class);
            startLock.putExtra("lockapp", lockApp);
            startLock.putExtra("needback", needBack);
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.setBackShot(back);
            startActivity(startLock);
            //RunningTaskService.flag = true;
            //RunningTaskService.currentApp = "";
            //Log.d("AllApp", RunningTaskService.currentApp);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        fade the virtual button.
         */
//        window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        window.setAttributes(params);

        setContentView(R.layout.activity_crystal);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lockApp = getIntent().getStringExtra("lockapp");
        needBack = getIntent().getBooleanExtra("needback", false);
        createVirtualEnviroment();

        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);

        AlertDialog alertDialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("Choose", );
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                stopService(new Intent(this, RunningTaskService.class));

                finish();
            } else if (data != null && resultCode != 0) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        //start virtual
                        virtualDisplay();
                    }
                }, 300);


                handler2.postDelayed(new Runnable() {
                    public void run() {
                        //capture the screen
                        startCapture();
                    }
                }, 700);

            }
        }
    }

    private void createVirtualEnviroment() {
//        if (Environment.isExternalStorageEmulated())
//            path = Environment.getExternalStorageDirectory().getPath() + "/Crystal/";
//        else
//            path = Environment.getDataDirectory().getPath() + "/Crystal/";
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MediaThumb/";
        File thumbFile = new File(path);
        if (!thumbFile.exists()) {
            thumbFile.mkdirs();
            File nomedia = new File(thumbFile, ".nomedia");
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenDensity = displayMetrics.densityDpi;
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
    }

    private void virtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror", screenWidth, screenHeight, screenDensity
                , DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
                , imageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = imageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();

        if (bitmap != null) {
//            try {
//                File imageFile = new File(path + "crystalShot.png");
//                if (imageFile.exists())
//                    imageFile.delete();
//                imageFile.createNewFile();
//                FileOutputStream out = new FileOutputStream(imageFile);
//                if (out != null) {
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    out.flush();
//                    out.close();
//                }
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            back = bitmap;
            handler2.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        this.overridePendingTransition(0, 0);
    }
}
