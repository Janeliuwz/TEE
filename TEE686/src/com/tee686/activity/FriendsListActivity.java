package com.tee686.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import com.casit.tee686.R;
import com.unionpay.mpay.views.r;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FriendsListActivity extends Activity{

	/*
	 * 初始化系列操作
	 */
	private Button addFriend;
	private ListView friendlist;
	
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
		     map.put("title", entry.getUser().toString());
		     if(presence.isAvailable() == true)
		     {
		    	 map.put("info", "在线");
		     }
		     else
		     {
		    	 map.put("info", "离线");
		     }
		     list.add(map);
		}
		
		map = new HashMap<String, Object>();
		map.put("title", "G1");
		map.put("info", "google 1");
		//map.put("img", R.drawable.i1);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G2");
		map.put("info", "google 2");
		//map.put("img", R.drawable.i2);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G3");
		map.put("info", "google 3");
		//map.put("img", R.drawable.i3);
		list.add(map);
		
		return list;
	}
	
	private void initControl() {
		addFriend = (Button)findViewById(R.id.btn_addfriend);
		friendlist = (ListView)findViewById(R.id.lv_friends);
		addFriend.setOnClickListener(addFriendListener);
		
		
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.vlist_lv_friend,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		
		friendlist.setAdapter(adapter);
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_friendslist);
		initControl();
	}

}
