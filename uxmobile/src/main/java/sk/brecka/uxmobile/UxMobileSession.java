package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
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
        System.out.println("New UxMobileSession");
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
        System.out.println("########### onEveryActivityStarted " + activity.getLocalClassName());
//        mVideoRecorder.onEveryActivityStarted(activity);
//        mInputRecorder.onEveryActivityStarted(activity);
    }

    @Override
    public void onEveryActivityStopped(Activity activity) {
        System.out.println("########### onEveryActivityStopped " + activity.getLocalClassName());
//        mVideoRecorder.onEveryActivityStopped(activity);
//        mInputRecorder.onEveryActivityStopped(activity);
    }

    @Override
    public void onFirstActivityStarted(Activity activity) {
        System.out.println("########### onFirstActivityStarted " + activity.getLocalClassName());
//        mVideoRecorder.onFirstActivityStarted(activity);
//        mInputRecorder.onFirstActivityStarted(activity);
    }

    @Override
    public void onApplicationEnded() {
        System.out.println("########### onApplicationEnded");
//        mVideoRecorder.onApplicationEnded();
//        mInputRecorder.onApplicationEnded();
//
//        uploadRecordings();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        System.out.println("########### onConfigurationChanged");

//        mInputRecorder.onConfigurationChanged(configuration);
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
        System.out.println("Uploading recordings");
        try {
            mRestClient.uploadVideo(mVideoRecorder.getOutput());
            mRestClient.uploadInput(mInputRecorder.getOutput());
        } catch (JSONException e) {
            Log.e(TAG, "uploadRecordings: ", e);
        }

    }
}
