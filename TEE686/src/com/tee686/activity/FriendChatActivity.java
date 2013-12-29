package com.tee686.activity;

import com.casit.tee686.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class FriendChatActivity extends Activity{
	private String friendName;
	private TextView tv_titleName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Intent intent = getIntent();
		friendName = intent.getStringExtra("friendName");
		initControl();
	}

	private void initControl() {				
		tv_titleName = (TextView)findViewById(R.id.tv_chatTitleName);
		tv_titleName.setText(friendName);
	}
}
