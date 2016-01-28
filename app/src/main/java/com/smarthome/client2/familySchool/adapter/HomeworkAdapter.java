package com.smarthome.client2.familySchool.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.Homework;
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
 * 家庭作业适配器
 */
public class HomeworkAdapter extends BaseAdapter
{

    private ArrayList<Homework> mList;

    private LayoutInflater mInflater;

    private ImageDownLoader mLoader;

    private onImageLoaderListener mListener;

    private String strPreDate;

    private String strCurrentDate;

    public HomeworkAdapter(Context context, ArrayList<Homework> list)
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
        mLoader.addListener(FsConstants.HOMEWORK_IMAGE, mListener);
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
            convertView = mInflater.inflate(R.layout.fs_item_list_homework,
                    null);
            holder = new Holder();
            holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.tvPublisher = (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvSubject = (TextView) convertView.findViewById(R.id.tv_subject);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        Homework item = mList.get(position);

        holder.tvPublisher.setText(item.getPublisher());
        strCurrentDate = item.getDate();
        if(strCurrentDate.equals(strPreDate)) {
            holder.tvDate.setVisibility(View.GONE);
        }
        else {
            strPreDate = strCurrentDate;
        }
//        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyy-MM-dd");
//        String    date    =    sDateFormat.format(new    java.util.Date());

        Calendar cal   =   Calendar.getInstance();
        String today = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        cal.add(Calendar.DATE,   -1);
        String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());

        if (strCurrentDate.substring(0,10).equals(today.substring(0,10))) {
            holder.tvDate.setText("今天");
        }else if (strCurrentDate.substring(0,10).equals(yesterday.substring(0,10))) {
            holder.tvDate.setText("昨天");}
        else {
            holder.tvDate.setText(item.getDate());
        }

        holder.tvSubject.setText(item.getSubject());

        if(item.getSubject().equals("数学")) {
            holder.ivHead.setImageResource(R.drawable.math_sm11);
        }else if(item.getSubject().equals("物理")) {
            holder.ivHead.setImageResource(R.drawable.physics_sm11);
        }else if(item.getSubject().equals("英语")) {
            holder.ivHead.setImageResource(R.drawable.english_sm11);
        }else if(item.getSubject().equals("政治")) {
            holder.ivHead.setImageResource(R.drawable.politics_sm11);
        }else if(item.getSubject().equals("语文")) {
            holder.ivHead.setImageResource(R.drawable.chinese_sm11);
        }else {
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
        }

        holder.tvContent.setText(item.getContent());
        return convertView;
    }

    static class Holder
    {
        ImageView ivHead;

        TextView tvPublisher;

        TextView tvDate;

        TextView tvSubject;

        TextView tvContent;
    }

}
