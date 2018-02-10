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
    private Activity mActivity;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeSensor mShakeDetector;

    private LifecycleObserver mLifecycleObserver;

    private VideoRecorder mVideoRecorder;
    private EventRecorder mEventRecorder;
    private RestClient mRestClient;


    public UxMobileSession(Application application, String apiKey) {
        Log.d("UxMobile", "UxMobileSession: New UxMobile Session");

        mContext = application;

        Config.get().setApiKey(apiKey);

        mLifecycleObserver = new LifecycleObserver(this);

        mVideoRecorder = new VideoRecorder();
        mEventRecorder = new EventRecorder();
        mRestClient = new RestClient();

        registerCallbacks(application);
//        registerShakeSensor();

        MyExceptionHandler.register();
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
        mEventRecorder.onFirstActivityStarted(activity);
    }

    @Override
    public void onEveryActivityStarted(Activity activity) {
        mActivity = activity;
        mVideoRecorder.onEveryActivityStarted(activity);
        mEventRecorder.onEveryActivityStarted(activity);
    }

    @Override
    public void onEveryActivityStopped(Activity activity) {
        mVideoRecorder.onEveryActivityStopped(activity);
        mEventRecorder.onEveryActivityStopped(activity);
    }

    @Override
    public void onLastActivityStopped(Activity activity) {
        mVideoRecorder.onLastActivityStopped(activity);
        mEventRecorder.onLastActivityStopped(activity);

        uploadRecordings();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        mVideoRecorder.onConfigurationChanged(configuration);
        mEventRecorder.onConfigurationChanged(configuration);
    }

    private void onSessionStarted() {
        mVideoRecorder.onSessionStarted();
        mEventRecorder.onSessionStarted();

        //
        if (Config.get().isRequestingTest()) {
            mRestClient.requestTest(new Runnable() {
                @Override
                public void run() {
                    onTestRequested();
                }
            });
        }
    }

    private void onTestRequested() {
        try {
            DialogBuilder.buildWelcomeDialog(mActivity).show();
        } catch (JSONException e) {
            Log.e("UxMobile", "onTestRequested: ", e);
        }
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

    void uploadRecordings() {
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
                mRestClient.uploadEvents(mEventRecorder.getOutput());
            }

        } catch (JSONException e) {
            Log.e("UxMobile", "uploadRecordings: ", e);
        }
    }

    public void addCustomEvent(String eventName) {
        mEventRecorder.addEvent(eventName);
    }

    public void addExceptionEvent(Throwable throwable) {
        mEventRecorder.addExceptionEvent(throwable);
    }
}
