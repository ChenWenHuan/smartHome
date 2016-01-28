package com.smarthome.client2.familySchool.model;

import java.util.ArrayList;

/**
 * @author n003913 一个学生一天的考勤实体类（教师用）
 *
 */
public class CardAnalysis {

	private long id;
	private String name;
	private ArrayList<CardOne> list;
	
	public CardAnalysis(long id, String name, ArrayList<CardOne> list) {
		this.id = id;
		this.name = name;
		this.list = list;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<CardOne> getList() {
		return list;
	}
	
}
