package sk.brecka.uxmobile.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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
import okhttp3.ResponseBody;
import okio.BufferedSink;
import sk.brecka.uxmobile.util.Config;

/**
 * Created by matej on 25.8.2017.
 */

public class RestClient {
    private static final String TAG = "RestClient";

    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static final String SERVICE_SESSION_START = "config";
    private static final String SERVICE_VIDEO_UPLOAD = "video";
    private static final String SERVICE_INPUT_UPLOAD = "input";

    private static final String FORM_SESSION = "session";

    private static final String RESPONSE_SESSION = "session";
    private static final String RESPONSE_RECORD = "record";

//    private static final String HOST_BASE = "team11-17.studenti.fiit.stuba.sk";
    private static final String HOST_BASE = "10.11.41.56";
    private static final int HOST_PORT = 8765;
    private static final String HOST_API = "api";

    private OkHttpClient mHttpClient = new OkHttpClient();

    // TODO: presunut do Config
    private String mSession = "";

    public void startSession(Context context) {

        final FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : Config.get(context).entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(buildUrl(SERVICE_SESSION_START))
                .post(builder.build())
                .build();

        // async execute
        new HttpExecutor() {
            @Override
            protected void onPostExecute(String response) {
                try {
                    if (response == null) {
                        // exception?
                        return;
                    }

                    final JSONObject jsonResponse = new JSONObject(response);

                    // TODO: passovat response do Config
                    mSession = jsonResponse.getString(RESPONSE_SESSION);
                    Log.d(TAG, "onPostExecute: session " + mSession);
                } catch (JSONException e) {
                    // malformed result
                    e.printStackTrace();
                }
            }
        }.execute(request);

    }

    public void uploadVideo(final File file) {

        RequestBody fileForm = FormBody.create(MEDIA_TYPE_MP4, file);

        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(buildSessionPart())
                .addFormDataPart("file", file.getName(), fileForm)
                .build();

        final HttpUrl url = buildUrl(SERVICE_VIDEO_UPLOAD);

        upload(url, multipartBody);
    }

    public void uploadInput(final JSONArray jsonArray) {
        // TODO: musi to byt multipart?
        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(FORM_SESSION, mSession)
                .addFormDataPart("event", jsonArray.toString())
                .build();

        Log.d(TAG, "uploadInput: " + jsonArray.toString());

        final HttpUrl url = buildUrl(SERVICE_INPUT_UPLOAD);

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
//                    System.out.println("response start ----");
//                    System.out.println(response.body().string());
//                    System.out.println("response end ----");
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: ", e);
                }
                return null;
            }
        }.execute();
    }

    private HttpUrl buildUrl(String service) {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(HOST_BASE)
                .port(HOST_PORT)
                .addPathSegment(HOST_API)
                .addPathSegment(service)
                .build();
    }

    private MultipartBody.Part buildSessionPart() {
        return MultipartBody.Part.create(
                new FormBody.Builder()
                        .add(FORM_SESSION, mSession)
                        .build());
    }

    private class HttpExecutor extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... params) {
            try {
                if (params.length < 1) {
                    return null;
                }

                final Response response = mHttpClient.newCall(params[0]).execute();

                // TODO: nejaka logika na response code atd

                final ResponseBody responseBody = response.body();

                if (responseBody != null) {
                    return responseBody.string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
