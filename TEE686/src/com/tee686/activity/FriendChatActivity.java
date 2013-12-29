package com.tee686.activity;

import com.casit.tee686.R;
import com.tee686.sqlite.MessageStore;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class FriendChatActivity extends Activity{
	
	private String userid;
	private String friendName;
	private TextView tv_titleName;
	private SharedPreferences share;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		initControl();
		
		Intent intent = getIntent();
		friendName = intent.getStringExtra("friendName");
		share = getSharedPreferences(UserLoginActivity.SharedName,
				Context.MODE_PRIVATE);
		userid = share.getString("uid","") + "@" + XmppTool.getServer();
		Toast.makeText(getApplicationContext(), 
				friendName + userid, Toast.LENGTH_SHORT).show();
		
		GetData();	
	}
	
	private void GetData()
	{
		MessageStore store = new MessageStore(FriendChatActivity.this);
		Cursor cursor = store.selectMessagelist(friendName, userid);
		while(cursor.moveToNext())
		{
			System.out.println(cursor.getString(cursor.getColumnIndex("id")));
			System.out.println(cursor.getString(cursor.getColumnIndex("whereto")));
			System.out.println(cursor.getString(cursor.getColumnIndex("wherefrom")));
			System.out.println(cursor.getString(cursor.getColumnIndex("msgcontent")));
			System.out.println(cursor.getString(cursor.getColumnIndex("datetime")));
		}
		cursor.close();
		store.closeDB();
	}

	private void initControl() {				
		tv_titleName = (TextView)findViewById(R.id.tv_chatTitleName);
		tv_titleName.setText(friendName);
	}
}
