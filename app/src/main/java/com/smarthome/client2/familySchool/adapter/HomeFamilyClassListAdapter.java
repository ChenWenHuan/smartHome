package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;

import com.smarthome.client2.activity.CameraDeviceList;
import com.smarthome.client2.activity.CameraMainActivity;
import com.smarthome.client2.activity.CreateNewMember;
import com.smarthome.client2.activity.FamilyInfoActivity;
import com.smarthome.client2.activity.LocationActivity;
import com.smarthome.client2.activity.OldPhoneAddStepOne;
import com.smarthome.client2.activity.SmartPhoneAddStepOne;
import com.smarthome.client2.activity.StudentPhoneAddStepOne;
import com.smarthome.client2.activity.UserInfoReadOrEditActivity_vii;
import com.smarthome.client2.activity.WatchAddStepOne;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.CameraInfoItem;
import com.smarthome.client2.bean.ContactBean;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.util.ScreenUtils;
import com.smarthome.client2.widget.CircleImageView;
import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class HomeFamilyClassListAdapter extends BaseAdapter {
	
    public final static float POP_ALPHA_VALUE = 0.3f;
	public final static int GET_NEW_HOME_MESSAGE_SUCCESS = 500;

    public final static int GET_NEW_HOME_MESSAGE_FAIL = 600;
	// 图片的第一行文字    
    private Context mContext;

    private LayoutInflater mInflater;
    
    private ArrayList<FamilyClassBean> familyClassData = new ArrayList<FamilyClassBean>();
    
    private Handler mHandler =null;
    
    private ImageDownLoader mLoader;
    private PopupWindow popupWindow;
	
    public HomeFamilyClassListAdapter(Context context, ArrayList<FamilyClassBean> list, Handler handle){
    	
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mHandler = handle;
        mLoader = ImageDownLoader.getInstance();
        familyClassData.clear();
        familyClassData.addAll(list);
    }
    
    public void setDataSource(ArrayList<FamilyClassBean> familyData){
    	
    	familyClassData.clear();
    	familyClassData.addAll(familyData);
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (familyClassData != null){
			
			return familyClassData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    @Override  
    public int getViewTypeCount() {  
        return 2;  
    }
    
    @Override  
    public int getItemViewType(int position) {  
    	
    	String type = familyClassData.get(position).getType();
        if (type.equals("0")){
        	return 0;
        }else if (type.equals("1")){
        	return 1;
        }
        return 2;
    	
    }  
    
    private void setDefaultFamilyBg(ImageView img, int position){
    	
    	int index = position + 1;
    	int resource_id; 
    	if (index%3 == 0){
    		resource_id = R.drawable.ico_home_page_bg3;
    		img.setImageDrawable(mContext.getResources().getDrawable(resource_id));
    		return;
    	}else if (index%2 == 0){
    		resource_id = R.drawable.ico_home_page_bg2;
    		img.setImageDrawable(mContext.getResources().getDrawable(resource_id));
    		return;
    	}else{
    		resource_id = R.drawable.ico_home_page_bg;
    		img.setImageDrawable(mContext.getResources().getDrawable(resource_id));
    		return;
    	}    	
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolderFamily holderF = null;
		ViewHolderClass  holderC = null;
		FamilyClassBean item = familyClassData.get(position);
		int type = getItemViewType(position);
		if(convertView == null){
			switch(type){
			case 0:
				//家庭视图
				holderF = new ViewHolderFamily();
				convertView = mInflater.inflate(R.layout.home_fragment_family_members, null);
				holderF.familyImg = (ImageView)convertView.findViewById(R.id.img_all_family);
				holderF.cameraImg = (ImageView)convertView.findViewById(R.id.img_camera);
				holderF.memGrid =  (GridView) convertView.findViewById(R.id.gd_family_persons);
				holderF.lyallShow = (LinearLayout)convertView.findViewById(R.id.ll_show_add_btn);
				holderF.lyaddNew = (LinearLayout)convertView.findViewById(R.id.ll_add_new);
				holderF.familyName = (TextView)convertView.findViewById(R.id.text_family_name);
				holderF.tvAddWatch = (TextView)convertView.findViewById(R.id.tv_add_watch);
				holderF.tvAddOld = (TextView)convertView.findViewById(R.id.tv_add_old);
				holderF.tvAddPhone = (TextView)convertView.findViewById(R.id.tv_add_phone);
				holderF.tvAddStu = (TextView)convertView.findViewById(R.id.tv_add_student);
				setDefaultFamilyBg(holderF.familyImg, position);
				if(item.getImgUrl().equals("")){
	        		holderF.familyImg.setTag(position);
	        	}else{
	        		holderF.familyImg.setTag(item.getImgUrl());
	        	}
				convertView.setTag(holderF);
				break;
			case 1:
				holderC = new ViewHolderClass();
				//班级视图

				break;
			default:
				break;
			}
		}else{
			switch(type){
			case 0:
				holderF = (ViewHolderFamily)convertView.getTag();
				break;
			case 1:
				holderC = (ViewHolderClass)convertView.getTag();
				break;
			default:
				break;
			}
		}
		switch(type){
		case 0:
			FamilyClick familyClick = new FamilyClick(item);
			FamilyMemItemClick familyGridClick = new FamilyMemItemClick(item);
			holderF.familyImg.setOnClickListener(familyClick);
			holderF.cameraImg.setOnClickListener(familyClick);
			holderF.familyName.setText(item.getName());
        	if (!item.getImgUrl().equals("")){
//        		mLoader.downloadImage(item.getImgUrl(), FsConstants.HOME_IMAGE, mHandler);
                Picasso.with(mContext).load(item.getImgUrl()).into(holderF.familyImg);
        	}else{
        		setDefaultFamilyBg(holderF.familyImg, position);
        	}
			holderF.lyallShow.setOnClickListener(familyClick);
			HomeFamilyMemGridAdapter familyGridAdapter = null;
			familyGridAdapter = new HomeFamilyMemGridAdapter(mContext, mHandler, item.id);
			holderF.memGrid.setAdapter(familyGridAdapter);
			holderF.memGrid.setOnItemClickListener(familyGridClick);
			familyGridAdapter.setDataSource(showAddPerson(item.getList(), item.getId()), item.getKeypersionID());
			int memSize = 0;
			memSize = item.getList().size();
			if (memSize <= 0){
				holderF.lyaddNew.setVisibility(View.VISIBLE);
				holderF.lyallShow.setVisibility(View.GONE);
			}else{
				holderF.lyaddNew.setVisibility(View.GONE);
				MemBean itemData = (MemBean) item.getList().get(memSize-1);
				if(itemData.memType.equals("5")){
					holderF.lyallShow.setVisibility(View.GONE);
				}else{
					holderF.lyallShow.setVisibility(View.VISIBLE);
				}
			}
			holderF.tvAddWatch.setOnClickListener(familyClick);
			holderF.tvAddOld.setOnClickListener(familyClick);
			holderF.tvAddPhone.setOnClickListener(familyClick);
			holderF.tvAddStu.setOnClickListener(familyClick);			
			break;
		case 1:

			break;
		default:
			break;
		}
		// TODO Auto-generated method stub
		return convertView;
	}
		
    private ArrayList<BaseBean> showAddPerson(ArrayList<BaseBean> memList, String groupid){
    	
    	int memCount;
    	MemBean item;
    	MemBean bean = new MemBean();
    	bean.memType = "5";
    	bean.memGroupID = groupid;
    	memCount = memList.size();
    	if ( memCount<= 0){
    		return memList;
    	}
    	if (memCount%2 == 0){
    		return memList;
    	}
    	
    	item = (MemBean)memList.get(memCount -1 );
    	if (item.getMemType().equals("5")){
    		return memList;
    	}
    	
    	memList.add(bean);
    	
    	return memList;
    	
    }
	
	static class ViewHolderFamily {
		ImageView familyImg = null;
		ImageView cameraImg = null;
		TextView  headTitle = null;
		GridView  memGrid = null;
		TextView  familyName = null;
        LinearLayout lyallShow = null;
        LinearLayout lyallHide = null;
        LinearLayout lyaddNew = null;
        TextView tvAddWatch = null;
        TextView tvAddOld = null;
        TextView tvAddPhone = null;
        TextView tvAddStu = null;
        
	}
	
	static class ViewHolderClass {
		CircleImageView classImg = null;
		TextView  headTitle = null;
		TextView  headMsg = null;
		GridView  memGrid = null;
		GridView  funcGrid = null;
        LinearLayout lyallShow = null;
        LinearLayout lyallHide = null;
        
	}
	
	private class FamilyClick implements OnClickListener{
		
		FamilyClassBean familyItem = null;
		
		public FamilyClick(FamilyClassBean item){
			this.familyItem = item;
		}
		@Override
		public void onClick(View v) {
			
			Intent intent = null;
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.img_all_family:
				intent = new Intent(mContext, FamilyInfoActivity.class);
				intent.putExtra("familyid", familyItem.id);
				intent.putExtra("famillyname", familyItem.name);
				intent.putExtra("familyKeyPersonID", familyItem.getKeypersionID());
				intent.putExtra("familyImagePath", familyItem.imgUrl);
				mContext.startActivity(intent);
				break;
			case R.id.img_camera:
				launchCameraActivity(familyItem.id);
				break;
			case R.id.ll_show_add_btn:
				getPopupWindow(familyItem);
				int height = ScreenUtils.getScreenHeight(mContext);
				backgroundAlpha(POP_ALPHA_VALUE);
				popupWindow.showAtLocation(v,  
										   Gravity.NO_GRAVITY, 
										   0, 
										   height- ScreenUtils.dip2px(mContext, 160));
				break;
				
			case R.id.tv_add_old:
				if (null != popupWindow) {
					   popupWindow.dismiss();
				}
//				intent = new Intent(mContext, CreateNewMember.class);
				intent = new Intent(mContext, OldPhoneAddStepOne.class);
				intent.putExtra("groupid", familyItem.id);
				intent.putExtra("memtype", "2");
				mContext.startActivity(intent);
				
				break;
			case R.id.tv_add_watch:
				if (null != popupWindow) {
					   popupWindow.dismiss();
				}
				intent = new Intent(mContext, WatchAddStepOne.class);
				intent.putExtra("groupid", familyItem.id);
				intent.putExtra("memtype", "6");
				mContext.startActivity(intent);
				
				break;
			case R.id.tv_add_student:
				if (null != popupWindow) {
					   popupWindow.dismiss();
				}
				intent = new Intent(mContext, StudentPhoneAddStepOne.class);
				intent.putExtra("groupid", familyItem.id);
				intent.putExtra("memtype", "1");
				mContext.startActivity(intent);
				
				break;
			case R.id.tv_add_phone:
				if (null != popupWindow) {
					   popupWindow.dismiss();
				}
				intent = new Intent(mContext, SmartPhoneAddStepOne.class);
				intent.putExtra("groupid", familyItem.id);
				intent.putExtra("memtype", "4");
				mContext.startActivity(intent);				
			    break;
			case R.id.tv_add_cancel:
				if (null != popupWindow) {
				   popupWindow.dismiss();
				}
				break;

			default:
				break;
			}
			
		}
		
	}	
	
	private void launchCameraActivity(String familyId){
		
		Intent intent = null;
		List<CameraInfoItem> cameraList = null;
		HashMap<String, List<CameraInfoItem>>  cameraData = null;
    	cameraData = SmartHomeApplication.getInstance().getFamilyCameraMap();
		if(cameraData.containsKey(familyId)){
			cameraList = cameraData.get(familyId);
		}
		if (cameraList.size() ==1){
			intent = new Intent(mContext, CameraMainActivity.class);
            Bundle data = new Bundle();
            data.putString(CameraDeviceList.CAMERA_SHOW_NAME_KEY, cameraList.get(0).cameraShowName);
            data.putString(CameraDeviceList.CAMERA_CID_KEY,cameraList.get(0).cameraID);
            data.putString(CameraDeviceList.CAMERA_USER_NAME_KEY,cameraList.get(0).cameraUserName);
            data.putString(CameraDeviceList.CAMERA_USER_PASS_KEY,cameraList.get(0).cameraPasswd);
            data.putString("familyId", familyId);
            intent.putExtras(data);
            mContext.startActivity(intent);
			
		}else{
			intent = new Intent(mContext, CameraDeviceList.class);
			intent.putExtra("familyid", familyId);
			mContext.startActivity(intent);
		}
		
	}
	private class FamilyMemItemClick implements OnItemClickListener{
		
		FamilyClassBean familyItem = null;
		
		public FamilyMemItemClick(FamilyClassBean item){
			this.familyItem = item;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub

			final MemBean item = (MemBean) familyItem.getList().get(position);
			if (!item.memType.equals("5")){
			// TODO Auto-generated method stub
				if(item.deviceType.equals("1") || item.deviceType.equals("2") || item.deviceType.equals("6")) {
					Intent intent = new Intent(mContext, LocationActivity.class);
					intent.putExtra("memeber_userId", item.memID);
					intent.putExtra("memeber_alias", item.memName);
					intent.putExtra("familyid", familyItem.id);
					intent.putExtra("imgurl", item.memHeadImgUrl);
					intent.putExtra("familykeypersonid", familyItem.keypersionID);
					mContext.startActivity(intent);
				}else if (item.deviceType.equals("4")){
					Intent intent = new Intent(mContext, UserInfoReadOrEditActivity_vii.class);
					intent.putExtra("userId", item.memID);
					intent.putExtra("aliasName", item.memName);
					intent.putExtra("devtype", item.deviceType);
					intent.putExtra("familyid", item.memGroupID);
					mContext.startActivity(intent);
				}
			}else{
				getPopupWindow(familyItem);
				int height = ScreenUtils.getScreenHeight(mContext);
				backgroundAlpha(0.5f);
				popupWindow.showAtLocation(arg0,  
										   Gravity.NO_GRAVITY, 
										   0, 
										   height- ScreenUtils.dip2px(mContext, 160));
			}
		}
	}	
	
    class GridItem
    {
        private String title;

        private int imageId;

        public GridItem()
        {
            super();
        }

        public GridItem(String title, int imageId)
        {
            super();
            this.title = title;
            this.imageId = imageId;
        }

        public String getTitle()
        {
            return title;
        }

        public int getImageId()
        {
            return imageId;
        }
    }
    
    protected void initPopuptWindow(FamilyClassBean itemData) {
	    // TODO Auto-generated method stub
    	TextView tvAddWatch;
    	TextView tvAddOld;
    	TextView tvAddStu;
    	TextView tvAddPhone;
    	TextView tvCancel;
    	
    	FamilyClick familyClick = new FamilyClick(itemData);
	    View popupWindow_view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.add_device_type, null,
	        false);
	    popupWindow_view.findViewById(R.id.tv_add_cancel).setVisibility(View.VISIBLE);
	    popupWindow = new PopupWindow(popupWindow_view, 
	    								ScreenUtils.getScreenWidth(mContext)
	    								, ScreenUtils.dip2px(mContext, 160) 
	    								, true);
	    tvAddWatch = (TextView)popupWindow_view.findViewById(R.id.tv_add_watch);
	    tvAddWatch.setOnClickListener(familyClick);
	    tvAddOld = (TextView)popupWindow_view.findViewById(R.id.tv_add_old);
	    tvAddOld.setOnClickListener(familyClick);
	    tvAddStu = (TextView)popupWindow_view.findViewById(R.id.tv_add_student);
	    tvAddStu.setOnClickListener(familyClick);
	    tvAddPhone = (TextView)popupWindow_view.findViewById(R.id.tv_add_phone);
	    tvAddPhone.setOnClickListener(familyClick);
	    tvCancel = (TextView)popupWindow_view.findViewById(R.id.tv_add_cancel);
	    tvCancel.setOnClickListener(familyClick);
	    popupWindow.setBackgroundDrawable(new PaintDrawable());
	    popupWindow.setFocusable(true);	    
		backgroundAlpha(POP_ALPHA_VALUE);

	       //添加pop窗口关闭事件
		popupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				backgroundAlpha(1f);
			}
		});

	    popupWindow_view.setOnTouchListener(new OnTouchListener() {
	      @Override
	      public boolean onTouch(View v, MotionEvent event) {
	        // TODO Auto-generated method stub
	        if (popupWindow != null && popupWindow.isShowing()) {
	          popupWindow.dismiss();
	          popupWindow = null;
	        }
	        return false;
	      }
	    });
	  }
    
    /***
	   * 获取PopupWindow实例
	   */
	  private void getPopupWindow(FamilyClassBean itemData) {
		  
		  
	    if (null != popupWindow) {
	      popupWindow.dismiss();
	      return;
	    } else {
	      initPopuptWindow(itemData);
	    }
	  }
	  
	  public void backgroundAlpha(float bgAlpha)  
	  {  
	        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();  
	        lp.alpha = bgAlpha; //0.0-1.0  
	        ((Activity) mContext).getWindow().setAttributes(lp);  
	  }  

}
