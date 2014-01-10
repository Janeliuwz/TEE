
package com.tee686.im;

import android.R.integer;
import android.content.Context;
import android.database.DataSetObserver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.casit.tee686.R;

public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;

    private static final int IMG_VOICE_1 = 0;
    private static final int IMG_VOICE_2 = 1;
    private static final int IMG_VOICE_3 = 2;
    
    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    


	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
	 	ChatMsgEntity entity = coll.get(position);
	 	
	 	if (entity.getMsgType()) {
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}
	 	else {
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}
	
	public boolean IsVoice(String msgContent) {
		if(msgContent!=null) {
			if(msgContent.length()>10) {
				if(msgContent.substring(0,10).equals("[voicemsg]"))	{
					return true;
				}
			}
		}
		return false;
	}


	public int getViewTypeCount() {
		return 2;
	}
	
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatMsgEntity entity = coll.get(position);
    	boolean isComMsg = entity.getMsgType();
    	String msgContent = entity.getText();
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null) {
	    	  if (isComMsg) {
	    		  convertView = mInflater.inflate(R.layout.im_chat_receive, null);
	    		  viewHolder = new ViewHolder();
				  viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_recvMsgTime);
				  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_recvContent);
				  viewHolder.tvFileName = (TextView) convertView.findViewById(R.id.tv_voice_filename);
				  viewHolder.tvPlayTime = (TextView) convertView.findViewById(R.id.tv_recvVoice_time);
				  viewHolder.ivVoice = (ImageView) convertView.findViewById(R.id.iv_recvVoice);
				  viewHolder.ivDot = (ImageView) convertView.findViewById(R.id.iv_recvVoice_dot);
	    		  
	    		  //System.out.println(entity.getDate().substring(0,10));				  
			  }
	    	  else {
	    		  //System.out.println(entity.getDate().substring(0,10));
				  convertView = mInflater.inflate(R.layout.im_chat_send, null);
				  viewHolder = new ViewHolder();
				  viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_sendMsgTime);
				  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_sendContent);
				  viewHolder.tvFileName = (TextView) convertView.findViewById(R.id.tv_voice_filename);
				  viewHolder.tvPlayTime = (TextView) convertView.findViewById(R.id.tv_sendVoice_time);
				  viewHolder.ivVoice = (ImageView) convertView.findViewById(R.id.iv_sendVoice);
				  viewHolder.ivDot = (ImageView) convertView.findViewById(R.id.iv_sendVoice_dot);
			  }		  
			  convertView.setTag(viewHolder);
	    }
	    else {
	        viewHolder = (ViewHolder)convertView.getTag();
	    }
	    viewHolder.isComMsg = isComMsg;
	    if(IsVoice(msgContent)) {
	    	viewHolder.tvTime.setText(entity.getDate());
			viewHolder.tvContent.setText("        ");
			viewHolder.tvFileName.setText(entity.getText().substring(10));
			viewHolder.tvPlayTime.setVisibility(View.VISIBLE);
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.ivDot.setVisibility(View.VISIBLE);
		}
		else {
			viewHolder.tvTime.setText(entity.getDate());
			viewHolder.tvContent.setText(msgContent);
			viewHolder.tvFileName.setText("");
			viewHolder.tvPlayTime.setVisibility(View.INVISIBLE);
			viewHolder.ivVoice.setVisibility(View.INVISIBLE);
			viewHolder.ivDot.setVisibility(View.INVISIBLE);
		}
	    return convertView;
    }
    
    static class ViewHolder { 
        public TextView tvTime;
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvFileName;
        public TextView tvPlayTime;
        public ImageView ivVoice;
        public ImageView ivDot;
        public boolean isComMsg = true;
    }
}
