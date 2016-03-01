package com.example.shaunrain.gesturelock.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.shaunrain.gesturelock.model.ImageModel;
import com.example.shaunrain.gesturelock.model.MediaModel;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShaunRain on 16/2/18.
 */
public class ImageUtil extends MediaUtil {
    private Context mContext;

    public ImageUtil(Context context) {
        this.mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }


    @Override
    public ImageModel getMediaFromUri(Uri contentUri) {
        ImageModel imageModel = null;
        ContentResolver mResolver = mContext.getContentResolver();
        Cursor cursor = mResolver.query(contentUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
            int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
            int date_add = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            int date_mod = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            imageModel = new ImageModel(id, title, name, path, width, height, size, mimeType, date_add, date_mod);
            imageModel.thumbnail = getThumb(mResolver, id);
        }
        cursor.close();
        Log.d("fromid", (imageModel.thumbnail == null) + "thumb");
        return imageModel;
    }

    public ImageModel getImageModelFromId(int id) {
        ImageModel im = null;
        ContentResolver mResolver = mContext.getContentResolver();
        Cursor cursor = mResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + "=" + id, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
            int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
            int date_add = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            int date_mod = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            im = new ImageModel(id, title, name, path, width, height, size, mimeType, date_add, date_mod);
            im.thumbnail = getThumb(mResolver, id);
        }
        cursor.close();
        Log.d("fromid", (im.thumbnail == null) + "thumb");
        return im;
    }

    /*
    通过id拿缩略图
     */
    public static Bitmap getThumb(ContentResolver resolver, int id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    }

    @Override
    public List<ImageModel> getList() {
        List<ImageModel> allImages = null;
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            allImages = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                int date_add = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int date_mod = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                ImageModel imageModel = new ImageModel(id, title, name, path, width, height, size, mimeType, date_add, date_mod);
                Log.d("MediaScan", imageModel.getName());
                allImages.add(imageModel);
            }
            cursor.close();
        }
        return allImages;
    }

    @Override
    public List<? extends MediaModel> getHideList() {
        List<ImageModel> list = DataSupport.findAll(ImageModel.class);
        for (Object im : list.toArray()) {
            Log.d("HideList", ((ImageModel) im).getThumbPath() + "");
        }
        return list;
    }

    @Override
    public <T extends MediaModel> void delSysMedia(T t) {
        ImageModel imageModel = (ImageModel) t;
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?",
                new String[]{String.valueOf(imageModel.getId())});
        contentResolver.delete(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                new String[]{String.valueOf(imageModel.getId())});
    }

    @Override
    public <T extends MediaModel> void addSysMedia(T t) {
        ImageModel imageModel = (ImageModel) t;
        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, imageModel.getTitle());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageModel.getName());
        values.put(MediaStore.Images.Media.DATA, imageModel.getPath());
        values.put(MediaStore.Images.Media.WIDTH, imageModel.getWidth());
        values.put(MediaStore.Images.Media.HEIGHT, imageModel.getHeight());
        values.put(MediaStore.Images.Media.SIZE, imageModel.getSize());
        values.put(MediaStore.Images.Media.MIME_TYPE, imageModel.getMimeType());
        values.put(MediaStore.Images.Media.DATE_ADDED, imageModel.getDate_add());
        values.put(MediaStore.Images.Media.DATE_MODIFIED, imageModel.getDate_mod());
        Uri contentUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


}
