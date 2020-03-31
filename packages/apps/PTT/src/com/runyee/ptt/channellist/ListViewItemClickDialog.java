package com.runyee.ptt.channellist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.runyee.ptt.R;
import com.runyee.ptt.txrx.TxRxFragment;

public class ListViewItemClickDialog extends Activity implements
		OnClickListener {

	private TextView tv_enable;
	private TextView tv_delete;
	private TextView tv_edit;
	Intent intent;
	Bundle bundle;
	int position;
	String id;
	String name;
	String info;
	String infoTX;
	String infoRX;
	String infoSQ;
	String infoCTCSS;

	private int flag = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_item_dialog_view);

		intent = getIntent();
		bundle = intent.getBundleExtra("tag_position");
		position = bundle.getInt("position");
		id = bundle.getString("id");
		name = bundle.getString("name");
		info = bundle.getString("info");

		infoTX = bundle.getString("infotx");
		infoRX = bundle.getString("inforx");
		infoSQ = bundle.getString("infosq");
		infoCTCSS = bundle.getString("infoctcss");

		Log.v("lwj1", "get position:" + position + " id:" + id + " name:"
				+ name + " infoTX:" + infoTX + " infoRX:" + infoRX + " infoSQ:"
				+ infoSQ + " infoCTCSS:" + infoCTCSS);

		initView();
	}

	private void initView() {
		tv_enable = (TextView) findViewById(R.id.tv_item_enable);
		tv_enable.setOnClickListener(this);

		tv_delete = (TextView) findViewById(R.id.tv_item_delete);
		tv_delete.setOnClickListener(this);

		tv_edit = (TextView) findViewById(R.id.tv_item_edit);
		tv_edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.tv_item_enable:

			Intent it_enable = new Intent();
			it_enable.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			it_enable.setClassName(this, "com.runyee.ptt.MainActivity");

			Bundle bundle_enable = new Bundle();
			bundle_enable.putBoolean("enable", true);
			bundle_enable.putInt("flag", flag);
			bundle_enable.putInt("position", position);
			bundle_enable.putString("id", this.id);
			bundle_enable.putString("name", name);
			bundle_enable.putString("info", info);
			bundle_enable.putString("infotx", infoTX);
			bundle_enable.putString("inforx", infoRX);
			bundle_enable.putString("infosq", infoSQ);
			bundle_enable.putString("infoctcss", infoCTCSS);
			it_enable.putExtra("tag_enable", bundle_enable);

			startActivity(it_enable);
			TxRxFragment.flag_ = 1;
			TxRxFragment.isFirstRunReceive = false;
			finish();
			break;
		case R.id.tv_item_delete:
			ChannelListFragment.deletItem(position, this.id);
			finish();
			break;
		case R.id.tv_item_edit:
			Intent it = new Intent();
			it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			it.setClassName(this,
					"com.runyee.ptt.channellist.ChannellistAddedDialog");

			Bundle bundle = new Bundle();
			bundle.putInt("flag", flag);
			bundle.putString("id", this.id);
			bundle.putString("name", name);
			bundle.putString("info", info);

			bundle.putString("infotx", infoTX);
			bundle.putString("inforx", infoRX);
			bundle.putString("infosq", infoSQ);
			bundle.putString("infoctcss", infoCTCSS);

			it.putExtra("tag_edit", bundle);

			startActivity(it);
			finish();
			break;
		}
		// finish();
	}

}
