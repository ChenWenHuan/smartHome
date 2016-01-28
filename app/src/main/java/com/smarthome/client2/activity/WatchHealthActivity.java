package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.util.DateUtil;
import com.smarthome.client2.util.JsonUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class WatchHealthActivity extends Activity {

	
	private FrameLayout fl_head;

    private CustomActionBar actionBar;
    private String mStrDeviceId = "";
    private String linkTopBindTelNum = "";
	private LinkTopSDKUtil linkInstance = null;
	private ArrayList<Map<String, String>> rstList = new ArrayList<Map<String, String>>();
	private ArrayList<HistoryData> sortList = new ArrayList<HistoryData>();
	private TextView tv_title, tv_num, tv_record;
	private String title;
	private ListView lv_history;
	private HealthStepsAdapter lvAdapter;
	
	private String[]  mStrArrayWeekDate = new String[7];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.watch_health_activity);
        
        mStrDeviceId = getIntent().getStringExtra("deviceId");
        title = getIntent().getStringExtra("title");
		linkTopBindTelNum = getIntent().getStringExtra("linkBindNum");
		if (!linkTopBindTelNum.equals("")) {
			initLinkSDK(linkTopBindTelNum);
		}
		initView();
		getHealthDateFromServer(mStrDeviceId);
		
		mStrArrayWeekDate = DateUtil.dateToWeek().split(",");
        
	}
	
	private void initLinkSDK(String linkaccount) {
		linkInstance = LinkTopSDKUtil.getInstance();
		linkInstance.initSDK(this, mLinkTopHandler);
		linkInstance.setupAccount(linkaccount, "888888");

	}
	
	private void getHealthDateFromServer(String deviceid){
		
		linkInstance.getHealthStep(deviceid);
	}
	
	private Handler mLinkTopHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case LinkTopSDKUtil.LINK_SDK_GET_HEALTH_STEPS:
				handleStepsData(msg.obj.toString());
				break;
				
			default:
				break;
			}
		}
	};
	
	private void initView(){
		
		tv_title = (TextView)this.findViewById(R.id.tv_title);
		tv_title.setText(title + "当前运动量(步)");
		tv_num = (TextView)this.findViewById(R.id.tv_num);
		tv_record = (TextView)this.findViewById(R.id.tv_health_record);
		tv_record.setText(title + "运动记录");
		lv_history = (ListView)this.findViewById(R.id.lv_history_data);
		lvAdapter = new HealthStepsAdapter();
		lv_history.setAdapter(lvAdapter);
		addTopBarToHead();
		
	}
	
	private void handleStepsData(String result){
		
		try {
			JSONObject resultJson = new JSONObject(result);
			JSONObject stepsJson = resultJson.getJSONObject("saved_steps");
			JsonUtil.JsonObject2HashMap(stepsJson, rstList);
			showHealthData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		lvAdapter.notifyDataSetChanged();
		
	}
	
	private void showHealthData(){
		
		for (int i=0; i<mStrArrayWeekDate.length; i++){

			HistoryData item = new HistoryData();
			item.strDate = mStrArrayWeekDate[i];
			String strSteps = getStepsByDate(mStrArrayWeekDate[i]);
			if(strSteps.equals("-1")){
				strSteps = "暂无数据";
			}else{
				
				strSteps = Integer.toString((int)Double.parseDouble(strSteps));
			}
			if (i == 0){
				tv_num.setText(strSteps);
			}
			item.strSteps = strSteps;
			Log.e("--showHealthData--", "date=" + mStrArrayWeekDate[i] + "--steps=" + getStepsByDate(mStrArrayWeekDate[i]));
			sortList.add(item);
		}
		
	}
	
	private String getStepsByDate(String date){
		
		Map<String, String> map;
		for (int i=0; i<rstList.size(); i++){
			map = rstList.get(i);
			if(map.containsKey(date)){
				return map.get(date);
			}
		}
		return "0";
		
	}
	
    //添加actionbar
    private void addTopBarToHead()
    {
    	fl_head = (FrameLayout) findViewById(R.id.fl_header_home);
        if (actionBar != null)
        {
        	fl_head.removeView(actionBar);
        }
        actionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                "运动计步",
                null,
                null);
        fl_head.addView(actionBar);
    }
    
    
    class HealthStepsAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return sortList.size();
        }

        @Override
        public HistoryData getItem(int position)
        {
            return sortList.get(position);
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
                convertView = LayoutInflater.from(WatchHealthActivity.this)
                        .inflate(R.layout.item_listview_health_record, null);
                holder = new Holder();
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tv_steps = (TextView) convertView.findViewById(R.id.tv_steps);

                convertView.setTag(holder);
            }
            else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_date.setText(sortList.get(position).strDate);
            holder.tv_steps.setText(sortList.get(position).strSteps);
            return convertView;
        }
    }
    
    class Holder
    {
        public TextView tv_date;
        public TextView tv_steps;
    }
    
    class HistoryData{
    	public String  strDate;
    	public String  strSteps;
    }
	
	
	

}
