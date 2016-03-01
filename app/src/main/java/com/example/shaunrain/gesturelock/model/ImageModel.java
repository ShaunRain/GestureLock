package com.example.shaunrain.gesturelock.model;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ShaunRain on 16/2/20.
 */
public class ImageModel extends MediaModel {

    private int id;
    private String title;
    private String name;
    private String path;
    private int width;
    private int height;
    private long size;
    private String mimeType;
    private int date_add;
    private int date_mod;
    private String thumbPath;

    public Bitmap thumbnail;

    public ImageModel(int id, String title, String name, String path, int width, int height, long size, String mimeType, int date_add, int date_mod) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.path = path;
        this.width = width;
        this.height = height;
        this.size = size;
        this.mimeType = mimeType;
        this.date_add = date_add;
        this.date_mod = date_mod;
        this.thumbPath = null;
    }

    @Override
    public String toString() {
        return "id: " + id + " path: " + path;
    }

    public ImageModel(Parcel source) {
        this.id = source.readInt();
        this.title = source.readString();
        this.name = source.readString();
        this.path = source.readString();
        this.width = source.readInt();
        this.height = source.readInt();
        this.size = source.readLong();
        this.mimeType = source.readString();
        this.date_add = source.readInt();
        this.date_mod = source.readInt();
        this.thumbPath = source.readString();
    }

    @Override
    public synchronized boolean save() {
        if (thumbnail != null) {
            Log.d("fromid", "save invoke");
            File imageThumb = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MediaThumb/", name);
            try {
                FileOutputStream fos = new FileOutputStream(imageThumb);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            thumbPath = imageThumb.getAbsolutePath();
            Log.d("ThumbGet?", thumbPath);
        }
        return super.save();
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

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(size);
        dest.writeString(mimeType);
        dest.writeInt(date_add);
        dest.writeInt(date_mod);
        dest.writeString(thumbPath);
    }

    public final static Parcelable.Creator<ImageModel> CREATOR = new Parcelable.Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel source) {
            return new ImageModel(source);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

}
