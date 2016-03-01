package com.example.shaunrain.gesturelock.model;

import android.graphics.drawable.Drawable;

/**
 * Created by ShaunRain on 16/1/20.
 */
public class AppInfo {

    private String label;
    private String packagename;
    private Drawable icon;
    private boolean isLock;

    public AppInfo(String label, String packagename, Drawable icon) {
        this.label = label;
        this.packagename = packagename;
        this.icon = icon;
        isLock = false;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public String getLabel() {
        return label;
    }

    public String getPackagename() {
        return packagename;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isLock() {
        return isLock;
    }
}
