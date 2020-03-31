package com.android.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;

import com.android.settings.R;

public class ActivityCopyFile extends Activity {
	private final String ACTION_WALLPAPER_LOCKSCREEN_PATH = "cn.sh.hct.wallpaperchooser.LOCKSCREEN_PATH";
	private final File mFileTo = new File(Environment.getDataDirectory() + "/lock.png");

	final int mSize = 1024;

	MyHandler mHandler;

	private final int RESULT_OK = 0x1;
	private final int RESULT_FAIL = 0x2;

	ProgressDialog mProgressDialog;

	private int mScreenWidth, mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mHandler = new MyHandler();

		mProgressDialog = ProgressDialog.show(this, getString(R.string.hct_lockscreen_title), getString(R.string.hct_lockscreen_setting));

		final String fileFrom = getIntent().getStringExtra(ACTION_WALLPAPER_LOCKSCREEN_PATH);

		if (null == fileFrom) {
			return;
		}

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();

		android.os.SystemProperties.set("persist.sys.lswflag", "true");

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				Message msg = new Message();

				if (mFileTo.exists()) {
					mFileTo.delete();
				}

				try {
					if (mFileTo.createNewFile()) {
						int byteread = 0;

						mFileTo.setWritable(true, false);
						mFileTo.setReadable(true, false);

						Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						Bitmap bitmap = BitmapFactory.decodeFile(fileFrom, opts);
						int width = opts.outWidth;
						int height = opts.outHeight;

						if (width > mScreenWidth || height > mScreenHeight) {
							opts.inJustDecodeBounds = false;
							bitmap = BitmapFactory.decodeFile(fileFrom);

							Bitmap dstBitmap = ThumbnailUtils.extractThumbnail(bitmap, mScreenWidth, mScreenHeight);

							FileOutputStream fOut = new FileOutputStream(mFileTo);
							dstBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

							fOut.flush();
							fOut.close();
						} else {
							InputStream streamFrom = new FileInputStream(fileFrom);
							FileOutputStream streamTo = new FileOutputStream(mFileTo);
							byte[] buffer = new byte[mSize];

							while ((byteread = streamFrom.read(buffer)) != -1) {
								streamTo.write(buffer, 0, byteread);
							}

							streamFrom.close();
							streamTo.close();
						}
					}

					msg.arg1 = RESULT_OK;
				} catch (IOException e) {
					msg.arg1 = RESULT_FAIL;

					e.printStackTrace();
				}

				mHandler.sendMessage(msg);
			}
		}.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.arg1) {
				case RESULT_OK: {

					mProgressDialog.hide();
					break;
				}

				default:
				case RESULT_FAIL: {
					break;
				}
			}

			finish();
		}
	}
}
