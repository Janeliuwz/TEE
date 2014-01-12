
package com.tee686.im;

import android.R.integer;
import android.app.AlertDialog;
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
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
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
    	
    	final ChatMsgEntity entity = coll.get(position);
    	int voiceTime = entity.getVoiceTime();
    	final boolean isComMsg = entity.getMsgType();
    	String msgContent = entity.getText();
    	final int pos = position;
    	
    	//ViewHolder viewHolder = null;	
	    if (convertView == null) {
	    	//接收到的消息
	    	if (isComMsg) {
	    		convertView = mInflater.inflate(R.layout.im_chat_receive, null);
	    		viewHolder = new ViewHolder();
				viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_recvMsgTime);
				viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_recvContent);
				viewHolder.tvFileName = (TextView) convertView.findViewById(R.id.tv_voice_filename);
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
				viewHolder.tvFileName = (TextView) convertView.findViewById(R.id.tv_voice_filename);
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
	    viewHolder.isComMsg = isComMsg;
	    if(IsVoice(msgContent)) {
	    	
	    	//根据音频时长设置消息框长度
	    	String voiceStr = "vvv";
	    	int voiceStrLen = voiceTime / 2;
	    	while(voiceStrLen-- > 0 ) {
	    		voiceStr += "v";
	    	}
	    	
	    	viewHolder.tvTime.setText(entity.getDate());
			viewHolder.tvContent.setText(voiceStr);
			viewHolder.tvContent.setTextColor(Color.argb(0, 0, 0, 0)); //文字透明
			viewHolder.tvFileName.setText(entity.getText().substring(10));
			viewHolder.tvPlayTime.setText(voiceTime + "''");
			viewHolder.tvPlayTime.setVisibility(View.VISIBLE);
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.ivDot.setVisibility(View.VISIBLE);
			viewHolder.tvContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(isComMsg) {
						viewHolder.ivVoice.setImageResource(R.drawable.im_recvvoiceplaying);
						System.out.println("receive");
					}
					else {
						viewHolder.ivVoice.setImageResource(R.drawable.im_sendvoiceplaying);
						System.out.println("send");
					}
					
					animationDrawable = (AnimationDrawable)viewHolder.ivVoice.getDrawable();
					
					//TODO:播放语音
					animationDrawable.start();
					
					//文件路径
					String audioPath = Environment
							.getExternalStorageDirectory().getPath() + "/im/record"; entity.getText().substring(10);
					File audioFile = new File(audioPath,entity.getText().substring(10));
					
					final MediaPlayer mediaPlayer = new MediaPlayer();
					mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播出完毕事件  
				        @Override public void onCompletion(MediaPlayer arg0) {  
					        mediaPlayer.release(); 
					        animationDrawable.stop();
				        }  
					});  
			        mediaPlayer.reset();
			        try {
						mediaPlayer.setDataSource(audioFile.getAbsolutePath());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
			        try {
						mediaPlayer.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
			        mediaPlayer.start();//播放  
										
					//TODO:监测语音播放结束时停止图片动画
					//animationDrawable.stop();
					
					System.out.println("单击语音消息");
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
								System.out.println("删除语音" + pos);
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
		else {
			viewHolder.tvTime.setText(entity.getDate());
			viewHolder.tvContent.setText(msgContent);
			viewHolder.tvContent.setTextColor(Color.BLACK);	
			viewHolder.tvFileName.setText("");
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
							// TODO 
							switch(which) {
							case CHATLIST_MENU_DELETE:
								System.out.println("删除文字" + pos);
								break;
							case CHATLIST_MENU_COPY:
								System.out.println("复制文字" + pos);
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
    /*
    OnClickListener voiceListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
    };
    OnLongClickListener voiceLongListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			return false;
		}
    };
    OnLongClickListener textLongListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {

			return false;
		}
    };*/

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
