package com.tee686.activity;

import com.casit.tee686.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

public class FriendSearchActivity extends Activity{
	
	private ListView userlist;
	private void initControl() {
		userlist = (ListView)findViewById(R.id.search_friend_list);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_searchfriend);
		initControl();
	}

}
