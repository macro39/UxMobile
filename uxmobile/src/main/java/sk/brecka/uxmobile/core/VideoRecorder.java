package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
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
//        final String videoPath = Environment.getExternalStorageDirectory().toString() + "/" + filename;
        mVideoPath = mCurrentActivity.getFilesDir().toString() + "/" + filename;

        int screenWidth = 384;
        int screenHeight = 240;

        try {
            mEncoder = new NativeEncoder(screenWidth, screenHeight, 1, 64_000, mVideoPath);
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
    public void onApplicationEnded() {
        super.onApplicationEnded();

        Log.i(TAG, "onApplicationEnded: stopping video recording");

//        try {
//        // TODO: handlovanie ak nenatocil nic
//            mEncoder.finish();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mVideoTask.cancel();
        mVideoTimer.cancel();

        mVideoTask = null;
        mVideoTimer = null;
    }

    private void captureFrame() {
        final Configuration configuration = mCurrentActivity.getResources().getConfiguration();
//        System.out.println("orientation " + configuration.orientation);
//        System.out.println("keyboardHidden " + configuration.keyboardHidden);

        final View rootView = mCurrentActivity.getWindow().getDecorView().getRootView();

        if (rootView.getWidth() == 0 && rootView.getHeight() == 0) {
            // nie je co natacat
            return;
        }

//        System.out.println("im main thread: " + (Looper.myLooper() == Looper.getMainLooper()));

//        try {
        mScreenBuffer.drawToBuffer(rootView);
//            mEncoder.encodeFrame(mScreenBuffer.getBitmap());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public File getOutput() {
        return new File(mVideoPath);
    }

    private class FrameEncodingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                mEncoder.encodeFrame(mScreenBuffer.getBitmap());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
