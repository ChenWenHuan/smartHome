package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.MsgTarget;
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
 * @author n003913 老师查看一条留言的对象
 */
public class LookTargetAdapter extends BaseAdapter
{

    private ArrayList<MsgTarget> mList;

    private LayoutInflater mInflater;

    private ImageDownLoader loader;

    private onImageLoaderListener loaderListener;

    public LookTargetAdapter(ArrayList<MsgTarget> list, Context context)
    {
        mList = list;
        mInflater = LayoutInflater.from(context);
        loaderListener = new onImageLoaderListener()
        {
            @Override
            public void onImageLoader(Bitmap bitmap, String url)
            {
                notifyDataSetChanged();
            }
        };
        loader = ImageDownLoader.getInstance();
        loader.addListener(FsConstants.TARGET_IMAGE, loaderListener);
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
            convertView = mInflater.inflate(R.layout.fs_item_list_look_target,
                    null);
            holder = new Holder();
            holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        MsgTarget item = mList.get(position);
        Bitmap bitmap = loader.downloadImage(item.getHeadUrl(),
                FsConstants.TARGET_IMAGE);
        if (bitmap == null)
        {
            holder.ivHead.setImageResource(R.drawable.default_pictures);
        }
        else
        {
            holder.ivHead.setImageBitmap(bitmap);
        }
        holder.tvName.setText(item.getName());
        return convertView;
    }

    static class Holder
    {
        ImageView ivHead;

        TextView tvName;
    }

}
