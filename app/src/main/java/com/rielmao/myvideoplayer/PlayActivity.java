package com.rielmao.myvideoplayer;


import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class PlayActivity extends AppCompatActivity {
    public static final String LAST_PLAY_TIME = "last_play_time";
    private VideoView videoView;
    private TextView tv_title;
    private TextView tv_width_height;
    private TextView tv_size;
    private TextView tv_add_time;
    String path;
    private int mLastPlayedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Uri uri = getIntent().getData();
        path = uri.getPath();
        initUI();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }else{
            getData();
        }

        videoView.start();
    }

    private void initUI() {
        videoView = (VideoView) findViewById(R.id.video_view);
        tv_title = (TextView) findViewById(R.id.video_title);
        tv_width_height = (TextView) findViewById(R.id.video_width_height);
        tv_size = (TextView) findViewById(R.id.video_size);
        tv_add_time = (TextView) findViewById(R.id.video_create_time);
        videoView.setVideoPath(path);
        MediaController controller = new MediaController(this);
        videoView.setMediaController(controller);
    }

    public void getData() {

        String[] searchKey = new String[]{
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
        };
        String where = MediaStore.Video.Media.DATA+"='"+path+"'";
        String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                searchKey, where, null, sortOrder);
        if (cursor!=null){
            if (cursor.getCount()>0){
                cursor.moveToNext();
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String createTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                VideoItem item = new VideoItem(title,path,createTime);
                tv_title.setText(title);
                tv_width_height.setText(width+"*"+height);
                tv_add_time.setText(item.createdTime);
                tv_size.setText(String.valueOf(size/1024/1024)+"M");
            }else {
                tv_title.setText(R.string.unknown);
                tv_width_height.setText(R.string.unknown);
                tv_add_time.setText(R.string.unknown);
                tv_size.setText(String.valueOf(R.string.unknown));
            }
            cursor.close();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        mLastPlayedTime = videoView.getCurrentPosition();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_PLAY_TIME,videoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastPlayedTime = savedInstanceState.getInt(LAST_PLAY_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
        if (mLastPlayedTime>0)
            videoView.seekTo(mLastPlayedTime);
    }
}
