package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.ScoreFamily;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author n003913
 * 成绩信息适配器（家长）
 */
public class ScoreAdapterFamily extends BaseAdapter
{

    private LayoutInflater mInflater;

    private ArrayList<ScoreFamily> mList;

    private ImageDownLoader mLoader;

    private onImageLoaderListener mListener;

    public ScoreAdapterFamily(Context context, ArrayList<ScoreFamily> list)
    {
        mInflater = LayoutInflater.from(context);
        mList = list;
        mLoader = ImageDownLoader.getInstance();
        mListener = new onImageLoaderListener()
        {
            @Override
            public void onImageLoader(Bitmap bitmap, String url)
            {
                notifyDataSetChanged();
            }
        };
        mLoader.addListener(FsConstants.SCORE_IMAGE, mListener);
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
            convertView = mInflater.inflate(R.layout.fs_item_list_score_family,
                    null);
            holder = new Holder();
            holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.tvPublisher = (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvExam = (TextView) convertView.findViewById(R.id.tv_exam);
            holder.tvSubject = (TextView) convertView.findViewById(R.id.tv_subject);
            holder.tvScore = (TextView) convertView.findViewById(R.id.tv_score);
            holder.tvRank = (TextView) convertView.findViewById(R.id.tv_rank);
            holder.tvRankHint = (TextView) convertView.findViewById(R.id.tv_rank_hint);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        ScoreFamily item = mList.get(position);
        Bitmap bitmap = mLoader.downloadImage(item.getHeadUrl(),
                FsConstants.HOMEWORK_IMAGE);
        if (bitmap == null)
        {
            holder.ivHead.setImageResource(R.drawable.default_pictures);
        }
        else
        {
            holder.ivHead.setImageBitmap(bitmap);
        }
        holder.tvPublisher.setText(item.getPublisher());
        holder.tvDate.setText(item.getDate());
        holder.tvExam.setText(item.getExamName());
        holder.tvSubject.setText(item.getSubject());
        holder.tvScore.setText(item.getScore());
        if (item.isScore())
        {
            holder.tvRankHint.setVisibility(View.VISIBLE);
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(item.getRank());
        }
        else
        {
            holder.tvRankHint.setVisibility(View.GONE);
            holder.tvRank.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class Holder
    {
        ImageView ivHead;

        TextView tvPublisher;

        TextView tvDate;

        TextView tvExam;

        TextView tvSubject;

        TextView tvScore;

        TextView tvRank;

        TextView tvRankHint;
    }

}
