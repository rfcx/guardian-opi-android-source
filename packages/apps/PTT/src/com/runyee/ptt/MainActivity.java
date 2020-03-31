package com.runyee.ptt;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.media.AudioSystem;
import com.android.internal.telephony.ITelephony;
import android.os.ServiceManager;

import com.runyee.ptt.channellist.ChannelListFragment;
import com.runyee.ptt.help.HelpFragment;
import com.runyee.ptt.settings.SettingsFragment;
import com.runyee.ptt.txrx.TxRxFragment;
import com.runyee.ptt.Utils;

public class MainActivity extends FragmentActivity implements
		OnCheckedChangeListener {

	private RadioGroup rg_tabMenu;
	public static RadioButton rb_txrx;

	private TxRxFragment mTxRxFragment;
	private SettingsFragment mSettingsFragment;
	private ChannelListFragment mChannelListFragment;
	private HelpFragment mHelpFragment;

	static String result = null;
	static int status;

	private AudioManager am;
	public static NotificationManager nm;

	// private CustomDialog customDialog;
	private ImageView volume1, volume2, volume3, volume4, volume5, volume6,
			volume7;
	private ImageView[] imageView = { volume1, volume2, volume3, volume4,
			volume5, volume6, volume7 };
	private int[] imageViewId = { R.id.volume1, R.id.volume2, R.id.volume3,
			R.id.volume4, R.id.volume5, R.id.volume6, R.id.volume7 };

	private int volume_val = Utils.readIntFromSharePreference("volume", 6);
	File device = new File("/dev/ttyMT1");
	private int baudrate = 9600;
	private StringBuilder AT_DMOSETGROUP_VOLUME;
	private int volume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initViews();
		showNotification();
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void initViews() {
		rg_tabMenu = (RadioGroup) findViewById(R.id.tab_menu);
		rg_tabMenu.setOnCheckedChangeListener(this);

		rb_txrx = (RadioButton) findViewById(R.id.rb_txRx);

		// set defaul fragment
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
		mTxRxFragment = new TxRxFragment();
		ft.replace(R.id.main_content, mTxRxFragment);
		ft.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (isMusicActive() || isFmActive()) {
			GPIO.openGPIO("138");
		} else {
			GPIO.closeGPIO("138");
		}
		GPIO.closeGPIO("136");
		GPIO.openGPIO("25");
		GPIO.closeGPIO("23");
		GPIO.modifiedGPIO("101:0 0 0 0 0 1 0");
		GPIO.modifiedGPIO("102:0 0 0 0 0 1 0");
		if (nm != null) {
			nm.cancel(R.string.app_name);
		}
		// android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
		switch (checkedId) {
		case R.id.rb_txRx:
			if (mTxRxFragment == null) {
				mTxRxFragment = new TxRxFragment();
			}
			/*
			 * if (!mTxRxFragment.isAdded()) { ft.add(R.id.main_content,
			 * mTxRxFragment); ft.show(mTxRxFragment); if (mSettingsFragment !=
			 * null) { ft.hide(mSettingsFragment); } if (mChannelListFragment !=
			 * null) { ft.hide(mChannelListFragment); } if (mHelpFragment !=
			 * null) { ft.hide(mHelpFragment); } } else if
			 * (mTxRxFragment.isAdded()) { if (mSettingsFragment != null) {
			 * ft.hide(mSettingsFragment); } if (mChannelListFragment != null) {
			 * ft.hide(mChannelListFragment); } if (mHelpFragment != null) {
			 * ft.hide(mHelpFragment); } ft.show(mTxRxFragment); }
			 */
			ft.replace(R.id.main_content, mTxRxFragment);
			break;
		case R.id.rb_settings:
			if (mSettingsFragment == null) {
				mSettingsFragment = new SettingsFragment();
			}
			/*
			 * if (!mSettingsFragment.isAdded()) { ft.add(R.id.main_content,
			 * mSettingsFragment); ft.show(mSettingsFragment); if (mTxRxFragment
			 * != null) { ft.hide(mTxRxFragment); } if (mChannelListFragment !=
			 * null) { ft.hide(mChannelListFragment); } if (mHelpFragment !=
			 * null) { ft.hide(mHelpFragment); } } else if
			 * (mSettingsFragment.isAdded()) { ft.show(mSettingsFragment); if
			 * (mTxRxFragment != null) { ft.hide(mTxRxFragment); } if
			 * (mChannelListFragment != null) { ft.hide(mChannelListFragment); }
			 * if (mHelpFragment != null) { ft.hide(mHelpFragment); } }
			 */
			ft.replace(R.id.main_content, mSettingsFragment);
			break;
		case R.id.rb_channelList:
			if (mChannelListFragment == null) {
				mChannelListFragment = new ChannelListFragment();
			}
			/*
			 * if (!mChannelListFragment.isAdded()) { ft.add(R.id.main_content,
			 * mChannelListFragment); ft.show(mChannelListFragment); if
			 * (mTxRxFragment != null) { ft.hide(mTxRxFragment); } if
			 * (mSettingsFragment != null) { ft.hide(mSettingsFragment); } if
			 * (mHelpFragment != null) { ft.hide(mHelpFragment); } } else if
			 * (mChannelListFragment.isAdded()) { ft.show(mChannelListFragment);
			 * if (mTxRxFragment != null) { ft.hide(mTxRxFragment); } if
			 * (mSettingsFragment != null) { ft.hide(mSettingsFragment); } if
			 * (mHelpFragment != null) { ft.hide(mHelpFragment); } }
			 */
			ft.replace(R.id.main_content, mChannelListFragment);
			break;
		case R.id.rb_help:
			if (mHelpFragment == null) {
				mHelpFragment = new HelpFragment();
			}
			/*
			 * if (!mHelpFragment.isAdded()) { ft.add(R.id.main_content,
			 * mHelpFragment); ft.show(mHelpFragment); if (mTxRxFragment !=
			 * null) { ft.hide(mTxRxFragment); } if (mSettingsFragment != null)
			 * { ft.hide(mSettingsFragment); } if (mChannelListFragment != null)
			 * { ft.hide(mChannelListFragment); } } else if
			 * (mHelpFragment.isAdded()) { if (mTxRxFragment != null) {
			 * ft.hide(mTxRxFragment); } if (mSettingsFragment != null) {
			 * ft.hide(mSettingsFragment); } if (mChannelListFragment != null) {
			 * ft.hide(mChannelListFragment); } ft.show(mHelpFragment); }
			 */
			ft.replace(R.id.main_content, mHelpFragment);
			break;

		}
		ft.commit();
	}

	int i, j = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//android.util.Log.d("lwj", "keyCode:" + keyCode);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle(R.string.dialog_exit_title);
			builder.setMessage(R.string.dialog_exit_content);
			builder.setPositiveButton(R.string.dialog_exit_button_ok,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							if (isMusicActive() || isFmActive()) {
								GPIO.openGPIO("138");
							} else {
								GPIO.closeGPIO("138");
							}
							GPIO.closeGPIO("136");
							GPIO.openGPIO("25");
							GPIO.closeGPIO("23");
							GPIO.modifiedGPIO("101:0 0 0 0 0 1 0");
							GPIO.modifiedGPIO("102:0 0 0 0 0 1 0");
							if (nm != null) {
								nm.cancel(R.string.app_name);
							}
							// android.os.Process.killProcess(android.os.Process
							// .myPid());
							System.exit(0);
						}
					});

			builder.setNegativeButton(R.string.dialog_exit_button_cancel,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					});
			builder.create().show();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_F10) {
			++volume_val;
			if (volume_val <= 0) {
				volume_val = 0;
			} else if (volume_val >= 7) {
				volume_val = 7;
			}
			android.util.Log.d("lwj111", "KEYCODE_VOLUME_UP" + volume_val);
			// 音量界面
			/*
			 * final CustomDialog customDialog = new
			 * CustomDialog(MainActivity.this, R.style.ThemeNoDisplay,
			 * R.layout.volume); customDialog.show();
			 * 
			 * for (int i = 0; i < 7; i++) { imageView[i] = (ImageView)
			 * customDialog .findViewById(imageViewId[i]);
			 * //imageView[i].setVisibility(View.VISIBLE); }
			 * 
			 * for (int i = 0; i <= volume_val; i++) {
			 * imageView[i].setVisibility(View.VISIBLE); for (int k = volume_val
			 * + 1; k < 7; k++) { imageView[i].setVisibility(View.GONE); } } new
			 * android.os.Handler().postDelayed(new Runnable() {
			 * 
			 * @Override public void run() { customDialog.dismiss(); } }, 1000);
			 */
			Utils.writeIntToSharePreference("volume", volume_val + 1);
			volume = Utils.readIntFromSharePreference("volume", 8);
			TxRxFragment.tv_volume.setText(String.valueOf(volume));
			writeVolume();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_F11) {
			--volume_val;
			if (volume_val <= 0) {
				volume_val = 0;
			} else if (volume_val >= 7) {
				volume_val = 7;
			}
			android.util.Log.d("lwj111", "KEYCODE_VOLUME_DOWN" + volume_val);
			// 音量界面
			/*
			 * final CustomDialog customDialog = new
			 * CustomDialog(MainActivity.this, R.style.ThemeNoDisplay,
			 * R.layout.volume); customDialog.show();
			 * 
			 * for (int i = 0; i < 7; i++) { imageView[i] = (ImageView)
			 * customDialog .findViewById(imageViewId[i]);
			 * //imageView[i].setVisibility(View.GONE); }
			 * 
			 * for (int i = 0; i <= volume_val; i++) {
			 * imageView[i].setVisibility(View.VISIBLE); for (int k = volume_val
			 * + 1; k < 7; k++) { imageView[i].setVisibility(View.GONE); } } new
			 * android.os.Handler().postDelayed(new Runnable() {
			 * 
			 * @Override public void run() { customDialog.dismiss(); } }, 1000);
			 */
			Utils.writeIntToSharePreference("volume", volume_val + 1);
			volume = Utils.readIntFromSharePreference("volume", 8);
			TxRxFragment.tv_volume.setText(String.valueOf(volume));
			writeVolume();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	boolean isMusicActive() {
		if (MyApplication.getContextObject() != null) {
			am = (AudioManager) getApplicationContext().getSystemService(
					Context.AUDIO_SERVICE);
		}
		if (am == null) {
			return false;
		}
		return (phoneIsInUse()
				|| AudioSystem.isStreamActive(AudioSystem.STREAM_MUSIC, 0)
				|| AudioSystem.isStreamActive(AudioSystem.STREAM_RING, 0)
				|| AudioSystem.isStreamActive(AudioSystem.STREAM_ALARM, 0)
				|| AudioSystem.isStreamActive(AudioSystem.STREAM_VOICE_CALL, 0)
				|| AudioSystem.isStreamActive(AudioSystem.STREAM_SYSTEM, 0)
				|| AudioSystem.isStreamActive(AudioSystem.STREAM_NOTIFICATION,
						0) || AudioSystem.isStreamActive(AudioSystem.STREAM_FM,
				0));
	}

	private boolean phoneIsInUse() {
		boolean phoneInUse = false;
		try {
			ITelephony phone = ITelephony.Stub.asInterface(ServiceManager
					.checkService("phone"));
			if (phone != null)
				phoneInUse = !phone.isIdle();
		} catch (RemoteException e) {

		}
		return phoneInUse;
	}

	boolean isFmActive() {
		if (MyApplication.getContextObject() != null) {
			am = (AudioManager) getApplicationContext().getSystemService(
					Context.AUDIO_SERVICE);
		}
		if (am == null) {
			return false;
		}
		return am.isFmActive();
	}

	protected void showNotification() {
		CharSequence from = "PTT";
		CharSequence message = "PTT Still run background!";

		Intent intent = new Intent();
		ComponentName componentName = new ComponentName("com.runyee.ptt",
				"com.runyee.ptt.MainActivity");
		intent.setComponent(componentName);
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		// construct the Notification object.
		Notification notif = new Notification(R.drawable.ic_launcher_ptt,
				"PTT start up", System.currentTimeMillis());
		notif.setLatestEventInfo(this, from, message, contentIntent);

		// look up the notification manager service
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(R.string.app_name, notif);
	}

	private void writeVolume() {
		AT_DMOSETGROUP_VOLUME = new StringBuilder(100);
		AT_DMOSETGROUP_VOLUME.append("AT");
		AT_DMOSETGROUP_VOLUME.append("+");
		AT_DMOSETGROUP_VOLUME.append("DMOSETVOLUME");
		AT_DMOSETGROUP_VOLUME.append("=");
		AT_DMOSETGROUP_VOLUME.append(String.valueOf(volume));
		AT_DMOSETGROUP_VOLUME.append("\r\n");

		new android.os.Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					TxRxFragment.openSerialPort(device, baudrate);
					TxRxFragment.writeDataToSerilPort(String
							.valueOf(AT_DMOSETGROUP_VOLUME));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 1000);

		try {
			// TxRxFragment.openSerialPort(device, baudrate);
			TxRxFragment.readDataFromSerilPort();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
