package com.android.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;

public class CommunicationsGestureSettings extends SettingsPreferenceFragment implements DialogInterface.OnClickListener, OnPreferenceChangeListener{
	private static final String TOGGLE_MMSDIAL_PREFERENCE = "toggle_mmsdial_preference";
	private static final String TOGGLE_AUTOANSWER_PREFERENCE = "toggle_autoanswer_preference";
	
	private CheckBoxPreference mToggleMmsDialPreference;
	private CheckBoxPreference mToggleAutoanswerPreference;
	
	private DialogInterface mWarnAutoanswer;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.communications_gesture_settings);
		mToggleMmsDialPreference = (CheckBoxPreference) findPreference(TOGGLE_MMSDIAL_PREFERENCE);
		mToggleMmsDialPreference.setChecked(android.os.SystemProperties
						.getBoolean("persist.sys.ng_autodial", false));
		mToggleMmsDialPreference.setOnPreferenceChangeListener(this);
		mToggleAutoanswerPreference = (CheckBoxPreference) findPreference(TOGGLE_AUTOANSWER_PREFERENCE);
		mToggleAutoanswerPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.ng_autoanswer", false));
		mToggleAutoanswerPreference.setOnPreferenceChangeListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWarnAutoanswer != null) {
			mWarnAutoanswer.dismiss();
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == mWarnAutoanswer
				&& which == DialogInterface.BUTTON_POSITIVE) {
			if (mToggleAutoanswerPreference != null) {
				mToggleAutoanswerPreference.setChecked(true);
				android.os.SystemProperties.set("persist.sys.ng_autoanswer",
						"true");
			}
		}
	}
	
	private void warnAutoanswer() {
		// TODO: DialogFragment?
		mWarnAutoanswer = new AlertDialog.Builder(getActivity())
				.setTitle(getResources().getString(R.string.error_title))
				.setIcon(com.android.internal.R.drawable.ic_dialog_alert)
				.setMessage(
						getResources().getString(
								R.string.open_auto_answer_warning))
				.setPositiveButton(android.R.string.yes, this)
				.setNegativeButton(android.R.string.no, null).show();
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference == mToggleMmsDialPreference){
			android.os.SystemProperties.set("persist.sys.ng_autodial",
					(Boolean)newValue ? "true" : "false");
			return true;
		}else if(preference == mToggleAutoanswerPreference){
//			if (getResources().getBoolean(R.bool.auto_answer_warning_enable)) {
//				if ((Boolean)newValue) {
//					mToggleAutoanswerPreference.setChecked(false);
//					warnAutoanswer();
//				} else {
//					android.os.SystemProperties.set(
//							"persist.sys.ng_autoanswer", "false");
//				}
//			} else {
				android.os.SystemProperties.set("persist.sys.ng_autoanswer",
						(Boolean)newValue ? "true" : "false");
			//}
			return true;
		}
		return false;
	}
}
