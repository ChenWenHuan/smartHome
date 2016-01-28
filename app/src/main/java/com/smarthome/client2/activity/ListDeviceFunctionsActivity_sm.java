package com.smarthome.client2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.ToastUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListDeviceFunctionsActivity_sm extends BaseActivity {


    private GridView list_function_grid;
    private FrameLayout fl_header_device_function;
    private CustomActionBar actionBar;
    private String mUserId = "";
    private String mStrDeviceCode = "";
    private long mNDevId;
    private String mUserName;
    private String mDeviceType = "";
    private String watchDeviceAccount = "";
    private String mDeviceTelNum = "";
    private String mFamilyKeyPersonId;
    private String mFamilyId;
    private GridItemAdapter adapter;
    private ToastUtil mToastUtil;
    private ImageView imgDevice;
    private TextView tvDeviceId;
    private TextView tvDeviceTelNum;
    private TextView tvDeviceAccount;
    private Button btnDelDevice;
    private LinkTopSDKUtil instance = null;

    private NetStatusListener mNetStatusListener;

    private String[] titles = new String[]{"监听", "呼叫",
            "闹钟", "休眠",
            "亲情号码", "白名单",
            "紧急呼叫", "运动计步",
            "电量剩余", "告警设置",
            "定位设置", "同步"};

    private int[] images = {
            R.drawable.ic_monitor, R.drawable.ic_call,
            R.drawable.ic_alarm, R.drawable.ic_dormant,
            R.drawable.ic_number, R.drawable.ic_whitelist,
            R.drawable.ic_sos, R.drawable.ic_sports,
            R.drawable.power, R.drawable.dnd,
            R.drawable.gps_setting, R.drawable.safe};

    public final static int INDEX_MONITOR = 0;
    public final static int INDEX_CALL = 1;
    public final static int INDEX_ALRAMCLOCK = 2;
    public final static int INDEX_DISTURB = 3;
    public final static int INDEX_RELATIVE = 4;
    public final static int INDEX_WHITE = 5;
    public final static int INDEX_SOS = 6;
    public final static int INDEX_TARGET = 7;
    public final static int INDEX_POWER = 8;
    public final static int INDEX_WARNING = 9;  //原来是 index——alarm
    public final static int INDEX_GPS = 10;
    public final static int INDEX_SYNC = 11;
    
    private Map<Integer, Boolean> mIndexMap = new HashMap<Integer, Boolean>();
    private String removeMem[] = new String[]{"确定移除", "取消移除"};
    private boolean mOCAlarm = false;
    private boolean mElectricity = false;
    private final static int LIST_FUNCTION_SUCCESS = 701;
    private final static int LIST_FUNCTION_FAIL = 702;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.e_deviece_function);
        addTopBarToHead();

        Bundle bundle = getIntent().getExtras();

        if (bundle.containsKey("userId")) {
            mUserId = bundle.getString("userId");
        }
        if (bundle.containsKey("deviceTelNum")) {
        	mDeviceTelNum = bundle.getString("deviceTelNum");
        }
        if (bundle.containsKey("deviceCode")) {
            mStrDeviceCode = bundle.getString("deviceCode");
        }
        if (bundle.containsKey("devId")) {
            mNDevId = Long.parseLong(bundle.getString("devId"));
        }
        if (bundle.containsKey("deviceType")) {
            mDeviceType = bundle.getString("deviceType");
        }
        if (mDeviceType.equals("6") && bundle.containsKey("watchAccout")) {
            watchDeviceAccount = bundle.getString("watchAccout");
        }
        mUserName = bundle.getString("userName");
        mFamilyKeyPersonId = bundle.getString("familyKeyPersonId");
        mFamilyId = bundle.getString("familyid");
        initFunctions();
        getFunctions();
        initView();
    }
    
    private void initView(){
    	
    	imgDevice = (ImageView)this.findViewById(R.id.img_device);
    	if (mDeviceType.equals("6")){
    		imgDevice.setImageResource(R.drawable.icon_watch_sm_sc80110);
    	}else if (mDeviceType.equals("2")){
    		imgDevice.setImageResource(R.drawable.icon_older_sm_sc70110);
    	}else if (mDeviceType.equals("1")){
    		imgDevice.setImageResource(R.drawable.device_student_v2);
    	}
    	tvDeviceId = (TextView)this.findViewById(R.id.tv_device_id);
    	tvDeviceId.setText("设备编号：" + mStrDeviceCode.toUpperCase());
    	tvDeviceTelNum = (TextView)this.findViewById(R.id.tv_devide_tel_num);
    	tvDeviceTelNum.setText("设备通讯号：" + mDeviceTelNum);
    	tvDeviceAccount = (TextView)this.findViewById(R.id.tv_bind_account);
    	if (mDeviceType.equals("6")){
    		tvDeviceAccount.setVisibility(View.VISIBLE);
    		tvDeviceAccount.setText("设备绑定账号：" + watchDeviceAccount);
    	}
    	initGridLayout();
    	btnDelDevice = (Button)this.findViewById(R.id.btn_delete_device);
    	btnDelDevice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

                //获取用户信息，如有手表在删除账号之前，先将手表进行解绑操作
                 if (mDeviceType.equals("6")){
                     String userTelNum =Preferences.getInstance(ListDeviceFunctionsActivity_sm.this.getApplicationContext()).getUserTelNum();
                     if(watchDeviceAccount.equals(userTelNum)){
                            instance = LinkTopSDKUtil.getInstance();
                            instance.initSDK(ListDeviceFunctionsActivity_sm.this, handler);
                            instance.setupAccount(userTelNum, "888888");
                            instance.loginToken();
                        }
                 }

                new AlertDialog.Builder(ListDeviceFunctionsActivity_sm.this).setTitle("删除成员及设备")
                        .setItems(removeMem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (which == 0) {
                                    removeFamilyMem(mFamilyId, mUserId);
                                } else if (which == 1) {

                                }
                            }
                        }).show();

			}
		});
    }

    private void addTopBarToHead() {
        fl_header_device_function = (FrameLayout) findViewById(R.id.fl_header_device_fuction);

        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                "设备详情",
                null,
                null);

        fl_header_device_function.addView(actionBar);
    }

    private void getFunctions() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("devId", mNDevId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.GETDEVFUNCTION,
                handler,
                LIST_FUNCTION_SUCCESS,
                LIST_FUNCTION_FAIL);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {
                case LIST_FUNCTION_SUCCESS:
                    String Obj = msg.obj.toString();
                    updateUserInfo(Obj);
                    break;
                case LIST_FUNCTION_FAIL:
                    break;
                case Constants.GET_DATA_SUCCESS:
                	if(instance != null){
						instance.unBindDevice(mStrDeviceCode);
					}
                	showToast("已成功删除此成员及设备！");
                	ListDeviceFunctionsActivity_sm.this.setResult(RESULT_OK);
                	ListDeviceFunctionsActivity_sm.this.finish();
                	break;
                case Constants.GET_DATA_FAIL:
                	showToast("删除成员及设备失败，请重试！");
                	break;
                default:
                	break;
            }
            
        }
    };


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    private void initGridLayout() {
        list_function_grid = (GridView) findViewById(R.id.e_device_function_grid);

        adapter = new GridItemAdapter(titles, images, this);
        list_function_grid.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        list_function_grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent;
                switch (relationList.get(position)) {
                    case INDEX_MONITOR:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                DeviceFunctionWatchingCall_sm.class);
                        intent.putExtra("userId", mUserId);
                        intent.putExtra("deviceCode", mStrDeviceCode);
                        intent.putExtra("devId", mNDevId);
                        intent.putExtra("wachingOrCall", 1);
                        startActivity(intent);
                        break;
                    case INDEX_CALL:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                DeviceFunctionWatchingCall_sm.class);
                        intent.putExtra("userId", mUserId);
                        intent.putExtra("deviceCode", mStrDeviceCode);
                        intent.putExtra("devId", mNDevId);
                        intent.putExtra("wachingOrCall", 0);
                        startActivity(intent);
                        break;

                    case INDEX_ALRAMCLOCK:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                LocationAlarmClockActivity.class);
                        intent.putExtra("devId", mNDevId);
                        startActivity(intent);
                        break;
                    case INDEX_DISTURB:
                        String type = Preferences.getInstance(getApplicationContext())
                                .getDeviceModel();
                        if (type.equalsIgnoreCase("gk309")
                                || type.equalsIgnoreCase("gs300")) {
                            intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                    DisruptTypeOneActivity.class);
                            intent.putExtra("devId", mNDevId);
                            startActivity(intent);

                        } else {
                            intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                    DisruptTypeOneActivity.class);
                            intent.putExtra("devId", mNDevId);
                            startActivity(intent);
                        }
                        break;
                    case INDEX_RELATIVE:
                        if (mDeviceType.equals("6")) {
                            intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                    WatchWhiteNameListActivity.class);
                            intent.putExtra("deviceCode", mStrDeviceCode + ":" + watchDeviceAccount);
                        } else {
                            intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                    RelativePhoneActivity.class);
                            intent.putExtra("deviceCode", mStrDeviceCode);
                        }                        
                        intent.putExtra("devId", Long.toString(mNDevId));
                        startActivity(intent);
                        break;

                    case INDEX_WHITE:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                WhiteSpaceActivity.class);
                        startActivity(intent);
                        break;

                    case INDEX_SOS:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                SOSPhoneActivity.class);
                        startActivity(intent);
                        break;

                    case INDEX_POWER:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                LowPowerActivity.class);
                        intent.putExtra("userId", mUserId);
                        startActivity(intent);
                        break;

                    case INDEX_WARNING:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                OCAlarmActivity.class);
                        intent.putExtra("ocAlarm", mOCAlarm);
                        intent.putExtra("electricity", mElectricity);
                        startActivity(intent);
                        break;
                    case INDEX_GPS:
                        intent = new Intent(ListDeviceFunctionsActivity_sm.this,
                                GPSTypeOneActivity.class);
                        startActivity(intent);
                        break;
                    case INDEX_TARGET:
                    	intent = new Intent(ListDeviceFunctionsActivity_sm.this,
								WatchHealthActivity.class);
						
						intent.putExtra("deviceId",mStrDeviceCode);
						intent.putExtra("linkBindNum", watchDeviceAccount);
						intent.putExtra("watchTelNum", mDeviceTelNum);
						intent.putExtra("title", mUserName);
						startActivity(intent);
                    	break;
                    case INDEX_SYNC:
                        break;
                    default:
                        break;

                }
            }
        });
    }


    class GridItem {
        private String title;

        private int imageId;

        public GridItem() {
            super();
        }

        public GridItem(String title, int imageId) {
            super();
            this.title = title;
            this.imageId = imageId;
        }

        public String getTitle() {
            return title;
        }

        public int getImageId() {
            return imageId;
        }
    }


    private Map<Integer, Integer> relationList = new HashMap<Integer, Integer>();

    private class GridItemAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<GridItem> gridItemList;

        public GridItemAdapter(String[] titles, int[] images, Context context) {
            super();
            relationList.clear();
            gridItemList = new ArrayList<GridItem>();
            inflater = LayoutInflater.from(context);
            int pos = 0;
            for (int i = 0; i < images.length; i++) {
                if (mIndexMap.get(i)) {
                    GridItem picture = new GridItem(titles[i], images[i]);
                    gridItemList.add(picture);
                    relationList.put(pos, i);
                    pos++;
                }
            }
        }

        @Override
        public int getCount() {
            if (null != gridItemList) {
                return gridItemList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return gridItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.e_family_detail_item,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.family_detail_title = (TextView) convertView.findViewById(R.id.e_family_detail_title);
                viewHolder.family_detail_image = (ImageView) convertView.findViewById(R.id.e_family_detail_image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.family_detail_title.setText(gridItemList.get(position)
                    .getTitle());

            viewHolder.family_detail_image.setImageResource(gridItemList.get(position)
                    .getImageId());

            return convertView;
        }

        private class ViewHolder {
            public ImageView family_detail_image;
            public TextView family_detail_title;

        }
    }

    private void initFunctions() {
        mIndexMap.put(INDEX_MONITOR, false);
        mIndexMap.put(INDEX_CALL, false);
        mIndexMap.put(INDEX_ALRAMCLOCK, false);
        mIndexMap.put(INDEX_DISTURB, false);
        mIndexMap.put(INDEX_RELATIVE, false);
        mIndexMap.put(INDEX_WHITE, false);
        mIndexMap.put(INDEX_SOS, false);
        if(mDeviceType.equals("6")){
        	mIndexMap.put(INDEX_TARGET, true);
        }else{
        	mIndexMap.put(INDEX_TARGET, false);
        }
        
        mIndexMap.put(INDEX_POWER, false);
        mIndexMap.put(INDEX_WARNING, false);
        mIndexMap.put(INDEX_GPS, false);
        mIndexMap.put(INDEX_SYNC, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateUserInfo(String result) {
        try {
            JSONObject json = new JSONObject(result);

            JSONObject function = json.getJSONObject("data");

            mIndexMap.put(INDEX_MONITOR, function.getBoolean("listener"));
            mIndexMap.put(INDEX_CALL, function.getBoolean("call"));
            mIndexMap.put(INDEX_ALRAMCLOCK, function.getBoolean("clock"));
            mIndexMap.put(INDEX_DISTURB, function.getBoolean("notDisturb"));
            mIndexMap.put(INDEX_RELATIVE, function.getBoolean("familyNum"));
            mIndexMap.put(INDEX_WHITE, function.getBoolean("whiteNum"));
            mIndexMap.put(INDEX_SOS, function.getBoolean("sosNum"));
            initGridLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void finish() {
        if (mNetStatusListener != null) {
            mNetStatusListener.setActivityFinish();
            NetStatusListener.mClickflag = false;
            mNetStatusListener.setRunning(false);
            mNetStatusListener.cancleToast();
        }
        if (mToastUtil != null) {
            mToastUtil.cancleToast();
        }
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mNetStatusListener != null
                && mNetStatusListener.getCustomToast() != null) {
            return mNetStatusListener.cancleToast();
        } else if (mToastUtil != null && mToastUtil.getCustomToast() != null) {
            return mToastUtil.cancleToast();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
	private boolean checkIsKeyPerson(String keyPersonID) {
		
		String userID = "";
		
		Preferences preferences = Preferences.getInstance(ListDeviceFunctionsActivity_sm.this.getApplicationContext());
		userID = preferences.getUserID();

		return keyPersonID.equals(userID);
	}
	
	private void removeFamilyMem(String familyID, String memID) {
		
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("famId", familyID);
            obj.put("userId", memID);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.DEL_FAMILY_MEM_V20,
                handler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
	}

}
