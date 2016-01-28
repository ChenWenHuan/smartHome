package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.FamilyBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.widget.CircleImageView;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomeClassMemGridAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<BaseBean> mMemList;

    private ImageDownLoader mLoader;
    
    private Handler imgHandler;
    
    private class MemHolder{
    	CircleImageView   headImg;
    	TextView          tvName;
    }
    
    public HomeClassMemGridAdapter(Context context,Handler imgHandle )
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        
        mLoader = ImageDownLoader.getInstance();
        imgHandler = imgHandle;

    }
    
    public void setDataSource(ArrayList<BaseBean> list){
    	mMemList = list;
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mMemList == null)
			return 0;
		return mMemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)     {
		
		MemHolder holder;
		MemBean item = (MemBean) mMemList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_fragment_teacher_item_members, null);
            holder = new MemHolder();
            holder.headImg = (CircleImageView) convertView.findViewById(R.id.img_mem_head);
            holder.headImg.setTag(item.getMemHeadImgUrl());
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_mem_name);
            convertView.setTag(holder);
        }
        else {
            holder = (MemHolder) convertView.getTag();
        }
        if (!item.memType.equals("1")){
        	if (!item.memHeadImgUrl.equals("")){
        		mLoader.downloadImage(item.memHeadImgUrl, FsConstants.HANDLE_IMAGE, imgHandler);
        	}else{
        		holder.headImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ico_head_blue));
        	}
	        holder.tvName.setText(item.memName);
        }else{
        	holder.headImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ico_student_list));
	        holder.tvName.setText("学生列表");
        }
        return convertView;
    }

}
