package com.smarthome.client2.friendgroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.friendgroup.PinnedSectionListView.PinnedSectionListAdapter;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.AsyncImageLoader;
import com.smarthome.client2.util.ScreenUtils;
import com.umeng.analytics.MobclickAgent;

public class FriendGroup extends Activity implements
        RefreshListView.IOnRefreshListener
{

    private final static String HEADSHOT = "_headshot";

    private final static String CONTENTSHOT = "_contentshot";

    private final static int RESUlT_LIST = 1000;

    private RelativeLayout pengyouquan_view, maillist_view;

    /////////////////////朋友圈///////////////////////
    private RefreshListView friendGroup;

    private FriendGroupAdapter friendGroupAdapter;//朋友圈adapter

    private Button addItem;

    private ImageButton pengyouquan_part, maillist_part;

    private LinearLayout soft_input_keyboard;

    private EditText item_add_comment_content;//评论edittext

    private Button item_add_comment_btn;//评论提交

    private ImageButton title_pengyouquan_back;

    /////////////////////朋友圈///////////////////////

    private List<Map<String, Bitmap>> bitmapList = new ArrayList<Map<String, Bitmap>>();

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    takePicturePath = (String) msg.obj;
                    break;

                default:
                    break;
            }
        }
    };

    private String takePicturePath;

    private RefreshDataAsynTask mRefreshAsynTask;

    private ArrayList<Map<String, String>> commentList = new ArrayList<Map<String, String>>();

    /**
     * headshot headname time content_img content_text
     * 
     * */
    private List<Map<String, Object>> friendList = new ArrayList<Map<String, Object>>();

    private PhotoLayout photoLayout;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_group);
        //		initFriendGroupView();
        pengyouquan_view = (RelativeLayout) findViewById(R.id.pengyouquan_view);
        maillist_view = (RelativeLayout) findViewById(R.id.maillist_view);

        addItem = (Button) findViewById(R.id.pengyouquan_add);
        addItem.setOnClickListener(item_add_click);

        soft_input_keyboard = (LinearLayout) findViewById(R.id.soft_input_keyboard);
        item_add_comment_content = (EditText) findViewById(R.id.item_add_comment_content);
        item_add_comment_btn = (Button) findViewById(R.id.item_add_comment_btn);

        pengyouquan_part = (ImageButton) findViewById(R.id.pengyouquan_part);
        maillist_part = (ImageButton) findViewById(R.id.maillist_part);
        pengyouquan_part.setOnClickListener(item_add_click);
        maillist_part.setOnClickListener(item_add_click);
        initFriendGroupView();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    private void initFriendGroupView()
    {
        friendGroup = (RefreshListView) findViewById(R.id.pengyouquan);
        friendGroupAdapter = new FriendGroupAdapter();
        friendGroup.setAdapter(friendGroupAdapter);
        friendGroup.setOnRefreshListener(this);

        final ActionBar actionBar = this.getActionBar();
        actionBar.setCustomView(R.layout.friend_group_title);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.show();
        title_pengyouquan_back = (ImageButton) findViewById(R.id.title_pengyouquan_back);
        title_pengyouquan_back.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                finish();
            }
        });

        photoLayout = new PhotoLayout(getApplicationContext(), FriendGroup.this);
        photoLayout.setHandler(handler);

        //		intent = getIntent();
        //		if (intent.getExtras() != null
        //				&& (intent.getExtras().get("item_content_img") != null || intent
        //						.getExtras().get("item_content_img") != null)) {
        //			for (int i = 0; i < 10; i++) {
        //				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");
        //				Map<String, Object> map = new HashMap<String, Object>();
        //				map.put("item_headshot", R.drawable.ic_launcher);
        //				map.put("item_headname", "daitm" + i);
        //				map.put("item_content_time", sdf.format(new Date()));
        //				map.put("item_content_img",
        //						intent.getExtras().get("item_content_img"));
        //				map.put("item_content_text",
        //						intent.getExtras().get("item_content_text"));
        //
        //				friendList.add(map);
        //			}
        //			List<Uri> imgList = (ArrayList<Uri>) intent.getExtras().get(
        //					"item_content_img_uri");
        //			List<Bitmap> smallList = (ArrayList<Bitmap>) intent.getExtras().get(
        //					"item_content_img");
        //			
        //			for(int j=0;j<smallList.size();j++){
        //				Map<String,Bitmap> imgMap = new HashMap<String, Bitmap>();
        //				imgMap.put("small", smallList.get(j));
        //				imgMap.put("big", photoLayout.getBigImage(imgList.get(j)));
        //				bitmapList.add(imgMap);
        //			}
        //			friendGroupAdapter.notifyDataSetChanged();
        //		}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if (resultCode == Activity.RESULT_OK)
            {
                switch (requestCode)
                {
                // 如果是直接从相册获取
                    case Constants.REQUEST_CODE_FOR_PHOTO_ALBUM:
                        photoLayout.startPhotoZoom(data.getData());
                        break;
                    // 如果是调用相机拍照时
                    case Constants.REQUEST_CODE_FOR_TAKE_CAMERA:
                        File temp = new File(takePicturePath);
                        photoLayout.startPhotoZoom(Uri.fromFile(temp));
                        break;
                    // 取得裁剪后的图片
                    case Constants.REQUEST_CODE_START_PHOTO_ZOOM:
                        if (data != null)
                        {
                            photoLayout.setPicToView(data);
                        }
                        break;
                    case RESUlT_LIST:
                        if (data.getExtras() != null
                                && (data.getExtras().get("item_content_img") != null || data.getExtras()
                                        .get("item_content_img") != null))
                        {
                            for (int i = 0; i < 10; i++)
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "MM-dd hh:mm");
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("item_headshot", R.drawable.ic_launcher);
                                map.put("item_headname", "daitm" + i);
                                map.put("item_content_time",
                                        sdf.format(new Date()));
                                map.put("item_content_img", data.getExtras()
                                        .get("item_content_img"));
                                map.put("item_content_text", data.getExtras()
                                        .get("item_content_text"));

                                friendList.add(map);
                            }
                            List<Uri> imgList = (ArrayList<Uri>) data.getExtras()
                                    .get("item_content_img_uri");
                            List<Bitmap> smallList = (ArrayList<Bitmap>) data.getExtras()
                                    .get("item_content_img");

                            for (int j = 0; j < smallList.size(); j++)
                            {
                                Map<String, Bitmap> imgMap = new HashMap<String, Bitmap>();
                                imgMap.put("small", smallList.get(j));
                                imgMap.put("big",
                                        photoLayout.getBigImage(imgList.get(j)));
                                bitmapList.add(imgMap);
                            }
                            friendGroupAdapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //	private BitmapListListener bitmapListListener = new BitmapListListener() {
    //
    //		@Override
    //		public void BitmapListListener(List<Map<String, Bitmap>> imgList) {
    //			bitmapList = imgList;
    //		}
    //	};

    public interface BitmapListListener
    {
        void BitmapListListener(List<Map<String, Bitmap>> imgList);
    }

    public class FriendGroupAdapter extends BaseAdapter
    {

        private AsyncImageLoader asyncImageLoader;

        @Override
        public int getCount()
        {
            return friendList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return friendList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent)
        {
            final ViewHolder holder;
            if (view == null)
            {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                view = inflater.inflate(R.layout.friend_group_item, null);
                holder.item_headname = (TextView) view.findViewById(R.id.item_headname);
                holder.item_content_text = (TextView) view.findViewById(R.id.item_content_text);
                holder.item_content_time = (TextView) view.findViewById(R.id.item_content_time);
                holder.item_headshot = (ImageView) view.findViewById(R.id.item_headshot);
                holder.item_content_img = (LinearLayout) view.findViewById(R.id.item_content_img);
                holder.item_comment_btn = (ImageButton) view.findViewById(R.id.item_comment_btn);
                holder.item_zan = (ImageButton) view.findViewById(R.id.item_zan);
                holder.item_comment_content = (ListView) view.findViewById(R.id.item_comment_content);
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }

            holder.item_headname.setText(friendList.get(pos)
                    .get("item_headname")
                    .toString());
            holder.item_content_text.setText(friendList.get(pos)
                    .get("item_content_text")
                    .toString());
            holder.item_content_time.setText(friendList.get(pos)
                    .get("item_content_time")
                    .toString());

            holder.item_headshot.setImageResource((Integer) friendList.get(pos)
                    .get("item_headshot"));

            holder.item_content_img.removeAllViews();
            for (final Map<String, Bitmap> map : bitmapList)
            {
                ImageView imageView = new ImageView(getApplicationContext());
                BitmapDrawable bd = new BitmapDrawable(map.get("small"));
                Drawable d = bd;
                imageView.setImageDrawable(d);
                imageView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.show_big_img,
                                null);
                        Dialog dialog = new Dialog(FriendGroup.this,
                                R.style.MyDialog);
                        dialog.setContentView(layout);
                        dialog.show();
                        ImageView show_big_img = (ImageView) layout.findViewById(R.id.show_big_img);
                        show_big_img.setImageBitmap(map.get("big"));
                    }
                });
                holder.item_content_img.addView(imageView, 0);
            }
            // /////////////////////// 动态加载图片///////////////////////////////
            // String head_key = friendList.get(pos).get("userId") + HEADSHOT;
            // String content_key = friendList.get(pos).get("userId") +
            // CONTENTSHOT;
            // holder.item_headshot.setTag(head_key);
            // Drawable cachedImage_headshot = asyncImageLoader.loadDrawable(
            // head_key, getApplicationContext(), new ImageCallback() {
            // @Override
            // public void imageLoaded(Drawable imageDrawable,
            // String key) {
            // ImageView imageViewByTag = (ImageView) friendGroup
            // .findViewWithTag(key);
            // if (imageViewByTag != null) {
            // imageViewByTag.setImageDrawable(imageDrawable);
            // }
            // }
            // });
            //
            // holder.item_content_img.setTag(content_key);
            // Drawable cachedImage_item_content_img = asyncImageLoader
            // .loadDrawable(content_key, getApplicationContext(), new
            // ImageCallback() {
            // public void imageLoaded(Drawable imageDrawable,
            // String key) {
            // ImageView imageViewByTag = (ImageView) friendGroup
            // .findViewWithTag(key);
            // if (imageViewByTag != null) {
            // imageViewByTag.setImageDrawable(imageDrawable);
            // }
            // }
            // });
            // if (cachedImage_headshot == null) {
            // holder.item_headshot.setImageResource(R.drawable.ic_launcher);
            // } else {
            // holder.item_headshot.setImageDrawable(cachedImage_headshot);
            // }
            // if (cachedImage_headshot == null) {
            // holder.item_content_img
            // .setImageResource(R.drawable.ic_launcher);
            // } else {
            // holder.item_content_img
            // .setImageDrawable(cachedImage_item_content_img);
            // }
            // /////////////////////// 动态加载图片///////////////////////////////

            // 评论
            holder.item_comment_btn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    soft_input_keyboard.setVisibility(View.VISIBLE);
                    holder.item_comment_content.setVisibility(View.VISIBLE);
                    final CommentAdapter commentAdapter = new CommentAdapter();
                    holder.item_comment_content.setAdapter(commentAdapter);
                    // 弹出键盘
                    InputMethodManager inputManager = (InputMethodManager) item_add_comment_content.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    item_add_comment_btn.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View arg0)
                        {
                            // 提交
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("comment_content",
                                    item_add_comment_content.getText()
                                            .toString()
                                            + " "
                                            + getCurrentDate());
                            commentList.add(map);
                            commentAdapter.notifyDataSetChanged();

                            soft_input_keyboard.setVisibility(View.GONE);
                            item_add_comment_content.setText("");
                        }
                    });
                }
            });

            // 赞
            holder.item_zan.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // TODO 检查数据库“赞”字段
                    Toast.makeText(getApplicationContext(),
                            "已赞",
                            Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        final class ViewHolder
        {
            TextView item_headname, item_content_text, item_content_time;

            ImageView item_headshot;

            LinearLayout item_content_img;

            ImageButton item_comment_btn, item_zan;

            ListView item_comment_content;
        }
    }

    public class CommentAdapter extends BaseAdapter
    {

        public CommentAdapter()
        {
        }

        @Override
        public int getCount()
        {
            return commentList.size();
        }

        @Override
        public Object getItem(int pos)
        {
            return commentList.get(pos);
        }

        @Override
        public long getItemId(int pos)
        {
            return pos;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup)
        {
            final ViewHolder holder;
            if (view == null)
            {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                view = inflater.inflate(R.layout.friend_comment, null);
                holder.comment_master = (TextView) view.findViewById(R.id.comment_master);
                holder.comment_server = (TextView) view.findViewById(R.id.comment_server);
                holder.comment_content = (TextView) view.findViewById(R.id.comment_content);
                holder.comment_reply = (TextView) view.findViewById(R.id.comment_reply);
                holder.comment_colon = (TextView) view.findViewById(R.id.comment_colon);
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }

            holder.comment_master.setText(commentList.get(pos)
                    .get("comment_master"));
            holder.comment_server.setText(commentList.get(pos)
                    .get("comment_server"));
            holder.comment_content.setText(commentList.get(pos)
                    .get("comment_content"));

            if (TextUtils.isEmpty(commentList.get(pos).get("comment_server")))
            {
                holder.comment_master.setText("");
                holder.comment_server.setText("");
                holder.comment_reply.setText("");
                holder.comment_colon.setText("");
            }

            return view;
        }

        final class ViewHolder
        {
            TextView comment_master;

            TextView comment_server;

            TextView comment_content;

            TextView comment_reply;

            TextView comment_colon;
        }

    }

    @Override
    public void OnRefresh()
    {
        mRefreshAsynTask = new RefreshDataAsynTask();
        mRefreshAsynTask.execute();
    }

    class RefreshDataAsynTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... arg0)
        {

            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // index++;
            // data.addFirst("genius" + index);

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            Toast toast = Toast.makeText(getApplicationContext(),
                    "刷新完毕",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 70);
            toast.show();
            friendGroupAdapter.notifyDataSetChanged();
            friendGroup.onRefreshComplete();
        }

    }

    private String getCurrentDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");
        return sdf.format(new Date());
    }

    public BitmapListener bitmapListener = new BitmapListener()
    {
        @Override
        public void onBitmapListener(Bitmap smallMap, Bitmap bigMap)
        {
            Map<String, Bitmap> map = new HashMap<String, Bitmap>();
            map.put("small", smallMap);
            map.put("big", bigMap);
            bitmapList.add(map);
        }
    };

    public interface BitmapListener
    {
        void onBitmapListener(Bitmap smallMap, Bitmap bigMap);
    }

    private OnClickListener item_add_click = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.pengyouquan_add:
                    Intent intent = new Intent(FriendGroup.this,
                            FriendAddPhoto.class);
                    startActivityForResult(intent, RESUlT_LIST);
                    //				startActivity(intent);
                    //				finish();
                    break;
                case R.id.pengyouquan_part:
                    pengyouquan_view.setVisibility(View.VISIBLE);
                    maillist_view.setVisibility(View.GONE);
                    initFriendGroupView();
                    break;
                case R.id.maillist_part:
                    pengyouquan_view.setVisibility(View.GONE);
                    maillist_view.setVisibility(View.VISIBLE);
                    initMailListView();
                    break;
            }
        }
    };

    ////////////////////////////////////////////通讯录///////////////////////////////////////////////////////

    private ListView mMailListView;

    private MailListAdapter mailListAdapter;

    private ImageButton title_back;

    private TextView title_all, title_family, title_classmate;

    private Button title_add;

    private ListView listView_friend_head_letter;

    private PinnedSectionListView list_view_friend;// 城市信息的listview

    private MyPinnedSectionListAdapter pinnedSectionListAdapter;// 城市信息的listview对应的adapter

    private HashMap<String, Integer> letterTitleMap = new HashMap<String, Integer>();

    private String[] memberList;

    private Handler handler_list_view_title = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    // TLog.Log("zxl--->", "handler");
                    //				adjustListTitle();// 滑动过快，调整悬浮标题栏的字母

                    break;
                default:
                    break;
            }
        }
    };

    private void initMailListView()
    {
        //		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
        //				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //		View view = inflater
        //				.inflate(R.layout.maillist, null);
        if (mMailListView == null)
        {
            mMailListView = (ListView) findViewById(R.id.maillist);
            mailListAdapter = new MailListAdapter();
            mMailListView.setAdapter(mailListAdapter);
        }
        else
        {
            mailListAdapter.notifyDataSetChanged();
        }

        final ActionBar actionBar = this.getActionBar();
        actionBar.setCustomView(R.layout.maillist_title);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.show();
        title_all = (TextView) findViewById(R.id.title_all);
        title_family = (TextView) findViewById(R.id.title_family);
        title_classmate = (TextView) findViewById(R.id.title_classmate);
        title_all.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                title_all.setBackgroundColor(Color.BLUE);
                title_family.setBackgroundColor(0);
                title_classmate.setBackgroundColor(0);
            }
        });
        title_family.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                title_family.setBackgroundColor(Color.BLUE);
                title_all.setBackgroundColor(0);
                title_classmate.setBackgroundColor(0);
            }
        });
        title_classmate.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                title_classmate.setBackgroundColor(Color.BLUE);
                title_family.setBackgroundColor(0);
                title_all.setBackgroundColor(0);
            }
        });

        title_back = (ImageButton) findViewById(R.id.title_back);
        title_add = (Button) findViewById(R.id.title_add);
        title_back.setOnClickListener(item_add_click);
        title_add.setOnClickListener(item_add_click);
        title_back.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                pengyouquan_view.setVisibility(View.VISIBLE);
                maillist_view.setVisibility(View.GONE);
                actionBar.hide();
            }
        });
        title_add.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(FriendGroup.this,
                        MailListAddActivity.class);
                startActivity(intent);
            }
        });
        //		list_view_friend = (PinnedSectionListView)findViewById(R.id.list_view_friend);
        //		if (null == pinnedSectionListAdapter) {
        //			pinnedSectionListAdapter = new MyPinnedSectionListAdapter();
        //			list_view_friend.setAdapter(pinnedSectionListAdapter);
        //		}
        //		pinnedSectionListAdapter
        //				.setListCityHeadLetterAndName(showFriendListItemBeans);
        //		pinnedSectionListAdapter.notifyDataSetChanged();
        //		listView_friend_head_letter = (ListView) findViewById(R.id.listView_friend_head_letter);
        //		listView_friend_head_letter.setOnTouchListener(asOnTouch);
        //		
        //		list_view_friend.setOnScrollListener(new OnScrollListener() {
        //			@Override
        //			public void onScrollStateChanged(AbsListView view, int scrollState) {
        //				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        //					fixListTitlePosition();// 显示城市信息的listview滑动停止后，调整浮动显示的标题
        //				}
        //			}
        //
        //			@Override
        //			public void onScroll(AbsListView view, int firstVisibleItem,
        //					int visibleItemCount, int totalItemCount) {
        //			}
        //		});
    }

    /**
     * 根据右侧点击的导航字母，显示对应的城市信息
     */
    private OnTouchListener asOnTouch = new OnTouchListener()
    {

        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:// 0
                    mathScrollerPosition(event.getY());
                    break;
                case MotionEvent.ACTION_UP:// 抬起时调整悬浮标题栏的字母
                    fixListTitlePosition();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mathScrollerPosition(event.getY());
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 调整浮动显示的标题
     */
    private void fixListTitlePosition()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                handler_list_view_title.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 显示字符
     * 
     * @param y
     */
    private void mathScrollerPosition(float y)
    {
        int height = listView_friend_head_letter.getHeight();
        float charHeight = (ScreenUtils.getScreenHeight(getApplicationContext()) - ScreenUtils.dip2px(getApplicationContext(),
                48) * 2) / 26.0f;
        char c = 'A';
        if (y < 0)
            y = 0;
        else if (y > height)
            y = height;

        int index = (int) (Math.ceil(y / charHeight)) - 1;
        if (index < 0)
            index = 0;
        else if (index > 25)
            index = 25;

        String key = String.valueOf((char) (c + index));
        setPinnedSectionListSelection(key);
        //		adjustListTitle();
    }

    /**
     * 根据右侧点击的导航字母，显示对应的城市信息
     * 
     * @param key
     *            点击的字母
     */
    private void setPinnedSectionListSelection(String key)
    {
        if (letterTitleMap.containsKey(key))
        {
            int position = letterTitleMap.get(key);
            list_view_friend.setSelection(position);
        }
    }

    public class MailListAdapter extends BaseAdapter
    {

        private AsyncImageLoader asyncImageLoader;

        @Override
        public int getCount()
        {
            return memberList.length;
        }

        @Override
        public Object getItem(int pos)
        {
            return memberList[pos];
        }

        @Override
        public long getItemId(int pos)
        {
            return pos;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup)
        {
            final ViewHolder holder;
            if (view == null)
            {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                view = inflater.inflate(R.layout.maillist_item, null);
                holder.member_image = (ImageView) view.findViewById(R.id.member_image);
                holder.member_name = (TextView) view.findViewById(R.id.member_name);
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }

            //			// 动态加载图片
            //			String key = "";
            //			holder.member_image
            //					.setTag(key);
            //			Drawable cachedImage = asyncImageLoader.loadDrawable(key,
            //					getApplicationContext(), new ImageCallback() {
            //						public void imageLoaded(Drawable imageDrawable,
            //								String key) {
            //							ImageView imageViewByTag = (ImageView) mMailListView
            //									.findViewWithTag(key);
            //							if (imageViewByTag != null) {
            //								imageViewByTag
            //										.setImageDrawable(imageDrawable);
            //							}
            //						}
            //					});
            //			if (cachedImage == null) {
            //				holder.member_image
            //						.setImageResource(R.drawable.ic_launcher);
            //			} else {
            //				holder.member_image
            //						.setImageDrawable(cachedImage);
            //			}

            holder.member_name.setText(memberList[pos]);

            return view;
        }

        final class ViewHolder
        {
            ImageView member_image;

            TextView member_name;
        }

    }

    private class MyPinnedSectionListAdapter implements
            PinnedSectionListAdapter
    {

        @Override
        public boolean areAllItemsEnabled()
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isEnabled(int arg0)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Object getItem(int arg0)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getItemViewType(int arg0)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getViewTypeCount()
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean hasStableIds()
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isEmpty()
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void notifyDataSetChanged()
        {
            // TODO Auto-generated method stub

        }

    }

    /* (non-Javadoc)
    * @see android.app.Activity#onResume()
    */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }

}
