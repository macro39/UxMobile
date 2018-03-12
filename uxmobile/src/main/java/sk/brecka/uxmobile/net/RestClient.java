package sk.brecka.uxmobile.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
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
import sk.brecka.uxmobile.model.study.Task;
import sk.brecka.uxmobile.util.Config;
import sk.brecka.uxmobile.util.LongLog;

/**
 * Created by matej on 25.8.2017.
 */

public class RestClient {
    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static final String SERVICE_SESSION_START = "config";
    private static final String SERVICE_VIDEO_UPLOAD = "video";
    private static final String SERVICE_INPUT_UPLOAD = "input";
    private static final String SERVICE_REQUEST_TEST = "study";

    private static final String FORM_SESSION = "session";
    private static final String FORM_API_KEY = "api_key";

    private static final String RESPONSE_SESSION = "session";
    private static final String RESPONSE_RECORD_VIDEO = "video_record";
    private static final String RESPONSE_RECORD_EVENT = "event_record";
    private static final String RESPONSE_RECORD_WIFI_ONLY = "wifi_only";
    private static final String RESPONSE_VIDEO_FPS = "video_fps";
    private static final String RESPONSE_VIDEO_BITRATE = "video_bitrate";
    private static final String RESPONSE_VIDEO_HEIGHT = "video_height";
    private static final String RESPONSE_VIDEO_WIDTH = "video_width";
    private static final String RESPONSE_REQUEST_TEST = "request_test";
    private static final String RESPONSE_WELCOME_DIALOG = "dialog_welcome";
    private static final String RESPONSE_INSTRUCTION_DIALOG = "dialog_instruction";
    private static final String RESPONSE_TASK_DIALOG = "dialog_task";
    private static final String RESPONSE_TASK_COMPLETION_DIALOG = "dialog_task_completion";
    private static final String RESPONSE_THANK_YOU_DIALOG = "dialog_thank_you";
    private static final String RESPONSE_TASK = "task";

    private static final String HOST_BASE = "mobux.team";

    //    private static final String HOST_WEBAPP = "mobux_dev";
    private static final String HOST_WEBAPP = "sfs";

    private static final int HOST_PORT = 443;
    private static final String HOST_API = "api";

    private OkHttpClient mHttpClient = new OkHttpClient();

    public void startSession(Context context, final Runnable callback) {

        final FormBody.Builder builder = new FormBody.Builder();

        // api key
        builder.add(FORM_API_KEY, Config.get().getApiKey());

        // device information
        for (Map.Entry<String, String> entry : Config.getDeviceConfig(context).entrySet()) {
            Log.d("UxMobile", "startSession: " + entry.getKey() + " " + entry.getValue());
            builder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(buildUrl(SERVICE_SESSION_START))
                .post(builder.build())
                .build();

        Log.d("UxMobile", "startSession: " + request.url().toString());

        // async execute
        new HttpExecutor() {
            @Override
            protected void onPostExecute(String response) {
                try {
                    Log.d("UxMobile", "onPostExecute: " + response);
                    if (response == null) {
                        // exception?
                        return;
                    }

                    //
                    final JSONObject jsonResponse = new JSONObject(response);

                    //
                    final String session = jsonResponse.getString(RESPONSE_SESSION);
                    final boolean recordVideo = jsonResponse.getBoolean(RESPONSE_RECORD_VIDEO);
                    final boolean recordEvent = jsonResponse.getBoolean(RESPONSE_RECORD_EVENT);
                    final boolean recordWifiOnly = jsonResponse.getBoolean(RESPONSE_RECORD_WIFI_ONLY);
                    final int videoFps = jsonResponse.getInt(RESPONSE_VIDEO_FPS);
                    final int videoBitrate = jsonResponse.getInt(RESPONSE_VIDEO_BITRATE);
                    final int videoHeight = jsonResponse.getInt(RESPONSE_VIDEO_HEIGHT);
                    final int videoWidth = jsonResponse.getInt(RESPONSE_VIDEO_WIDTH);
                    final boolean requestTest = jsonResponse.getBoolean(RESPONSE_REQUEST_TEST);

                    //
                    Config.get().setSession(session);
                    Config.get().setRecordingVideo(recordVideo);
                    Config.get().setRecordingEvents(recordEvent);
                    Config.get().setRecordingWifiOnly(recordWifiOnly);
                    Config.get().setVideoFps(videoFps);
                    Config.get().setVideoBitrate(videoBitrate);
                    Config.get().setVideoHeight(videoHeight);
                    Config.get().setVideoWidth(videoWidth);
                    Config.get().setRequestingTest(requestTest);

                    callback.run();
                } catch (JSONException e) {
                    // malformed result
                    Log.e("UxMobile", "doInBackground: ", e);
                }
            }
        }.execute(request);

    }

    public void requestTest(final Runnable callback) {

        final FormBody.Builder builder = new FormBody.Builder();

        // session
        builder.add(FORM_SESSION, Config.get().getSession());

        //
        Request request = new Request.Builder()
                .url(buildUrl(SERVICE_REQUEST_TEST))
                .post(builder.build())
                .build();

        Log.d("UxMobile", "requestTest: " + request.url().toString());

        // async execute
        new HttpExecutor() {
            @Override
            protected void onPostExecute(String response) {
                try {
                    Log.d("UxMobile", "onPostExecute: " + response);
                    if (response == null) {
                        // exception?
                        return;
                    }

                    //
                    final JSONObject jsonResponse = new JSONObject(response);

                    //
                    final JSONObject welcomeDialog = jsonResponse.getJSONObject(RESPONSE_WELCOME_DIALOG);
                    final JSONObject instructionDialog = jsonResponse.getJSONObject(RESPONSE_INSTRUCTION_DIALOG);
                    final JSONObject taskDialog = jsonResponse.getJSONObject(RESPONSE_TASK_DIALOG);
                    final JSONObject taskCompletionDialog = jsonResponse.getJSONObject(RESPONSE_TASK_COMPLETION_DIALOG);
                    final JSONObject thankYouDialog = jsonResponse.getJSONObject(RESPONSE_THANK_YOU_DIALOG);

                    //
                    final Task task = Task.fromJson(jsonResponse.getJSONObject(RESPONSE_TASK));

                    //
                    Config.get().setWelcomeDialogJson(welcomeDialog);
                    Config.get().setInstructionDialogJson(instructionDialog);
                    Config.get().setTaskDialogJson(taskDialog);
                    Config.get().setTaskCompletionDialogJson(taskCompletionDialog);
                    Config.get().setThankYouDialogJson(thankYouDialog);

                    Config.get().setCurrentTask(task);

                    callback.run();
                } catch (JSONException e) {
                    // malformed result
                    Log.e("UxMobile", "doInBackground: ", e);
                }
            }
        }.execute(request);

    }

    public void uploadVideo(final File file) {

        if (file == null) {
            Log.d("UxMobile", "uploadVideo: file is null, not uploading");
            return;
        }

        if (!file.exists()) {
            Log.d("UxMobile", "uploadVideo: file doesnt exist, not uploading");
            return;
        }

        RequestBody fileForm = FormBody.create(MEDIA_TYPE_MP4, file);

        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(FORM_SESSION, Config.get().getSession())
                .addFormDataPart("file", file.getName(), fileForm)
                .build();

        Log.d("UxMobile", "uploadVideo: " + file);

        final HttpUrl url = buildUrl(SERVICE_VIDEO_UPLOAD);

        uploadAsync(url, multipartBody);
    }

    public void uploadEvents(final JSONArray jsonArray) {

        if (jsonArray == null) {
            Log.d("UxMobile", "uploadVideo: json is null, not uploading");
            return;
        }

        // TODO: musi to byt multipart?
        final RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(FORM_SESSION, Config.get().getSession())
                .addFormDataPart("event", jsonArray.toString())
                .build();

        LongLog.d("UxMobile", "uploadEvents: " + jsonArray);

        final HttpUrl url = buildUrl(SERVICE_INPUT_UPLOAD);

        uploadAsync(url, multipartBody);
    }


    private void upload(final HttpUrl url, final RequestBody requestBody) {

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response;
        try {
            response = mHttpClient.newCall(request).execute();

//            LongLog.d("UxMobile", "doInBackground: " + response.body().string());
        } catch (IOException e) {
            Log.e("UxMobile", "doInBackground: ", e);
        }
    }

    private void uploadAsync(final HttpUrl url, final RequestBody requestBody) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                upload(url, requestBody);

                return null;
            }
        }.execute();
    }

    private HttpUrl buildUrl(String service) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(HOST_BASE)
                .port(HOST_PORT)
                .addPathSegment(HOST_WEBAPP)
                .addPathSegment(HOST_API)
                .addPathSegment(service)
                .build();
    }

    private MultipartBody.Part buildSessionPart() {
        return MultipartBody.Part.create(
                new FormBody.Builder()
                        .add(FORM_SESSION, Config.get().getSession())
                        .build());
    }

    private class HttpExecutor extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... params) {
            try {
                if (params.length != 1) {
                    return null;
                }

                final Response response = mHttpClient.newCall(params[0]).execute();

                if (response.code() != HttpURLConnection.HTTP_OK) {
                    Log.d("UxMobile", "doInBackground: " + response.code());
//                    LongLog.d("UxMobile", "doInBackground: " + response.body().string());
                    return null;
                }

                final ResponseBody responseBody = response.body();

                if (responseBody != null) {
                    return responseBody.string();
                }
            } catch (IOException e) {
                Log.e("UxMobile", "doInBackground: ", e);
            }

            return null;
        }
    }
}
