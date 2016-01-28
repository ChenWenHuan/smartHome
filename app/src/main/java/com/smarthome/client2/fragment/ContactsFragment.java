package com.smarthome.client2.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.smarthome.client2.R;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.activity.SearchFamilyMember_sm;
import com.smarthome.client2.bean.ContactBean;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.familySchool.adapter.HomeContactListAdapter;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;


public class ContactsFragment extends CommonFragment {

	public static final int REQUEST_CODE_USER_MESSAGE_DETAIL = 1;

	public static final int RESULT_CODE_USER_MESSAGE_DETAIL = 2;

	private View containerView;

	private View listHeader;

	private ProgressDialog dialog;

	private ListView list_view_contacts;
	
	private boolean isLoad = false;


	private HomeContactListAdapter contactsListAdapter = null;

	private FrameLayout fl_head_user_contacts;

	private CustomActionBar actionBar;

	private MainActivity ma;
	
	private ArrayList<ContactBean> contactDatalist = new ArrayList<ContactBean>();
	
	private ArrayList<FamilyClassBean> familyClassList = null;
	
	boolean inSearchMode = false;

	private final static String TAG = "ContactsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		containerView = inflater.inflate(R.layout.contact_fragement_view_sm,
				null);

		addTopBarToHead();
		initView();
		initData();
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

	private void initView() {
		list_view_contacts = (ListView) containerView.findViewById(R.id.list_view_contact);
		LayoutInflater inflater = LayoutInflater.from(ma);
		listHeader = inflater.inflate(R.layout.contact_fragement_list_head, null);
		//list_view_contacts.addHeaderView(listHeader, null, true);
		listHeader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ma, SearchFamilyMember_sm.class);
				startActivity(intent);
			}
		});
	}
	
	private void initData() {
		SmartHomeApplication app = SmartHomeApplication.getInstance();
		ArrayList<FamilyClassBean> contacts = app.getDataList();

		if (!isLoad) {
			isLoad = true;
			familyClassList = SmartHomeApplication.getInstance().getDataList();
			for (int i = 0; i < familyClassList.size(); i++) {
				FamilyClassBean item = familyClassList.get(i);
				for (int j = 0; j < item.getList().size(); j++) {
					MemBean mem = (MemBean) item.getList().get(j);
					if (mem.getMemType().compareToIgnoreCase("5") != 0) {
						ContactBean contactStudentItem = new ContactBean(mem.memID,
								mem.memHeadImgUrl,
								mem.memName, mem.getMemType(), mem.phoneNum);
						contactDatalist.add(contactStudentItem);
					}
				}
			}
		}

		contactsListAdapter = new HomeContactListAdapter(ma, contactDatalist, mHandler);
		list_view_contacts.setAdapter(contactsListAdapter);
		contactsListAdapter.notifyDataSetChanged();
		
	}

	private void addTopBarToHead() {
		fl_head_user_contacts = (FrameLayout) containerView.findViewById(R.id.fl_head_contact);
		actionBar = TopBarUtils.createCustomActionBar(getActivity(),
				0,
				null,
				"通讯录",
				0,
				null);
		fl_head_user_contacts.addView(actionBar);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case FsConstants.HANDLE_IMAGE:
					handleImageLoader(msg);
					break;

			}
		}
	};


	private void handleImageLoader(Message msg) {

		Bitmap bm;
		String tag;

		bm = (Bitmap) msg.obj;
		tag = msg.getData().getString("tag");

		CircleImageView headView = (CircleImageView) list_view_contacts.findViewWithTag(tag);

		if (bm != null && headView != null) {
			headView.setImageBitmap(bm);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
	}
}
