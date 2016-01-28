package com.smarthome.client2.familySchool.model;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

/**
 * @author n003913 班级圈中联系人实体类
 * 
 */
public class ClassContacts
{

    private String name;

    private String phone;

    private String note;

    private Spannable spannable;

    public ClassContacts(String name, String phone, String note)
    {
        this.name = name;
        this.phone = phone;
        this.note = note;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public Spannable getSpannable(int size1, int size2, int color1, int color2)
    {
        if (spannable == null)
        {
            int len1 = name.length();
            int len2 = note.length();
            spannable = new SpannableString(name + note);
            spannable.setSpan(new AbsoluteSizeSpan(size1),
                    0,
                    len1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AbsoluteSizeSpan(size2),
                    len1,
                    len1 + len2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(color1),
                    0,
                    len1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(color2), len1, len1
                    + len2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

}
