package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.Exam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author n003913
 * 考试列表的适配器（教师）
 */
public class ExamAdapterTeacher extends BaseAdapter
{
    private LayoutInflater mInflater;

    private ArrayList<Exam> mList;

    public ExamAdapterTeacher(Context context, ArrayList<Exam> list)
    {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount()
    {
        return mList.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.fs_item_list_exam_teacher,
                    null);
            holder = new Holder();
            holder.tvExam = (TextView) convertView.findViewById(R.id.tv_exam_name);
            holder.tvSubject = (TextView) convertView.findViewById(R.id.tv_subject);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        Exam item = mList.get(position);
        holder.tvExam.setText(item.getName());
        holder.tvSubject.setText(item.getSubject());
        holder.tvDate.setText(item.getDate());
        return convertView;
    }

    static class Holder
    {
        TextView tvExam;

        TextView tvSubject;

        TextView tvDate;
    }

}
