package com.smarthome.client2.familySchool.model;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * @author n003913 留言回复实体类
 * 
 */
public class MessageReply
{

    private String id;

    private String message_id;

    private String reply_user_id;

    private String rev_user_id;

    private String content;

    private String reply_time;

    private String reply_username;

    private String rev_username;

    private SpannableString spannable;

    public MessageReply(String id, String message_id, String reply_user_id,
            String rev_user_id, String content, String reply_time,
            String reply_username, String rev_username)
    {
        this.id = id;
        this.message_id = message_id;
        this.reply_user_id = reply_user_id;
        this.rev_user_id = rev_user_id;
        this.content = content;
        this.reply_time = reply_time;
        this.reply_username = reply_username;
        this.rev_username = rev_username;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMessage_id()
    {
        return message_id;
    }

    public void setMessage_id(String message_id)
    {
        this.message_id = message_id;
    }

    public String getReply_user_id()
    {
        return reply_user_id;
    }

    public void setReply_user_id(String reply_user_id)
    {
        this.reply_user_id = reply_user_id;
    }

    public String getRev_user_id()
    {
        return rev_user_id;
    }

    public void setRev_user_id(String rev_user_id)
    {
        this.rev_user_id = rev_user_id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getReply_time()
    {
        return reply_time;
    }

    public void setReply_time(String reply_time)
    {
        this.reply_time = reply_time;
    }

    public String getReply_username()
    {
        return reply_username;
    }

    public String getRev_username()
    {
        return rev_username;
    }

    /**
     * 展现给老师看的一条回复
     * @return
     */
    public SpannableString getFormatReply(int color)
    {
        if (spannable == null)
        {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(reply_username)
                    .append(" 回复 ")
                    .append(rev_username)
                    .append("：")
                    .append(content);
            spannable = new SpannableString(sBuilder);
            int replyLen = reply_username.length();
            int revLen = rev_username.length();
            spannable.setSpan(new ForegroundColorSpan(color),
                    0,
                    replyLen,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(color),
                    replyLen + 4,
                    replyLen + 4 + revLen,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
        return spannable;
    }
}
