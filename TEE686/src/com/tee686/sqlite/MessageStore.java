package com.tee686.sqlite;

import java.text.SimpleDateFormat;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MessageStore {
	
	private Context context;
	private SQLiteDatabase db;
	private IM_SQLiteHelper sqhelper;
	
	public MessageStore(Context context)
	{
		this.context = context;
		sqhelper = new IM_SQLiteHelper(context);
		db=sqhelper.getWritableDatabase();
	}
	
	public IM_SQLiteHelper gethelper()
	{
		return sqhelper;
	}
	
	public SQLiteDatabase getWritebleDB()
	{
		return db;
	}
	
	public void closeDB()
	{
		db.close();
		sqhelper.close();
	}
	
	public long insertMessagelist(Map<String,String> msg)
	{
		ContentValues values = new ContentValues();
		values.put("whereto", msg.get("to"));
		values.put("wherefrom", msg.get("from"));
		values.put("msgcontent", msg.get("content"));
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
		String date = sDateFormat.format(new java.util.Date());
		values.put("datetime", date);
		long ret = db.insert("messagelist", "id", values);
		return ret;
	}
	
	//Todo:
	//自己写的方法

}
