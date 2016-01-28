package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.ScoreTeacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author n003913
 * 成绩信息适配器（教师）
 */
public class ScoreAdapterTeacher extends BaseAdapter
{

    private LayoutInflater mInflater;

    private ArrayList<ScoreTeacher> mList;

    private boolean isScore = false;

    public ScoreAdapterTeacher(Context context, ArrayList<ScoreTeacher> list,
            boolean isScore)
    {
        mInflater = LayoutInflater.from(context);
        mList = list;
        this.isScore = isScore;
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
            convertView = mInflater.inflate(R.layout.fs_item_list_score_teacher,
                    null);
            holder = new Holder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvScore = (TextView) convertView.findViewById(R.id.tv_score);
            holder.tvRank = (TextView) convertView.findViewById(R.id.tv_rank);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        ScoreTeacher item = mList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvScore.setText(item.getScore());
        if (isScore)
        {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(item.getRank());
        }
        else
        {
            holder.tvRank.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class Holder
    {
        TextView tvName;

        TextView tvScore;

        TextView tvRank;
    }

}
