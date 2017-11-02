package com.rmathur.bixbegone;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    // global instance of shared preferences
    public SharedPreferences preferences;

    // constants
    final String SHARED_PREF_NAME = "general_prefs";
    final String SERVICE_ENABLED_PREF = "service_enabled";
    final String START_ON_BOOT_PREF = "start_on_boot";
    final String BUTTON_ACTION_PREF = "button_action";

    public PreferenceHelper(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean getServiceEnabledStatus() {
        return preferences.getBoolean(SERVICE_ENABLED_PREF, false);
    }

    public void setServiceEnabledStatus(boolean enabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SERVICE_ENABLED_PREF, enabled);
        editor.commit();
    }

    public boolean getStartOnBootStatus() {
        return preferences.getBoolean(START_ON_BOOT_PREF, false);
    }

    public void setStartOnBootStatus(boolean enabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(START_ON_BOOT_PREF, enabled);
        editor.commit();
    }

    public int getButtonAction() {
        return preferences.getInt(BUTTON_ACTION_PREF, 0);
    }

    public void setButtonAction(int action) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BUTTON_ACTION_PREF, action);
        editor.commit();
    }
}
