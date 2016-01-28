package com.smarthome.client2.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.AddOlderActivity;
import com.smarthome.client2.activity.FamilyListDetailActivity_sm;
import com.smarthome.client2.activity.SearchFamilyMember;

import java.util.List;
import java.util.Map;

public class FamilyPopMenuLayout_sm extends LinearLayout {

	private LinearLayout family_choose_ll_listening;
	private LinearLayout family_choose_ll_calling;
	private LinearLayout family_choose_ll_alarming;
	private LinearLayout family_choose_ll_sleeping;
	private LinearLayout family_choose_ll_relative_number;
	private LinearLayout family_choose_ll_white_list;
	private LinearLayout family_choose_ll_sos;
	private LinearLayout family_choose_ll_sport_target;
	private LinearLayout family_choose_ll_power_left;
	private LinearLayout family_choose_ll_warning;
	private LinearLayout family_choose_ll_gps_setting;
	private LinearLayout family_choose_ll_sync;

	private PopupWindow mPopupWindowDialog;

	private Context ctx;

	private View view;

	private int height = 0;

	private  Map<Integer, Boolean> mIndexMap;

	public FamilyPopMenuLayout_sm(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FamilyPopMenuLayout_sm(Context context, int height) {
		super(context);
		this.height = height;
		init(context);
	}

	public FamilyPopMenuLayout_sm(Context context, Map<Integer, Boolean> indexMap, int height) {
		super(context);
		this.height = height;
		this.mIndexMap = indexMap;
		init(context);
	}

	private void init(Context ctx) {
		this.ctx = ctx;
		initPopUpWindowView();
	}

	private void initPopUpWindowView() {
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.family_choose_dialog_sm, null);

		LinearLayout ll = (LinearLayout) view.findViewById(R.id.family_choose_ll);
		LayoutParams lp = (LayoutParams) ll.getLayoutParams();
		lp.topMargin = height;
		ll.setLayoutParams(lp);

		family_choose_ll_listening = (LinearLayout) view.findViewById(R.id.family_choose_ll_listening);
		family_choose_ll_calling = (LinearLayout) view.findViewById(R.id.family_choose_ll_calling);
		family_choose_ll_alarming = (LinearLayout) view.findViewById(R.id.family_choose_ll_alarming);
		family_choose_ll_sleeping = (LinearLayout) view.findViewById(R.id.family_choose_ll_sleeping);
		family_choose_ll_relative_number = (LinearLayout) view.findViewById(R.id.family_choose_ll_relative_number);
		family_choose_ll_white_list = (LinearLayout) view.findViewById(R.id.family_choose_ll_white_list);
		family_choose_ll_sos = (LinearLayout) view.findViewById(R.id.family_choose_ll_sos);
		family_choose_ll_sport_target = (LinearLayout) view.findViewById(R.id.family_choose_ll_sport_target);
		family_choose_ll_power_left = (LinearLayout) view.findViewById(R.id.family_choose_ll_power_left);
		family_choose_ll_warning = (LinearLayout) view.findViewById(R.id.family_choose_ll_warning);
		family_choose_ll_gps_setting = (LinearLayout) view.findViewById(R.id.family_choose_ll_gps_setting);
		family_choose_ll_sync = (LinearLayout) view.findViewById(R.id.family_choose_ll_sync);

		dynamicHideMenu();

		mPopupWindowDialog = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mPopupWindowDialog.setFocusable(true);
		mPopupWindowDialog.update();
		mPopupWindowDialog.setBackgroundDrawable(new BitmapDrawable(
				getResources(), (Bitmap) null));
		mPopupWindowDialog.setOutsideTouchable(true);

		family_choose_ll_listening.setOnClickListener(item_click);
		family_choose_ll_calling.setOnClickListener(item_click);
		family_choose_ll_alarming.setOnClickListener(item_click);
		family_choose_ll_relative_number.setOnClickListener(item_click);
		family_choose_ll_white_list.setOnClickListener(item_click);
		family_choose_ll_sos.setOnClickListener(item_click);
		family_choose_ll_sport_target.setOnClickListener(item_click);
		family_choose_ll_power_left.setOnClickListener(item_click);
		family_choose_ll_warning.setOnClickListener(item_click);
		family_choose_ll_gps_setting.setOnClickListener(item_click);
		family_choose_ll_sync.setOnClickListener(item_click);


		view.setOnClickListener(item_click);
	}

	private OnClickListener item_click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent;
			v.setBackgroundColor(getResources().getColor(R.color.submit_item_blue));
			switch (v.getId()) {
				case R.id.family_choose_ll_listening:// 监听
					intent = new Intent(ctx, SearchFamilyMember.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_calling:// 呼叫
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_alarming:// 闹钟
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_relative_number:// 亲情号码
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_white_list:// 白名单
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_sos:// sos
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_sport_target:// 运动目标
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				case R.id.family_choose_ll_power_left:// 电量剩余
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
				case R.id.family_choose_ll_warning:// 告警
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
				case R.id.family_choose_ll_gps_setting:// gps设置
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
				case R.id.family_choose_ll_sync:// 同步
					intent = new Intent(ctx, AddOlderActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
					popupWindow();
					break;
				default:
					popupWindow();
					break;
			}
		}
	};

	private  void dynamicHideMenu() {

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_MONITOR)) {
			family_choose_ll_listening.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_CALL)) {
			family_choose_ll_calling.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_ALRAMCLOCK)) {
			family_choose_ll_alarming.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_DISTURB)) {
			family_choose_ll_sleeping.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_RELATIVE)) {
			family_choose_ll_relative_number.setVisibility(View.GONE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_WHITE)) {
			family_choose_ll_white_list.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_SOS)) {
			family_choose_ll_sos.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_TARGET)) {
			family_choose_ll_sport_target.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_POWER)) {
			family_choose_ll_power_left.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_ALARM)) {
			family_choose_ll_warning.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_GPS)) {
			family_choose_ll_gps_setting.setVisibility(View.VISIBLE);
		}

		if(mIndexMap.get(FamilyListDetailActivity_sm.INDEX_SYNC)) {
			family_choose_ll_sync.setVisibility(View.VISIBLE);
		}
	}

	private void popupWindow() {
		if (mPopupWindowDialog != null && mPopupWindowDialog.isShowing()) {
			mPopupWindowDialog.dismiss();
		}
	}

	public void initFamilyFrame(int layout, int widget) {
		InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		if (mPopupWindowDialog != null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(layout, null);
			mPopupWindowDialog.showAtLocation(v.findViewById(widget),
					Gravity.TOP | Gravity.RIGHT,
					0,
					0);
		}
	}


//	String getTopActivity(Context context) {
//		ActivityManager manager = (ActivityManager) context.getSystemService(ctx.ACTIVITY_SERVICE);
//		List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
//
//		if (runningTaskInfos != null)
//			return (runningTaskInfos.get(0).topActivity).toString();
//		else
//			return null;
//	}

}
