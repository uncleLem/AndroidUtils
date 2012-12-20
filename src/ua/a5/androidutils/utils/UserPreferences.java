package ua.a5.androidutils.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserPreferences {

    public static final String SETTINGS = "SETTINGS";

    private Context context;

    public UserPreferences(Context context) {
        if (context != null) {
            this.context = context;
        }
    }

    public String getStringPreference(String key) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public float getFloatPreference(String key) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return settings.getFloat(key, -1f);
    }

    public long getLongPreference(String key) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return settings.getLong(key, -1l);
    }

    public int getIntPreference(String key) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return settings.getInt(key, -1);
    }

    public boolean getBooleanPreference(String key) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public void setBooleanPreferences(String key, Boolean data) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, data);
        editor.commit();
    }

    public void setStringPreferences(String key, String data) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public void setIntPreferences(String key, int data) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, data);
        editor.commit();
    }

    public void setFloatPreferences(String key, float data) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, data);
        editor.commit();
    }

    public void setLongPreferences(String key, long data) {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, data);
        editor.commit();
    }
}

