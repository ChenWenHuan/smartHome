package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.FamilyBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomeFamilyMemGridAdapter extends BaseAdapter {
	
    private Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<BaseBean> mMemList = null;

    private ImageDownLoader mLoader;
    
    private Handler imgHandler;
    
    private String familyID;
    
    private String keyPersonID; 
    
    private class MemHolder{
    	CircleImageView   headImg;
    	TextView          tvName;
    	TextView          tvAddress;
    }
    
    public HomeFamilyMemGridAdapter(Context context, Handler imgHandle, String groupID )
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        familyID = groupID;
        mLoader = ImageDownLoader.getInstance();
        imgHandler = imgHandle;
    }
    
    public void setDataSource(ArrayList<BaseBean> list, String keyId){
    	
    	this.keyPersonID = keyId;
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
            convertView = mInflater.inflate(R.layout.home_fragment_family_item_members, null);
            holder = new MemHolder();
            holder.headImg = (CircleImageView) convertView.findViewById(R.id.img_person_pic);
            holder.headImg.setTag(familyID + position + item.getMemHeadImgUrl());
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_mem_name);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_mem_address);
            convertView.setTag(holder);
        }
        else {
            holder = (MemHolder) convertView.getTag();
        }
        if (!item.getMemType().equals("5")){
        	if(item.getMemHeadImgUrl().equals("")){
        		holder.headImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ico_head_blue));
        	}else{
//        		mLoader.downloadImage(item.getMemHeadImgUrl(), FsConstants.HANDLE_IMAGE, imgHandler,
//        				familyID + position + item.getMemHeadImgUrl());
                Picasso.with(mContext).load(item.getMemHeadImgUrl()).into(holder.headImg);
        	}
        	
        	if (keyPersonID.equals(item.getMemID())){
        		holder.tvName.setText(item.getMemName());
        		holder.tvName.setTextColor(0xffff0000);
        	}else{
        		holder.tvName.setText(item.getMemName());
        		holder.tvName.setTextColor(0xff333333);
        	}
        	if (item.getLocation().equals("")){
        		holder.tvAddress.setText("位置信息未知");
        		holder.tvAddress.setSelected(false);
        	}else{
        		holder.tvAddress.setText(item.getLocation());
        		holder.tvAddress.setSelected(true);
        	}
	        
        }else{
        	holder.headImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.btn_add));
	        holder.tvName.setText("添加成员");
	        holder.tvAddress.setText("");
        }
        return convertView;
    }

}
