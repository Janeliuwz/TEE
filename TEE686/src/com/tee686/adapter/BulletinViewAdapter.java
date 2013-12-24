package com.tee686.adapter;

import java.util.List;

import com.casit.tee686.R;
import com.tee686.activity.BulletinActivity;
import com.tee686.entity.PubContent;
import com.tee686.utils.ImageUtil;
import com.tee686.utils.ImageUtil.ImageCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BulletinViewAdapter extends BaseAdapter {

	private Context context;
	private List<PubContent> pubContents;
	private LayoutInflater inflater;
	private BulletinActivity activity;
	
//	public byte[] data;
	
//	private Bitmap headImage;
	
	public BulletinViewAdapter(Context context, List<PubContent> pubContents, BulletinActivity activity) {
		this.context = context;
		this.pubContents = pubContents;
		this.activity = activity;
		inflater = LayoutInflater.from(this.context);
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
		final PubContent pubContent = pubContents.get(position);

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bulletin_board, null);
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
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvContent.setText(pubContent.getContent());
		viewHolder.tvSendtime.setText(pubContent.getSendtime());
		viewHolder.tvUsername.setText(pubContent.getUsername());
//		viewHolder.ivHeadimage.setTag(pubContent.getHeadimage());
//		viewHolder.ivImagefile.setTag(pubContent.getImageFile());
		
		if (!"".equals(pubContent.getHeadimage())) {
			// new HeadImgAsyncTask().execute(pubContent.getHeadimage());
			ImageUtil.setThumbnailView(pubContent.getHeadimage(),
					viewHolder.ivHeadimage, context, new ImageCallback() {
						
						@Override
						public void loadImage(Bitmap bitmap, String imagePath) {
							// TODO Auto-generated method stub
							
						}
					}, true);
		}
		if (pubContent.getImageFile() != null) {
			ImageUtil.setThumbnailView(pubContent.getImageFile(),
					viewHolder.ivImagefile, context, new ImageCallback() {
						
						@Override
						public void loadImage(Bitmap bitmap, String imagePath) {
							// TODO Auto-generated method stub
							
						}
					}, true);
			
			/*if(headImage!=null) {
				viewHolder.ivHeadimage.setImageBitmap(headImage);
			}*/
			
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
	
	
	
	/*class HeadImgAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			headImage = UserHeadUtil.getUserHeadImage(params[0]);
			return null;			
		}		
	}*/
	
	
}
