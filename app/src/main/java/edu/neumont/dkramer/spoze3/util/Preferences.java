package edu.neumont.dkramer.spoze3.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * An easier way to set and read from SharedPreferences
 * Created by dkramer on 11/14/17.
 */
public class Preferences {
	private static boolean sIsInitialized = false;
	private static SharedPreferences sPrefs;
	private static SharedPreferences.Editor sEditor;
	private static Preferences sInstance;


	private Preferences() { }

	public static void init(Activity activity) {
		if (!sIsInitialized && sInstance == null) {
			sPrefs = activity.getPreferences(Context.MODE_PRIVATE);
			sEditor = sPrefs.edit();
			sInstance = new Preferences();
			sIsInitialized = true;
		}
	}

	public static void finish() {
		// save uncommitted changes
		save();
		sIsInitialized = false;
		sInstance = null;
	}

	public static Preferences putString(Key key, String value) {
		sEditor.putString(key.name(), value);
		return sInstance;
	}

	public static Preferences putBoolean(Key key, boolean value) {
		sEditor.putBoolean(key.name(), value);
		return sInstance;
	}

	public static Preferences putInt(Key key, int value) {
		sEditor.putInt(key.name(), value);
		return sInstance;
	}

	/* Accessors */

	public static String getString(Key key) {
		return getString(key, "");
	}

	public static String getString(Key key, String defaultValue) {
		return sPrefs.getString(key.name(), defaultValue);
	}

	public static boolean getBoolean(Key key) {
		return getBoolean(key, true);
	}

	public static boolean getBoolean(Key key, boolean defaultValue) {
		return sPrefs.getBoolean(key.name(), defaultValue);
	}

	public static int getInt(Key key) {
		return getInt(key, 0);
	}

	public static int getInt(Key key, int defaultValue) {
		return sPrefs.getInt(key.name(), defaultValue);
	}

	public static void save() {
		sEditor.commit();
	}



	/* Preference Keys */
	public enum Key {
		SHAKE_ACTION,
		SHAKE_SENSITIVITY,
		SWIPE_ACTION,
		GALLERY_DIR,
        SCREENSHOT_FORMAT,
		SCREENSHOT_QUALITY
	}
}
