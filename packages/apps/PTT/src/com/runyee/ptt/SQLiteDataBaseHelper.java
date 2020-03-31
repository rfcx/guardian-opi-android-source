package com.runyee.ptt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "CHANNELINFO_DB";
	private final static int DATABASE_VERSION = 1;

	private final static String CHANNELLIST_TABLE = "channellist";
	public final static String _ID = "_id";
	public final static String CHANNELLIST_NAME = "channelname";
	public final static String CHANNELLIST_INFO = "channelinfo";
	public final static String CHANNELLIST_INFO_TX = "channelinfotx";
	public final static String CHANNELLIST_INFO_RX = "channelinforx";
	public final static String CHANNELLIST_INFO_SQ = "channelinfosq";
	public final static String CHANNELLIST_INFO_CTCSS = "channelinfoctcss";

	/*
	 * public DataBaseHelper(Context context, String name, CursorFactory
	 * factory, int version) { super(context, DATABASE_NAME, null,
	 * DATABASE_VERSION); }
	 */

	public SQLiteDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + CHANNELLIST_TABLE + " (" + _ID
				+ " integer primary key autoincrement, " + CHANNELLIST_NAME
				+ " text, " + CHANNELLIST_INFO + " text, "
				+ CHANNELLIST_INFO_TX + " text, " + CHANNELLIST_INFO_RX
				+ " text, " + CHANNELLIST_INFO_SQ + " text, "
				+ CHANNELLIST_INFO_CTCSS + " text ) ";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = " drop table if exists " + CHANNELLIST_TABLE;
		db.execSQL(sql);
	}

	public long insertData(String[] strArray) {
		SQLiteDatabase sdb = this.getWritableDatabase(); // ÊµÀýÊýŸÝ¿â
		ContentValues cv = new ContentValues();
		cv.put(CHANNELLIST_NAME, strArray[0]);
		cv.put(CHANNELLIST_INFO, strArray[1]);
		cv.put(CHANNELLIST_INFO_TX, strArray[2]);
		cv.put(CHANNELLIST_INFO_RX, strArray[3]);
		cv.put(CHANNELLIST_INFO_SQ, strArray[4]);
		cv.put(CHANNELLIST_INFO_CTCSS, strArray[5]);
		long data = sdb.insert(CHANNELLIST_TABLE, null, cv);
		return data;
	}

	public void deleteData(String id) {
		SQLiteDatabase sdb = this.getWritableDatabase();
		String whereClause = _ID + "=?";
		String[] whereArgs = { id };
		sdb.delete(CHANNELLIST_TABLE, whereClause, whereArgs);
	}

	public int updateData(String id, String[] strArray) {
		SQLiteDatabase sdb = this.getWritableDatabase(); // ÊµÀýÊýŸÝ¿â
		ContentValues cv = new ContentValues();
		cv.put(CHANNELLIST_NAME, strArray[0]);
		cv.put(CHANNELLIST_INFO, strArray[1]);
		cv.put(CHANNELLIST_INFO_TX, strArray[2]);
		cv.put(CHANNELLIST_INFO_RX, strArray[3]);
		cv.put(CHANNELLIST_INFO_SQ, strArray[4]);
		cv.put(CHANNELLIST_INFO_CTCSS, strArray[5]);
		String whereClause = _ID + "=?";
		String[] whereArgs = { id };
		int data = sdb.update(CHANNELLIST_TABLE, cv, whereClause, whereArgs);
		return data;
	}

	public Cursor queryData(String id) {
		SQLiteDatabase sdb = this.getWritableDatabase();
		String selection = _ID + "=?";
		String[] selectionArgs = { id };
		Cursor cursor = sdb.query(CHANNELLIST_TABLE, null, selection,
				selectionArgs, null, null, null);
		return cursor;
	}

	public Cursor readAllData(String sortByOrder) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(CHANNELLIST_TABLE, new String[] { _ID,
				CHANNELLIST_NAME, CHANNELLIST_INFO, CHANNELLIST_INFO_TX,
				CHANNELLIST_INFO_RX, CHANNELLIST_INFO_SQ,
				CHANNELLIST_INFO_CTCSS }, null, null, null, null, sortByOrder);
		return cursor;
	}

	public Cursor readAllData() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(CHANNELLIST_TABLE, new String[] { _ID,
				CHANNELLIST_NAME, CHANNELLIST_INFO, CHANNELLIST_INFO_TX,
				CHANNELLIST_INFO_RX, CHANNELLIST_INFO_SQ,
				CHANNELLIST_INFO_CTCSS }, null, null, null, null, null);
		return cursor;
	}
}