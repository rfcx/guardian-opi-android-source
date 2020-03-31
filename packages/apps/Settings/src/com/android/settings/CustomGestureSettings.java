package com.android.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;

public class CustomGestureSettings extends SettingsPreferenceFragment implements
		OnPreferenceChangeListener {

	private static final String KEY_GESTURE_CATEGORY = "gesture_category";
	private static final String KEY_OTHER_GESTURE_CATEGORY = "other_gesture_category";
	private static final String TOGGLE_FACE_DECTOR_PREFERENCE = "toggle_face_dector_preference";

	private PreferenceCategory gestureCategory;
	private PreferenceCategory systemGestureCategory;
	private CheckBoxPreference mToggleFaceDectorPreference;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.custom_gesture_settings);
		gestureCategory = (PreferenceCategory) findPreference(KEY_GESTURE_CATEGORY);
		systemGestureCategory = (PreferenceCategory) findPreference(KEY_OTHER_GESTURE_CATEGORY);
		
		
		mToggleFaceDectorPreference = (CheckBoxPreference) findPreference(TOGGLE_FACE_DECTOR_PREFERENCE);
		if (getResources().getBoolean(
				R.bool.config_remove_face_dector_preference)
				&& mToggleFaceDectorPreference != null) {
			systemGestureCategory.removePreference(mToggleFaceDectorPreference);
		} else {
			mToggleFaceDectorPreference.setChecked(android.os.SystemProperties
					.getBoolean("persist.sys.face", false));
			mToggleFaceDectorPreference.setOnPreferenceChangeListener(this);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		Utils.actionbarMade(getActivity());
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (mToggleFaceDectorPreference == preference) {
			android.os.SystemProperties.set("persist.sys.face",
					(Boolean)newValue ? "true" : "false");
		}
		return true;
	}
}
