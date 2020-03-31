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

public class NumberPickerDialogSQ extends Activity implements OnClickListener,
		OnValueChangeListener {

	private NumberPicker mNumberPicker;
	private Button btn_ok;
	private Button btn_back;
	private int defVal_numPicker = 4;
	private int val_sq = Utils.readIntFromSharePreference("sq",
			defVal_numPicker);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.numberpicker_view_sq);

		initView();
	}

	private void initView() {
		mNumberPicker = (NumberPicker) findViewById(R.id.numpicker);
		mNumberPicker.setMaxValue(8);
		mNumberPicker.setMinValue(0);
		mNumberPicker.setFocusableInTouchMode(true);
		mNumberPicker.setClickable(false);
		mNumberPicker.setValue(Utils.readIntFromSharePreference("sq",
				defVal_numPicker));
		mNumberPicker.setOnValueChangedListener(this);

		btn_ok = (Button) findViewById(R.id.ok);
		btn_ok.setOnClickListener(this);
		btn_back = (Button) findViewById(R.id.back);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
		Log.v("lwj", "oldValue:" + oldValue + "newValue:" + newValue);
		val_sq = newValue;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.ok:
			Utils.writeIntToSharePreference("sq", val_sq);
			val_sq = Utils.readIntFromSharePreference("sq", defVal_numPicker);
			break;
		case R.id.back:
			val_sq = Utils.readIntFromSharePreference("sq", defVal_numPicker);
			break;
		}
		Intent it = new Intent();
		it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		it.setClassName(this, "com.runyee.ptt.settings.SettingsFragment");
		it.putExtra("val_sq", val_sq);
		setResult(RESULT_OK, it);
		finish();
	}

}