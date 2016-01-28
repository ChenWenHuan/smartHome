package com.smarthome.client2.familySchool.model;

/**
 * @author n003913
 * 一条通知公告（教师、家长）
 */
public class Notice
{
    private long id;

    private String headUrl;

    private String publisher;

    /**
     * 公告通知的对象
     */
    private String target;

    private String date;

    private String text;

    private String picUrl;

    public Notice(long id, String headUrl, String publisher, String target,
            String date, String text, String picUrl)
    {
        this.id = id;
        this.headUrl = headUrl;
        this.publisher = publisher;
        this.target = target;
        this.date = date;
        this.text = text;
        this.picUrl = picUrl;
    }

    public long getId()
    {
        return id;
    }

    public String getHeadUrl()
    {
        return headUrl;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public String getTarget()
    {
        return target;
    }

    public String getDate()
    {
        return date;
    }

    public String getText()
    {
        return text;
    }

    public String getPicUrl()
    {
        return picUrl;
    }

}
