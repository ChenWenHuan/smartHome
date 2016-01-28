package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.fragment.HomeFragement_V11;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.message.MessageUtil;
import com.smarthome.client2.model.retrofitServices.LogoutService;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.model.logout.LogoutServiceResult;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MySettingActivity extends BaseActivity {

    @Bind(R.id.my_setting_page_header)
    FrameLayout mySettingPageHeader;

    // 推荐
    @Bind(R.id.ll_share_slding_menu)
    LinearLayout llShareSldingMenu;

    // 版本更新
    @Bind(R.id.ll_update_slding_menu)
    LinearLayout llUpdateSldingMenu;

    // 问题反馈
    @Bind(R.id.ll_response_sliding_menu)
    LinearLayout llResponseSlidingMenu;

    // 自动更新
    @Bind(R.id.tv_autologin)
    TextView tvAutologin;

    @Bind(R.id.ll_about_sliding_menu)
    LinearLayout llAboutSlidingMenu;

//    @Bind(R.id.ll_list_menus)
//    LinearLayout llListMenus;

    @Bind(R.id.tv_logout)
    TextView tvLogout;

    @Bind(R.id.ll_logout_sliding_menu)
    LinearLayout llLogoutSlidingMenu;


    private ServiceGenerator serviceGenerator = null;
    
    private MyBroadcastReceiver receiver;

    // private MyAsyncTask myAsyncTask;

    private Dialog dialog_about;

    private Dialog dialog_logout;

    private ProgressDialog progressDialog;

//    private MainActivity ma;

    private CustomActionBar mActionBar;

    /**
     * 分享的链接
     */
    private String mSharedLink;

    private String picPath;

    /**
     * 上次点击好友推荐的时间
     */
    private long mShareTime = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FsConstants.HTTP_START:
                    showProgressDialog("正在获取分享链接……");
                    break;
                case FsConstants.HEAD_IMAGE:
                    if (msg.obj != null) {
//                        imgSlidingRightMenuPersonPic.setImageBitmap((Bitmap) msg.obj);
//                        if (picPath != null) {
//                            Preferences preferences = Preferences.getInstance(ma.getApplicationContext());
//                            preferences.setHeadPath(picPath);
//                            ma.sendBroadcast(new Intent(HomeFragement_V11.NEW_HEAD_IMG));
//                        }
                    }
                    break;
                case FsConstants.HTTP_SUCCESS:
                    String result = (String) msg.obj;
                    if (ResultParsers.getCode(result).equals("200")) {
                        String tmp = ResultParsers.getData(result);
                        if (tmp != null && !tmp.isEmpty()) {
                            mSharedLink = tmp;
                            share();
                        } else {
                            Toast.makeText(MySettingActivity.this,
                                    getString(R.string.data_parser_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MySettingActivity.this,
                                getString(R.string.server_offline),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    Toast.makeText(MySettingActivity.this,
                            getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                    break;
                case FsConstants.HTTP_FINISH:
                    removeProgressDialog();
                    break;

                default:
                    break;
            }
        }

    };

    /**
     * 通知消息小红点变化
     */
    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (HomeFragement_V11.NEW_MESSAGE.equals(action)) {
                if (Preferences.getInstance(MySettingActivity.this).getHasNewMessage()) {
                    //把小红点先去掉
                } else {
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_setting_layout);
        serviceGenerator = ServiceGenerator.getInstance(this);
        ButterKnife.bind(this);
        addTopBarToHead();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            MySettingActivity.this.unregisterReceiver(receiver);
        }
        ButterKnife.unbind(this);
    }

    /**
     * 如果有对话框，点击home，再从widget点击进入家庭成员个人页面，点击返回后到成员列表界面会有对话框，体验不好。
     * 所以在这里dismiss掉。
     */
    @Override
    public void onStop() {
        if (dialog_about != null && dialog_about.isShowing()) {
            dialog_about.dismiss();
        }
//        if (dialog_clear != null && dialog_clear.isShowing()) {
//            dialog_clear.dismiss();
//        }
        if (dialog_logout != null && dialog_logout.isShowing()) {
            dialog_logout.dismiss();
        }
        super.onStop();
    }

    private void addTopBarToHead() {
        if (mActionBar != null) {
            mySettingPageHeader.removeView(mActionBar);
        }

        mActionBar = TopBarUtils.createCustomActionBarInvisiableRightImage(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                view -> finish(),
                "设置",
                R.drawable.default_pictures,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    }
                });
        mySettingPageHeader.addView(mActionBar);
    }

    /**
     * 调用友盟检查更新
     */
    @OnClick(R.id.ll_update_slding_menu)
    public void umengUpdate() {
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int arg0, UpdateResponse arg1) {
                switch (arg0) {
                    case UpdateStatus.No:
                        Toast.makeText(SmartHomeApplication.getInstance(),
                                "当前已是最新版本",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout:
                        Toast.makeText(SmartHomeApplication.getInstance(),
                                R.string.no_network,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        UmengUpdateAgent.forceUpdate(MySettingActivity.this);
    }

    /**
     * 退出登陆
     */
    @OnClick(R.id.ll_logout_sliding_menu)
    public void logoutDialog() {
        if (MySettingActivity.this == null)
            return;
        Builder builder = new Builder(MySettingActivity.this);
        builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                doLogout();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setTitle(null);
        builder.setMessage("确认退出吗?");
        dialog_logout = builder.create();
        dialog_logout.show();
    }

    /**
     * 关于对话框
     */
    @OnClick(R.id.ll_about_sliding_menu)
    public void AboutDialog() {
        View view = LayoutInflater.from(MySettingActivity.this)
                .inflate(R.layout.item_dialog_about, null);
        TextView tv_version = (TextView) view.findViewById(R.id.tv_version);
        PackageManager manager = MySettingActivity.this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(MySettingActivity.this.getPackageName(),
                    0);
            tv_version.setText("版本：" + info.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        dialog_about = new Dialog(MySettingActivity.this, R.style.circle_corner);
        dialog_about.setContentView(view);
        WindowManager.LayoutParams attributes = dialog_about.getWindow()
                .getAttributes();
        Preferences tmpP = Preferences.getInstance(MySettingActivity.this);
        attributes.width = (int) (tmpP.getScreenW() * 0.7);
        attributes.height = (int) (tmpP.getScreenH() * 0.55);
        dialog_about.getWindow().setAttributes(attributes);
        dialog_about.show();
    }

    @OnClick(R.id.ll_share_slding_menu)
    public void onShare() {
        long tmpTime = System.currentTimeMillis();
        // 避免短时间内连续点击多次弹出选择页面
        if (tmpTime - mShareTime <= 3000) {
            return;
        }
        mShareTime = tmpTime;
        if (mSharedLink == null) {
            getSharedLink();
        } else {
            share();
        }
    }

    @OnClick(R.id.ll_response_sliding_menu)
    public void onResponse() {
        startActivity(new Intent(MySettingActivity.this,
                FbCustomActivity.class));
    }

    private void doLogout() {
        showProgressDialog("正在退出……");
        LogoutService logoutService = serviceGenerator.createService(LogoutService.class);
        Call call = null;
        call = logoutService.logout();
        call.enqueue(new Callback<LogoutServiceResult>() {
                         @Override
                         public void onResponse(Response<LogoutServiceResult> response, Retrofit retrofit) {
                             removeProgressDialog();
                             if (response.body().getRetcode().equals(200)) {
                                 Intent intent = new Intent(MySettingActivity.this, LoginActivity_sm.class);
                                 startActivity(intent);
                                 AppManager.getAppManager().finishAllActivity();
                                 MessageUtil.stopPollingService();
                             }
                         }

                         @Override
                         public void onFailure(Throwable throwable) {
                             removeProgressDialog();
                             Toast.makeText(MySettingActivity.this, "退出失败，请重试", Toast.LENGTH_SHORT).show();
                         }
                     }
        );
    }
//    myAsyncTask = new MyAsyncTask();
//    myAsyncTask.execute();

    /**
     * 好友推荐，调用系统注册的程序
     */
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        // intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_SUBJECT, "应用推荐");
        intent.putExtra(Intent.EXTRA_TEXT, "关注孩子学习、家人健康，幸福生活，由此开始（来自"
                + getString(R.string.app_name) + "，戳我下载"
                + mSharedLink + "）");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择要分享到的程序"));
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(MySettingActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void removeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 向服务器请求分享链接
     */
    private void getSharedLink() {
        HttpJson pJson = new HttpJson();
        pJson.put("key", "download.qrcode");
        MyHttpUtil.post("/account/config.action", pJson, mHandler);
    }


//    /**
//     * 自动登录设置
//     */
//    private void setAutoLogin() {
//        autoLogin = !autoLogin;
//        Preferences.getInstance(MySettingActivity.this).setAutoLogin(autoLogin);
//        if (autoLogin) {
//            tv_autologin.setText("自动登录（已开启）");
//        } else {
//            tv_autologin.setText("自动登录（已关闭）");
//        }
//    }

//    /**
//     * 清除缓存对话框
//     */
//    private void ClearCacheDialog() {
//        dialog_clear = new Builder(MySettingActivity.this).setMessage("确定清除缓存？")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        File file = MySettingActivity.this.getCacheDir();
//                        if (file != null && file.exists() && file.isDirectory()) {
//                            for (File item : file.listFiles()) {
//                                item.delete();
//                            }
//                        }
//                        File fileSD = MySettingActivity.this.getExternalCacheDir();
//                        if (fileSD != null && fileSD.exists()
//                                && fileSD.isDirectory()) {
//                            for (File item : fileSD.listFiles()) {
//                                item.delete();
//                            }
//                        }
//                        new FileUtil(MySettingActivity.this).deleteFile();
//                        System.gc();
//                        Toast.makeText(MySettingActivity.this,
//                                "缓存已清除",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("取消", null)
//                .create();
//        dialog_clear.show();
//    }


//    private OnClickListener ll_ClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent;
//            MainActivity ra;
//            Preferences preferences = Preferences.getInstance(ma.getApplicationContext());
//            switch (v.getId()) {
//                case R.id.img_sliding_right_menu_person_pic:
//                    UserInfo userInfo = new UserInfo();
//                    mMemberId = preferences.getUserID();
//                    mMemberUserName = preferences.getRealName();
////                    userInfo.setUserId(mMemberId);
////                    userInfo.setGroupId(mGroupId);
////                    userInfo.setCurrentUser(mIsCurrentUser);
////                    userInfo.setName(mMemberUserName);
////                    userInfo.setDeviceCode(mStrIMEI);
////                    userInfo.setDeviceId(mStrDeviceId);
////                    userInfo.setParent(isParent);
////                    userInfo.setStudent(isStudent);
////                    userInfo.setOld(isOld);
//                    break;
//
//
//                case R.id.ll_add_slding_menu:
//                    //goMessageFragment();
//                    break;
//                case R.id.ll_device_slding_menu:
//                    intent = new Intent(ma,
//                            BindActivity_sm.class);
//                    mMemberId = preferences.getUserID();
//                    intent.putExtra("userId", mMemberId);
//                    startActivity(intent);
//                    break;
//
//
//                default:
//                    break;
//            }
//        }
//    };

//    class MyAsyncTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected void onPreExecute() {
//            showProgressDialog("正在退出……");
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String result = MyHttpUtil.post(HttpUtil.BASE_URL_SMART
//                    + "/account/logout.action", null);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            removeProgressDialog();
//            Log.i("LOGOUT", result);
//            if (result != null && ResultParsers.getCode(result).equals("200")) {
//                Intent intent = new Intent(MySettingActivity.this, LoginActivity_sm.class);
//                startActivity(intent);
//                MySettingActivity.this.finish();
//                MessageUtil.stopPollingService();
//            } else {
//                Toast.makeText(MySettingActivity.this, "退出失败，请重试", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//    }
}
