package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import sk.brecka.uxmobile.NativeEncoder;
import sk.brecka.uxmobile.ScreenBuffer;

/**
 * Created by matej on 25.8.2017.
 */

public class VideoRecorder extends BaseRecorder {
    private static final String TAG = "VideoRecorder";

    private static final int VIDEO_FRAMERATE = 1;
    private static final int VIDEO_RESOLUTION = 240;

    private NativeEncoder mEncoder;
    private ScreenBuffer mScreenBuffer;

    private Timer mVideoTimer;
    private TimerTask mVideoTask;

    private String mVideoPath;

    public VideoRecorder() {
    }

    @Override
    public void onFirstActivityStarted(Activity activity) {
        super.onFirstActivityStarted(activity);

        Log.i(TAG, "onActivityStarted: starting video recording");

        final String filename = String.valueOf(System.currentTimeMillis()) + ".mp4";

        // TODO: nezapisovat to na external storage
        mVideoPath = Environment.getExternalStorageDirectory().toString() + "/" + filename;
//        mVideoPath = mCurrentActivity.getFilesDir().toString() + "/" + filename;

        // TODO: toto z configu
        final int screenWidth = 384;
        final int screenHeight = 240;
        final int bitrate = 64_000;

        try {
            mEncoder = new NativeEncoder(screenWidth, screenHeight, 1, bitrate, mVideoPath);
            mScreenBuffer = new ScreenBuffer(screenWidth, screenHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        mVideoTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        captureFrame();
                        new FrameEncodingAsyncTask().execute();
                    }
                });

            }
        };

        mVideoTimer = new Timer();
        mVideoTimer.schedule(mVideoTask, 0, 1000 / VIDEO_FRAMERATE);
    }

    @Override
    public void onLastActivityStopped(Activity activity) {
        super.onLastActivityStopped(activity);

        Log.i(TAG, "onLastActivityStopped: stopping video recording");

        try {
            // TODO: handlovanie ak nenatocil nic
            mEncoder.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mVideoTask.cancel();
        mVideoTimer.cancel();

        mVideoTask = null;
        mVideoTimer = null;
    }

    private void captureFrame() {
        final View rootView = mCurrentActivity.getWindow().getDecorView().getRootView();

        if (rootView.getWidth() == 0 && rootView.getHeight() == 0) {
            // nie je co natacat
            return;
        }

        mScreenBuffer.drawToBuffer(rootView);
    }

    public File getOutput() {
        return new File(mVideoPath);
    }

    private class FrameEncodingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (!mScreenBuffer.isEmpty()) {
                    mEncoder.encodeFrame(mScreenBuffer.getBitmap());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
