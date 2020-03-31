package com.runyee.ptt.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.runyee.ptt.R;
import com.runyee.ptt.Utils;

public class NumberPickerDialogCTDCSS extends Activity implements
		OnClickListener, OnValueChangeListener {

	private NumberPicker mNumberPickerTx;
	private NumberPicker mNumberPickerRx;
	private Button btn_ok;
	private Button btn_back;
	private int defVal_numPicker = 1;
	private int val_tx = Utils.readIntFromSharePreference("ctdcss_tx",
			defVal_numPicker);
	private int val_rx = Utils.readIntFromSharePreference("ctdcss_rx",
			defVal_numPicker);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.numberpicker_view_ctdcss);

		initView();
	}

	private void initView() {
		mNumberPickerTx = (NumberPicker) findViewById(R.id.numpicker_tx);
		mNumberPickerTx.setMaxValue(121);
		mNumberPickerTx.setMinValue(0);
		mNumberPickerTx.setFocusableInTouchMode(true);
		mNumberPickerTx.setClickable(true);
		mNumberPickerTx.setValue(Utils.readIntFromSharePreference("ctdcss_tx",
				defVal_numPicker));
		mNumberPickerTx.setOnValueChangedListener(this);

		mNumberPickerRx = (NumberPicker) findViewById(R.id.numpicker_rx);
		mNumberPickerRx.setMaxValue(6);
		mNumberPickerRx.setMinValue(0);
		mNumberPickerRx.setFocusableInTouchMode(true);
		mNumberPickerRx.setClickable(true);
		mNumberPickerRx.setValue(Utils.readIntFromSharePreference("ctdcss_rx",
				defVal_numPicker));
		mNumberPickerRx.setOnValueChangedListener(this);

		btn_ok = (Button) findViewById(R.id.ok);
		btn_ok.setOnClickListener(this);
		btn_back = (Button) findViewById(R.id.back);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
		Log.v("lwj", "oldValue:" + oldValue + "newValue:" + newValue);

		int id = picker.getId();
		switch (id) {
		case R.id.numpicker_tx:
			val_tx = newValue;
			break;

		case R.id.numpicker_rx:
			val_rx = newValue;
			break;
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.ok:
			Utils.writeIntToSharePreference("ctdcss_tx", val_tx);
			val_tx = Utils.readIntFromSharePreference("ctdcss_tx",
					defVal_numPicker);
			Utils.writeIntToSharePreference("ctdcss_rx", val_rx);
			val_rx = Utils.readIntFromSharePreference("ctdcss_rx",
					defVal_numPicker);
			break;
		case R.id.back:
			val_tx = Utils.readIntFromSharePreference("ctdcss_tx",
					defVal_numPicker);
			val_rx = Utils.readIntFromSharePreference("ctdcss_rx",
					defVal_numPicker);
			break;
		}
		Intent it = new Intent();
		it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		it.setClassName(this, "com.runyee.ptt.settings.SettingsFragment");
		it.putExtra("ctdcss_tx", val_tx);
		it.putExtra("ctdcss_rx", val_rx);
		setResult(RESULT_OK, it);
		finish();
	}

}