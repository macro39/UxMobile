package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import sk.brecka.uxmobile.adapter.ActivityLifecycleAdapter;
import sk.brecka.uxmobile.core.InputRecorder;
import sk.brecka.uxmobile.core.VideoRecorder;
import sk.brecka.uxmobile.net.RestClient;

/**
 * Created by matej on 4.10.2017.
 */

public class UxMobile {
    private final String TAG = getClass().getName();

    private static UxMobile sInstance = null;

    private Context mContext;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeSensor mShakeDetector;

    private VideoRecorder mVideoRecorder;
    private InputRecorder mInputRecorder;
    private RestClient mRestClient;


    private UxMobile(Context context) {
        mContext = context;

        mVideoRecorder = new VideoRecorder();
        mInputRecorder = new InputRecorder();
        mRestClient = new RestClient();

        mRestClient.startSession(context);
    }

    public static void start(Context context) {
        start((Application) context.getApplicationContext());
    }

    public static void start(Application application) {
        sInstance = new UxMobile(application);
        sInstance.registerCallbacks(application);
        sInstance.registerShakeSensor();
    }

    private void registerCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleAdapter() {
            @Override
            public void onActivityStarted(Activity activity) {
//                mVideoRecorder.onActivityStarted(activity);
                mInputRecorder.onActivityStarted(activity);
                mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            }

            @Override
            public void onActivityStopped(Activity activity) {
//                mVideoRecorder.onActivityStopped(activity);
                mInputRecorder.onActivityStopped(activity);
                mSensorManager.unregisterListener(mShakeDetector);

                // TODO: nech odosiela az na konci uplne vsetkych aktivit
                uploadRecordings();
            }
        });
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
