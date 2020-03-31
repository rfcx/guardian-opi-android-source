package com.runyee.ptt.settings;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.String;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runyee.ptt.MainActivity;
import com.runyee.ptt.MaxTextLengthFilter;
import com.runyee.ptt.R;
import com.runyee.ptt.Utils;
import com.runyee.ptt.txrx.TxRxFragment;

import android.text.LoginFilter;

public class SettingsFragment extends Fragment implements OnClickListener {

	private TextView tv_powContent;
	private ImageView iv_powImageView;

	private TextView tv_gbwContent;
	private ImageView iv_gbwImageView;

	public static TextView tv_sqContent;
	private ImageView iv_sqImageView;

	private TextView tv_ctdcssContent;
	private ImageView iv_ctdcssImageView;

	private EditText txEditText;
	private EditText rxEditText;

	private Button btn_save;

	private int i = Utils.readIntFromSharePreference("index_pow", 1);
	private int j = Utils.readIntFromSharePreference("index_gbw", 1);
	private String val_pow[] = { "Low", "High" };
	private String val_gbw[] = { "12.5KHz", "25KHz" };
	private String array_ctdcss[] = { "0KHz", "1KHz", "2KHz", "3KHz", "4KHz",
			"5KHz", "6KHz", "7KHz" };
	// private String val_ctdcss[][] = {{},{}};
	String defVal_pow;
	String defVal_gbw;
	String defVal_ctdcss;

	int defVal_sq;
	int defVal_ctdcss_tx;
	int defVal_ctdcss_rx;
	String defVal_tx = Utils.readStringFromSharePreference("tx", "409.750");// String.valueOf(409.750f)
	String defVal_rx = Utils.readStringFromSharePreference("rx", "409.750");// String.valueOf(409.750f)

	public static StringBuilder AT_DMOSETGROUP;
	private TxRxFragment mTxRxFragment;
	private Toast toast;

	File device = new File("/dev/ttyMT1");
	int baudrate = 9600;

	String TFV = Utils.readStringFromSharePreference("tx", "409.750");// String.valueOf(409.750f)

	String RFV = Utils.readStringFromSharePreference("rx", "409.750");// String.valueOf(409.750f)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		i = Utils.readIntFromSharePreference("index_pow", 1);
		j = Utils.readIntFromSharePreference("index_gbw", 1);
		defVal_pow = Utils.readStringFromSharePreference("pow", "High");
		defVal_gbw = Utils.readStringFromSharePreference("gbw", "25KHz");
		defVal_sq = Utils.readIntFromSharePreference("sq", 4);

		defVal_ctdcss_tx = Utils.readIntFromSharePreference("ctdcss_tx", 1);
		defVal_ctdcss_rx = Utils.readIntFromSharePreference("ctdcss_rx", 1);
		/*
		 * defVal_ctdcss = defVal_ctdcss_tx + "(" +
		 * array_ctdcss[defVal_ctdcss_tx] + ")" + " " + defVal_ctdcss_rx + "(" +
		 * array_ctdcss[defVal_ctdcss_rx] + ")";
		 */

		defVal_tx = Utils.readStringFromSharePreference("tx", "409.750");// String.valueOf(409.750f)
		defVal_rx = Utils.readStringFromSharePreference("rx", "409.750");// String.valueOf(409.750f)

		View view = inflater.inflate(R.layout.settings, container, false);
		tv_powContent = (TextView) view.findViewById(R.id.settings_pow_content);
		tv_powContent.setText(defVal_pow);
		iv_powImageView = (ImageView) view.findViewById(R.id.settings_pow_iv);
		iv_powImageView.setOnClickListener(this);

		tv_gbwContent = (TextView) view.findViewById(R.id.settings_gbw_content);
		tv_gbwContent.setText(defVal_gbw);
		iv_gbwImageView = (ImageView) view.findViewById(R.id.settings_gbw_iv);
		iv_gbwImageView.setOnClickListener(this);

		tv_sqContent = (TextView) view.findViewById(R.id.settings_sq_content);
		tv_sqContent.setText(String.valueOf(defVal_sq));
		iv_sqImageView = (ImageView) view.findViewById(R.id.settings_sq_iv);
		iv_sqImageView.setOnClickListener(this);

		tv_ctdcssContent = (TextView) view
				.findViewById(R.id.settings_ctdcss_content);
		tv_ctdcssContent.setText(String.valueOf(defVal_ctdcss_tx));
		iv_ctdcssImageView = (ImageView) view
				.findViewById(R.id.settings_ctdcss_iv);
		iv_ctdcssImageView.setOnClickListener(this);

		txEditText = (EditText) view.findViewById(R.id.settings_tx_content);
		txEditText.setText(defVal_tx);
		// txEditText.setFilters(new InputFilter[] { new MaxTextLengthFilter(
		// getActivity(), 7) });
		txEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String channelTX = txEditText.getText().toString();
				// 频率输入第一位只能是4
				InputFilter[] filters = new InputFilter[] { new MaxTextLengthFilter(
						getActivity(), 7) };
				if (txEditText.getText().toString().length() < 1) {
					filters[0] = new MyInputFilter("4");
					txEditText.setFilters(filters);
				} else if (txEditText.getText().toString().length() >= 1) {
					filters = new InputFilter[] { new MaxTextLengthFilter(
							getActivity(), 7) };
					txEditText.setFilters(filters);
				}
				// 频率输入第一位只能是4
				try {
					if (Float.parseFloat(channelTX) < 400.000f) {
						toast = Toast.makeText(getActivity(),
								R.string.txrx_min_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					} else if (Float.parseFloat(channelTX) > 470.000f) {
						toast = Toast.makeText(getActivity(),
								R.string.txrx_max_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
				} catch (NumberFormatException e) {
					toast = Toast.makeText(getActivity(),
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			}
		});

		rxEditText = (EditText) view.findViewById(R.id.settings_rx_content);
		rxEditText.setText(defVal_rx);

		/*
		 * InputFilter[] filters = new InputFilter[] { new MaxTextLengthFilter(
		 * getActivity(), 7) };
		 */

		// rxEditText.setFilters(new InputFilter[] { new MaxTextLengthFilter(
		// getActivity(), 7) });
		rxEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String channelRX = rxEditText.getText().toString();
				// 频率输入第一位只能是4
				InputFilter[] filters = new InputFilter[] { new MaxTextLengthFilter(
						getActivity(), 7) };
				if (rxEditText.getText().toString().length() < 1) {
					filters[0] = new MyInputFilter("4");
					rxEditText.setFilters(filters);
				} else if (rxEditText.getText().toString().length() >= 1) {
					filters = new InputFilter[] { new MaxTextLengthFilter(
							getActivity(), 7) };
					rxEditText.setFilters(filters);
				}
				// 频率输入第一位只能是4
				try {
					if (Float.parseFloat(channelRX) < 400.000f) {
						toast = Toast.makeText(getActivity(),
								R.string.txrx_min_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					} else if (Float.parseFloat(channelRX) > 470.000f) {
						toast = Toast.makeText(getActivity(),
								R.string.txrx_max_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
				} catch (NumberFormatException e) {
					toast = Toast.makeText(getActivity(),
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			}
		});

		btn_save = (Button) view.findViewById(R.id.settings_save);
		btn_save.setOnClickListener(this);

		return view;
	}

	public class MyInputFilter extends LoginFilter.UsernameFilterGeneric {
		private String mAllowedDigits;

		public MyInputFilter(String digits) {
			mAllowedDigits = digits;
		}

		@Override
		public boolean isAllowed(char c) {
			if (mAllowedDigits.indexOf(c) != -1) {
				return true;
			} else {
				Pattern p_txrx = Pattern.compile("[0-9]*");
				Matcher m_txrx = p_txrx.matcher(String.valueOf(c));
				Pattern p_sq = Pattern.compile("[.]");
				Matcher m_sq = p_sq.matcher(String.valueOf(c));
				if (/* !m_sq.matches() && */!m_txrx.matches()) {
					toast = Toast.makeText(getActivity(),
							R.string.edittext_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				} else {
					toast = Toast.makeText(getActivity(),
							R.string.txrx_min_max_frequence_text,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			}
			return false;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();

		switch (id) {
		case R.id.settings_pow_iv:
			i++;
			if (i >= 2)
				i = 0;
			//Log.v("lwj", "i = " + i);
			tv_powContent.setText(val_pow[i]);
			Utils.writeIntToSharePreference("index_pow", i);
			Utils.writeStringToSharePreference("pow", val_pow[i]);
			break;
		case R.id.settings_gbw_iv:
			j++;
			if (j >= 2)
				j = 0;
			//Log.v("lwj", "j = " + j);
			tv_gbwContent.setText(val_gbw[j]);
			Utils.writeIntToSharePreference("index_gbw", j);
			Utils.writeStringToSharePreference("gbw", val_gbw[j]);
			break;
		case R.id.settings_sq_iv:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setClassName(getActivity(),
					"com.runyee.ptt.settings.NumberPickerDialogSQ");
			startActivityForResult(intent, 0);
			break;
		case R.id.settings_ctdcss_iv:
			Intent intent1 = new Intent();
			intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent1.setClassName(getActivity(),
					"com.runyee.ptt.settings.NumberPickerDialogCTDCSS");
			startActivityForResult(intent1, 1);
			break;
		case R.id.settings_save:
			int GBW = Utils.readIntFromSharePreference("index_gbw", 1);

			String channelTX = txEditText.getText().toString();
			try {
				if (Float.parseFloat(channelTX) >= 400.000f
						&& (Float.parseFloat(channelTX) <= 470.000f) && channelTX.length()==7) {
					TFV = channelTX;
				} else {
					toast = Toast.makeText(getActivity(),
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
					TFV = Utils.readStringFromSharePreference("TX", "409.750");// String.valueOf(409.750f)
					// txEditText.setText(TFV);
				}
			} catch (NumberFormatException e) {
				toast = Toast.makeText(getActivity(), R.string.txrx_valid_text,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				TFV = Utils.readStringFromSharePreference("TX", "409.750");// String.valueOf(409.750f)
				// txEditText.setText(TFV);
			}

			Utils.writeStringToSharePreference("tx", TFV);

			String channelRX = rxEditText.getText().toString();
			try {
				if (Float.parseFloat(channelRX) >= 400.000f
						&& (Float.parseFloat(channelRX) <= 470.000f) && channelRX.length()==7) {
					RFV = channelRX;
				} else {
					toast = Toast.makeText(getActivity(),
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
					RFV = Utils.readStringFromSharePreference("RX", "409.750");// String.valueOf(409.750f)
					// rxEditText.setText(RFV);
				}
			} catch (NumberFormatException e) {
				toast = Toast.makeText(getActivity(), R.string.txrx_valid_text,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				RFV = Utils.readStringFromSharePreference("RX", "409.750");// String.valueOf(409.750f)
				// rxEditText.setText(RFV);
			}

			/*
			 * if (channelTX.length() < 7 || (channelRX.length() < 7)) { toast =
			 * Toast.makeText(getActivity(), R.string.vaild_text_length,
			 * Toast.LENGTH_SHORT); toast.setGravity(Gravity.TOP, 0, 0);
			 * toast.show(); }
			 */

			Utils.writeStringToSharePreference("rx", RFV);

			int CXCSS = Utils.readIntFromSharePreference("ctdcss_tx", 1);
			int SQ = Utils.readIntFromSharePreference("sq", 4);
			int POWER = Utils.readIntFromSharePreference("index_pow", 1);
			AT_DMOSETGROUP = new StringBuilder(100);
			AT_DMOSETGROUP.append("AT");
			AT_DMOSETGROUP.append("+");
			AT_DMOSETGROUP.append("DMOSETGROUP");
			AT_DMOSETGROUP.append("=");
			AT_DMOSETGROUP.append(GBW);
			AT_DMOSETGROUP.append(",");
			AT_DMOSETGROUP.append(TFV);
			AT_DMOSETGROUP.append(",");
			AT_DMOSETGROUP.append(RFV);
			AT_DMOSETGROUP.append(",");
			AT_DMOSETGROUP.append(CXCSS);
			AT_DMOSETGROUP.append(",");
			AT_DMOSETGROUP.append(SQ);
			AT_DMOSETGROUP.append(",");
			AT_DMOSETGROUP.append(POWER);
			AT_DMOSETGROUP.append("\r\n");

			Utils.writeStringToSharePreference("val_AT_DMOSETGROUP",
					String.valueOf(AT_DMOSETGROUP));

			Utils.writeStringToSharePreference("GBW",
					Utils.readStringFromSharePreference("gbw", "25KHz"));
			Utils.writeStringToSharePreference("POW",
					Utils.readStringFromSharePreference("pow", "High"));

			Utils.writeIntToSharePreference("INDEX_GBW",
					Utils.readIntFromSharePreference("index_gbw", 1));
			Utils.writeIntToSharePreference("INDEX_POW",
					Utils.readIntFromSharePreference("index_pow", 1));

			Utils.writeIntToSharePreference("SQ",
					Utils.readIntFromSharePreference("sq", 4));
			Utils.writeIntToSharePreference("CTCSS",
					Utils.readIntFromSharePreference("ctdcss_tx", 1));

			Utils.writeStringToSharePreference("TX", TFV);
			Utils.writeStringToSharePreference("RX", RFV);

			// 把值写进串口
			try {
				TxRxFragment.openSerialPort(device, baudrate);
				TxRxFragment.writeDataToSerilPort(String
						.valueOf(AT_DMOSETGROUP));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.v("lwj1", "AT_DMOSETGROUP:" + AT_DMOSETGROUP);

			android.support.v4.app.FragmentManager fm = getActivity()
					.getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction ft = fm
					.beginTransaction();
			if (mTxRxFragment == null) {
				mTxRxFragment = new TxRxFragment();
			}
			ft.replace(R.id.main_content, mTxRxFragment);
			ft.commit();
			TxRxFragment.flag_ = 0;
			TxRxFragment.isFirstRunReceive = false;
			MainActivity.rb_txrx.setChecked(true);
			// lwj123
			/*
			 * try { int result = TxRxFragment.closeSerialPort(device,
			 * baudrate); Log.v("lwj1", "result:" + result); } catch
			 * (SecurityException e) { e.printStackTrace(); } catch (IOException
			 * e) { e.printStackTrace(); }
			 */
			// lwj123
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.v("lwj", "onActivityResult-->requestCode:" + requestCode);
		int val_sq = Utils.readIntFromSharePreference("sq", 4);
		int val_tx = Utils.readIntFromSharePreference("ctdcss_tx", 1);
		int val_rx = Utils.readIntFromSharePreference("ctdcss_rx", 1);
		// String val_ctdcss = val_tx + "(" + array_ctdcss[val_tx] + ")" + " "
		// + val_rx + "(" + array_ctdcss[val_rx] + ")";

		switch (requestCode) {
		case 0:
			try {
				Bundle bundle = data.getExtras();
				val_sq = bundle.getInt("val_sq");
			} catch (NullPointerException e) {

			}

			Log.v("lwj", "val_sq:" + val_sq);
			tv_sqContent.setText(String.valueOf(val_sq));
			break;
		case 1:
			try {
				Bundle bundle = data.getExtras();
				val_tx = bundle.getInt("ctdcss_tx");
				val_rx = bundle.getInt("ctdcss_rx");
			} catch (NullPointerException e) {
			}

			// val_ctdcss = val_tx + "(" + array_ctdcss[val_tx] + ")" + " "
			// + val_rx + "(" + array_ctdcss[val_rx] + ")";
			//Log.v("lwj", "val_tx:" + val_tx);
			tv_ctdcssContent.setText(String.valueOf(val_tx));
			break;

		}
	}
}
