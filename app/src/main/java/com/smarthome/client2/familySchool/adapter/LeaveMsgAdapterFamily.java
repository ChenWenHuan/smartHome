package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author n003913 留言适配器（家长）
 */
public class LeaveMsgAdapterFamily extends BaseAdapter
{

    private ArrayList<LeaveMessage> list;

    private LayoutInflater mInflater;

    private ImageDownLoader loader;

    private onImageLoaderListener loaderListener;

    private String childName;

    private String childId;

    private FamilyReplyListener replyListener;

    private int color;

    public LeaveMsgAdapterFamily(Context mContext,
            ArrayList<LeaveMessage> list, String childId, String childName,
            FamilyReplyListener listener)
    {
        this.list = list;
        this.childId = childId;
        this.childName = childName;
        replyListener = listener;
        mInflater = LayoutInflater.from(mContext);
        loaderListener = new onImageLoaderListener()
        {
            @Override
            public void onImageLoader(Bitmap bitmap, String url)
            {
                notifyDataSetChanged();
            }
        };
        loader = ImageDownLoader.getInstance();
        loader.addListener(FsConstants.LEAVE_MESSAGE_IMAGE, loaderListener);
        color = mContext.getResources().getColor(R.color.leave_msg_blue);
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
        ViewWarpper warpper;
        if (convertView == null)
        {
            warpper = new ViewWarpper();
            convertView = warpper.getView();
            convertView.setTag(warpper);
        }
        else
        {
            warpper = (ViewWarpper) convertView.getTag();
        }
        LeaveMessage model = list.get(position);
        Bitmap bitmap = loader.downloadImage(model.getHeadUrl(),
                FsConstants.LEAVE_MESSAGE_IMAGE);
        if (bitmap != null)
        {
            warpper.getIv_head().setImageBitmap(bitmap);
        }
        else
        {
            warpper.getIv_head().setImageResource(R.drawable.ico_head_blue);
        }
        warpper.getTv_teacher().setText(model.getPublisher());
        warpper.getTv_time().setText(model.getTime());
        warpper.getTv_first_message().setText(model.getContent());
        SpannableString spanned = model.getFormatReply(childId,
                childName,
                color);
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
