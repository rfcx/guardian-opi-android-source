package com.android.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class SystemGestureSettings extends SettingsPreferenceFragment implements
		OnPreferenceChangeListener {

	private static String TOGGLE_SCREENSHOT_PREFERENCE = "toggle_screenshot_preference";
	private static String TOGGLE_VOLUME_PREFERENCE = "toggle_volume_preference";
	private static String TOGGLE_DOUBLE_CLICK_PREFERENCE = "toggle_double_click_preference";

	private CheckBoxPreference mToggleScreenshotPreference;
	private CheckBoxPreference mToggleVolumePreference;
	private CheckBoxPreference mToggleDoubleClickPreference;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.system_gesture_settings);
		mToggleScreenshotPreference = (CheckBoxPreference) findPreference(TOGGLE_SCREENSHOT_PREFERENCE);
		mToggleScreenshotPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.fingerscreenshot", false));

		mToggleScreenshotPreference.setOnPreferenceChangeListener(this);

		mToggleVolumePreference = (CheckBoxPreference) findPreference(TOGGLE_VOLUME_PREFERENCE);
		mToggleVolumePreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.fingersvolume", false));
		mToggleVolumePreference.setOnPreferenceChangeListener(this);

		mToggleDoubleClickPreference = (CheckBoxPreference) findPreference(TOGGLE_DOUBLE_CLICK_PREFERENCE);
		mToggleDoubleClickPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.double-click-home", false));
		mToggleDoubleClickPreference.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (mToggleScreenshotPreference == preference) {
			android.os.SystemProperties.set("persist.sys.fingerscreenshot",
					(Boolean) newValue ? "true" : "false");
			return true;
		} else if (mToggleVolumePreference == preference) {
			android.os.SystemProperties.set("persist.sys.fingersvolume",
					(Boolean) newValue ? "true" : "false");
			return true;
		} else if (mToggleDoubleClickPreference == preference) {
			android.os.SystemProperties.set("persist.sys.double-click-home",
					(Boolean) newValue ? "true" : "false");
			return true;
		}
		return false;
	}
}