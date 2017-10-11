package sk.brecka.uxmobile.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
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
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static final String FORM_USER = "user";
    private static final String FORM_SESSION = "session";

    private OkHttpClient mHttpClient = new OkHttpClient();

    private String mUser = "";
    private String mSession = "";

    // TODO: nejake normalne url
    private static final String BASE_URL = "http://10.11.41.56:8765";

    public void uploadVideo(final File file) {

        MultipartBody.Part userPart = MultipartBody.Part.createFormData(FORM_USER, mUser);
        MultipartBody.Part sessionPart = MultipartBody.Part.createFormData(FORM_SESSION, mSession);
        RequestBody fileForm = FormBody.create(MEDIA_TYPE_MP4, file);

        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(userPart)
                .addPart(sessionPart)
                .addFormDataPart("file", file.getName(), fileForm)
                .build();

        final HttpUrl url = HttpUrl.parse(BASE_URL + "/upload/video");

        upload(url, multipartBody);
    }

    public void uploadInput(final JSONObject jsonObject) {
        final RequestBody jsonForm = FormBody.create(MEDIA_TYPE_JSON,jsonObject.toString());
        final HttpUrl url = HttpUrl.parse(BASE_URL + "/upload/input");

        upload(url,jsonForm);
    }

    public void uploadInput(final JSONArray jsonArray) {
        final RequestBody jsonForm = FormBody.create(MEDIA_TYPE_JSON,jsonArray.toString());
        final HttpUrl url = HttpUrl.parse(BASE_URL + "/upload/input");

        upload(url,jsonForm);
    }

    private void upload(final HttpUrl url, final RequestBody requestBody) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Request request = new Request.Builder()
                        .url(url)
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
