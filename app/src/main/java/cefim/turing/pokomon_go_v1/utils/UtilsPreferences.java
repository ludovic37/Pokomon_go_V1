package cefim.turing.pokomon_go_v1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by peterbardu on 3/24/17.
 */

public class UtilsPreferences {

    public static final String PREF_NAME = "prefs_file";
    public static final String PREF_TOKEN = "prefs_token";

    private static UtilsPreferences INSTANCE;

    private static SharedPreferences mPreferences;

    private UtilsPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static UtilsPreferences getPreferences(Context context) {
        if (INSTANCE == null)
            INSTANCE = new UtilsPreferences(context);

        return INSTANCE;
    }

    public String getString(String key) {
        return mPreferences.getString(key, null);
    }

    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public int getInt(String key, int value) {
        return mPreferences.getInt(key, value);
    }

    public long getLong(String key) {
        return mPreferences.getLong(key, 0);
    }

    public boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean value) {
        return mPreferences.getBoolean(key, value);
    }

    public void setKey(String key, String value) {
        Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setKey(String key, int value) {
        Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setKey(String key, boolean value) {
        Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setKey(String key, long value) {
        Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
