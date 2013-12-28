package com.tee686.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class messageDB extends SQLiteOpenHelper{

	private static final String DB_NAME = "message.db";
	private static final int version = 1; 
	
	public messageDB(Context context) {
		super(context, DB_NAME, null, version);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
