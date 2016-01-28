package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.CardAnalysis;
import com.smarthome.client2.familySchool.view.AttendView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author n003913 出勤记录适配器（教师）
 */
public class AttendanceAdapterTeacher extends BaseAdapter
{

    private ArrayList<CardAnalysis> list;

    private LayoutInflater mInflater;

    public AttendanceAdapterTeacher(Context mContext,
            ArrayList<CardAnalysis> list)
    {
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = holder.getView();
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.getTvInfo().setData(list.get(position));
        return convertView;
    }

    private class ViewHolder
    {

        private View view;

        private AttendView tvInfo;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_attendance_teacher,
                        null);
            }
            return view;
        }

        public AttendView getTvInfo()
        {
            if (tvInfo == null)
            {
                tvInfo = (AttendView) view.findViewById(R.id.lineview);
            }
            return tvInfo;
        }

    }

}
