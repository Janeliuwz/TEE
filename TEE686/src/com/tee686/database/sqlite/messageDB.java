package com.tee686.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
<<<<<<< HEAD
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class messageDB extends SQLiteOpenHelper{
	
	public messageDB(Context context,String name,CursorFactory factory,int version) {
		super(context, name, factory, version);
=======
import android.database.sqlite.SQLiteOpenHelper;

public class messageDB extends SQLiteOpenHelper{

	private static final String DB_NAME = "message.db";
	private static final int version = 1; 
	
	public messageDB(Context context) {
		super(context, DB_NAME, null, version);
>>>>>>> b389ed3de978d8b183e28cf5bedae45048d6f969
		// TODO Auto-generated constructor stub
	}
	
	@Override
<<<<<<< HEAD
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists messagelist(id integer primary key,whereto varchar,wherefrom varchar,messagecontent nvarchar,date text)");
=======
	public void onCreate(SQLiteDatabase arg0) {

>>>>>>> b389ed3de978d8b183e28cf5bedae45048d6f969
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
