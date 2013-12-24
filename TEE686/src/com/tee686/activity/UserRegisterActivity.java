package com.tee686.activity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import com.casit.tee686.R;
import com.tee686.config.Urls;
import com.tee686.entity.UserInfoItem;
import com.tee686.https.NetWorkHelper;
import com.tee686.ui.base.BaseActivity;
import com.tee686.utils.IntentUtil;
import com.tee686.xmpp.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserRegisterActivity extends BaseActivity {

	public static String SharedName = "register";
	public static String UID = "uid";// 用户名
	public static String PWD = "pwd";// 密码
	public static String KEY = "key";// key
	public static String BIR = "bir";// 生日
	public static String PVC = "pvc";// 省份
	public static String CITY = "city";// 城市
	public static String SEX = "sex";// 性别
	public static String PIC = "pic";// 头像图片地址
	public static String TEL = "tel";// 手机号
	public static String PLA = "plat";// 第三方登陆平台
//	public static String REG = "reg";// 注册时间
	private String info;

	private EditText username;
	private EditText pwd;
	private TextView mobile;	
	private Button mCommunity;	
	private Button register;
	private LinearLayout gohome;
	private SharedPreferences share;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");	
	UserInfoItem userInfoItem;
//	Pattern p = Pattern.compile("\\w+[\\n\\r\\t]*");
	
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);
		initControl();
		
		// 获取本机手机号
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getLine1Number() != null && !"".equals(tm.getLine1Number())) {
			mobile.setText(tm.getLine1Number());
		}
		
		initSharedPreference();

		gohome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showLongToast(getResources().getString(R.string.user_center_error));
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				
				UID = username.getText().toString();
				PWD = pwd.getText().toString();
				checkUsername(UID, PWD);
				
				try  
			    {  
			        Registration reg = new Registration();  
			  
			        reg.setType(IQ.Type.SET);  
			        reg.setTo(XmppTool.getConnection().getServiceName());  
			        System.out.println(XmppTool.getConnection().getServiceName());  
			        reg.setUsername(UID);  
			        reg.setPassword(PWD);  
			        reg.addAttribute("android", "tee_createUser_android");  
			        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));  
			        PacketCollector collector = XmppTool.getConnection().createPacketCollector(filter);  
			        XmppTool.getConnection().sendPacket(reg);  
			        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());  
			  
			        collector.cancel();// 停止请求results（是否成功的结果）  
			        System.out.println(result);  
			        if (result == null)  
			        {  
			            Toast.makeText(getApplicationContext(), "服务器没有返回结果", Toast.LENGTH_SHORT)  
			                    .show();  
			        }  
			        else if (result.getType() == IQ.Type.ERROR)  
			        {  
			            if (result.getError().toString().equalsIgnoreCase("conflict(409)"))  
			            {  
			                Toast.makeText(getApplicationContext(), "这个账号已经存在",  
			                        Toast.LENGTH_SHORT).show();  
			            }  
			            else  
			            {  
			                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT)  
			                        .show();  
			            }  
			        }  
			        else if (result.getType() == IQ.Type.RESULT)  
			        {  
			            Toast.makeText(getApplicationContext(), "恭喜你注册成功",  
			                    Toast.LENGTH_SHORT).show();  
			            Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);  
			            // 注册成功将信息发送给主界面  
			            UserLoginActivity.UID = UID;  
			            UserLoginActivity.PWD = PWD;
			            startActivity(intent);  
			            //if (XmppTool.login(UID, PWD))  
			            //{  
			            //    startActivity(intent);  
			            //}  
			        }  
			    }  
			    catch (Exception ex)  
			    {  
			        ex.printStackTrace();  
			    }  
			}
		});
//<<<<<<< HEAD
//=======
		
		mCommunity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentUtil.start_activity(UserRegisterActivity.this, BulletinActivity.class);
			}
		});
//>>>>>>> temp
	}	

	protected void checkUsername(String name, String pwd) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, R.string.user_username, Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, R.string.user_pwd, Toast.LENGTH_SHORT).show();
			return;
		} else if (!NetWorkHelper.checkNetState(this)) {
			Toast.makeText(this, R.string.httpisNull, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// String loginUser = String.format(Urls.USER_LOGIN, name, pwd);
		new RegisterAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Urls.USER_REGISTER);
	}

	private void initSharedPreference() {
		// TODO Auto-generated method stub
		share = getSharedPreferences(SharedName, MODE_PRIVATE);
		if (share.contains(UID)) {
			username.setText(share.getString(UID, ""));						
		}
		
	}

	private void initControl() {
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.register_username);
		pwd = (EditText) findViewById(R.id.register_pwd);
		mobile = (TextView) findViewById(R.id.phone_number);		
//<<<<<<< HEAD
//=======
		mCommunity = (Button) findViewById(R.id.btn_community);		
//>>>>>>> temp
		register = (Button) findViewById(R.id.user_register);
		gohome = (LinearLayout) findViewById(R.id.Linear_above_toHome);
	}

	class RegisterAsyncTask extends AsyncTask<String, Void, Boolean> {
//<<<<<<< HEAD
//=======
		private HttpURLConnection conn;
		
//>>>>>>> temp
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showAlertDialog("温馨提示", "正在注册请稍等一下~");
		}

		@Override
		protected Boolean doInBackground(String... params) {
//<<<<<<< HEAD
//=======
			
//>>>>>>> temp
			StringBuffer result = new StringBuffer();
			userInfoItem = new UserInfoItem(mobile.getText().toString(), username.getText().toString(),
					pwd.getText().toString(), share.getString(SEX, ""), share.getString(BIR, ""), share.getString(PVC, ""), 
					share.getString(CITY, ""), format.format(new Date()), share.getString(PLA, ""));
			try {				
				byte[] data = new ObjectMapper().writeValueAsBytes(userInfoItem);
				URL url = new URL(params[0]);
//<<<<<<< HEAD
				//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//=======
				conn = (HttpURLConnection) url.openConnection();
//>>>>>>> temp
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				conn.setReadTimeout(5000);
				conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
				conn.setRequestProperty("Content-Length", String.valueOf(data.length));
				OutputStream out = conn.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
				if(conn.getResponseCode() == 200) {
					byte[] buffer = new byte[1024];
					InputStream in = conn.getInputStream();
					while(in.read(buffer) != -1) {
						result.append(new String(buffer, "utf-8"));
					}
					info = result.toString();
					in.close();
//<<<<<<< HEAD
//				} 
//				conn.disconnect();	
//				return true;
//			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//=======
					conn.disconnect();	
					return true;
				} 
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				conn.disconnect();
//>>>>>>> temp
			}
			return false;
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAlertDialog.dismiss();
			if (result) {
				showLongToast(info);
				Editor editor = getSharedPreferences(SharedName, MODE_PRIVATE)
						.edit();
				editor.putString(UID, username.getText().toString());
				editor.putString(PWD, pwd.getText().toString());				
				editor.putString(TEL, mobile.getText().toString());							
				editor.commit();
				IntentUtil.start_activity(UserRegisterActivity.this,
						UserCenterActivity.class);
				finish();
			} else {
//<<<<<<< HEAD
				//showLongToast("网络出现问题，请稍后再试");
				//username.setText("");
//=======
				showLongToast("网络出现问题，请稍后再试");				
//>>>>>>> temp
				pwd.setText("");
			}
		}
	}	
}
