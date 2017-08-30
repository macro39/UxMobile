package sk.brecka.uxtune;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.PrintWriterPrinter;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import sk.brecka.uxtune.adapter.ActivityLifecycleAdapter;
import sk.brecka.uxtune.core.InputRecorder;
import sk.brecka.uxtune.core.VideoRecorder;
import sk.brecka.uxtune.net.RestClient;

/**
 * Created by matej on 25.8.2017.
 */

public class Uxtune {
    private final String TAG = getClass().getName();

    private static Uxtune sInstance = null;

    private Context mContext;

    private VideoRecorder mVideoRecorder;
    private InputRecorder mInputRecorder;
    private RestClient mRestClient;

    private Uxtune(Context context) {
        mContext = context;

        mVideoRecorder = new VideoRecorder();
        mInputRecorder = new InputRecorder();
        mRestClient = new RestClient();

        // TODO: toto nasetovat z responsu od servra
        mRestClient.setUser("test_user");
        mRestClient.setSession("se_" + System.currentTimeMillis());
    }

    public static void start(Context context) {
        start((Application) context.getApplicationContext());
    }

    public static void start(Application application) {
        sInstance = new Uxtune(application);
        sInstance.registerCallbacks(application);
    }

    private void registerCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleAdapter() {
            @Override
            public void onActivityStarted(Activity activity) {
                mVideoRecorder.onActivityStarted(activity);
                mInputRecorder.onActivityStarted(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mVideoRecorder.onActivityStopped(activity);
                mInputRecorder.onActivityStopped(activity);

                uploadRecordings();
            }
        });
    }

    private void uploadRecordings() {
        // video
        mRestClient.uploadVideo(mVideoRecorder.getVideoFile());

        // data
        try {
            final File data = new File(mContext.getExternalFilesDir(null), "data.txt");
            PrintWriter printWriter = new PrintWriter(data);
            printWriter.print(mInputRecorder.getOutput().toString());
            // TODO: posielanie aj ostatnych veci, nie len inputy

            printWriter.close();

            mRestClient.uploadData(data);
        } catch (FileNotFoundException | JSONException e) {
            Log.e(TAG, "uploadRecordings: ", e);
        }

    }
}
