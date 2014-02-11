
package com.tee686.im;

import android.R.integer;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.casit.tee686.R;
import com.tee686.activity.FriendChatActivity;
import com.tee686.sqlite.MessageStore;

public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;
    
	//private ImageView animationIV;
	private ViewHolder viewHolder = null;	
    private AnimationDrawable animationDrawable; 
    
    private static final int IMG_VOICE_1 = 0;
    private static final int IMG_VOICE_2 = 1;
    private static final int IMG_VOICE_3 = 2;
	private static final int CHATLIST_MENU_DELETE = 0;
	private static final int CHATLIST_MENU_COPY = 1;
    
    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
    	ListView msgItem = new ListView(ctx);
    	ViewGroup parent = (ViewGroup)msgItem
    			.getItemAtPosition(position);
    	return parent;
        //return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
	public int getItemViewType(int position) {
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
    	
    	//定义成final对象以便在onclick响应中使用
    	final ChatMsgEntity entity = coll.get(position);  	
    	final boolean isComMsg = entity.getMsgType();   	
    	final int pos = position;
    	final ChatMsgViewAdapter adapter = this;
    	
    	String msgContent = entity.getText();
    	int voiceTime = entity.getVoiceTime();
    	
    	//ViewHolder viewHolder = null;	
	    if (convertView == null) {
	    	//接收到的消息
	    	if (isComMsg) {
	    		convertView = mInflater.inflate(R.layout.im_chat_receive, null);
	    		viewHolder = new ViewHolder();
				viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_recvMsgTime);
				viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_recvContent);
				viewHolder.tvFileNameAndTime = (TextView) convertView.findViewById(R.id.tv_voice_filename);
				viewHolder.tvPlayTime = (TextView) convertView.findViewById(R.id.tv_recvVoice_time);
				viewHolder.ivVoice = (ImageView) convertView.findViewById(R.id.iv_recvVoice);
				viewHolder.ivDot = (ImageView) convertView.findViewById(R.id.iv_recvVoice_dot);
				//animationIV = (ImageView) convertView.findViewById(R.id.iv_recvVoice);
				//System.out.println(entity.getDate().substring(0,10));				  
			}
	    	//发送的消息
	    	else {
	    		//System.out.println(entity.getDate().substring(0,10));
				convertView = mInflater.inflate(R.layout.im_chat_send, null);
				viewHolder = new ViewHolder();
				viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_sendMsgTime);
				viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_sendContent);
				viewHolder.tvFileNameAndTime = (TextView) convertView.findViewById(R.id.tv_voice_filename);
				viewHolder.tvPlayTime = (TextView) convertView.findViewById(R.id.tv_sendVoice_time);
				viewHolder.ivVoice = (ImageView) convertView.findViewById(R.id.iv_sendVoice);
				viewHolder.ivDot = (ImageView) convertView.findViewById(R.id.iv_sendVoice_dot);
				//animationIV = (ImageView) convertView.findViewById(R.id.iv_sendVoice);
			}		  
			convertView.setTag(viewHolder);
	    }
	    else {
	        viewHolder = (ViewHolder)convertView.getTag();
	    }
	    
	    //必须定义在这里，定义在最前面convertView可能为null
	    final View view = convertView;
	    viewHolder.isComMsg = isComMsg;
	    
	    //如果是音频消息
	    if(IsVoice(msgContent)) {
	    	
	    	//根据音频时长设置消息框长度
	    	String voiceStr = "www";
	    	int voiceStrLen = voiceTime / 2;
	    	while(voiceStrLen-- > 0 ) {
	    		voiceStr += "w";
	    	}
	    	
	    	viewHolder.tvTime.setText(entity.getDate());
			viewHolder.tvContent.setText(voiceStr);
			viewHolder.tvContent.setTextColor(Color.argb(0, 0, 0, 0)); //文字透明
			viewHolder.tvFileNameAndTime.setText(entity.getText().substring(10));
			viewHolder.tvPlayTime.setText(voiceTime + "''");
			viewHolder.tvPlayTime.setVisibility(View.VISIBLE);
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.ivDot.setVisibility(View.VISIBLE);
			
			viewHolder.tvContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ImageView ivVoice = null;
					ImageView ivDot = null;
					if(isComMsg) {
						ivVoice = (ImageView)view.findViewById(R.id.iv_recvVoice);
						ivDot = (ImageView)view.findViewById(R.id.iv_recvVoice_dot);
						ivVoice.setImageResource(R.drawable.im_recvvoiceplaying);
						System.out.println("receive");
					}
					else {
						ivVoice = (ImageView)view.findViewById(R.id.iv_sendVoice);
						ivDot = (ImageView)view.findViewById(R.id.iv_sendVoice_dot);
						ivVoice.setImageResource(R.drawable.im_sendvoiceplaying);
						System.out.println("send");
					}
					
					final ImageView ivVoice_finish = ivVoice;
					animationDrawable = (AnimationDrawable)ivVoice.getDrawable();
					
					String msgcontent = entity.getText().substring(10);
					String[] command = msgcontent.split(",");
					
					//文件路径
					String audioPath = Environment
							.getExternalStorageDirectory().getPath() + "/im/record"; 
					File audioFile = new File(audioPath,command[0]);
					
					MediaPlayer mediaPlayer = new MediaPlayer();
					mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播出完毕事件  
				        @Override public void onCompletion(MediaPlayer mp) {  
					        mp.release(); 
					        animationDrawable.stop();
					        if(isComMsg) {
					        	ivVoice_finish.setImageResource(R.drawable.chatfrom_voice_playing);
						     }
						     else {
						    	ivVoice_finish.setImageResource(R.drawable.chatto_voice_playing);
						     }
				        }  
					});  
			        mediaPlayer.reset();
			        try {
						mediaPlayer.setDataSource(audioFile.getAbsolutePath());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}  
			        try {
						mediaPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}  
			        mediaPlayer.start();//播放  
			        animationDrawable.start();
			        ivDot.setVisibility(View.INVISIBLE);
			        
					System.out.println("单击语音消息" + pos);
				}
			});
			viewHolder.tvContent.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);		
					builder.setItems(R.array.chatvoicemenu, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 
							switch(which) {
							case CHATLIST_MENU_DELETE:
								//System.out.println("删除语音" + pos);
								
								//删除数据库项
								MessageStore store = new MessageStore(ctx);
								store.deleteMessagelistfromdatetiem(entity.getDate());
								store.closeDB();
								
								//刷新数据
								coll.remove(pos);
								adapter.notifyDataSetChanged();
								
								//删除对应的文件
								String msgcontent = entity.getText().substring(10);
								String[] command = msgcontent.split(",");
								
								//文件路径
								String audioPath = Environment
										.getExternalStorageDirectory().getPath() + "/im/record"; 
								File audioFile = new File(audioPath,command[0]);
								
								//删除
								audioFile.delete();
								break;
							}
						}
					});
					AlertDialog alertDialog = builder.create(); //创建对话框
					alertDialog.setCanceledOnTouchOutside(true); //点击对话框外部则消失
					alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //对话框无标题栏
					alertDialog.show();
					return false;
				}
			});
		}
	    
	    //如果非音频消息
		else {
			viewHolder.tvTime.setText(entity.getDate());
			viewHolder.tvContent.setText(msgContent);
			viewHolder.tvContent.setTextColor(Color.BLACK);	
			viewHolder.tvFileNameAndTime.setText("");
			viewHolder.tvPlayTime.setVisibility(View.INVISIBLE);
			viewHolder.ivVoice.setVisibility(View.INVISIBLE);
			viewHolder.ivDot.setVisibility(View.INVISIBLE);

			viewHolder.tvContent.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
									
					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);		
					builder.setItems(R.array.chattextmenu, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							//取当前点击的对话文本的view
							TextView contentMsg = null;
							if(isComMsg) {
								contentMsg = (TextView)view.findViewById(R.id.tv_recvContent);
								System.out.println("receive");
							}
							else {
								contentMsg = (TextView)view.findViewById(R.id.tv_sendContent);
								System.out.println("send");
							}
							
							switch(which) {
							case CHATLIST_MENU_DELETE:
								//System.out.println("删除文字" + pos);
								//删除数据库项
								MessageStore store = new MessageStore(ctx);
								store.deleteMessagelistfromdatetiem(entity.getDate());
								store.closeDB();
								
								//刷新数据
								coll.remove(pos);
								adapter.notifyDataSetChanged();
								break;
							case CHATLIST_MENU_COPY:
								
								//System.out.println("复制文字" + pos);
								
								//取得系统全局剪贴板
								ClipboardManager copy = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
								
								//将当前的文本内容放入剪切板
								copy.setPrimaryClip(ClipData.newPlainText("simple text",contentMsg.getText()));
								break;
							}
						}
					});
					AlertDialog alertDialog = builder.create(); //创建对话框
					alertDialog.setCanceledOnTouchOutside(true); //点击对话框外部则消失
					alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //对话框无标题栏 
					alertDialog.show();
					return false;
				}
			});
		}
	    return convertView;
    }

    static class ViewHolder { 
        public TextView tvTime;
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvFileNameAndTime;
        public TextView tvPlayTime;
        public ImageView ivVoice;
        public ImageView ivDot;
        public boolean isComMsg = true;
    }
}
