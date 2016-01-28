package com.smarthome.client2.widget;

public class MemberInfo {

	private String id;// 用户id
	private int calorie = 0;
	private int step = 0;
	private int aim = 0;
	private boolean hasSports = false;
	private String address = "地理位置未知";
	private String groupId;// 家庭组编码
	private String name;
	private String headName;
	private String headPath;

	public MemberInfo(String id, String headPath, String headName) {
		this.id = id;
		this.headPath = headPath;
		this.headName = headName;
	}

	public MemberInfo(String id, String headPath, String name,String location) {
		this.id = id;
		this.headPath = headPath;
		this.name = name;
		this.address = location;
	}

	public MemberInfo(String id, String groupId, String name,
	                  String headPath,String location) {
		this.id = id;
		this.groupId = groupId;
		this.name = name;
		this.headPath = headPath;
		this.address = location;

	}

	public MemberInfo(String id, String groupId, String name, String headPath,
	                  String headName,String location) {
		this.id = id;
		this.groupId = groupId;
		this.name = name;
		this.headName = headName;
		this.headPath = headPath;
		this.address = location;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getHeadUrl() {
		return headPath + headName;
	}

	public int getCalorie() {
		return calorie;
	}

	public int getStep() {
		return step;
	}

	public int getAim() {
		return aim;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCalorie(int calorie) {
		this.calorie = calorie;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void setAim(int aim) {
		this.aim = aim;
	}

	public boolean isHasSports() {
		return hasSports;
	}

	public void setHasSports(boolean hasSports) {
		this.hasSports = hasSports;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHeadName() {
		return headName;
	}

	public void setHeadName(String headName) {
		this.headName = headName;
	}

	public String getHeadPath() {
		return headPath;
	}

	public void setHeadPath(String headPath) {
		this.headPath = headPath;
	}

	@Override
	public String toString() {
		return "[" + id + "#" + name + "#" + groupId + "#" + headName + "#"
				+ headPath + "]";
	}

}
