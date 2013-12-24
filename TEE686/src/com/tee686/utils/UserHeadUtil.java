package com.tee686.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UserHeadUtil {

	private static byte[] data;
	
	public static Bitmap getUserHeadImage(String imageUrl) {
		try {								
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();				
			conn.setUseCaches(false);
			conn.setDoInput(true);				
			conn.setConnectTimeout(5000);					
			if(conn.getResponseCode() == 200) {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				InputStream in = conn.getInputStream();
				while((len = in.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				in.close();
				data = outStream.toByteArray();
				return BitmapFactory.decodeByteArray(data, 0, data.length);
				
			} 
			conn.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;			
	}
}
