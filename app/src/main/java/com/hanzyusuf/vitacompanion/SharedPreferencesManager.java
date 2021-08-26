package com.hanzyusuf.vitacompanion;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "PSV_COMPANION_APP_SETTINGS";

    // properties
    private static final String PSV_IP = "PSV_IP";

    private SharedPreferencesManager() {}

    private static SharedPreferences getSharedPreferences() {
        return MyApplication.getAppContext().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getLastIP() {
        return getSharedPreferences().getString(PSV_IP , null);
    }

    public static void setLastIP(String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PSV_IP, newValue);
        editor.apply();
    }
}