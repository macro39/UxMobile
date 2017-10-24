package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;

import sk.brecka.uxmobile.adapter.LifecycleObserver;
import sk.brecka.uxmobile.core.InputRecorder;
import sk.brecka.uxmobile.core.LifecycleCallback;
import sk.brecka.uxmobile.core.VideoRecorder;
import sk.brecka.uxmobile.net.RestClient;

/**
 * Created by matej on 23.10.2017.
 */

public class UxMobileSession implements LifecycleCallback {

    private final String TAG = getClass().getName();

    private Context mContext;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeSensor mShakeDetector;

    private LifecycleObserver mLifecycleObserver;

    private VideoRecorder mVideoRecorder;
    private InputRecorder mInputRecorder;
    private RestClient mRestClient;


    public UxMobileSession(Application application) {
        mContext = application;

        mLifecycleObserver = new LifecycleObserver(this);

        mVideoRecorder = new VideoRecorder();
        mInputRecorder = new InputRecorder();
        mRestClient = new RestClient();

//        mRestClient.startSession(application);

        registerCallbacks(application);
        registerShakeSensor();
    }

    @Override
    public void onEveryActivityStarted(Activity activity) {
        mVideoRecorder.onEveryActivityStarted(activity);
        mInputRecorder.onEveryActivityStarted(activity);
    }

    @Override
    public void onEveryActivityStopped(Activity activity) {
        mVideoRecorder.onEveryActivityStopped(activity);
        mInputRecorder.onEveryActivityStopped(activity);
    }

    @Override
    public void onFirstActivityStarted(Activity activity) {
        mVideoRecorder.onFirstActivityStarted(activity);
        mInputRecorder.onFirstActivityStarted(activity);
//        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onApplicationEnded() {
        mVideoRecorder.onApplicationEnded();
        mInputRecorder.onApplicationEnded();
//        mSensorManager.unregisterListener(mShakeDetector);

        uploadRecordings();
    }

    private void registerCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(mLifecycleObserver);
        application.registerComponentCallbacks(mLifecycleObserver);
    }

    private void registerShakeSensor() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeSensor();
        mShakeDetector.setOnShakeListener(new ShakeSensor.OnShakeListener() {

            @Override
            public void onShake(int count) {
                // TODO
            }
        });
    }

    private void uploadRecordings() {
        try {
//            mRestClient.uploadVideo(mVideoRecorder.getVideoFile());
            mRestClient.uploadInput(mInputRecorder.getOutput());
        } catch (JSONException e) {
            Log.e(TAG, "uploadRecordings: ", e);
        }

    }
}
