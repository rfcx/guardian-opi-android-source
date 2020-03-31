package com.runyee.ptt.channellist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.String;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runyee.ptt.MaxTextLengthFilter;
import com.runyee.ptt.R;
import com.runyee.ptt.Utils;
import android.text.LoginFilter;

public class ChannellistAddedDialog extends Activity implements OnClickListener {

	private EditText et_channelName;
	private EditText et_channelTX;
	private EditText et_channelRX;
	private EditText et_channelSQ;
	private TextView tv_channelCTCSS;
	private ImageView channelCTCSSPicker;

	String channel_info;

	private Button channelSave;
	private Button channelBack;

	Intent intent;
	Bundle bundle;
	int flag;
	private String id;
	private String name;
	private String info;
	private String infoTX;
	private String infoRX;
	private String infoSQ;
	private String infoCTCSS;

	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.channel_add_view);

		intent = getIntent();
		bundle = intent.getBundleExtra("tag_edit");
		if (bundle != null) {
			flag = bundle.getInt("flag");
			id = bundle.getString("id");
			name = bundle.getString("name");
			info = bundle.getString("info");

			infoTX = bundle.getString("infotx");
			infoRX = bundle.getString("inforx");
			infoSQ = bundle.getString("infosq");
			infoCTCSS = bundle.getString("infoctcss");
		}

		Log.v("lwj1", "added position:" + " id:" + id + " name:" + name
				+ " infoTX:" + infoTX + " infoRX:" + infoRX + " infoSQ:"
				+ infoSQ + " infoCTCSS:" + infoCTCSS);

		initView();
	}

	private void initView() {

		et_channelName = (EditText) findViewById(R.id.channel_name_content);
		if (flag == 1) {
			et_channelName.setText(name);
		} else
			et_channelName.setText("New Channel");
		et_channelName.setFilters(new InputFilter[] { new MaxTextLengthFilter(
				ChannellistAddedDialog.this, 25) });
		// et_channelName.addTextChangedListener(this);

		et_channelTX = (EditText) findViewById(R.id.channel_tx_content);
		if (flag == 1) {
			et_channelTX.setText(infoTX);
		} else
			et_channelTX.setText("400.125");
		// et_channelTX.setFilters(new InputFilter[] { new MaxTextLengthFilter(
		// ChannellistAddedDialog.this, 7) });

		et_channelTX.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				android.util.Log.d("lwj", "onTextChanged-->arg0:" + arg0);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				android.util.Log.d("lwj", "beforeTextChanged-->arg0:" + arg0);
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String channelTX = et_channelTX.getText().toString();
				// 频率输入第一位只能是4
				InputFilter[] filters = new InputFilter[] { new MaxTextLengthFilter(
						ChannellistAddedDialog.this, 7) };
				if (et_channelTX.getText().toString().length() < 1) {
					filters[0] = new MyInputFilter("4");
					et_channelTX.setFilters(filters);
				} else if (et_channelTX.getText().toString().length() >= 1) {
					filters = new InputFilter[] { new MaxTextLengthFilter(
							ChannellistAddedDialog.this, 7) };
					et_channelTX.setFilters(filters);
				}
				// 频率输入第一位只能是4
				try {
					if (Float.parseFloat(channelTX) < 400.000f) {
						toast = Toast.makeText(ChannellistAddedDialog.this,
								R.string.txrx_min_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					} else if (Float.parseFloat(channelTX) > 470.000f) {
						toast = Toast.makeText(ChannellistAddedDialog.this,
								R.string.txrx_max_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
				} catch (NumberFormatException e) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}

				/*
				 * if (channelTX.length() < 7) { toast =
				 * Toast.makeText(ChannellistAddedDialog.this,
				 * R.string.vaild_text_length, Toast.LENGTH_LONG);
				 * toast.setGravity(Gravity.TOP, 0, 0); toast.show(); }
				 */

				int inputType_tx = et_channelTX.getInputType();
				if (et_channelTX.getInputType() != 3) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
				android.util.Log.d("lwj", "inputType_tx:" + inputType_tx);
			}
		});

		et_channelRX = (EditText) findViewById(R.id.channel_rx_content);
		if (flag == 1) {
			et_channelRX.setText(infoRX);
		} else
			et_channelRX.setText("400.125");
		// et_channelRX.setFilters(new InputFilter[] { new MaxTextLengthFilter(
		// ChannellistAddedDialog.this, 7) });
		et_channelRX.addTextChangedListener(new TextWatcher() {

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
				String channelRX = et_channelRX.getText().toString();
				// 频率输入第一位只能是4
				InputFilter[] filters = new InputFilter[] { new MaxTextLengthFilter(
						ChannellistAddedDialog.this, 7) };
				if (et_channelRX.getText().toString().length() < 1) {
					filters[0] = new MyInputFilter("4");
					et_channelRX.setFilters(filters);
				} else if (et_channelRX.getText().toString().length() >= 1) {
					filters = new InputFilter[] { new MaxTextLengthFilter(
							ChannellistAddedDialog.this, 7) };
					et_channelRX.setFilters(filters);
				}
				// 频率输入第一位只能是4
				try {
					if (Float.parseFloat(channelRX) < 400.000f) {
						toast = Toast.makeText(ChannellistAddedDialog.this,
								R.string.txrx_min_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					} else if (Float.parseFloat(channelRX) > 470.000f) {
						toast = Toast.makeText(ChannellistAddedDialog.this,
								R.string.txrx_max_frequence_text,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
				} catch (NumberFormatException e) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}

				/*
				 * if (channelRX.length() < 7) { toast =
				 * Toast.makeText(ChannellistAddedDialog.this,
				 * R.string.vaild_text_length, Toast.LENGTH_LONG);
				 * toast.setGravity(Gravity.TOP, 0, 0); toast.show(); }
				 */

				int inputType_rx = et_channelRX.getInputType();
				if (et_channelRX.getInputType() != 3) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
				android.util.Log.d("lwj", "inputType_rx:" + inputType_rx);
			}
		});

		et_channelSQ = (EditText) findViewById(R.id.channel_sq_content);
		if (flag == 1) {
			et_channelSQ.setText(infoSQ);
		} else
			et_channelSQ.setText("4");
		et_channelSQ.setFilters(new InputFilter[] { new MaxTextLengthFilter(
				ChannellistAddedDialog.this, 1) });
		et_channelSQ.addTextChangedListener(new TextWatcher() {

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
				String channelSQ = et_channelSQ.getText().toString();
				Pattern p_sq = Pattern.compile("[0-8]*");
				Matcher m_sq = p_sq.matcher(channelSQ);
				if (!m_sq.matches()) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.sq_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}

				int inputType_sq = et_channelSQ.getInputType();
				if (et_channelSQ.getInputType() != 3) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.sq_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
				android.util.Log.d("lwj", "inputType_sq:" + inputType_sq);
			}
		});

		tv_channelCTCSS = (TextView) findViewById(R.id.channel_ctcss_content);
		if (flag == 1) {
			tv_channelCTCSS.setText(infoCTCSS);
		} else
			tv_channelCTCSS.setText("1");

		channelCTCSSPicker = (ImageView) findViewById(R.id.channel_numpicker);
		channelCTCSSPicker.setOnClickListener(this);

		channelSave = (Button) findViewById(R.id.channel_save);
		channelSave.setOnClickListener(this);
		channelBack = (Button) findViewById(R.id.channel_back);
		channelBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		switch (viewId) {
		case R.id.channel_numpicker:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setClassName(this,
					"com.runyee.ptt.channellist.NumberPickerDialogChannel");
			startActivityForResult(intent, 2);
			break;

		case R.id.channel_save:
			String channelName = et_channelName.getText().toString();
			Utils.writeStringToSharePreference("channel_name", channelName);
			String channelTX = et_channelTX.getText().toString();
			try {
				if (Float.parseFloat(channelTX) >= 400.000f
						&& (Float.parseFloat(channelTX) <= 470.000f) && channelTX.length()==7) {
					channelTX = et_channelTX.getText().toString();
				} else {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
					channelTX = String.valueOf(400.125f);
					et_channelRX.setText(channelTX);
				}
			} catch (NumberFormatException e) {
				toast = Toast.makeText(ChannellistAddedDialog.this,
						R.string.txrx_valid_text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				channelTX = String.valueOf(400.125f);
				et_channelRX.setText(channelTX);
			}
			Utils.writeStringToSharePreference("channel_tx", channelTX);

			String channelRX = et_channelRX.getText().toString();
			try {
				if (Float.parseFloat(channelRX) >= 400.000f
						&& (Float.parseFloat(channelRX) <= 470.000f) && channelRX.length()==7) {
					channelRX = et_channelRX.getText().toString();
				} else {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
					channelRX = String.valueOf(400.125f);
					et_channelRX.setText(channelRX);
				}
			} catch (NumberFormatException e) {
				toast = Toast.makeText(ChannellistAddedDialog.this,
						R.string.txrx_valid_text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				channelRX = String.valueOf(400.125f);
				et_channelRX.setText(channelRX);
			}

			if (channelTX.length() < 7 || channelRX.length() < 7) {
				toast = Toast.makeText(ChannellistAddedDialog.this,
						R.string.vaild_text_length, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}

			Utils.writeStringToSharePreference("channel_rx", channelRX);

			String channelSQ = et_channelSQ.getText().toString();
			Utils.writeStringToSharePreference("channel_sq", channelSQ);
			int channelCTCSS = Integer.parseInt(String.valueOf(tv_channelCTCSS
					.getText()));
			Utils.writeStringToSharePreference("channel_ctcss",
					String.valueOf(channelCTCSS));

			channel_info = "TX:" + channelTX + " RX:" + channelRX + " SQ:"
					+ channelSQ + " CTX:" + channelCTCSS + " CRX:"
					+ channelCTCSS;
			Log.v("lwj", "channel_info:" + channel_info);
			Utils.writeStringToSharePreference("channel_info", channel_info);

			if (flag == 1) {
				// 更新数据
				ChannelListFragment.updateItem(0, id, new String[] {
						channelName, channel_info, channelTX, channelRX,
						channelSQ, String.valueOf(channelCTCSS) });
			} else {
				// 动态添加数据
				if (ChannelListFragment.count < 20) {
					ChannelListFragment.addItem();
				}
			}
			finish();
			break;
		case R.id.channel_back:
			finish();
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		int val_tx = Utils.readIntFromSharePreference("channel_ctdcss_tx", 1);
		int val_rx = Utils.readIntFromSharePreference("channel_ctdcss_rx", 1);
		Log.v("lwj", "requestCode:" + requestCode + " val_tx:" + val_tx);
		switch (requestCode) {
		case 2:
			try {
				Bundle bundle = data.getExtras();
				val_tx = bundle.getInt("channel_ctdcss_tx");
				val_rx = bundle.getInt("channel_ctdcss_rx");
			} catch (NullPointerException e) {

			}

			Log.v("lwj", "val_tx:" + val_tx + " val_rx:" + val_rx);
			tv_channelCTCSS.setText(String.valueOf(val_tx));
			break;
		}
	}

	public class MyInputFilter extends LoginFilter.UsernameFilterGeneric {
		private String mAllowedDigits;

		public MyInputFilter(String digits) {
			mAllowedDigits = digits;
		}

		@Override
		public boolean isAllowed(char c) {
			android.util.Log.d("lwj", "c:" + c);
			if (mAllowedDigits.indexOf(c) != -1) {
				return true;
			} else {
				Pattern p_txrx = Pattern.compile("[0-9]*");
				Matcher m_txrx = p_txrx.matcher(String.valueOf(c));
				Pattern p_sq = Pattern.compile("[.]");
				Matcher m_sq = p_sq.matcher(String.valueOf(c));
				if (/* !m_sq.matches() && */!m_txrx.matches()) {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.edittext_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				} else {
					toast = Toast.makeText(ChannellistAddedDialog.this,
							R.string.txrx_min_max_frequence_text,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			}
			return false;
		}
	}
}
