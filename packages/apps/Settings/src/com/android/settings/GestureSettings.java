package com.android.settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceChangeListener;

import android.util.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.mediatek.common.featureoption.FeatureOption;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

//import android.os.IFacedetectedService;
public class GestureSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
		//DialogInterface.OnClickListener, 

	private static final String TOGGLE_GALLERY_PREFERENCE = "toggle_gallery_preference";
	private static final String TOGGLE_MUSIC_PREFERENCE = "toggle_music_preference";
	private static final String TOGGLE_CAMERA_PREFERENCE = "toggle_camera_preference";
	private static final String TOGGLE_LAUNCHER_PREFERENCE = "toggle_launcher_preference";
	private static final String TOGGLE_UNLOCK_PREFERENCE = "toggle_unlock_preference";
	//private static final String TOGGLE_MMSDIAL_PREFERENCE = "toggle_mmsdial_preference";
	//private static final String TOGGLE_AUTOANSWER_PREFERENCE = "toggle_autoanswer_preference";
	// add by xuejin face detected
	//private static final String TOGGLE_SMARTREAD_PREFERENCE = "toggle_smartread_preference";
	// private static final String GESTURE_CATEGORY = "gesture_category";
	//private DialogInterface mWarnAutoanswer;
	private static final String TOGGLE_FACE_DECTOR_PREFERENCE = "toggle_face_dector_preference";

	// private PreferenceCategory mGestureCategory;

//	private static String TOGGLE_SCREENSHOT_PREFERENCE = "toggle_screenshot_preference";
//	private static String TOGGLE_VOLUME_PREFERENCE = "toggle_volume_preference";
	

	private CheckBoxPreference mToggleGalleryPreference;
	private CheckBoxPreference mToggleMusicPreference;
	private CheckBoxPreference mToggleCameraPreference;
	private CheckBoxPreference mToggleLauncherPreference;
	private CheckBoxPreference mToggleUnlockPreference;
	//private CheckBoxPreference mToggleMmsDialPreference;
	//private CheckBoxPreference mToggleAutoanswerPreference;
	//private CheckBoxPreference mToggleSmartreadPreference; // add by xuejin face
															// detected

	//private CheckBoxPreference mToggleFaceDectorPreference;// added by
															// wangshanlong

	//private CheckBoxPreference mToggleScreenshotPreference;
	
	//private CheckBoxPreference mToggleVolumePreference;

	//private DialogInterface mWarnAutoDial;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.gesture_settings);
		mToggleGalleryPreference = (CheckBoxPreference) findPreference(TOGGLE_GALLERY_PREFERENCE);
		mToggleGalleryPreference.setOnPreferenceChangeListener(this);
		mToggleMusicPreference = (CheckBoxPreference) findPreference(TOGGLE_MUSIC_PREFERENCE);
		mToggleMusicPreference.setOnPreferenceChangeListener(this);
		mToggleCameraPreference = (CheckBoxPreference) findPreference(TOGGLE_CAMERA_PREFERENCE);
		mToggleCameraPreference.setOnPreferenceChangeListener(this);
		mToggleLauncherPreference = (CheckBoxPreference) findPreference(TOGGLE_LAUNCHER_PREFERENCE);
		mToggleLauncherPreference.setOnPreferenceChangeListener(this);
		mToggleUnlockPreference = (CheckBoxPreference) findPreference(TOGGLE_UNLOCK_PREFERENCE);
		mToggleUnlockPreference.setOnPreferenceChangeListener(this);
		//mToggleMmsDialPreference = (CheckBoxPreference) findPreference(TOGGLE_MMSDIAL_PREFERENCE);
		//mToggleMmsDialPreference.setOnPreferenceChangeListener(this);
		//mToggleAutoanswerPreference = (CheckBoxPreference) findPreference(TOGGLE_AUTOANSWER_PREFERENCE);
		//mToggleAutoanswerPreference.setOnPreferenceChangeListener(this);
		// add by xuejin face detected
		//mToggleSmartreadPreference = (CheckBoxPreference) findPreference(TOGGLE_SMARTREAD_PREFERENCE);
		//mToggleSmartreadPreference.setOnPreferenceChangeListener(this);
		// end by xuejin face detected
		//mToggleFaceDectorPreference = (CheckBoxPreference) findPreference(TOGGLE_FACE_DECTOR_PREFERENCE);
		//mToggleFaceDectorPreference.setOnPreferenceChangeListener(this);

		//mToggleScreenshotPreference = (CheckBoxPreference) findPreference(TOGGLE_SCREENSHOT_PREFERENCE);
		//mToggleScreenshotPreference.setOnPreferenceChangeListener(this);
		
		//mToggleVolumePreference = (CheckBoxPreference) findPreference(TOGGLE_VOLUME_PREFERENCE);
		//mToggleVolumePreference.setOnPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
//		Utils.actionbarMade(getActivity());
		initAllPreferences();
	}

	private void initAllPreferences() {
		// mGestureCategory = (PreferenceCategory)
		// findPreference(GESTURE_CATEGORY);

		mToggleGalleryPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.ng_gallery", false));
		// add by wangxiaobo 2014.02.25(start)
		// if (FeatureOption.HCT_YIYUN_LAUNCHER_MUSIC_PAGE) {
		// mGestureCategory.removePreference(mToggleMusicPreference);
		// }else{
		mToggleMusicPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.ng_music", false));
		// }
		// add by wangxiaobo 2014.02.25(end)
		mToggleCameraPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.ng_camera", false));
		mToggleLauncherPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.ng_launcher", false));
		mToggleUnlockPreference.setChecked(android.os.SystemProperties
				.getBoolean("persist.sys.ng_keyguard", false));
		//mToggleMmsDialPreference.setChecked(android.os.SystemProperties
		//		.getBoolean("persist.sys.ng_autodial", false));
		//mToggleAutoanswerPreference.setChecked(android.os.SystemProperties
		//		.getBoolean("persist.sys.ng_autoanswer", false));
		// add by xuejin face detected
		// if(!FeatureOption.HCT_FACE_RECOGNITION){
		/*
		PreferenceCategory smartPreferenceCategory = (PreferenceCategory) findPreference("smartmode_category");
		if (smartPreferenceCategory != null
				&& mToggleSmartreadPreference != null) {
			// getPreferenceScreen().removePreference(smartPreferenceCategory);
			smartPreferenceCategory
					.removePreference(mToggleSmartreadPreference);
		}
		*/
		// }else{
		// mToggleSmartreadPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.facedetected_on",false));
		// }
		// end by xuejin face detected
		/*
		if (getResources().getBoolean(
				R.bool.config_remove_face_dector_preference)
				&& smartPreferenceCategory != null) {
			// && mToggleSmartreadPreference != null) {
			// ((PreferenceCategory) findPreference("smartmode_category"))
			// .removePreference(mToggleSmartreadPreference);
			getPreferenceScreen().removePreference(smartPreferenceCategory);
		} else {
			if (getResources()
					.getBoolean(R.bool.config_remove_smart_read_force)
					&& mToggleSmartreadPreference != null) {
				((PreferenceCategory) findPreference("smartmode_category"))
						.removePreference(mToggleSmartreadPreference);
			} else {
				mToggleSmartreadPreference
						.setChecked(android.os.SystemProperties.getBoolean(
								"persist.sys.facedetected_on", false));
			}
			if (getResources().getBoolean(
					R.bool.config_remove_face_dector_preference)
					&& mToggleFaceDectorPreference != null) {
				((PreferenceCategory) findPreference("smartmode_category"))
						.removePreference(mToggleFaceDectorPreference);
			} else {
				mToggleFaceDectorPreference
						.setChecked(android.os.SystemProperties.getBoolean(
								"persist.sys.face", false));
			}
			
		}*/

		//mToggleScreenshotPreference.setChecked(android.os.SystemProperties
		//		.getBoolean("persist.sys.fingerscreenshot", false));
		//mToggleVolumePreference.setChecked(android.os.SystemProperties
		//		.getBoolean("persist.sys.fingersvolume", false));

	}

	/*
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
	*/
	
	/*
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
	*/

	@Override
	public void onDestroy() {
		super.onDestroy();
		/*
		if (mWarnAutoanswer != null) {
			mWarnAutoanswer.dismiss();
		}
		*/
	}

	private boolean handlePreference(Preference preference, boolean newValue) {
		if (mToggleGalleryPreference == preference) {
			android.os.SystemProperties.set("persist.sys.ng_gallery",
					newValue ? "true" : "false");
			return true;
		} else if (mToggleMusicPreference == preference) {
			android.os.SystemProperties.set("persist.sys.ng_music",
					newValue ? "true" : "false");
			return true;
		} else if (mToggleCameraPreference == preference) {
			android.os.SystemProperties.set("persist.sys.ng_camera",
					newValue ? "true" : "false");
			return true;
		} else if (mToggleLauncherPreference == preference) {
			android.os.SystemProperties.set("persist.sys.ng_launcher",
					newValue ? "true" : "false");
			return true;
		} else if (mToggleUnlockPreference == preference) {
			android.os.SystemProperties.set("persist.sys.ng_keyguard",
					newValue ? "true" : "false");
			return true;
		} /*else if (mToggleMmsDialPreference == preference) {
			android.os.SystemProperties.set("persist.sys.ng_autodial",
					newValue ? "true" : "false");
			return true;
		} else if (mToggleAutoanswerPreference == preference) {
			if (getResources().getBoolean(R.bool.auto_answer_warning_enable)) {
				if (mToggleAutoanswerPreference.isChecked()) {
					mToggleAutoanswerPreference.setChecked(false);
					warnAutoanswer();
				} else {
					android.os.SystemProperties.set(
							"persist.sys.ng_autoanswer", "false");
				}
			} else {
				android.os.SystemProperties.set("persist.sys.ng_autoanswer",
						newValue ? "true" : "false");
			}
			return true;
		}
		*/
		// add by xuejin face detected
		/*
		else if (mToggleSmartreadPreference == preference) {
			android.os.SystemProperties.set("persist.sys.facedetected_on",
					newValue ? "true" : "false");
			IBinder faceBinder = ServiceManager.getService("facedetected");
			// IFacedetectedService iFacedetectedService =
			// IFacedetectedService.Stub.asInterface(faceBinder);
			// try {
			// iFacedetectedService.end();
			// } catch (RemoteException e) {
			// Log.e(TAG, "Facedetected service is unavailable for queries");
			// }
			return true;
		}
		// end by xuejin face detected
		/*
		else if (mToggleFaceDectorPreference == preference) {
			android.os.SystemProperties.set("persist.sys.face",
					newValue ? "true" : "false");
		}else if(mToggleScreenshotPreference == preference){
			android.os.SystemProperties.set("persist.sys.fingerscreenshot",
					newValue ? "true" : "false");
		}else if(mToggleVolumePreference == preference){
			android.os.SystemProperties.set("persist.sys.fingersvolume",
					newValue ? "true" : "false");
		}*/

		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		handlePreference(preference, (Boolean) newValue);
		return true;
	}

	// @Override
	// public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
	// Preference preference) {
	// boolean ret = handlePreference(preferenceScreen, preference);
	// if (!ret) {
	// ret = super.onPreferenceTreeClick(preferenceScreen, preference);
	// }
	// return ret;
	// }

}
