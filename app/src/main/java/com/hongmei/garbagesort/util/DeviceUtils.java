package com.hongmei.garbagesort.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.StringUtils;
import com.hongmei.garbagesort.ext.UtilsExtKt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author shenrh
 * @version 17/1/11
 */
public final class DeviceUtils {

    private static final int DEFAULT_CACHE_SIZE = 1024;

    private static Boolean isEmui;

    // 防止被继承
    private DeviceUtils() {
    }

    public static boolean isEmui() {
        if (isEmui != null) {
            return isEmui;
        }
        boolean result;
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            Object emuiVersion = getString.invoke(null, "ro.build.version.emui");
            result = emuiVersion instanceof String && !StringUtils.isEmpty((String) emuiVersion) && !Build.UNKNOWN.equals((String) emuiVersion);
        } catch (Throwable e) {
            e.printStackTrace();
            result = false;
        }
        isEmui = result;
        return result;
    }

    private static Boolean isCoolpad;

    public static boolean isCoolpad() {
        if (isCoolpad != null) {
            return isCoolpad;
        }
        final String brand = Build.BRAND;
        if (!TextUtils.isEmpty(brand)) {
            isCoolpad = brand.toLowerCase(Locale.getDefault()).contains("coolpad");
        } else {
            isCoolpad = false;
        }
        return isCoolpad;
    }

    /**
     * 判断设备是否使用代理上网
     *
     * @param context
     * @return
     */
    public static boolean isWifiProxy(Context context) {
        String proxyHost;
        int proxyPort;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            proxyHost = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyHost = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyHost)) && (proxyPort != -1);
    }

    /**
     * 判断设备上是否安装了xposed
     *
     * @return
     */
    public static boolean isInstallXposed() {
        try {
            throw new Exception("hook");
        } catch (Exception e) {
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().contains("de.robv.android.xposed")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在页面onCreate的setContentView()之前设置
     *
     * @param activity
     */
    public static void setNoScreenCap(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }


    private static final String TAG = "DeviceUtils";
    private static int sStatusBarHeight = 0;

    /**
     * check current ROM is MIUI or not
     */
    private static boolean sIsMiui = false;
    private static boolean sIsMiuiInited = false;

    // 屏幕尺寸相关
    public static boolean deviceDataHasInit = false;
    public static int displayMetricsWidthPixels;
    public static int displayMetricsHeightPixels;

    private static int mIsFoldableScreen = -1;

    private static boolean checkHuaweiFoldableList(String deviceName) {
        if (TextUtils.equals(deviceName, "HWTAH")) {
            return true;
        }
        if (TextUtils.equals(deviceName, "unknownRLI")) {
            return true;
        }
        if (TextUtils.equals(deviceName, "unknownRHA")) {
            return true;
        }
        if (TextUtils.equals(deviceName, "HWTAH-C")) {
            return true;
        }
        return false;
    }

    public static boolean isFoldableScreen() {
        if (mIsFoldableScreen > -1) {
            return mIsFoldableScreen > 0;
        }
        mIsFoldableScreen = 0;
        if (isSamsung() && TextUtils.equals(Build.DEVICE, "winner")) {
            mIsFoldableScreen = 1;
        } else if (isHuawei() && checkHuaweiFoldableList(Build.DEVICE)) {
            mIsFoldableScreen = 1;
        }
        return mIsFoldableScreen > 0;
    }

    public static boolean isMiui() {
        if (!sIsMiuiInited) {
            try {
                Class<?> clz = Class.forName("miui.os.Build");
                if (clz != null) {
                    sIsMiui = true;
                }
            } catch (Exception e) {
                // ignore
            }
            sIsMiuiInited = true;
        }
        return sIsMiui;
    }

    /**
     * Miui自有Api
     */
    public static void setMiuiStatusBarDarkMode(boolean darkmode, Window window) {
        try {
            Class<? extends Window> clazz = window.getClass();
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static String sMiuiVersion;

    public static boolean isMiuiV6() {
        initMiuiVersion();
        return "V6".equals(sMiuiVersion);
    }

    public static boolean isMiuiV7() {
        initMiuiVersion();
        return "V7".equals(sMiuiVersion);
    }

    public static boolean isMiuiV8() {
        initMiuiVersion();
        return "V8".equals(sMiuiVersion);
    }

    public static boolean isMiuiV9() {
        initMiuiVersion();
        return "V9".equals(sMiuiVersion);
    }

    private static void initMiuiVersion() {
        if (sMiuiVersion == null) {
            try {
                sMiuiVersion = getProperty("ro.miui.ui.version.name");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sMiuiVersion = sMiuiVersion == null ? "" : sMiuiVersion;
        }
    }

    private static int sEmuiLevel = -1;

    public static int getEmuiLevel() {
        if (sEmuiLevel > -1) {
            return sEmuiLevel;
        }
        sEmuiLevel = 0;
        Properties properties = new Properties();
        File propFile = new File(Environment.getRootDirectory(), "build.prop");
        FileInputStream fis = null;
        if (propFile.exists()) {
            try {
                fis = new FileInputStream(propFile);
                properties.load(fis);
                fis.close();
                fis = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (properties.containsKey("ro.build.hw_emui_api_level")) {
            String valueString = properties.getProperty("ro.build.hw_emui_api_level");
            try {
                sEmuiLevel = Integer.parseInt(valueString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sEmuiLevel;
    }

    public static boolean isHuawei() {
        return Build.MANUFACTURER != null && Build.MANUFACTURER.toUpperCase(Locale.getDefault()).contains("HUAWEI");
    }

    @SuppressWarnings("checkstyle:MethodName")
    public static boolean isSumsungV4_4_4() {
        if (isSamsung()) {
            if (Build.VERSION.RELEASE.startsWith("4.4.4")) {
                return true;
            } else if (Build.VERSION.RELEASE.startsWith("4.4.2") && Build.DEVICE.startsWith("klte")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSumsungV5() {
        if (isSamsung()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSumsungCorePrime() {
        if (isSamsung()) {
            if (Build.DISPLAY.contains("G3608ZMU1AOA4")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFlyme() {
        return Build.DISPLAY.startsWith("Flyme");
    }

    public static boolean isFlyme2() {
        return Build.DISPLAY.startsWith("Flyme 2");
    }

    public static boolean isFlyme4() {
        return Build.DISPLAY.startsWith("Flyme OS 4");
    }

    public static boolean isFlyme5() {
        return Build.DISPLAY.startsWith("Flyme 5");
    }

    public static boolean isFlyme6() {
        return Build.DISPLAY.startsWith("Flyme 6");
    }

    public static boolean isFlyme7() {
        return Build.DISPLAY.startsWith("Flyme 7");
    }

    public static boolean isOnePlusLOLLIPOP() {
        return Build.BRAND.equals("ONEPLUS") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isSamsung() {
        if ("samsung".equalsIgnoreCase(Build.BRAND) || "samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }

        return false;
    }

    public static boolean isLG() {
        if ("lge".equalsIgnoreCase(Build.BRAND) || "lge".equalsIgnoreCase(Build.MANUFACTURER)) {
            if (Build.MODEL != null) {
                String str = Build.MODEL.toLowerCase(Locale.getDefault());
                if (str.contains("lg")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isMeizuMx3() {
        if (isMeizu()) {
            return "mx3".equalsIgnoreCase(Build.DEVICE);
        }

        return false;
    }

    @SuppressWarnings("checkstyle:BooleanExpressionComplexity")
    public static boolean isHtcOs() {
        if (Build.BRAND != null && Build.BRAND.toLowerCase(Locale.getDefault()).contains("htc")
                && Build.MANUFACTURER != null && Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("htc")
                && Build.MODEL != null && Build.MODEL.toLowerCase(Locale.getDefault()).contains("htc")) {
            return true;
        }
        return false;
    }

    public static boolean isMeizu() {
        String brand = Build.BRAND;
        if (brand == null) {
            return false;
        }

        return brand.toLowerCase(Locale.ENGLISH).indexOf("meizu") > -1;
    }

    public static boolean isVivo() {
        String brand = Build.BRAND;
        if (!TextUtils.isEmpty(brand) && brand.toLowerCase(Locale.getDefault()).contains("vivo")) {
            return true;
        }
        String model = Build.MODEL;
        if (!TextUtils.isEmpty(model) && model.toLowerCase(Locale.getDefault()).contains("vivo")) {
            return true;
        }
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer) && manufacturer.toLowerCase(Locale.getDefault()).contains("vivo")) {
            return true;
        }
        return false;
    }

//    public static boolean isEmui() {
//        return check(ROM_EMUI);
//    }

    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    public static boolean isQiku() {
        return check(ROM_QIKU) || check("360");
    }

    public static boolean isSmartisan() {
        return check(ROM_SMARTISAN);
    }

    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_GIONEE = "QIONEE";
    public static final String ROM_VIVO = "VIVO";
    public static final String ROM_QIKU = "QIKU";
    public static final String ROM_ZTE = "zte";
    public static final String ROM_LENOVO = "LENOVO";
    public static final String ROM_SAMSUNG = "samsung";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
    private static final String KEY_VERSION_GIONEE = "ro.gn.sv.version";
    private static final String KEY_VERSION_LENOVO = "ro.lenovo.lvp.version";

    private static String sName;

    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    private static String sVersion;

    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    public static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_GIONEE))) {
            sName = ROM_GIONEE;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_LENOVO))) {
            sName = ROM_LENOVO;
        } else if (getManufacturer().toLowerCase(Locale.getDefault()).contains(ROM_SAMSUNG)) {
            sName = ROM_SAMSUNG;
        } else if (getManufacturer().toLowerCase(Locale.getDefault()).contains(ROM_ZTE)) {
            // 不靠谱
            sName = ROM_ZTE;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase(Locale.getDefault()).contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase(Locale.getDefault());
            }
        }
        return sName.equals(rom);
    }

    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"), DEFAULT_CACHE_SIZE);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public static String getManufacturer() {
        return (Build.MANUFACTURER) == null ? "" : (Build.MANUFACTURER).trim();
    }

    private static final String KEY_SYSTEM_PROPERTIES_CACHE = "system_properties_cache";
    private static final Map<String, String> SYSTEM_PROPERTIES_CACHE = new HashMap<>();

    public static String getSystemPropertyInSp(Context context, String propName) {
        String result;
        synchronized (SYSTEM_PROPERTIES_CACHE) {
            result = SYSTEM_PROPERTIES_CACHE.get(propName);
            if (StringUtils.isEmpty(result)) { // 内存缓存中没有就从sp中读
                try {
                    SharedPreferences sp = context.getSharedPreferences(KEY_SYSTEM_PROPERTIES_CACHE, Context.MODE_PRIVATE);
                    result = sp.getString(propName, null);
                    if (StringUtils.isEmpty(result)) { // sp中没有，从cmd中读取并存入sp中
                        result = getSystemProperty(propName);
                        if (!StringUtils.isEmpty(result)) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(propName, result);
                            editor.apply();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"), DEFAULT_CACHE_SIZE);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 获取 emui 版本号
     *
     * @return
     */
    public static double getEmuiVersion() {
        final double defaultVersion = 4.0;
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultVersion;
    }

    public static boolean hasSmartBar() {
        if (!isMeizu()) {
            return false;
        }

        try {
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    public static boolean hasVirtualButtons(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            boolean hasPermanentMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasVirtualButtons = !hasPermanentMenuKey;
            return hasVirtualButtons;
        } else {
            return false;
        }
    }

    public static String getDeviceId(Context context) {
        String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (TextUtils.isEmpty(deviceId) || "000000000000000".equals(deviceId)) {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            deviceId = wm.getConnectionInfo().getMacAddress();
        }
        return deviceId;
    }

    public static boolean isLenovo() {
        if ("lenovo".equalsIgnoreCase(Build.BRAND) || "lenovo".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        if ("motorola".equalsIgnoreCase(Build.BRAND) || "motorola".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    public static long getTotalMemory() {
        String memInfoFile = "/proc/meminfo"; // 系统内存信息文件
        final int size = 8192;
        final int byteSize = DEFAULT_CACHE_SIZE;
        long initialMemory = 0;

        BufferedReader localBufferedReader = null;
        try {
            FileReader localFileReader = new FileReader(memInfoFile);
            localBufferedReader = new BufferedReader(localFileReader, size);
            String totalMemInfo = localBufferedReader.readLine(); // 读取meminfo第一行，系统总内存大小
            String[] memInfos = totalMemInfo.split("\\s+");

            initialMemory = Integer.parseInt(memInfos[1]) * byteSize; // 获得系统总内存，单位是KB，乘以1024转换为Byte

//			Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localBufferedReader != null) {
                try {
                    localBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return initialMemory;
    }

    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight > 0) {
            return sStatusBarHeight;
        }
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //从getDimensionPixelOffset替换为getDimensionPixelSize，部分华为8.0设备有系统bug，低分辨率模式下getDimensionPixelOffset拿到的值较大.
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        final int dpValue = 25;
        if (result == 0) {
            result = UtilsExtKt.toPx(dpValue);
        }
        sStatusBarHeight = result;
        return result;
    }

    public static void initDeviceData(Context context) {
        if (context == null) {
            return;
        }
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager == null) {
            return;
        }
        manager.getDefaultDisplay().getMetrics(metric);
        int orientation = manager.getDefaultDisplay().getOrientation();
        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
            displayMetricsWidthPixels = metric.heightPixels; // 屏幕宽度（像素）
            displayMetricsHeightPixels = metric.widthPixels; // 屏幕高度（像素）
        } else {
            displayMetricsWidthPixels = metric.widthPixels; // 屏幕宽度（像素）
            displayMetricsHeightPixels = metric.heightPixels; // 屏幕高度（像素）
        }
        deviceDataHasInit = true;
    }

    /**
     * 获取屏幕宽度，无论横屏或竖屏，获取到的值保持一致
     */
    public static int getEquipmentWidth(Context context) {
        if (!deviceDataHasInit || isFoldableScreen()) {
            initDeviceData(context);
        }
        return displayMetricsWidthPixels;
    }

    /**
     * 获取屏幕高度，无论横屏或竖屏，获取到的值保持一致
     */
    public static int getEquipmentHeight(Context context) {
        if (!deviceDataHasInit || isFoldableScreen()) {
            initDeviceData(context);
        }
        return displayMetricsHeightPixels;
    }

    public static boolean isPad(Context context) {
        if (context == null || context.getResources() == null || context.getResources().getConfiguration() == null) {
            return false;
        }
        return "tablet".equals(getDeviceType(context));
    }

    /**
     * 判断是否为华为 3.x
     *
     * @return
     */
    @SuppressWarnings("checkstyle:MethodName")
    public static boolean isHuawei_Os_3x() {
        if (isHuawei()) {
            String property = getSystemProperty("ro.build.version.emui");
            if ("EmotionUI 3".equals(property) || "EmotionUI_3.1".contains(property)) {
                return true;
            }
            if ("EmotionUI_3.0".contains(property)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                return getInternalDimensionSize(context, IMMERSION_NAVIGATION_BAR_HEIGHT);
            }
        }
        return result;
    }


    /**
     * MIUI导航栏显示隐藏标识位
     */
    private static final String IMMERSION_MIUI_NAVIGATION_BAR_HIDE_SHOW = "force_fsg_nav_bar";

    /**
     * EMUI导航栏显示隐藏标识位
     */
    private static final String IMMERSION_EMUI_NAVIGATION_BAR_HIDE_SHOW = "navigationbar_is_min";

    /**
     * 导航栏竖屏高度标识位
     */
    static final String IMMERSION_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";

    /**
     * 是否有导航栏
     *
     * @param activity
     * @return
     */
    public static boolean hasNavBar(Activity activity) {
        //判断小米手机是否开启了全面屏，开启了，直接返回false
        if (Settings.Global.getInt(activity.getContentResolver(), IMMERSION_MIUI_NAVIGATION_BAR_HIDE_SHOW, 0) != 0) {
            return false;
        }
        //判断华为手机是否隐藏了导航栏，隐藏了，直接返回false
        if (isEmui()) {
            if (isHuawei_Os_3x() || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (Settings.System.getInt(activity.getContentResolver(), IMMERSION_EMUI_NAVIGATION_BAR_HIDE_SHOW, 0) != 0) {
                    return false;
                }
            } else {
                if (Settings.Global.getInt(activity.getContentResolver(), IMMERSION_EMUI_NAVIGATION_BAR_HIDE_SHOW, 0) != 0) {
                    return false;
                }
            }
        }
        //其他手机根据屏幕真实高度与显示高度是否相同来判断
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 获取系统属性尺寸，如系统导航栏、状态栏
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    return Math.round(sizeOne * densityTwo / densityOne);
                }
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }

    private static String getDeviceType(Context context) {
        String devicetype = getSystemPropertyInSp(context, "ro.build.characteristics");
        if ((((TextUtils.isEmpty(devicetype)) || (!devicetype.equals("tablet"))))) {
            devicetype = "phone";
        }
        return devicetype;
    }

    /**
     * 获取物理屏幕宽度 (实时获取)
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        int orientation = manager.getDefaultDisplay().getOrientation();
        int width;
        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
            width = metric.heightPixels; // 屏幕宽度（像素）
        } else {
            width = metric.widthPixels; // 屏幕宽度（像素）
        }
        return width;
    }

    /**
     * 获取物理屏幕高度 (实时获取)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        int orientation = manager.getDefaultDisplay().getOrientation();
        int height;
        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
            height = metric.widthPixels; // 屏幕高度（像素）
        } else {
            height = metric.heightPixels; // 屏幕高度（像素）
        }
        return height;
    }

    private static volatile Properties properties = null;

    private static String getProperty(final String name) throws IOException {
        if (properties == null) {
            synchronized (DeviceUtils.class) {
                if (properties == null) {
                    properties = new Properties();
                    properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                }
            }
        }
        return properties.getProperty(name);
    }

}