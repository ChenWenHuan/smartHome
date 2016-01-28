package com.smarthome.client2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.LogFactory;
import com.smarthome.client2.common.CommonLog;
import com.umeng.analytics.MobclickAgent;

public class CommonFragment extends Fragment
{

    public static final CommonLog log = LogFactory.createLog();

    public ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.common_layout, null);
        mImageView = (ImageView) view.findViewById(R.id.imageView);

        return view;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause()
    {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        super.onPause();
    }

}
