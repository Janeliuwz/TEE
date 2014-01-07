/*
 * 自定义好友列表ListView适配器
 * 实现快速滚动索引接口SectionIndexer
 */

package com.tee686.im;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.casit.tee686.R;

public class FriendsListAdapter extends BaseAdapter implements SectionIndexer {

/*	public static class friends {
		public String username;
		public Boolean presence;
	}*/
	
	static class ViewHolder {
		TextView tvCatalog; //目录
		ImageView ivAvatar; //头像
		TextView tvName; //用户名
		TextView tvStatus; //状态
	}
	//private static List<friends> list = new ArrayList<friends>();
	private List<Map<String,String>> mNames;
	private Context mContext;
	//private String[] mNames;
	
	//构造函数
	@SuppressWarnings("unchecked")
	public FriendsListAdapter(Context mContext, List<Map<String,String>> mNames) {
		this.mContext = mContext;
		this.mNames = mNames;
	}
	
	@Override
	public int getCount() {
		return mNames.size();
	}

	@Override
	public Object getItem(int position) {
		return mNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		String username = mNames.get(position).get("name");
		String presence = mNames.get(position).get("presence");
		String catalog = PinyinUtil.getPingYin(username).toUpperCase().substring(0, 1);
		
		//填充视图，不为空时直接使用
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.im_friendslist_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.tv_frienditem_catalog);
			viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.iv_frienditem_avatar);
			viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_frienditem_name);
			viewHolder.tvStatus = (TextView)convertView.findViewById(R.id.tv_frienditem_status);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		//设置字母索引
		if(position == 0) {
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
			viewHolder.tvCatalog.setText(catalog);
		}
		else {
			String lastCatalog = PinyinUtil.getPingYin(mNames.get(position-1).get("name")).toUpperCase().substring(0, 1);
			if(catalog.equals(lastCatalog)) {
				viewHolder.tvCatalog.setVisibility(View.GONE);
			}
			else {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			}
		}
		
		//TODO:头像
		//viewHolder.ivAvatar.setImageResource();
		viewHolder.tvName.setText(username);
		viewHolder.tvStatus.setText(presence);
		/*Boolean presenceStatu = findpresenceStatubyUsername(username);
		if(presenceStatu == true)
		{
			viewHolder.tvStatus.setText("在线");
		}
		else
		{
			viewHolder.tvStatus.setText("离线");
		}*/
		
		return convertView;
	}

	//获取用户名所属字母索引
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mNames.size(); i++) {
            //String l = PinyinUtil.converterToFirstSpell(mNames[i]).substring(0, 1);
            String l = PinyinUtil.getPingYin(mNames.get(i).get("name")).toUpperCase();
            char firstChar = l.charAt(0);
            if (firstChar == section) {  
                return i;  
            }
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
	
	/*public static Boolean findpresenceStatubyUsername(String username)
	{
		Boolean presenceStatu = true;
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).username.equals(username))
			{
				presenceStatu = list.get(i).presence;
				break;
			}
		}
		return presenceStatu;
	}*/
	
}
