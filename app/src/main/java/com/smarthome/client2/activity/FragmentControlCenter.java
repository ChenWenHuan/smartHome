package com.smarthome.client2.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.smarthome.client2.R;
import com.smarthome.client2.fragment.ContactsFragment;
import com.smarthome.client2.fragment.HomeFragement_V11;
import com.smarthome.client2.fragment.LocationFragment;
import com.smarthome.client2.fragment.MessageFragment_sm;
import com.smarthome.client2.fragment.MyInfoFragment;
import com.smarthome.client2.fragment.UserMessageFragment;

public class FragmentControlCenter
{

    private static FragmentControlCenter instance;

    private static Context mContext;

    private Map<String, FragmentModel> mFragmentModelMaps = new HashMap<String, FragmentModel>();

    private FragmentControlCenter(Context context)
    {
        mContext = context;
    }

    public static synchronized FragmentControlCenter getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new FragmentControlCenter(context);
        }
        return instance;
    }

    public FragmentModel getLocationFragmentModel()
    {
        FragmentModel fragmentModel = mFragmentModelMaps.get(FragmentBuilder.LOCATION_FRAGMENT);
        if (fragmentModel == null)
        {
            fragmentModel = FragmentBuilder.getLocationFragmentModel();
            mFragmentModelMaps.put(FragmentBuilder.LOCATION_FRAGMENT,
                    fragmentModel);
        }
        return fragmentModel;
    }





    public FragmentModel getMessageFragmentModel()
    {
        FragmentModel fragmentModel = mFragmentModelMaps.get(FragmentBuilder.MESSAGE_FRAGMENT);
        if (fragmentModel == null)
        {
            fragmentModel = FragmentBuilder.getMessageFragmentModel();
            mFragmentModelMaps.put(FragmentBuilder.MESSAGE_FRAGMENT,
                    fragmentModel);
        }
        return fragmentModel;
    }


    public FragmentModel getSMHomeFragmentModel()
    {
        FragmentModel fragmentModel = mFragmentModelMaps.get(FragmentBuilder.HOME_FRAGMENT_SM);
        if (fragmentModel == null)
        {
            fragmentModel = FragmentBuilder.getSMHomeFragmentModel();
            mFragmentModelMaps.put(FragmentBuilder.HOME_FRAGMENT_SM, fragmentModel);
        }
        return fragmentModel;
    }

    public FragmentModel getSMContactsFragmentModel()
    {
        FragmentModel fragmentModel = mFragmentModelMaps.get(FragmentBuilder.CONTACTS_FREGMENT_SM);
        if (fragmentModel == null)
        {
            fragmentModel = FragmentBuilder.getContactsFragmentModel();
            mFragmentModelMaps.put(FragmentBuilder.CONTACTS_FREGMENT_SM,
                    fragmentModel);
        }
        return fragmentModel;
    }

    public FragmentModel getMyInfoFragmentModel()
    {
        FragmentModel fragmentModel = mFragmentModelMaps.get(FragmentBuilder.MYINFO_FREGMENT_V20);
        if (fragmentModel == null)
        {
            fragmentModel = FragmentBuilder.getMyInfoFragmentModel();
            mFragmentModelMaps.put(FragmentBuilder.MYINFO_FREGMENT_V20,
                    fragmentModel);
        }
        return fragmentModel;
    }

    public FragmentModel getSMMessagesFragmentModel()
    {
        FragmentModel fragmentModel = mFragmentModelMaps.get(FragmentBuilder.MESSAGES_FREGMENT_SM);
        if (fragmentModel == null)
        {
            fragmentModel = FragmentBuilder.getSMMessagesFragmentModel();
            mFragmentModelMaps.put(FragmentBuilder.MESSAGES_FREGMENT_SM,
                    fragmentModel);
        }
        return fragmentModel;
    }


    public FragmentModel getFragmentModel(String name)
    {
        return mFragmentModelMaps.get(name);
    }

    public void addFragmentModel(String name, FragmentModel fragment)
    {
        mFragmentModelMaps.put(name, fragment);
    }

    public static class FragmentBuilder
    {
        public static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";

        public static final String INFO_FRAGMENT = "INFO_FRAGMENT";

        public static final String GOAL_FRAGMENT = "GOAL_FRAGMENT";

        public static final String BATTERY_FRAGMENT = "BATTERY_FRAGMENT";

        public static final String BIND_FRAGMENT = "BIND_FRAGMENT";

        public static final String ABOUT_FRAGMENT = "ABOUT_FRAGMENT";

        public static final String LOCATION_FRAGMENT = "LOCATION_FRAGMENT";

        public static final String HEALTHY_FRAGMENT = "HEALTHY_FRAGMENT";

        public static final String SCHOOLHOME_FRAGMENT = "SCHOOLHOME_FRAGMENT";

        public static final String FAMILY_FRAGMENT = "FAMILY_FRAGMENT";

        public static final String HOME_FRAGMENT = "HOME_FRAGMENT";

        public static final String MESSAGE_FRAGMENT = "MESSAGE_FRAGMENT";

        public static final String NAVIGATION_FREGMENT = "NAVIGATION_FREGMENT";

        public static final String CONTACTS_FREGMENT_SM = "CONTACTS_FREGMENT_SM";

        public static final String MYINFO_FREGMENT_V20 = "MYINFO_FREGMENT_V20";

        public static final String MESSAGES_FREGMENT_SM = "MESSAGES_FREGMENT_SM";

        public static final String HOME_FRAGMENT_SM = "SM_HOME_FRAGMENT_SM";

        public static final String  SMART_PHONE_ADD_NEW = "SMART_PHONE_ADD_NEW";

        public static final String  SMART_PHONE_ADD_OLD = "SMART_PHONE_ADD_OLD";

        public static FragmentModel getMessageFragmentModel()
        {
            UserMessageFragment fragment = new UserMessageFragment();
            FragmentModel fragmentModel = new FragmentModel(
                    mContext.getResources().getString(R.string.home), fragment);
            return fragmentModel;
        }

        public static FragmentModel getLocationFragmentModel()
        {
            LocationFragment fragment = new LocationFragment();
            FragmentModel fragmentModel = new FragmentModel(
                    mContext.getResources().getString(R.string.location),
                    fragment);
            return fragmentModel;
        }


        public static FragmentModel getSMHomeFragmentModel()
        {
        	HomeFragement_V11 fragment = new HomeFragement_V11();
            FragmentModel fragmentModel = new FragmentModel(
                    mContext.getResources().getString(R.string.yijia), fragment);
            return fragmentModel;
        }

        public static FragmentModel getSMMessagesFragmentModel()
        {
            MessageFragment_sm fragment = new MessageFragment_sm();
            FragmentModel fragmentModel = new FragmentModel(
                    mContext.getResources().getString(R.string.messages), fragment);
            return fragmentModel;
        }

        public static FragmentModel getContactsFragmentModel()
        {
            ContactsFragment fragment = new ContactsFragment();
            FragmentModel fragmentModel = new FragmentModel(
                    mContext.getResources().getString(R.string.contacts),
                    fragment);
            return fragmentModel;
        }

        public static FragmentModel getMyInfoFragmentModel()
        {
            MyInfoFragment fragment = new MyInfoFragment();
            FragmentModel fragmentModel = new FragmentModel(
                    mContext.getResources().getString(R.string.myinfo),
                    fragment);
            return fragmentModel;
        }

    }
}
