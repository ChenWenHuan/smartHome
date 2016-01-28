package com.smarthome.client2.familySchool.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.SysMessageBean;
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

public class MessageListAdapter extends BaseAdapter {
	
    private Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<SysMessageBean> mMsgList;

    private ImageDownLoader mLoader;

    private onImageLoaderListener mListener;

    public MessageListAdapter(Context context, ArrayList<SysMessageBean> list){
    	
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mMsgList = list;
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
    @Override
    public int getCount()
    {
        return mMsgList.size();
    }

    @Override
    public SysMessageBean getItem(int position)
    {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
            ViewGroup parent)
    {

        Holder holder = null;

        if (null == convertView)
        {
            convertView = mInflater
                    .inflate(R.layout.item_list_view_user_message, null);
            holder = new Holder();
            holder.msg_item_img = (ImageView) convertView.findViewById(R.id.msg_item_img);
            holder.msg_item_title = (TextView) convertView.findViewById(R.id.msg_item_title);
            holder.tv_item_list_view_user_message = (TextView) convertView.findViewById(R.id.tv_item_list_view_user_message);
            holder.tv_item_list_view_user_message_time = (TextView) convertView.findViewById(R.id.tv_item_list_view_user_message_time);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        //          消息类型
        //          1:好友请求
        //          2:系统消息
        //          3:挑战赛请求
        //          4：家庭成员请求
        //          1:未处理
        //          2:同意
        //          3:拒绝
        final SysMessageBean bean = mMsgList.get(position);
        int s_img = R.drawable.default_touxiang;
        switch (Integer.parseInt(bean.msgtype)){
        case SysMessageBean.TYPE_01:
            s_img = R.drawable.msg_friend;
            break;
        case SysMessageBean.TYPE_02:
        	s_img = R.drawable.ico_class1_msg;
            break;
        }
        holder.tv_item_list_view_user_message.setText(bean.getContent());
        holder.msg_item_img.setImageResource(s_img);
        holder.msg_item_title.setText(bean.getTitle());

        Date d1 = new Date();
        long diff = d1.getTime() - bean.sendtime;
        long days = checktime(bean.sendtime);
        if (days == 0)
        {
            if (diff / 1000 / 60 <= 1)
            {
                holder.tv_item_list_view_user_message_time.setText("刚刚");
            }
            else
            {
                holder.tv_item_list_view_user_message_time.setText("今天 "
                        + timeShow(bean.sendtime));
            }
        }
        else if (days == 1)
        {
            holder.tv_item_list_view_user_message_time.setText("昨天 "
                    + timeShow(bean.sendtime));
        }
        else if (days <= 30)
        {
            holder.tv_item_list_view_user_message_time.setText("" + days
                    + "天前");
        }
        else if (days > 30 && days < 365)
        {
            holder.tv_item_list_view_user_message_time.setText(monthShow(days));
        }
        else
        {
            holder.tv_item_list_view_user_message_time.setText(yearShow(days));
        }

        return convertView;
    }
    
    class Holder
    {
        public TextView tv_item_list_view_user_message;
                
        public TextView tv_item_list_view_user_message_time;

        public TextView msg_item_title;

        public ImageView msg_item_img;
    }
    
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private String timeShow(long createTime)
    {
        Date d = new Date();
        d.setTime(createTime);
        return sdf.format(d);
    }

    private String monthShow(long days)
    {
        return days / 30 + "个月前";
    }

    private String yearShow(long days)
    {
        return days / 365 + "年前";
    }

    private long checktime(long time)
    {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        Calendar sendtime = Calendar.getInstance();
        sendtime.setTimeInMillis(time);
        sendtime.set(Calendar.HOUR_OF_DAY, 0);
        sendtime.set(Calendar.MINUTE, 0);
        sendtime.set(Calendar.SECOND, 0);
        long diff = now.getTimeInMillis() - sendtime.getTimeInMillis();
        long days = (diff / (1000 * 60 * 60 * 24));
        return days;
    }


}
