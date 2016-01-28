package com.smarthome.client2.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.activity.FragmentModel;
import com.smarthome.client2.activity.LoginActivity_sm;
import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.HomeFamilyClassListAdapter;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LoginUtil;
import com.smarthome.client2.widget.CircleImageView;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeFragement_V11 extends CommonFragment {
	
	public final static String NEW_MESSAGE = "com.smarthome.client2.fragment.HomeFragment.sendNewMessage";
	public final static String NEW_HEAD_IMG= "com.smarthome.client2.fragment.HomeFragment.reflshHeadImage";

	private MainActivity ma;

	private View containerView;

	private ListView listViewHome = null;

	private HomeFamilyClassListAdapter listAdapter = null;

	private ArrayList<FamilyClassBean> familClassList = null;

	private ProgressDialog mProgressDlg;

	private Fragment mContent;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		familClassList = SmartHomeApplication.getInstance().getDataList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ma = (MainActivity) activity;
		mProgressDlg = new ProgressDialog(ma);
		mProgressDlg.setCanceledOnTouchOutside(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		
		containerView = inflater.inflate(R.layout.home_fragement_v11, null);
		listViewHome = (ListView) containerView.findViewById(R.id.lv_home);
		return containerView;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		listAdapter = new HomeFamilyClassListAdapter(ma, familClassList, mHandler);
		listViewHome.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
	}
	/**
	 * 调用相应的fragment
	 *
	 * @param fragment
	 */
	public void switchContent(final FragmentModel fragment) {
		mContent = fragment.mFragment;

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commitAllowingStateLoss();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
			}
		}, 50);

	}

	private void handleImageLoader(Message msg) {

		Bitmap bm;
		String tag;

		bm = (Bitmap) msg.obj;
		tag = msg.getData().getString("tag");

		CircleImageView headView = (CircleImageView) listViewHome.findViewWithTag(tag);

		if (bm != null && headView != null) {
			headView.setImageBitmap(bm);
		}
	}

	private void handleHomeImageLoader(Message msg) {

		Bitmap bm;
		String tag;

		bm = (Bitmap) msg.obj;
		tag = msg.getData().getString("imgurl");

		ImageView headView = (ImageView) listViewHome.findViewWithTag(tag);
		if (bm != null && headView != null) {
			headView.setImageBitmap(bm);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case Constants.GET_DATA_START:
					break;
				case FsConstants.HANDLE_IMAGE:
					handleImageLoader(msg);
					break;
				case FsConstants.HOME_IMAGE:
					handleHomeImageLoader(msg);
					break;
				case Constants.GET_DATA_FAIL:
					mProgressDlg.dismiss();
					
					break;
				case Constants.SESSION_TIME_OUT:
					Intent intent = new Intent(getActivity(),
							LoginActivity_sm.class);
					startActivity(intent);
					break;
				case HomeFamilyClassListAdapter.GET_NEW_HOME_MESSAGE_SUCCESS:
					refreshFamilyMsgView(msg);
				case 201:
					mProgressDlg.dismiss();
					Toast.makeText(ma,
							ma.getString(R.string.no_network),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}

	};

	public  void refreshFamilyView() {

		familClassList = SmartHomeApplication.getInstance().getDataList();
		listAdapter.setDataSource(familClassList);
		listAdapter.notifyDataSetChanged();
		return;
	}

	private void refreshFamilyMsgView(Message msg) {

		String tag;
		String msgContent;

		msgContent = (String) msg.obj;
		tag = msg.getData().getString("tag");

		TextView tvFamilyMsg = (TextView) listViewHome.findViewWithTag(tag);
		if (tvFamilyMsg == null) {
		}
		if (msgContent != null) {
			tvFamilyMsg.setText(msgContent);
		}
	}


	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(null);
		super.onDestroy();
	}

}
