package com.tentel.shared.settings.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tentel.shared.packages.PackageUtils;
import com.tentel.shared.tentel.tentelConstants;
import com.tentel.shared.logger.Logger;
import com.tentel.shared.data.DataUtils;
import com.tentel.shared.settings.preferences.tentelPreferenceConstants.tentel_APP;

public class tentelAppSharedPreferences {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

    private int MIN_FONTSIZE;
    private int MAX_FONTSIZE;
    private int DEFAULT_FONTSIZE;

    private static final String LOG_TAG = "tentelAppSharedPreferences";

    private tentelAppSharedPreferences(@NonNull Context context) {
        mContext = context;
        mSharedPreferences = getPrivateSharedPreferences(mContext);

        setFontVariables(context);
    }

    /**
     * Get the {@link Context} for a package name.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link tentelConstants#tentel_PACKAGE_NAME}.
     * @return Returns the {@link tentelAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static tentelAppSharedPreferences build(@NonNull final Context context) {
        Context tentelPackageContext = PackageUtils.getContextForPackage(context, tentelConstants.tentel_PACKAGE_NAME);
        if (tentelPackageContext == null)
            return null;
        else
            return new tentelAppSharedPreferences(tentelPackageContext);
    }

    /**
     * Get the {@link Context} for a package name.
     *
     * @param context The {@link Activity} to use to get the {@link Context} of the
     *                {@link tentelConstants#tentel_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link tentelAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static tentelAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context tentelPackageContext = PackageUtils.getContextForPackageOrExitApp(context, tentelConstants.tentel_PACKAGE_NAME, exitAppOnError);
        if (tentelPackageContext == null)
            return null;
        else
            return new tentelAppSharedPreferences(tentelPackageContext);
    }

    private static SharedPreferences getPrivateSharedPreferences(Context context) {
        if (context == null) return null;
        return SharedPreferenceUtils.getPrivateSharedPreferences(context, tentelConstants.tentel_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION);
    }



    public boolean shouldShowTerminalToolbar() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_SHOW_TERMINAL_TOOLBAR, tentel_APP.DEFAULT_VALUE_SHOW_TERMINAL_TOOLBAR);
    }

    public void setShowTerminalToolbar(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_SHOW_TERMINAL_TOOLBAR, value, false);
    }

    public boolean toogleShowTerminalToolbar() {
        boolean currentValue = shouldShowTerminalToolbar();
        setShowTerminalToolbar(!currentValue);
        return !currentValue;
    }



    public boolean isTerminalMarginAdjustmentEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_TERMINAL_MARGIN_ADJUSTMENT, tentel_APP.DEFAULT_TERMINAL_MARGIN_ADJUSTMENT);
    }

    public void setTerminalMarginAdjustment(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_TERMINAL_MARGIN_ADJUSTMENT, value, false);
    }



    public boolean isSoftKeyboardEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_SOFT_KEYBOARD_ENABLED, tentel_APP.DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED);
    }

    public void setSoftKeyboardEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_SOFT_KEYBOARD_ENABLED, value, false);
    }

    public boolean isSoftKeyboardEnabledOnlyIfNoHardware() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE, tentel_APP.DEFAULT_VALUE_KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE);
    }

    public void setSoftKeyboardEnabledOnlyIfNoHardware(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_SOFT_KEYBOARD_ENABLED_ONLY_IF_NO_HARDWARE, value, false);
    }



    public boolean shouldKeepScreenOn() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_KEEP_SCREEN_ON, tentel_APP.DEFAULT_VALUE_KEEP_SCREEN_ON);
    }

    public void setKeepScreenOn(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_KEEP_SCREEN_ON, value, false);
    }



    public static int[] getDefaultFontSizes(Context context) {
        float dipInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());

        int[] sizes = new int[3];

        // This is a bit arbitrary and sub-optimal. We want to give a sensible default for minimum font size
        // to prevent invisible text due to zoom be mistake:
        sizes[1] = (int) (4f * dipInPixels); // min

        // http://www.google.com/design/spec/style/typography.html#typography-line-height
        int defaultFontSize = Math.round(12 * dipInPixels);
        // Make it divisible by 2 since that is the minimal adjustment step:
        if (defaultFontSize % 2 == 1) defaultFontSize--;

        sizes[0] = defaultFontSize; // default

        sizes[2] = 256; // max

        return sizes;
    }

    public void setFontVariables(Context context) {
        int[] sizes = getDefaultFontSizes(context);

        DEFAULT_FONTSIZE = sizes[0];
        MIN_FONTSIZE = sizes[1];
        MAX_FONTSIZE = sizes[2];
    }

    public int getFontSize() {
        int fontSize = SharedPreferenceUtils.getIntStoredAsString(mSharedPreferences, tentel_APP.KEY_FONTSIZE, DEFAULT_FONTSIZE);
        return DataUtils.clamp(fontSize, MIN_FONTSIZE, MAX_FONTSIZE);
    }

    public void setFontSize(int value) {
        SharedPreferenceUtils.setIntStoredAsString(mSharedPreferences, tentel_APP.KEY_FONTSIZE, value, false);
    }

    public void changeFontSize(boolean increase) {
        int fontSize = getFontSize();

        fontSize += (increase ? 1 : -1) * 2;
        fontSize = Math.max(MIN_FONTSIZE, Math.min(fontSize, MAX_FONTSIZE));

        setFontSize(fontSize);
    }



    public String getCurrentSession() {
        return SharedPreferenceUtils.getString(mSharedPreferences, tentel_APP.KEY_CURRENT_SESSION, null, true);
    }

    public void setCurrentSession(String value) {
        SharedPreferenceUtils.setString(mSharedPreferences, tentel_APP.KEY_CURRENT_SESSION, value, false);
    }



    public int getLogLevel() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, tentel_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, tentel_APP.KEY_LOG_LEVEL, logLevel, false);
    }



    public int getLastNotificationId() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, tentel_APP.KEY_LAST_NOTIFICATION_ID, tentel_APP.DEFAULT_VALUE_KEY_LAST_NOTIFICATION_ID);
    }

    public void setLastNotificationId(int notificationId) {
        SharedPreferenceUtils.setInt(mSharedPreferences, tentel_APP.KEY_LAST_NOTIFICATION_ID, notificationId, false);
    }



    public boolean isTerminalViewKeyLoggingEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED, tentel_APP.DEFAULT_VALUE_TERMINAL_VIEW_KEY_LOGGING_ENABLED);
    }

    public void setTerminalViewKeyLoggingEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED, value, false);
    }



    public boolean arePluginErrorNotificationsEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED, tentel_APP.DEFAULT_VALUE_PLUGIN_ERROR_NOTIFICATIONS_ENABLED);
    }

    public void setPluginErrorNotificationsEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED, value, false);
    }



    public boolean areCrashReportNotificationsEnabled() {
        return SharedPreferenceUtils.getBoolean(mSharedPreferences, tentel_APP.KEY_CRASH_REPORT_NOTIFICATIONS_ENABLED, tentel_APP.DEFAULT_VALUE_CRASH_REPORT_NOTIFICATIONS_ENABLED);
    }

    public void setCrashReportNotificationsEnabled(boolean value) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, tentel_APP.KEY_CRASH_REPORT_NOTIFICATIONS_ENABLED, value, false);
    }

}
