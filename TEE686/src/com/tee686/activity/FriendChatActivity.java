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
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import com.tee686.im.ChatMsgEntity;
import com.tee686.im.ChatMsgViewAdapter;
import com.tee686.im.RecordButton;
import com.tee686.im.RecordButton.OnFinishedRecordListener;

import com.casit.tee686.R;
import com.tee686.sqlite.MessageStore;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FriendChatActivity extends Activity{
	
	private String userid;//当前user的jid
	private String friendName;//当前对话对方的jid
	private TextView tv_titleName;
	private SharedPreferences share;
	private ChatMsgViewAdapter mAdapter;
	private ListView listMessage;
	private Button sendMessage;
	private Button btnBack;
	private ImageButton btnInfo;
	private RecordButton btnTalk;
	private EditText tv_msg;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	//private ImageView animationIV;  
    //private AnimationDrawable animationDrawable; 
	
	private static final int CHATLIST_MENU_DELETE = 0;
	private static final int CHATLIST_MENU_COPY = 1;
	
	private OutgoingFileTransfer sendTransfer;
	public static String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/im/record";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams
				.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //默认不弹出软键盘
			
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
		mDataArrays.clear();
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
		//listMessage.setOnItemClickListener(itemListener);
		//listMessage.setOnItemLongClickListener(itemLongListener);
	}
	
	/*
	 * 对话列表项单击响应
	OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			ChatMsgEntity entity = mDataArrays.get(pos);
			boolean isComeMsg = entity.getMsgType();
			String msgContent = entity.getText();
			
			//若为语音消息，播放语音，设置图片动画
			if(isVoice(msgContent)) {
				//判断是接收还是发送的消息，获取对应视图的id
				if (isComeMsg) {
					animationIV = (ImageView)findViewById(R.id.iv_recvVoice);
					animationIV.setImageResource(R.drawable.im_recvvoiceplaying);
					System.out.println("receive");
				}
				else {
					animationIV = (ImageView)findViewById(R.id.iv_sendVoice);
					animationIV.setImageResource(R.drawable.im_sendvoiceplaying);
					System.out.println("send");
				}
				animationDrawable = (AnimationDrawable)animationIV.getDrawable();
				//播放语音		
				animationDrawable.start();		
				//监测语音播放结束时停止图片动画
				//animationDrawable.stop();		
				System.out.println("单击语音消息");
			}
			else {
				System.out.println("单击文字消息");
			}
		}
	};
	 * 对话列表项长按响应
	private OnItemLongClickListener itemLongListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, final View view, int pos, long id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(FriendChatActivity.this);		
			ChatMsgEntity entity = mDataArrays.get(pos);
			boolean isComeMsg = entity.getMsgType();
			String msgContent = entity.getText();
			
			//判断是接收还是发送的消息，获取对应视图的id
			if (isComeMsg) {
				System.out.println("receive");
			}
			else {
				System.out.println("send");
			}
			//创建语音消息或文字消息的长按菜单项
			if(isVoice(msgContent)) {
				builder.setItems(R.array.chatvoicemenu, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {
						case CHATLIST_MENU_DELETE:
							System.out.println("删除语音");
							break;
						}
					}
				});
			}
			else {
				builder.setItems(R.array.chattextmenu, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {
						case CHATLIST_MENU_DELETE:
							System.out.println("删除文字");
							break;
						case CHATLIST_MENU_COPY:
							System.out.println("复制文字");
							break;
						}
					}
				});
			}
			AlertDialog alertDialog = builder.create(); //创建对话框
			alertDialog.setCanceledOnTouchOutside(true); //点击对话框外部则消失
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //对话框无标题栏
			//Window window = alertDialog.getWindow(); //获取对话框窗口
			//WindowManager.LayoutParams lp = window.getAttributes(); //设置对话框窗口属性
			//window.setAttributes(lp);  
			alertDialog.show();
			return false;
		}
	};
	public boolean isVoice(String msgContent) {
		if(msgContent!=null) {
			if(msgContent.length()>10) {
				if(msgContent.substring(0,10).equals("[voicemsg]"))	{
					return true;
				}
			}
		}
		return false;
	}*/
	
	/*--Button监听器------------------------------------------
	 * 	backlistener -- private Button btnBack;
	 *  infolistener -- private ImageButton btnInfo;
	 * 	sendlistener -- private Button sendMessage;
	 *  finishlistener -- private RecordButton btnTalk;
	 -------------------------------------------------------*/
	//title中返回按钮监听器
	private OnClickListener backlistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			unregisterReceiver(mReceiver);
			System.out.println("注销监听");
			FriendChatActivity.this.finish();
		}
	};
	//title中个人信息按钮监听器
	private OnClickListener infolistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//TODO:FriendInfoActivity
			//Intent intent = new Intent(FriendChatActivity.this, FriendInfoActivity.class);
			//intent.putExtra("friendName", friendName);
			//startActivity(intent);
			Toast.makeText(getApplicationContext(), "查看好友资料", Toast.LENGTH_SHORT).show();
		}
	};
	//发送消息按钮监听器
	private OnClickListener sendlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
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
	//录音完成监听器
	private OnFinishedRecordListener finishlistener = new OnFinishedRecordListener() {

		@Override
		public void onFinishedRecord(String audioPath, int time) {
			System.out.println("Record finished!!Saved to" + audioPath);
			
			if(audioPath != null) {
				try {
					//发送语音消息
					String msg = "[voicemsg]"+audioPath.substring(audioPath.lastIndexOf('/'));
					ChatManager cm = XmppTool.XMPPgetChatManager();
					Chat chat = cm.createChat(friendName, null);
					chat.sendMessage(msg);
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
					entity.setVoiceTime(time);
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
					//Todo：发送语音文件
					
					//sendFile(audioPath);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				Toast.makeText(FriendChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	/*
	 * 句柄初始化
	 */
	private void initControl() {				
		tv_titleName = (TextView)findViewById(R.id.tv_chatTitleName);
		tv_msg = (EditText)findViewById(R.id.text_chatMsg);
		tv_titleName.setText(friendName);
		listMessage = (ListView)findViewById(R.id.chatList);
		sendMessage = (Button)findViewById(R.id.btn_chatSend);
		sendMessage.setOnClickListener(sendlistener);
		btnBack = (Button)findViewById(R.id.btn_chatback);
		btnBack.setOnClickListener(backlistener);
		btnInfo = (ImageButton)findViewById(R.id.btn_chat_userinfo);
		btnInfo.setOnClickListener(infolistener);
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

	public void sendFile(String path) {
		FileTransferManager sendFileManager = new FileTransferManager(
				XmppTool.getConnection());
		//sendTransfer = sendFileManager.createOutgoingFileTransfer(arg0);
		
		try {
			sendTransfer.sendFile(new java.io.File(path), "send file");
			//TODO
		}
		catch (XMPPException e){
			e.printStackTrace();
		}
	}
	public void receivedFile() {
		//TODO
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
