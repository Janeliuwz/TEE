package com.tee686.service.base;

import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;

public  class BaseDao {
	ObjectMapper mObjectMapper = new ObjectMapper();

	protected Activity mActivity;
	
	public BaseDao(){};
	
	public BaseDao(Activity activity)
	{
		mActivity=activity;
	}

	
}
