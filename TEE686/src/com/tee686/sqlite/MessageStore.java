package com.tee686.sqlite;

import java.text.SimpleDateFormat;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
		
		if(msg.containsKey("ifread"))
			values.put("ifread",0);
		else
			values.put("ifread",1);
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
		String date = sDateFormat.format(new java.util.Date());
		values.put("datetime", date);
		long ret = db.insert("messagelist", "id", values);
		return ret;
	}
	
	public Cursor selectMessagelist(String friendid,String userid)
	{
		return db.rawQuery("select * from messagelist where (whereto=? and wherefrom=?) or (whereto=? and wherefrom=?) order by id asc", new String[]{friendid,userid,userid,friendid});
	}
	
	public boolean deletedatabase(String name)
	{
		return true;
	}
	//Todo:
	//自己写的方法

}
