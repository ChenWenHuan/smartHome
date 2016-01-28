package com.smarthome.client2.familySchool.model;

/**
 * @author n003913
 * 一条成绩信息（家长）
 */
public class ScoreFamily
{
    private long id;

    private String publisher;

    private String headUrl;

    private String date;

    private String examName;

    private String subject;

    private String score;

    private String rank;

    /**
     * 成绩类型（分数还是其他），分数有排名，其他无排名
     */
    private boolean isScore;

    public ScoreFamily(long id, String publisher, String headUrl, String date,
            String examName, String subject, String score, String rank,
            boolean isScore)
    {
        this.id = id;
        this.publisher = publisher;
        this.headUrl = headUrl;
        this.date = date;
        this.examName = examName;
        this.subject = subject;
        this.score = score;
        this.rank = rank;
        this.isScore = isScore;
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

    public String getExamName()
    {
        return examName;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getScore()
    {
        return score;
    }

    public String getRank()
    {
        return rank;
    }

    public boolean isScore()
    {
        return isScore;
    }
}
