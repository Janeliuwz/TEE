package com.tee686.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import com.tee686.xmpp.XmppTool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.casit.tee686.R;
import com.tee686.config.Urls;
import com.tee686.entity.UserInfoItem;
import com.tee686.https.HttpUtils;
import com.tee686.https.NetWorkHelper;
import com.tee686.indicator.PageIndicator;
import com.tee686.sqlite.MessageStore;
import com.tee686.ui.base.BaseFragmentActivity;
import com.tee686.utils.IntentUtil;
import com.tee686.view.UserCollectFragment;
import com.tee686.view.UserIntroFragment;
import com.tee686.view.UserLogOutFragment;

public class UserCenterActivity extends BaseFragmentActivity implements
		OnClickListener {

	public static String UID = "uid";
	private Button mCommunity;
	private Button ref_buButton;
	private Button friendsList;
	private String result = "";

	ViewPager mViewPager;
	TabPageAdapter mTabsAdapter;
	PageIndicator mIndicator;
	LinearLayout llGoHome;
	private LinearLayout loadLayout;
	private LinearLayout loadFaillayout;

	ImageView ImgLeft;
	ImageView ImgRight;

	private SharedPreferences share;

	private UserInfoItem mUserInfoItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_activity);
		initControl();
		share = getSharedPreferences(UserLoginActivity.SharedName,
				Context.MODE_PRIVATE);
		if (savedInstanceState != null) {
			try {
				mUserInfoItem = new ObjectMapper().readValue(
						savedInstanceState.getString("json"),
						UserInfoItem.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loadLayout.setVisibility(View.GONE);
			mTabsAdapter = new TabPageAdapter(this);
			mViewPager.setAdapter(mTabsAdapter);
			mIndicator.setViewPager(mViewPager);
			if (mUserInfoItem == null) {
				UserLogOutFragment fragment = new UserLogOutFragment(
						UserCenterActivity.this, true);

				mTabsAdapter.addTab(
						getString(R.string.user_center_get_info_error),
						fragment);
				return;
			}
			
			mTabsAdapter.addTab(getString(R.string.user_center_my_Collect),
					new UserCollectFragment(UserCenterActivity.this));			 
			mTabsAdapter.addTab(getString(R.string.user_center_my_Intro),
					new UserIntroFragment(mUserInfoItem));
			mTabsAdapter.addTab(getString(R.string.user_center_exit),
					new UserLogOutFragment(UserCenterActivity.this, false));
			
			mTabsAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(0);
		} else if (!NetWorkHelper.checkNetState(this)) {
			loadLayout.setVisibility(View.GONE);
			loadFaillayout.setVisibility(View.VISIBLE);
		}
		else {
			initViewPager();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("json", result);
	}

	// [start]初始化
	private void initControl() {
		ImgLeft = (ImageView) findViewById(R.id.imageview_user_left);
		ImgRight = (ImageView) findViewById(R.id.imageview_user_right);
		mCommunity = (Button) findViewById(R.id.btn_community);		
		mCommunity.setOnClickListener(new myOnClickListener());
		ref_buButton = (Button) findViewById(R.id.bn_refresh);
		ref_buButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ContentAsyncTask().execute(String.format(Urls.USER_INFO, share.getString(UID, "")));
			}
		});
		friendsList = (Button)findViewById(R.id.btn_friendsList);
		friendsList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserCenterActivity.this, FriendsMainActivity.class);
				startActivity(intent);
			}
			
		});
		mViewPager = (ViewPager) findViewById(R.id.user_pager);
		mViewPager.setOffscreenPageLimit(2);
		mIndicator = (PageIndicator) findViewById(R.id.user_indicator);
		mIndicator
				.setOnPageChangeListener(new IndicatorOnPageChangedListener());
		llGoHome = (LinearLayout) findViewById(R.id.Linear_above_toHome);
		llGoHome.setOnClickListener(this);
		loadLayout = (LinearLayout) findViewById(R.id.view_loading);
		loadFaillayout = (LinearLayout) findViewById(R.id.view_load_fail);
		
		//开启消息监听
		ChatManager cm = XmppTool.XMPPgetChatManager();
	    cm.addChatListener(new ChatManagerListener() 
	    	{
				@Override
				public void chatCreated(Chat chat, boolean able) 
				{
					chat.addMessageListener(new MessageListener() {
						@Override
						public void processMessage(Chat chat2, Message message)
						{
							String from = message.getFrom();
							String to = message.getTo();
							String friendId=null;
							String userId=null;
							if(from.contains("/"))
							{
								friendId = from.substring(0,from.lastIndexOf("/"));
							}
							else
							{
								friendId = from;
							}
							if(to.contains("/"))
							{
								userId = to.substring(0,to.lastIndexOf("/"));	
							}
							else
							{
								userId = to;
							}
							//System.out.println(friendId);
							//System.out.println(userId);
							//System.out.println(message.getBody());
							
							Map<String,String> msg = new HashMap<String,String>();
							msg.put("to", userId);
							msg.put("from", friendId);
							msg.put("content", message.getBody());
							msg.put("ifread","no");
							msg.put("uid", userId);
							
							//存入数据库
							MessageStore store = new MessageStore(UserCenterActivity.this);
							long result = 0;
							if((result = store.insertMessagelist(msg))!=-1)
							{
								//System.out.println(result);
							}
							else
							{
								System.out.println("插入数据库失败");
							}
							store.closeDB();
							
							//发送广播通知更新聊天页面与通讯录页面内容
							Intent intent = new Intent("com.tee686.activity.FriendChatActivity");
							intent.putExtra("content", message.getBody());
							intent.putExtra("friendId", friendId);
							intent.putExtra("userid",userId);
					        sendBroadcast(intent);
					        
					        Intent newmsgintent = new Intent("com.tee686.activity.FriendsListActivity");
					        sendBroadcast(newmsgintent);
						}
					});
				}
			}
	    );
	     //监听prensence包  
		  PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));  
	      PacketListener listener = new PacketListener() {  
	
	          @Override  
	          public void processPacket(Packet packet) 
	          {	              
	              //看API可知道   Presence是Packet的子类  
	              if (packet instanceof Presence)
	              { 
	            	  System.out.println(packet.toXML());
	                  Presence presence = (Presence) packet;  
	                  //Presence还有很多方法，可查看API   
	                  String from = presence.getFrom();//发送方  
	                  String to = presence.getTo();//接收方  
	                  //Presence.Type有7中状态  
	                  if (presence.getType().equals(Presence.Type.subscribe))
	                  {//好友申请  
	                	   
	                  }
	                  else if (presence.getType().equals(Presence.Type.subscribed))
	                  {//同意添加好友  
	                        
	                  } 
	                  else if (presence.getType().equals(Presence.Type.unsubscribe))
	                  {//拒绝添加好友  和  删除好友  
	                        
	                  } 
	                  else if (presence.getType().equals(Presence.Type.unsubscribed)) 
	                  {//这个我没用到  
	                  	
	                  }
	                  else if (presence.getType().equals(Presence.Type.unavailable))
	                  {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表  
	                        
	                  } 
	                  else 
	                  {//好友上线  
	                        
	                  }  
	              }  
	          }  
	      };
	      
	      XmppTool.getConnection().addPacketListener(listener, filter); 
	      Roster roster = XmppTool.getConnection().getRoster();
	  	  roster.addRosterListener(
	  			  new RosterListener() {     
	                    @Override    
	                    //监听好友申请消息  
	                    public void entriesAdded(Collection<String> invites) {    
	                        // TODO Auto-generated method stub    
	                        System.out.println("监听到好友申请的消息是："+invites);   
	                       for (Iterator iter = invites.iterator(); iter.hasNext();) {  
	                              String fromUserJids = (String)iter.next();  
	                              System.out.println("fromUserJids是："+fromUserJids);
	                        }                         
	                       }      
	                    @Override    
	                    //监听好友同意添加消息  
		                public void entriesUpdated(Collection<String> invites) {    
	                           // TODO Auto-generated method stub    
	                          System.out.println("监听到好友同意的消息是："+invites);    
	                          for (Iterator iter = invites.iterator(); iter.hasNext();) {  
	                             String fromUserJids = (String)iter.next(); 
	                             System.out.println("同意添加的好友是："+fromUserJids);   
	                            }                        
	                          }   
	                   @Override    
	                    //监听好友删除消息  
	                    public void entriesDeleted(Collection<String> delFriends) {    
	                       // TODO Auto-generated method stub    
	                       System.out.println("监听到删除好友的消息是："+delFriends);
	                       }   
	                   @Override    
	                   //监听好友状态改变消息  
	                    public void presenceChanged(Presence presence) {    
	                        // TODO Auto-generated method stub 
	                	   }
	                   }); 
	}


	private void initViewPager() {
		mTabsAdapter = new TabPageAdapter(this);
		mViewPager.setAdapter(mTabsAdapter);
		mIndicator.setViewPager(mViewPager);
		String url = String.format(Urls.USER_INFO, share.getString(UID, ""));
		new ContentAsyncTask().execute(url);
	}

	// [end]
	public class TabPageAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mFragments;
		public List<String> tabs = new ArrayList<String>();

		/*private final int[] COLORS = new int[] { R.color.red, R.color.green,
				R.color.blue, R.color.white, R.color.black };*/

		public TabPageAdapter(UserCenterActivity userCenterActivity) {
			super(userCenterActivity.getSupportFragmentManager());
			mFragments = new ArrayList<Fragment>();

		}

		public void addTab(String title, Fragment fragment) {
			mFragments.add(fragment);
			tabs.add(title);
			notifyDataSetChanged();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabs.get(position);
		}

		@Override
		public Fragment getItem(int arg0) {
			return mFragments.get(arg0);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

	}

	public class ContentAsyncTask extends AsyncTask<String, Void, UserInfoItem> {		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadLayout.setVisibility(View.VISIBLE);
		}

		@Override
		protected UserInfoItem doInBackground(String... params) {
			try {
				result = HttpUtils.getByHttpClient(UserCenterActivity.this, params[0]);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				mUserInfoItem = new ObjectMapper().readValue(result,
						UserInfoItem.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mUserInfoItem;		
			
		}

		@Override
		protected void onPostExecute(UserInfoItem result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadLayout.setVisibility(View.GONE);
			if (result == null) {
				
				UserLogOutFragment fragment = new UserLogOutFragment(
						UserCenterActivity.this, true);

				mTabsAdapter.addTab(
						getString(R.string.user_center_get_info_error),
						fragment);
				return;
			}
			
			mTabsAdapter.addTab(getString(R.string.user_center_my_Collect),
					new UserCollectFragment(UserCenterActivity.this));			 
			mTabsAdapter.addTab(getString(R.string.user_center_my_Intro),
					new UserIntroFragment(result));
			mTabsAdapter.addTab(getString(R.string.user_center_exit),
					new UserLogOutFragment(UserCenterActivity.this, false));
			
			mTabsAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(1);
		}
	}

	private class IndicatorOnPageChangedListener implements
			OnPageChangeListener {

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
			switch (arg0) {
			
			case 0: 
				ImgLeft.setVisibility(8);				
				break;
			case 2:
				ImgRight.setVisibility(8);				
				break;
			default:
				ImgLeft.setVisibility(0);
				ImgRight.setVisibility(0);
			}
		}

	}

	private class myOnClickListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_community:
				IntentUtil.start_activity(UserCenterActivity.this, BulletinActivity.class);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Linear_above_toHome:
			finish();
			break;
		}
	}

}
