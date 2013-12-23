package com.tee686.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.VideoView;

import com.alipay.android.appDemo4.AlixId;
import com.alipay.android.appDemo4.BaseHelper;
import com.alipay.android.appDemo4.MobileSecurePayHelper;
import com.alipay.android.appDemo4.MobileSecurePayer;
import com.alipay.android.appDemo4.PartnerConfig;
import com.alipay.android.appDemo4.ResultChecker;
import com.alipay.android.appDemo4.Rsa;
import com.casit.tee686.R;
import com.unionpay.UPPayAssistEx;

public class ME_RvioActivity extends Activity{

	protected static final String TAG = "ME_Rvio";
	private VideoView vv;
    private ImageButton ib;
	private ProgressDialog mProgress;
	private Dialog dialog = null;
	private View dialogView = null;
	private SimpleAdapter listAdapter;
//	protected String transid;	
//	private String result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q1_me_rvio);
		if(isPayed()) {
			contentShow();
		} else {
			dialogView = getLayoutInflater().inflate(R.layout.pay_list, null);
			dialog = new Dialog(this);
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	        dialog.setContentView(dialogView);
	        ListView listView = (ListView)dialogView.findViewById(R.id.paylist);
			initPayList();
			dialog.getWindow().setGravity(Gravity.CENTER);
	        //dialog.show();
	        //防止弹出dialog后窗口变暗
	        Window window = dialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.dimAmount =0f;
	        window.setAttributes(lp);
			
			//确认是否付款	        
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("确认付款");
        	builder.setMessage("更多精彩内容请付费后开启体验！");
        	builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ME_RvioActivity.this.dialog.show();
					/*String url = HttpUtil.BASE_URL + "servlet/TradeServlet";
					//使用NameValuePair来保存要传递的Post参数  
					List<NameValuePair> params = new ArrayList<NameValuePair>();  
					//添加要传递的参数  
					params.add(new BasicNameValuePair("orderDesc", this.getClass().getSimpleName()));
					HttpPost request = HttpUtil.getHttpPost(url);
					try {						
						request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result = HttpUtil.queryStringForPost(request);*/
					
				}
			});
        	builder.setNegativeButton(android.R.string.no, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ME_RvioActivity.this.finish();
					overridePendingTransition(R.anim.hold, R.anim.me_rvio_zoomout);
				}
			});
        	builder.show();
	        
			
        	listView.setAdapter(listAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					switch(arg2) {
					case 0:
						performAliPay();
						break;
					case 1:
						performUpPay();
						/*if(result != null && !result.equals("网络异常！")) {
							String queryUrl = HttpUtil.BASE_URL + "servlet/CallbackServlet";
							HttpPost queryRequest = HttpUtil.getHttpPost(queryUrl);
							transid = HttpUtil.queryStringForPost(queryRequest);
						}
						if(transid != null) {
							performUpPay();
						} else {
							Toast.makeText(ME_RvioActivity.this, "系统忙，请稍后再试", Toast.LENGTH_SHORT).show();
						}*/
						
						break;
					}
				}
			});			
			
		}
	}

	public boolean isPayed() {		
		// TODO Auto-generated method stub		
		File file = getFileStreamPath("pay.xml");
		if(file.exists()) {
			return true;
		}
		return false;
	}

	private void contentShow() {		  
		vv = (VideoView)findViewById(R.id.vv_q1_me_rvio);
		vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.me_rvio));
		
//		vv.setOnCompletionListener(onCompListener);
		vv.start();

        ib = (ImageButton)findViewById(R.id.me_rvio_btn);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ME_RvioActivity.this, Section686Activity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("sec", 4);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
	}			
	
	private void initPayList() {
		List<Map<String, Object>> contents = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("PIC", R.drawable.alipay);
		map.put("CONTENT", "支付宝交易");
		contents.add(map);
		/*map = new HashMap<String, Object>();
		map.put("PIC", R.drawable.up);
		map.put("CONTENT", "银联在线交易");
		contents.add(map);*/
		listAdapter = new SimpleAdapter(this, contents, R.layout.pay_list_content, new String[] {"PIC", "CONTENT"}, new int[] {R.id.paylist_pic, R.id.paylist_content});		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data == null) {
			return;
		}
		String str = data.getExtras().getString("pay_result");
		if(str.equalsIgnoreCase("success")) {
			Toast.makeText(this, "支付成功!", Toast.LENGTH_SHORT).show();
			setPayTag();
			dialog.dismiss();
			contentShow();			
		} else if (str.equalsIgnoreCase("fail")) {
			Toast.makeText(this, "支付失败!", Toast.LENGTH_SHORT).show();
			finish();
			overridePendingTransition(R.anim.hold, R.anim.me_rvio_zoomout);
		} else if(str.equalsIgnoreCase("cancel")) {
			Toast.makeText(this, "您已经取消了本次订单的支付", Toast.LENGTH_SHORT).show();
			finish();
			overridePendingTransition(R.anim.hold, R.anim.me_rvio_zoomout);
		}
	}

	private void setPayTag() {
		// TODO Auto-generated method stub
		//Uri uri = Uri.parse("android.resource://"+getPackageName()+"/pay.xml");		
		try {
			FileOutputStream fos = openFileOutput("pay.xml", MODE_PRIVATE);
			fos.write(1);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//getDir(uri.getPath(), MODE_PRIVATE).mkdir();
		
	}

	private void performUpPay() {
		// TODO Auto-generated method stub
		String serverMode = "01";
		int ret = UPPayAssistEx.startPay(this, null, null, getOutTradeNo(), serverMode);
		if(ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
			//安装assets中提供的银联支付插件
			UPPayAssistEx.installUPPayPlugin(this);
		}
	}

	private void performAliPay() {
		// check to see if the MobileSecurePay is already installed.
        // 检测安全支付服务是否被安装
        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
        mspHelper.detectMobile_sp();
        
        // start pay for this order.
 		// 根据订单信息开始进行支付
 		try {
 			// prepare the order info.
 			// 准备订单信息
 			String orderInfo = getOrderInfo();
 			// 这里根据签名方式对订单信息进行签名
 			String signType = getSignType();
 			String strsign = sign(signType, orderInfo);
 			Log.v("sign:", strsign);
 			// 对签名进行编码
 			strsign = URLEncoder.encode(strsign, "UTF-8");
 			// 组装好参数
 			String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
 					+ getSignType();
 			Log.v("orderInfo:", info);
 			// start the pay.
 			// 调用pay方法进行支付
 			MobileSecurePayer msp = new MobileSecurePayer();
 			boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, this);

 			if (bRet) {
 				// show the progress bar to indicate that we have started
 				// paying.
 				// 显示“正在支付”进度条
 				closeProgress();
 				mProgress = BaseHelper.showProgress(this, null, "正在支付", false,
 						true);
 			} else
 				;
 		} catch (Exception ex) {
 			Toast.makeText(this, R.string.remote_call_failed,
 					Toast.LENGTH_SHORT).show();
 		}
	}
	
	private String sign(String signType, String orderInfo) {
		// TODO Auto-generated method stub
		return Rsa.sign(orderInfo, PartnerConfig.RSA_PRIVATE);
	}

	private String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	/**
	 * 关闭进度框
	 */
	private void closeProgress() {
		// TODO Auto-generated method stub
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**获取商品订单信息
	 * @return
	 */
	private String getOrderInfo() {
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "out_trade_no=" + "\"" + getOutTradeNo() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "subject=" + "\"" + this.getClass().getSimpleName()
				+ "\"";
		strOrderInfo += "&";
		strOrderInfo += "body=" + "\"" + "食道中段右心室流入流出切面" + "\"";
		strOrderInfo += "&";
		strOrderInfo += "total_fee=" + "\""
				+ "6" + "\"";
		strOrderInfo += "&";
		strOrderInfo += "notify_url=" + "\""
				+ "http://210.75.239.227/payment/servlet/NotifyReceiverServlet" + "\"";

		return strOrderInfo;
	}

	/**获取外部订单号
	 * @return
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String strKey = format.format(date);

		Random r = new Random();
		strKey = strKey + r.nextInt();
		strKey = strKey.substring(0, 15);
		return strKey;
	}
	
	// the handler use to receive the pay result.
	// 这里接收支付结果，支付宝手机端同步通知
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String ret = (String) msg.obj;

				Log.e(TAG, ret); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();

					BaseHelper.log(TAG, ret);

					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = ret.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = ret.indexOf("};memo=");
						tradeStatus = ret.substring(imemoStart, imemoEnd);

						// 先验签通知
						ResultChecker resultChecker = new ResultChecker(ret);
						int retVal = resultChecker.checkSign();
						// 验签失败
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(
									ME_RvioActivity.this,
									"提示",
									getResources().getString(
											R.string.check_sign_failed),
									android.R.drawable.ic_dialog_alert);
							finish();
							overridePendingTransition(R.anim.hold, R.anim.me_rvio_zoomout);
						} else {// 验签成功。验签成功后再判断交易状态码
							if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
								BaseHelper.showDialog(ME_RvioActivity.this, "提示",
										"支付成功",
										R.drawable.infoicon);
								setPayTag();
								dialog.dismiss();
								contentShow();
							}
							else {
								BaseHelper.showDialog(ME_RvioActivity.this, "提示",
										"支付失败。交易状态码:" + tradeStatus,
										R.drawable.infoicon);
								finish();
								overridePendingTransition(R.anim.hold, R.anim.me_rvio_zoomout);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						/*BaseHelper.showDialog(ME_RvioActivity.this, "提示", ret,
								R.drawable.infoicon);*/
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/*OnCompletionListener onCompListener = new OnCompletionListener(){

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.me_rvio));

			vv.start();
		}		
	};*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            /*Intent intent = new Intent(this, Section686Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
            startActivity(intent);*/
            finish();
            overridePendingTransition(R.anim.hold, R.anim.me_rvio_zoomout);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*private String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED);   
        if(sdCardExist){
              //��ȡ��Ŀ¼ 
          sdDir = Environment.getExternalStorageDirectory();
        }   
        return sdDir.toString(); 
    } */
    protected void onRestart() {
        super.onResume();        
        vv = (VideoView)findViewById(R.id.vv_q1_me_rvio);
        vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.me_rvio));
//	        vv.setOnCompletionListener(onCompListener);
        vv.start();        
    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();

		Log.v(TAG, "onDestroy");

		try {
			mProgress.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
