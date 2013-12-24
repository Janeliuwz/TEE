package com.tee686.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;



import com.tee686.activity.UserCenterActivity;
import com.tee686.config.Urls;
import com.tee686.entity.PubContent;
import com.tee686.https.HttpUtils;
import com.tee686.utils.ImageUtil;
import com.tee686.utils.ImageUtil.ImageCallback;
import com.tee686.utils.IntentUtil;
import com.tee686.ui.base.BaseActivity;
import com.casit.tee686.R;

//import android.widget.Button;

public class BulletinActivity extends BaseActivity {

	
	
	private ImageButton newBulletin;
	private Button mCommunity;
	private Button refresh;
	private LinearLayout goHome;
	private LinearLayout listContent;
	private LinearLayout loadFailed;
	private ListView lv;
//	private List<Map<String, Object>> mlist;
	private MyAdapter mAdapter;
	SharedPreferences share;
	private List<PubContent> mPubContents = new ArrayList<PubContent>();
	
	private String result;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.bulletin);			
		initControl();
		mCommunity.setVisibility(View.GONE);	
		initSharePreferences();	
		new CheckCacheTask().execute();
		new DataAsyncTask().execute(Urls.USER_DOAWLOAD_IMAGE);

				/*new SimpleAdapter(this, mlist, R.layout.bulletin_board, new String[] {"userhead", 
				"bulletincontent", "username", "image", "sendtime"}, new int[] {R.id.iv_userhead, 
				R.id.tv_bulletincontent, R.id.tv_username, R.id.iv_bulletin, R.id.tv_sendtime});*/
		
		goHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (share.contains(UserLoginActivity.KEY)
						&& !share.getString(UserLoginActivity.KEY, "").equals(
								"")) {
					IntentUtil.start_activity(BulletinActivity.this,
							UserCenterActivity.class);
					finish();
				} else {
					showLongToast(getResources().getString(
							R.string.user_center_error));
				}
			}
		});
		
		newBulletin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentUtil.start_activity(BulletinActivity.this,
						EditActivity.class);
			}
		});
		
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DataAsyncTask().execute(Urls.USER_DOAWLOAD_IMAGE);
			}
		});
		
	}
	
	private void initSharePreferences() {
		// TODO Auto-generated method stub
		share = getSharedPreferences(UserLoginActivity.SharedName, Context.MODE_PRIVATE);
		/*if (share.contains("pubContents")) {
			try {
				JSONArray jsonArray = new JSONArray(share.getString("pubContents", ""));
				for(int i=0; i<jsonArray.length(); i++) {
					String json = jsonArray.getString(i);
					PubContent pubContent = new ObjectMapper().readValue(json, PubContent.class);
					mPubContents.add(pubContent);
				}
				mAdapter = new MyAdapter();
				mAdapter.appendToList(mPubContents);
				lv.setAdapter(mAdapter);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} else {
			new DataAsyncTask().execute(Urls.USER_DOAWLOAD_IMAGE);
		}*/
	}
	
	private void initControl() {
		newBulletin = (ImageButton) findViewById(R.id.newbulletin);		
		goHome = (LinearLayout) findViewById(R.id.Linear_above_toHome);
		listContent = (LinearLayout) findViewById(R.id.list_content);
		loadFailed = (LinearLayout) findViewById(R.id.view_load_fail);
		mCommunity = (Button) findViewById(R.id.btn_community);
		refresh = (Button) findViewById(R.id.bn_refresh);
		lv = (ListView) findViewById(R.id.lv_community_content);
//		mlist = new ArrayList<Map<String,Object>>();
	}	
	
	/**
	 * @author Jason
	 *获得列表数据
	 */
	class DataAsyncTask extends AsyncTask<String, Void, List<PubContent>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showAlertDialog("温馨提示", "正在加载列表信息请稍后");
		}

		@Override
		protected List<PubContent> doInBackground(String... params) {			
			
			try {				
				result = HttpUtils.getByHttpClient(BulletinActivity.this, params[0]);	
				StringBuilder sb = new StringBuilder(result);
				result = sb.deleteCharAt(result.lastIndexOf(",")).toString();
//				share.edit().putString("pubContents", result).commit();		
				JSONArray jsonArray = new JSONArray(result);
				for(int i=0; i<jsonArray.length(); i++) {
					String json = jsonArray.getString(i);
					PubContent pubContent = new ObjectMapper().readValue(json, PubContent.class);
					mPubContents.add(pubContent);
				}
				
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*for(PubContent pubContent : mPubContents) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userhead", pubContent.getHeadimage());
				map.put("bulletincontent", pubContent.getContent());
				map.put("username", pubContent.getUsername());
				map.put("image", pubContent.getImageFile());
				map.put("sendtime", pubContent.getSendtime());
				mlist.add(map);
			}*/ 
			
			
			return mPubContents;
		}

		@Override
		protected void onPostExecute(List<PubContent> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAlertDialog.dismiss();
			if(!result.isEmpty()) {
				mAdapter = new MyAdapter();
				mAdapter.appendToList(result);
				lv.setAdapter(mAdapter);
				listContent.setVisibility(View.VISIBLE);
				loadFailed.setVisibility(View.GONE);
//				result.clear();
			} else {
				showShortToast("获取数据失败");
				listContent.setVisibility(View.GONE);
				loadFailed.setVisibility(View.VISIBLE);
			}
			
		}
		
	}
	
	class CheckCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ImageUtil.checkCache(BulletinActivity.this);
			return null;
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	ImageCallback callback1 = new ImageCallback() {
		
		@Override
		public void loadImage(Bitmap bitmap, String imagePath) {
			// TODO Auto-generated method stub
			try {
				ImageView img = (ImageView) lv.findViewWithTag(imagePath);
				img.setImageBitmap(bitmap);
			} catch (NullPointerException ex) {
				Log.e("error", "ImageView = null");
			}
		}
	};

	class MyAdapter extends BaseAdapter {
		
		private List<PubContent> pubContents = new ArrayList<PubContent>();	
		
		public MyAdapter() {}		
		
		public void appendToList(List<PubContent> lists) {
			if (lists == null) {
				return;
			}
			pubContents.addAll(lists);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pubContents.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pubContents.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PubContent pubContent = pubContents.get(position);
			ViewHolder viewHolder;
			convertView = null;
			convertView = getLayoutInflater().inflate(R.layout.bulletin_board, null);
			viewHolder = new ViewHolder();
			viewHolder.ivHeadimage = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_bulletincontent);
			viewHolder.ivImagefile = (ImageView) convertView
					.findViewById(R.id.iv_imagefile);
			viewHolder.tvUsername = (TextView) convertView
					.findViewById(R.id.tv_pub_username);
			viewHolder.tvSendtime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			convertView.setTag(viewHolder);
			 /*else {
				viewHolder = (ViewHolder) convertView.getTag();
			}*/
			viewHolder.tvContent.setText(pubContent.getContent());
			viewHolder.tvSendtime.setText(pubContent.getSendtime());
			viewHolder.tvUsername.setText(pubContent.getUsername());
//			viewHolder.ivHeadimage.setTag(pubContent.getHeadimage());
//			viewHolder.ivImagefile.setTag(pubContent.getImageFile());
			synchronized (viewHolder) {
				if (!"".equals(pubContent.getHeadimage())) {
					// new HeadImgAsyncTask().execute(pubContent.getHeadimage());
					ImageUtil.setThumbnailView(pubContent.getHeadimage(),
							viewHolder.ivHeadimage, BulletinActivity.this, callback1, true);
				}
				if (pubContent.getImageFile() != null) {
					ImageUtil.setThumbnailView(pubContent.getImageFile(),
							viewHolder.ivImagefile, BulletinActivity.this, callback1, true);
					
					/*if(headImage!=null) {
						viewHolder.ivHeadimage.setImageBitmap(headImage);
					}*/
					
				}
			}		
			
			
			return convertView;
		}

		private class ViewHolder {
			public ImageView ivHeadimage;
			public TextView tvContent;
			public ImageView ivImagefile;
			public TextView tvUsername;
			public TextView tvSendtime;
		}
	}
}
