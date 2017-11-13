package sk.brecka.uxmobile.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
    private static final String TAG_DEV_REQUEST_ID = "request_id";
    private static final String TAG_DEV_DEVICE_CODE_INTERNAL = "device_code_internal";
    private static final String TAG_DEV_MANUFACTURER = "manufacturer";
    private static final String TAG_DEV_MODEL = "model";
    private static final String TAG_DEV_NET_SCREEN_HEIGHT = "net_screen_height";
    private static final String TAG_DEV_NET_SCREEN_WIDTH = "net_screen_width";
    private static final String TAG_DEV_PHYSICAL_MENU_BUTTON = "physical_menu_button";
    private static final String TAG_DEV_FONT_SCALE = "font_scale";
    private static final String TAG_DEV_DEVICE_UNIQUE_ID = "device_unique_id";
    private static final String TAG_DEV_INITIAL_ORIENTATION = "initial_orientation";

    // session information
    private String mApiKey;
    private String mSession;

    private boolean mIsRecordingVideo;
    private boolean mIsRecordingEvents;
    private boolean mIsRecordingWifiOnly;

    private int mVideoFps;
    private int mVideoBitrate;
    private int mVideoHeight;
    private int mVideoWidth;

    // potencialne v buducnosti
//    public static final String TAG_DETECT_CRASHES = "detect_crash";
//    public static final String TAG_HIDE_INPUT = "hide_input";
//    public static final String TAG_HIDE_SENSITIVE = "hide_sensitive";
//    public static final String TAG_HIDE_WEB = "hide_web";
//    public static final String TAG_UPLOAD_APP_ICON = "upload_icon";
//    public static final String TAG_UPLOAD_VIDEO_ON_CRASH = "upload_video_on_crash";

    private static final Config sSession = new Config();

    private Config() {
    }

    public static Config get() {
        return sSession;
    }

    public static Map<String, String> getDeviceConfig(Context context) {
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

        deviceConfig.put(TAG_DEV_DEVICE_CODE, "");
        deviceConfig.put(TAG_DEV_OS_VERSION, Build.VERSION.RELEASE);
        deviceConfig.put(TAG_DEV_SDK_VERSION, String.valueOf(Build.VERSION.SDK_INT));
        deviceConfig.put(TAG_DEV_LOCALE, Locale.getDefault().getLanguage());
        deviceConfig.put(TAG_DEV_CONNECTIVITY, "");
        deviceConfig.put(TAG_DEV_SCREEN_WIDTH, String.valueOf(displayMetrics.widthPixels));
        deviceConfig.put(TAG_DEV_SCREEN_HEIGHT, String.valueOf(displayMetrics.heightPixels));
        deviceConfig.put(TAG_DEV_SCREEN_DPI, String.valueOf(resources.getConfiguration().densityDpi));
        deviceConfig.put(TAG_DEV_CLIENT_TIME, new Date().toString());
        deviceConfig.put(TAG_DEV_REQUEST_ID, "");
        deviceConfig.put(TAG_DEV_DEVICE_CODE_INTERNAL, "");
        deviceConfig.put(TAG_DEV_MANUFACTURER, Build.MANUFACTURER);
        deviceConfig.put(TAG_DEV_MODEL, Build.MODEL);
        deviceConfig.put(TAG_DEV_PHYSICAL_MENU_BUTTON, String.valueOf(ViewConfiguration.get(context).hasPermanentMenuKey()));
        deviceConfig.put(TAG_DEV_FONT_SCALE, String.valueOf(resources.getConfiguration().fontScale));
        deviceConfig.put(TAG_DEV_DEVICE_UNIQUE_ID, "");
        deviceConfig.put(TAG_DEV_INITIAL_ORIENTATION, "");

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
}
