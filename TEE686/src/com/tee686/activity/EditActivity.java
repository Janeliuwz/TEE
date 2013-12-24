package com.tee686.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alipay.android.appDemo4.Base64;
import com.casit.tee686.R;
import com.tee686.config.Urls;
import com.tee686.entity.PubContent;
import com.tee686.ui.base.BaseActivity;
import com.tee686.utils.DateUtil;

public class EditActivity extends BaseActivity {

	private static final int REQUE_CODE_CAMERA = 1;
	private static final int REQUE_CODE_PHOTO = 2;
	private static final int REQUE_CODE_CROP = 3;
	
	private ImageView ivBack;
	private ImageView ivPic;
	private ImageButton ibPublish;
	private EditText content;
	private PopupWindow popupWindow;
	private View contentView;
	private Button btn_camera;
	private Button btn_photo;
	private Bitmap cropBitmap;	//保存裁剪后的图片
	private File cameraFile = null;	//保存拍照后的图片文件
	private Uri cropImageUri; //裁剪图片存储位置
	protected Intent intent = null;
	private PubContent pubContent;
	SharedPreferences shared;	
	
private String info = "上传失败";//服务器返回的信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_bulletin);
		initControl();
		initSharePreferences();		
//		mStorage = Frontia.getStorage();
		if(null != savedInstanceState) {
			ivPic.setImageBitmap((Bitmap) savedInstanceState.getParcelable("image"));
			content.setText(savedInstanceState.getString("content"));
		}
		
		ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(!content.getText().toString().equals("")) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							EditActivity.this);
					final AlertDialog ad = builder.create();
					ad.show();
					builder.setMessage("要放弃发布公告吗?")
							.setPositiveButton("是",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											ad.dismiss();
											EditActivity.this.finish();
											overridePendingTransition(
													R.anim.umeng_fb_slide_in_from_left,
													R.anim.umeng_fb_slide_out_from_right);
										}
									})
							.setNegativeButton("否",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											ad.dismiss();
										}
									});
					// dialog.setCancelable(true);
					builder.show();
         		} else {
         			finish();
         			overridePendingTransition(R.anim.umeng_fb_slide_in_from_left, 
         					R.anim.umeng_fb_slide_out_from_right);
         		}
            }
        });
		
		ivPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopuWindow(v);
			}
		});
		
		btn_camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, getCropUri());
				popupWindow.dismiss();
				startActivityForResult(intent, REQUE_CODE_CAMERA);
			}
		});
		
		btn_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(Intent.ACTION_PICK, null); 
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				popupWindow.dismiss();
				startActivityForResult(intent, REQUE_CODE_PHOTO);
			}
		});
		
		ibPublish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!content.getText().toString().equals("")) {
					new PublishTaskAsync().execute(Urls.USER_PUBLISH);
//					saveData();
				} else {
					showShortToast("请输入内容后再发布");
				}
			}
		});
	}

	private void initControl() {
		// TODO Auto-generated method stub
		ivBack = (ImageView) findViewById(R.id.details_imageview_gohome);
		ivPic = (ImageView) findViewById(R.id.iv_bulletin_pic);
		ibPublish = (ImageButton) findViewById(R.id.ib_bulletin_ok);
		content = (EditText) findViewById(R.id.et_publish_bulletin);
		contentView = getLayoutInflater().inflate(R.layout.popup, null);
		btn_camera = (Button) contentView.findViewById(R.id.camara);
        btn_photo = (Button) contentView.findViewById(R.id.photo);
        
	}
	
	private void initSharePreferences() {
		// TODO Auto-generated method stub
		shared = getSharedPreferences(UserLoginActivity.SharedName, Context.MODE_PRIVATE);
		
	}
	/**
	 * 显示PopupWindow用于选择图片上传方式
	 */
	public void showPopuWindow(View v){
		if(null == popupWindow){
			popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.popu_animation);
		}
		popupWindow.showAtLocation(content, Gravity.BOTTOM, 0, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUE_CODE_CAMERA:
				startPhotoZoom(Uri.fromFile(cameraFile));
				break;
			case REQUE_CODE_PHOTO:
				if(null!= data){
					startPhotoZoom(data.getData());
				}
				break;
			case REQUE_CODE_CROP:
				try {
					cropBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(cropBitmap!= null){
					ivPic.setImageBitmap(cropBitmap);
				}				
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param data
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 1000);
		intent.putExtra("outputY", 1000);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getCropUri());
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", false);
		startActivityForResult(intent, REQUE_CODE_CROP);
	}

	private Uri getCropUri() {
		File fileDir;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = new File(Environment.getExternalStorageDirectory()+"/user_photos"); 
			if(!fileDir.exists()){  
				fileDir.mkdirs();  
			}  
		}else{
			Toast.makeText(EditActivity.this, R.string.sd_noexit, Toast.LENGTH_SHORT).show();
			return null;
		}
		cameraFile = new File(fileDir.getAbsoluteFile()+"/"+System.currentTimeMillis()+".jpg");
		cropImageUri = Uri.fromFile(cameraFile);
		return cropImageUri;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode && !content.getText().toString().equals("")) {
		   final AlertDialog.Builder builder = new AlertDialog.Builder(this);
           final AlertDialog ad = builder.create();
           ad.show();
           builder.setMessage("要放弃发布公告吗?").setPositiveButton("是", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
            	   ad.dismiss();
            	   EditActivity.this.finish();
            	   overridePendingTransition(R.anim.umeng_fb_slide_in_from_left, 
            			   R.anim.umeng_fb_slide_out_from_right);
               }
           }).setNegativeButton("否", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   ad.dismiss();
               }
           });
           //dialog.setCancelable(true);
           builder.show();
           return true;
		} 
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putParcelable("image", cropBitmap);
		outState.putString("content", content.getText().toString());
	}
	
	/**
	 * baidu云存储
	 */
	/*protected void saveData() {

		pubContent = new PubContent(content.getText().toString(), DateUtil.getCurrentDateTime());
		FrontiaAccount user = Frontia.getCurrentAccount();
		FrontiaACL acl = new FrontiaACL();
		acl.setAccountReadable(user, true);
		acl.setAccountWritable(user, true);
		try {
			json = new ObjectMapper().writeValueAsString(pubContent);
			showShortToast(json);
		} catch (JsonGenerationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {				
			data = new FrontiaData(new JSONObject(json));
			data.setACL(acl);
			mStorage.insertData(data, new DataInsertListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					showShortToast("发表成功");
				}
				
				@Override
				public void onFailure(int errCode, String errMsg) {
					// TODO Auto-generated method stub
					Log.d("data","错误为"+errCode+errMsg);
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(cameraFile.exists()) {			
			file = new FrontiaFile();
			file.setNativePath(cameraFile.getAbsolutePath());
			file.setRemotePath("publish_image/"+System.currentTimeMillis()+".jpg");
			file.setACL(acl);
			mStorage.uploadFile(file, new FileProgressListener() {
				
				@Override
				public void onProgress(String source, long bytes, long total) {
					// TODO Auto-generated method stub
					Log.d("image","正在上传"+source+",已经上传"+bytes+",一共"+total);
				}
			}, new FileTransferListener() {
				
				@Override
				public void onSuccess(String source, String target) {
					// TODO Auto-generated method stub
					Log.d("image", "上传成功");
				}
				
				@Override
				public void onFailure(String source, int errCode, String errMsg) {
					// TODO Auto-generated method stub
					Log.d("image","源文件为"+source+",错误为"+errCode+errMsg);					
				}
			});				
		}

	}*/
	
	class PublishTaskAsync extends AsyncTask<String, Void, Boolean> {					

		private HttpURLConnection conn;
		private boolean flag;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showAlertDialog("温馨提示", "正在发布请稍等一下~");
		}
		
		@Override
		protected Boolean doInBackground(String... params) {	
			//Afinal网络传输
			/*if(cameraFile==null) {
				param = new AjaxParams();
				param.put("content", content.getText().toString());
				param.put("sendtime", DateUtil.getCurrentDateTime());
				param.put("headimage", shared.getString(UserLoginActivity.PIC, ""));
				param.put("username", shared.getString(UserLoginActivity.UID, ""));
				FinalHttp http = new FinalHttp();
				http.post(params[0], param, new AjaxCallBack<Object>() {

					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, errorNo, strMsg);
						flag = false;
					}

					@Override
					public void onSuccess(Object t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						flag = true;
					}

				});
			} 
			param = new AjaxParams();
			try {
				param.put("imagefile", cameraFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			param.put("content", content.getText().toString());
			param.put("sendtime", DateUtil.getCurrentDateTime());
			param.put("headimage", shared.getString(UserLoginActivity.PIC, ""));
			param.put("username", shared.getString(UserLoginActivity.UID, ""));
			FinalHttp fh = new FinalHttp();
			fh.post(params[0], param, new AjaxCallBack<Object>() {
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					// TODO Auto-generated method stub
					super.onFailure(t, errorNo, strMsg);
					flag = false;
				}

				@Override
				public void onSuccess(Object t) {
					// TODO Auto-generated method stub
					super.onSuccess(t);
					flag = true;
				}
			});*/
			
			
		
			 
			//json格式数据网络传输			
			StringBuffer result = new StringBuffer();
			pubContent = new PubContent(content.getText().toString(),
					DateUtil.getCurrentDateTime(), shared.getString(
							UserLoginActivity.PIC, ""), shared.getString(
							UserLoginActivity.UID, ""));
			try {
				byte[] data = new ObjectMapper().writeValueAsBytes(pubContent);
				URL url = new URL(params[0]);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				conn.setReadTimeout(5000);
				conn.setRequestProperty("Content-Type",
						"text/plain; charset=UTF-8");
				conn.setRequestProperty("Content-Length",
						String.valueOf(data.length));
				OutputStream out = conn.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
				if (conn.getResponseCode() == 200) {
					byte[] connbuffer = new byte[1024];
					InputStream in = conn.getInputStream();
					while (in.read(connbuffer) != -1) {
						result.append(new String(connbuffer, "utf-8"));
					}
					info = result.toString();
					in.close();
					conn.disconnect();
					flag = true;
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				conn.disconnect();
			}	
			if(cameraFile!=null) {
				info = uploadImage(cropBitmap, params[0]);
				if(info.equals("图片上传失败")) {
					flag = false;
				}
			}									
			
			return flag;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAlertDialog.dismiss();
			if (result) {
				showLongToast(info);				
				Intent intent = new Intent(EditActivity.this, BulletinActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.umeng_fb_slide_in_from_left, 
						R.anim.umeng_fb_slide_out_from_right);				
			} else {
				showLongToast("网络出现问题，请稍后再试");				
			}
		}		
		
	}

	/**图片上传服务器
	 * @param bitmap 上传图片
	 * @param url 服务器servlet地址
	 * @return 服务器响应信息
	 */
	public static String uploadImage(Bitmap bitmap, String url) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] b = stream.toByteArray();
        // 将图片流以字符串形式存储下来
        String file = new String(Base64.encode(b));
        HttpClient client = new DefaultHttpClient();
        // 设置上传参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("file", file));
        HttpPost post = new HttpPost(url);
        UrlEncodedFormEntity entity;
        try {
                entity = new UrlEncodedFormEntity(formparams, "UTF-8");
                post.addHeader("Accept",
                                "text/javascript, text/html, application/xml, text/xml");
                post.addHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
                post.addHeader("Accept-Encoding", "gzip,deflate,sdch");
                post.addHeader("Connection", "Keep-Alive");
                post.addHeader("Cache-Control", "no-cache");
                post.addHeader("Content-Type", "application/x-www-form-urlencoded");
                post.setEntity(entity);
                HttpResponse response = client.execute(post);               
                HttpEntity e = response.getEntity();                
                if (200 == response.getStatusLine().getStatusCode()) {
                	client.getConnectionManager().shutdown();    
                	return EntityUtils.toString(e);
                } else {
                	client.getConnectionManager().shutdown();                    	
                }
                
        } catch (Exception e) {
                e.printStackTrace();
        }
		return "图片上传失败";
	}
	
	
}
