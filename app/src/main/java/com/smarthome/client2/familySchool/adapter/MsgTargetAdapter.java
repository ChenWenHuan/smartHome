package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.model.MsgTarget;

/**
 * @author n003913 选择留言对象适配器
 */
public class MsgTargetAdapter extends BaseAdapter
{

    private ArrayList<MsgTarget> list;

    private LayoutInflater mInflater;

    private ArrayList<MsgTarget> selectedList;

    private Drawable img_checked;

    private Drawable img_unchecked;

    public MsgTargetAdapter(ArrayList<MsgTarget> list, Context mContext)
    {
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
        selectedList = new ArrayList<MsgTarget>();
        img_checked = mContext.getResources().getDrawable(R.drawable.tick_in);
        img_checked.setBounds(0,
                0,
                img_checked.getMinimumWidth(),
                img_checked.getMinimumHeight());
        img_unchecked = mContext.getResources().getDrawable(R.drawable.tick);
        img_unchecked.setBounds(0,
                0,
                img_unchecked.getMinimumWidth(),
                img_unchecked.getMinimumHeight());
    }

    public ArrayList<MsgTarget> getSelectedTargets()
    {
        if (selectedList == null)
        {
            selectedList = new ArrayList<MsgTarget>();
        }
        selectedList.clear();
        for (MsgTarget target : list)
        {
            if (target.isChecked() && !target.getId().equals("-1"))
            {
                selectedList.add(target);
            }
        }
        return selectedList;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return !list.get(position).getId().equals("-1");
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (list.get(position).getId().equals("-1"))
        {
            return 0;
        }
        return 1;
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
        HolderInitial hInitial;
        MsgTarget target = list.get(position);
        int type = getItemViewType(position);
        if (type == 0)
        {
            if (convertView == null)
            {
                hInitial = new HolderInitial();
                convertView = hInitial.getView();
                convertView.setTag(hInitial);
            }
            else
            {
                hInitial = (HolderInitial) convertView.getTag();
            }
            hInitial.getTv_initial().setText(target.getName());
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
            holder.getCb().setText(target.getName());
            holder.refreshListener(target);
            holder.setCheckedImage(target.isChecked());
        }
        return convertView;
    }

    private class Holder
    {
        private View view;

        private CheckBox cb;

        private MsgTarget mTarget;

        private OnCheckedChangeListener listener;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_msg_target, null);
            }
            return view;
        }

        public CheckBox getCb()
        {
            if (cb == null)
            {
                cb = (CheckBox) view.findViewById(R.id.cb);
            }
            return cb;
        }

        public void setCheckedImage(boolean checked)
        {
            cb.setChecked(checked);
            if (checked)
            {
                cb.setCompoundDrawables(img_checked, null, null, null);
            }
            else
            {
                cb.setCompoundDrawables(img_unchecked, null, null, null);
            }
        }

        public void refreshListener(MsgTarget target)
        {
            mTarget = target;
            if (listener == null)
            {
                listener = new OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked)
                    {
                        mTarget.setChecked(isChecked);
                        if (isChecked)
                        {
                            cb.setCompoundDrawables(img_checked,
                                    null,
                                    null,
                                    null);
                        }
                        else
                        {
                            cb.setCompoundDrawables(img_unchecked,
                                    null,
                                    null,
                                    null);
                        }
                    }
                };
                cb.setOnCheckedChangeListener(listener);
            }
        }

    }

    private class HolderInitial
    {

        private View view;

        private TextView tv_initial;

        public View getView()
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.fs_item_list_contacts_initial,
                        null);
            }
            return view;
        }

        public TextView getTv_initial()
        {
            if (tv_initial == null)
            {
                tv_initial = (TextView) view.findViewById(R.id.tv_initial);
            }
            return tv_initial;
        }

    }

}
