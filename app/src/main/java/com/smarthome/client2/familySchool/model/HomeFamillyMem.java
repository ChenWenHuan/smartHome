package com.smarthome.client2.familySchool.model;

public class HomeFamillyMem {
	
	private String headUrl;
	private String memId;
	private String memName;
	private String memAddress;
	
	
	public HomeFamillyMem(String memId, String headUrl, String memName, String memAddress){
		
		this.headUrl = headUrl;
		this.memId = memId;
		this.memName = memName;
		this.memAddress = memAddress;
	}
	
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public String getMemId() {
		return memId;
	}
	public void setMemId(String memId) {
		this.memId = memId;
	}
	public String getMemName() {
		return memName;
	}
	public void setMemName(String memName) {
		this.memName = memName;
	}
	public String getMemAddress() {
		return memAddress;
	}
	public void setMemAddress(String memAddress) {
		this.memAddress = memAddress;
	}


	
	
}
