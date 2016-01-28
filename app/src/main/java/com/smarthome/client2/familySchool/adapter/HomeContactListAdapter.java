package com.smarthome.client2.familySchool.adapter;

import java.util.ArrayList;

import com.smarthome.client2.R;
import com.smarthome.client2.bean.ContactBean;
import com.smarthome.client2.bean.SysMessageBean;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;
import com.smarthome.client2.widget.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class HomeContactListAdapter extends BaseAdapter {

	private Context mContext;

	private LayoutInflater mInflater;

	private ArrayList<ContactBean> mContactList;

	private ImageDownLoader mLoader;

	private onImageLoaderListener mListener;

	private Handler imgHandler;

	public HomeContactListAdapter(Context context, ArrayList<ContactBean> list, Handler imgHandle) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mContactList = list;
		mLoader = ImageDownLoader.getInstance();
		imgHandler = imgHandle;
		mListener = new onImageLoaderListener() {
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				notifyDataSetChanged();
			}
		};
		mLoader.addListener(FsConstants.NOTICE_IMAGE, mListener);

	}

	@Override
	public int getCount() {
		return mContactList.size();
	}

	@Override
	public ContactBean getItem(int position) {
		return mContactList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
	                    ViewGroup parent) {

		Holder holder = null;
		final ContactBean bean = mContactList.get(position);

		if (null == convertView) {
			convertView = mInflater
					.inflate(R.layout.item_list_view_contact, null);
			holder = new Holder();
			holder.contact_lay = (RelativeLayout) convertView.findViewById(R.id.ll_contacts_info);
			holder.contact_item_img = (CircleImageView) convertView.findViewById(R.id.contact_item_img);
			//holder.contact_item_img = (ImageView) convertView.findViewById(R.id.contact_item_img);
			//if(bean.getContact_img().compareToIgnoreCase("") != 0) {
			holder.contact_item_img.setTag(bean.getContact_id() + position + bean.getContact_img());
			//}
			//else {
			//	holder.contact_item_img = (ImageView) convertView.findViewById(R.id.contact_item_img);
			//}

			holder.contact_item_title = (TextView) convertView.findViewById(R.id.contact_item_title);
			holder.contact_item_phone = (TextView) convertView.findViewById(R.id.contact_item_phone);
			holder.contact_item_phone_img =  (CircleImageView) convertView.findViewById(R.id.contact_item_phone_img);


			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		ContactItemClick contactItemClick = new ContactItemClick(bean);

		int s_img = R.drawable.default_touxiang;
		switch (Integer.parseInt(bean.getContact_type())) {
			case SysMessageBean.TYPE_01:
				s_img = R.drawable.ico_class1_msg;
				break;
			case SysMessageBean.TYPE_03:
				s_img = R.drawable.msg_friend;
				break;
		}
		if (bean.getContact_img().equals("")) {
			holder.contact_item_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ico_head_blue));
		} else {
			mLoader.downloadImage(bean.getContact_img(), FsConstants.HANDLE_IMAGE, imgHandler,
					bean.getContact_id() + position + bean.getContact_img());
		}

		holder.contact_item_phone_img.setOnClickListener(contactItemClick);
		holder.contact_item_title.setText(bean.getContact_name());
		holder.contact_item_phone.setText(bean.getContact_phone());
		return convertView;
	}

	class Holder {
		public RelativeLayout contact_lay;
		public TextView contact_item_title;
		public TextView contact_item_phone;
		public CircleImageView contact_item_img;
		public CircleImageView contact_item_phone_img;
	}


	private class ContactItemClick implements OnClickListener {


		ContactBean contactItem = null;

		public ContactItemClick(ContactBean item) {
			this.contactItem = item;
		}

		@Override
		public void onClick(View v) {
			final String phoneNum = contactItem.contact_phone;
			Intent intent = new Intent(Intent.ACTION_CALL);
			String call = "tel:" + phoneNum;
			intent.setData(Uri.parse(call));
			mContext.startActivity(intent);
		}

	}

}
