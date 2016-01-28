package com.smarthome.client2.familySchool.model;

import java.util.ArrayList;

/**
 * @author n003913 课程表实体类
 * 
 */
public class Syllabus {
	
	private long id;// 课表在数据库中的id
	private int amNum = 0;// 上午最多课程数
	private int pmNum = 0;// 下午最多课程数
	private int beforeAm = 0;// 早自习最多课程数
	private int afterPm = 0;// 晚自习最多课程数
	private String date;// 课表更新日期
	private ArrayList<SyllabusItem> list;

	public Syllabus(long id, int amNum, int pmNum, int beforeAm, int afterPm,
			String date, ArrayList<SyllabusItem> list) {
		this.id = id;
		this.amNum = amNum;
		this.pmNum = pmNum;
		this.beforeAm = beforeAm;
		this.afterPm = afterPm;
		this.date = date;
		this.list = list;
	}

	public int getAmNum() {
		return amNum;
	}

	public void setAmNum(int amNum) {
		this.amNum = amNum;
	}

	public int getPmNum() {
		return pmNum;
	}

	public void setPmNum(int pmNum) {
		this.pmNum = pmNum;
	}

	public int getBeforeAm() {
		return beforeAm;
	}

	public void setBeforeAm(int beforeAm) {
		this.beforeAm = beforeAm;
	}

	public int getAfterPm() {
		return afterPm;
	}

	public void setAfterPm(int afterPm) {
		this.afterPm = afterPm;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public long getId() {
		return id;
	}

	public ArrayList<SyllabusItem> getList() {
		return list;
	}

	public void setList(ArrayList<SyllabusItem> list) {
		this.list = list;
	}

	/**
	 * 获取课程中对应位置的课程名称
	 * 
	 * @param type
	 *            0早自习，1上午，2下午，3晚自习
	 * @param periodSort
	 *            对应时间段中的课程顺序（如下午第二节）
	 * @param weekday
	 *            周几（1，2，3，4，5）
	 * @return String
	 */
	public String getPositionSubject(int type, int periodSort, int weekday) {
		/*
		 * 计算在当天课程中的总顺序
		 */
		if (type == 1) {
			periodSort = beforeAm + periodSort;
		} else if (type == 2) {
			periodSort = beforeAm + amNum + periodSort;
		} else if (type == 3) {
			periodSort = beforeAm + amNum + pmNum + periodSort;
		}
		int size = list.size();
		SyllabusItem item;
		for (int i = 0; i < size; i++) {
			item = list.get(i);
			if (item.getType() == type && item.getSort() == periodSort
					&& item.getWeekday() == weekday) {
				return item.getSubjectname();
			}
		}
		return "";
	}

}
