package com.example.shaunrain.gesturelock.ui;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaunrain.gesturelock.view.LockPatternView;
import com.example.shaunrain.gesturelock.MyApplication;
import com.example.shaunrain.gesturelock.R;

public class LockMainActivity extends Activity implements LockPatternView.OnPatternChangeListener {

    private String pass;
    private int count = 0;

    private TextView lock_hint;
    private LockPatternView lock;
    private FrameLayout glass;
    private SharedPreferences sp;
    private MyApplication myApplication = null;
    private String lockApp;
    private ImageView lock_icon;

    private Boolean needBack;
    private long failedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApplication = (MyApplication) getApplication();
        Bitmap back = myApplication.getBackShot();
        //getWindow().getDecorView().setBackground(new BitmapDrawable(back));
        blur(back);
        lock_hint = (TextView) findViewById(R.id.lock_hint);
        lock = (LockPatternView) findViewById(R.id.lock);
        lock.setPatternListener(this);
        glass = (FrameLayout) findViewById(R.id.glass);
        lock_icon = (ImageView) findViewById(R.id.lock_icon);
        lockApp = getIntent().getStringExtra("lockapp");
        needBack = getIntent().getBooleanExtra("needback", false);
        try {
            lock_icon.setImageDrawable(getPackageManager().getApplicationIcon(lockApp));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sp = getSharedPreferences("lock_data", Context.MODE_PRIVATE);

        pass = sp.getString("password", null);
        count = sp.getInt("count", 0);
        failedTime = sp.getLong("failedtime", 0l);
        long gap = SystemClock.elapsedRealtime() - failedTime;
        if (failedTime > 0 && gap < 60000) {
            glass.setClickable(true);
            lock_hint.setText("抱歉, 一会儿再来吧");
            lock_hint.setTextColor(getResources().getColor(R.color.color_normal));
        } else {
            onPatternReset();
            if (pass == null)
                lock_hint.setText("无密码 请设置!");
            if (myApplication.change)
                lock_hint.setText("旧密码");
        }

    }

    @Override
    public void onPatternChange(String password) {
        if (!TextUtils.isEmpty(password)) {
            SharedPreferences.Editor editor = sp.edit();
            //还没密码
            if (pass == null) {
                pass = password;
                editor.putString("password", pass);
                editor.commit();
                Toast.makeText(this, "设置密码成功!", Toast.LENGTH_SHORT).show();
                if (myApplication.first || myApplication.change) {
                    startActivity(new Intent(LockMainActivity.this, LockSettingActivity.class));
                    myApplication.first = false;
                    myApplication.change = false;
                }
                finish();
            }
            //有密码
            else {
                if (password.equals(pass)) {
                    if (myApplication.change) {
                        lock.resetPoint();
                        pass = null;
                    } else {
                        Toast.makeText(this, "解锁成功!", Toast.LENGTH_SHORT).show();
                        editor.putInt("count", 0);
                        editor.commit();
                        if (myApplication.common && needBack) {
                            myApplication.common = false;
                            startActivity(new Intent(LockMainActivity.this, LockSettingActivity.class));
                        }
                        finish();
                    }
                } else {
                    lock.beError();
                    count++;
                    lock_hint.setText("密码错误! 可尝试次数: " + (5 - count));
                    if (count >= 5) {
                        failedTime = SystemClock.elapsedRealtime();
                        editor.putLong("failedtime", failedTime);
                        editor.commit();
                        lock.disablePoint();
                    } else {
                        editor.putInt("count", count);
                        editor.commit();
                    }
                }
            }

        }
    }

    @Override
    public void onPatternError() {
        lock_hint.setText("至少5点!");
        lock_hint.setTextColor(getResources().getColor(R.color.color_error));
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(300);
    }

    @Override
    public void onPatternReset() {
        glass.setClickable(false);
        lock_hint.setTextColor(getResources().getColor(R.color.color_normal));
        if (myApplication.change) {
            lock_hint.setText("新密码");
        } else
            lock_hint.setText("请绘制密码以打开");
    }

    @Override
    public void onPatternStart(boolean isStart) {
        if (isStart) {
            lock_hint.setText("继续以完成绘制");
            lock_hint.setTextColor(getResources().getColor(R.color.color_press));
        }
    }

    @Override
    public void onPatternDisable() {
        lock_hint.setTextColor(getResources().getColor(R.color.color_disable));
        lock_hint.setText("连续多次错误, 稍后再试!");
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(300);
        glass.setClickable(true);
    }

    private void blur(Bitmap bkg) {
        long startMs = System.currentTimeMillis();
        float radius = 20;

        bkg = small(bkg);
        Bitmap bitmap = bkg.copy(bkg.getConfig(), true);

        final RenderScript rs = RenderScript.create(this);
        final Allocation input = Allocation.createFromBitmap(rs, bkg, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        bitmap = big(bitmap);
        this.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        rs.destroy();
    }

    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1f, 1f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.2f, 0.2f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }

}
