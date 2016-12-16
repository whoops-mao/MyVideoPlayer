package com.rielmao.myvideoplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Riel on 2016/12/12.
 */

public class VideoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private VideoUpdateTask mVideoUpdateTask;
    private ListView mVideoListView;
    private ArrayList<VideoItem> mVideoList;

    private MenuItem mMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mVideoList = new ArrayList<>();
        mVideoListView = (ListView) findViewById(R.id.video_list);
        assert mVideoListView != null;
        mVideoListView.setOnItemClickListener(this);
        mVideoListView.setAdapter(new VideoAdapter(this, R.layout.list_item, mVideoList));
        updateVideoList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenuItem = menu.findItem(R.id.menu_refresh);
        if (mVideoUpdateTask != null && mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMenuItem.setTitle(R.string.in_refresh);
        } else {
            mMenuItem.setTitle(R.string.refresh);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                if (mVideoUpdateTask != null && mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mVideoUpdateTask.cancel(true);
                    mVideoUpdateTask = null;
                } else {
                    mVideoUpdateTask = new VideoUpdateTask();
                    mVideoUpdateTask.execute();
                    if (mMenuItem!=null)
                    mMenuItem.setTitle(R.string.in_refresh);
                }
            }
            break;
            case R.id.menu_about:{
                Intent i = new Intent(this,AboutActivity.class);
                startActivity(i);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void updateVideoList() {
        mVideoUpdateTask = new VideoUpdateTask();
        mVideoUpdateTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoItem item = mVideoList.get(position);
        Intent intent = new Intent(this,PlayActivity.class);
        intent.setData(Uri.parse(item.path));
        startActivity(intent);
    }

    private class VideoUpdateTask extends AsyncTask<Object, VideoItem, Void> {
        private ArrayList<VideoItem> mDataList = new ArrayList<>();
        @Override
        protected Void doInBackground(Object... params) {
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] searchKey = new String[]{
                    MediaStore.Video.Media.TITLE,   //title
                    MediaStore.Images.Media.DATA,   //location
                    MediaStore.Images.Media.DATE_ADDED  //added time
            };
           // String where = MediaStore.Video.Media.DATA + " like \"%" + "/Video" + "%\"";
            String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            Cursor cursor = getContentResolver().query(uri, searchKey, null, null, sortOrder);
            if (cursor != null) {
                while (cursor.moveToNext() && !isCancelled()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                    String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    VideoItem videoItem = new VideoItem(name, path, createdTime);
                    if (!mVideoList.contains(videoItem)) {
                        videoItem.createThumb();
                        publishProgress(videoItem);
                    }
                    mDataList.add(videoItem);
                }
                cursor.close();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(VideoItem... values) {
            super.onProgressUpdate(values);
            VideoItem videoItem = values[0];
            mVideoList.add(videoItem);
            VideoAdapter adapter = (VideoAdapter) mVideoListView.getAdapter();
            adapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
                updateResult();
        }

        private void updateResult() {
            for (int i = 0; i < mVideoList.size(); i++) {
                if (!mDataList.contains(mVideoList.get(i))){
                    mVideoList.get(i).releaseThumb();
                    mVideoList.remove(i);
                    i--;
                }
            }
            mDataList.clear();
            VideoAdapter adapter = (VideoAdapter) mVideoListView.getAdapter();
            adapter.notifyDataSetChanged();
            if (mMenuItem!=null)
            mMenuItem.setTitle(R.string.refresh);
        }

        @Override
        protected void onCancelled() {
            updateResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoUpdateTask != null && (mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING)) {
            mVideoUpdateTask.cancel(true);
        }
        mVideoUpdateTask = null;
    }
}
