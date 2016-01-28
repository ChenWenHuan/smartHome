package com.smarthome.client2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.GloableData;
import com.smarthome.client2.familySchool.view.AbListViewFooter;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.BitmapUtil;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.MySoftInputUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class SearchFamilyMember extends Activity
{
    private FrameLayout fl_family_member_head;

    private CustomActionBar actionBar;

    private int pageNum = 1;

    private Boolean pageOver = false;

    private int pageSize = 15;

    private ProgressDialog progressDlg;

    private PopupWindow mPopupWindowDialog;

    private AbPullListView listView;

    private static FamilyMemberAdapter adapter;

    private static List<FamilyMemberCusor> mAdapterList = new ArrayList<FamilyMemberCusor>();

    private Button btn_submit; //当前点击的是哪一项

    public static List<Bitmap> mHeadBitmap = new ArrayList<Bitmap>(); //保存头像的list

    private Boolean mbAction = false; //true向上拉，false向下拉

    private View viewPopup;

    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ac_search_family_member);
        addTopBarToHead();

        initView();
        AppManager.getAppManager().addActivity(this);
        //适配每页显示的查询结果条数
        int px = (int) (getResources().getDimension(R.dimen.item_height) + 0.5f);
        pageSize = GloableData.getScreenH(SearchFamilyMember.this) / px;
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    //初始化界面元素
    private void initView()
    {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        viewPopup = inflater.inflate(R.layout.popup_add_family_member, null);
        mPopupWindowDialog = new PopupWindow(viewPopup,
                GloableData.getScreenW(SearchFamilyMember.this) / 3 * 2,
                LayoutParams.WRAP_CONTENT);
        mPopupWindowDialog.setFocusable(true);
        mPopupWindowDialog.update();
        mPopupWindowDialog.setBackgroundDrawable(new BitmapDrawable(
                getResources(), (Bitmap) null));
        mPopupWindowDialog.setOutsideTouchable(true);

        btn_submit = (Button) viewPopup.findViewById(R.id.submit_add_member);

        Button btn_cancel = (Button) viewPopup.findViewById(R.id.cancel_add_member);
        btn_cancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                mPopupWindowDialog.dismiss();
            }
        });

        mAdapterList.clear();
        adapter = new FamilyMemberAdapter(SearchFamilyMember.this, mAdapterList);

        progressDlg = new ProgressDialog(SearchFamilyMember.this);
        progressDlg.setCanceledOnTouchOutside(false);

        listView = (AbPullListView) findViewById(R.id.member_list);
        listView.setAdapter(adapter);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        listView.getFooterView()
                .setFooterProgressBarDrawable(this.getResources()
                        .getDrawable(R.drawable.fs_pull_progress));

        listView.setAbOnListViewListener(new AbOnListViewListener()
        {
            @Override
            public void onRefresh()
            {
                mbAction = true;
            }

            @Override
            public void onLoadMore()
            {
                mbAction = false;
                String tmpSearch = actionBar.getSearchContent().trim();
                if (searchText.isEmpty() && !tmpSearch.isEmpty())
                {
                    searchText = tmpSearch;
                }
                else if (searchText.isEmpty() && tmpSearch.isEmpty())
                {
                    Toast.makeText(SearchFamilyMember.this,
                            R.string.search_key_empty,
                            Toast.LENGTH_SHORT).show();
                    listView.stopLoadMore();
                    return;
                }
                if (!pageOver)
                {
                    getSearchResult();
                }
                else
                {
                    listView.stopLoadMore();
                    if (mAdapterList.isEmpty())
                    {
                        listView.getFooterView()
                                .setState(AbListViewFooter.STATE_EMPTY);
                    }
                    else
                    {
                        listView.getFooterView()
                                .setState(AbListViewFooter.STATE_ALL);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int arg2,
                    final long arg3)
            {
                ImageView ivHeadPic = (ImageView) viewPopup.findViewById(R.id.head_pic);
                if (mHeadBitmap.size() > 0 && mHeadBitmap.get((int) arg3) != null)
                {
                    ivHeadPic.setBackgroundDrawable(new BitmapDrawable(
                            mHeadBitmap.get((int) arg3)));
                }
                else
                {
                    ivHeadPic.setBackgroundResource(R.drawable.default_pictures);
                }
                TextView tvName = (TextView) viewPopup.findViewById(R.id.name);

                if (arg2 < parent.getAdapter().getCount())
                {
                    tvName.setText(mAdapterList.get((int) arg3).getName());
                    TextView tvTelNum = (TextView) viewPopup.findViewById(R.id.telnum);
                    tvTelNum.setText(mAdapterList.get((int) arg3).getTelNum());
                    mPopupWindowDialog.showAtLocation(listView,
                            Gravity.CENTER,
                            0,
                            0);
                }

                btn_submit.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View arg0)
                    {
                        try
                        {
                            if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
                            {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.no_network),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            add_family_member_handler.sendEmptyMessage(Constants.GET_DATA_START);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", mAdapterList.get((int) arg3)
                                    .getId());
                            HttpUtil.postRequest(obj,
                                    Constants.ADD_FAMILY_MEMBER,
                                    add_family_member_handler,
                                    Constants.GET_DATA_SUCCESS,
                                    Constants.GET_DATA_FAIL);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }

    private Handler add_family_member_handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    progressDlg.setMessage(getString(R.string.search_add_member_wait));
                    progressDlg.show();
                    mPopupWindowDialog.dismiss();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    progressDlg.dismiss();
                    mPopupWindowDialog.dismiss();

                    String strMsgObj = msg.obj.toString();
                    try
                    {
                        JSONObject js = new JSONObject(strMsgObj);
                        String strRet = js.getString("data");
                        if (strRet.equals("1"))
                        {
                            Toast.makeText(SearchFamilyMember.this,
                                    R.string.search_add_member_exist,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (strRet.equals("2"))
                        {
                            Toast.makeText(SearchFamilyMember.this,
                                    R.string.search_add_member_ok,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SearchFamilyMember.this,
                                    R.string.search_add_member_request,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                case Constants.GET_DATA_FAIL:
                    progressDlg.dismiss();
                    Toast.makeText(SearchFamilyMember.this,
                            R.string.search_add_member_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                case 201:
                    progressDlg.dismiss();
                    break;
                default:
                    progressDlg.dismiss();
                    Toast.makeText(SearchFamilyMember.this,
                            R.string.unknown_error,
                            Toast.LENGTH_SHORT).show();
            }
        }

    };

    // 构建Runnable对象，在runnable中更新界面  
    private Runnable runnableUi = new Runnable()
    {
        @Override
        public void run()
        {
            adapter.notifyDataSetChanged();
        }

    };

    //处理查询家庭成员结果数据
    private Handler mTmpHandler = new Handler();

    private Handler get_family_member_handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                    listView.getFooterView().setVisibility(View.VISIBLE);
                    progressDlg.setMessage(getString(R.string.search_family_member_wait));
                    progressDlg.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    progressDlg.dismiss();
                    if (mbAction)
                    {
                        listView.stopRefresh();
                    }
                    else
                    {
                        listView.stopLoadMore();
                    }
                    progressDlg.dismiss();
                    try
                    {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        JSONArray memberArray = json.getJSONArray("data");
                        for (int i = 0; i < memberArray.length(); i++)
                        {
                            JSONObject item = memberArray.getJSONObject(i);
                            FamilyMemberCusor cusor = new FamilyMemberCusor();
                            cusor.setId(item.getString("id"));
                            cusor.setName(item.getString("userrealname"));
                            cusor.setTelNum(item.getString("telnum"));
                            cusor.setHeadPic(item.getString("headpicpath")
                                    + item.getString("headpicname"));
                            mAdapterList.add(cusor);
                        }

                        listView.setPullLoadEnable(true);
                        if (mAdapterList.size() == 0)
                        {
                            listView.getFooterView()
                                    .setState(AbListViewFooter.STATE_EMPTY);
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        else
                        {
                            listView.getFooterView()
                                    .setState(AbListViewFooter.STATE_PULLING);
                        }

                        if (memberArray.length() == pageSize)
                        {
                            pageNum++;
                        }
                        else
                        {
                            pageOver = true;
                            listView.getFooterView()
                                    .setState(AbListViewFooter.STATE_ALL);
                        }

                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mHeadBitmap.clear();
                                int picCount = mAdapterList.size();
                                for (int i = 0; i < picCount; i++)
                                {
                                    try
                                    {
                                        if (i < mAdapterList.size())
                                        {
                                            mHeadBitmap.add(BitmapUtil.getBitmap(mAdapterList.get(i)
                                                    .getHeadPic()));
                                        }
                                    }
                                    catch (IOException e)
                                    {
                                        mHeadBitmap.add(null);
                                        e.printStackTrace();
                                    }
                                }

                                //如果图片都已经能够获取完了，发送刷新界面的请求
                                mTmpHandler.post(runnableUi);
                            }
                        }).start();

                        //adapter = new FamilyMemberAdapter(SearchFamilyMember.this, mAdapterList);

                        adapter.setFamilyMemberCusor(mAdapterList);
                        adapter.notifyDataSetChanged();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                case 201:
                    progressDlg.dismiss();
                    if (mbAction)
                    {
                        listView.stopRefresh();
                    }
                    else
                    {
                        listView.stopLoadMore();
                    }
                    progressDlg.dismiss();
                    //                    Toast.makeText(SearchFamilyMember.this, R.string.server_off,
                    //                        Toast.LENGTH_SHORT).show();
                case Constants.GET_DATA_FAIL:
                    progressDlg.dismiss();
                    if (mbAction)
                    {
                        listView.stopRefresh();
                    }
                    else
                    {
                        listView.stopLoadMore();
                    }
                    progressDlg.dismiss();
                    Toast.makeText(SearchFamilyMember.this,
                            R.string.search_family_member_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    progressDlg.dismiss();
                    if (mbAction)
                    {
                        listView.stopRefresh();
                    }
                    else
                    {
                        listView.stopLoadMore();
                    }
                    progressDlg.dismiss();
                    Toast.makeText(SearchFamilyMember.this,
                            R.string.unknown_error,
                            Toast.LENGTH_SHORT).show();
            }
        }

    };

    //查询家庭成员的请求
    private void getSearchResult()
    {
        if (!HttpUtil.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
            mAdapterList.clear();
            adapter.setFamilyMemberCusor(mAdapterList);
            listView.getFooterView().setState(AbListViewFooter.STATE_READY);
            adapter.notifyDataSetChanged();
            if (listView.isLoading())
            {
                listView.stopLoadMore();
            }
            return;
        }

        try
        {
            JSONObject json = new JSONObject();
            json.put("keyword", searchText); //关键字
            json.put("page", pageNum); //页码
            json.put("size", pageSize); //每页显示的条数，根据屏幕适配
            get_family_member_handler.sendEmptyMessage(Constants.GET_DATA_START);
            HttpUtil.postRequest(json,
                    Constants.QUERY_FAMILY_MEMBER,
                    get_family_member_handler,
                    Constants.GET_DATA_SUCCESS,
                    Constants.GET_DATA_FAIL);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //添加actonbar
    private void addTopBarToHead()
    {
        fl_family_member_head = (FrameLayout) findViewById(R.id.fl_header_home);
        if (actionBar != null)
        {
            fl_family_member_head.removeView(actionBar);
        }

        actionBar = TopBarUtils.createCustomSearchActionBar(SearchFamilyMember.this,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        MySoftInputUtil.hideInputMethod(SearchFamilyMember.this,
                                getCurrentFocus());
                        finish();
                    }
                },
                R.drawable.search,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        searchText = actionBar.getSearchContent().trim();
                        if (searchText.isEmpty())
                        {
                            Toast.makeText(SearchFamilyMember.this,
                                    R.string.search_key_empty,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        pageOver = false;
                        pageNum = 1;
                        mAdapterList.clear();
                        mHeadBitmap.clear();
                        MySoftInputUtil.hideInputMethod(getApplicationContext(),
                                actionBar.editTextMiddle);
                        getSearchResult();
                    }
                });
        fl_family_member_head.addView(actionBar);
    }

    //查询结果列表的适配器
    class FamilyMemberAdapter extends BaseAdapter
    {
        /**
         * 上下文对象
         */
        private final Context mContext;

        /**
         * 当前要显示的列表
         */
        private List<FamilyMemberCusor> items;

        public FamilyMemberAdapter(Context context,
                List<FamilyMemberCusor> items)
        {
            this.mContext = context;
            this.items = items;
        }

        public void setFamilyMemberCusor(List<FamilyMemberCusor> items)
        {
            this.items = items;
        }

        @Override
        public int getCount()
        {
            return items.size();
        }

        @Override
        public Object getItem(int position)
        {
            return items.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup parent)
        {
            final Getter getter;
            if (view == null)
            {
                view = LayoutInflater.from(SearchFamilyMember.this)
                        .inflate(R.layout.home_fragment_item, null);

                getter = new Getter();
                getter.member_type = (TextView) view.findViewById(R.id.item_type);
                getter.home_family_headshot = (ImageView) view.findViewById(R.id.home_family_headshot);
                getter.home_family_name = (TextView) view.findViewById(R.id.home_family_name);
                getter.home_family_item_layout = (LinearLayout) view.findViewById(R.id.home_family_item_layout);
                getter.member_type_layout = (RelativeLayout) view.findViewById(R.id.item_head_title);
                view.setTag(getter);
            }
            else
                getter = (Getter) view.getTag();

            getter.member_type.setVisibility(View.GONE);
            if (pos < mHeadBitmap.size())
            {
                if (mHeadBitmap.get(pos) == null)
                {
                    getter.home_family_headshot.setBackgroundResource(R.drawable.default_pictures);
                }
                else
                {
                    getter.home_family_headshot.setBackgroundDrawable(new BitmapDrawable(
                            mHeadBitmap.get(pos)));
                }
            }
            else
            {
                getter.home_family_headshot.setBackgroundResource(R.drawable.default_pictures);
            }
            String name = items.get(pos).getName();
            getter.home_family_name.setText(name);
            getter.member_type_layout.setVisibility(View.GONE);
            return view;
        }

        class Getter
        {
            TextView member_type;

            ImageView home_family_headshot;

            TextView home_family_name;

            LinearLayout home_family_item_layout;

            RelativeLayout member_type_layout;
        }

    }

    //查询结果数据类
    class FamilyMemberCusor
    {
        private String id = ""; //ID

        private String memberName = ""; //姓名

        private String memberTel = ""; //电话号码

        private String memberHeadPic = ""; //用户头像地址

        public void setId(String strId)
        {
            this.id = strId;
        }

        public String getId()
        {
            return this.id;
        }

        public void setName(String strName)
        {
            this.memberName = strName;
        }

        public String getName()
        {
            return this.memberName;
        }

        public void setTelNum(String strTelNum)
        {
            this.memberTel = strTelNum;
        }

        public String getTelNum()
        {
            return this.memberTel;
        }

        public void setHeadPic(String strHeadPic)
        {
            this.memberHeadPic = strHeadPic;
        }

        public String getHeadPic()
        {
            return this.memberHeadPic;
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
