package com.smarthome.client2.familySchool.model;

/**
 * @author n003913
 * 课程实体类
 */
public class Subject
{
    private String id;

    private String name;

    public Subject(String id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

}
