package com.smarthome.client2.activity;

import java.util.ArrayList;
import java.util.List;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.FamilyRelativeBean;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectFamilyRelative extends Activity {
	
	private FrameLayout fl_head;

    private CustomActionBar actionBar;
    
    private ListView   lv_relative;
    
    private FamilyRelativeAdapter lv_adapter;
    
    private ArrayList<FamilyRelativeBean> items = null;
    
    private String title;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_family_relative);
        initView();
        lv_relative = (ListView)this.findViewById(R.id.lv_relative);
        
        lv_relative.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
                Intent intent = SelectFamilyRelative.this.getIntent();
                Bundle data = new Bundle();
                data.putString("title",
                		items.get(position).getTitle());
                data.putString("code",
                		items.get(position).getCode());
                intent.putExtras(data);
                SelectFamilyRelative.this.setResult(RESULT_OK, intent);
                SelectFamilyRelative.this.finish();
                return;
				
			}
		});
        
        lv_adapter = new FamilyRelativeAdapter(this, items);
        lv_relative.setAdapter(lv_adapter);
        lv_adapter.notifyDataSetChanged();
        

    }
    
    private void initView(){
        String strAction = this.getIntent().getAction();
        if(strAction.equals("SEL_FAMILY_RELATIVE")){
        	items = SmartHomeApplication.getInstance().getRelativeData();
        	title= "请选择家庭关系";
        }else{
        	items = new ArrayList<FamilyRelativeBean>();
        	FamilyRelativeBean b1 = new FamilyRelativeBean();
        	b1.code = "4";
        	b1.title = "智能机";
        	items.add(b1);
        	FamilyRelativeBean b2 = new FamilyRelativeBean();
        	b2.code = "2";
        	b2.title = "老人机";
        	items.add(b2);
        	FamilyRelativeBean b3 = new FamilyRelativeBean();
        	b3.code = "1";
        	b3.title = "学生机";
        	items.add(b3);
        	FamilyRelativeBean b4 = new FamilyRelativeBean();
        	b4.code = "6";
        	b4.title = "儿童手表";
        	items.add(b4);
//        	FamilyRelativeBean b5 = new FamilyRelativeBean();
//        	b5.code = "7";
//        	b5.title = "跟踪器";
//        	items.add(b5);
        	title= "请选择设备类型";
        	
        }
    	addTopBarToHead();
    }
    
    //添加actionbar
    private void addTopBarToHead()
    {
    	fl_head = (FrameLayout) findViewById(R.id.fl_header_home);
        if (actionBar != null)
        {
        	fl_head.removeView(actionBar);
        }
        actionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                title,
                null,
                null);
        fl_head.addView(actionBar);
    }
    
    
    class FamilyRelativeAdapter extends BaseAdapter
    {
        /**
         * 上下文对象
         */
        private final Context mContext;

        /**
         * 当前要显示的列表
         */
        private List<FamilyRelativeBean> items;

        public FamilyRelativeAdapter(Context context, List<FamilyRelativeBean> items)
        {
            this.mContext = context;
            this.items = items;
        }

        public void setFamilyMemberCusor(List<FamilyRelativeBean> items)
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
            final Holder getter;
            if (view == null)
            {
                view = LayoutInflater.from(SelectFamilyRelative.this)
                        .inflate(R.layout.family_relative_list_item, null);

                getter = new Holder();
                getter.tv_relative_name = (TextView) view.findViewById(R.id.tv_title);
                view.setTag(getter);
            }
            else{
                getter = (Holder) view.getTag();
            }
            String name = items.get(pos).getTitle();
            getter.tv_relative_name.setText(name);
            return view;
        }

        class Holder
        {
            TextView tv_relative_name;
        }

    }
}
