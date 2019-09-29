package sk.uxtweak.uxmobile.core;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import sk.uxtweak.uxmobile.NativeEncoder;
import sk.uxtweak.uxmobile.ScreenBuffer;
import sk.uxtweak.uxmobile.util.Config;

/**
 * Created by matej on 25.8.2017.
 */

public class VideoRecorder extends BaseRecorder {
    private static final String FILENAME = "uxmobile_screen_recording.mp4";

    private NativeEncoder mEncoder;
    private ScreenBuffer mScreenBuffer;

    private Timer mVideoTimer;
    private TimerTask mVideoTask;

    private boolean mIsRecording = false;

    private File mOutputFile;

    public VideoRecorder() {
    }

    @Override
    public void onSessionStarted() {

        mIsRecording = Config.get().isRecordingVideo();

        if (!mIsRecording) {
            return;
        }

        Log.i("UxMobile", "onSessionStarted: starting video recording");

        try {
            final String videoPath = getCurrentActivity().getFilesDir().toString() + File.separator + FILENAME;
            mOutputFile = new File(videoPath);

            // overwrite has issues, delete to be sure
            if (mOutputFile.exists()) {
                mOutputFile.delete();
            }
            final int screenWidth = Config.get().getVideoWidth();
            final int screenHeight = Config.get().getVideoHeight();
            final int bitrate = Config.get().getVideoBitrate();
            final int framerate = Config.get().getVideoFps();

            mEncoder = new NativeEncoder(screenWidth, screenHeight, framerate, bitrate, videoPath);
            mScreenBuffer = new ScreenBuffer(screenWidth, screenHeight);

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

        } catch (IOException e) {
            Log.e("UxMobile", "doInBackground: ", e);
        }
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
//        final View rootLayout = ((ViewGroup) mCurrentActivity.getWindow().getDecorView().getRootView()).getChildAt(0); // neobsahuje listy, ma problem s vrstvami
        final View rootLayout = getCurrentActivity().getWindow().getDecorView(); // obsahuje hornu aj spodnu listu, nema problem s vrstvami

        if (rootLayout == null || (rootLayout.getWidth() == 0 && rootLayout.getHeight() == 0)) {
            // nothing to record
            return;
        }

        mScreenBuffer.drawToBuffer(rootLayout);
    }

    public File getOutput() {
        return mOutputFile;
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
