package sk.brecka.uxmobile.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by matej on 16.10.2017.
 */

public class Config {
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

    public static Map<String, String> get(Context context) {
        final Map<String, String> config = new LinkedHashMap<>();

        try {
            final String packageName = context.getPackageName();
            final PackageManager pm = context.getPackageManager();
            final PackageInfo pInfo = pm.getPackageInfo(packageName, 0);

            config.put(CONFIG_APP_VERSION, pInfo.versionName);
            config.put(CONFIG_APP_VERSION_CODE, String.valueOf(pInfo.versionCode));
            config.put(CONFIG_PACKAGE_NAME, pInfo.packageName);

        } catch (PackageManager.NameNotFoundException ignored) {
            config.put(CONFIG_APP_VERSION, "");
            config.put(CONFIG_APP_VERSION_CODE, "");
            config.put(CONFIG_PACKAGE_NAME, "");
        }

        final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        final Resources resources = context.getResources();

        config.put(CONFIG_DEVICE_CODE, "");
        config.put(CONFIG_OS_VERSION, Build.VERSION.RELEASE);
        config.put(CONFIG_SDK_VERSION, String.valueOf(Build.VERSION.SDK_INT));
        config.put(CONFIG_LOCALE, Locale.getDefault().getLanguage());
        config.put(CONFIG_CONNECTIVITY, "");
        config.put(CONFIG_SCREEN_WIDTH, String.valueOf(displayMetrics.widthPixels));
        config.put(CONFIG_SCREEN_HEIGHT, String.valueOf(displayMetrics.heightPixels));
        config.put(CONFIG_SCREEN_DPI, String.valueOf(resources.getConfiguration().densityDpi));
        config.put(CONFIG_CLIENT_TIME, new Date().toString());
        config.put(CONFIG_REQUEST_ID, "");
        config.put(CONFIG_DEVICE_CODE_INTERNAL, "");
        config.put(CONFIG_MANUFACTURER, Build.MANUFACTURER);
        config.put(CONFIG_MODEL, Build.MODEL);
        config.put(CONFIG_PHYSICAL_MENU_BUTTON, String.valueOf(ViewConfiguration.get(context).hasPermanentMenuKey()));
        config.put(CONFIG_FONT_SCALE, String.valueOf(resources.getConfiguration().fontScale));
        config.put(CONFIG_DEVICE_UNIQUE_ID, "");
        config.put(CONFIG_INITIAL_ORIENTATION, "");

        return config;
    }
}
