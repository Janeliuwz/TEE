package com.tee686.activity;

import java.util.Iterator;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.casit.tee686.R;
import com.tee686.xmpp.XmppTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FriendAddActivity extends Activity{

	private String username;
	
	private OnClickListener addFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Roster roster = XmppTool.getConnection().getRoster();
			try 
			{
				roster.createEntry(username, null, null);
			} 
			catch (XMPPException e)
			{
				Toast.makeText(getApplicationContext(), "添加好友失败", Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(getApplicationContext(), "添加好友成功", Toast.LENGTH_SHORT).show();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_addfriend);
		
		username = getIntent().getStringExtra("USERID");
		TextView userText = (TextView)findViewById(R.id.add_f_username);
		userText.setText(username);
		
		Button btn=(Button)findViewById(R.id.add_f_submit);
		btn.setOnClickListener(addFriendListener);
	}

}
