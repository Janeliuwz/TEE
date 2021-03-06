package com.tee686.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class IM_SQLiteHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "imdata.db";
	private static final String DB_CREATE = "create table if not exists messagelist(id integer primary key, userid varchar, whereto varchar, wherefrom varchar, msgcontent nvarchar, datetime text,ifread integer,voicetime integer)";
	private static final String NEW_CREATE = "create table if not exists newlist(id integer primary key,friendid varchar,userid varchar)";
	private static final int version = 1;
	
	public IM_SQLiteHelper(Context context) {
		super(context, DB_NAME, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.print("create db");
		db.execSQL(DB_CREATE);
		db.execSQL(NEW_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("upgrade a database");
		//db.execSQL("alter table messagelist add ifread integer");
	}
}
