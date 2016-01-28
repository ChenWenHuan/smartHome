package com.smarthome.client2.familySchool.model;

/**
 * @author n003913
 * 一条考试信息
 */
public class Exam
{

    private long id;

    private String name;

    private String subject;

    private String date;

    /**
     * 成绩类型（0分数、1其他）
     */
    private int type;

    public Exam(long id, String name, String subject, String date, int type)
    {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.date = date;
        this.type = type;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getDate()
    {
        return date;
    }

    public int getType()
    {
        return type;
    }

}
