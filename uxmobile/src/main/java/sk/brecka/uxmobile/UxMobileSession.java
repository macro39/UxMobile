package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;

import sk.brecka.uxmobile.adapter.LifecycleObserver;
import sk.brecka.uxmobile.core.EventRecorder;
import sk.brecka.uxmobile.core.LifecycleCallback;
import sk.brecka.uxmobile.core.VideoRecorder;
import sk.brecka.uxmobile.net.RestClient;
import sk.brecka.uxmobile.util.Config;
import sk.brecka.uxmobile.util.NetworkUtils;

/**
 * Created by matej on 23.10.2017.
 */

public class UxMobileSession implements LifecycleCallback {
    private Context mContext;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeSensor mShakeDetector;

    private LifecycleObserver mLifecycleObserver;

    private VideoRecorder mVideoRecorder;
    private EventRecorder mInputRecorder;
    private RestClient mRestClient;


    public UxMobileSession(Application application, String apiKey) {
        Log.d("UxMobile", "UxMobileSession: New UxMobile Session");

        mContext = application;

        Config.get().setApiKey(apiKey);

        mLifecycleObserver = new LifecycleObserver(this);

        mVideoRecorder = new VideoRecorder();
        mInputRecorder = new EventRecorder();
        mRestClient = new RestClient();

        registerCallbacks(application);
//        registerShakeSensor();
    }

    @Override
    public void onFirstActivityStarted(final Activity activity) {
        mRestClient.startSession(activity, new Runnable() {
            @Override
            public void run() {
                onSessionStarted();
            }
        });
        mVideoRecorder.onFirstActivityStarted(activity);
        mInputRecorder.onFirstActivityStarted(activity);
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
    public void onLastActivityStopped(Activity activity) {
        mVideoRecorder.onLastActivityStopped(activity);
        mInputRecorder.onLastActivityStopped(activity);

        uploadRecordings();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        mVideoRecorder.onConfigurationChanged(configuration);
        mInputRecorder.onConfigurationChanged(configuration);
    }

    public void onSessionStarted() {
        mVideoRecorder.onSessionStarted();
        mInputRecorder.onSessionStarted();
    }

    private void registerCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(mLifecycleObserver);
        application.registerComponentCallbacks(mLifecycleObserver);
    }

    private void registerShakeSensor() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeSensor();
        mShakeDetector.setOnShakeListener(new ShakeSensor.OnShakeListener() {

            @Override
            public void onShake(int count) {
                // TODO
            }
        });
    }

    private void uploadRecordings() {
        Log.d("UxMobile", "uploadRecordings ");
        try {
            // only continue if uploading isn't set to wifi only or the device has an unlimited connection
            if (Config.get().isRecordingWifiOnly() && !NetworkUtils.hasUnlimitedConnection(mContext)) {
                return;
            }

            if (Config.get().isRecordingVideo()) {
                mRestClient.uploadVideo(mVideoRecorder.getOutput());
            }

            if (Config.get().isRecordingEvents()) {
                mRestClient.uploadInput(mInputRecorder.getOutput());
            }

        } catch (JSONException e) {
            Log.e("UxMobile", "uploadRecordings: ", e);
        }

    }
}
