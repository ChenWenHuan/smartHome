package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthome.client2.common.TLog;
import com.smarthome.client2.familySchool.model.Notice;
import com.smarthome.client2.familySchool.ui.ImageZoomActivity;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;

/**
 * @author n003913
 * 通知公告适配器（教师、家长）
 */
public class NoticeAdapter extends BaseAdapter
{

    private Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<Notice> mList;

    private boolean forTeacher;

    private ImageDownLoader mLoader;

    private onImageLoaderListener mListener;

    public NoticeAdapter(Context context, ArrayList<Notice> list,
            boolean isforTeacher)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        forTeacher = isforTeacher;
        mLoader = ImageDownLoader.getInstance();
        mListener = new onImageLoaderListener()
        {
            @Override
            public void onImageLoader(Bitmap bitmap, String url)
            {
                notifyDataSetChanged();
            }
        };
        mLoader.addListener(FsConstants.NOTICE_IMAGE, mListener);
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
        final Holder holder;
        final int nLineCounts =0;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.fs_item_list_notice, null);
            holder = new Holder();
            holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.tvPublisher = (TextView) convertView.findViewById(R.id.tv_publisher);
            if (forTeacher)
            {
                holder.tvTarget = (TextView) convertView.findViewById(R.id.tv_target);
                holder.tvTarget.setVisibility(View.VISIBLE);
            }
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvText = (TextView) convertView.findViewById(R.id.tv_text);
            holder.tvTextMore = (TextView) convertView.findViewById(R.id.tv_text_more);
            holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);

            holder.tvTextMore.setOnClickListener(new OnClickListener()
            {
                Boolean showContentFlag = false;

                @Override
                public void onClick(View v)
                {
                    if(!showContentFlag) {
                        holder.tvTextMore.setText("折叠");
                        holder.tvText.setEllipsize(null);
                        showContentFlag = true ;
                        TLog.Log("NoticeAdapter---tvTextMore"+ "!showContentFlag"+showContentFlag);
                    }
                    else {
                        holder.tvText.setLines(2);
                        holder.tvTextMore.setText("全文");
                        holder.tvText.setEllipsize(TextUtils.TruncateAt.END);
                        showContentFlag = false;
                        TLog.Log("NoticeAdapter---tvTextMore"+ "showContentFlag"+showContentFlag);
                    }

                    notifyDataSetChanged();
                }
            });

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        Notice item = mList.get(position);
        Bitmap headBmp = mLoader.downloadImage(item.getHeadUrl(),
                FsConstants.NOTICE_IMAGE);
        if (headBmp == null)
        {
            holder.ivHead.setImageResource(R.drawable.default_pictures);
        }
        else
        {
            holder.ivHead.setImageBitmap(headBmp);
        }
        holder.tvPublisher.setText(item.getPublisher());
        if (forTeacher)
        {
            holder.tvTarget.setText(item.getTarget());
        }
        holder.tvDate.setText(item.getDate());
        String text = item.getText();

        if (text.isEmpty())
        {
            holder.tvText.setVisibility(View.GONE);
        }
        else
        {
            holder.tvText.setVisibility(View.VISIBLE);
            holder.tvText.setText(text);
            // 鉴于 点击 全文 刷新不灵敏，现在先去掉
//            if( text.length()  > 30) {
//                if(holder.tvTextMore.getText().equals("全文") ) {
//                    TLog.Log("NoticeAdapter---tvTextMore"+holder.tvText.getLineCount()+text.length()+ text);
//                    holder.tvText.setLines(2);
//                }
//                holder.tvTextMore.setVisibility(View.VISIBLE);
//            }
//            else {
//                holder.tvTextMore.setVisibility(View.INVISIBLE);
//            }
        }
        final String picUrl = item.getPicUrl();
        if (picUrl.isEmpty())
        {
            holder.ivPic.setVisibility(View.GONE);
        }
        else
        {
            holder.ivPic.setVisibility(View.GONE);
            Bitmap picBmp = mLoader.downloadImage(picUrl,
                    FsConstants.NOTICE_IMAGE);
            if (picBmp == null)
            {
                holder.ivPic.setImageResource(R.drawable.refresh);
            }
            else
            {
                holder.ivPic.setImageBitmap(picBmp);
            }
            holder.ivPic.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext,
                            ImageZoomActivity.class);
                    intent.putExtra("image_path", picUrl);
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    static class Holder
    {
        ImageView ivHead;

        TextView tvPublisher;

        TextView tvTarget;

        TextView tvDate;

        TextView tvText;

        TextView tvTextMore;

        ImageView ivPic;

    }


}
