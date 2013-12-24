package com.tee686.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.casit.tee686.R;
import com.tee686.activity.UserLoginActivity;
import com.tee686.entity.UserInfoItem;
import com.tee686.utils.ImageUtil;
import com.tee686.utils.ImageUtil.ImageCallback;
import com.tee686.utils.UserHeadUtil;

@SuppressLint("ValidFragment")
public class UserIntroFragment extends Fragment {
	UserInfoItem mUserInfoItem;
	private ImageView img;
	private TextView txtName;
	private TextView txtRegTime;
	private TextView txtEP;
	private TextView txtEM;
	private GridView gvGrid;
	SimpleAdapter mAdapter;
	private List<Map<String, Object>> mList;
	private Context mContext;
	private byte[] data;
//	private String[] items = new String[] { "选择本地图片", "拍照" };
	SharedPreferences share;
	LayoutInflater inflater;
	
	/* 头像名称   
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";   
      
     请求码   
    private static final int IMAGE_REQUEST_CODE = 0;    
    private static final int CAMERA_REQUEST_CODE = 1;    
    private static final int RESULT_REQUEST_CODE = 2;
	private static final int RESULT_CANCELED = KeyEvent.KEYCODE_BACK;*/

	public UserIntroFragment(UserInfoItem result) {
		mUserInfoItem = result;
	}

	// [start]继承方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = inflater.getContext();
		View v = inflater.inflate(R.layout.user_center_intro_fragment, null);
		initControl(v);
//		initGridView();
		setControl();
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putByteArray("bitmap", data);
		
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null) {
			data = savedInstanceState.getByteArray("bitmap");
			img.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
			img.setScaleType(ScaleType.CENTER_CROP);
		}
	}

	private void initControl(View v) {
		img = (ImageView) v.findViewById(R.id.user_imageview_icon);
		txtName = (TextView) v.findViewById(R.id.user_textview_name);
		txtRegTime = (TextView) v.findViewById(R.id.user_textview_reg_time);
		txtEP = (TextView) v.findViewById(R.id.user_textview_e_p);
		txtEM = (TextView) v.findViewById(R.id.user_textview_e_m);
		gvGrid = (GridView) v.findViewById(R.id.user_gridview_medal);
		share = getActivity().getSharedPreferences(UserLoginActivity.SharedName, 0);
	}

	/*private void initGridView() {
		getData();
		mAdapter = new SimpleAdapter(mContext, mList,
				R.layout.user_gridview_item_medal, new String[] { "img" },
				new int[] { R.id.user_imageview_medal }) {

			@Override
			public void setViewImage(ImageView v, String value) {
				// TODO Auto-generated method stub
				super.setViewImage(v, value);
				ImageUtil.setThumbnailView(value, v, mContext,
						new imageCallback(), false);
			}

			class imageCallback implements ImageCallback {

				@Override
				public void loadImage(Bitmap bitmap, String imagePath) {
					// TODO Auto-generated method stub
					try {
						ImageView img = (ImageView) gvGrid
								.findViewWithTag(imagePath);
						img.setImageBitmap(bitmap);
					} catch (NullPointerException ex) {
						Log.e("error", "ImageView = null");
					}
				}

			}

		};
		gvGrid.setAdapter(mAdapter);
	}*/

	/*private void getData() {
		mList = new ArrayList<Map<String, Object>>();
		for (int i = 0, count = mUserInfoItem.getIcon().size(); i < count; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", mUserInfoItem.getIcon().get(i).getImg());
			mList.add(map);
		}
	}*/

	private void setControl() {
		txtName.setText(mUserInfoItem.getUsername());
		txtRegTime.setText(mUserInfoItem.getRegtime());
		txtEP.setText(getString(R.string.user_center_e_coin, mUserInfoItem.getTp()));
		txtEM.setText(getString(R.string.user_center_e_reputation,  mUserInfoItem.getTm()));		
		String imgUrl = share.getString(UserLoginActivity.PIC, "");
		if(null != imgUrl && !"".equals(imgUrl)) {			
			new imgAsyncTask().execute(imgUrl);			
		} 
		/*ImageUtil.setThumbnailView(imgUrl, img, mContext,
				new myImageCallBack(), true);*/
	}

	/**
	 * 设置用户头像
	 */
	/*private void showDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(getActivity())
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (hasSdcard()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	protected boolean hasSdcard() {
		// TODO Auto-generated method stub
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}*/
	
	class imgAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {			
			img.setImageBitmap(UserHeadUtil.getUserHeadImage(params[0]));
			img.setScaleType(ScaleType.CENTER_CROP);
			return null;			
		}		
	}

	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory(),
							IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					setImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	*//**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 *//*
	public void startPhotoZoom(Uri uri) {
		if (uri == null) {
			Log.i("tag", "The uri is not exist.");
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	*//**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 *//*
	private void setImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			img.setBackgroundDrawable(drawable);
		}
	}*/
	
}
