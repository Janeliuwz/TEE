package com.tee686.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;

import com.tee686.im.ChatMsgEntity;
import com.tee686.im.ChatMsgViewAdapter;

import com.casit.tee686.R;
import com.tee686.sqlite.MessageStore;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	private Button sendMessage;
	private EditText tv_msg;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat);
		
		//默认不弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
		//获取有户名和对话用户名
		Intent intent = getIntent();
		friendName = intent.getStringExtra("friendName");
		share = getSharedPreferences(UserLoginActivity.SharedName,
				Context.MODE_PRIVATE);
		userid = share.getString("uid","") + "@" + XmppTool.getServer();
		
		initControl();
		GetData();
		
		//注册广播接收器
		IntentFilter intentFilter = new IntentFilter("com.tee686.activity.FriendChatActivity");
		registerReceiver(mReceiver, intentFilter);
	}
	
	private void GetData()
	{
		//this.deleteDatabase("imdata.db");
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
			System.out.println(cursor.getString(cursor.getColumnIndex("ifread")));
		}
		cursor.close();
		store.closeDB();
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		if(mDataArrays.size()<6)
		{
			listMessage.setStackFromBottom(false);
		}
		else
		{
			listMessage.setStackFromBottom(true);
		}
		listMessage.setAdapter(mAdapter);
	}
	
	private OnClickListener sendlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String msg = tv_msg.getText().toString();
			if(msg.equals("")||msg == null)
			{
				Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
			}
			else
			{
				tv_msg.setText("");
				try 
				{
					ChatManager cm = XmppTool.getConnection().getChatManager();
					Chat chat = cm.createChat(friendName, null);
					chat.sendMessage(msg);
				} 
				catch (XMPPException e) {
					System.out.println(e.getMessage());
				}
				
				//发送内容存入数据库
				Map<String,String> tempmsg = new HashMap<String,String>();
				tempmsg.put("to", friendName);
				tempmsg.put("from", userid);
				tempmsg.put("content", msg);
				MessageStore store = new MessageStore(FriendChatActivity.this);
				long result = 0;
				if((result = store.insertMessagelist(tempmsg))!=-1)
				{
					System.out.println(result);
				}
				store.closeDB();
				
				//更新界面
				ChatMsgEntity entity = new ChatMsgEntity();
				entity.setText(msg);
				entity.setName(userid);
				entity.setMsgType(false);
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
				String date = sDateFormat.format(new java.util.Date());
				entity.setDate(date);
				mDataArrays.add(entity);
				if(mDataArrays.size()<6)
				{
					listMessage.setStackFromBottom(false);
				}
				else
				{
					listMessage.setStackFromBottom(true);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
		
	};

	private void initControl() {				
		tv_titleName = (TextView)findViewById(R.id.tv_chatTitleName);
		tv_msg = (EditText)findViewById(R.id.text_chatMsg);
		tv_titleName.setText(friendName);
		listMessage = (ListView) findViewById(R.id.chatList);
		sendMessage = (Button) findViewById(R.id.btn_chatSend);
		
		sendMessage.setOnClickListener(sendlistener);
	}
	
	@Override
	public void onBackPressed() 
	{
		unregisterReceiver(mReceiver);
		System.out.println("注销监听");
		this.finish();
	}
	
	//定义broadcastreceiver
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			System.out.println("broadcast Receiver");
			String friendId = intent.getStringExtra("friendId");
			String userId = intent.getStringExtra("userid");
			String content = intent.getStringExtra("content");
			if (friendId.equals(friendName)&&userId.equals(userid))
			{
				ChatMsgEntity entity = new ChatMsgEntity();
				entity.setText(content);
				entity.setName(friendId);
				entity.setMsgType(true);
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
				String date = sDateFormat.format(new java.util.Date());
				entity.setDate(date);
				mDataArrays.add(entity);
				if(mDataArrays.size()<6)
				{
					listMessage.setStackFromBottom(false);
				}
				else
				{
					listMessage.setStackFromBottom(true);
				}
				mAdapter.notifyDataSetChanged();
			}
			//System.out.println("//"+friendId);
		}
	};
}
