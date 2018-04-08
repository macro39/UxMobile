package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;

import java.util.Map;

import sk.brecka.uxmobile.adapter.LifecycleObserver;
import sk.brecka.uxmobile.core.EventRecorder;
import sk.brecka.uxmobile.core.LifecycleCallback;
import sk.brecka.uxmobile.core.VideoRecorder;
import sk.brecka.uxmobile.model.event.TaskEvent;
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
        Config.get().setRecordingsUploaded(false);

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
            DialogBuilder.buildWelcomeDialog(mActivity, this).show();
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
        mShakeDetector = new ShakeSensor();
        mShakeDetector.setOnShakeListener(new ShakeSensor.OnShakeListener() {

            @Override
            public void onShake(int count) {
                Log.d("UxMobile", "onShake: " + count);
                try {
                    DialogBuilder.buildTaskCompletionDialog(mActivity, UxMobileSession.this).show();
                } catch (JSONException e) {
                    Log.e("UxMobile", "onShake: ", e);
                }

                // TODO
            }
        });

        mSensorManager.registerListener(mShakeDetector, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterShakeSensor() {
        mSensorManager.unregisterListener(mShakeDetector);
    }

    void uploadRecordings() {
        Log.d("UxMobile", "uploadRecordings ");
        try {
            final Config config = Config.get();

            if (config.hasUploaded()) {
                return;
            }

            // only continue if uploading isn't set to wifi only or the device has an unlimited connection
            if (config.isUploadingWifiOnly() && !NetworkUtils.hasUnlimitedConnection(mContext)) {
                return;
            }

            config.setRecordingsUploaded(true);

            if (config.isUploadingOverriden() || config.isUploadingVideo()) {
                mRestClient.uploadVideo(mVideoRecorder.getOutput());
            }

            if (config.isUploadingEvents()) {
                mRestClient.uploadEvents(mEventRecorder.getOutput());
            }

        } catch (JSONException e) {
            Log.e("UxMobile", "uploadRecordings: ", e);
        }
    }

    public void addCustomEvent(String eventName) {
        mEventRecorder.addCustomEvent(eventName);
    }

    public void addCustomEvent(String eventName, Map<String, String> payload) {
        mEventRecorder.addCustomEvent(eventName, payload);
    }

    public void addExceptionEvent(Throwable throwable) {
        mEventRecorder.addExceptionEvent(throwable);
    }

    private void addTaskEvent(TaskEvent.Status status) {
        mEventRecorder.addTaskEvent(Config.get().getCurrentTask(), status);
    }

    public void startTest() {
        registerShakeSensor();
        addTaskEvent(TaskEvent.Status.STARTED);
        Config.get().setTaskRunning(true);
    }

    public void completeTest() {
        addTaskEvent(TaskEvent.Status.COMPLETED);
        Config.get().setTaskRunning(false);
    }

    public void skipTest() {
        addTaskEvent(TaskEvent.Status.SKIPPED);
        Config.get().setTaskRunning(false);
    }

    public void cancelTest() {
        addTaskEvent(TaskEvent.Status.CANCELLED);
        Config.get().setTaskRunning(false);
    }
}
