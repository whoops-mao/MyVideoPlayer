package com.rielmao.myvideoplayer;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Riel on 2016/12/12.
 */

class VideoItem {
    String name;
    String path;
    String createdTime;
    Bitmap thumb;

    VideoItem(String name, String path, String createdTime) {
        this.name = name;
        this.path = path;
        SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日HH时mm分");
        Date d = new Date(Long.valueOf(createdTime)*1000);
        this.createdTime = sdf.format(d);
    }
    void createThumb(){
        if (this.thumb==null){
            this.thumb = ThumbnailUtils.createVideoThumbnail(path,MediaStore.Images.Thumbnails.MINI_KIND);
        }

    }

    void releaseThumb(){
        if (this.thumb!=null){
            this.thumb.recycle();
            this.thumb = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        VideoItem another = (VideoItem) o;
        return another.path.equals(this.path);
    }
}
