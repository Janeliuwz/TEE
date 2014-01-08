package com.tee686.im;

import java.io.File;
import java.io.IOException;

import com.casit.tee686.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager.LayoutParams;
//import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RecordButton extends ImageButton {
	/*
	 * 成员变量
	 */
	private Dialog recordIndicator;
	private MediaRecorder recorder;
	private Handler volumeHandler;
	private static ImageView iv_recDialog;
	
	private long startTime; //录音开始时间
	private String mFileName = null;
	private static int[] recImgRes = {R.drawable.im_mic_2, R.drawable.im_mic_3, 
		R.drawable.im_mic_4, R.drawable.im_mic_5}; //录音音量图片
	
	private static final int MIN_INTERVAL_TIME = 2000; //最小录音时长
	private static final int MAX_TIME = 60000; //
	private static final int IMG_MIC_2 = 0;
	private static final int IMG_MIC_3 = 1;
	private static final int IMG_MIC_4 = 2;
	private static final int IMG_MIC_5 = 3;
	
	private ObtainDecibelThread decibelThread; //音量控制进程
	private OnFinishedRecordListener finishedListener;
	
	/*
	 * 构造函数RecordButton
	 */
	public RecordButton(Context context) {
		super(context);
		volumeHandler = new ShowVolumeHandler();
	}
	public RecordButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		volumeHandler = new ShowVolumeHandler();
	}
	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		volumeHandler = new ShowVolumeHandler();
	}

	public void setSavePath(String path) {
		this.mFileName = path;
	}
	
	public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
		this.finishedListener = listener;
	}
	
	/*
	 * 录音窗口释放监听器
	 */
	private OnDismissListener onDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			stopRecording();
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int action = event.getAction();
		
		if(mFileName == null) {
			return false;
		}
		
		switch(action) {
		
		case MotionEvent.ACTION_DOWN:
			//this.setText("松开发送");
			initDialog();
			break;
			
		case MotionEvent.ACTION_UP:
			//this.setText("按住录音");
			finishRecord();
			break;
			
		case MotionEvent.ACTION_CANCEL:
			cancelRecord();
			Toast.makeText(getContext(), "取消录音", 1).show();
			break;
		}
		
		return true;
	}
	
	/*
	 * 初始化录音图标浮窗
	 */
	private void initDialog() {
		startTime = System.currentTimeMillis();
		iv_recDialog = new ImageView(getContext());
		iv_recDialog.setImageResource(R.drawable.im_mic_2);
		
		//当前界面显示录音浮窗
		recordIndicator = new Dialog(getContext(), 
				R.style.RecordToastDialog); //圆角矩阵形状
		recordIndicator.setContentView(iv_recDialog, new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		recordIndicator.setOnDismissListener(onDismissListener);
		LayoutParams lp = recordIndicator.getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		
		startRecording();
		recordIndicator.show();
	}
	
	/*---------------------------------------
	 *  录音功能函数:
	 *   private void startRecording();
	 *   private void stopRecording();
	 *   private void finishRecord();
	 *   private void cancelRecord();
	 ----------------------------------------*/
	 private void startRecording() {
		 
		 recorder = new MediaRecorder();
		 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		 recorder.setAudioChannels(1);
		 recorder.setAudioEncodingBitRate(4000);
		 recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		 recorder.setOutputFile(mFileName);
		 recorder.setMaxDuration(MAX_TIME);
		 //监听录音时长
		 recorder.setOnInfoListener(new OnInfoListener() {
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				switch(what) {
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
					//RecordButton.this.setText("按住录音");
					finishRecord();
					Toast.makeText(getContext(), "超过最大时长", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		 });

		 try {
			 recorder.prepare();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 
		 recorder.start();
		 decibelThread = new ObtainDecibelThread();
		 decibelThread.start();
		 
	 }
	 private void stopRecording() {

		 if(decibelThread != null) {
			 decibelThread.exit();
			 decibelThread = null;
		 }
		 if(recorder != null) {
			 recorder.stop();
			 recorder.release();
			 recorder = null;
		 }
	 }
	 private void finishRecord() {
		 stopRecording();
		 recordIndicator.dismiss();
		 
		 long intervalTime = System.currentTimeMillis() - startTime; //录音时长
		 if(intervalTime < MIN_INTERVAL_TIME) {
			 Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
			 File file = new File(mFileName);
			 file.delete();
			 return;
		 }
		 
		 if(finishedListener != null) {
			 finishedListener.onFinishedRecord(mFileName, (int)(intervalTime / 1000));
		 }
	 }
	 private void cancelRecord() {
		 stopRecording();
		 recordIndicator.dismiss(); //释放浮窗
		 
		 Toast.makeText(getContext(), "录音取消", Toast.LENGTH_SHORT).show();
		 File file = new File(mFileName);
		 file.delete();
	 }
	 
	 
	/*------------------------------------------------------------
	 *  内部类:
	 *   private class ObtainDecibelThread extends Thread
	 *   public static class ShowVolumeHandler extends Handler
	 ------------------------------------------------------------*/
	private class ObtainDecibelThread extends Thread {
		
		private volatile boolean isRunning = true;
		
		public void exit() {
			isRunning = false;
		}

		@Override
		public void run() {
			while(isRunning) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(recorder == null || !isRunning) {
					break;
				}
				
				int maxAmplitude = recorder.getMaxAmplitude(); //最大振幅
				if(maxAmplitude != 0) {
					
					int f = (int)(10 * Math.log(maxAmplitude) / Math.log(10));
					//控制录音浮窗的图片动画
					if(f < 26) {
						volumeHandler.sendEmptyMessage(IMG_MIC_2);
					}
					else if(f < 32) {
						volumeHandler.sendEmptyMessage(IMG_MIC_3);
					}
					else if(f < 38) {
						volumeHandler.sendEmptyMessage(IMG_MIC_4);
					}
					else {
						volumeHandler.sendEmptyMessage(IMG_MIC_5);
					}
				}
			}
		}
	}
	
	static class ShowVolumeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			iv_recDialog.setImageResource(recImgRes[msg.what]);
		}
	}
	
	public interface OnFinishedRecordListener {
		public void onFinishedRecord(String audioPath, int time); //重载接口
	}
	
}
