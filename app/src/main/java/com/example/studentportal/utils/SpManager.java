package com.example.studentportal.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpManager {

    // USER POST
    public static final String PREF_BATCH = "PREF_BATCH";
    public static final String PREF_POST_IMAGE_URL = "PREF_IMAGE_URL";
    public static final String PREF_USER_ID = "PREF_USER_ID";
    public static final String PREF_USER_NAME = "PREF_USER_NAME";
    public static final String PREF_USER_PROFILE_IMAGE = "PREF_USER_PROFILE_IMAGE";

    private static final String PREF = "EMON_PREF";

    public static void saveBoolean(Context context, String key, Boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveString(Context context, String key, String Value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, Value);
        editor.apply();

    }


    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "DNF");
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sharedPrf = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPrf.getInt(key, 0);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPrf = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPrf.getInt(key, defaultValue);
    }


    public static void clearData(Context context) {
        SharedPreferences sharedPrf = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrf.edit();
        editor.clear();
        editor.apply();
    }


}
