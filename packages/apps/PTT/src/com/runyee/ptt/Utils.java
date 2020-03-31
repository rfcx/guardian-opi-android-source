package com.runyee.ptt;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
	static SharedPreferences sp;
	static SharedPreferences.Editor editor;

	public static void writeStringToSharePreference(String key, String value) {
		sp = MyApplication.getContextObject().getSharedPreferences("ptt_data",
				Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void writeIntToSharePreference(String key, int value) {
		sp = MyApplication.getContextObject().getSharedPreferences("ptt_data",
				Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static String readStringFromSharePreference(String key, String value) {
		sp = MyApplication.getContextObject().getSharedPreferences("ptt_data",
				Context.MODE_WORLD_READABLE);
		String val = sp.getString(key, value);
		return val;
	}

	public static int readIntFromSharePreference(String key, int value) {
		sp = MyApplication.getContextObject().getSharedPreferences("ptt_data",
				Context.MODE_WORLD_READABLE);
		int val = sp.getInt(key, value);
		return val;
	}

}