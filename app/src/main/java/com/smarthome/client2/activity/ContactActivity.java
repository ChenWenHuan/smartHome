package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.umeng.analytics.MobclickAgent;

public class ContactActivity extends Activity
{
    private final static int WHAT = 1;

    private MailListAdapter mailListAdapter;

    private FrameLayout fl_header_contact;

    private CustomActionBar actionBar;

    private int total = 0;

    private ListView contactListview;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == WHAT)
            {
                contactListview = (ListView) findViewById(R.id.contact_listview);
                mailListAdapter = new MailListAdapter();
                contactListview.setAdapter(mailListAdapter);
                contactListview.setOnItemClickListener(mItemClickListener);
            }
        }
    };

    private Thread mThread = new Thread(new Runnable()
    {
        @Override
        public void run()
        {
            getPhoneContacts();
            getSIMContacts();
            mHandler.sendEmptyMessage(WHAT);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact);
        addTopBarToHead();

        total = getIntent().getExtras().getInt("total");

        mSelectHashMap.clear();
        mThread.start();
        //        getPhoneContacts();
        //        getSIMContacts();

        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    private void saveContact()
    {
        StringBuffer con = new StringBuffer();
        Iterator<Integer> iterator = mSelectHashMap.keySet().iterator();
        while (iterator.hasNext())
        {
            con.append(mSelectHashMap.get(iterator.next())).append(",");
        }
        Preferences.getInstance(getApplicationContext())
                .setContact(con.substring(0, con.length() - 1).toString());
    }

    private boolean mClickflag = false;

    private void addTopBarToHead()
    {
        fl_header_contact = (FrameLayout) findViewById(R.id.fl_header_contact);
        actionBar = TopBarUtils.createCustomActionBar(this,
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                getString(R.string.title_add_white),
                getString(R.string.common_btn_yes),
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mClickflag)
                        {
                            return;
                        }
                        mClickflag = true;
                        if (mSelectHashMap.size() + total > 5)
                        {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.white_5_white),
                                    Toast.LENGTH_SHORT).show();
                            mClickflag = false;
                            return;
                        }
                        if (mSelectHashMap.size() == 0)
                        {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.white_no_contract),
                                    Toast.LENGTH_SHORT).show();
                            mClickflag = false;
                            return;
                        }
                        Iterator<Integer> iterator = mSelectHashMap.keySet()
                                .iterator();
                        while (iterator.hasNext())
                        {
                            String cc = mSelectHashMap.get(iterator.next());
                            if (cc.split("-")[1].length() < 3
                                    || cc.split("-")[1].length() > 11)
                            {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.white_invalid_phone),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                mClickflag = false;
                                return;
                            }
                        }

                        mClickflag = false;
                        saveContact();
                        sendBroadcast(new Intent(
                                WhiteSpaceActivity.GET_CONTACT_NUMBER));
                        finish();
                    }
                });
        fl_header_contact.addView(actionBar);
    }

    private HashMap<Integer, String> mSelectHashMap = new HashMap<Integer, String>();

    // ////////////////////////获取通讯录信息/////////////////////////////////////
    private ArrayList<String> mContacts = new ArrayList<String>();

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
                if (TextUtils.isEmpty(phoneNumber)
                        && mContacts.indexOf(phoneNumber) >= 0)
                {
                    continue;
                }

                String contactName = phoneCursor.getString(Constants.PHONES_DISPLAY_NAME_INDEX);
                String contact = contactName + "-" + phoneNumber;
                mContacts.add(contact);
                Log.d("", "daitm----contact---" + contact);
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
            // Toast.makeText(getApplicationContext(),
            // "请确认sim卡是否插入或者sim卡暂时不可用！",
            // Toast.LENGTH_SHORT).show();
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
                    if (TextUtils.isEmpty(phoneNumber)
                            && mContacts.indexOf(phoneNumber) >= 0)
                    {
                        continue;
                    }

                    String contactName = phoneCursor.getString(Constants.PHONES_DISPLAY_NAME_INDEX);
                    String contact = contactName + "-" + phoneNumber;
                    mContacts.add(contact);
                }
                phoneCursor.close();
            }
        }
    }

    // ////////////////////////获取通讯录信息/////////////////////////////////////

    class MailListAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return mContacts.size();
        }

        @Override
        public Object getItem(int pos)
        {
            return mContacts.get(pos);
        }

        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup viewgroup)
        {

            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.mail_list_relative_phone_item, null);
            final Getter getter;
            if (view != null)
            {
                getter = new Getter();
                getter.mail_list_item_relative_name = (TextView) view.findViewById(R.id.mail_list_item_relative_name);
                getter.mail_list_item_relative_number = (TextView) view.findViewById(R.id.mail_list_item_relative_number);
                getter.mail_list_item_relative_img = (ImageView) view.findViewById(R.id.mail_list_item_relative_img);
                view.setTag(getter);
            }
            else
            {
                getter = (Getter) view.getTag();
            }
            if (mSelectHashMap.containsKey(pos))
            {
                getter.mail_list_item_relative_img.setSelected(true);
            }
            getter.mail_list_item_relative_name.setText(mContacts.get(pos)
                    .split("-")[0]);
            getter.mail_list_item_relative_number.setText(mContacts.get(pos)
                    .split("-")[1]);
            return view;
        }

        public class Getter
        {
            private TextView mail_list_item_relative_name;

            private TextView mail_list_item_relative_number;

            private ImageView mail_list_item_relative_img;
        }
    }

    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onPause();
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

    private OnItemClickListener mItemClickListener = new OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id)
        {
            ImageView relativeImage = (ImageView) view.findViewById(R.id.mail_list_item_relative_img);
            if (!relativeImage.isSelected())
            {
                String cc = mContacts.get(position);
                if (cc.split("-")[1].startsWith("+86"))
                {
                    cc = cc.split("-")[0]
                            + "-"
                            + cc.split("-")[1].substring(3,
                                    cc.split("-")[1].length());
                }
                mSelectHashMap.put(position, cc.replace(" ", ""));
                relativeImage.setSelected(true);
            }
            else
            {
                mSelectHashMap.remove(position);
                relativeImage.setSelected(false);
            }
        }
    };

}
