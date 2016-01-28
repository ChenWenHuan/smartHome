package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.WatchHealthActivity.HistoryData;
import com.smarthome.client2.activity.WatchHealthActivity.Holder;
import com.smarthome.client2.bean.MyPhoneNumber;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class WatchWhiteNameListActivity extends Activity {

	private FrameLayout fl_header_relative_activity;

	private CustomActionBar actionBar;

	private List<MyPhoneNumber> newRelativeList = new ArrayList<MyPhoneNumber>();
	private List<MyPhoneNumber> oldRelativeList = new ArrayList<MyPhoneNumber>();
	private ListView lv_whitname;
	private WhiteNameAdapter lv_adapter;
	private String  deviceID;
	
	private String deviceCode;
	
	private String watchDeviceID;
	private String watchAccount;	
	private String watchAlias = "";
	private boolean hasWhiteName = false;
	
	private LinkTopSDKUtil instance = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_watch_whitelist);
		initView();
	
		addTopBarToHead();
		deviceID = this.getIntent().getStringExtra("devId");
		deviceCode = this.getIntent().getStringExtra("deviceCode");
		if (deviceCode.contains(":")){
			watchDeviceID = deviceCode.split(":")[0];
			watchAccount = deviceCode.split(":")[1];
			instance = LinkTopSDKUtil.getInstance();
			instance.initSDK(WatchWhiteNameListActivity.this, handlerWatchData);
		
			instance.setupAccount(watchAccount, "888888");			
		}
		initData();
		
	}
	
	 private Handler handlerWatchData = new Handler(){
		 public void handleMessage(android.os.Message msg){
			 switch(msg.what){
			 case LinkTopSDKUtil.LINK_SDK_EDIT_WITE_LIST:
			 
				 if (msg.arg1 == 200){
					 setFamilyPhoneFromServer(deviceID,newRelativeList);
				 }else{
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					NetStatusListener.mClickflag = false;
					Toast.makeText(getApplicationContext(),"亲情号码设置失败，请再试一次!",
                             Toast.LENGTH_SHORT)
                             .show();
				 }
				 break;
			 case LinkTopSDKUtil.LINK_SDK_SEND_ALIAS:
				 if (msg.arg1 == 200){
					if(!hasWhiteName){
						setFamilyPhoneFromServer(deviceID,newRelativeList);
					}
				 }else{
					 Toast.makeText(getApplicationContext(),"主账号修改失败，请再试一次!",
                             Toast.LENGTH_SHORT)
                             .show();
				 }
				 break;
			 default:
				 break;
			 }
		 }
		 
	 };

	@Override
	public void finish() {

		super.finish();
	}

	private void addTopBarToHead() {
		fl_header_relative_activity = (FrameLayout) findViewById(R.id.fl_header_relative_activity);
		actionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				},
				getString(R.string.title_relative),
				getString(R.string.common_btn_save),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						lv_whitname.clearFocus();
						int checkResult = checkNewDataValid(newRelativeList);
						if(checkResult == 1){
							Toast.makeText(getApplicationContext(),
									"存在不合法的电话号码，请重新输入！",
									Toast.LENGTH_SHORT)
									.show();	
							return;
						}else if (checkResult == 2){
							Toast.makeText(getApplicationContext(),
									"号码合法，姓名不能为空，请重新输入！",
									Toast.LENGTH_SHORT)
									.show();
							return;
						}
						AlertDialog alertDialog = new AlertDialog.Builder(WatchWhiteNameListActivity.this)
								.setMessage(getString(R.string.netlistener_ask))
								.setNegativeButton(getString(R.string.common_btn_yes),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
//												setFamilyPhoneFromServer(deviceID,
//														newRelativeList);
												sendWhiteNameToWatch(oldRelativeList, newRelativeList);
												dialog.dismiss();
											}
										})
								.setPositiveButton(getString(R.string.common_btn_no),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												NetStatusListener.mClickflag = false;
											}
										})
								.create();
						alertDialog.setCanceledOnTouchOutside(false);
						alertDialog.show();
					}
				});
		fl_header_relative_activity.addView(actionBar);
	}

	private void initView() {
		lv_whitname = (ListView)this.findViewById(R.id.lv_white_name);
		lv_adapter = new WhiteNameAdapter();
		lv_whitname.setAdapter(lv_adapter);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private final static int SET_FAMILY_PHONE = 1;

	private final static int GET_FAMILY_PHONE = 2;

	private List<Integer> server_type = new ArrayList<Integer>();

	private ProgressDialog dialog = null;
	
	private void addBlankDataForOldData(int startIndex){
		
		Log.e("-addBlankDataForOldData-", "--startIndex=" + startIndex);
		
		for (int i=startIndex; i < 13; i++){
			MyPhoneNumber relativeNumber = new MyPhoneNumber();
			relativeNumber.nickName = "";
			relativeNumber.phoneNumber = "";
			oldRelativeList.add(relativeNumber);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
				case Constants.GET_DATA_START:
					dialog = new ProgressDialog(WatchWhiteNameListActivity.this);
					dialog.setMessage(getString(R.string.relative_ready_to_get_info));
					dialog.show();

					if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
							Toast.makeText(getApplicationContext(),
									HttpUtil.responseHandler(getApplicationContext(),
											Constants.NO_NETWORK),
									Toast.LENGTH_SHORT)
									.show();
						}
						return;
					}
					break;
				case Constants.GET_DATA_SUCCESS:
					switch (server_type.get(0)) {
						case SET_FAMILY_PHONE:

							if (dialog != null && dialog.isShowing()) {
								dialog.dismiss();
							}
							Toast.makeText(getApplicationContext(),"亲情号码设置成功!",
									                                    Toast.LENGTH_SHORT)
									                                    .show();
							WatchWhiteNameListActivity.this.finish();
							NetStatusListener.mClickflag = false;
							break;
						case GET_FAMILY_PHONE:
							try {
								oldRelativeList.clear();
								JSONObject data = new JSONObject(
										msg.obj.toString());
								JSONArray array = data.getJSONArray("data");								
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									int id = obj.getInt("id");
									String nickName = obj.getString("nickName");
									if (TextUtils.isEmpty(nickName) && i==0){
										nickName = "关注者" + Integer.toString(i+1);
									}
									String phone = obj.getString("phone");

									MyPhoneNumber relativeNumber = new MyPhoneNumber();
									relativeNumber.nickName = nickName;
									relativeNumber.phoneNumber = phone;
									relativeNumber.id = id;
									oldRelativeList.add(relativeNumber);

								}
								addBlankDataForOldData(oldRelativeList.size());
//								newRelativeList.addAll(oldRelativeList);
								copyOldDataToNew(oldRelativeList,newRelativeList);
								lv_adapter.notifyDataSetChanged();
								dialog.setMessage(getString(R.string.relative_receive_info_success));
								dialog.dismiss();
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
					}
					server_type.remove(0);
					break;
				case Constants.GET_DATA_FAIL:
					dialog.setMessage(getString(R.string.relative_receive_info_fail));
					dialog.dismiss();
					break;
				default:
					break;

			}
		}
	};
	
	private int checkNewDataValid(List<MyPhoneNumber> newList){
		
		for (int i=0; i<newList.size(); i++){
			
			String name = newList.get(i).nickName;
			String phonenum = newList.get(i).phoneNumber;
			if(phonenum.equals("") && name.equals("")){
				continue;
			}
			if(phonenum.length() <3 || phonenum.length() > 12 && phonenum.length() > 0){
				return 1;
			}
			if(phonenum.length() > 0 && name.equals("")){
				return 2;
			}
		}
		return 0;
		
	}

	/**
	 * 亲情号码（setFamilyPhone）
	 */
	private void setFamilyPhoneFromServer(String deviceId, List<MyPhoneNumber> list) {
		if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(),
					HttpUtil.responseHandler(getApplicationContext(),
							Constants.NO_NETWORK),
					Toast.LENGTH_SHORT).show();
			NetStatusListener.mClickflag = false;
			return;
		}
//		mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
		server_type.add(SET_FAMILY_PHONE);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
			JSONArray array = new JSONArray();
			for (MyPhoneNumber relativeNumber : list) {
				JSONObject phoneObj = new JSONObject();
				phoneObj.put("nickName", relativeNumber.nickName);
				phoneObj.put("phone", relativeNumber.phoneNumber);
				array.put(phoneObj);
			}
			obj.put("data", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.SET_FAMILY_PHONE,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	/**
	 * 亲情号码（getFamilyPhone）
	 */
	private void getFamilyPhoneFromServer(String deviceId) {
		server_type.add(GET_FAMILY_PHONE);
		Log.e("-getFamilyPhoneFromServer--", "--deviceId=" + deviceId);
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceId", deviceId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtil.postRequest(obj,
				Constants.GET_FAMILY_PHONE,
				mHandler,
				Constants.GET_DATA_SUCCESS,
				Constants.GET_DATA_FAIL);
	}

	private void initData() {
		mHandler.sendEmptyMessage(Constants.GET_DATA_START);
		getFamilyPhoneFromServer(deviceID);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart(getClass().getSimpleName());
		MobclickAgent.onResume(this);
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd(getClass().getSimpleName());
		MobclickAgent.onPause(this);
		super.onPause();
	}



	
	private void sendWhiteNameToWatch(List<MyPhoneNumber> oldList, List<MyPhoneNumber> newList){
		
		StringBuilder  sb = new StringBuilder("");
		for(int i=0; i<oldList.size(); i++){
			MyPhoneNumber oldItem = oldList.get(i);
			if(!TextUtils.isEmpty(oldItem.phoneNumber)){
				//只有号码不一样的情况才会有删除
				if(searchInNewList(oldItem, newList) < 1){
					if(!TextUtils.isEmpty(sb.toString())){
						sb.append("|");
						sb.append("1," + oldItem.phoneNumber + "," + oldItem.nickName);
					}else{
						sb.append("1," + oldItem.phoneNumber + "," + oldItem.nickName);
					}
				}
			}
		}
		
		if (sb.toString().contains(watchAccount)){
			Toast.makeText(getApplicationContext(),
					"主号码：" + watchAccount + "不能被修改！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		for(int i=0; i<newList.size(); i++){
			MyPhoneNumber newItem = newList.get(i);
			if(!TextUtils.isEmpty(newItem.phoneNumber)){
				//号码和别名只要有不一样的地方就需要更新
				if(searchInOldList(newItem, oldList) < 1){
					//此处如果修改主账号的名称就像手表发命令
					if(newItem.phoneNumber.equals(watchAccount)){
//						instance.sendAlias(watchDeviceID, newItem.nickName);
						watchAlias = newItem.nickName;
					}else{
						if(!TextUtils.isEmpty(sb.toString())){
							sb.append("|");
							sb.append("2," + newItem.phoneNumber + "," + newItem.nickName);
						}else{
							sb.append("2," + newItem.phoneNumber + "," + newItem.nickName);
						}
					}
				}
			}
		}
//		if (sb.toString().contains(watchAccount)){
//			Toast.makeText(getApplicationContext(),
//					"主账号：" + watchAccount + "不能被修改！",
//					Toast.LENGTH_SHORT).show();
//			return;
//		}
		
		if(!sb.toString().equals("") || !watchAlias.equals("")){
			Log.e("--sendWhiteNameToWatch-", "--command="+sb.toString());
			if(!watchAlias.equals("")){
				instance.sendAlias(watchDeviceID, watchAlias);
			}
			if(!sb.toString().equals("")){
				hasWhiteName = true;
				instance.editWiteNameList(watchDeviceID, sb.toString());
			}
		}else{
			hasWhiteName = false;
			Toast.makeText(getApplicationContext(),
					"您未对原有亲情号码做任何修改！",
					Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private int searchInNewList(MyPhoneNumber item, List<MyPhoneNumber> listData){
		
		for(int i=0; i<listData.size(); i++){
			if(item.compare(listData.get(i)) > 0){
				return 1;			
			}
		}		
		return 0;
	}
	
	private int searchInOldList(MyPhoneNumber item, List<MyPhoneNumber> listData){
		
		for(int i=0; i<listData.size(); i++){
			if(item.compare(listData.get(i)) == 2){
				return 1;			
			}
		}		
		return 0;
	}
	
	private void copyOldDataToNew(List<MyPhoneNumber> oldList, List<MyPhoneNumber> newList){
		
		for (int i=0; i<oldList.size(); i++ ){
			MyPhoneNumber relativeNumber = new MyPhoneNumber();
			relativeNumber.nickName = oldList.get(i).nickName;
			relativeNumber.phoneNumber = oldList.get(i).phoneNumber;
			newList.add(relativeNumber);
		}
		
	}
	
	class WhiteNameAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return newRelativeList.size();
        }

        @Override
        public MyPhoneNumber getItem(int position)
        {
            return newRelativeList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent)
        {

            Holder holder = null;

            if (null == convertView){
                convertView = LayoutInflater.from(WatchWhiteNameListActivity.this)
                        .inflate(R.layout.item_watch_whitelist, null);
                holder = new Holder();
                holder.et_name = (EditText) convertView.findViewById(R.id.et_family_name);
                holder.et_num = (EditText) convertView.findViewById(R.id.et_family_phone);

                convertView.setTag(holder);
            }
            else {
                holder = (Holder) convertView.getTag();
            }
            
            holder.et_name.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus){
						newRelativeList.get(position).nickName = ((EditText)v).getEditableText().toString().trim();
					}
				}
			});
            holder.et_num.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus){
						String num = ((EditText)v).getEditableText().toString().trim();
						newRelativeList.get(position).phoneNumber = num;
					}
				}
				});
         
            holder.et_name.setText(newRelativeList.get(position).nickName);
            holder.et_num.setText(newRelativeList.get(position).phoneNumber);
            return convertView;
        }
    }
    
    class Holder
    {
        public EditText et_name;
        public EditText et_num;
    }

}
