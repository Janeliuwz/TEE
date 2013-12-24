package com.tee686.entity;

import java.io.Serializable;

public class PubContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8421200143402665228L;
	private String content; 	
	private String sendtime;
	private String headimage;
	private String username;	
	private String imageFile;	
	private Integer pubId;	
	
	
	public PubContent(String content, String sendtime, String headimage, String username) {
		this.content = content;			
		this.sendtime = sendtime;
		this.headimage = headimage;
		this.username = username;
	}
	
	public PubContent() {
		super();
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}	
	

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}	

	public String getHeadimage() {
		return headimage;
	}

	public void setHeadimage(String headimage) {
		this.headimage = headimage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	public Integer getPubId() {
		return pubId;
	}

	public void setPubId(Integer pubId) {
		this.pubId = pubId;
	} 
	
	
}
