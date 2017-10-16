package sk.brecka.uxmobile.net;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by matej on 25.8.2017.
 */

public class RestClient {
    private final String TAG = getClass().getName();

    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static final String SERVICE_SESSION_START = "config";
    private static final String SERVICE_VIDEO_UPLOAD = "video";
    private static final String SERVICE_INPUT_UPLOAD = "input";

    private static final String FORM_USER = "user";
    private static final String FORM_SESSION = "session";

    private OkHttpClient mHttpClient = new OkHttpClient();

    private String mUser = "";
    private String mSession = "";

    // TODO: nejake normalne url
//    private static final String BASE_URL = "http://10.11.41.56:8765";
    private static final String BASE_URL = "http://147.175.145.52:8765";

    public void uploadConfig(final Map<String, String> config) {
        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : config.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        final HttpUrl url = HttpUrl.parse(BASE_URL + "/upload/config");

        upload(url, builder.build());
    }

    public void uploadVideo(final File file) {

        RequestBody fileForm = FormBody.create(MEDIA_TYPE_MP4, file);

        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(buildSessionPart())
                .addFormDataPart("file", file.getName(), fileForm)
                .build();

        final HttpUrl url = HttpUrl.parse(BASE_URL + "/upload/video");

        upload(url, multipartBody);
    }

    public void uploadInput(final JSONArray jsonArray) {
//        final RequestBody jsonForm = FormBody.create(MEDIA_TYPE_JSON, jsonArray.toString());


        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(buildSessionPart())
                .addFormDataPart("input",jsonArray.toString())
                .build();

        final HttpUrl url = HttpUrl.parse(BASE_URL + "/upload/input");

        upload(url, multipartBody);
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

    private HttpUrl buildUrl(){
        // TODO + pridat argument na service
        return null;
    }

    private MultipartBody.Part buildSessionPart() {
        return MultipartBody.Part.create(
                new FormBody.Builder()
                        .add(FORM_USER, mUser)
                        .add(FORM_SESSION, mSession)
                        .build());
    }


    public void setUser(String user) {
        mUser = user;
    }

    public void setSession(String session) {
        mSession = session;
    }
}
