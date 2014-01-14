package com.tee686.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import com.casit.tee686.R;
import com.unionpay.mpay.views.r;
import com.tee686.im.ChatMsgEntity;
import com.tee686.im.ChatMsgViewAdapter;
import com.tee686.im.FriendsListAdapter;
import com.tee686.im.NewMsgListAdapter;
import com.tee686.im.PinyinComparator;
import com.tee686.im.SideBar;
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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
//import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FriendsMainActivity extends Activity{

	public static FriendsMainActivity instance = null;
	
	//Tab Bottom 标签页
	private ViewPager mTabPager; //页卡内容
	private ImageView mTabCursor; //tab按钮选中时底端动画图片
	private ImageView mTabMsg, mTabContacts; //页卡底端按钮图片
	private LinearLayout btnMsg, btnContacts; //tab底部页卡按钮
	private static final int TAB_MESSAGE = 0;
	private static final int TAB_CONTACTS = 1;
	
	private int currIndex = 0; //当前页卡编号
	private int bmpW; //动画图片宽度
	private int offset = 0; //动画图片偏移量
	
	private View layout;
	private LayoutInflater inflater;
	private View view0;
	private View view1;
	
	private ImageButton addFriend;
	private ListView friendsList;
	private ListView msgList;
	
	private WindowManager mWindowManager;
	private SideBar friendsListIndexbar;
	private TextView mDialogText;
	private List<Map<String,String>> mNewMsg = new ArrayList<Map<String,String>>();
	//private List<FriendsListAdapter.friends> mfriendsList = new ArrayList<FriendsListAdapter.friends>();
	private List<Map<String,String>> tempNames =  new ArrayList<Map<String,String>>();
	private List<Map<String,String>> friendsNamesList =  new ArrayList<Map<String,String>>();
	private String[] friendsNames;
	private NewMsgListAdapter mAdapter;
	private FriendsListAdapter fAdapter;
	private PagerAdapter mPagerAdapter;
	
	//菜单选项
	private static final int CONTACTS_MENU_INFO = 0;
	private static final int CONTACTS_MENU_SEND = 1;
	private static final int CONTACTS_MENU_DELETE = 2;
	private static final int MSG_MENU_CLEAR = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_main);
		//this.deleteDatabase("imdata.db");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
				
		initViewPager();
		
		//获取新消息数据
		//getNewMsgListView();
		
		//设置新消息listview的适配器
		//mAdapter = new NewMsgListAdapter(this, mNewMsg);
		//newMsgList.setAdapter(mAdapter);
		
		//好友列表信息
		//getFriendsListView();
		
		//注册广播接收器
		IntentFilter intentFilter = new IntentFilter("com.tee686.activity.FriendsMainActivity");
		registerReceiver(mReceiver, intentFilter);
	}
		
	@Override
	protected void onRestart() {
		super.onRestart();
		
		getNewMsgData(); //获取新消息数据
		mAdapter.notifyDataSetChanged(); //更新新消息列表
		
		//注册广播接收器
		IntentFilter intentFilter = new IntentFilter("com.tee686.activity.FriendsMainActivity");
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public void onBackPressed() 
	{
		//注销广播监听器
		unregisterReceiver(mReceiver);
		System.out.println("注销监听");
        mWindowManager.removeView(mDialogText);
		this.finish();
	}
	
	//定义broadcastreceiver
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			System.out.println("broadcast Receiver");

			getNewMsgData(); //获取新消息数据
			mAdapter.notifyDataSetChanged(); //更新新消息列表
		}
	};

	/*
	 * 获取新消息列表数据
	 */
	private void getNewMsgData()
	{
		mNewMsg.clear();
		Map<String,String> tempid = new HashMap<String,String>();
		SharedPreferences share = getSharedPreferences(UserLoginActivity.SharedName,
				Context.MODE_PRIVATE);
		
		//当前user的jid
		String userid = share.getString("uid","") + "@" + XmppTool.getServer();
		
		//查询数据库获取新消息
		MessageStore store = new MessageStore(FriendsMainActivity.this);
		Cursor newlistcursor = store.selectNewlist();
		while(newlistcursor.moveToNext()) {
			
			String friendid = newlistcursor.getString(newlistcursor.getColumnIndex("friendid"));
			Cursor newMsgcursor = store.selectMessagelist(friendid, userid);
			
			if(newMsgcursor.moveToLast()) {
				
				Map<String,String> newMsg = new HashMap<String,String>();
				newMsg.put("msgcontent",newMsgcursor.getString(newMsgcursor.getColumnIndex("msgcontent")));
				newMsg.put("friendid",friendid);
				newMsg.put("datetime",newMsgcursor.getString(newMsgcursor.getColumnIndex("datetime")));
				Integer count = 0;
				Cursor countcursor = store.selectNewmsg(friendid,userid);
				
				while(countcursor.moveToNext()) {
					count++;
				}
				
				if(count>99) {
					newMsg.put("count", "99");
				}
				else {
					newMsg.put("count", count.toString());
				}
				mNewMsg.add(newMsg);
			}
			newMsgcursor.close();
		}
		/*Cursor newMsgcursor = store.selectNewmsg(userid);
		while(newMsgcursor.moveToNext())
		{
			String friendid = newMsgcursor.getString(newMsgcursor.getColumnIndex("wherefrom"));
			if(!tempid.containsKey(friendid))
			{
				tempid.put(friendid, "yes");
				Map<String,String> newMsg = new HashMap<String,String>();
				newMsg.put("msgcontent",newMsgcursor.getString(newMsgcursor.getColumnIndex("msgcontent")));
				newMsg.put("friendid",friendid);
				mNewMsg.add(newMsg);
			}
		}*/
		newlistcursor.close();
		store.closeDB();
	}
	
	private void getNewMsgView() {
		
		//单击列表项跳转
		msgList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				unregisterReceiver(mReceiver);
				TextView tv_name = (TextView)view.findViewById(R.id.tv_msgitem_name);

				//跳转到聊天界面
				Intent intent = new Intent(FriendsMainActivity.this, FriendChatActivity.class);
				intent.putExtra("friendName", tv_name.getText().toString());
				startActivity(intent);
			}
		});	
		
		//长按列表项显示菜单
		msgList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int pos, long id) {
				
				//设置对话框的创建属性
				AlertDialog.Builder builder = new AlertDialog.Builder(FriendsMainActivity.this);
				builder.setTitle("请选择");
				builder.setItems(R.array.msgmenu, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						switch(which) {
						
						case MSG_MENU_CLEAR:
							//TODO:显示“正在删除”
							TextView tv_name = (TextView)view.findViewById(R.id.tv_msgitem_name);
							String delname = tv_name.getText().toString();							
							SharedPreferences share = getSharedPreferences(UserLoginActivity.SharedName,
									Context.MODE_PRIVATE);
							String userid = share.getString("uid","") + "@" + XmppTool.getServer();
							MessageStore store = new MessageStore(FriendsMainActivity.this);
							store.deleteNewlist(delname, userid);
							store.closeDB();
							getNewMsgData(); //重新获取数据
							mAdapter.notifyDataSetChanged(); //数据改变
							System.out.println("删除");
							break;
						}
					}
				});
				AlertDialog alertDialog = builder.create(); //创建对话框
				alertDialog.setCanceledOnTouchOutside(true); //点击对话框外部则消失
				//alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //对话框无标题栏
				alertDialog.show();
				
				return false;
			}
		});
	}
	
	/*
	 * 获取好友列表数据
	 */
	@SuppressWarnings("unchecked")
	private void getFriendsListData() {
		
		tempNames.clear();
		friendsNamesList.clear(); //使用List才能监听到动态变化
		Map<String,String> tempfriends = new HashMap<String,String>();
		Map<String,String> temppresence = new HashMap<String,String>();
		Roster roster = XmppTool.getConnection().getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) 
		{
		     Presence presence = roster.getPresence(entry.getUser());			     
		     tempfriends = new HashMap<String,String>();
		     if(presence.isAvailable() == true)
		     {
		    	 tempfriends.put("name", entry.getUser().toString());
		    	 tempfriends.put("presence", "yes");
		    	 temppresence.put(entry.getUser().toString(),"yes");
		     }
		     else
		     {
		    	 tempfriends.put("name", entry.getUser().toString());
		    	 tempfriends.put("presence", "no");
		    	 temppresence.put(entry.getUser().toString(),"no");
		     }
		     tempNames.add(tempfriends);
		}
		friendsNames = new String[tempNames.size()];
		for(int i=0;i<tempNames.size();i++)
		{
			friendsNames[i]=tempNames.get(i).get("name");
			System.out.println(friendsNames[i]);
		}
		Arrays.sort(friendsNames, new PinyinComparator()); //按姓名拼音排序
		
		//将排序完成后的好友名及状态存入到List中
		System.out.println(friendsNamesList.size());
		for(int j=0;j<friendsNames.length;j++)
		{
			String presence = temppresence.get(friendsNames[j]);
			System.out.println(presence);
			if(presence.equals("yes"))
			{
				tempfriends = new HashMap<String,String>();
				tempfriends.put("name", friendsNames[j]);
				tempfriends.put("presence", "在线");
				friendsNamesList.add(tempfriends);
			}
			if(presence.equals("no"))
			{
				tempfriends = new HashMap<String,String>();
				tempfriends.put("name", friendsNames[j]);
				tempfriends.put("presence", "离线");
				friendsNamesList.add(tempfriends);
			}
		}
		System.out.println(friendsNamesList.size());
		/*mfriendsList.clear();
		FriendsListAdapter.friends tempfriends;
		Roster roster = XmppTool.getConnection().getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) 
		{
		     Presence presence = roster.getPresence(entry.getUser());			     
		     tempfriends = new FriendsListAdapter.friends();
		     if(presence.isAvailable() == true)
		     {
		    	 tempfriends.username = entry.getUser().toString();
		    	 tempfriends.presence = true;
		     }
		     else
		     {
		    	 tempfriends.username = entry.getUser().toString();
		    	 tempfriends.presence = false;
		     }
		     mfriendsList.add(tempfriends);
		}
		friendsNames = new String[mfriendsList.size()];
		for(int i=0;i<mfriendsList.size();i++)
		{
			this.friendsNames[i]=mfriendsList.get(i).username;
		}
		Arrays.sort(friendsNames, new PinyinComparator());
		System.out.println("排序完成");*/
	}
	
	/*
	 * 更新好友列表视图
	 */
	private void getFriendsListView() {

		//为视图注册长按上下文菜单
		//registerForContextMenu(friendsList);
		//friendsList.setAdapter(new FriendsListAdapter(this));

		friendsListIndexbar.setListView(friendsList);
		mDialogText.setVisibility(View.INVISIBLE);
		
		//监听好友列表项单击操作，显示菜单
		friendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				final TextView tv_name = (TextView)view.findViewById(R.id.tv_frienditem_name);

				//设置对话框创建属性
				AlertDialog.Builder builder = new AlertDialog.Builder(FriendsMainActivity.this);
				builder.setTitle("请选择");
				builder.setItems(R.array.contactsmenu, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {
						
						case CONTACTS_MENU_INFO:
							//TODO:FriendInfoActivity
							//Intent intent0 = new Intent(FriendsMainActivity.this, 
								//FriendInfoActivity.class);
							//intent0.putExtra("friendName", tv_name.getText());
							//startActivity(intent0);
							System.out.println("好友资料");
							break;
							
						case CONTACTS_MENU_SEND:
							Intent intent1 = new Intent(FriendsMainActivity.this, 
									FriendChatActivity.class);
							intent1.putExtra("friendName", tv_name.getText());
							startActivity(intent1);
							System.out.println("发送消息");
							break;
							
						case CONTACTS_MENU_DELETE:
							
							//TODO 1、删除item项。2、解除好友关系
							
							System.out.println("删除好友");
							break;
						}
					}
				});
				
				AlertDialog alertDialog = builder.create(); //创建对话框
				alertDialog.setCanceledOnTouchOutside(true); //点击对话框外部则消失
				//alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //对话框无标题栏
				alertDialog.show();		
			}
		});
		
		//监听好友列表项长按操作，显示上下文菜单
		/*friendsList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				TextView tv_name = (TextView)arg1.findViewById(R.id.tv_frienditem_name);
				Toast.makeText(getApplicationContext(), 
						tv_name.getText(), Toast.LENGTH_SHORT).show();				
				friendsList.showContextMenu();
				return true;
			}
			
		});*/
		
		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        friendsListIndexbar.setTextView(mDialogText);
	}

	/*
	 * Tab页卡视图初始化
	 */
	private void initViewPager() {
		mTabPager = (ViewPager)findViewById(R.id.im_tabPager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		mTabCursor = (ImageView)findViewById(R.id.iv_tabselected);
		mTabMsg = (ImageView)findViewById(R.id.iv_msgtab);
		mTabContacts = (ImageView)findViewById(R.id.iv_contactstab);
		btnMsg = (LinearLayout)findViewById(R.id.ll_msgtab);
		btnContacts = (LinearLayout)findViewById(R.id.ll_contactstab);
		
		btnMsg.setOnClickListener(new MyOnClickListener(0));
		btnContacts.setOnClickListener(new MyOnClickListener(1));
		
		//初始化横条动画图片
		initCursorImage();
		
		//装入分页页卡数据
		LayoutInflater mLI = LayoutInflater.from(this);
		View view0 = mLI.inflate(R.layout.im_page_msg, null);
		View view1 = mLI.inflate(R.layout.im_page_contacts, null);
		
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view0);
		views.add(view1);
		
		//填充ViewPager的数据适配器
		mPagerAdapter = new PagerAdapter() {

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View v = null;
				v = views.get(position);
				((ViewPager)container).addView(v);
				
				switch(position) {
				//ViewPager的消息列表页卡初始化化
				case TAB_MESSAGE:

					msgList = (ListView)v.findViewById(R.id.lv_newMsgs); //获取其中的listview
					getNewMsgData(); //获取数据
					getNewMsgView();
									
					//设置适配器
					mAdapter = new NewMsgListAdapter(FriendsMainActivity.this, mNewMsg);
					msgList.setAdapter(mAdapter);
					break;	
					
				//ViewPager的通讯录页卡初始化化
				case TAB_CONTACTS:
					
					friendsList = (ListView)v.findViewById(R.id.lv_friends);
					friendsListIndexbar = (SideBar)v.findViewById(R.id.friendslist_sideBar);
					mDialogText = (TextView)LayoutInflater.from(FriendsMainActivity.this)
							.inflate(R.layout.im_friendslist_pos, null);
					addFriend = (ImageButton)v.findViewById(R.id.btn_addfriend);		
					addFriend.setOnClickListener(addFriendListener);
					
					getFriendsListData();
					getFriendsListView();

					fAdapter = new FriendsListAdapter(FriendsMainActivity.this, friendsNamesList);
					friendsList.setAdapter(fAdapter);
					break;
				}				
				return views.get(position);
			}

			@Override
			public int getItemPosition(Object object) {
				return POSITION_NONE;
			}

			@Override
			public void setPrimaryItem(ViewGroup container, int position, Object object) {
				switch(position) {
				case TAB_MESSAGE:
					getNewMsgData(); //重新获取数据
					mAdapter.notifyDataSetChanged(); //数据改变
					break;
					
				case TAB_CONTACTS:
					getFriendsListData();
					fAdapter.notifyDataSetChanged();
					break;
				}
				super.setPrimaryItem(container, position, object);
			}			
		};
		mTabPager.setAdapter(mPagerAdapter);
	}
	
	/*
	 * 初始化动画图片
	 */
	private void initCursorImage() {
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.im_tab_bg)
				.getWidth();
		
		//获取屏幕分辨率
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm); 
		int displayWidth = dm.widthPixels;
		
		//调整动画图片显示位置
		offset = (displayWidth/2 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		mTabCursor.setImageMatrix(matrix);
	}
	
	/*
	 * 添加好友按钮监听器
	 */
	private OnClickListener addFriendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(FriendsMainActivity.this, FriendSearchActivity.class);
			startActivity(intent);
		}
	};

	/*
	 * 底部按钮点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {

		private int index = 0;
		
		public MyOnClickListener(int i) {
			index = i;
		}
		
		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	}
	
	/*
	 * Tab页卡切换监听器
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW; //页卡1->页卡2偏移量
				
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			//mPagerAdapter.notifyDataSetChanged();
			switch(arg0) {
			
			case TAB_MESSAGE:
				//getNewMsgListView(view0);
				//mAdapter.notifyDataSetChanged();
				mTabMsg.setImageDrawable(getResources().getDrawable(R.drawable.im_tab_msg_pressed));
				if(currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTabContacts.setImageDrawable(getResources()
							.getDrawable(R.drawable.im_tab_contacts));
				}
				break;
				
			case TAB_CONTACTS:
				mTabContacts.setImageDrawable(getResources()
						.getDrawable(R.drawable.im_tab_contacts_pressed));
				if(currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
					mTabMsg.setImageDrawable(getResources().getDrawable(R.drawable.im_tab_msg));
				}
				break;
			}
			
			currIndex = arg0;
			animation.setFillAfter(true); //TRUE:图片停在动画结束位置
			animation.setDuration(150);
			mTabCursor.startAnimation(animation);
		}
		
	}

	/*
	 * 创建上下文菜单
	 */
	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
	
		//手动添加上下文菜单选项
		//menu.setHeaderTitle("请选择操作");
		//menu.add(0, 0, 0, "发送消息");
		//menu.add(0, 1, 0, "查看资料");
		//menu.add(0, 2, 0, "删除好友");
		//menu.add(0, 3, 0, "清空记录");
		
		//通过xml文件配置上下文菜单
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.friendslistmenu, menu);
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}*/

	/*
	 * 长按上下文菜单
	 */
	/*@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		//当前被选择的视图项的信息
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		TextView tv_name = (TextView)menuInfo.targetView.findViewById(R.id.tv_frienditem_name);
		Toast.makeText(this, tv_name.getText().toString(), Toast.LENGTH_SHORT).show();	
		
		//上下文菜单选择项的操作
		switch(item.getItemId()) {	
		
		case FLIST_CONTEXTMENU_SEND:  //发送消息
			Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
			//System.out.println(item.getItemId());
			break;
		case FLIST_CONTEXTMENU_INFO:  //查看资料
			Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
			//System.out.println(item.getItemId());
			break;
		case FLIST_CONTEXTMENU_DELETE:  //删除好友
			Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
			//System.out.println(item.getItemId());
			break;
		case FLIST_CONTEXTMENU_CLEAR:  //清空记录
			Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
			//System.out.println(item.getItemId());
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}*/

	/*
	 * 关闭上下文菜单
	 */
	/*@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
	}*/

	//private static String[] testNames = {"阿里","baidu","ali","Ali","Baidu","度娘","谷哥","企鹅","1234",
	//"北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
	
	/*
	private List<Map<String, Object>> getData() 
	{
		Roster roster = XmppTool.getConnection().getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (RosterEntry entry : entries) 
		{
		     Presence presence = roster.getPresence(entry.getUser());
		     
		     map = new HashMap<String, Object>();
		     map.put("name", entry.getUser().toString());
		     if(presence.isAvailable() == true)
		     {
		    	 map.put("status", "在线");
		     }
		     else
		     {
		    	 map.put("status", "离线");
		     }
		     list.add(map);
		}
		
		map = new HashMap<String, Object>();
		map.put("name", "G1");
		map.put("status", "在线");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "谷哥");
		map.put("status", "离线");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "度娘");
		map.put("status", "在线");
		list.add(map);
		
		return list;
	}
	*/
}
