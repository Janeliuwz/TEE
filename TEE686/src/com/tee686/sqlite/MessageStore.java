package com.tee686.sqlite;

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
	
	//Todo:
	//自己写的方法

}
