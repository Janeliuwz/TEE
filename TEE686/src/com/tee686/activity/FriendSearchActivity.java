package com.tee686.activity;

import java.util.Iterator;

import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.tee686.xmpp.XmppTool;

import com.casit.tee686.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class FriendSearchActivity extends Activity{
	
	//private ListView userlist;
	private EditText username;
	private ImageButton searchButton;
	
	private OnClickListener searchFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String keyword = username.getText().toString();
			String value = "";
			try{
					UserSearchManager search = new UserSearchManager(XmppTool.getConnection());
					Form searchForm = search.getSearchForm("search."+ XmppTool.getConnection().getServiceName());
					Form answerForm = searchForm.createAnswerForm();
					answerForm.setAnswer("Username", true);
					answerForm.setAnswer("search", keyword);
					org.jivesoftware.smackx.ReportedData data = search.getSearchResults(answerForm, "search."+ XmppTool.getConnection().getServiceName());

					if (data.getRows() != null) {
						Boolean ishas = false;
						Iterator<Row> it = data.getRows();
						while (it.hasNext()) {
							Row row = it.next();
							Iterator iterator = row.getValues("jid");
							if (iterator.hasNext()) {
								value = iterator.next().toString();
								System.out.println("result:"+value);
								if(value.substring(0,value.lastIndexOf('@')).equals(keyword))
								{
									ishas = true;
								}
							}	
						}
						if(ishas == false)
						{
							Toast.makeText(getApplicationContext(), "没有查找到该用户名", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Intent intent = new Intent();
							intent.setClass(FriendSearchActivity.this, FriendAddActivity.class);
							intent.putExtra("USERID",value);
							startActivity(intent);
						}
					}
						
			} catch (Exception e) {
				System.out.println(e.getMessage());
				// Toast.makeText(this,e.getMessage()+" "+e.getClass().toString(),
				// Toast.LENGTH_SHORT).show();
			}
		}		
	};
	
	private void initControl() {
		//userlist = (ListView)findViewById(R.id.search_friend_list);
		username = (EditText)findViewById(R.id.search_user_name);
		searchButton = (ImageButton)findViewById(R.id.ib_friendsearch);
		searchButton.setOnClickListener(searchFriendListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_searchfriend);
		initControl();
	}

}
