package com.smarthome.client2.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.adapter.LeaveMsgAdapterFamily.FamilyReplyListener;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaveMsgAdapterFamily_V11 extends BaseAdapter {

	private ArrayList<LeaveMessage> list;

    private LayoutInflater mInflater;

    private ImageDownLoader mLoader;
    
    private Handler imgHandler;

    private FamilyReplyListener replyListener;

    private int color;

    public LeaveMsgAdapterFamily_V11(Context mContext,
            ArrayList<LeaveMessage> list,Handler imgHandle,
            FamilyReplyListener listener)
    {
        this.list = list;
        replyListener = listener;
        mInflater = LayoutInflater.from(mContext);
       
        color = mContext.getResources().getColor(R.color.leave_msg_blue);
        mLoader = ImageDownLoader.getInstance();
        imgHandler = imgHandle;
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
    	LeaveMessage model = list.get(position);
        ViewWarpper warpper;
        if (convertView == null)
        {
            warpper = new ViewWarpper();
            convertView = warpper.getView();
            convertView.setTag(warpper);
            if (model.getHeadUrl().equals("")){
            	warpper.getIv_head().setTag(position);
            }else {
            	warpper.getIv_head().setTag(position + model.getHeadUrl());
            }
        }
        else
        {
            warpper = (ViewWarpper) convertView.getTag();
        }
        
       
        if (model.getHeadUrl().equals("")){
        	warpper.getIv_head().setTag(position);
        	warpper.getIv_head().setImageResource(R.drawable.ico_head_blue);
        }else {
        	warpper.getIv_head().setTag(position + model.getHeadUrl());
        	mLoader.downloadImage(model.getHeadUrl(), FsConstants.HANDLE_IMAGE, imgHandler,
        			position + model.getHeadUrl());
        }
     
        warpper.getTv_teacher().setText(model.getPublisher());
        warpper.getTv_time().setText(convertDateTime(model.getTime()));
        warpper.getTv_first_message().setText(model.getContent());
        SpannableString spanned = model.getFormatReply(color);
        if (spanned == null)
        {
            warpper.getTv_other_message().setVisibility(View.GONE);
            warpper.getIv_reply_line().setVisibility(View.GONE);
        }
        else
        {
            warpper.getTv_other_message().setVisibility(View.VISIBLE);
            warpper.getIv_reply_line().setVisibility(View.VISIBLE);
            warpper.getTv_other_message().setText(spanned);
        }
        warpper.refreshListener(model);
        return convertView;
    }
    
    private String convertDateTime(String dateTime){
    	String retStr = "";
    	
    	SimpleDateFormat formatter; 
    	
    	formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
    	try {
			Date ctime = formatter.parse(dateTime);
            Date d1 = new Date();
            long diff = d1.getTime() - ctime.getTime();
            long days = checktime(ctime.getTime());
            if (days == 0)
            {
                if (diff / 1000 / 60 <= 1)
                {
                    retStr = "刚刚";
                }
                else
                {
                	retStr = "今天 " + timeShow(ctime.getTime());
                }
            }
            else if (days == 1)
            {
            	retStr = "昨天 " + timeShow(ctime.getTime());
            }
            else if (days <= 30)
            {
            	retStr = "" + days + "天前";
            }
            else if (days > 30 && days < 365)
            {
            	retStr = monthShow(days);
            }
            else
            {
            	retStr = yearShow(days);
            }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	return retStr;
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

    private class ViewWarpper
    {
        private View view;

        private ImageView iv_head;

        private TextView tv_teacher;

        private TextView tv_time;

        private TextView tv_answer;

        private TextView tv_first_message;

        private TextView tv_other_message;
        
        private ImageView iv_reply_line;

        private LeaveMessage lMessage;

        private OnClickListener listener;

        public View getView()
        
        
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_leave_message_family,
                        null);
            }
            return view;
        }
        
        public ImageView getIv_reply_line(){
        	
        	if (iv_reply_line == null)
            {
        		iv_reply_line = (ImageView) view.findViewById(R.id.img_reply_line);
            }
            return iv_reply_line;
        	
        }

        public ImageView getIv_head()
        {
            if (iv_head == null)
            {
                iv_head = (ImageView) view.findViewById(R.id.iv_head);
            }
            return iv_head;
        }

        public TextView getTv_teacher()
        {
            if (tv_teacher == null)
            {
                tv_teacher = (TextView) view.findViewById(R.id.tv_teacher);
            }
            return tv_teacher;
        }

        public TextView getTv_time()
        {
            if (tv_time == null)
            {
                tv_time = (TextView) view.findViewById(R.id.tv_time);
            }
            return tv_time;
        }

        public TextView getTv_answer()
        {
            if (tv_answer == null)
            {
                tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            }
            return tv_answer;
        }

        public TextView getTv_first_message()
        {
            if (tv_first_message == null)
            {
                tv_first_message = (TextView) view.findViewById(R.id.tv_first_message);
            }
            return tv_first_message;
        }

        public TextView getTv_other_message()
        {
            if (tv_other_message == null)
            {
                tv_other_message = (TextView) view.findViewById(R.id.tv_other_message);
            }
            return tv_other_message;
        }

        public void refreshListener(LeaveMessage item)
        {
            lMessage = item;
            if (listener == null)
            {
                listener = new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        replyListener.onReply(lMessage);
                    }
                };
                getTv_answer().setOnClickListener(listener);
            }
        }

    }

    public interface FamilyReplyListener
    {
        void onReply(LeaveMessage msg);
    }

}
