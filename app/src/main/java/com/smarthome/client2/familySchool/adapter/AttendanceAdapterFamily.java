package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.CardLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author n003913 出勤记录适配器（家长）
 */
public class AttendanceAdapterFamily extends BaseAdapter
{

    private ArrayList<CardLog> list;

    private LayoutInflater mInflater;

    private final String inSchool;

    private final String outSchool;

    private final static String INTYPE = "0";

    private final static String OUTTYPE = "1";

    public AttendanceAdapterFamily(Context mContext, ArrayList<CardLog> list)
    {
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
        inSchool = mContext.getString(R.string.in_school);
        outSchool = mContext.getString(R.string.out_school);
    }

    public void setList(ArrayList<CardLog> list)
    {
        this.list = list;
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
        CardLog model = list.get(position);
        // 远程打卡，进校在左
        if (model.getType().equals(INTYPE))
        {
            holder.getTvLeft().setVisibility(View.VISIBLE);
            holder.getTvLeft().setText(inSchool + model.getTime());
            holder.getTvRight().setVisibility(View.INVISIBLE);
        }
        // 远程打卡，出校在右
        else if (model.getType().equals(OUTTYPE))
        {
            holder.getTvRight().setVisibility(View.VISIBLE);
            holder.getTvRight().setText(model.getTime() + outSchool);
            holder.getTvLeft().setVisibility(View.INVISIBLE);
        }
        // 近程打卡，统一在右
        else
        {
            holder.getTvRight().setVisibility(View.VISIBLE);
            holder.getTvRight().setText(model.getTime());
            holder.getTvLeft().setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private class ViewHolder
    {

        private View view;

        private TextView tvRight;

        private TextView tvLeft;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_attendance_familyl,
                        null);
            }
            return view;
        }

        public TextView getTvRight()
        {
            if (tvRight == null)
            {
                tvRight = (TextView) view.findViewById(R.id.tv_right);
            }
            return tvRight;
        }

        public TextView getTvLeft()
        {
            if (tvLeft == null)
            {
                tvLeft = (TextView) view.findViewById(R.id.tv_left);
            }
            return tvLeft;
        }

    }

}
