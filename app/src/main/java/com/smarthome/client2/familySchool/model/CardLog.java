package com.smarthome.client2.familySchool.model;

/**
 * @author n003913 打卡记录实体类（考勤页面，家长用）
 *
 */
public class CardLog {

	private String time;// 打卡时间
	private String type;// 打卡类型，进校、出校
	
	public CardLog(String time, String type) {
		super();
		this.time = time;
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public String getType() {
		return type;
	}
	
}
