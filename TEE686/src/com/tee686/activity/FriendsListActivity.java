package com.tee686.activity;

import com.casit.tee686.R;
import com.unionpay.mpay.views.r;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FriendsListActivity extends Activity{

	/*
	 * 初始化系列操作
	 */
	private Button addFriend;
	private void initControl() {
		addFriend = (Button)findViewById(R.id.btn_addfriend);
		addFriend.setOnClickListener(addFriendListener);
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
