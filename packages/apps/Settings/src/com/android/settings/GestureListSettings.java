package com.android.settings;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.app.Activity;

public class GestureListSettings extends SettingsPreferenceFragment implements
		CompoundButton.OnCheckedChangeListener,OnPreferenceChangeListener{
	private static final String KEY_GESTURE_SETTINGS = "key_gesture_list_settings";
	private static final String KEY_GESTURE_UP = "key_gesture_up";
	private static final String KEY_GESTURE_DOWN = "key_gesture_down";
	private static final String KEY_GESTURE_LEFT = "key_gesture_left_right";
	private static final String KEY_GESTURE_C = "key_gesture_c";
	private static final String KEY_GESTURE_M = "key_gesture_m";
	private static final String KEY_GESTURE_E = "key_gesture_e";
	private static final String KEY_GESTURE_O = "key_gesture_o";
	private static final String KEY_GESTURE_W = "key_gesture_w";

	private PreferenceScreen mGestureSettingsPreference;
	private CheckBoxPreference mGestureUpPreference;
	private CheckBoxPreference mGestureDownPreference;
	private CheckBoxPreference mGestureLeftPreference;
	private CheckBoxPreference mGestureCPreference;
	private CheckBoxPreference mGestureMPreference;
	private CheckBoxPreference mGestureEPreference;
	private CheckBoxPreference mGestureOPreference;
	private CheckBoxPreference mGestureWPreference;
	
	private boolean mGestureUpEnable = true;
	private boolean mGestureDownEnable = true;
	private boolean mGestureLeftEnable = true;
	private boolean mGestureCEnable = true;
	private boolean mGestureMEnable = true;
	private boolean mGestureEEnable = true;
	private boolean mGestureOEnable = true;
	private boolean mGestureWEnable = true;
	
	private CheckBox mEnabledSwitch;
	private boolean mLastEnabledState;

	//public final String UI_PREFS_NAME = "gesture_uiinfo";
	//public static SharedPreferences mUiGestureAppInfo;
	private Activity mActivity;
	private final String PROPERTIES_GESTURE_POWERONOFF = "persist.sys.gestureonoff";
	
	private final static String PROPERTIES_GESTURE_UP = "persist.sys.gestureup";
	private final static String PROPERTIES_GESTURE_DOWN = "persist.sys.gesturedown";
	private final static String PROPERTIES_GESTURE_LEFT = "persist.sys.gestureleft";
	private final static String PROPERTIES_GESTURE_C = "persist.sys.gesturec";
	private final static String PROPERTIES_GESTURE_M = "persist.sys.gesturem";
	private final static String PROPERTIES_GESTURE_E = "persist.sys.gesturee";
	private final static String PROPERTIES_GESTURE_O = "persist.sys.gestureo";
	private final static String PROPERTIES_GESTURE_W = "persist.sys.gesturew";

	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.hct_gesture_unlock_setting);
		initScreen();
	}

	@Override
	public void onResume() {
		super.onResume();

//		boolean gestureonoff = SystemProperties
//				.getBoolean(PROPERTIES_GESTURE_POWERONOFF, true);
//		Utils.actionbarMade(getActivity());
//
//		View view = getActivity().getActionBar().getCustomView();
//		mEnabledSwitch = (CheckBox) view.findViewById(R.id.actionbar_switch);
//		mEnabledSwitch.setVisibility(View.VISIBLE);
//		mEnabledSwitch.setOnCheckedChangeListener(this);
//
//		if (gestureonoff) {
//			mEnabledSwitch.setChecked(true);
//			getPreferenceScreen().setEnabled(true);
//		} else {
//			mEnabledSwitch.setChecked(false);
//			getPreferenceScreen().setEnabled(false);
//		}
	}

	private void initScreen() {

		final PreferenceScreen parent = getPreferenceScreen();
		mGestureUpPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_UP);
		mGestureUpPreference.setOnPreferenceChangeListener(this);
		
		mGestureDownPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_DOWN);
		mGestureDownPreference.setOnPreferenceChangeListener(this);
		
		mGestureLeftPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_LEFT);
		mGestureLeftPreference.setOnPreferenceChangeListener(this);
		
		mGestureCPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_C);
		mGestureCPreference.setOnPreferenceChangeListener(this);
		
		mGestureMPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_M);
		mGestureMPreference.setOnPreferenceChangeListener(this);
		
		mGestureEPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_E);
		mGestureEPreference.setOnPreferenceChangeListener(this);
		
		mGestureOPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_O);
		mGestureOPreference.setOnPreferenceChangeListener(this);
		
		mGestureWPreference = (CheckBoxPreference) parent
				.findPreference(KEY_GESTURE_W);
		mGestureWPreference.setOnPreferenceChangeListener(this);

		mGestureUpEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_UP, true);
		mGestureDownEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_DOWN, true);
		mGestureLeftEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_LEFT, true);
		mGestureCEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_C, true);
		mGestureMEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_M, true);
		mGestureEEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_E, true);
		mGestureOEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_O, true);
		mGestureWEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_W, true);
		
		mGestureUpPreference.setChecked(mGestureUpEnable);
		mGestureDownPreference.setChecked(mGestureDownEnable);
		mGestureLeftPreference.setChecked(mGestureLeftEnable);
		mGestureCPreference.setChecked(mGestureCEnable);
		mGestureMPreference.setChecked(mGestureMEnable);
		mGestureEPreference.setChecked(mGestureEEnable);
		mGestureOPreference.setChecked(mGestureOEnable);
		mGestureWPreference.setChecked(mGestureWEnable);
		

	}

	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == mEnabledSwitch) {
			if (isChecked) {
				getPreferenceScreen().setEnabled(true);
				SystemProperties.set(PROPERTIES_GESTURE_POWERONOFF, "true");
			} else {
				getPreferenceScreen().setEnabled(false);
				SystemProperties.set(PROPERTIES_GESTURE_POWERONOFF, "false");
			}
//			SystemProperties.set(PROPERTIES_GESTURE_UP, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_DOWN, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_LEFT, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_C, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_M, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_E, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_O, isChecked ? "true" : "false");
//			SystemProperties.set(PROPERTIES_GESTURE_W, isChecked ? "true" : "false");
			
		}
	}

//	public static void setGestureItemOnOff(String key, boolean enable) {
//
//		if (key.equals(KEY_GESTURE_UP)) {
//			SystemProperties.set(PROPERTIES_GESTURE_UP, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_DOWN)) {
//			SystemProperties.set(PROPERTIES_GESTURE_DOWN, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_LEFT)) {
//			SystemProperties.set(PROPERTIES_GESTURE_LEFT, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_C)) {
//			SystemProperties.set(PROPERTIES_GESTURE_C, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_M)) {
//			SystemProperties.set(PROPERTIES_GESTURE_M, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_E)) {
//			SystemProperties.set(PROPERTIES_GESTURE_E, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_O)) {
//			SystemProperties.set(PROPERTIES_GESTURE_O, enable ? "true" : "false");
//		} else if (key.equals(KEY_GESTURE_W)) {
//			SystemProperties.set(PROPERTIES_GESTURE_W, enable ? "true" : "false");
//		}
//	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object enable) {
		if(mGestureUpPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_UP, (Boolean) enable ? "true" : "false");
		}else if(mGestureDownPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_DOWN, (Boolean) enable ? "true" : "false");
		}else if(mGestureLeftPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_LEFT, (Boolean) enable ? "true" : "false");
		}else if(mGestureCPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_C, (Boolean) enable ? "true" : "false");
		}else if(mGestureMPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_M, (Boolean) enable ? "true" : "false");
		}else if(mGestureEPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_E, (Boolean) enable ? "true" : "false");
		}else if(mGestureOPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_O, (Boolean) enable ? "true" : "false");
		}else if(mGestureWPreference == preference){
			SystemProperties.set(PROPERTIES_GESTURE_W, (Boolean) enable ? "true" : "false");
		}
		return true;
	}

}
