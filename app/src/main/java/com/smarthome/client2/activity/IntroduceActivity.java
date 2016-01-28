package com.smarthome.client2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.VersionManager;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.view.MyIntroduceView;
import com.umeng.analytics.MobclickAgent;

/**
 * 引导界面
 * @author xiaolong.zhang
 *
 */
public class IntroduceActivity extends Activity implements OnClickListener
{

    private MyIntroduceView customView;

    private LayoutInflater inflater = null;

    private View view1, view2, view3, view4;

    // private ImageView img1, img2, img3, img4;

    private Button mBtnEntry;

    private TextView mNoticeText;

    private CheckBox mCheckBox;

    private BroadcastReceiver mReceiver;

    // 引导页的下面的圆点布局
    private LinearLayout linearLayout_introuduce_point;

    private OnCheckedChangeListener mCheckBoxListener = new OnCheckedChangeListener()
    {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked)
        {
            if (buttonView == mCheckBox && isChecked)
            {
                mBtnEntry.setEnabled(true);
            }
            else
            {
                mBtnEntry.setEnabled(false);
            }

        }
    };

    // private Bitmap bitmap1, bitmap2, bitmap3, bitmap5;
    // private int positon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.introduce_activity);
        inflater = LayoutInflater.from(this);
        init();
        mReceiver = new MyBroadcastReceiver();
        IntentFilter f1 = new IntentFilter();
        f1.addAction(MyIntroduceView.NowViewChange);// 注册广播，引导页下一页，圆点随即改变
        IntroduceActivity.this.registerReceiver(mReceiver, f1);
    }

    private void init()
    {
        linearLayout_introuduce_point = (LinearLayout) findViewById(R.id.linearLayout_introuduce_point);
        linearLayout_introuduce_point.setVisibility(View.INVISIBLE);
        customView = (MyIntroduceView) findViewById(R.id.customAdView1);
        //        view1 = inflater.inflate(R.layout.introduce_view1, null);
        //      img1 = (ImageView) view1.findViewById(R.id.img_guide_1);
        view2 = inflater.inflate(R.layout.introduce_view2, null);
        //        img2 = (ImageView) view2.findViewById(R.id.img_guide_2);
        view3 = inflater.inflate(R.layout.introduce_view3, null);
        //        img3 = (ImageView) view3.findViewById(R.id.img_guide_3);
        view4 = inflater.inflate(R.layout.introduce_view4, null);
        //        img4 = (ImageView) view4.findViewById(R.id.img_guide_4);
        mBtnEntry = (Button) view4.findViewById(R.id.button1);
        GradientDrawable drawable = new GradientDrawable();  
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框  
        drawable.setStroke(1, Color.RED); // 边框粗细及颜色  
        drawable.setColor(0xFFFFFF); // 边框内部颜色
        mBtnEntry.setBackgroundDrawable(drawable);
        //        customView.addMyView(view1);
        mNoticeText = (TextView) view4.findViewById(R.id.notice_detail);
        mNoticeText.setText(R.string.introduce_notice);
        mNoticeText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mNoticeText.setClickable(true);
        mNoticeText.setOnClickListener(this);
        mCheckBox = (CheckBox) view4.findViewById(R.id.notice_checkbox);
        mCheckBox.setVisibility(View.GONE);
        mCheckBox.setOnCheckedChangeListener(mCheckBoxListener);
        customView.addMyView(view2);
        customView.addMyView(view3);
        customView.addMyView(view4);
        customView.mCurrentScreen = 0;
        mBtnEntry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == mBtnEntry)
        {
            Intent in = new Intent(IntroduceActivity.this, LoginActivity_sm.class);
            // in.putExtra("isFromIntroduce", true);// 是否从引导页进入
            startActivity(in);
            Preferences.getInstance(IntroduceActivity.this)
                    .setLastVersionCode(VersionManager.getSofewareVersionCode(IntroduceActivity.this));
            finish();
        }
        else if (v == mNoticeText)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(HttpUtil.DISCLAIMSER_URL));
            try
            {
                startActivity(intent);
            }
            catch (Exception e)
            {
                TLog.Log("access notice failed:" + e.toString());
            }
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            int point = intent.getIntExtra("position", 0);
            //            positon = point;
            if (MyIntroduceView.NowViewChange.equals(action))
            {
                for (int i = 0; i < linearLayout_introuduce_point.getChildCount(); i++)
                {
                    ImageView iv = (ImageView) linearLayout_introuduce_point.getChildAt(i);
                    if (i == point)
                    {
                        iv.setImageResource(R.drawable.intro_point2);
                    }
                    else
                    {
                        iv.setImageResource(R.drawable.intro_point1);
                    }
                }
            }

            // setBitmap();

        }
    }

    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
        // setBitmap();
    }

    private long main_key_down_start_time = 0;

    private boolean isFirstMainKeyDown = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (isFirstMainKeyDown)
            {
                main_key_down_start_time = System.currentTimeMillis();
                Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
                isFirstMainKeyDown = false;
                return false;
            }
            else
            {

                // 如果是在首页，三秒内第二次按返回键，如果菜单栏是关闭状态，则退出应用
                if (System.currentTimeMillis() - main_key_down_start_time < 3000)
                {
                    return super.onKeyDown(keyCode, event);
                }
                else
                {
                    main_key_down_start_time = System.currentTimeMillis();
                    Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //      if (bitmap1 != null) {
        //          if (!bitmap1.isRecycled()) {
        //              bitmap1.recycle();
        //          }
        //          bitmap1 = null;
        //      }
        //
        //      if (bitmap2 != null) {
        //          if (!bitmap2.isRecycled()) {
        //              bitmap2.recycle();
        //          }
        //          bitmap2 = null;
        //      }
        //
        //      if (bitmap3 != null) {
        //          if (!bitmap3.isRecycled()) {
        //              bitmap3.recycle();
        //          }
        //          bitmap3 = null;
        //      }
        //
        //      if (bitmap5 != null) {
        //          if (!bitmap5.isRecycled()) {
        //              bitmap5.recycle();
        //          }
        //          bitmap5 = null;
        //      }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mReceiver != null)
        {
            unregisterReceiver(mReceiver);
        }
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
