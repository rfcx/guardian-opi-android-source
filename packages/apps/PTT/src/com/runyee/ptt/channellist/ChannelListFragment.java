package com.runyee.ptt.channellist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.runyee.ptt.MyApplication;
import com.runyee.ptt.R;
import com.runyee.ptt.SQLiteDataBaseHelper;
import com.runyee.ptt.Utils;
import com.runyee.ptt.txrx.TxRxFragment;

public class ChannelListFragment extends Fragment implements OnClickListener {

	private TextView tv_channel_count;
	private ImageView iv_add_channel;
	private ListView mListView;

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	public static int count;

	public static String def_channelName = "New Channel";
	public static String def_channelInfo_TX = "400.125";
	public static String def_channelInfo_RX = "400.125";
	public static String def_channelInfo_SQ = "5";
	public static String def_channelInfo_CTCSS = "1";

	public static String def_channelInfo = "TX:" + def_channelInfo_TX + " RX:"
			+ def_channelInfo_RX + " SQ:" + def_channelInfo_SQ + " CTX:"
			+ def_channelInfo_CTCSS + " CRX:" + def_channelInfo_CTCSS;

	static ChannelListAdapter mAdapter;
	public static List<Map<String, Object>> mList;

	static SQLiteDataBaseHelper sqlite;
	Cursor cursor;
	String idString = "0";
	String nametmp = def_channelName;
	String infotmp = def_channelInfo;
	String infoTXtmp = def_channelInfo_TX;
	String infoRXtmp = def_channelInfo_RX;
	String infoSQtmp = def_channelInfo_SQ;
	String infoCTCSStmp = def_channelInfo_CTCSS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.channellist, container, false);
		mContext = MyApplication.getContextObject();
		sqlite = new SQLiteDataBaseHelper(mContext);
		cursor = sqlite.readAllData();
		Log.v("lwj", "onCreateView-->count:" + cursor.getCount());
		if (cursor.getCount() == 0) {
			sqlite.insertData(new String[] { def_channelName, def_channelInfo,
					def_channelInfo_TX, def_channelInfo_RX, def_channelInfo_SQ,
					def_channelInfo_CTCSS });
		}

		tv_channel_count = (TextView) view.findViewById(R.id.channel_count);
		iv_add_channel = (ImageView) view.findViewById(R.id.channel_add);
		iv_add_channel.setOnClickListener(this);
		mListView = (ListView) view.findViewById(R.id.channel_listview);
		mListView.setDivider(new ColorDrawable(Color.GRAY));
		mListView.setDividerHeight(1);

		mData = getChannelInfo();
		mAdapter = new ChannelListAdapter(mContext, mData);
		mAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				cursor = sqlite.readAllData();
				for (int i = 0; i < cursor.getCount(); i++) {
					if (position == i) {
						cursor.moveToPosition(i);

						idString = cursor.getString(0);
						nametmp = cursor.getString(1);
						infotmp = cursor.getString(2);

						infoTXtmp = cursor.getString(3);
						infoRXtmp = cursor.getString(4);
						infoSQtmp = cursor.getString(5);
						infoCTCSStmp = cursor.getString(6);
						Log.v("lwj", "position:" + position + "id" + idString);
					}
				}

				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setClassName(getActivity(),
						"com.runyee.ptt.channellist.ListViewItemClickDialog");
				Bundle bundle = new Bundle();

				bundle.putString("id", idString);
				bundle.putString("name", nametmp);
				bundle.putString("info", infotmp);

				bundle.putString("infotx", infoTXtmp);
				bundle.putString("inforx", infoRXtmp);
				bundle.putString("infosq", infoSQtmp);
				bundle.putString("infoctcss", infoCTCSStmp);

				bundle.putInt("position", position);
				intent.putExtra("tag_position", bundle);

				startActivityForResult(intent, 3);
			}
		});

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		cursor.close();
		sqlite.close();
	}

	@Override
	public void onDestroy() {
		cursor.close();
		sqlite.close();
		super.onDestroy();
	}

	private List<Map<String, Object>> getChannelInfo() {
		mList = new ArrayList<Map<String, Object>>();

		cursor = sqlite.readAllData();// 这里注意，不加上的话，第一次进入会显示空白
		// while (cursor.moveToNext()) {
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("channel_name", cursor.getString(1));
			map.put("channel_info", cursor.getString(2));
			Log.v("lwj",
					"getChannelInfo()-->" + "i:" + " name:"
							+ cursor.getString(1) + " info:"
							+ cursor.getString(2));
			mList.add(map);
		}
		// }

		return mList;
	}

	public static void addItem() {
		String channelName = Utils.readStringFromSharePreference(
				"channel_name", "New Channel");
		String channelInfo = Utils.readStringFromSharePreference(
				"channel_info", def_channelInfo);
		String channelInfoTX = Utils.readStringFromSharePreference(
				"channel_tx", def_channelInfo_TX);
		String channelInfoRX = Utils.readStringFromSharePreference(
				"channel_rx", def_channelInfo_RX);
		String channelInfoSQ = Utils.readStringFromSharePreference(
				"channel_sq", def_channelInfo_SQ);
		String channelInfoCTCSS = Utils.readStringFromSharePreference(
				"channel_ctcss", def_channelInfo_CTCSS);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("channel_name", channelName);
		map.put("channel_info", channelInfo);
		mList.add(map);
		sqlite.insertData(new String[] { channelName, channelInfo,
				channelInfoTX, channelInfoRX, channelInfoSQ, channelInfoCTCSS });
		mAdapter.notifyDataSetChanged();
	}

	public static void deletItem(int position, String id) {
		mList.remove(position);
		sqlite.deleteData(id);
		mAdapter.notifyDataSetChanged();
	}

	public static void updateItem(int position, String id, String[] strArray) {
		// mList.remove(position);
		sqlite.updateData(id, strArray);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.channel_add:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setClassName(getActivity(),
					"com.runyee.ptt.channellist.ChannellistAddedDialog");
			startActivity(intent);
			break;
		}
	}

	private class ChannelListAdapter extends BaseAdapter {

		public ChannelListAdapter(Context context,
				List<Map<String, Object>> data) {
			mContext = context;
			mData = data;
			mInflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			count = mData.size();
			return count;
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listview_item, null);
				viewHolder.tv_channel_name = (TextView) convertView
						.findViewById(R.id.listview_item_title);
				viewHolder.tv_channel_content = (TextView) convertView
						.findViewById(R.id.listview_item_content);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (mData != null) {
				cursor = sqlite.readAllData();
				for (int i = 0; i < cursor.getCount(); i++) {
					if (position == i) {
						cursor.moveToPosition(position);
						viewHolder.tv_channel_name.setText(cursor.getString(1));
						viewHolder.tv_channel_content.setText(cursor
								.getString(2));
						viewHolder.tv_channel_name.setTextColor(Color.GRAY);
						viewHolder.tv_channel_content.setTextColor(Color.GRAY);
					}
					if (position == TxRxFragment.position) {
						Log.d("lwj1", "enable:" + TxRxFragment.enable + " pos:"
								+ TxRxFragment.position);
						if (TxRxFragment.enable) {
							viewHolder.tv_channel_name
									.setTextColor(Color.WHITE);
							viewHolder.tv_channel_content
									.setTextColor(Color.WHITE);
						}
					}
				}
			}

			tv_channel_count.setText(count + "/20");
			return convertView;
		}

		public final class ViewHolder {
			public TextView tv_channel_name;
			public TextView tv_channel_content;
		}

	}

}
