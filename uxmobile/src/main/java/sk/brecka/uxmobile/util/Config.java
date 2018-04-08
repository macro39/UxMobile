package sk.brecka.uxmobile.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import sk.brecka.uxmobile.model.study.Task;

/**
 * Created by matej on 16.10.2017.
 */

public class Config {
    // device information tags
    private static final String TAG_DEV_APP_VERSION = "app_version";
    private static final String TAG_DEV_APP_VERSION_CODE = "app_version_code";
    private static final String TAG_DEV_PACKAGE_NAME = "package_name";
    private static final String TAG_DEV_DEVICE_CODE = "device_code";
    private static final String TAG_DEV_OS_VERSION = "os_version";
    private static final String TAG_DEV_SDK_VERSION = "sdk_version";
    private static final String TAG_DEV_LOCALE = "locale";
    private static final String TAG_DEV_CONNECTIVITY = "connectivity";
    private static final String TAG_DEV_SCREEN_WIDTH = "screen_width";
    private static final String TAG_DEV_SCREEN_HEIGHT = "screen_height";
    private static final String TAG_DEV_SCREEN_DPI = "screen_dpi";
    private static final String TAG_DEV_CLIENT_TIME = "client_time";
    private static final String TAG_DEV_DEVICE = "device";
    private static final String TAG_DEV_MANUFACTURER = "brand";
    private static final String TAG_DEV_MODEL = "model";
    private static final String TAG_DEV_PHYSICAL_MENU_BUTTON = "physical_menu_button";
    private static final String TAG_DEV_FONT_SCALE = "font_scale";
    private static final String TAG_DEV_DEVICE_UNIQUE_ID = "device_id";
    private static final String TAG_DEV_SCREEN_LARGE = "is_large";
    private static final String TAG_DEV_SCREEN_ORIENTATION = "orientation";
    private static final String TAG_DEV_TOTAL_MEMORY = "total_memory";

    // session information
    private String mApiKey;
    private String mSession;

    private boolean mIsRecordingVideo = false;
    private boolean mIsRecordingEvents = false;
    private boolean mIsRecordingWifiOnly = true;

    private int mVideoFps;
    private int mVideoBitrate;
    private int mVideoHeight;
    private int mVideoWidth;

    private Task mCurrentTask = null;
    private boolean mRequestingTest = false;
    private boolean mTestOptIn = false;
    private boolean mIsTaskRunning = false;

    private JSONObject mInstructionDialogJson;
    private JSONObject mTaskDialogJson;
    private JSONObject mTaskCompletionDialogJson;
    private JSONObject mThankYouDialogJson;

    private boolean mHasUploaded = false;

    private static final Config sSession = new Config();

    // potencialne v buducnosti
//    public static final String TAG_DETECT_CRASHES = "detect_crash";
//    public static final String TAG_HIDE_INPUT = "hide_input";
//    public static final String TAG_HIDE_SENSITIVE = "hide_sensitive";
//    public static final String TAG_HIDE_WEB = "hide_web";
//    public static final String TAG_UPLOAD_APP_ICON = "upload_icon";
//    public static final String TAG_UPLOAD_VIDEO_ON_CRASH = "upload_video_on_crash";
    // dialogs
    private JSONObject mWelcomeDialogJson;

    private Config() {
    }

    public static Config get() {
        return sSession;
    }

    public static Map<String, String> getDeviceConfig(final Context context) {
        // ensures data is up to date when method is called
        final Map<String, String> deviceConfig = new LinkedHashMap<>();

        try {
            final String packageName = context.getPackageName();
            final PackageManager pm = context.getPackageManager();
            final PackageInfo pInfo = pm.getPackageInfo(packageName, 0);

            deviceConfig.put(TAG_DEV_APP_VERSION, pInfo.versionName);
            deviceConfig.put(TAG_DEV_APP_VERSION_CODE, String.valueOf(pInfo.versionCode));
            deviceConfig.put(TAG_DEV_PACKAGE_NAME, pInfo.packageName);

        } catch (PackageManager.NameNotFoundException ignored) {
            ignored.printStackTrace();
            deviceConfig.put(TAG_DEV_APP_VERSION, "");
            deviceConfig.put(TAG_DEV_APP_VERSION_CODE, "");
            deviceConfig.put(TAG_DEV_PACKAGE_NAME, "");
        }

        final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        final Resources resources = context.getResources();
        final Configuration configuration = resources.getConfiguration();

        deviceConfig.put(TAG_DEV_OS_VERSION, Build.VERSION.RELEASE);
        deviceConfig.put(TAG_DEV_SDK_VERSION, String.valueOf(Build.VERSION.SDK_INT));

        deviceConfig.put(TAG_DEV_DEVICE, Build.DEVICE);
        deviceConfig.put(TAG_DEV_MANUFACTURER, Build.MANUFACTURER);
        deviceConfig.put(TAG_DEV_MODEL, Build.MODEL);

        deviceConfig.put(TAG_DEV_DEVICE_UNIQUE_ID, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        deviceConfig.put(TAG_DEV_LOCALE, Locale.getDefault().getLanguage());

        deviceConfig.put(TAG_DEV_SCREEN_WIDTH, String.valueOf(displayMetrics.widthPixels));
        deviceConfig.put(TAG_DEV_SCREEN_HEIGHT, String.valueOf(displayMetrics.heightPixels));
        deviceConfig.put(TAG_DEV_SCREEN_DPI, String.valueOf(configuration.densityDpi));
        deviceConfig.put(TAG_DEV_SCREEN_ORIENTATION, String.valueOf(configuration.orientation));
        deviceConfig.put(TAG_DEV_SCREEN_LARGE, String.valueOf((configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE));

        deviceConfig.put(TAG_DEV_CONNECTIVITY, NetworkUtils.getConnectivityString(context));
        deviceConfig.put(TAG_DEV_PHYSICAL_MENU_BUTTON, String.valueOf(ViewConfiguration.get(context).hasPermanentMenuKey()));
        deviceConfig.put(TAG_DEV_FONT_SCALE, String.valueOf(resources.getConfiguration().fontScale));
        deviceConfig.put(TAG_DEV_CLIENT_TIME, new Date().toString());

        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memoryInfo);
        deviceConfig.put(TAG_DEV_TOTAL_MEMORY, String.valueOf(memoryInfo.totalMem));

        return deviceConfig;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
    }

    public String getSession() {
        return mSession;
    }

    public void setSession(String session) {
        mSession = session;
    }

    public boolean isRecordingVideo() {
        return mIsRecordingVideo;
    }

    public void setRecordingVideo(boolean recordingVideo) {
        mIsRecordingVideo = recordingVideo;
    }

    public boolean isRecordingEvents() {
        return mIsRecordingEvents;
    }

    public void setRecordingEvents(boolean recordingEvents) {
        mIsRecordingEvents = recordingEvents;
    }

    public boolean isRecordingWifiOnly() {
        return mIsRecordingWifiOnly;
    }

    public void setRecordingWifiOnly(boolean recordingWifiOnly) {
        mIsRecordingWifiOnly = recordingWifiOnly;
    }

    public int getVideoFps() {
        return mVideoFps;
    }

    public void setVideoFps(int videoFps) {
        mVideoFps = videoFps;
    }

    public int getVideoBitrate() {
        return mVideoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        mVideoBitrate = videoBitrate;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        mVideoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        mVideoWidth = videoWidth;
    }

    public boolean isRequestingTest() {
        return mRequestingTest;
    }

    public void setRequestingTest(boolean requestingTest) {
        mRequestingTest = requestingTest;
    }

    public boolean isTestOptIn() {
        return mTestOptIn;
    }

    public void setTestOptIn(boolean testOptIn) {
        mTestOptIn = testOptIn;
    }

    public JSONObject getWelcomeDialogJson() {
        return mWelcomeDialogJson;
    }

    public void setWelcomeDialogJson(JSONObject welcomeDialogJson) {
        mWelcomeDialogJson = welcomeDialogJson;
    }

    public JSONObject getInstructionDialogJson() {
        return mInstructionDialogJson;
    }

    public void setInstructionDialogJson(JSONObject instructionDialogJson) {
        mInstructionDialogJson = instructionDialogJson;
    }

    public JSONObject getTaskDialogJson() {
        return mTaskDialogJson;
    }

    public void setTaskDialogJson(JSONObject taskDialogJson) {
        mTaskDialogJson = taskDialogJson;
    }

    public JSONObject getTaskCompletionDialogJson() {
        return mTaskCompletionDialogJson;
    }

    public void setTaskCompletionDialogJson(JSONObject taskCompletionDialogJson) {
        mTaskCompletionDialogJson = taskCompletionDialogJson;
    }

    public JSONObject getThankYouDialogJson() {
        return mThankYouDialogJson;
    }

    public void setThankYouDialogJson(JSONObject thankYouDialogJson) {
        mThankYouDialogJson = thankYouDialogJson;
    }

    public boolean isTaskRunning() {
        return mIsTaskRunning;
    }

    public void setTaskRunning(boolean taskRunning) {
        mIsTaskRunning = taskRunning;
    }

    public boolean hasUploaded() {
        return mHasUploaded;
    }

    public void setHasUploaded(boolean hasUploaded) {
        mHasUploaded = hasUploaded;
    }

    public Task getCurrentTask() {
        return mCurrentTask;
    }

    public void setCurrentTask(Task currentTask) {
        mCurrentTask = currentTask;
    }
}
