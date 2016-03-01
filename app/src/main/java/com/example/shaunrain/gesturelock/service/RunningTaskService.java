package com.example.shaunrain.gesturelock.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.shaunrain.gesturelock.MyApplication;
import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.ui.CrystalActivity;
import com.example.shaunrain.gesturelock.ui.LockSettingActivity;

import java.util.List;
import java.util.Set;

/**
 * Created by ShaunRain on 16/1/20.
 */
public class RunningTaskService extends Service {

    private Thread thread;
    private MyApplication myApplication = null;
    private ActivityManager activityManager;
    private SharedPreferences sp;
    private Set<String> lockApps;
    private Intent toLock;
    public static boolean flag;
    private String lastApp;
    private String currentApp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getSharedPreferences("lock_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("islock", true);
        editor.commit();

        myApplication = (MyApplication) getApplication();
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        toLock = new Intent(this, CrystalActivity.class);
        // 服务里面是没有任务栈的，所以要指定一个新的任务栈，不然是无法在服务里面启动activity的
        toLock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        flag = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    lockApps = sp.getStringSet("lockapps", null);
                    String packageName = null;
                    if (Build.VERSION.SDK_INT >= 21) {
                        List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
                        packageName = tasks.get(0).processName;
                    } else {
                        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager
                                .getRunningTasks(1);
                        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                        ComponentName topActivity = runningTaskInfo.topActivity;
                        if (topActivity != null) {
                            packageName = topActivity.getPackageName();
                        }
                    }
                    lastApp = currentApp;
                    currentApp = packageName;
                    Log.d("AllApp", currentApp + "," + lastApp);
                    if (lockApps != null && lockApps.contains(packageName) && !currentApp.equals(lastApp)
                            && !lastApp.equals("com.example.shaunrain.gesturelock")) {
                        //flag = false;
                        toLock.putExtra("lockapp", packageName);

                        startActivity(toLock);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

        Intent notiIntent = new Intent(this, LockSettingActivity.class);
        notiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, 0);
        Notification notification = new Notification.Builder(this).setContentTitle("一把锁已挂")
                .setSmallIcon(R.drawable.small_lock)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_lock))
                .setContentText("点击进入设置")
                .setContentIntent(pendingIntent).build();
        startForeground(Notification.FLAG_FOREGROUND_SERVICE, notification);
    }


    @Override
    public void onDestroy() {
        Log.d("AllApp", "destroyed");
        super.onDestroy();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("islock", false);
        editor.commit();
        flag = false;
        stopForeground(true);
    }
}
