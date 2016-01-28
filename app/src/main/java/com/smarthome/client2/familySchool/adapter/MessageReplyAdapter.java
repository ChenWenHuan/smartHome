package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.model.MessageReply;

/**
 * @author n003913 留言回复适配器
 */
public class MessageReplyAdapter extends BaseAdapter
{

    private ArrayList<MessageReply> list;

    private LeaveMessage leaveMsg;

    private Context mContext;

    private LayoutInflater mInflater;

    private String userId;

    private int color;

    private TeacherReplyListener replyListener;

    public MessageReplyAdapter(LeaveMessage leaveMsg, Context mContext,
            TeacherReplyListener replyListener)
    {
        this.leaveMsg = leaveMsg;
        this.list = leaveMsg.getReplies();
        this.mContext = mContext;
        this.replyListener = replyListener;
        mInflater = LayoutInflater.from(mContext);
        userId = Preferences.getInstance(mContext).getUserID();
        color = mContext.getResources().getColor(R.color.leave_msg_blue);
    }

    public ArrayList<MessageReply> getList()
    {
        return list;
    }

    /**
     * 更新数据源，并刷新
     * @param message
     */
    public void setMessage(LeaveMessage message)
    {
        leaveMsg = message;
        list = message.getReplies();
        notifyDataSetChanged();
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
        Holder holder;
        if (convertView == null)
        {
            holder = new Holder();
            convertView = holder.getView();
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        MessageReply reply = list.get(position);
        holder.getTv().setText(reply.getFormatReply(color));
        holder.refreshListener(reply);
        return convertView;
    }

    private class Holder
    {
        private View view;

        private TextView tv;

        private OnClickListener listener;

        private MessageReply reply;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_leave_message_reply,
                        null);
            }
            return view;
        }

        public TextView getTv()
        {
            if (tv == null)
            {
                tv = (TextView) view.findViewById(R.id.tv_reply);
            }
            return tv;
        }

        public void refreshListener(MessageReply item)
        {
            reply = item;
            if (listener == null)
            {
                listener = new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (reply.getRev_user_id().equals(userId)
                                && !reply.getReply_user_id().equals(userId))
                        {
                            replyListener.onReply(leaveMsg, reply);
                        }
                        else
                        {
                            Toast.makeText(mContext,
                                    "不能回复自己哦",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                getTv().setOnClickListener(listener);
            }
        }

    }

    public interface TeacherReplyListener
    {
        void onReply(LeaveMessage msg, MessageReply reply);
    }

}
