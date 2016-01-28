package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.ClassContacts;
import com.smarthome.client2.util.ScreenUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author n003913 班级圈联系人适配器（家长、老师）
 */
public class ContactsListAdapter extends BaseAdapter
{

    private ArrayList<ClassContacts> list;

    private Context mContext;

    private LayoutInflater mInflater;

    private int size1;

    private int size2;

    private int color1;

    private int color2;

    public ContactsListAdapter(ArrayList<ClassContacts> list, Context mContext)
    {
        this.list = list;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        size1 = ScreenUtils.sp2px(mContext, 19);
        size2 = ScreenUtils.sp2px(mContext, 11);
        color1 = mContext.getResources()
                .getColor(R.color.class_circle_text_deep);
        color2 = mContext.getResources().getColor(R.color.class_circle_yellow);
    }

    public void setList(ArrayList<ClassContacts> list)
    {
        this.list = list;
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
        Holder holder = null;
        HolderInitial holderInitial = null;
        final ClassContacts model = list.get(position);
        int type = getItemViewType(position);
        if (type == 0)
        {
            if (convertView == null)
            {
                holderInitial = new HolderInitial();
                convertView = holderInitial.getView();
                convertView.setTag(holderInitial);
            }
            else
            {
                holderInitial = (HolderInitial) convertView.getTag();
            }
            holderInitial.getTvInitial().setText(model.getPhone());
        }
        else
        {
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
            holder.getTvName().setText(model.getSpannable(size1,
                    size2,
                    color1,
                    color2));
            holder.getTvPhone().setText(model.getPhone());
            holder.refreshListener(model);
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        // 首字母视图
        if (list.get(position).getName() == null)
        {
            return 0;
        }
        // 联系人视图
        return 1;
    }

    private class Holder
    {

        private View view;

        private TextView tvName;

        private TextView tvPhone;

        private ImageView ivSms;

        private ImageView ivPhone;

        private ClassContacts contacts;

        private OnClickListener listener;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_contacts, null);
            }
            return view;
        }

        public TextView getTvName()
        {
            if (tvName == null)
            {
                tvName = (TextView) view.findViewById(R.id.tv_name);
            }
            return tvName;
        }

        public TextView getTvPhone()
        {
            if (tvPhone == null)
            {
                tvPhone = (TextView) view.findViewById(R.id.tv_phone);
            }
            return tvPhone;
        }

        public ImageView getIvSms()
        {
            if (ivSms == null)
            {
                ivSms = (ImageView) view.findViewById(R.id.iv_sms);
            }
            return ivSms;
        }

        public ImageView getIvPhone()
        {
            if (ivPhone == null)
            {
                ivPhone = (ImageView) view.findViewById(R.id.iv_phone);
            }
            return ivPhone;
        }

        public void refreshListener(ClassContacts item)
        {
            contacts = item;
            if (listener == null)
            {
                listener = new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (v.getId() == R.id.iv_phone)
                        {
                            Uri uri = Uri.parse("tel:" + contacts.getPhone());
                            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                            mContext.startActivity(intent);
                        }
                        else if (v.getId() == R.id.iv_sms)
                        {
                            Uri uri = Uri.parse("smsto:" + contacts.getPhone());
                            Intent intent = new Intent(Intent.ACTION_SENDTO,
                                    uri);
                            mContext.startActivity(intent);
                        }
                    }
                };
                getIvPhone().setOnClickListener(listener);
                getIvSms().setOnClickListener(listener);
            }
        }

    }

    private class HolderInitial
    {

        private View view;

        private TextView tvInitial;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_contacts_initial,
                        null);
            }
            return view;
        }

        public TextView getTvInitial()
        {
            if (tvInitial == null)
            {
                tvInitial = (TextView) view.findViewById(R.id.tv_initial);
            }
            return tvInitial;
        }

    }

}
