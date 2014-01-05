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
import com.tee686.im.NewMsgListAdapter;
import com.tee686.im.PinyinUtil;
import com.tee686.im.PinyinComparator;
import com.tee686.im.SideBar;
import com.tee686.sqlite.MessageStore;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
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
	private ViewPager mTabPager;
	private ImageView mTabSelected; //tab按钮选中时底端动画图片
	private ImageView mTabMsg, mTabContacts;
	private LinearLayout btnMsg, btnContacts;
	private static final int TAB_MESSAGE = 0;
	private static final int TAB_CONTACTS = 1;
	
	private int currIndex = 0; //当前页卡编号
	private int zero = 0; //动画图片偏移量
	private int one; //动画图片位移
	
	private View layout;
	private LayoutInflater inflater;
	
	private Button addFriend;
	private ListView friendsList;
	private ListView msgList;
	private WindowManager mWindowManager;
	private SideBar friendsListIndexbar;
	private TextView mDialogText;
	private List<Map<String,String>> mNewMsg = new ArrayList<Map<String,String>>();
	private NewMsgListAdapter mAdapter;
	
	//上下文菜单选项
	private static final int FLIST_CONTEXTMENU_SEND = Menu.FIRST;
	private static final int FLIST_CONTEXTMENU_INFO = Menu.FIRST + 1;
	private static final int FLIST_CONTEXTMENU_DELETE = Menu.FIRST + 2;
	private static final int FLIST_CONTEXTMENU_CLEAR = Menu.FIRST + 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_main);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
				
		initViewPager();
		
		//initControl();
		
		//获取新消息数据
		//getNewMsgListView();
		
		//设置新消息listview的适配器
		//mAdapter = new NewMsgListAdapter(this, mNewMsg);
		//newMsgList.setAdapter(mAdapter);
		
		//好友列表信息
		//getFriendsListView();
		
		//注册广播接收器
		//IntentFilter intentFilter = new IntentFilter("com.tee686.activity.FriendsListActivity");
		//registerReceiver(mReceiver, intentFilter);
	}	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//获取新消息数据
		//getNewMsgListView();
		
		//更新新消息列表
		//mAdapter.notifyDataSetChanged();
		
		//注册广播接收器
		//IntentFilter intentFilter = new IntentFilter("com.tee686.activity.FriendsListActivity");
		//registerReceiver(mReceiver, intentFilter);
	}
	
	@Override
	public void onBackPressed() 
	{
		//注销广播监听器
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
			
			//获取新消息数据
			//getNewMsgListView();
			
			//更新新消息列表
			mAdapter.notifyDataSetChanged();
		}
	};

	/*
	 * 获取新消息列表视图
	 */
	/*private void getNewMsgListView()
	{
		mNewMsg.clear();
		newMsgList = (ListView)findViewById(R.id.lv_newMsgs);
		Map<String,String> tempid = new HashMap<String,String>();
		SharedPreferences share = getSharedPreferences(UserLoginActivity.SharedName,
				Context.MODE_PRIVATE);
		
		//当前user的jid
		String userid = share.getString("uid","") + "@" + XmppTool.getServer();
		newMsgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				
				unregisterReceiver(mReceiver);
				TextView tv_name = (TextView)view.findViewById(R.id.tv_msgitem_name);
				Toast.makeText(getApplicationContext(), 
						tv_name.getText(), Toast.LENGTH_SHORT).show();
				
				//跳转到聊天界面
				Intent intent = new Intent(FriendsMainActivity.this, FriendChatActivity.class);
				intent.putExtra("friendName", tv_name.getText());
				startActivity(intent);
			}
			
		});
		
		//查询数据库获取新消息
		MessageStore store = new MessageStore(FriendsMainActivity.this);
		Cursor newMsgcursor = store.selectNewmsg(userid);
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
		}
		newMsgcursor.close();
		store.closeDB();
	}*/
		
	/*
	 * 更新好友列表视图
	 */
/*	private void getFriendsListView() {
						
		friendsList = (ListView)findViewById(R.id.lv_friends);
		
		//为视图注册长按上下文菜单
		registerForContextMenu(friendsList);
		friendsList.setAdapter(new FriendsListAdapter(this));
		friendsListIndexbar = (SideBar)findViewById(R.id.friendslist_sideBar);
		friendsListIndexbar.setListView(friendsList);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.im_friendslist_pos, null);
		mDialogText.setVisibility(View.INVISIBLE);
		
		//单击好友列表项
		friendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				TextView tv_name = (TextView)view.findViewById(R.id.tv_frienditem_name);
				Toast.makeText(getApplicationContext(), 
						tv_name.getText(), Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(FriendsMainActivity.this, FriendChatActivity.class);
				intent.putExtra("friendName", tv_name.getText());
				startActivity(intent);
			}
			
		});
		//监听好友列表项长按操作，显示上下文菜单
		friendsList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView tv_name = (TextView)arg1.findViewById(R.id.tv_frienditem_name);
				Toast.makeText(getApplicationContext(), 
						tv_name.getText(), Toast.LENGTH_SHORT).show();				
				friendsList.showContextMenu();
				return true;
			}
			
		});
		
		//TODO
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        friendsListIndexbar.setTextView(mDialogText);
	}
*/	
	/*
	 * 创建上下文菜单
	 */
/*	@Override
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
	}
*/
	/*
	 * 长按上下文菜单
	 */
/*	@Override
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
	}
*/
	/*
	 * 关闭上下文菜单
	 */
/*	@Override
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);
	}
*/

	/*
	 * Tab页卡视图初始化
	 */
	private void initViewPager() {
		mTabPager = (ViewPager)findViewById(R.id.im_tabPager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		mTabSelected = (ImageView)findViewById(R.id.iv_tabselected);
		mTabMsg = (ImageView)findViewById(R.id.iv_msgtab);
		mTabContacts = (ImageView)findViewById(R.id.iv_contactstab);
		btnMsg = (LinearLayout)findViewById(R.id.ll_msgtab);
		btnContacts = (LinearLayout)findViewById(R.id.ll_contactstab);
		
		btnMsg.setOnClickListener(new MyOnClickListener(0));
		btnContacts.setOnClickListener(new MyOnClickListener(1));
		
		//获取屏幕分辨率
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm); 
		int displayWidth = dm.widthPixels;
		//int displayHeight = dm.heightPixels;
		one = displayWidth/2;
		
		//装入分页页卡数据
		LayoutInflater mLI = LayoutInflater.from(this);
		View view0 = mLI.inflate(R.layout.im_page_msg, null);
		View view1 = mLI.inflate(R.layout.im_page_contacts, null);
		
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view0);
		views.add(view1);
		
		//填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

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
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
			
		};
		
		mTabPager.setAdapter(mPagerAdapter);
	}
	
	/*
	 * 句柄初始化操作
	 */
/*	private void initControl() {
		
		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		addFriend = (Button)findViewById(R.id.btn_addfriend);		
		addFriend.setOnClickListener(addFriendListener);
		

		//SimpleAdapter adapter = new SimpleAdapter(this, 
		//		getData(), 
		//		R.layout.im_friendslist_item,
		//		new String[]{"avatar", "name", "status"},
		//		new int[]{R.id.iv_frienditem_avatar, R.id.tv_frienditem_name, R.id.tv_frienditem_status}
		//);
		friendlist.setAdapter(adapter);

	}
*/
	
	/*
	 * 监听器
	 */
/*	private OnClickListener addFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(FriendsMainActivity.this, FriendSearchActivity.class);
			startActivity(intent);
		}
	};
*/

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

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = null;
			switch(arg0) {
			case TAB_MESSAGE:
				mTabMsg.setImageDrawable(getResources().getDrawable(R.drawable.im_tab_msg_pressed));
				if(currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTabContacts.setImageDrawable(getResources().getDrawable(R.drawable.im_tab_contacts));
				}
				break;
			case TAB_CONTACTS:
				mTabContacts.setImageDrawable(getResources().getDrawable(R.drawable.im_tab_contacts_pressed));
				if(currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTabMsg.setImageDrawable(getResources().getDrawable(R.drawable.im_tab_msg));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true); //TRUE:图片停在动画结束位置
			animation.setDuration(150);
			mTabSelected.startAnimation(animation);
		}
		
	}

	/*
	 * 自定义好友列表ListView适配器
	 * 实现快速滚动索引接口SectionIndexer
	 */
	static class friends
	{
		String username;
		Boolean presence;
	};
	
	//private static String[] testNames = {"阿里","baidu","ali","Ali","Baidu","度娘","谷哥","企鹅","1234","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
	private static List<friends> list = new ArrayList<friends>();
	static class FriendsListAdapter extends BaseAdapter implements SectionIndexer {

		private Context mContext;
		private String[] mNames;
		
		static class ViewHolder {
			TextView tvCatalog; //目录
			ImageView ivAvatar; //头像
			TextView tvName; //用户名
			TextView tvStatus; //状态
		}
		
		@SuppressWarnings("unchecked")
		public FriendsListAdapter(Context mContext) {
			this.mContext = mContext;
			//TODO:完成对好友名称的获取
			list.clear();
			friends tempfriends;
			Roster roster = XmppTool.getConnection().getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) 
			{
			     Presence presence = roster.getPresence(entry.getUser());			     
			     tempfriends = new friends();
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
			     list.add(tempfriends);
			}
			mNames = new String[list.size()];
			for(int i=0;i<list.size();i++)
			{
				this.mNames[i]=list.get(i).username;
			}
			//this.mNames = testNames;
			Arrays.sort(mNames,new PinyinComparator());
			System.out.println("排序完成");
		}
		
		@Override
		public int getCount() {
			return mNames.length;
		}

		@Override
		public Object getItem(int position) {
			return mNames[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			final String username = mNames[position];
			String catalog = PinyinUtil.getPingYin(username).toUpperCase().substring(0, 1);
			
			//填充视图，不为空时直接使用
			if(convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.im_friendslist_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.tv_frienditem_catalog);
				viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.iv_frienditem_avatar);
				viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_frienditem_name);
				viewHolder.tvStatus = (TextView)convertView.findViewById(R.id.tv_frienditem_status);
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			//设置字母索引
			if(position == 0) {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			}
			else {
				String lastCatalog = PinyinUtil.getPingYin(mNames[position-1]).toUpperCase().substring(0, 1);
				if(catalog.equals(lastCatalog)) {
					viewHolder.tvCatalog.setVisibility(View.GONE);
				}
				else {
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
			}
			
			//TODO:头像
			//viewHolder.ivAvatar.setImageResource();
			viewHolder.tvName.setText(username);
			//TODO:状态
			Boolean presenceStatu = findpresenceStatubyUsername(username);
			if(presenceStatu == true)
			{
				viewHolder.tvStatus.setText("在线");
			}
			else
			{
				viewHolder.tvStatus.setText("离线");
			}
			
			return convertView;
		}

		//获取用户名所属字母索引
		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < mNames.length; i++) {
	            //String l = PinyinUtil.converterToFirstSpell(mNames[i]).substring(0, 1);
	            String l = PinyinUtil.getPingYin(mNames[i]).toUpperCase();
	            char firstChar = l.charAt(0);
	            if (firstChar == section) {  
	                return i;  
	            }
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
		
		public static Boolean findpresenceStatubyUsername(String username)
		{
			Boolean presenceStatu = true;
			for(int i=0;i<list.size();i++)
			{
				if(list.get(i).username.equals(username))
				{
					presenceStatu = list.get(i).presence;
					break;
				}
			}
			return presenceStatu;
		}
		
	}
	
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
