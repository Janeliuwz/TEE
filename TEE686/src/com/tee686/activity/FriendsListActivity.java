package com.tee686.activity;

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
import com.tee686.im.PinyinUtil;
import com.tee686.im.PinyinComparator;
import com.tee686.im.SideBar;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
//import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FriendsListActivity extends Activity{

	private Button addFriend;
	private ListView friendsList;
	private WindowManager mWindowManager;
	private SideBar friendsListIndexbar;
	private TextView mDialogText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_friendslist);
		initControl();
		getFriendsListView();
	}
		
	/*
	 * 更新好友列表视图
	 */
	private void getFriendsListView() {
		
		friendsList = (ListView)findViewById(R.id.lv_friends);
		friendsList.setAdapter(new FriendsListAdapter(this));
		friendsListIndexbar = (SideBar)findViewById(R.id.friendslist_sideBar);
		friendsListIndexbar.setListView(friendsList);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.im_friendslist_pos, null);
		mDialogText.setVisibility(View.INVISIBLE);
		
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
	
	/*
	 * 句柄初始化操作
	 */
	private void initControl() {
		
		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		addFriend = (Button)findViewById(R.id.btn_addfriend);		
		addFriend.setOnClickListener(addFriendListener);
		
		/*
		SimpleAdapter adapter = new SimpleAdapter(this, 
				getData(), 
				R.layout.im_friendslist_item,
				new String[]{"avatar", "name", "status"},
				new int[]{R.id.iv_frienditem_avatar, R.id.tv_frienditem_name, R.id.tv_frienditem_status}
		);
		friendlist.setAdapter(adapter);
		*/
	}
	
	/*
	 * 监听器
	 */
	private OnClickListener addFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(FriendsListActivity.this, FriendSearchActivity.class);
			startActivity(intent);
		}
		
	};

	/*
	 * 自定义好友列表ListView适配器
	 * 实现快速滚动索引接口SectionIndexer
	 * TODO:完成对好友名称的获取
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
			TextView tvStatus; //状态ni y
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
