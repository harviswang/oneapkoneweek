package com.example.testgif;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;


public class MainActivity extends Activity {

    private PowerManager.WakeLock mWakeLock = null;
    private GifVideoImageView mGifVideoImageView;
    private static final String GIF_FILE_PATH = "animation/finish.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mGifVideoImageView = new GifVideoImageView(this);
        mGifVideoImageView.setGifVideoViewPath(GIF_FILE_PATH);
        mGifVideoImageView.startPlayGifVideoView();
        setContentView(mGifVideoImageView);
        getWakeLock();
    }

    public void getWakeLock() {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        mWakeLock.acquire();
        System.out.println("getWakeLock() acquire wakelock");
    }
}
