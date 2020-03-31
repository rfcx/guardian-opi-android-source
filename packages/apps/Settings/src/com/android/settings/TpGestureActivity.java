package com.android.settings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.os.SystemClock;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.hardware.input.InputManager;
import android.view.InputDevice;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

public class TpGestureActivity extends Activity {
	protected static final int MSG_FINISH = 0;
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_FINISH:
//		    	long mDownTime = SystemClock.uptimeMillis();
//				sendEvent(KeyEvent.ACTION_DOWN, 0, mDownTime);
//				sendEvent(KeyEvent.ACTION_UP, 0, mDownTime+100);
				PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
				pm.wakeUp(SystemClock.uptimeMillis());
				pm.goToSleep(SystemClock.uptimeMillis());
				finish();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String packageName = intent.getStringExtra("packageName");
		String className = intent.getStringExtra("className");
		String musicAction = intent.getStringExtra("music_action");
		Log.e("TpGestureActivity","packageName = "+packageName+" className = "+className);
		if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className) && Utils.isApkExist(this, packageName)) {
			Intent tpgestureIntent = new Intent(Intent.ACTION_MAIN);
			tpgestureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			tpgestureIntent.setClassName(packageName, className);
			startActivity(tpgestureIntent);
			this.finish();
		}else if(!TextUtils.isEmpty(musicAction)){
			startMusicService(musicAction);
		}
		
	}
	
	
	  private void startMusicService(String action){
	    	Intent intent = new Intent();
	    	intent.setAction("com.android.music.MediaPlaybackService");
	    	intent.putExtra("command", action);
	    	startService(intent);
	    	mHandler.sendEmptyMessageAtTime(0, 1000);
	  }
	
	void sendEvent(int action, int flags, long when) {
		final int repeatCount = (flags & KeyEvent.FLAG_LONG_PRESS) != 0 ? 1 : 0;
		final KeyEvent ev = new KeyEvent(when, when, action, KeyEvent.KEYCODE_POWER, repeatCount, 0,
		        KeyCharacterMap.VIRTUAL_KEYBOARD, 0, flags | KeyEvent.FLAG_FROM_SYSTEM
		                | KeyEvent.FLAG_VIRTUAL_HARD_KEY, InputDevice.SOURCE_KEYBOARD);
		boolean flag = InputManager.getInstance().injectInputEvent(ev,
		        InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        }
}
