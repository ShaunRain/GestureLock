package com.example.shaunrain.gesturelock;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.litepal.LitePalApplication;

/**
 * Created by ShaunRain on 16/1/17.
 */
public class MyApplication extends LitePalApplication {

    public boolean first = false;
    public boolean change = false;
    public Bitmap backShot = null;
    public boolean common = true;

    @Override
    public void onCreate() {
        super.onCreate();
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }

    public void setBackShot(Bitmap backShot) {
        this.backShot = backShot;
    }

    public Bitmap getBackShot() {
        return backShot;
    }


}
