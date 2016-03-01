package com.example.shaunrain.gesturelock.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.shaunrain.gesturelock.model.MediaModel;

import java.io.File;
import java.util.List;

/**
 * Created by ShaunRain on 16/2/18.
 */
public abstract class MediaUtil {

    public abstract Context getContext();

    /*Uri转Path*/
    public abstract MediaModel getMediaFromUri(Uri contentUri);

    /*扫描媒体库该类型所有文件*/
    public abstract List<? extends MediaModel> getList();

    /*查找所有该类型的隐藏文件*/
    public abstract List<? extends MediaModel> getHideList();

    /*删除系统媒体库记录和缓存*/
    public abstract <T extends MediaModel> void delSysMedia(T t);

    /*插入系统媒体库记录*/
    public abstract <T extends MediaModel> void addSysMedia(T t);

    /*隐藏媒体文件*/
    public boolean hideMedia(MediaModel mediaModel) {
        boolean success = false;
        String path = mediaModel.getPath();
        String oldName = mediaModel.getName();
        File hideFile = new File(path);
        if (hideFile.exists()) {
            String parent = hideFile.getParent();
            String newName = "." + oldName + ".lock";
            File newFile = new File(parent, newName);
            success = hideFile.renameTo(newFile);
            delSysMedia(mediaModel);
            mediaModel.setPath(newFile.getAbsolutePath());
            success &= mediaModel.save();
            Log.d("fromid", success + "hideMedia");
        }
        return success;
    }

    /*恢复媒体文件*/
    public boolean unhideMedia(MediaModel mediaModel) {
        boolean success = false;
        String path = mediaModel.getPath();
        String originName = mediaModel.getName();
        File hideFile = new File(path);
        if (hideFile.exists()) {
            String parent = hideFile.getParent();
            File originFile = new File(parent, originName);
            success = hideFile.renameTo(originFile);
            mediaModel.delete();
            mediaModel.setPath(originFile.getAbsolutePath());
            addSysMedia(mediaModel);
        }
        return success;
    }

}
