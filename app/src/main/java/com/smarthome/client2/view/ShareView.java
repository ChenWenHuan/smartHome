package com.smarthome.client2.view;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.MyShareAppBean;
import com.smarthome.client2.bean.MyShareAppListBean;
import com.smarthome.client2.util.MyShareUtil;

public class ShareView extends LinearLayout{
	private Context ctx;
	private View share_view;
	
	private LinearLayout ll_share,ll_btn_share_cancle;
	private GridView gridView_share;
	private Button btn_share_cancle;
	
	private MyShareAppListBean myShareAppListBean = new MyShareAppListBean();
	
	private MyAdapter myAdapter;
	
	private TranslateAnimation mShowShareViewAnimation;
	private TranslateAnimation mHideShareViewAnimation;
	public boolean isHideAnimationing = false;

	public ShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ShareView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(final Context ctx){
		this.ctx = ctx;
		share_view = LayoutInflater.from(ctx).inflate(R.layout.share_third_part_view, this);
		
		ll_share = (LinearLayout) share_view.findViewById(R.id.ll_share_third_part);
		ll_btn_share_cancle = (LinearLayout) share_view.findViewById(R.id.ll_btn_share_cancle);
		gridView_share = (GridView) share_view.findViewById(R.id.gridView_third_part);
		btn_share_cancle = (Button) share_view.findViewById(R.id.btn_share_cancle_third_part);
		
		myAdapter = new MyAdapter();
		gridView_share.setAdapter(myAdapter);
		initShareViewAnimation();
		
		btn_share_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				endAnimationShare();
			}
		});
		
		gridView_share.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				MyShareAppBean shareAppBean = myShareAppListBean.list.get(position);

				if(doShareListener != null){
					doShareListener.doShare(shareAppBean);
				}
				
				endAnimationShare();
			}
		});
		
		ll_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				endAnimationShare();				
			}
		});
		
		ll_btn_share_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}
	
	public void showShareView(){
		myShareAppListBean.list.clear();
        List<ResolveInfo> resolveInfos = MyShareUtil.getShareTargets(ctx);
        for(ResolveInfo r : resolveInfos){
        	if(!r.activityInfo.name.equals("com.tencent.mobileqq.activity.JumpActivity")//QQ好友
        			&&!r.activityInfo.name.equals("com.tencent.mobileqq.activity.qfileJumpActivity")//QQ我的电脑
        			&&!r.activityInfo.name.equals("com.tencent.mm.ui.tools.AddFavoriteUI")){//微信收藏
        		PackageManager pm= ctx.getPackageManager();  
        		try {
        			MyShareAppBean bean = new MyShareAppBean();
        			ApplicationInfo applicationInfo = pm.getApplicationInfo(r.activityInfo.packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        			
        			bean.activity_package_name = r.activityInfo.packageName;
        			bean.activity_name = r.activityInfo.name;
        			
        			if(r.activityInfo.name.equals("com.tencent.mobileqq.activity.JumpActivity")){
//        			bean.app_name = "QQ好友";
        			}else if(r.activityInfo.name.equals("com.tencent.mobileqq.activity.qfileJumpActivity")){
//        			bean.app_name = "QQ我的电脑";
        			}else {
        				bean.app_name = pm.getApplicationLabel(applicationInfo).toString();
        			}
        			
        			bean.app_package_name = applicationInfo.packageName;
        			bean.app_drawable = pm.getApplicationIcon(applicationInfo);
        			myShareAppListBean.list.add(bean);
        		} catch (NameNotFoundException e) {
        			e.printStackTrace();
        		}
        	}
        }
        
        gridView_share.setAdapter(myAdapter);
        
        startAnimationShare();
	}
	
	private void initShareViewAnimation() {
		// 从自已-1倍的位置移到自己原来的位置
		mShowShareViewAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mHideShareViewAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
		mShowShareViewAnimation.setDuration(500);
		mHideShareViewAnimation.setDuration(500);

		// 关闭底部按钮的监听
		mHideShareViewAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ll_share.setVisibility(View.GONE);
				isHideAnimationing = false;
			}
		});
	}
	
	private void startAnimationShare() {
		ll_share.setVisibility(View.VISIBLE);
		ll_share.startAnimation(mShowShareViewAnimation);
	}

	public void endAnimationShare() {
		if(isHideAnimationing || ll_share.getVisibility() == View.GONE){
			return;
		}
		isHideAnimationing = true;
		
		ll_share.setVisibility(View.VISIBLE);
		ll_share.startAnimation(mHideShareViewAnimation);
	}
	
	public int getShareViewVisibility(){
		return ll_share.getVisibility();
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return myShareAppListBean.list.size();
		}

		@Override
		public MyShareAppBean getItem(int position) {
			return myShareAppListBean.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(null == convertView){
				convertView = LayoutInflater.from(ctx).inflate(R.layout.my_share_list_item, null);
				holder = new Holder();
				holder.imageView = (ImageView) convertView.findViewById(R.id.img_my_share_list_item);
				holder.tv = (TextView) convertView.findViewById(R.id.tv_my_share_list_item);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			
			MyShareAppBean bean = myShareAppListBean.list.get(position);
			holder.imageView.setImageDrawable(bean.app_drawable);
			holder.tv.setText(bean.app_name);
			
			return convertView;
		}
		
		class Holder{
			ImageView imageView;
			TextView tv;
		}
	}
	
	private DoShareListener doShareListener = null;
	public void setDoShareListener(DoShareListener doShareListener){
		this.doShareListener = doShareListener;
	}
	public interface DoShareListener{
		void doShare(MyShareAppBean shareAppBean);
	}
	
}
