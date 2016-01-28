package com.smarthome.client2.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.AddOlderActivity;
import com.smarthome.client2.activity.SearchFamilyMember;

public class MyFamilyLayout extends LinearLayout
{

    private LinearLayout family_choose_search;

    private LinearLayout family_choose_old;

    private PopupWindow mPopupWindowDialog;

    private Context ctx;

    private View view;

    private int height = 0;

    public MyFamilyLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public MyFamilyLayout(Context context, int height)
    {
        super(context);
        this.height = height;
        init(context);
    }

    private void init(Context ctx)
    {
        this.ctx = ctx;
        initView();
    }

    private void initView()
    {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.family_choose_dialog, null);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.family_choose_item_ll);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ll.getLayoutParams();
        lp.topMargin = height;
        ll.setLayoutParams(lp);

        family_choose_search = (LinearLayout) view.findViewById(R.id.family_choose_ll);
        family_choose_old = (LinearLayout) view.findViewById(R.id.family_old_ll);
        mPopupWindowDialog = new PopupWindow(view, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        mPopupWindowDialog.setFocusable(true);
        mPopupWindowDialog.update();
        mPopupWindowDialog.setBackgroundDrawable(new BitmapDrawable(
                getResources(), (Bitmap) null));
        mPopupWindowDialog.setOutsideTouchable(true);

        family_choose_search.setOnClickListener(item_click);
        family_choose_old.setOnClickListener(item_click);
        view.setOnClickListener(item_click);
    }

    private OnClickListener item_click = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent;
            v.setBackgroundColor(getResources().getColor(R.color.submit_item_blue));
            switch (v.getId())
            {
                case R.id.family_choose_ll:// 搜索
                    intent = new Intent(ctx, SearchFamilyMember.class);
                    ctx.startActivity(intent);
                    popupWindow();
                    break;
                case R.id.family_old_ll:// 老人
                    intent = new Intent(ctx, AddOlderActivity.class);
                    ctx.startActivity(intent);
                    popupWindow();
                    break;
                default:
                    popupWindow();
                    break;
            }
        }
    };

    private void popupWindow()
    {
        if (mPopupWindowDialog != null && mPopupWindowDialog.isShowing())
        {
            mPopupWindowDialog.dismiss();
        }
    }

    public void initFamilyFrame(int layout, int widget)
    {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (mPopupWindowDialog != null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(layout, null);
            mPopupWindowDialog.showAtLocation(v.findViewById(widget),
                    Gravity.TOP | Gravity.RIGHT,
                    0,
                    0);
        }
    }

}
