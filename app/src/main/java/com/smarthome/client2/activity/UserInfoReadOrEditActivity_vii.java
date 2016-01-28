package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.bean.UserInfo;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.RequestResult;
import com.smarthome.client2.util.ToastUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.util.UserInfoUtil;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * [用户信息查看修改界面]<BR>
 * 用于查看和修改用户信息
 *
 * @author archermind
 * @version [ODP Client R001C01LAI141, 2014年12月26日]
 */
public class UserInfoReadOrEditActivity_vii extends BaseActivity implements OnClickListener {
    public static final String OLD_FLITER = "com.smarthome.oldphone";

    @Bind(R.id.e_edit_person_setting_info_person)
    FrameLayout eEditPersonSettingInfoPerson;
    @Bind(R.id.e_edit_person_edit_pic)
    CircleImageView eEditPersonEditPic;
    @Bind(R.id.e_edit_person_pic_lay)
    RelativeLayout eEditPersonPicLay;
    @Bind(R.id.e_edit_person_ll_pic_lay)
    LinearLayout eEditPersonLlPicLay;
    @Bind(R.id.e_edit_person_edit_nickname)
    TextView eEditPersonEditNickname;
    @Bind(R.id.e_edit_person_nick_lay)
    RelativeLayout eEditPersonNickLay;
    @Bind(R.id.e_edit_person_ll_nick_lay)
    LinearLayout eEditPersonLlNickLay;
    @Bind(R.id.e_edit_person_edit_phone_number)
    TextView eEditPersonEditPhoneNumber;
    @Bind(R.id.e_edit_person_ll_alias)
    LinearLayout eEditPersonAliasLay;
    @Bind(R.id.e_edit_person_edit_alias)
    TextView eEditPersonAlias;
    @Bind(R.id.e_edit_person_phone_number_lay)
    RelativeLayout eEditPersonPhoneNumberLay;
    @Bind(R.id.e_edit_person_ll_phone_number_lay)
    LinearLayout eEditPersonLlPhoneNumberLay;
    @Bind(R.id.e_edit_person_edit_sex)
    TextView eEditPersonEditSex;
    @Bind(R.id.e_edit_person_sex_lay)
    RelativeLayout eEditPersonSexLay;
    @Bind(R.id.e_edit_person_ll_sex_lay)
    LinearLayout eEditPersonLlSexLay;
    @Bind(R.id.e_edit_person_edit_birth)
    TextView eEditPersonEditBirth;
    @Bind(R.id.e_edit_person_birth_lay)
    RelativeLayout eEditPersonBirthLay;
    @Bind(R.id.e_edit_person_ll_birth_lay)
    LinearLayout eEditPersonLlBirthLay;
    @Bind(R.id.bt_send_msg)
    Button btnSendMsg;
    @Bind(R.id.bt_remove_or_del_mem)
    Button btnRemoveMem;
    @Bind(R.id.e_edit_person_main_ui)
    ScrollView eEditPersonMainUi;
    @Bind(R.id.linelayout_edit_person_info)
    LinearLayout linelayoutEditPersonInfo;

    /**
     * 日志开关
     */
    private static final boolean DEBUG = true;

    /**
     * for load image
     */
    private static final int LOAD_IMG_START = 1;

    private static final int LOAD_IMG_SUCCESS = 2;

    private static final int LOAD_IMG_FAIL = 3;

    /**
     * for save image
     */
    private static final int UP_LOAD_IMG_START = 4;

    private static final int UP_LOAD_IMG_SUCCESS = 5;

    private static final int UP_LOAD_IMG_FAIL = 6;

    /**
     * for save user info
     */
    private static final int SAVE_DATA_FAIL = 9;

    private static final int SAVE_DATA_SUCCESS = 10;

    private static final int SAVE_DATA_START = 11;


    private static final int SAVE_NAME_FAIL = 7;

    private static final int SAVE_NAME_NULL = 888;

    private static final int SAVE_BIRTHDAY_ERROR = 999;

    private static final int SAVE_BIRTHDAY_EMPTY = 1000;
    private static final int SAVE_SEXY_ERROR = 1001;
    private static final int SAVE_SEXY_NULL = 1002;

    /**
     * for get user info
     */
    private static final int GETUSERINFO_START = 111;

    private static final int GETUSERINFO_SUCCESS = 222;

    private static final int GETUSERINFO_FAIL = 333;

    /**
     * for dialog id
     */
    private static final int DIALOG_USERINFO_PROGRESS = 0;

    private static final String DIALOG_MSG = "dialog_msg";

    private CustomActionBar mActionBar;

    private PopupWindow mPopupWindowDialog;

    private String takePicturePath;// 调用相机拍摄照片的名字

    private String filePath = "";// 裁剪后图片的路径

    private Bitmap bitmap;

    private String head_path = Environment.getExternalStorageDirectory()
            .getPath() + File.separator + "default_pictures.png";

    private UserInfo mUserInfo = new UserInfo();

    private String mStrUserId;   //userId
    private String mStrLoginUserID;

    private String mUserName = "";
    private String familyId = "";

    private int mStatus;
    private final String sex[] = new String[]{"男", "女"};
    private int done;
    private final String newMemPic[] = new String[]{"拍照", "从相册选择"};

    // 下面字段供 intent 回传值用
    private final int GETFROMPIC = 1;      // 从相册获取
    private final int CALLCAMERA = 2;      // 从相机获取
    private final int CRAPPIC = 3;         //裁减相片
    private final int MODIFY_NICKNAME = 4; //修改昵称
    private final int MODIFY_ALIAS = 5; //
    private int mIsModified = 0;
    private String devType = "";
    private LinkTopSDKUtil instance = null;
    private boolean isUpdateName = false;
    private boolean isUpdateSex = false;
    private boolean isUpdateBirth = false;
    private String removeMem[] = new String[]{"确定移除", "取消移除"};
    private String mWatchLinkId;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent data) {
            if (data.getAction().equals(OLD_FLITER)) {
                mUserInfo.setDeviceCode(data.getExtras()
                        .getString(UserInfoUtil.KEY_USER_DEVICECODE));
                mUserInfo.setDeviceId(data.getExtras()
                        .getString(UserInfoUtil.KEY_USER_DEVICEID));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.e_edit_person_info);
        ButterKnife.bind(this);
        initDataFromIntent();
        initView();
        getUserInfo();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_USERINFO_PROGRESS:
                dialog = new ProgressDialog(this);
                dialog.setCanceledOnTouchOutside(false);
                break;
            default:
                dialog = null;
                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        switch (id) {
            case DIALOG_USERINFO_PROGRESS:
                String msg = bundle.getString(DIALOG_MSG);
                ((ProgressDialog) dialog).setMessage(msg);
                break;
        }
        super.onPrepareDialog(id, dialog, bundle);
    }

    private void initUserInfo() {
        MemBean loginUserInfo = SmartHomeApplication.getInstance().getLoginMemberInfo();
        // 本页面暂时只服务于登陆用户
        mStrUserId = loginUserInfo.getMemID();

    }

    private void initView() {
        addTopBarToHead();
    }


    private void addTopBarToHead() {
        if (mActionBar != null) {
            eEditPersonSettingInfoPerson.removeView(mActionBar);
        }
        if (mUserName.isEmpty()) {
            mUserName = "我的信息";
        }
        mActionBar = TopBarUtils.createCustomActionBarInvisiableRightImage(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                mUserName,
                R.drawable.default_pictures,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        saveUserInfo();
                    }
                });
        eEditPersonSettingInfoPerson.addView(mActionBar);
    }

    private void initDataFromIntent() {
        MemBean loginUserInfo = SmartHomeApplication.getInstance().getLoginMemberInfo();
        Intent intent = getIntent();
        if (intent.hasExtra("userId")) {
            mStrUserId = intent.getStringExtra("userId");
            mStrLoginUserID = loginUserInfo.getMemID();
            if (mStrUserId.equalsIgnoreCase(mStrLoginUserID)) {
                mUserName = "我的信息";
            }
            else {
                mUserName = intent.getStringExtra("aliasName");
                eEditPersonAlias.setText(mUserName);
            }
            if(intent.hasExtra("devtype")){
                devType = intent.getStringExtra("devtype");
                if(devType.equals("1") || devType.equals("2") || devType.equals("6")){
                    btnSendMsg.setVisibility(View.GONE);
                    btnRemoveMem.setText("删除此成员及设备");
                }else if (devType.equals("4")){
                    btnSendMsg.setVisibility(View.VISIBLE);
                    btnRemoveMem.setText("移出家庭");
                }
            }
            if(intent.hasExtra("familyid")){
                familyId = intent.getStringExtra("familyid");
            }
        } else {
            // 本页面暂时只服务于登陆用户
            mStrUserId = loginUserInfo.getMemID();
            eEditPersonAliasLay.setVisibility(View.GONE);
            mUserName = "我的信息";
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case GETFROMPIC:// 如果是直接从相册获取
                        BitmapUtil.startPhotoZoom(UserInfoReadOrEditActivity_vii.this,
                                data.getData(),
                                3);
                        break;
                    case CALLCAMERA:// 如果是调用相机拍照时
                        File temp = new File(takePicturePath);
                        BitmapUtil.startPhotoZoom(UserInfoReadOrEditActivity_vii.this,
                                Uri.fromFile(temp),
                                3);
                        break;
                    case CRAPPIC:// 取得裁剪后的图片
                        if (data != null && data.getExtras() != null) {
                            Bundle extras = data.getExtras();
                            Bitmap image = extras.getParcelable(UserInfoUtil.KEY_JASON_DATA);
                            filePath = Constants.IMAGE_FILE_PATH
                                    + UserInfoUtil.USER_PICNAME_DEFAULT;
                            BitmapUtil.setPicToView(image,
                                    Constants.IMAGE_FILE_PATH,
                                    UserInfoUtil.USER_PICNAME_DEFAULT);
                        }
                        break;
                    case MODIFY_NICKNAME: // user nick name 用户名称修改
                        if (data != null && data.getExtras() != null) {
                            Bundle bundle = data.getExtras();
                            String name = bundle.getString("nickname");
                            if (!name.equalsIgnoreCase(eEditPersonEditNickname.getText().toString())) {
                                if (TextUtils.isEmpty(name)) {
                                    eEditPersonEditNickname.setText("");
                                } else {
                                    eEditPersonEditNickname.setText(name);
                                }
                            }
                            isUpdateName = true;
                            mUserInfo.setName(name);
                            new SaveInfoTask().execute();
                        }
                        break;
                    case MODIFY_ALIAS:
                        String aliasName = data.getStringExtra("familyname");
                        eEditPersonAlias.setText(aliasName);
                        break;
                    default:
                        break;
                }
                if (!TextUtils.isEmpty(filePath)) {
                    Bitmap bm = BitmapFactory.decodeFile(filePath);
                    // bm = toRoundBitmap(bm);
                    if (bm != null) {
                        eEditPersonEditPic.setImageBitmap(bm);
                    }
                    new SaveIconTask().execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        if (HttpUtil.isNetworkAvailable(this)) {
            updateState(GETUSERINFO_START, Constants.SC_OK);
            new GetUserInfoTask().execute();
        } else {
            updateState(GETUSERINFO_FAIL, Constants.NO_NETWORK);
        }
    }

    /**
     * [update state]<BR>
     * 更新界面状态
     *
     * @param state 状态
     * @param “code”  状态码
     */
    private void updateState(int state, String errorMsg) {
        if (isFinishing()) {
            return;
        }
        mStatus = state;
        removeDialog(DIALOG_USERINFO_PROGRESS);
        showToast(errorMsg);
    }

    private void updateState(int state, int code) {
        if (isFinishing()) {
            return;
        }
        mStatus = state;
        Bundle bundle = new Bundle();
        String errormsg = HttpUtil.responseHandler(this, code);
        switch (state) {
            case GETUSERINFO_START:
                bundle.putString(DIALOG_MSG, getString(R.string.info_loading));
                showDialog(DIALOG_USERINFO_PROGRESS, bundle);
                break;
            case GETUSERINFO_SUCCESS:
                removeDialog(DIALOG_USERINFO_PROGRESS);
                //showToast(R.string.info_getuserinfo_success);
                break;
            case GETUSERINFO_FAIL:
                removeDialog(DIALOG_USERINFO_PROGRESS);
                switch (code) {
                    case Constants.NO_NETWORK:
                        showToast(R.string.no_network);
                        break;
                    case Constants.SERVER_OFFLINE:
                        showToast(errormsg);
                        break;
                    case Constants.UNKNOW_RESULT:
                        showToast(R.string.info_getuserinfo_fail);
                        break;
                    default:
                        showToast(errormsg);
                        break;
                }
                break;
            case LOAD_IMG_START:
                break;
            case LOAD_IMG_SUCCESS:
                try {
                    bitmap = BitmapUtil.getBitmap(mUserInfo.getHeadPicPath()
                            + mUserInfo.getHeadPicName());
                } catch (Exception e) {
                    log(e.toString());
                }
                if (bitmap != null) {
                    eEditPersonEditPic.setImageBitmap(bitmap);
                }
                break;
            case LOAD_IMG_FAIL:
                showToast(errormsg);
                break;
            case UP_LOAD_IMG_SUCCESS:
                break;
            case UP_LOAD_IMG_FAIL:
                switch (code) {
                    case Constants.NO_NETWORK:
                        showToast(R.string.no_network);
                        break;
                    case Constants.SERVER_OFFLINE:
                        showToast(R.string.info_server_offline);
                        break;
                    case Constants.UNKNOW_RESULT:
                        showToast(R.string.info_upload_icon_fail);
                        break;
                    default:
                        showToast(errormsg);
                        break;
                }
                break;
            case UP_LOAD_IMG_START:
            case SAVE_DATA_START:
                bundle.putString(DIALOG_MSG, getString(R.string.info_saving));
                showDialog(DIALOG_USERINFO_PROGRESS, bundle);
                break;
            case SAVE_DATA_FAIL:
                removeDialog(DIALOG_USERINFO_PROGRESS);
                switch (code) {
                    case SAVE_NAME_FAIL:
                        if (mUserInfo.isCurrentUser()) {
                            showToast(R.string.info_realname_overlength);
                        } else {
                            showToast(R.string.info_nickname_overlength);
                        }
                        break;
                    case SAVE_NAME_NULL:
                        if (mUserInfo.isCurrentUser()) {
                            showToast(R.string.info_realname_null);
                        } else {
                            showToast(R.string.info_nickname_null);
                        }
                        break;
                    case SAVE_BIRTHDAY_EMPTY:
                        showToast(R.string.info_birth_empty);
                        break;
                    case SAVE_BIRTHDAY_ERROR://用户输入的生日不是正确的年份
                        showToast(R.string.info_birth_error);
                        break;
                    case Constants.NO_NETWORK:
                        showToast(R.string.no_network);
                        break;
                    case Constants.SERVER_OFFLINE:
                        showToast(errormsg);
                        break;
                    case SAVE_SEXY_ERROR:
                        showToast("性别保存错误!");
                        break;
                    case SAVE_SEXY_NULL:
                        showToast("性别不能为空!");
                        break;
                    default:
                        showToast(errormsg);
                        break;
                }
                break;
            case SAVE_DATA_SUCCESS:
                showToast(R.string.info_save_ok);

                break;
            default:
                showToast(R.string.unknown_error);
                break;

        }
    }

    @Override
    protected void onDestroy() {
       // unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    @OnClick({R.id.e_edit_person_edit_pic,
            R.id.e_edit_person_ll_nick_lay,
            R.id.e_edit_person_ll_phone_number_lay,
            R.id.e_edit_person_ll_sex_lay,
            R.id.e_edit_person_ll_birth_lay,
            R.id.e_edit_person_ll_alias,
            R.id.bt_send_msg,
            R.id.bt_remove_or_del_mem})
    public void onClick(View v) {
        int year = 1987, month = 10, day = 01;
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.e_edit_person_edit_pic:
                closeInputMethod();
                new AlertDialog.Builder(UserInfoReadOrEditActivity_vii.this).setTitle("个人头像")
                        .setItems(newMemPic, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent();
                                if (which == 0) {
                                    String sdStatus = Environment.getExternalStorageState();
                                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                                        showToast(R.string.info_alert_hasnosdcard);
                                        return;
                                    }
                                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                    // 下面这句指定调用相机拍照后的照片存储的路径

                                    takePicturePath = Constants.IMAGE_FILE_PATH
                                            + UserInfoUtil.USER_PICNAME_DEFAULT;
                                    showToast(takePicturePath);
                                    File image = new File(takePicturePath);
                                    if (!image.exists())
                                        try {
                                            log("" + image.createNewFile());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                                    startActivityForResult(intent, CALLCAMERA);
                                } else if (which == 1) {
                                    //相册选择
                                    intent.setAction(Intent.ACTION_PICK);
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            "image/*");
                                    takePicturePath = getIntent().getStringExtra("data");
                                    startActivityForResult(intent, GETFROMPIC);
                                }

                            }
                        }).show();
                break;

            case R.id.e_edit_person_ll_nick_lay:
                intent = new Intent();
                intent.setClass(UserInfoReadOrEditActivity_vii.this, UiEditUserInfoNickname_sm.class);
                startActivityForResult(intent, MODIFY_NICKNAME);

                break;
            case R.id.e_edit_person_ll_phone_number_lay:
                showToast("暂不支持修改手机号码！");
                break;
            case R.id.e_edit_person_ll_sex_lay:
                new AlertDialog.Builder(UserInfoReadOrEditActivity_vii.this).setTitle("性别设置")
                        .setItems(sex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String strSex = sex[which];
                                if (!strSex.equalsIgnoreCase(eEditPersonEditSex.getText().toString())) {
                                    eEditPersonEditSex.setText(sex[which]);
                                    isUpdateSex = true;
                                    setUpdateSex();
                                    new SaveInfoTask().execute();
                                }

                            }
                        }).show();
                break;
            case R.id.e_edit_person_ll_birth_lay:
                String birthday = eEditPersonEditBirth.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format.parse(birthday);
                    year = date.getYear();
                    if (year < 130) {
                        year = 1900 + year;
                    }
                    month = date.getMonth();
                    day = date.getDate();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                MyDatePickerDialog dialog = new MyDatePickerDialog(UserInfoReadOrEditActivity_vii.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String strBirth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                if (!strBirth.equalsIgnoreCase(eEditPersonEditBirth.getText().toString())) {
                                    eEditPersonEditBirth.setText(strBirth);
                                    isUpdateBirth = true;
                                    setUpdateBirthInfo();
                                    mUserInfo.setBirthday(mUserInfo.getBirthday());
                                    new SaveInfoTask().execute();
                                }
                            }
                        }, year, month, day);
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        done = 1;
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        done = 0;
                    }
                });
                dialog.show();
                break;

            case R.id.e_edit_person_ll_alias:
                modifyFamilyMemTitle();
                break;
            case R.id.bt_send_msg:
                break;
            case R.id.bt_remove_or_del_mem:
                removeFamilyMemClick();
                break;
            default:
                break;
        }
    }

    private void removeFamilyMemClick(){

        //获取用户信息，如有手表在删除账号之前，先将手表进行解绑操作
        if (devType.equals("6")){
            getUserInfoByID(mStrUserId);
        }

        new AlertDialog.Builder(UserInfoReadOrEditActivity_vii.this).setTitle("删除成员及设备")
                .setItems(removeMem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        if (which == 0) {
                            removeFamilyMem(familyId, mStrUserId);
                        } else if (which == 1) {

                        }
                    }
                }).show();
    }

    private void modifyFamilyMemTitle() {
        Intent intent = new Intent(UserInfoReadOrEditActivity_vii.this,
                UiEditFamilyName.class);
        intent.setAction(UiEditFamilyName.EDIT_FAMILY_MEM_MARK);
        intent.putExtra("userid", mStrUserId);
        intent.putExtra("familyid", mUserInfo.getGroupId());
        intent.putExtra("name", mUserName);
        startActivityForResult(intent, MODIFY_ALIAS);
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            if (imm != null) {
                if (this.getCurrentFocus() == null)
                    return;
                if (this.getCurrentFocus().getWindowToken() == null)
                    return;
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void setUpdateSex(){
        UserInfo info = mUserInfo;
        String sexy = eEditPersonEditSex.getText().toString().trim();
        if (TextUtils.isEmpty(sexy)) {
            updateState(SAVE_DATA_FAIL, SAVE_SEXY_NULL);
            return;
        } else {
            if (sexy.compareToIgnoreCase("男") == 0) {
                info.setGender("1");
            } else {
                info.setGender("0");
            }
        }
    }

    private void setUpdateBirthInfo(){
        UserInfo info = mUserInfo;

        String birth = eEditPersonEditBirth.getText().toString().trim();
        if (TextUtils.isEmpty(birth)) {
            updateState(SAVE_DATA_FAIL, SAVE_BIRTHDAY_EMPTY);
            return;
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        String strBirth = "1987";
        try {
            Date date = myFmt.parse(birth);
            strBirth = new SimpleDateFormat("yyyyMMdd").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (birth.compareTo("0") != 0) {
            info.setBirthday(strBirth);
        }
        return;
    }

    private boolean doSaveInfo() {
        UserInfo info = mUserInfo;

        String birth = eEditPersonEditBirth.getText().toString().trim();
        if (TextUtils.isEmpty(birth)) {
            updateState(SAVE_DATA_FAIL, SAVE_BIRTHDAY_EMPTY);
            return false;
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        String strBirth = "1987";
        try {
            Date date = myFmt.parse(birth);
            strBirth = new SimpleDateFormat("yyyyMMdd").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (birth.compareTo("0") != 0) {
            info.setBirthday(strBirth);
        }


        String name = eEditPersonEditNickname.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            updateState(SAVE_DATA_FAIL, SAVE_NAME_NULL);
            return false;
        } else if (name.length() > 10) {
            updateState(SAVE_DATA_FAIL, SAVE_NAME_FAIL);
            return false;
        } else {
            info.setName(name);
        }

        String sexy = eEditPersonEditSex.getText().toString().trim();
        if (TextUtils.isEmpty(sexy)) {
            updateState(SAVE_DATA_FAIL, SAVE_SEXY_NULL);
            return false;
        } else {
            if (sexy.compareToIgnoreCase("男") == 0) {
                info.setGender("1");
            } else {
                info.setGender("0");
            }

        }

        String sNum = eEditPersonEditPhoneNumber.getText().toString().trim();
        info.setTelnum(sNum);

        mUserInfo = info;
        return true;
    }

    private void updateView() {
        if (TextUtils.isEmpty(mUserInfo.getName())) {
            eEditPersonEditNickname.setText("");
        } else {
            eEditPersonEditNickname.setText(mUserInfo.getName());
        }

        if (TextUtils.isEmpty(mUserInfo.getTelnum())) {
            eEditPersonEditPhoneNumber.setText("");
        } else {
            eEditPersonEditPhoneNumber.setText(mUserInfo.getTelnum());
        }

        if (TextUtils.isEmpty(mUserInfo.getGender())) {
            eEditPersonEditSex.setText("");
        } else {
            if (mUserInfo.getGender().compareToIgnoreCase("1") == 0) {
                eEditPersonEditSex.setText("男");
            } else {
                eEditPersonEditSex.setText("女");
            }
        }

        if (TextUtils.isEmpty(mUserInfo.getBirthday())) {
            eEditPersonEditBirth.setText("");
        } else {
            String bir = mUserInfo.getBirthday();
            String year = "1987";
            if (bir.length() > 4) {
                year = bir.substring(0, 4);
            }

           String month = "01" ;
           if (bir.length() > 6) {
               month = bir.substring(4, 6);
           }
            String dat = "01";
            if (bir.length() > 7) {
                dat = bir.substring(6, 8);
            }
            if (bir.length() > 7) {
                eEditPersonEditBirth.setText(year + "-" + month + "-" + dat);
            }
            else {
                eEditPersonEditBirth.setText(" ");
            }
        }
    }
    /**
     * save User Info<BR>
     * 保存用户信息
     */
    private void saveUserInfo() {
        if (HttpUtil.isNetworkAvailable(getApplicationContext())) {
            if (hasTaskAlready()) {
                updateState(SAVE_DATA_START, Constants.SC_OK);
                return;
            }

            if (mIsModified > 0) {
                if (doSaveInfo()) {
                    updateState(SAVE_DATA_START, Constants.SC_OK);
                    new SaveInfoTask().execute();
                }
            }
        } else {
            updateState(SAVE_DATA_FAIL, Constants.NO_NETWORK);
        }
    }

    /**
     * [Get User Info Task]
     * 查询用户信息
     *
     * @author
     * @version
     */
    class GetUserInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return UserInfoUtil.getUserInfoByUserId(UserInfoReadOrEditActivity_vii.this,
                    mStrUserId);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || TextUtils.isEmpty(result)) {
                log("GetUserInfoTask----error----else");
                updateState(GETUSERINFO_FAIL, Constants.JSON_ERROR);
                return;
            }
            boolean isCode = false;

            int resultcode = Constants.UNKNOW_RESULT;
            try {
                JSONObject jobj = new JSONObject(result);
                resultcode = jobj.getInt("retcode");
                isCode = (resultcode == 200);
            } catch (Exception e) {
                isCode = false;
            }
            if (!isCode) {
                log("GetUserInfoTask----error----" + result);
                updateState(GETUSERINFO_FAIL, resultcode);
            } else {
                boolean bResult = UserInfoUtil.parseToUserInfo(mUserInfo,result);
                if (!bResult) {
                    log("GetUserInfoTask----fail----" + result);
                    updateState(GETUSERINFO_FAIL, Constants.JSON_ERROR);
                } else {
                   // mUserInfo = userInfo;
                    log("GetUserInfoTask----success----" + result);
                    updateView();
                    updateState(GETUSERINFO_SUCCESS, Constants.SC_OK);
                    loadUserImage();
                }
            }
        }
    }

    /**
     * [load User Image]<BR>
     * 加载用户头像
     */
    private void loadUserImage() {
       String strHeadPicFullPath = mUserInfo.getHeadPicPath()
                + mUserInfo.getHeadPicName();
        if (strHeadPicFullPath.length() > 5) {
            Picasso.with(UserInfoReadOrEditActivity_vii.this).load(strHeadPicFullPath).into(eEditPersonEditPic);
        }
    }

    /**
     * [保存用户信息任务类]<BR>
     * 用于保存用户信息
     *
     * @author archermind
     * @version [ODP Client R001C01LAI141, 2014年12月26日]
     */
    class SaveInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = String.valueOf(Constants.UNKNOW_RESULT);
            JSONObject obj = new JSONObject();
            try {
                obj.put(UserInfoUtil.KEY_ID, mStrUserId);
                if(isUpdateSex) {
                    obj.put(UserInfoUtil.KEY_USER_GENDER, mUserInfo.getGender());
                }
                if(isUpdateBirth) {
                    obj.put(UserInfoUtil.KEY_USER_BIRTHDAY, mUserInfo.getBirthday().replaceAll("-", ""));
                }
                if(isUpdateName) {
                    obj.put(UserInfoUtil.KEY_USER_REALNAME, mUserInfo.getName());
                }
            } catch (JSONException e) {
                log(e.toString());
            }
            final RequestResult requestResult = new RequestResult();
            HttpUtil.postRequest(obj,
                    Constants.SET_USERINOF,
                    requestResult,
                    UserInfoReadOrEditActivity_vii.this);

            result = requestResult.getResult();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int resultcode = Constants.UNKNOW_RESULT;
            String resultMsg = "";
            if (!TextUtils.isEmpty(result)) {
                boolean isCode = false;
                try {
                    JSONObject jobj = new JSONObject(result);
                    if (jobj.has("retmsg")) {
                        resultMsg = jobj.getString("retmsg");
                    }

                    resultcode = jobj.getInt("retcode");
                    isCode = resultcode == Constants.SC_OK;
                    if(resultcode == 200){
                        isUpdateName = false;
                        isUpdateBirth = false;
                        isUpdateSex = false;
                    }

                } catch (Exception e) {
                    isCode = false;
                }
                if (!isCode) {
                    if (resultMsg.isEmpty()) {
                        updateState(SAVE_DATA_FAIL, resultcode);
                    } else {
                        updateState(SAVE_DATA_FAIL, resultMsg);
                    }

                } else {
                    if (resultMsg.isEmpty()) {
                        updateState(SAVE_DATA_SUCCESS, resultcode);
                    } else {
                        updateState(SAVE_DATA_SUCCESS, resultMsg);
                    }
                }
            } else {
                updateState(SAVE_DATA_FAIL, resultcode);
            }
        }
    }

    /**
     * [Save user Icon Task]<BR>
     * 保存头像
     *
     * @author archermind
     * @version [ODP Client R001C01LAI141, 2014年12月26日]
     */
    class SaveIconTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = String.valueOf(Constants.UNKNOW_RESULT);
            try {
                result = HttpUtil.upLoadFile(mStrUserId, new File(
                        filePath), HttpUtil.BASE_URL
                        + "/account/setPhoto.action", "userId");
            } catch (ClientProtocolException e) {
                log(e.toString());
            } catch (IOException e) {
                log(e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int resultcode = Constants.UNKNOW_RESULT;
            if (!TextUtils.isEmpty(result)) {
                boolean isCode = false;
                try {
                    JSONObject jobj = new JSONObject(result);
                    resultcode = jobj.getInt("retcode");
                    isCode = true;
                } catch (Exception e) {
                    isCode = false;
                }
                if (!isCode) {
                    updateState(UP_LOAD_IMG_FAIL, resultcode);
                } else {
                    // go on save other info
                    UserInfo info = UserInfoUtil.refreshWidget(UserInfoReadOrEditActivity_vii.this,
                            result,
                            mStrUserId);
                    if (info != null) {
                        mUserInfo.setHeadPicName(info.getHeadPicName());
                        mUserInfo.setHeadPicPath(info.getHeadPicPath());
                    }
                    updateState(UP_LOAD_IMG_FAIL, "更新头像成功");
                }
            }
        }
    }


    private boolean hasTaskAlready() {
        log("hasTask" + mStatus);
        switch (mStatus) {
            case UP_LOAD_IMG_START:
            case SAVE_DATA_START:
                return true;
            default:
                return false;
        }
    }

    class MyDatePickerDialog extends DatePickerDialog {
        OnDateSetListener mCallback;

        public MyDatePickerDialog(Context context, OnDateSetListener callBack,
                                  int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            mCallback = callBack;
        }

        @Override
        protected void onStop() {
            if (done == 1) {
                if (mCallback != null) {
                    DatePicker dataPick = this.getDatePicker();
                    dataPick.clearFocus();
                    mCallback.onDateSet(dataPick, dataPick.getYear(),
                            dataPick.getMonth(), dataPick.getDayOfMonth());
                }
            }
        }
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {

                case Constants.GET_DATA_SUCCESS:
                    if(instance != null){
                        instance.unBindDevice(mWatchLinkId);
                    }
                    showToast("已成功移除！");
                    UserInfoReadOrEditActivity_vii.this.finish();
                    break;
                case Constants.GET_DATA_FAIL:
                    showToast("移除成员失败，请重试！");
                    break;
                default:
                    break;
            }

        }
    };


    private void getUserInfoByID(String memID) {

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("acctid", memID);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpUtil.postRequest(obj,
                Constants.GET_USER_INFO,
                gerUserInfoHandler,
                Constants.GET_DATA_SUCCESS,
                Constants.GET_DATA_FAIL);
    }

    private Handler gerUserInfoHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FsConstants.HTTP_START:
                    break;
                case Constants.GET_DATA_SUCCESS:
                    String[] linkDeviceInfo = null;
                    linkDeviceInfo = UserInfoUtil.parserUserLinkTopDeviceInfo(msg.obj.toString());
                    if (linkDeviceInfo != null){
                        mWatchLinkId = linkDeviceInfo[0];
                        String linkDeviceAccout = linkDeviceInfo[1];
                        instance = LinkTopSDKUtil.getInstance();
                        instance.initSDK(UserInfoReadOrEditActivity_vii.this, handler);
                        instance.setupAccount(linkDeviceAccout, "888888");
                        instance.loginToken();
                    }
                    break;
                case Constants.GET_DATA_FAIL:
                    break;
                case FsConstants.HTTP_FINISH:
                    break;
                default:
                    break;
            }
        }
    };


}
