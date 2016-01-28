package com.smarthome.client2.familySchool.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.smarthome.client2.activity.WatchChatActivity_SM;
import com.smarthome.client2.bean.ChatMessage;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.widget.CircleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthome.client2.R;


public class WatchMessageAdapter extends BaseAdapter {


	private Context mContext;
	private LayoutInflater mInflater;
	private List<ChatMessage> mMsgList;
	private Handler mHandle;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private ViewHolder holder;
	private String fromImgUrl;
	private String toImgUrl;
	private Bitmap fromBM = null;
	private Bitmap toBM = null;
	private ImageDownLoader mImageLoader;


	
	public WatchMessageAdapter(Context context, Handler handle,
			List<ChatMessage> msgList, String fromUrl, String toUrl) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mHandle = handle;
		mMsgList = msgList;
		mInflater = LayoutInflater.from(context);
		this.fromImgUrl = fromUrl;
		this.toImgUrl = toUrl;
		mImageLoader = ImageDownLoader.getInstance();
		if (fromImgUrl != null && !fromUrl.equals("")){
			mImageLoader.downloadImage(fromImgUrl, FsConstants.HANDLE_IMAGE, mImgHandler,
					fromImgUrl);
		}
		if (toImgUrl != null && !toImgUrl.equals("")){
			mImageLoader.downloadImage(toImgUrl, FsConstants.HANDLE_IMAGE, mImgHandler,
					toImgUrl);
		}
	}
	
	 private Handler mImgHandler = new Handler()
	    {

	        @Override
	        public void handleMessage(Message msg)
	        {
	            switch (msg.what)
	            {
	                case FsConstants.HANDLE_IMAGE:
	                	String tag = msg.getData().getString("tag");
	                    if (tag.equals(fromImgUrl)){
	                    	fromBM = (Bitmap)msg.obj;
	                    	notifyDataSetChanged();
	                    }else if (tag.equals(toImgUrl)){
	                    	toBM = (Bitmap)msg.obj;
	                    	notifyDataSetChanged();
	                    }
	                	break;
	                default:
	                    break;
	            }
	        }
	    };

	public void removeHeadMsg() {
		if (mMsgList.size() - 10 > 10) {
			for (int i = 0; i < 10; i++) {
				mMsgList.remove(i);
			}
			notifyDataSetChanged();
		}
	}

	public void setMessageList(List<ChatMessage> msgList) {
		mMsgList = msgList;
		notifyDataSetChanged();
	}

	public void upDateMsg(ChatMessage msg) {
		mMsgList.add(msg);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ChatMessage item = mMsgList.get(position);
		String isComMsg = item.getMsgFlag();
		String isNew = item.getIsNew();
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			holder = new ViewHolder();
			if (isComMsg.equals("1")) {
				convertView = mInflater.inflate(R.layout.chat_item_left, null);
			} else {
				convertView = mInflater.inflate(R.layout.chat_item_right, null);
			}
			holder.head = (CircleImageView) convertView.findViewById(R.id.icon);
			holder.time = (TextView) convertView.findViewById(R.id.datetime);
			holder.msg = (TextView) convertView.findViewById(R.id.textView2);
			holder.imageView = (ImageView) convertView.findViewById(R.id.new_msg_flag);

			convertView.setTag(R.drawable.ic_launcher + position);
		} else {
			holder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
					+ position);
		}	
		if (isComMsg.equals("1")) {
			if (fromBM != null){
				holder.head.setImageBitmap(fromBM);
			}
		} else {
			if (toBM != null){
				holder.head.setImageBitmap(toBM);
			}
		}
		holder.time.setText(getChatTime(Long.parseLong(item.getMsgTime())));
		holder.msg.setText(item.getMsgContent());
		//如果是声音文件
		if (item.getMsgType().equals("2")) {
			holder.msg.setText("");
			if (isComMsg.equals("0")){
				holder.msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatto_voice_playing, 0, 0, 0);
			}else{
				holder.msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatfrom_voice_playing, 0);
				if(isNew.equals("1")){
					holder.imageView.setVisibility(View.VISIBLE);
				}else{
					holder.imageView.setVisibility(View.GONE);
				}
			}
		} else {
			holder.msg.setText(item.getMsgContent());			
			holder.msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		
		holder.msg.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (item.getMsgType().equals("2")) {
					if (item.getIsNew().equals("0")){
						playMusic( Constants.WATCH_REACORD_AUDIO_PATH +item.getMsgContent()) ;
					}else{
						((WatchChatActivity_SM)mContext).playRecordMusic(item);
					}
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		CircleImageView head;
		TextView time;
		TextView msg;
		ImageView imageView;

	}
	
	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


		public  String getTime(long time) {
			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
			return format.format(new Date(time));
		}

		public  String getHourAndMin(long time) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			return format.format(new Date(time));
		}

		public  String getChatTime(long timesamp) {
			String result = "";
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			Date today = new Date(System.currentTimeMillis());
			Date otherDay = new Date(timesamp);
			int temp = Integer.parseInt(sdf.format(today))
					- Integer.parseInt(sdf.format(otherDay));

			switch (temp) {
			case 0:
				result = "今天 " + getHourAndMin(timesamp);
				break;
			case 1:
				result = "昨天 " + getHourAndMin(timesamp);
				break;
			case 2:
				result = "前天 " + getHourAndMin(timesamp);
				break;

			default:
				// result = temp + "天前 ";
				result = getTime(timesamp);
				break;
			}

			return result;
		}
		
		
		public void sendMessage(int what, String type, String data, String url, Bitmap bm) {
		Bundle b = new Bundle();
		b.putString("url", url);
		b.putString("data", data);
		b.putString("type", type);
		
		Message m = new Message();
		m.what = what;
		m.setData(b);
		m.obj = bm;
		mHandle.sendMessage(m);
	}
}