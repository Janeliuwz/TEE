package com.tee686.activity;

import java.util.ArrayList;
import java.util.List;

import com.tee686.im.ChatMsgEntity;
import com.tee686.im.ChatMsgViewAdapter;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendChatActivity extends Activity{
	
	private String userid;
	private String friendName;
	private TextView tv_titleName;
	private SharedPreferences share;
	private ChatMsgViewAdapter mAdapter;
	private ListView listMessage;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat);
		
		//默认不弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		initControl();
		
		//获取有户名和对话用户名
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
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setText(cursor.getString(cursor.getColumnIndex("msgcontent")));
			entity.setDate(cursor.getString(cursor.getColumnIndex("datetime")));
			if(cursor.getString(cursor.getColumnIndex("whereto")).equals(userid))
			{
				entity.setName(friendName);
				entity.setMsgType(true);
			}
			else
			{
				entity.setName(userid);
				entity.setMsgType(false);
			}
			mDataArrays.add(entity);
			System.out.println(cursor.getString(cursor.getColumnIndex("id")));
			System.out.println(cursor.getString(cursor.getColumnIndex("whereto")));
			System.out.println(cursor.getString(cursor.getColumnIndex("wherefrom")));
			System.out.println(cursor.getString(cursor.getColumnIndex("msgcontent")));
			System.out.println(cursor.getString(cursor.getColumnIndex("datetime")));		
		}
		cursor.close();
		store.closeDB();
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		listMessage.setAdapter(mAdapter);
	}

	private void initControl() {				
		tv_titleName = (TextView)findViewById(R.id.tv_chatTitleName);
		tv_titleName.setText(friendName);
		listMessage = (ListView) findViewById(R.id.chatList);
	}
}
