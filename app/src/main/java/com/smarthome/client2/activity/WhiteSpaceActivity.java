package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.MyPhoneNumber;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.ExceptionReciver;
import com.smarthome.client2.util.HomeListener;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MyExceptionDialog;
import com.smarthome.client2.util.MySoftInputUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.HomeListener.OnHomePressedListener;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class WhiteSpaceActivity extends Activity implements OnClickListener {

    private final static int WHITE_FORBID_ALL = 0;

    private final static int WHITE_ALLOW_ALL = 1;

    private final static int WHITE_USING_LIST = 2;

    private int selectType;

    public final static String GET_CONTACT_NUMBER = "com.smarthome.client2.activity.getcontactnumber";

    private RelativeLayout forbidden_relative_phone_layout;

    private RelativeLayout white_phone_layout;

    private RelativeLayout allow_relative_phone_layout;

    private LinearLayout relative_phone_layout;

    private LinearLayout white_phone_list_layout;

    private LinearLayout relative_phone_layout_white;

    private ImageView forbidden_relative_phone_img;

    private ImageView white_phone_img;

    private ImageView allow_relative_phone_img;

    private ListView white_phone_list_listview;

    private LinearLayout white_phone_list_tv;

    //	private ScrollView relative_phone_scrollview;

    private FrameLayout fl_header_white_activity;

    private CustomActionBar actionBar;

    private List<String> whiteList = new ArrayList<String>();


    private WhiteListAdapter whiteListAdapter;

    private MyExceptionDialog myExceptionDialog;

    private HomeListener mHomeListener;

    private OnHomePressedListener mHomePressedListener = new OnHomePressedListener() {

        @Override
        public void onHomePressed() {
            if (mNetStatusListener != null) {
                mNetStatusListener.cancleToast();
            }
        }

        @Override
        public void onHomeLongPressed() {
            if (mNetStatusListener != null) {
                mNetStatusListener.cancleToast();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_white_space);
        initWidget();
        initData();
        addTopBarToHead();
        AppManager.getAppManager().addActivity(this);
    }

    private boolean mClickDelflag = false;

    private void addTopBarToHead() {
        fl_header_white_activity = (FrameLayout) findViewById(R.id.fl_header_white_activity);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                getString(R.string.title_white),
                getString(R.string.common_btn_save),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mNetStatusListener != null) {
                            mNetStatusListener.cancleToast();
                        }
                        if (white_space_edit_dialog.getVisibility() == View.VISIBLE) {
                            return;
                        }
                        if (NetStatusListener.mClickflag) {
                            //                            Toast.makeText(getApplicationContext(),
                            //                                    getString(R.string.netlistener_already_set),
                            //                                    Toast.LENGTH_SHORT)
                            return;
                        }
                        NetStatusListener.mClickflag = true;
                        if (whiteList.size() > 5) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.white_5_white),
                                    Toast.LENGTH_SHORT).show();
                            NetStatusListener.mClickflag = false;
                            return;
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                WhiteSpaceActivity.this).setOnKeyListener(new OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface arg0,
                                                 int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    NetStatusListener.mClickflag = false;
                                }
                                return false;
                            }
                        })
                                .setMessage(getString(R.string.netlistener_ask))
                                .setNegativeButton(getString(R.string.common_btn_yes),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                setWhiteSpaceFromServer(Preferences.getInstance(getApplicationContext())
                                                                .getDeviceId(),
                                                        selectType,
                                                        whiteList);
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
        fl_header_white_activity.addView(actionBar);
    }

    @Override
    public void finish() {
        if (mNetStatusListener != null) {
            mNetStatusListener.setActivityFinish();
            NetStatusListener.mClickflag = false;
            mNetStatusListener.setRunning(false);
            mNetStatusListener.cancleToast();
        }
        super.finish();
    }

    private BroadcastReceiver contactReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(GET_CONTACT_NUMBER)) {
                location_white_dialog_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        150)});
                location_white_dialog_edit.setText(Preferences.getInstance(getApplicationContext())
                        .getContact());
            }
        }
    };

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mNetStatusListener != null) {
                mNetStatusListener.cancleToast();
            }
            return false;
        }
    };

    private void initWidget() {
        mHomeListener = new HomeListener(getApplicationContext());
        mHomeListener.setOnHomePressedListener(mHomePressedListener);
        mHomeListener.startWatch();
        relative_phone_layout = (LinearLayout) findViewById(R.id.relative_phone_layout);
        relative_phone_layout.setOnTouchListener(mOnTouchListener);
        myExceptionDialog = new MyExceptionDialog(WhiteSpaceActivity.this);
        myExceptionDialog.setSubmitClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myExceptionDialog.dismissMyDialog();
            }
        });
        //		relative_phone_scrollview = (ScrollView) findViewById(R.id.relative_phone_scrollview);
        white_space_edit_dialog = (LinearLayout) findViewById(R.id.white_space_edit_dialog);
        forbidden_relative_phone_layout = (RelativeLayout) findViewById(R.id.forbidden_relative_phone_layout);
        white_phone_layout = (RelativeLayout) findViewById(R.id.white_phone_layout);
        white_phone_list_layout = (LinearLayout) findViewById(R.id.white_phone_list_layout);
        forbidden_relative_phone_img = (ImageView) findViewById(R.id.forbidden_relative_phone_img);
        white_phone_img = (ImageView) findViewById(R.id.white_phone_img);
        white_phone_list_listview = (ListView) findViewById(R.id.white_phone_list_listview);
        white_phone_list_listview.setOnTouchListener(mOnTouchListener);
        white_phone_list_tv = (LinearLayout) findViewById(R.id.white_phone_list_tv);

        allow_relative_phone_img = (ImageView) findViewById(R.id.allow_relative_phone_img);
        allow_relative_phone_layout = (RelativeLayout) findViewById(R.id.allow_relative_phone_layout);

        relative_phone_layout_white = (LinearLayout) findViewById(R.id.relative_phone_layout_white);
        location_white_dialog_edit = (EditText) findViewById(R.id.location_white_dialog_edit);
        location_white_dialog_phone_book = (ImageView) findViewById(R.id.location_white_dialog_phone_book);
        location_white_dialog_edit.setHint(getString(R.string.white_edit_hint));
        location_white_dialog_edit.setOnClickListener(editWhiteListener);
        location_white_dialog_phone_book.setOnClickListener(editWhiteListener);

        location_white_edit_layout_btn_1 = (Button) findViewById(R.id.location_white_edit_layout_btn_1);
        location_white_edit_layout_btn_2 = (Button) findViewById(R.id.location_white_edit_layout_btn_2);

        location_white_edit_layout_btn_1.setOnClickListener(editWhiteListener);
        location_white_edit_layout_btn_2.setOnClickListener(editWhiteListener);

        whiteListAdapter = new WhiteListAdapter();

        white_phone_list_listview.setAdapter(whiteListAdapter);

        allow_relative_phone_layout.setOnClickListener(this);
        forbidden_relative_phone_layout.setOnClickListener(this);
        white_phone_layout.setOnClickListener(this);
        white_phone_list_tv.setOnClickListener(this);

        registerReceiver(contactReciver, new IntentFilter(GET_CONTACT_NUMBER));

        int type = Preferences.getInstance(getApplicationContext())
                .getWhiteType();
        forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
        switch (type) {
            case WHITE_FORBID_ALL:
                forbidden_relative_phone_img.setImageResource(R.drawable.white03);
                white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_list_layout.setVisibility(View.GONE);
                break;
            case WHITE_USING_LIST:  // 使用白名单
                white_phone_img.setImageResource(R.drawable.white03);
                forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_list_layout.setVisibility(View.VISIBLE);
                break;
            case WHITE_ALLOW_ALL:  //允许所有
                allow_relative_phone_img.setImageResource(R.drawable.white03);
                forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_list_layout.setVisibility(View.GONE);
                break;
        }
//        if (Preferences.getInstance(getApplicationContext())
//                .getDeviceModel()
//                .indexOf("K210") >= 0)
//        {
//            allow_relative_phone_layout.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            allow_relative_phone_layout.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
        if (contactReciver != null) {
            unregisterReceiver(contactReciver);
        }
        if (myExceptionDialog != null) {
            myExceptionDialog.dismissMyDialog();
        }
        if (mHomeListener != null) {
            mHomeListener.stopWatch();
        }
    }

    @Override
    public void onClick(View v) {
        if (mNetStatusListener != null) {
            mNetStatusListener.cancleToast();
        }
        switch (v.getId()) {
            case R.id.forbidden_relative_phone_layout:
                forbidden_relative_phone_img.setImageResource(R.drawable.white03);
                allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_list_layout.setVisibility(View.GONE);
                selectType = 0;
                white_space_edit_dialog.setVisibility(View.GONE);
                break;

            case R.id.allow_relative_phone_layout:  // 允许所有
                allow_relative_phone_img.setImageResource(R.drawable.white03);
                forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                selectType = 1;
                white_phone_list_layout.setVisibility(View.GONE);
                white_space_edit_dialog.setVisibility(View.GONE);
                break;

            case R.id.white_phone_layout: //使用白名单
                white_phone_img.setImageResource(R.drawable.white03);
                forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                white_phone_list_layout.setVisibility(View.VISIBLE);
                selectType = 2;

                break;

            case R.id.white_phone_list_tv:
                if (whiteList.size() >= 5) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.white_5_white),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                forbidden_relative_phone_layout.setClickable(false);
                allow_relative_phone_layout.setClickable(false);
                white_phone_layout.setClickable(false);
                initListDialog();
                break;
            default:
                break;
        }
    }

    private BaseAdapter adapter = null;

    private EditText location_white_dialog_edit;

    private ImageView location_white_dialog_phone_book;

    private Button location_white_edit_layout_btn_1,
            location_white_edit_layout_btn_2;

    private LinearLayout white_space_edit_dialog;

    private OnClickListener editWhiteListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.location_white_dialog_edit:
                    //				Toast.makeText(getApplicationContext(), "不允许编辑", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.location_white_dialog_phone_book:
                    Intent intent = new Intent(WhiteSpaceActivity.this,
                            ContactActivity.class);
                    intent.putExtra("total", whiteList.size());
                    startActivity(intent);
                    break;
                case R.id.location_white_edit_layout_btn_1:
                    if (!TextUtils.isEmpty(location_white_dialog_edit.getText()
                            .toString())) {
                        String[] arr = location_white_dialog_edit.getText()
                                .toString()
                                .split(",");
                        List<String> originPhoneList = whiteList;
                        for (String s : arr) {
                            MyPhoneNumber phone = new MyPhoneNumber();
                            if (s.indexOf("-") >= 0) {
                                phone.nickName = s.split("-")[0];
                                phone.phoneNumber = s.split("-")[1];

                                if (phone.phoneNumber.length() < 3
                                        || phone.phoneNumber.length() > 11) {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.white_wrong_phone_number),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    whiteList = originPhoneList;
                                    return;
                                }

                            } else {
                                phone.phoneNumber = s;

                                if (phone.phoneNumber.length() < 3
                                        || phone.phoneNumber.length() > 11) {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.white_wrong_phone_number),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    whiteList = originPhoneList;
                                    return;
                                }
                            }
                            whiteList.add(phone.phoneNumber);
                        }
                        adapter.notifyDataSetChanged();
                        white_space_edit_dialog.setVisibility(View.GONE);
                        forbidden_relative_phone_layout.setClickable(true);
                        allow_relative_phone_layout.setClickable(true);
                        white_phone_layout.setClickable(true);
                        MySoftInputUtil.hideInputMethod(getApplicationContext(),
                                white_space_edit_dialog);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.common_phone_isnull),
                                Toast.LENGTH_SHORT).show();
                    }
                    location_white_dialog_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            12)});
                    break;
                case R.id.location_white_edit_layout_btn_2:
                    white_space_edit_dialog.setVisibility(View.GONE);
                    forbidden_relative_phone_layout.setClickable(true);
                    allow_relative_phone_layout.setClickable(true);
                    white_phone_layout.setClickable(true);
                    MySoftInputUtil.hideInputMethod(getApplicationContext(),
                            white_space_edit_dialog);
                    location_white_dialog_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            12)});
                    break;
            }
        }
    };

    private void initListDialog() {
        location_white_dialog_edit.setText("");
        adapter = whiteListAdapter;
        white_space_edit_dialog.setVisibility(View.VISIBLE);
    }

    class WhiteListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return whiteList.size();
        }

        @Override
        public Object getItem(int pos) {
            return whiteList.get(pos);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup viewgroup) {

            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.location_white_list_item, null);
            Getter getter;
            if (view != null) {
                getter = new Getter();
                getter.white_list_number = (TextView) view.findViewById(R.id.white_list_number);
                getter.white_list_delete = (ImageView) view.findViewById(R.id.white_list_delete);
                view.setTag(getter);
            } else
                getter = (Getter) view.getTag();

            getter.white_list_number.setText(whiteList.get(pos));
            getter.white_list_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNetStatusListener != null) {
                        mNetStatusListener.cancleToast();
                    }
                    if (mClickDelflag) {
                        return;
                    }
                    mClickDelflag = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            WhiteSpaceActivity.this);
                    builder.setMessage(getString(R.string.white_question_delete));
                    builder.setNegativeButton(getString(R.string.common_btn_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //                                    if (!TextUtils.isEmpty(whiteList.get(pos)
                                    //                                            .split("-")[0]))
                                    //                                    {
                                    //                                        deleteWhiteSpaceFromServer(whiteList.get(pos)
                                    //                                                .split("-")[0]);
                                    //                                        deleteId = pos;
                                    //                                    }
                                    //                                    else
                                    //                                    {
                                    whiteList.remove(pos);
                                    whiteListAdapter.notifyDataSetChanged();
                                    //                                    }
                                    dialog.cancel();
                                    mClickDelflag = false;
                                }
                            });
                    builder.setPositiveButton(getString(R.string.common_btn_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                    mClickDelflag = false;
                                }
                            });
                    builder.create().show();
                }
            });

            return view;
        }

        public class Getter {
            private TextView white_list_number;

            private ImageView white_list_delete;
        }
    }

    private final static int SET_WHITE_SPACE = 5;

    private final static int GET_WHITE_SPACE = 6;

    private final static int DEL_WHITE_SPACE_ITEM = 7;

    private List<Integer> server_type = new ArrayList<Integer>();

    private ProgressDialog dialog = null;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case Constants.GET_DATA_START:
                    dialog = new ProgressDialog(WhiteSpaceActivity.this);
                    dialog.setMessage(getString(R.string.white_ready_to_get_info));
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
                        case SET_WHITE_SPACE:
                            if (selectType == 0) {
                                Preferences.getInstance(getApplicationContext())
                                        .setWhiteType(WHITE_FORBID_ALL);
                            } else if (selectType == 1) {  //允许所有
                                Preferences.getInstance(getApplicationContext())
                                        .setWhiteType(WHITE_ALLOW_ALL);
                            } else if (selectType == 2) {  //使用白名单
                                Preferences.getInstance(getApplicationContext())
                                        .setWhiteType(WHITE_USING_LIST);
                            }

                            mNetStatusListener.parseNetStatusJson(msg.obj.toString(),
                                    WhiteSpaceActivity.this,
                                    dialog);
//                            if (dialog != null && dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
                            break;
                        case GET_WHITE_SPACE:
                            try {
                                whiteList.clear();
                                JSONObject json = new JSONObject(
                                        msg.obj.toString());
                                JSONObject data = json.getJSONObject("data");
                                JSONArray array = data.getJSONArray("list");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String phone = obj.getString("user_phone");
                                    if (!TextUtils.isEmpty(phone)) {
                                        whiteList.add(phone);
                                    } else {
                                        whiteList.clear();
                                    }
                                }
                                if(data.has("type")) {
                                    int type = data.getInt("type");
                                    Preferences.getInstance(getApplicationContext())
                                            .setWhiteType(type);
                                    switch (type) {
                                        case WHITE_FORBID_ALL:
                                            forbidden_relative_phone_img.setImageResource(R.drawable.white03);
                                            white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                                            allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                                            white_phone_list_layout.setVisibility(View.GONE);
                                            break;
                                        case WHITE_USING_LIST:  // 使用白名单
                                            white_phone_img.setImageResource(R.drawable.white03);
                                            forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                                            allow_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                                            white_phone_list_layout.setVisibility(View.VISIBLE);
                                            break;
                                        case WHITE_ALLOW_ALL:  //允许所有
                                            allow_relative_phone_img.setImageResource(R.drawable.white03);
                                            forbidden_relative_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                                            white_phone_img.setImageResource(R.drawable.ic_action_bar_right_save_no_pressed);
                                            white_phone_list_layout.setVisibility(View.GONE);
                                            break;
                                    }
                                }

                                whiteListAdapter.notifyDataSetChanged();
                                dialog.setMessage(getString(R.string.white_receive_info_success));
                                dialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DEL_WHITE_SPACE_ITEM:
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.white_delete_success),
                                    Toast.LENGTH_SHORT).show();
                            whiteList.remove(whiteList.get(deleteId));
                            whiteListAdapter.notifyDataSetChanged();
                            break;
                    }
                    server_type.remove(0);
                    break;
                case Constants.GET_DATA_FAIL:
                    dialog.setMessage(getString(R.string.white_receive_info_fail));
                    dialog.dismiss();
                    break;
                case Constants.SET_NETLISENER_DATA_START:
                    mNetStatusListener = new NetStatusListener();
                    dialog = new ProgressDialog(WhiteSpaceActivity.this);
                    dialog.setMessage(getString(R.string.netlistener_set_data));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode,
                                             KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                NetStatusListener.mClickflag = false;
                                if (mNetStatusListener != null
                                        && mNetStatusListener.isRunning()) {
                                    mNetStatusListener.setRunning(false);
                                }
                            }
                            return false;
                        }
                    });
                    dialog.show();
                    ExceptionReciver.setNetLisenerDialog(dialog);
                    break;
            }
        }

    };

    /**
     * 白名单（setWhiteSpaceFromServer） type:0:禁止非亲情号码呼入 1:自定义
     */
    private void setWhiteSpaceFromServer(int deviceId, int type,
                                         List<String> list) {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    HttpUtil.responseHandler(getApplicationContext(),
                            Constants.NO_NETWORK),
                    Toast.LENGTH_SHORT).show();
            NetStatusListener.mClickflag = false;
            return;
        }
        mHandler.sendEmptyMessage(Constants.SET_NETLISENER_DATA_START);
        server_type.add(SET_WHITE_SPACE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId", deviceId);
            obj.put("type", type);
//            if (selectType == 0) {
//                obj.put("list", "");
//            } else if (selectType == 1) {
//                obj.put("list", "*");
//            } else if (selectType == 2) {
//                JSONArray array = new JSONArray();
//                for (String whiteNumber : list) {
//                    array.put(whiteNumber);
//                }
//                obj.put("list", array);
//            }

            if (type == WHITE_USING_LIST) {
                JSONArray array = new JSONArray();
                for (String whiteNumber : list) {
                    array.put(whiteNumber);
                }
                obj.put("list", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.SET_WHITE_SPACE,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    /**
     * 白名单（getWhiteSpaceFromServer）
     */
    private void getWhiteSpaceFromServer(int deviceId) {
        server_type.add(GET_WHITE_SPACE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.GET_WHITE_SPACE,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private int deleteId = 0;

    /**
     * 删除白名单
     */
    private void deleteWhiteSpaceFromServer(String id) {
        mHandler.sendEmptyMessage(Constants.GET_DATA_START);
        server_type.add(DEL_WHITE_SPACE_ITEM);
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", Integer.parseInt(id));
            obj.put("deviceId",
                    Preferences.getInstance(getApplicationContext())
                            .getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.DEL_WHITE_SPACE_ITEM,
                mHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private void initData() {
        int deviceId = Preferences.getInstance(getApplicationContext())
                .getDeviceId();
        mHandler.sendEmptyMessage(Constants.GET_DATA_START);
        getWhiteSpaceFromServer(deviceId);
        selectType = Preferences.getInstance(getApplicationContext()).getWhiteType();
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

    private NetStatusListener mNetStatusListener;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //        if (keyCode == event.KEYCODE_BACK)
        //        {
        if (mNetStatusListener != null
                && mNetStatusListener.getCustomToast() != null) {
            return mNetStatusListener.cancleToast();
        }
        //        }
        return super.onKeyDown(keyCode, event);
    }

}
