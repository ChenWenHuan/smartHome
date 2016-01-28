package com.smarthome.client2.familySchool.model;

/**
 * @author n003913 一个学生信息（id、name）
 */
public class MsgTarget
{

    private String id;

    private String name;

    private boolean checked;

    private String score;

    private String headUrl;

    private String userId;

    public MsgTarget()
    {
        super();
    }

    public MsgTarget(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public MsgTarget(String id, String name, String userId)
    {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public String getHeadUrl()
    {
        return headUrl;
    }

    public void setNameHead(String name, String head)
    {
        this.name = name;
        this.headUrl = head;
    }
}
