package com.smarthome.client2.friendgroup;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.AsyncImageLoader;
import com.umeng.analytics.MobclickAgent;

public class MailListAddActivity extends Activity implements OnClickListener
{

    private EditText maillist_add_by_search;

    private Button maillist_add_by_maillist, maillist_add_by_search_btn;

    private ImageButton title_add_back;

    private LinearLayout maillist_other;

    private ListView maillist_add_by_search_list;

    private List<String> searchList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maillist_add);

        ActionBar actionBar = this.getActionBar();
        actionBar.setCustomView(R.layout.maillist_add_title);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.show();

        maillist_add_by_search = (EditText) findViewById(R.id.maillist_add_by_search);
        maillist_add_by_maillist = (Button) findViewById(R.id.maillist_add_by_maillist);
        title_add_back = (ImageButton) findViewById(R.id.title_add_back);

        maillist_add_by_search_btn = (Button) findViewById(R.id.maillist_add_by_search_btn);
        maillist_other = (LinearLayout) findViewById(R.id.maillist_other);
        maillist_add_by_search_list = (ListView) findViewById(R.id.maillist_add_by_search_list);

        maillist_add_by_maillist.setOnClickListener(this);
        title_add_back.setOnClickListener(this);
        maillist_add_by_search_btn.setOnClickListener(this);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.maillist_add_by_maillist:
                getPhoneContacts();
                getSIMContacts();
                getFriendsFromServer();
                break;
            case R.id.title_add_back:
                finish();
                break;
            case R.id.maillist_add_by_search_btn:
                maillist_add_by_search_list.setVisibility(View.VISIBLE);
                maillist_other.setVisibility(View.GONE);
                getFriendsFromServer();
                break;
            default:
                break;
        }
    }

    public class MailListAddAdapter extends BaseAdapter
    {

        private AsyncImageLoader asyncImageLoader;

        @Override
        public int getCount()
        {
            return searchList.size();
        }

        @Override
        public Object getItem(int pos)
        {
            return searchList.get(pos);
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
                view = inflater.inflate(R.layout.maillist_search_list, null);
                holder.search_list_image = (ImageView) view.findViewById(R.id.search_list_image);
                holder.search_list_name = (TextView) view.findViewById(R.id.search_list_name);
                holder.search_list_sex = (TextView) view.findViewById(R.id.search_list_sex);
                holder.search_list_signiture = (TextView) view.findViewById(R.id.search_list_signiture);
                holder.search_list_distance = (TextView) view.findViewById(R.id.search_list_distance);
                holder.search_list_lasttime = (TextView) view.findViewById(R.id.search_list_lasttime);
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
            //				holder.search_list_image
            //						.setImageResource(R.drawable.ic_launcher);
            //			} else {
            //				holder.search_list_image
            //						.setImageDrawable(cachedImage);
            //			}

            holder.search_list_name.setText(searchList.get(pos));
            holder.search_list_sex.setText(searchList.get(pos));
            holder.search_list_signiture.setText(searchList.get(pos));
            holder.search_list_distance.setText(searchList.get(pos));
            holder.search_list_lasttime.setText(searchList.get(pos));

            return view;
        }

        final class ViewHolder
        {
            ImageView search_list_image;

            TextView search_list_name, search_list_sex, search_list_signiture,
                    search_list_distance, search_list_lasttime;
        }

    }

    //////////////////////////获取通讯录信息/////////////////////////////////////
    private ArrayList<String> mContactsNumber = new ArrayList<String>();

    private void getPhoneContacts()
    {
        ContentResolver resolver = getApplicationContext().getContentResolver();

        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
                Constants.PHONES_PROJECTION,
                null,
                null,
                null);

        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {

                String phoneNumber = phoneCursor.getString(Constants.PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                {
                    continue;
                }

                mContactsNumber.add(phoneNumber);
            }
            phoneCursor.close();
        }
    }

    private void getSIMContacts()
    {
        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        int absent = manager.getSimState();
        if (1 == absent)
        {
            Toast.makeText(getApplicationContext(),
                    "请确认sim卡是否插入或者sim卡暂时不可用！",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            Uri uri = Uri.parse("content://icc/adn");
            Cursor phoneCursor = resolver.query(uri,
                    Constants.PHONES_PROJECTION,
                    null,
                    null,
                    null);

            if (phoneCursor != null)
            {
                while (phoneCursor.moveToNext())
                {

                    String phoneNumber = phoneCursor.getString(Constants.PHONES_NUMBER_INDEX);
                    if (TextUtils.isEmpty(phoneNumber))
                    {
                        continue;
                    }

                    mContactsNumber.add(phoneNumber);
                }
                phoneCursor.close();
            }
        }
    }

    //////////////////////////获取通讯录信息/////////////////////////////////////

    private void getFriendsFromServer()
    {

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
