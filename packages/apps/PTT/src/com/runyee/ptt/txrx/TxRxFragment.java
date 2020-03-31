package com.runyee.ptt.txrx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runyee.ptt.GPIO;
import com.runyee.ptt.R;
import com.runyee.ptt.SerialPort;
import com.runyee.ptt.Utils;

import android.support.v4.content.LocalBroadcastManager;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

import com.runyee.ptt.MyApplication;
import com.runyee.ptt.MainActivity;
import android.media.AudioSystem;
import android.view.Gravity;

import com.android.internal.telephony.ITelephony;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.app.Service;

public class TxRxFragment extends Fragment implements OnClickListener {

	private TextView tv_pow;
	private TextView tv_gbw;
	private TextView tv_sq;
	private TextView tv_ctdcss;
	public static TextView tv_volume;
	private TextView tv_tx;
	public static TextView tv_rx;
	private ImageView iv_plink;
	private Button btn_send;

	private String val_pow;
	private String val_gbw;
	private int index_pow;
	private int index_gbw;
	private int val_sq;
	private int val_ctdcss_tx;
	private int val_ctdcss_rx;
	private String val_ctdcss;
	private String val_tx;
	private String val_rx;

	File device = new File("/dev/ttyMT1");
	int def_baudrate = 9600;
	String data = "send";

	String val_AT_DMOSETGROUP;
	int sampRate = 8000;
	private AudioRecord recorder;
	private AudioTrack player;

	AudioReceiveThread receiveThread;
	AudioSendThread sendThread;
	private boolean isStopTalk = true;
	public static boolean isFirstRunReceive = true;

	Intent intent;
	Bundle bundle;
	int flag;
	public static int flag_;
	public static int position;
	String id;
	String name;
	String info;
	String infoTX;
	String infoRX;
	String infoSQ;
	String infoCTCSS;
	public static boolean enable;

	private TalkReceiver talkReceiver;
	LocalBroadcastManager mBroadcastManager;
	boolean status_ear;

	private AudioManager am;
	private AudioManager localAudioManager;

	public static boolean flag_fatal = false;

	public static String dataReceived;
	private Toast toast;

	// private String array_ctdcss[] = { "0KHz", "1KHz", "2KHz", "3KHz", "4KHz",
	// "5KHz", "6KHz", "7KHz" };
	private boolean mIncomingFlag;
	private String mIncomingNumber;

	public class TalkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			android.util.Log.d("lwj", "action=" + action);
			if ("com.runyee.ptt.talk.down".equals(action)) {
				iv_plink.setVisibility(View.VISIBLE);
				//setFlickerAnimation(iv_plink);
				isStopTalk = false;
				// GPIO.closeGPIO("25");

				stopTrackReceive();
				// startRecordSend();
				new recordSendTask().execute();
			} else if ("com.runyee.ptt.talk.up".equals(action)) {
				iv_plink.setVisibility(View.GONE);
				// GPIO.openGPIO("25");
				isStopTalk = true;
				// stopRecordSend();
				// startTrackReceive();
				new trackReceiveTask().execute();
			} else if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
				status_ear = intent.getIntExtra("state", 0) == 1;
				android.util.Log.d("lwj", "status_ear:" + status_ear);
			}
			// 来电去电处理
			else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				// 去电
				mIncomingFlag = false;
				String phoneNumber = intent
						.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.i("lwj", "call OUT:" + phoneNumber);
				GPIO.closeGPIO("136");
				GPIO.openGPIO("25");
				GPIO.modifiedGPIO("101:0 0 0 0 0 1 0");
				GPIO.modifiedGPIO("102:0 0 0 0 0 1 0");
			} else if (action
					.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
				// 来电
				TelephonyManager tManager = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				/*
				 * switch (tManager.getCallState()) {
				 * 
				 * case TelephonyManager.CALL_STATE_RINGING: mIncomingNumber =
				 * intent.getStringExtra("incoming_number"); Log.i("lwj",
				 * "RINGING :" + mIncomingNumber);
				 * GPIO.modifiedGPIO("101:1 1 1 0 1 0 0");
				 * GPIO.modifiedGPIO("102:1 0 1 1 0 1 0"); GPIO.openGPIO("136");
				 * GPIO.openGPIO("25"); break; case
				 * TelephonyManager.CALL_STATE_OFFHOOK: if (mIncomingFlag) {
				 * Log.i("lwj", "incoming ACCEPT :" + mIncomingNumber); }
				 * GPIO.modifiedGPIO("101:1 1 1 0 1 0 0");
				 * GPIO.modifiedGPIO("102:1 0 1 1 0 1 0"); GPIO.openGPIO("136");
				 * GPIO.openGPIO("25"); new android.os.Handler().postDelayed(new
				 * Runnable() {
				 * 
				 * @Override public void run() { try { openSerialPort(device,
				 * 9600); writeDataToSerilPort(val_AT_DMOSETGROUP); } catch
				 * (IOException e) { e.printStackTrace(); } } }, 1000); break;
				 * case TelephonyManager.CALL_STATE_IDLE: if (mIncomingFlag) {
				 * Log.i("lwj", "incoming IDLE"); } GPIO.closeGPIO("136");
				 * GPIO.openGPIO("25"); GPIO.modifiedGPIO("101:0 0 0 0 0 1 0");
				 * GPIO.modifiedGPIO("102:0 0 0 0 0 1 0"); break;
				 */
				if (phoneIsInUse()) {
					GPIO.closeGPIO("136");
					GPIO.openGPIO("25");
					GPIO.modifiedGPIO("101:0 0 0 0 0 1 0");
					GPIO.modifiedGPIO("102:0 0 0 0 0 1 0");
				} else if (!phoneIsInUse()) {
					GPIO.modifiedGPIO("101:1 1 1 0 1 0 0");
					GPIO.modifiedGPIO("102:1 0 1 1 0 1 0");
					GPIO.openGPIO("136");
					GPIO.openGPIO("25");
					new android.os.Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							try {
								openSerialPort(device, 9600);
								writeDataToSerilPort(val_AT_DMOSETGROUP);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}, 1000);
				}
			}
			// 来电去电处理
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Log.d("lwj", "TxRxFragment-->onCreateView-->flag_:" + flag_);

		intent = getActivity().getIntent();
		bundle = intent.getBundleExtra("tag_enable");
		if (flag_ == 1) {
			if (bundle != null) {
				flag = bundle.getInt("flag");
				position = bundle.getInt("position");
				id = bundle.getString("id");
				enable = bundle.getBoolean("enable", false);
				name = bundle.getString("name");
				info = bundle.getString("info");

				infoTX = bundle.getString("infotx");
				infoRX = bundle.getString("inforx");
				infoSQ = bundle.getString("infosq");
				infoCTCSS = bundle.getString("infoctcss");
			}
		}
		Log.d("lwj1", "TxRxFragment" + " id:" + id + " enable:" + enable
				+ " name:" + name + " infoTX:" + infoTX + " infoRX:" + infoRX
				+ " infoSQ:" + infoSQ + " infoCTCSS:" + infoCTCSS);

		readDateFromSharePreference();

		View view = inflater.inflate(R.layout.txrx, container, false);
		tv_pow = (TextView) view.findViewById(R.id.txrx_pow_content);
		tv_gbw = (TextView) view.findViewById(R.id.txrx_gbw_content);
		tv_sq = (TextView) view.findViewById(R.id.txrx_sq_content);
		tv_ctdcss = (TextView) view.findViewById(R.id.txrx_ctdcss_content);
		tv_volume = (TextView) view.findViewById(R.id.txrx_volume_content);
		tv_tx = (TextView) view.findViewById(R.id.txrx_tx_content);
		tv_rx = (TextView) view.findViewById(R.id.txrx_rx_content);

		iv_plink = (ImageView) view.findViewById(R.id.txrx_blink_iv);
		iv_plink.setVisibility(View.GONE);

		btn_send = (Button) view.findViewById(R.id.txrx_send);
		// btn_send.setOnClickListener(this);

		/*
		 * 按下松开的时候将IO的操作放在异步任务中，这样响应速度就可以加快 
		 * 有一个问题就是如果反复点击发送按钮，会闪退
		 * 
		 */
		btn_send.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					iv_plink.setVisibility(View.VISIBLE);
					//setFlickerAnimation(iv_plink);
					isStopTalk = false;
					// GPIO.closeGPIO("25");
					
					stopTrackReceive();
					// startRecordSend();
					new recordSendTask().execute();
					break;
				case MotionEvent.ACTION_MOVE:
					isStopTalk = false;
					break;
				case MotionEvent.ACTION_UP:
					iv_plink.setVisibility(View.GONE);
					// GPIO.openGPIO("25");
					isStopTalk = true;
					// stopRecordSend();
					// startTrackReceive();
					new trackReceiveTask().execute();
					break;
				}
				return true;
			}
		});

		setTextToAllViews();

		/*
		 * new android.os.Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { //GPIO.openGPIO("138");
		 * GPIO.openGPIO("136"); GPIO.openGPIO("25"); } }, 500);
		 */

		GPIO.modifiedGPIO("101:1 1 1 0 1 0 0");
		GPIO.modifiedGPIO("102:1 0 1 1 0 1 0");

		GPIO.openGPIO("136");
		GPIO.openGPIO("25");

		new android.os.Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				// 默认进入app就可以接收数据

				StringBuilder AT_DMOSETGROUP_VOLUME;
				AT_DMOSETGROUP_VOLUME = new StringBuilder(100);
				AT_DMOSETGROUP_VOLUME.append("AT");
				AT_DMOSETGROUP_VOLUME.append("+");
				AT_DMOSETGROUP_VOLUME.append("DMOSETVOLUME");
				AT_DMOSETGROUP_VOLUME.append("=");
				AT_DMOSETGROUP_VOLUME.append(String.valueOf(Utils
						.readIntFromSharePreference("volume", 8)));
				AT_DMOSETGROUP_VOLUME.append("\r\n");

				try {
					openSerialPort(device, 9600);
					writeDataToSerilPort(val_AT_DMOSETGROUP); // 设置模块参数

					writeDataToSerilPort(String.valueOf(AT_DMOSETGROUP_VOLUME));// 设置音量
					// closeSerialPort(device, def_baudrate);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (isFirstRunReceive) {
					receiveThread = new AudioReceiveThread();
					receiveThread.start();
					isFirstRunReceive = false;
				}

				startTrackReceive();
			}
		}, 1000);

		// startRecordSend();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// android.util.Log.d("lwj", "onActivityCreated");
		mBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.runyee.ptt.talk.down");
		filter.addAction("com.runyee.ptt.talk.up");
		filter.addAction(Intent.ACTION_HEADSET_PLUG);
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		talkReceiver = new TalkReceiver();
		getActivity().registerReceiver(talkReceiver, filter);

		super.onActivityCreated(savedInstanceState);
	}

	private class trackReceiveTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			GPIO.openGPIO("25");
			startTrackReceive();
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	private void startTrackReceive() {
		// 获得音频缓冲区大小
		if (player == null) {
			int bufferSize = AudioTrack.getMinBufferSize(sampRate,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);
			// Log.d("", "播放缓冲区大小" + bufferSize);

			// 获得音轨对象
			player = new AudioTrack(AudioManager.STREAM_MUSIC, sampRate,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize,
					AudioTrack.MODE_STREAM);

		}
		// 设置喇叭音量
		player.setStereoVolume(1.0f, 1.0f);
		// 开始播放声音
		try {
			player.play();
			// Log.d("lwj", "AudioReceiveThread-->play()");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		byte[] audio = new byte[160];// 音频读取缓存
		int length = 0;

		try {
			length = mInputStream.read(audio);// 从网络读取音频数据
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] temp = audio.clone();
		if (length > 0 && length % 2 == 0) {
			// for(int
			// i=0;i<length;i++)audio[i]=(byte)(audio[i]*2);//音频放大1倍
			player.write(audio, 0, temp.length);// 播放音频数据
		}

		try {
			mInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopTrackReceive() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
	}

	private class recordSendTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			GPIO.closeGPIO("25");
			startRecordSend();
			return null;
		}
	}

	private void startRecordSend() {
		// 获得录音缓冲区大小
		int bufferSize = AudioRecord.getMinBufferSize(sampRate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);
		// Log.d("", "录音缓冲区大小" + bufferSize);

		// 获得录音机对象
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampRate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);

		try {
			recorder.startRecording();// 开始录音
			// Log.d("lwj", "AudioSendThread-->startRecording()");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		byte[] readBuffer = new byte[640];// 录音缓冲区

		int length = 0;

		length = recorder.read(readBuffer, 0, 640);// 从mic读取音频数据
		if (length > 0 && length % 2 == 0) {
			try {
				mOutputStream.write(readBuffer, 0, length);// 写入到输出流，把音频数据通过网络发送给对方
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void stopRecordSend() {
		try {
			if (recorder != null) {
				recorder.stop();
				recorder.release();
				recorder = null;
				mOutputStream.close();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d("lwj", "TxRxFragment-->onStart()");
		setTextToAllViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		flag_ = 0;
	}

	private void readDateFromSharePreference() {
		val_pow = Utils.readStringFromSharePreference("POW", "High");
		val_gbw = Utils.readStringFromSharePreference("GBW", "25KHz");

		index_pow = Utils.readIntFromSharePreference("INDEX_POW", 1);
		index_gbw = Utils.readIntFromSharePreference("INDEX_GBW", 1);

		val_sq = Utils.readIntFromSharePreference("SQ", 4);

		val_ctdcss_tx = Utils.readIntFromSharePreference("CTCSS", 1);
		val_ctdcss_rx = Utils.readIntFromSharePreference("ctdcss_rx", 1);
		// val_ctdcss = val_ctdcss_tx + "(" + array_ctdcss[val_ctdcss_tx] + ")"
		// + " " + val_ctdcss_rx + "(" + array_ctdcss[val_ctdcss_rx] + ")";

		val_tx = Utils.readStringFromSharePreference("TX", "409.750");// String.valueOf(409.750f)
		val_rx = Utils.readStringFromSharePreference("RX", "409.750");// String.valueOf(409.750f)

		if (flag_ == 1) {
			StringBuilder AT_DMOSETGROUP1;
			AT_DMOSETGROUP1 = new StringBuilder(100);
			AT_DMOSETGROUP1.append("AT");
			AT_DMOSETGROUP1.append("+");
			AT_DMOSETGROUP1.append("DMOSETGROUP");
			AT_DMOSETGROUP1.append("=");
			AT_DMOSETGROUP1.append(String.valueOf(index_gbw));
			AT_DMOSETGROUP1.append(",");
			AT_DMOSETGROUP1.append(infoTX);
			AT_DMOSETGROUP1.append(",");
			AT_DMOSETGROUP1.append(infoRX);
			AT_DMOSETGROUP1.append(",");
			AT_DMOSETGROUP1.append(infoCTCSS);
			AT_DMOSETGROUP1.append(",");
			AT_DMOSETGROUP1.append(infoSQ);
			AT_DMOSETGROUP1.append(",");
			AT_DMOSETGROUP1.append(String.valueOf(index_pow));
			AT_DMOSETGROUP1.append("\r\n");

			val_AT_DMOSETGROUP = String.valueOf(AT_DMOSETGROUP1);
			Utils.writeStringToSharePreference("val_AT_DMOSETGROUP",
					String.valueOf(AT_DMOSETGROUP1));

			if (infoTX.length() < 7 || infoRX.length() < 7) {
				toast = Toast.makeText(getActivity(),
						R.string.vaild_text_length, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			/*
			 * Utils.writeStringToSharePreference("GBW",
			 * Utils.readStringFromSharePreference("gbw", "25KHz"));
			 * Utils.writeStringToSharePreference("POW",
			 * Utils.readStringFromSharePreference("pow", "High"));
			 * 
			 * Utils.writeIntToSharePreference("INDEX_GBW",
			 * Utils.readIntFromSharePreference("index_gbw", 1));
			 * Utils.writeIntToSharePreference("INDEX_POW",
			 * Utils.readIntFromSharePreference("index_pow", 1));
			 */

			Utils.writeIntToSharePreference("SQ", Integer.parseInt(infoSQ));
			Utils.writeIntToSharePreference("CTCSS",
					Integer.parseInt(infoCTCSS));

			Utils.writeStringToSharePreference("TX", infoTX);
			Utils.writeStringToSharePreference("RX", infoRX);

		} else {
			StringBuilder def_AT_DMOSETGROUP1;
			def_AT_DMOSETGROUP1 = new StringBuilder(100);
			def_AT_DMOSETGROUP1.append("AT");
			def_AT_DMOSETGROUP1.append("+");
			def_AT_DMOSETGROUP1.append("DMOSETGROUP");
			def_AT_DMOSETGROUP1.append("=");
			def_AT_DMOSETGROUP1.append(String.valueOf(index_gbw));
			def_AT_DMOSETGROUP1.append(",");
			def_AT_DMOSETGROUP1.append(val_tx);
			def_AT_DMOSETGROUP1.append(",");
			def_AT_DMOSETGROUP1.append(val_rx);
			def_AT_DMOSETGROUP1.append(",");
			def_AT_DMOSETGROUP1.append(val_ctdcss_tx);
			def_AT_DMOSETGROUP1.append(",");
			def_AT_DMOSETGROUP1.append(val_sq);
			def_AT_DMOSETGROUP1.append(",");
			def_AT_DMOSETGROUP1.append(String.valueOf(index_pow));
			def_AT_DMOSETGROUP1.append("\r\n");
			val_AT_DMOSETGROUP = Utils.readStringFromSharePreference(
					"val_AT_DMOSETGROUP", String.valueOf(def_AT_DMOSETGROUP1));

			if (val_tx.length() < 7 || val_rx.length() < 7) {
				toast = Toast.makeText(getActivity(),
						R.string.vaild_text_length, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
		}
		Log.d("lwj1", "val_AT_DMOSETGROUP:" + val_AT_DMOSETGROUP);
	}

	private void setTextToAllViews() {
		if (flag_ == 1) {
			tv_pow.setText(val_pow);
			tv_gbw.setText(val_gbw);
			tv_sq.setText(infoSQ);
			tv_ctdcss.setText(infoCTCSS);
			tv_tx.setText(infoTX);
			tv_rx.setText(infoRX);
		} else {
			tv_pow.setText(val_pow);
			tv_gbw.setText(val_gbw);
			tv_sq.setText(String.valueOf(val_sq));
			tv_ctdcss.setText(String.valueOf(val_ctdcss_tx));
			tv_volume.setText(String.valueOf(Utils.readIntFromSharePreference(
					"volume", 7)));
			tv_tx.setText(val_tx);
			tv_rx.setText(val_rx);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.txrx_send:
			try {
				Log.v("lwj", "onClick-->send");
				iv_plink.setVisibility(View.VISIBLE);
				setFlickerAnimation(iv_plink);
				openSerialPort(device, 9600);
				writeDataToSerilPort(data);
				// readDataFromSerilPort();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	/*
	 * @Override public boolean onTouch(View view, MotionEvent event) { int
	 * action = event.getAction(); switch (action) { case
	 * MotionEvent.ACTION_DOWN: iv_plink.setVisibility(View.VISIBLE);
	 * setFlickerAnimation(iv_plink); isStopTalk = false; GPIO.closeGPIO("25");
	 * sendThread.start(); break; case MotionEvent.ACTION_MOVE:
	 * 
	 * break; case MotionEvent.ACTION_UP: iv_plink.setVisibility(View.GONE);
	 * GPIO.openGPIO("25"); isStopTalk = true; break; } return true; }
	 */

	static SerialPort serialPort;
	static FileOutputStream mOutputStream;
	static FileInputStream mInputStream;
	public static int flag_dataReceived = 3;

	public static void openSerialPort(File device, int baudrate)
			throws SecurityException, IOException {
		// Log.v("lwj", "openSerialPort()");
		serialPort = new SerialPort(device, baudrate);
		mOutputStream = (FileOutputStream) serialPort.getOutputStream();
		mInputStream = (FileInputStream) serialPort.getInputStream();
	}

	public static void writeDataToSerilPort(String data) throws IOException {
		// Log.d("lwj12", "writeDataToSerilPort()-->data:" + data);
		mOutputStream.write(new String(data).getBytes());
		mOutputStream.write('\n');
	}

	// lwj123 没验证，不确定方法是否正确
	public static int closeSerialPort(File device, int baudrate)
			throws SecurityException, IOException {
		// Log.d("lwj", "closeSerialPort()");
		if (serialPort == null) {
			serialPort = new SerialPort(device, baudrate);
		}
		int result = serialPort.close();
		return result;
	}

	// lwj123

	public static void readDataFromSerilPort() throws IOException {
		// Log.v("lwj12", "readDataFromSerilPort()");
		int size;
		byte[] buffer = new byte[128];
		if (mInputStream == null) {
			// Log.v("lwj12", "readDataFromSerilPort() + mInputStream=null");
			return;
		}
		size = mInputStream.read(buffer);
		// Log.v("lwj12", "size:" + size/* + " data:" + new String(buffer, 0,
		// size) */);
		if (size > 0) {
			onDataReceived(buffer, size);
		}
		// mInputStream.close();
		// mOutputStream.close();
	}

	static void onDataReceived(final byte[] buffer, final int size) {
		new Thread(new Runnable() {
			public void run() {
				dataReceived = new String(buffer, 0, size);
				Log.d("lwj12", "onDataReceived:" + new String(buffer, 0, size)
						+ " length:" + dataReceived.length());
				if (dataReceived != null) {
					dataReceived = dataReceived.replaceAll(" ", "");
					if (dataReceived.equals("+DMOSETGROUP:0")) {
						Log.d("lwj12", "dataReceived: " + dataReceived);
						flag_dataReceived = 0;
						// Toast.makeText(getActivity(), "success",
						// Toast.LENGTH_SHORT);
					} else if (dataReceived.equals("+DMOSETGROUP:1")) {
						flag_dataReceived = 1;
						// Toast.makeText(getActivity(), "数据格式错误",
						// Toast.LENGTH_SHORT);
					} else {
						flag_dataReceived = 2;
						// Toast.makeText(getActivity(), "failed",
						// Toast.LENGTH_SHORT);
					}
				}
			}
		}).start();
	}

	private void setFlickerAnimation(ImageView iv_chat_head) {
		final Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(300); // duration - half a second
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		iv_chat_head.setAnimation(animation);
	}

	public class AudioSendThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (true) {
				/*
				 * if (!isStopTalk) { Log.d("lwj11", "AudioSendThread"); //
				 * 获得录音缓冲区大小 int bufferSize =
				 * AudioRecord.getMinBufferSize(sampRate,
				 * AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				 * AudioFormat.ENCODING_PCM_16BIT); Log.d("", "录音缓冲区大小" +
				 * bufferSize);
				 * 
				 * // 获得录音机对象 recorder = new
				 * AudioRecord(MediaRecorder.AudioSource.MIC, sampRate,
				 * AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				 * AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
				 * 
				 * try { recorder.startRecording();// 开始录音 Log.d("lwj",
				 * "AudioSendThread-->startRecording()"); } catch
				 * (IllegalStateException e) { e.printStackTrace(); } byte[]
				 * readBuffer = new byte[640];// 录音缓冲区
				 * 
				 * int length = 0;
				 * 
				 * length = recorder.read(readBuffer, 0, 640);// 从mic读取音频数据 if
				 * (length > 0 && length % 2 == 0) { try {
				 * mOutputStream.write(readBuffer, 0, length);//
				 * 写入到输出流，把音频数据通过网络发送给对方 } catch (IOException e) {
				 * e.printStackTrace(); } } }
				 * 
				 * try { if (recorder != null) { recorder.stop();
				 * recorder.release(); recorder = null; mOutputStream.close(); }
				 * } catch (IllegalStateException e) { e.printStackTrace(); }
				 * catch (IOException e) { e.printStackTrace(); }
				 */
			}
		}

	}

	public class AudioReceiveThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (true) {
				if (isStopTalk) {
					// Log.d("lwj11", "AudioReceiveThread");
					// 获取GPIO26的状态
					String path = "/sys/devices/virtual/misc/mtgpio/pin";
					String result = GPIO.searchGPIO(path, "26");
					// 解决对讲在后台运行时，不停的按热键会报错的问题
					if (flag_fatal) {
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
						if (MainActivity.nm != null) {
							MainActivity.nm.cancel(R.string.app_name);
						}
						getActivity().finish();
						System.exit(0);
					}
					int status = Integer.parseInt(result.substring(2, 3)); // wout
					Log.d("lwj123", "AudioReceiveThread-->status:" + status_ear
							+ " isMusicActive():" + isMusicActive());

					if (status_ear || isHeadSetOn()) {
						GPIO.closeGPIO("138");
						if (status == 0) {
							try {
								Thread.sleep(50);
								GPIO.openGPIO("23");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (status == 1) {
							try {
								Thread.sleep(50);
								// GPIO.closeGPIO("23");
								if (isMusicActive() || isFmActive()) {
									GPIO.openGPIO("23");
								} else {
									GPIO.closeGPIO("23");
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						GPIO.closeGPIO("23");
						if (status == 0) {
							try {
								Thread.sleep(50);
								GPIO.openGPIO("138");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (status == 1) {
							try {
								Thread.sleep(50);
								if (isMusicActive() || isFmActive()) {
									GPIO.openGPIO("138");
								} else {
									GPIO.closeGPIO("138");
								}
								// GPIO.closeGPIO("138");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					/*
					 * if (phoneIsInUse()) { GPIO.closeGPIO("136");
					 * GPIO.openGPIO("25");
					 * GPIO.modifiedGPIO("101:0 0 0 0 0 1 0");
					 * GPIO.modifiedGPIO("102:0 0 0 0 0 1 0"); } else if
					 * (!phoneIsInUse()) {
					 * GPIO.modifiedGPIO("101:1 1 1 0 1 0 0");
					 * GPIO.modifiedGPIO("102:1 0 1 1 0 1 0");
					 * GPIO.openGPIO("136"); GPIO.openGPIO("25"); }
					 */

					// 获得音频缓冲区大小
					/*
					 * int bufferSize = AudioTrack.getMinBufferSize(sampRate,
					 * AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					 * AudioFormat.ENCODING_PCM_16BIT); Log.d("", "播放缓冲区大小" +
					 * bufferSize);
					 * 
					 * // 获得音轨对象 player = new
					 * AudioTrack(AudioManager.STREAM_MUSIC, sampRate,
					 * AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					 * AudioFormat.ENCODING_PCM_16BIT, bufferSize,
					 * AudioTrack.MODE_STREAM);
					 * 
					 * // 设置喇叭音量 player.setStereoVolume(1.0f, 1.0f);
					 * 
					 * // 开始播放声音 try { player.play(); Log.d("lwj",
					 * "AudioReceiveThread-->play()"); } catch
					 * (IllegalStateException e) { e.printStackTrace(); } byte[]
					 * audio = new byte[160];// 音频读取缓存 int length = 0;
					 * 
					 * try { length = mInputStream.read(audio);// 读取音频数据 } catch
					 * (IOException e) { e.printStackTrace(); } byte[] temp =
					 * audio.clone(); if (length > 0 && length % 2 == 0) { //
					 * for(int //
					 * i=0;i<length;i++)audio[i]=(byte)(audio[i]*2);//音频放大1倍
					 * player.write(audio, 0, temp.length);// 播放音频数据 } if
					 * (player != null) { player.stop(); player.release();
					 * player = null; } try { mInputStream.close(); } catch
					 * (IOException e) { e.printStackTrace(); }
					 */
				}
			}
		}
	}

	boolean isMusicActive() {

		if (MyApplication.getContextObject() != null) {
			am = (AudioManager) MyApplication.getContextObject()
					.getSystemService(Context.AUDIO_SERVICE);
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

	boolean isFmActive() {
		if (MyApplication.getContextObject() != null) {
			am = (AudioManager) MyApplication.getContextObject()
					.getSystemService(Context.AUDIO_SERVICE);
		}
		if (am == null) {
			return false;
		}
		return am.isFmActive();
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

	boolean isHeadSetOn() {
		if (MyApplication.getContextObject() != null) {
			localAudioManager = (AudioManager) MyApplication.getContextObject()
					.getSystemService(Context.AUDIO_SERVICE);
		}
		if (localAudioManager == null) {
			return false;
		}
		return localAudioManager.isWiredHeadsetOn();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopRecordSend();
		stopTrackReceive();
		mBroadcastManager.unregisterReceiver(talkReceiver);
	}
}
