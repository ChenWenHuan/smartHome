package com.smarthome.client2.familySchool.model;

import java.util.ArrayList;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * @author n003913 留言实体类（家长）
 */
public class LeaveMessage
{
    private String id;

    private String headUrl;// 头像地址

    private String publisher;// 留言发起者（老师姓名）

    private String publisherId;// 留言发起者（老师Id）

    private String time;// 留言发起时间

    private String content;// 留言内容

    private ArrayList<MessageReply> replies;// 回复列表

    private SpannableString spannable;

    public LeaveMessage(String id, String headUrl, String publisher,
            String publisherId, String time, String firstMessage,
            ArrayList<MessageReply> replies)
    {
        this.id = id;
        this.headUrl = headUrl;
        this.publisher = publisher;
        this.publisherId = publisherId;
        this.time = time;
        this.content = firstMessage;
        this.replies = replies;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPublisherId()
    {
        return publisherId;
    }

    public void setPublisherId(String publisherId)
    {
        this.publisherId = publisherId;
    }

    public String getHeadUrl()
    {
        return headUrl;
    }

    public void setHeadUrl(String headUrl)
    {
        this.headUrl = headUrl;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public ArrayList<MessageReply> getReplies()
    {
        return replies;
    }

    public void setReplies(ArrayList<MessageReply> replies)
    {
        this.replies = replies;
    }

    /**
     * 添加一条回复
     * @param reply
     */
    public void addReply(MessageReply reply)
    {
        if (replies == null)
        {
            replies = new ArrayList<MessageReply>();
        }
        replies.add(reply);
        spannable = null;
    }

    /**
     * 一条留言下的所有回复（格式化的，给家长看的）
     * @param childId
     * @param childName
     * @param color
     * @return
     */
    public SpannableString getFormatReply(String childId, String childName,
            int color)
    {
        if (spannable == null)
        {
            if (replies == null || replies.size() == 0)
            {
                return null;
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                int size = replies.size();
                for (int i = 0; i < size; i++)
                {
                    MessageReply reply = replies.get(i);
                    if (reply.getReply_user_id().equals(childId))
                    {
                        sb.append(childName)
                                .append(" 回复 ")
                                .append(publisher)
                                .append("：")
                                .append(reply.getContent())
                                .append("\n");
                    }
                    else
                    {
                        sb.append(publisher)
                                .append(" 回复 ")
                                .append(childName)
                                .append("：")
                                .append(reply.getContent())
                                .append("\n");
                    }
                }
                String formatReplies = sb.toString();
                formatReplies = formatReplies.substring(0,
                        formatReplies.length() - 1);
                spannable = new SpannableString(formatReplies);
                int len1 = childName.length();
                int len2 = publisher.length();
                int begin = 0;
                for (int i = 0; i < size; i++)
                {
                    MessageReply reply = replies.get(i);
                    if (reply.getReply_user_id().equals(childId))
                    {
                        spannable.setSpan(new ForegroundColorSpan(color),
                                begin,
                                begin + len1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        begin += (len1 + 4);
                        spannable.setSpan(new ForegroundColorSpan(color),
                                begin,
                                begin + len2,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        begin += (len2 + reply.getContent().length() + 2);
                    }
                    else
                    {
                        spannable.setSpan(new ForegroundColorSpan(color),
                                begin,
                                begin + len2,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        begin += (len2 + 4);
                        spannable.setSpan(new ForegroundColorSpan(color),
                                begin,
                                begin + len1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        begin += (len1 + reply.getContent().length() + 2);
                    }
                }
                return spannable;
            }
        }
        else
        {
            return spannable;
        }
    }
    
    public SpannableString getFormatReply( int color)
    {
        if (spannable == null)
        {
            if (replies == null || replies.size() == 0)
            {
                return null;
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                int size = replies.size();
                for (int i = 0; i < size; i++)
                {
                    MessageReply reply = replies.get(i);
                    String replayName = reply.getReply_username();
         
	                sb.append(replayName)
	                        .append(" 回复 ")	                        
	                        .append("：")
	                        .append(reply.getContent())
	                        .append("\n");
                   
                }
                String formatReplies = sb.toString();
                formatReplies = formatReplies.substring(0,
                        formatReplies.length() - 1);
                spannable = new SpannableString(formatReplies);
                
                int len2 = 0;
                int begin = 0;
                for (int i = 0; i < size; i++)
                {
                    MessageReply reply = replies.get(i);
                    int len1 = reply.getReply_username().length();
                    spannable.setSpan(new ForegroundColorSpan(color),
                            begin,
                            begin + len1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    begin += (len1 + 4);
                    spannable.setSpan(new ForegroundColorSpan(color),
                            begin,
                            begin + len2,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    begin += (len2 + reply.getContent().length() + 2);
                }
                return spannable;
            }
        }
        else
        {
            return spannable;
        }
    }

}
