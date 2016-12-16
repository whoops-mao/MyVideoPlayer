package com.rielmao.myvideoplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Riel on 2016/12/13.
 */

 class VideoAdapter extends ArrayAdapter<VideoItem> {
    private int mResource;
    private LayoutInflater mInflater;
    VideoAdapter(Context context, int resource, List<VideoItem> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        super.getView(position, convertView, parent);
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.thumb = (ImageView) convertView.findViewById(R.id.iv_thumb);
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        VideoItem item = getItem(position);
        if (item!=null) {
            holder.title.setText(item.name);
            holder.time.setText(item.createdTime);
            holder.thumb.setImageBitmap(item.thumb);
        }
        return convertView;
    }

    private static class ViewHolder{
        private  TextView title;
        private  TextView time;
        private  ImageView thumb;
    }
}
