package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.jcodec.common.model.Picture;

import java.util.Timer;
import java.util.TimerTask;

import sk.brecka.uxmobile.NativeEncoder;

/**
 * Created by matej on 25.8.2017.
 */

public class VideoRecorder extends BaseRecorder {

    private static final int VIDEO_FRAMERATE = 1;
    private static final int VIDEO_RESOLUTION = 240;

//    private File mVideoFile;
//    private AndroidSequenceEncoder mEncoder;

    private NativeEncoder mNativeEncoder;

    private Timer mVideoTimer;
    private TimerTask mVideoTask;

    public VideoRecorder() {
    }

    @Override
    protected void onFirstActivityStarted(Activity activity) {
        Log.i(TAG, "onActivityStarted: starting video recording");

        final String filename = String.valueOf(System.currentTimeMillis()) + ".mp4";
//        mVideoFile = new File(mCurrentActivity.getFilesDir(), filename);
        // TODO: nezapisovat to na external storage
        final String videoPath = Environment.getExternalStorageDirectory().toString() + "/" + filename;
//        final String videoPath = mCurrentActivity.getFilesDir().toString() + "/" + filename;
//        mVideoFile = new File(videoPath);chrome

        // TODO: lepsie tento try/catch
//        try {
//            mEncoder = AndroidSequenceEncoder.createSequenceEncoder(mVideoFile, VIDEO_FRAMERATE);

        mNativeEncoder = new NativeEncoder(videoPath);
//        mNativeEncoder.mux();
//            mEncoder = new MySequenceEncoder(mVideoFile, VIDEO_FRAMERATE);
//        } catch (IOException e) {
//            Log.e(TAG, "onActivityStarted: ", e);
//        }

        final Handler handler = new Handler();
        mVideoTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        captureFrame();
                    }
                });
            }
        };

        mVideoTimer = new Timer();
        mVideoTimer.schedule(mVideoTask, 0, 1000 / VIDEO_FRAMERATE);
    }

    @Override
    protected void onLastActivityStopped(Activity activity) {
        Log.i(TAG, "onActivityStopped: stopping video recording");
//        try {
        // TODO: handlovanie ak nenatocil nic
//            mEncoder.finish();

//        if (q > 1) {
        mNativeEncoder.stop();
//        }
//            mNativeEncoder.stop();
//        } catch (IOException e) {
//            Log.e(TAG, "onActivityStopped: ", e);
//        }

        mVideoTask.cancel();
        mVideoTimer.cancel();

        mVideoTask = null;
        mVideoTimer = null;
    }

    Picture mPicture = null;

    Bitmap bitmapBuffer = null;

    private void copyBitmap(final Bitmap src) {
        if (bitmapBuffer == null) {
            bitmapBuffer = downscaleBitmap(src,VIDEO_RESOLUTION);
        } else {
            for (int i = 0; i < bitmapBuffer.getWidth(); i++) {
                for (int j = 0; j < bitmapBuffer.getHeight(); j++) {
                    bitmapBuffer.setPixel(i, j, src.getPixel(i, j));
                }
            }
        }
    }

    private void captureFrame() {
        final View rootView = mCurrentActivity.getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);

        final Bitmap captured = rootView.getDrawingCache();

        if (captured == null) {
            // este nie je co zaznamenat
            return;
        }


//        if (mPicture == null) {
//        final Bitmap downscaled = downscaleBitmap(captured, VIDEO_RESOLUTION);
//        final Bitmap downscaled = captured;
//        copyBitmap(captured);
        mNativeEncoder.recordFrame(captured);
        rootView.setDrawingCacheEnabled(false);
//            mPicture = BitmapUtil.fromBitmap(downscaled);
//        }

        // toto sposobuje slowdown
//        try {
//            mEncoder.encodeImage(downscaled);
//            mEncoder.encodeNativeFrame(mPicture);
//        } catch (IOException | IllegalStateException e) {
//            Log.e(TAG, "captureFrame: ", e);
//        }
    }

    private Bitmap downscaleBitmap(final Bitmap source, int goalResolution) {
        final int smallerDimension = Math.min(source.getWidth(), source.getHeight());
        double downscaleRatio = ((double) goalResolution) / ((double) smallerDimension);

        // neupscalovat ak je nahodou source mensi ako cielove rozlisenie
        downscaleRatio = Math.min(1, downscaleRatio);

        return Bitmap.createScaledBitmap(source, (int) (downscaleRatio * source.getWidth()), (int) (downscaleRatio * source.getHeight()), false);
    }

//    public File getVideoFile() {
//        return mVideoFile;
//    }

    private class CaptureFrameAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            captureFrame();
            return null;
        }
    }
}
