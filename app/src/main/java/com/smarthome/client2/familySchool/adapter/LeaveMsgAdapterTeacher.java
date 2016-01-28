package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.MessageReplyAdapter.TeacherReplyListener;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.model.MessageReply;
import com.smarthome.client2.familySchool.ui.LookTargetActivity;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;
import com.smarthome.client2.familySchool.view.MyListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author n003913 留言适配器（教师）
 */
public class LeaveMsgAdapterTeacher extends BaseAdapter
{

    private Context mContext;

    private ArrayList<LeaveMessage> list;

    private LayoutInflater mInflater;

    private ImageDownLoader loader;

    private onImageLoaderListener loaderListener;

    private String headUrl;

    private String teacherName;

    private TeacherReplyListener replyListener;

    public LeaveMsgAdapterTeacher(Context mContext,
            ArrayList<LeaveMessage> list, TeacherReplyListener replyListener)
    {
        this.mContext = mContext;
        this.list = list;
        this.replyListener = replyListener;
        mInflater = LayoutInflater.from(mContext);
        Preferences preferences = Preferences.getInstance(mContext);
        headUrl = preferences.getAccountHeadUrl();
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
        teacherName = Preferences.getInstance(mContext).getRealName();
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
        Bitmap bitmap = loader.downloadImage(headUrl,
                FsConstants.LEAVE_MESSAGE_IMAGE);
        if (bitmap != null)
        {
            warpper.getIvHead().setImageBitmap(bitmap);
        }
        else
        {
            warpper.getIvHead().setImageResource(R.drawable.default_pictures);
        }
        warpper.getTvTeacher().setText(teacherName);
        warpper.getTvTime().setText(model.getTime());
        warpper.getTvMessage().setText(model.getContent());
        warpper.refreshAdapter(model);
        warpper.refreshListener(model.getId());
        return convertView;
    }

    private class ViewWarpper
    {
        private View view;

        private ImageView ivHead;

        private TextView tvTeacher;

        private TextView tvTime;

        private TextView tvMessage;

        private MyListView lvReply;

        private MessageReplyAdapter replyAdapter;

        private String msgId;

        private OnClickListener listener;

        private ImageView ivTarget;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_leave_message_teacher,
                        null);
            }
            return view;
        }

        public ImageView getIvHead()
        {
            if (ivHead == null)
            {
                ivHead = (ImageView) view.findViewById(R.id.iv_head);
            }
            return ivHead;
        }

        public TextView getTvTeacher()
        {
            if (tvTeacher == null)
            {
                tvTeacher = (TextView) view.findViewById(R.id.tv_teacher);
            }
            return tvTeacher;
        }

        public TextView getTvTime()
        {
            if (tvTime == null)
            {
                tvTime = (TextView) view.findViewById(R.id.tv_time);
            }
            return tvTime;
        }

        public TextView getTvMessage()
        {
            if (tvMessage == null)
            {
                tvMessage = (TextView) view.findViewById(R.id.tv_first_message);
            }
            return tvMessage;
        }

        public MyListView getLvMessage()
        {
            if (lvReply == null)
            {
                lvReply = (MyListView) view.findViewById(R.id.lv_other_message);
            }
            return lvReply;
        }

        public void refreshAdapter(LeaveMessage message)
        {
            ArrayList<MessageReply> childList = message.getReplies();
            getLvMessage();
            if (childList == null || childList.size() == 0)
            {
                lvReply.setVisibility(View.GONE);
            }
            else
            {
                lvReply.setVisibility(View.VISIBLE);
                if (replyAdapter == null)
                {
                    MessageReplyAdapter adapter = new MessageReplyAdapter(
                            message, mContext, replyListener);
                    lvReply.setAdapter(adapter);
                }
                else
                {
                    replyAdapter.setMessage(message);
                }
            }
        }

        public void refreshListener(String id)
        {
            msgId = id;
            if (listener == null)
            {
                listener = new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(mContext,
                                LookTargetActivity.class);
                        intent.putExtra("msgId", msgId);
                        mContext.startActivity(intent);
                    }
                };
                if (ivTarget == null)
                {
                    ivTarget = (ImageView) view.findViewById(R.id.iv_target);
                    ivTarget.setOnClickListener(listener);
                }
            }
        }
    }

}
