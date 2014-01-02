
package com.tee686.im;

import android.R.integer;
import android.content.Context;
import android.database.DataSetObserver;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.casit.tee686.R;

public class NewMsgListAdapter extends BaseAdapter {
	
//	public static interface IMsgViewType
//	{
//		int IMVT_COM_MSG = 0;
//		int IMVT_TO_MSG = 1;
//	}
	
    private static final String TAG = NewMsgListAdapter.class.getSimpleName();

    private List<Map<String,String>> msg;

    private Context ctx;
    
    private LayoutInflater mInflater;

    public NewMsgListAdapter(Context context, List<Map<String,String>> msg) {
        ctx = context;
        this.msg = msg;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return msg.size();
    }

    public Object getItem(int position) {
        return msg.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    

//	public int getItemViewType(int position) {
//		// TODO Auto-generated method stub
//	 	ChatMsgEntity entity = msg.get(position);
//	 	
//	 	if (entity.getMsgType())
//	 	{
//	 		return IMsgViewType.IMVT_COM_MSG;
//	 	}else{
//	 		return IMsgViewType.IMVT_TO_MSG;
//	 	}
//	 	
//	}


//	public int getViewTypeCount() {
//		// TODO Auto-generated method stub
//		return 2;
//	}
	
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	Map<String,String> entity = msg.get(position);
    	//boolean isComMsg = entity.getMsgType();
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
			  convertView = mInflater.inflate(R.layout.im_newmsglist_item, null);
			  viewHolder = new ViewHolder();
			  viewHolder.tvFriendid = (TextView) convertView.findViewById(R.id.tv_newmsgitem_name);
			  viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tv_new_msg);
			  convertView.setTag(viewHolder);
	    }
	    else
	    {
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
		    
	    viewHolder.tvFriendid.setText(entity.get("friendid"));
	    //viewHolder.tvUserName.setText(entity.getName());
	    viewHolder.tvMsg.setText(entity.get("msgcontent"));	    
	    return convertView;
    }
    
    static class ViewHolder { 
        public TextView tvFriendid;
        public TextView tvMsg;
        //public TextView tvContent;
    }
}
