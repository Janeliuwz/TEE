package com.tee686.entity;

import java.io.Serializable;

public class UserInfoItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4083656511702918056L;
	private String mobile;
	private String username;
	private String pwd;
	private String sex;
	private String birthday;
	private String province;
	private String city;
	private Integer tm = 0; //用户积分
	private Integer tp = 0; //用户威望
	private Integer userid;
	private String regtime;
//	private String head_image_url;
	private String platform;//第三方登陆平台
//	private Integer flag = 0;
//	private Integer myselfid;
//	private boolean vip = false;//专家
//	private boolean vender = false;//厂商
	
	public UserInfoItem(String mobile, String username, String pwd, String sex,
			String birthday, String province, String city, String regtime,
			String platform) {
		super();
		this.mobile = mobile;
		this.username = username;
		this.pwd = pwd;
		this.sex = sex;
		this.birthday = birthday;
		this.province = province;
		this.city = city;
		this.regtime = regtime;		
		this.platform = platform;
	}
	
	public UserInfoItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getTm() {
		return tm;
	}
	public void setTm(Integer tm) {
		this.tm = tm;
	}
	public Integer getTp() {
		return tp;
	}
	public void setTp(Integer tp) {
		this.tp = tp;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getRegtime() {
		return regtime;
	}
	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}
	
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	/*public String getHead_image_url() {
		return head_image_url;
	}

	public void setHead_image_url(String head_image_url) {
		this.head_image_url = head_image_url;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public boolean isVender() {
		return vender;
	}

	public void setVender(boolean vender) {
		this.vender = vender;
	}

	public Integer getMyselfid() {
		return myselfid;
	}

	public void setMyselfid(Integer myselfid) {
		this.myselfid = myselfid;
	}
	*/
	

	
}


