package com.smarthome.client2.familySchool.model;


/**
 * @author n003913 一个时间点的考勤信息（教师页面）
 *
 */
public class CardOne {

	private String time;// 打卡时间
	private String state;// 状态（正常、迟到、早退）
	private String type;// 进校、出校
	
	public CardOne(String time, String state, String type) {
		super();
		this.time = time;
		this.state = state;
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public String getState() {
		return state;
	}

	public String getType() {
		return type;
	}
	
}
