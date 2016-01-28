package com.smarthome.client2.familySchool.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.model.MsgTarget;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.util.ScreenUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author n003913 发布成绩页面（教师）
 */
public class ScoreInputActivity extends BaseActivity
{
    private ImageView ivBack;

    private TextView tvPublish;

    private EditText etExamName;

    private LinearLayout lLayout;

    private String classId;

    private String subjectId;

    private String subjectName;

    private int type;

    private ArrayList<MsgTarget> mList;

    private String examName;

    private EditText[] eTexts;

    private JSONArray scores;

    private Handler listHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    showProgressDialog(R.string.is_loading);
                    break;
                case FsConstants.HTTP_SUCCESS:
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        ArrayList<MsgTarget> temp = ResultParsers.parserStudents(result);
                        if (temp == null)
                        {
                            showToast(R.string.data_parser_error);
                        }
                        else if (temp.isEmpty())
                        {
                            showToast("没有对应的学生");
                        }
                        else
                        {
                            mList = temp;
                            addStudentsView();
                        }
                    }
                    else
                    {
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    removeProgressDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private Handler publishHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    showProgressDialog(R.string.is_submitting);
                    break;
                case FsConstants.HTTP_SUCCESS:
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        Preferences.getInstance(ScoreInputActivity.this)
                                .setPublishNew(true);
                        showToast(R.string.publish_success);
                        finish();
                    }
                    else
                    {
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    removeProgressDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /* (non-Javadoc)
     * @see com.smarthome.client2.familySchool.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_score_input);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvPublish = (TextView) findViewById(R.id.tv_publish);
        etExamName = (EditText) findViewById(R.id.et_exam_name);
        lLayout = (LinearLayout) findViewById(R.id.llayout);

        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        subjectId = intent.getStringExtra("subjectId");
        subjectName = intent.getStringExtra("subjectName");
        type = intent.getIntExtra("input_type", -1);

        OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.iv_back:
                        ivBack.setImageResource(R.drawable.back_in);
                        cancelJudge();
                        break;
                    case R.id.tv_publish:
                        if (mList == null)
                        {
                            showToast("没有对应的学生，无法发布成绩");
                            return;
                        }
                        examName = etExamName.getText().toString();
                        if (examName.isEmpty())
                        {
                            showToast("请输入考试标题");
                            return;
                        }
                        if (type == 0)
                        {
                            boolean hasError = checkScores();
                            if (hasError)
                            {
                                showToast("存在非法数据，请重新输入");
                                return;
                            }
                        }
                        scores = getScores();
                        if (scores.length() == 0)
                        {
                            showToast("请至少输入一位学生的有效成绩");
                            return;
                        }
                        publishScores();
                        break;
                    default:
                        break;
                }
            }
        };
        ivBack.setOnClickListener(listener);
        tvPublish.setOnClickListener(listener);

        getStudents();
    }

    /**
     * 添加显示学生列表的视图
     */
    private void addStudentsView()
    {
        int size = mList.size();
        eTexts = new EditText[size];
        LinearLayout ll;
        TextView tvNum;
        TextView tvName;
        EditText etScore;
        for (int i = 0; i < size; i++)
        {
            final MsgTarget item = mList.get(i);

            ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.setLayoutParams(params);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBaselineAligned(false);

            tvNum = new TextView(this);
            tvNum.setText(i + 1 + "");
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            param1.weight = 1;
            tvNum.setGravity(Gravity.CENTER);
            tvNum.setBackgroundResource(R.drawable.submit_score_border_green);
            tvNum.setLayoutParams(param1);
            tvNum.setTextSize(16);
            tvNum.setTextColor(getResources().getColor(R.color.white));

            tvName = new TextView(this);
            tvName.setText(item.getName());
            tvName.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            param2.weight = 3;
            tvName.setLayoutParams(param2);
            tvName.setBackgroundResource(R.drawable.score_square);
            tvName.setTextSize(18);
            tvName.setTextColor(getResources().getColor(R.color.class_circle_text_deep));
            tvName.setMinHeight(ScreenUtils.dip2px(this, 48));

            eTexts[i] = etScore = new EditText(this);
            etScore.setHint("请输入成绩");
            etScore.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            param3.weight = 3;
            etScore.setLayoutParams(param3);
            etScore.setTextSize(16);
            etScore.setTextColor(getResources().getColor(R.color.class_circle_text_light));
            etScore.setBackgroundResource(R.drawable.input_box);
            if (type == 0)
            {
                etScore.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            else
            {
                etScore.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            etScore.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                    5) });
            etScore.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1,
                        int arg2, int arg3)
                {
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                        int arg2, int arg3)
                {
                }

                @Override
                public void afterTextChanged(Editable arg0)
                {
                    item.setScore(arg0.toString());
                }
            });

            ll.addView(tvNum);
            ll.addView(tvName);
            ll.addView(etScore);
            lLayout.addView(ll);
        }
    }

    /**
     * 检查输入数字的有效性
     * @return
     */
    private boolean checkScores()
    {
        boolean hasError = false;
        MsgTarget item;
        String score;
        int size = mList.size();
        for (int i = 0; i < size; i++)
        {
            item = mList.get(i);
            score = item.getScore();
            if (score != null
                    && !score.isEmpty()
                    && (score.startsWith(".") || score.endsWith(".") || (score.length() > 1
                            && score.startsWith("0") && !score.startsWith("0."))))
            {
                hasError = true;
                eTexts[i].setTextColor(getResources().getColor(R.color.red));
            }
            else
            {
                eTexts[i].setTextColor(getResources().getColor(R.color.class_circle_text_light));
            }
        }
        return hasError;
    }

    /**
     * 获取输入有效成绩的学生列表
     * @return
     */
    private JSONArray getScores()
    {
        JSONArray jArray = new JSONArray();
        JSONObject jObject;
        String score;
        for (MsgTarget item : mList)
        {
            score = item.getScore();
            if (score != null && !score.isEmpty())
            {
                try
                {
                    jObject = new JSONObject();
                    jObject.put("score", score);
                    jObject.put("studentid", item.getId());
                    jObject.put("userid", item.getUserId());
                    jObject.put("name", item.getName());
                    jArray.put(jObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return jArray;
    }

    /**
     * 获取学生列表
     */
    private void getStudents()
    {
        HttpJson params = new HttpJson();
        params.put("classid", classId);
        MyHttpUtil.post("/homeandschool/getStudentInfoByClass.action",
                params,
                listHandler);
    }

    /**
     * 发布成绩
     */
    private void publishScores()
    {
        HttpJson params = new HttpJson();
        params.put("classid", classId);
        params.put("subject", subjectId);
        params.put("title", examName);
        params.put("subject_name", subjectName);
        params.put("type", type);
        params.put("scores", scores);
        MyHttpUtil.post("/homeandschool/issueScore.action",
                params,
                publishHandler);
    }

    private void cancelJudge()
    {
        examName = etExamName.getText().toString();
        if (mList != null && (!examName.isEmpty() || getScores().length() > 0))
        {
            showCancelDialog();
        }
        else
        {
            finish();
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        cancelJudge();
    }
}
