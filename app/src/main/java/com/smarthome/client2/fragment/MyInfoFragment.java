package com.smarthome.client2.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.XXX.expandableListView.Group;
import com.smarthome.client2.XXX.expandableListView.ui.PinnedHeaderExpandableListView;
import com.smarthome.client2.XXX.expandableListView.ui.StickyLayout;
import com.smarthome.client2.activity.CameraAddByIdActivity;
import com.smarthome.client2.activity.CameraAddStepOne;
import com.smarthome.client2.activity.CameraAddStepTwo;
import com.smarthome.client2.activity.CameraDeviceList;
import com.smarthome.client2.activity.FamilyInfoActivity_vii;
import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.activity.MySettingActivity;
import com.smarthome.client2.activity.UserInfoReadOrEditActivity_vii;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.CameraInfoItem;
import com.smarthome.client2.bean.ContactBean;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.familySchool.adapter.HomeContactListAdapter;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyInfoFragment extends CommonFragment implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener, StickyLayout.OnGiveUpTouchEventListener {

    private View containerView;

    private ProgressDialog dialog;
    private boolean isLoad = false;

    private HomeContactListAdapter contactsListAdapter = null;

    private CustomActionBar actionBar;

    private MainActivity ma;

    private ArrayList<FamilyClassBean> familyClassList = null;
    private HashMap<String, List<CameraInfoItem>> mCamaraMap = null;
    private List<CameraInfoItem> cameraList = null;

    private ArrayList<BaseBean> contactDatalist = new ArrayList<BaseBean>();
    private ArrayList<BaseBean> otherSettings = new ArrayList<BaseBean>();
    private ArrayList<BaseBean> deviceLists = new ArrayList<BaseBean>();

    SmartHomeApplication app = SmartHomeApplication.getInstance();

    private final static String TAG = "MyInfoFragment";

    LinearLayout llStickyHeader;
    FrameLayout flHeadMyInfoFragment;
    CircleImageView imgMyInfoAccountHead;
    private PinnedHeaderExpandableListView expandableListView;
    private StickyLayout stickyLayout;
    private ArrayList<Group> groupList;
    private ArrayList<List<BaseBean>> childList;

    private MyexpandableListAdapter adapter;

    private String mStrMyEhome = "我的E家";
    private String mStrSetting = "设置";
    private String mStringAddCameraNotice = "暂无家庭摄像头，请点击＋添加!";
    private String mStringAddMemNotice = "暂无其他家庭成员，请到首页添加!";
    private String mStringGroupTitleOtherSetting = "其他设置";
    private String mStringHomeDevices = "家庭设备";
    private String mStringHomeMembers = "家庭成员";
    private String mStringFooterName = "  ";

    private int familyMemberSize = 0;
    private String mStrFamilyId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.my_info_children_main,
                null);

        llStickyHeader = (LinearLayout) containerView.findViewById(R.id.sticky_header);
        flHeadMyInfoFragment = (FrameLayout) containerView.findViewById(R.id.fl_head_my_info_fragment);
        imgMyInfoAccountHead = (CircleImageView) containerView.findViewById(R.id.img_my_info_account_head);
        expandableListView = (PinnedHeaderExpandableListView) containerView.findViewById(R.id.expandablelist);
        stickyLayout = (StickyLayout) containerView.findViewById(R.id.sticky_layout);
        addTopBarToHead();
        initData();
        adapter = new MyexpandableListAdapter(ma);
        expandableListView.setAdapter(adapter);

        for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
            expandableListView.expandGroup(i);
        }

        expandableListView.setOnHeaderUpdateListener(this);
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);
        stickyLayout.setOnGiveUpTouchEventListener(this);
        llStickyHeader.setOnClickListener((v) -> {
                Intent intent = new Intent(ma, UserInfoReadOrEditActivity_vii.class);
                String loginUserId = SmartHomeApplication.getInstance().getLoginMemberInfo().getMemID();
                intent.putExtra("userId", loginUserId);
                startActivity(intent);
            }
        );

        initView();

        return containerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ma = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        log("onResume");
        super.onResume();
    }

    public void refreshData(){
        initData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
        childList.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        String myHeadPath = app.getLoginMemberInfo().getMemHeadImgUrl();
        if (myHeadPath.length() > 5) {
            Picasso.with(ma).load(myHeadPath).into(imgMyInfoAccountHead);
        }
    }

    private void addTopBarToHead() {
        actionBar = TopBarUtils.createCustomActionBar(getActivity(),
                0,
                null,
                "我的",
                0,
                null);
        flHeadMyInfoFragment.addView(actionBar);
    }

    /***
     * InitData
     */
    void initData() {
        MemBean loginMember = SmartHomeApplication.getInstance().getLoginMemberInfo();

        childList = new ArrayList<List<BaseBean>>();
        groupList = new ArrayList<Group>();

        //全部设备分组
        Group groupListDevice = new Group();
        groupListDevice.setTitle(mStringHomeDevices);


        groupList.add(groupListDevice);

        //全部成员分组
        Group groupListMember = new Group();
        groupListMember.setTitle(mStringHomeMembers);
        groupList.add(groupListMember);

        Group groupOtherSettings = new Group();
        groupOtherSettings.setTitle(mStringGroupTitleOtherSetting);
        groupList.add(groupOtherSettings);

        deviceLists.clear();
        mCamaraMap = SmartHomeApplication.getInstance().getCameraMap();
        cameraList = mCamaraMap.get(loginMember.getMemGroupID());

        if (cameraList == null || cameraList.size() == 0) {
            CameraInfoItem deviceListNotice = new CameraInfoItem();
            deviceListNotice.setCameraShowName(mStringAddCameraNotice);
            deviceLists.add(deviceListNotice);
            childList.add(deviceLists);
        } else {
            int size = cameraList.size();
            for (int i = 0; i < size; i++) {
                BaseBean mem = cameraList.get(i);
                deviceLists.add(mem);
            }
            childList.add(deviceLists);
        }


        // 得出全部成员group的数据
        contactDatalist.clear();
        familyClassList = SmartHomeApplication.getInstance().getDataList();
        for (int i = 0; i < familyClassList.size(); i++) {
            FamilyClassBean item = familyClassList.get(i);
            // 挑选最多家庭的成员的一个家庭显示 2.0 版本
            if (familyMemberSize > item.getList().size()) {
                mStrFamilyId = item.getId();
            }
            familyMemberSize = item.getList().size();
            for (int j = 0; j < familyMemberSize; j++) {
                MemBean mem = (MemBean) item.getList().get(j);
                if (mem.getMemType().compareToIgnoreCase("5") != 0) {
                    ContactBean contactStudentItem = new ContactBean(mem.memID,
                            mem.memHeadImgUrl,
                            mem.memName,
                            mem.deviceType,
                            mem.phoneNum);
                    contactStudentItem.setContact_group_id(mem.memGroupID);
                    contactDatalist.add(contactStudentItem);

                }
            }
        }

        otherSettings.clear();
        if (contactDatalist.size() == 0) {
            ContactBean contactNotice = new ContactBean(" ", " ", mStringAddMemNotice, " ", " ");
            contactDatalist.add(contactNotice);
            childList.add(contactDatalist);
        } else {
            // 将全部成员的通讯录添加到childlist
            childList.add(contactDatalist);
        }

        otherSettings.clear();
        //模拟一个 我的E家 数据
        ContactBean eHome = new ContactBean(null, null, "我的E家", null, null);
        otherSettings.add(eHome);

        //模拟一个 设置 数据
        ContactBean eSetting = new ContactBean(null, null, "设置", null, null);
        otherSettings.add(eSetting);

        childList.add(otherSettings);
    }

    class GroupHolder {
        TextView textView;
        ImageView imageView;
    }

    class ChildHolder {
        CircleImageView myInfoItemImg;
        TextView textName;
        TextView textPhone;
        ImageView myInfoRightNoticeImg;
    }

    /***
     * ???
     *
     * @author Administrator
     */
    class MyexpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyexpandableListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {

            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = inflater.inflate(R.layout.my_info_group, null);
                groupHolder.textView = (TextView) convertView
                        .findViewById(R.id.group);
                groupHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.image);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            String groudTitle = ((Group) getGroup(groupPosition)).getTitle();
            groupHolder.textView.setText(groudTitle);
            groupHolder.imageView.setImageResource(R.drawable.ic_add);
            AddCameraOrMemClick addClick = new AddCameraOrMemClick(groupPosition);
            groupHolder.imageView.setOnClickListener(addClick);

            // ture is Expanded or false is not isExpanded
            if (isExpanded) {
                groupHolder.imageView.setImageResource(R.drawable.ic_add);
                //(R.drawable.expanded)
            }

            if (groudTitle.equalsIgnoreCase(mStringGroupTitleOtherSetting) ||
                    groudTitle.equalsIgnoreCase(mStringHomeMembers)) {
                groupHolder.imageView.setVisibility(View.INVISIBLE);
            } else {
                groupHolder.imageView.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder childHolder = null;
            String contactName = "";
            String phone = "";
            String itemImg = "";
            String devType = "";

            if (groupPosition == 0) {
                contactName = ((CameraInfoItem) getChild(groupPosition,
                        childPosition)).getCameraShowName();
            } else {
                contactName = ((ContactBean) getChild(groupPosition,
                        childPosition)).getContact_name();
                phone = ((ContactBean) getChild(groupPosition,
                        childPosition)).getContact_phone();
                itemImg = ((ContactBean) getChild(groupPosition,
                        childPosition)).getContact_img();
                devType = ((ContactBean) getChild(groupPosition,
                        childPosition)).getContact_type();
                if (!TextUtils.isEmpty(devType)) {
                    if (devType.equals("1")) {
                        devType = "学生机";
                    } else if (devType.equals("2")) {
                        devType = "老人机";
                    } else if (devType.equals("6")) {
                        devType = "儿童手表";
                    } else if (devType.equals("4")) {
                        devType = "智能机";
                    }
                }
            }


            if (convertView == null) {
                childHolder = new ChildHolder();

                if (groupPosition != 1 || contactName.equalsIgnoreCase(mStringAddMemNotice)) {
                    convertView = inflater.inflate(R.layout.my_info_child, null);
                } else {
                    convertView = inflater.inflate(R.layout.my_info_child_all_contacts, null);
                }
                childHolder.textName = (TextView) convertView
                        .findViewById(R.id.name);
                childHolder.textPhone = (TextView) convertView
                        .findViewById(R.id.phone);
                childHolder.myInfoItemImg = (CircleImageView) convertView
                        .findViewById(R.id.myInfo_item_img);

                childHolder.myInfoRightNoticeImg = (ImageView) convertView
                        .findViewById(R.id.right_notice);
                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }

            childHolder.textName.setText(contactName);
            if(!TextUtils.isEmpty(devType)){
                childHolder.textPhone.setText(String.format("%s:%s", devType, phone));
            }else{
                childHolder.textPhone.setText(phone);
            }


            if (contactName.equalsIgnoreCase(mStrMyEhome)) {
                childHolder.myInfoItemImg.setImageResource(R.drawable.ic_infohome);
            } else if (contactName.equalsIgnoreCase(mStrSetting)) {
                childHolder.myInfoItemImg.setImageResource(R.drawable.ic_seting);
            } else if (contactName.equalsIgnoreCase(mStringAddMemNotice)) {
                childHolder.myInfoItemImg.setVisibility(View.INVISIBLE);
                childHolder.myInfoRightNoticeImg.setVisibility(View.INVISIBLE);
            } else {
                if (itemImg != null && itemImg.length() > 5) {
                    Picasso.with(ma).load(itemImg).into(childHolder.myInfoItemImg);
                } else {
                    //default picture，后面加上性别判断
                    childHolder.myInfoItemImg.setImageResource(R.drawable.bg_head);
                }
            }
            if (groupPosition == 0) {
                childHolder.myInfoItemImg.setImageResource(R.drawable.ic_equipment);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v,
                                int groupPosition, final long id) {
        if (groupPosition == 0) {

        }
        if (groupPosition == 1) {

        }
        return true;

        //return false;
    }

    private class AddCameraOrMemClick implements View.OnClickListener {

        private int position;

        public AddCameraOrMemClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            if (position == 0) {
                //添加摄像头
                Log.e("MyInfo", "add camera click");
                Intent intent = new Intent(ma, CameraAddStepOne.class);
                intent.putExtra("familyId", mStrFamilyId);
                ma.startActivity(intent);
            } else if (position == 1) {
                //添加家庭成员
                Log.e("MyInfo", "add family mem click");
            }

        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {

        if (groupPosition == 0) {
            // 摄像头逻辑
            Intent intent = new Intent(ma,CameraAddByIdActivity.class);
            intent.setAction(CameraAddByIdActivity.CAMERA_UPDATE_ACTION);
            intent.putExtra(CameraDeviceList.CAMERA_DEV_ID, ((CameraInfoItem)childList.get(groupPosition).get(childPosition)).deviceid);
            intent.putExtra(CameraDeviceList.CAMERA_SHOW_NAME_KEY, ((CameraInfoItem)childList.get(groupPosition).get(childPosition)).cameraShowName);
            intent.putExtra(CameraDeviceList.CAMERA_CID_KEY,((CameraInfoItem)childList.get(groupPosition).get(childPosition)).cameraID);
            intent.putExtra(CameraDeviceList.CAMERA_USER_NAME_KEY,((CameraInfoItem)childList.get(groupPosition).get(childPosition)).cameraUserName);
            intent.putExtra(CameraDeviceList.CAMERA_USER_PASS_KEY,((CameraInfoItem)childList.get(groupPosition).get(childPosition)).cameraPasswd);
            startActivity(intent);
        } else if (groupPosition == 1) {
            Intent intent = new Intent(ma, UserInfoReadOrEditActivity_vii.class);
            String strUserId = ((ContactBean) childList.get(groupPosition).get(childPosition)).getContact_id();
            String aliasName = ((ContactBean) childList.get(groupPosition).get(childPosition)).getContact_name();
            String devType = ((ContactBean) childList.get(groupPosition).get(childPosition)).getContact_type();
            String familyId = ((ContactBean) childList.get(groupPosition).get(childPosition)).getContact_group_id();
            intent.putExtra("userId", strUserId);
            intent.putExtra("aliasName", aliasName);
            intent.putExtra("devtype", devType);
            intent.putExtra("familyid", familyId);
            startActivity(intent);
        } else if (groupPosition == 2) {
            String contactName = ((ContactBean) childList.get(groupPosition).get(childPosition)).getContact_name();
            if (contactName.equalsIgnoreCase(mStrSetting)) {
                Intent intent = new Intent(ma, MySettingActivity.class);
                startActivity(intent);
            } else if (contactName.equalsIgnoreCase(mStrMyEhome)) {
                Intent intent = new Intent(ma, FamilyInfoActivity_vii.class);
                intent.putExtra("familyId", mStrFamilyId);
                startActivity(intent);
            } else {
            }
        }

        return false;
    }

    @Override
    public View getPinnedHeader() {
        View headerView = ma.getLayoutInflater().inflate(R.layout.my_info_group, null);
        headerView.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        Group firstVisibleGroup = (Group) adapter.getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.group);
        textView.setText(firstVisibleGroup.getTitle());
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (expandableListView.getFirstVisiblePosition() == 0) {
            View view = expandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }

    private static final boolean DEBUG = SmartHomeApplication.PRINT_LOG;

    private static void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }

}
