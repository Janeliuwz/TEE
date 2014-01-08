package com.tee686.activity;

import java.io.File;
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
import com.tee686.im.RecordButton;
import com.tee686.im.RecordButton.OnFinishedRecordListener;

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
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendChatActivity extends Activity{
	
	private String userid;//当前user的jid
	private String friendName;//当前对话对方的jid
	private TextView tv_titleName;
	private SharedPreferences share;
	private ChatMsgViewAdapter mAdapter;
	private ListView listMessage;
	private Button sendMessage;
	private RecordButton btnTalk;
	private EditText tv_msg;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	
	public static String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/im/record";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat);
		
		//默认不弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
		//获取用户名和对话用户名
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
			System.out.println("从数据库获取对话数据");
			System.out.println("数据库id："+ cursor.getString(cursor.getColumnIndex("id")));
			System.out.println("发给" + cursor.getString(cursor.getColumnIndex("whereto")));
			System.out.println("来自" + cursor.getString(cursor.getColumnIndex("wherefrom")));
			System.out.println("内容：" + cursor.getString(cursor.getColumnIndex("msgcontent")));
			System.out.println("时间：" + cursor.getString(cursor.getColumnIndex("datetime")));
			System.out.println("是否已读：" + cursor.getString(cursor.getColumnIndex("ifread")));
			System.out.println("属于用户：" + cursor.getString(cursor.getColumnIndex("userid")));
		}
		store.updateMessagelist(friendName, userid);
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
	
	private OnFinishedRecordListener finishlistener = new OnFinishedRecordListener() {

		@Override
		public void onFinishedRecord(String audioPath, int time) {
			System.out.println("Record fished!!Saved to" + audioPath);
			
			if(audioPath != null) {
				try {
					//TODO:发送语音
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				Toast.makeText(FriendChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			}
		}
	};
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
					ChatManager cm = XmppTool.XMPPgetChatManager();
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
				tempmsg.put("uid", userid);
				MessageStore store = new MessageStore(FriendChatActivity.this);
				long result = 0;
				if((result = store.insertMessagelist(tempmsg))!=-1)
				{
					System.out.println(result);
				}
				store.deleteNewlist(friendName, userid);
				store.insertNewlist(friendName, userid);
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
		listMessage = (ListView)findViewById(R.id.chatList);
		sendMessage = (Button)findViewById(R.id.btn_chatSend);
		sendMessage.setOnClickListener(sendlistener);
		
		btnTalk = (RecordButton)findViewById(R.id.btn_talk);
		String path = RECORD_ROOT_PATH;
		File file = new File(path);
		file.mkdirs();
		path = path + "/" + System.currentTimeMillis() + ".amr";
		btnTalk.setSavePath(path);
		btnTalk.setOnFinishedRecordListener(finishlistener);
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
				MessageStore store = new MessageStore(FriendChatActivity.this);
				store.updateMessagelist(friendName, userid);
				store.closeDB();
			}
			//System.out.println("//"+friendId);
		}
	};
}
