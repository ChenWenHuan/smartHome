package com.smarthome.client2.familySchool.model;

/**
 * 
 * @author n003913 一节课程的信息
 * 
 */
public class SyllabusItem {

	private int weekday;// 周几
	private int sort;// 课程顺序
	private String subjectname;// 课程名称
	private int type;// 所属时段
	
	public SyllabusItem(int weekday, int sort, String subjectname,
			int type) {
		super();
		this.weekday = weekday;
		this.sort = sort;
		this.subjectname = subjectname;
		this.type = type;
	}

	public int getWeekday() {
		return weekday;
	}

	public int getSort() {
		return sort;
	}

	public String getSubjectname() {
		return subjectname;
	}

	public int getType() {
		return type;
	}

}
