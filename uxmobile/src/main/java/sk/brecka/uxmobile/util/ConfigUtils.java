package sk.brecka.uxmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by matej on 8.10.2017.
 */

public class ConfigUtils {

    private static final String CONFIG_APP_VERSION = "app_version";
    private static final String CONFIG_APP_VERSION_CODE = "app_version_code";
    private static final String CONFIG_PACKAGE_NAME = "package_name";
    private static final String CONFIG_DEVICE_CODE = "device_code";
    private static final String CONFIG_OS_VERSION = "os_version";
    private static final String CONFIG_SDK_VERSION = "sdk_version";
    private static final String CONFIG_LOCALE = "locale";
    private static final String CONFIG_CONNECTIVITY = "connectivity";
    private static final String CONFIG_SCREEN_WIDTH = "screen_width";
    private static final String CONFIG_SCREEN_HEIGHT = "screen_height";
    private static final String CONFIG_SCREEN_DPI = "screen_dpi";
    private static final String CONFIG_CLIENT_TIME = "client_time";
    private static final String CONFIG_REQUEST_ID = "request_id";
    private static final String CONFIG_DEVICE_CODE_INTERNAL = "device_code_internal";
    private static final String CONFIG_MANUFACTURER = "manufacturer";
    private static final String CONFIG_MODEL = "model";
    private static final String CONFIG_NET_SCREEN_HEIGHT = "net_screen_height";
    private static final String CONFIG_NET_SCREEN_WIDTH = "net_screen_width";
    private static final String CONFIG_PHYSICAL_MENU_BUTTON = "physical_menu_button";
    private static final String CONFIG_FONT_SCALE = "font_scale";
    private static final String CONFIG_DEVICE_UNIQUE_ID = "device_unique_id";
    private static final String CONFIG_INITIAL_ORIENTATION = "initial_orientation";

    public static JSONObject getConfig(Context context) {

        JSONObject config = null;
        try {
            final PackageManager pm = context.getPackageManager();
            final String packageName = context.getPackageName();
            final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            final Resources resources = context.getResources();
            final PackageInfo pInfo = pm.getPackageInfo(packageName, 0);

            config = new JSONObject()
                    .put(CONFIG_APP_VERSION, pInfo.versionName)
                    .put(CONFIG_APP_VERSION_CODE, pInfo.versionCode)
                    .put(CONFIG_PACKAGE_NAME, pInfo.packageName)
                    .put(CONFIG_DEVICE_CODE, "")
                    .put(CONFIG_OS_VERSION, Build.VERSION.RELEASE)
                    .put(CONFIG_SDK_VERSION, Build.VERSION.SDK_INT)
                    .put(CONFIG_LOCALE, Locale.getDefault().getLanguage())
                    .put(CONFIG_CONNECTIVITY, "")
                    .put(CONFIG_SCREEN_WIDTH, displayMetrics.widthPixels)
                    .put(CONFIG_SCREEN_HEIGHT, displayMetrics.heightPixels)
                    .put(CONFIG_SCREEN_DPI, resources.getConfiguration().densityDpi)
                    .put(CONFIG_CLIENT_TIME, new Date().toString())
                    .put(CONFIG_REQUEST_ID, "")
                    .put(CONFIG_DEVICE_CODE_INTERNAL, "")
                    .put(CONFIG_MANUFACTURER, Build.MANUFACTURER)
                    .put(CONFIG_MODEL, Build.MODEL)
                    .put(CONFIG_PHYSICAL_MENU_BUTTON, ViewConfiguration.get(context).hasPermanentMenuKey())
                    .put(CONFIG_FONT_SCALE, resources.getConfiguration().fontScale)
                    .put(CONFIG_DEVICE_UNIQUE_ID, "")
                    .put(CONFIG_INITIAL_ORIENTATION, "");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException ignored) {
            //
        }

        return config;
    }

    private ConfigUtils() {
    }
}
