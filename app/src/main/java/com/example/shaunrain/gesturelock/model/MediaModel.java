package com.example.shaunrain.gesturelock.model;

import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by ShaunRain on 16/2/21.
 */
public abstract class MediaModel extends DataSupport implements Parcelable {

    public abstract int getId();

    public abstract String getName();

    public abstract String getPath();

    public abstract void setPath(String path);

}
