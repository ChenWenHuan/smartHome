package com.smarthome.client2.familySchool.model;

/**
 * @author n003913
 * 家庭作业的实体类
 */
public class Homework
{
    private long id;

    private String publisher;

    private String headUrl;

    private String date;

    private String subject;

    private String content;

    public Homework(long id, String publisher, String headUrl, String date,
            String subject, String content)
    {
        this.id = id;
        this.publisher = publisher;
        this.headUrl = headUrl;
        this.date = date;
        this.subject = subject;
        this.content = content;
    }

    public long getId()
    {
        return id;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public String getHeadUrl()
    {
        return headUrl;
    }

    public String getDate()
    {
        return date;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getContent()
    {
        return content;
    }

}
