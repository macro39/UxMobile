package sk.brecka.uxtune.net;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by matej on 25.8.2017.
 */

public class RestClient {
    private final String TAG = getClass().getName();

    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

    private static final String FORM_USER = "user";
    private static final String FORM_SESSION = "session";

    private OkHttpClient mHttpClient = new OkHttpClient();

    private String mUser = "";
    private String mSession = "";

    public void uploadVideo(final File file) {
        uploadFile(file, MEDIA_TYPE_MP4);
    }

    public void uploadData(final File file) {
        uploadFile(file, MEDIA_TYPE_TEXT);
    }

    private void uploadFile(File file, MediaType mediaType) {
        MultipartBody.Part userPart = MultipartBody.Part.createFormData(FORM_USER, mUser);
        MultipartBody.Part sessionPart = MultipartBody.Part.createFormData(FORM_SESSION, mSession);
        RequestBody fileForm = FormBody.create(mediaType, file);

        RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(userPart)
                .addPart(sessionPart)
                .addFormDataPart("file", file.getName(), fileForm)
                .build();

        upload(multipartBody);
    }

    private void upload(final RequestBody requestBody) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Request request = new Request.Builder()
                        .url("http://192.168.0.31:8765/upload")
                        .post(requestBody)
                        .build();

                Response response = null;
                try {
                    response = mHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: ", e);
                }
                return null;
            }
        }.execute();
    }

    public void setUser(String user) {
        mUser = user;
    }

    public void setSession(String session) {
        mSession = session;
    }
}
