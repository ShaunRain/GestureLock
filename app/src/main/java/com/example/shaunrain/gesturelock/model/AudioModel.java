package com.example.shaunrain.gesturelock.model;

import android.os.Parcel;

/**
 * Created by ShaunRain on 16/2/20.
 */
public class AudioModel extends MediaModel {

    private int id;
    private String title;
    private String name;
    private String path;
    private int duration;
    private long size;
    private String mimeType;
    private int date_add;
    private int date_mod;

    public AudioModel(int id, String title, String name, String path, int duration, long size, String mimeType, int date_add, int date_mod) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.size = size;
        this.mimeType = mimeType;
        this.date_add = date_add;
        this.date_mod = date_mod;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getDate_add() {
        return date_add;
    }

    public void setDate_add(int date_add) {
        this.date_add = date_add;
    }

    public int getDate_mod() {
        return date_mod;
    }

    public void setDate_mod(int date_mod) {
        this.date_mod = date_mod;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
