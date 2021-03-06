package com.tee686.activity;

import java.util.Locale;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tee686.https.HttpUtils;
import com.tee686.activity.UserCenterActivity;
import com.tee686.utils.IntentUtil;
import com.tee686.ui.base.BaseActivity;
import com.tee686.xmpp.XmppTool;
import com.tee686.config.Urls;
import com.tee686.https.NetWorkHelper;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.FrontiaUser.FrontiaUserDetail;
import com.baidu.frontia.FrontiaUser.SEX;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorizationListener.AuthorizationListener;
import com.baidu.frontia.api.FrontiaAuthorizationListener.UserInfoListener;
import com.baidu.frontia.demo.Conf;
import com.casit.tee686.R;

//import android.widget.Button;

public class UserLoginActivity extends BaseActivity {

	public static String SharedName = "register";
	// public static String SharedName1 = "register";
	public static String UID = "uid";// 用户名
	public static String PWD = "pwd";// 密码
	public static String KEY = "key";// key
	public static String BIR = "bir";// 生日
	public static String PVC = "pvc";// 省份
	public static String CITY = "city";// 城市
	public static String SEX = "sex";// 性别
	public static String PIC = "pic";// 头像图片地址
	public static String PLA = "plat";//第三方登陆平台
	public static String Scope_Basic = "basic";// 用户基本权限，可以获取用户的基本信息
	public static String Scope_Netdisk = "netdisk";// 获取用户在个人云存储中存放的数据
	
	public static String name;
	public static String password;

	private EditText editUserID;
	private EditText editPwd;
	private Button mCommunity;
	private Button btnEnter;
	private Button btnRegister;
	private String key;
	private ImageButton qqButton;
	private ImageButton weiboButton;
	private LinearLayout goHome;
	SharedPreferences share;

	protected String mQQToken;
	FrontiaAuthorization mAuthorization;	
	protected String mSinaWeiboToken;
	private String username;
	private String birthday;
	private String province;
	private String city;
	private SEX sex;
	private String picurl;
	private String platform;


	/**
	 * 初始化
	 */
	private void initControl() {
		qqButton = (ImageButton) findViewById(R.id.qqLogin);
		weiboButton = (ImageButton) findViewById(R.id.weiboLogin);
		editUserID = (EditText) findViewById(R.id.edittext_user_username);
		editPwd = (EditText) findViewById(R.id.edittext_user_pwd);
		btnEnter = (Button) findViewById(R.id.button_user_login);
		btnRegister = (Button) findViewById(R.id.button_user_register);
		goHome = (LinearLayout) findViewById(R.id.Linear_above_toHome);
		mCommunity = (Button) findViewById(R.id.btn_community);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Frontia.init(getApplicationContext(), Conf.APIKEY);
		setContentView(R.layout.user_login_uid);
		
		initControl();
		initSharePreferences();
		mAuthorization = Frontia.getAuthorization();		

		btnEnter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				name = editUserID.getText().toString();
				password = editPwd.getText().toString();
				String loginUser;
				
				if(checkUsername(name,password))
				{
					loginUser = String.format(Urls.USER_LOGIN, name, password);

					new LoginAsyncTask().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, loginUser, name, password);
				}

			}
		});
		
		qqButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startQQ();
			}
		});

		weiboButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startWeibo();
			}
		});

		goHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (share.contains(UserLoginActivity.KEY)
						&& !share.getString(UserLoginActivity.KEY, "").equals(
								"")) {
					IntentUtil.start_activity(UserLoginActivity.this,
							UserCenterActivity.class);
					finish();
				} else {
					showLongToast(getResources().getString(
							R.string.user_center_error));
				}
			}
		});
		
		mCommunity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentUtil.start_activity(UserLoginActivity.this, BulletinActivity.class);
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentUtil.start_activity(UserLoginActivity.this,
						UserRegisterActivity.class);
				finish();
			}
		});
	}

	protected boolean checkUsername(String name, String pwd) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, R.string.user_username, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, R.string.user_pwd, Toast.LENGTH_SHORT).show();
			return false;
		} else if (!NetWorkHelper.checkNetState(this)) {
			Toast.makeText(this, R.string.httpisNull, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	private void initSharePreferences() {
		// TODO Auto-generated method stub
		share = getSharedPreferences(SharedName, Context.MODE_PRIVATE);
		if (share.contains(UID)) {
			editUserID.setText(share.getString(UID, ""));
			editPwd.setText(share.getString(PWD, ""));
		}
	}

	protected void startWeibo() {
		// TODO Auto-generated method stub
		// mAuthorization.enableSinaWeiboSSO(Conf.SINA_APP_KEY);
//		List<String> scope = new ArrayList<String>();
//		scope.add(Scope_Basic);
//		scope.add(Scope_Netdisk);
		mAuthorization.authorize(this,
				FrontiaAuthorization.PlatformType.SINA_WEIBO,
				new AuthorizationListener() {

					@Override
					public void onSuccess(FrontiaUser result) {						
						mSinaWeiboToken = result.getAccessToken();	
						userinfo(mSinaWeiboToken);
									
//						Toast.makeText(UserLoginActivity.this, "用户名:"+username, Toast.LENGTH_SHORT).show();
						new RegisterAsyncTask()
								.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, username);
					}

					@Override
					public void onFailure(int errorCode, String errorMessage) {
						Toast.makeText(UserLoginActivity.this, "登陆失败",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onCancel() {
						Toast.makeText(UserLoginActivity.this, "登陆取消",
								Toast.LENGTH_SHORT).show();
					}

				});
	}

	protected void startQQ() {
		// TODO Auto-generated method stub
		// mAuthorization.enableQQSSO(Conf.QQ_APP_KEY);		
		mAuthorization.authorize(this, FrontiaAuthorization.PlatformType.QQ,
				new AuthorizationListener() {

					@Override
					public void onSuccess(FrontiaUser result) {
						/*
						 * String log = "social id: " + result.getId() + "\n" +
						 * "token: " + result.getAccessToken() + "\n" +
						 * "expired: " + result.getExpiresIn();
						 * Log.d("SocialLogin", log);*/						
						mQQToken = result.getAccessToken();	
						userinfo(mQQToken);
								
//						Toast.makeText(UserLoginActivity.this, "用户名:"+username, Toast.LENGTH_SHORT).show();
						new RegisterAsyncTask().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, username);
					}

					@Override
					public void onFailure(int errorCode, String errorMessage) {
						Toast.makeText(UserLoginActivity.this, "登陆失败",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onCancel() {
						Toast.makeText(UserLoginActivity.this, "登陆取消",
								Toast.LENGTH_SHORT).show();
					}

				});
	}

	class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {
		private boolean flag = false;
		private boolean server0Login(String param0) {
			String result = "";
			try {
				result = HttpUtils.getByHttpClient(UserLoginActivity.this, param0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();				
			}
			if(null == result) {
				flag = true;
				return false;
			}
			try {
				JSONObject jsonObj = new JSONObject(result);
				// JSONObject response = jsonObj.getJSONObject("response");
				if(jsonObj.getString("isErr").equals("no")) {
					key = jsonObj.getString("key");
					return true;
				}
				return false;

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		private boolean server1Login(final String IM_name, final String IM_pwd) {
			try  
		    {  
				if(!XmppTool.login(IM_name, IM_pwd))
					return false;
				return true;
		    }  
		    catch (Exception ex)  
		    { 
		    	//Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
		    	XmppTool.closeConnection();
		    	ex.printStackTrace();  
		    	return false;
		    }
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showAlertDialog("温馨提示", "正在登录请稍等一下~");
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub

			// if (!HttpUtils.isNetworkAvailable(UserLoginUidActivity.this)) {
			// showLongToast(getResources().getString(R.string.httpisNull));
			// return false;
			// }
			
			//TEE用户中心服务器登陆
			//if(!server0Login(params[0]))
			//	return false;
			//即时通讯服务器登陆
			if(!server1Login(params[1], params[2]))
				return false;
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAlertDialog.dismiss();
			if (result) {
				showLongToast("登录成功");
				share = getSharedPreferences(SharedName,Context.MODE_PRIVATE);
				Editor edit = share.edit();
				edit.putString(UID, editUserID.getText().toString());
				edit.putString(PWD, editPwd.getText().toString());
				edit.putString(KEY, key);
				edit.commit();
				IntentUtil.start_activity(UserLoginActivity.this,
						UserCenterActivity.class);
				finish();
			} else {
				if(flag) {
					showLongToast("网络连接失败，请稍后再试");
				} else {
					showLongToast(getResources().getString(R.string.user_login_error));
					editUserID.setText("");
					editPwd.setText("");
				}				
			}
		}

	}

	class RegisterAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showAlertDialog("温馨提示", "正在提交注册请稍等一下~");
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAlertDialog.dismiss();				
			if (result) {
				showLongToast("第三方帐号登录成功,请完善其他信息");
				Editor edit = getSharedPreferences(SharedName, MODE_PRIVATE)
						.edit();
				edit.putString(UID, username);
				edit.putString(KEY, key);
				edit.putString(BIR, birthday);
				edit.putString(PVC, province);
				edit.putString(CITY, city);
				edit.putString(SEX, sex.name().toLowerCase(Locale.ENGLISH)); 
				edit.putString(PIC, picurl);
				edit.putString(PLA, platform);
				edit.commit();
				IntentUtil.start_activity(UserLoginActivity.this,
						UserRegisterActivity.class);
				finish();
			} else {
				showLongToast(getResources().getString(
						R.string.user_3rdloginInfo_error));
				editUserID.setText("");
				editPwd.setText("");
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {						
			return null != params[0];
			
		}

	}

	private void userinfo(String accessToken) {
		mAuthorization.getUserInfo(accessToken, new UserInfoListener() {

			@Override
			public void onSuccess(FrontiaUserDetail result) {
				username = result.getName();
				birthday = result.getBirthday();
				city = result.getCity();
				province = result.getProvince();
				sex = result.getSex();
				picurl = result.getHeadUrl();
				platform = result.getPlatform();
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				Toast.makeText(UserLoginActivity.this, errMsg,
						Toast.LENGTH_SHORT).show();
			}
		});		

	}

}
