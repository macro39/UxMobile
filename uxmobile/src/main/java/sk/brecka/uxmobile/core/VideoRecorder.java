package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import sk.brecka.uxmobile.NativeEncoder;
import sk.brecka.uxmobile.ScreenBuffer;
import sk.brecka.uxmobile.util.Config;

/**
 * Created by matej on 25.8.2017.
 */

public class VideoRecorder extends BaseRecorder {
    private NativeEncoder mEncoder;
    private ScreenBuffer mScreenBuffer;

    private Timer mVideoTimer;
    private TimerTask mVideoTask;

    private String mVideoPath;

    private boolean mIsRecording = false;

    public VideoRecorder() {
    }

    @Override
    public void onSessionStarted() {

        mIsRecording = Config.get().isRecordingVideo();

        if (!mIsRecording) {
            return;
        }

        Log.i("UxMobile", "onSessionStarted: starting video recording");

        final String filename = String.valueOf(System.currentTimeMillis()) + ".mp4";

        // TODO: nezapisovat to na external storage
        mVideoPath = mCurrentActivity.getFilesDir().toString() + "/" + filename;

        final int screenWidth = Config.get().getVideoWidth();
        final int screenHeight = Config.get().getVideoHeight();
        final int bitrate = Config.get().getVideoBitrate();
        final int framerate = Config.get().getVideoFps();

        try {
            mEncoder = new NativeEncoder(screenWidth, screenHeight, framerate, bitrate, mVideoPath);
            mScreenBuffer = new ScreenBuffer(screenWidth, screenHeight);
        } catch (IOException e) {
            Log.e("UxMobile", "doInBackground: ", e);
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
        mVideoTimer.schedule(mVideoTask, 0, 1000 / framerate);
    }

    @Override
    public void onLastActivityStopped(Activity activity) {
        super.onLastActivityStopped(activity);

        if (!mIsRecording) {
            return;
        }

        mIsRecording = false;

        Log.i("UxMobile", "onLastActivityStopped: stopping video recording");

        try {
            // TODO: handlovanie ak nenatocil nic
            mEncoder.finish();
        } catch (IOException e) {
            Log.e("UxMobile", "doInBackground: ", e);
        }

        mVideoTask.cancel();
        mVideoTimer.cancel();

        mVideoTask = null;
        mVideoTimer = null;
    }

    private void captureFrame() {
        // TODO: ak crashne castovanie alebo getChild() tak treba inak premysliet nahravanie
        final View rootLayout = ((ViewGroup) mCurrentActivity.getWindow().getDecorView().getRootView()).getChildAt(0);

        if (rootLayout == null || (rootLayout.getWidth() == 0 && rootLayout.getHeight() == 0)) {
            // nothing to record
            return;
        }

        mScreenBuffer.drawToBuffer(rootLayout);
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
                Log.e("UxMobile", "doInBackground: ", e);
            }
            return null;
        }
    }
}
