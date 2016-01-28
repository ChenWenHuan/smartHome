package com.smarthome.client2.widget;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.util.HttpUtil;

/**
 * @author n003913 健康、位置数据管理类（用于widget）
 */
public class HLManager
{

    private final static String SPORTSURL = HttpUtil.BASE_URL_WEAR
            + "/app/getFamilyData.action";

    private final static String LOCATIONSURL = HttpUtil.BASE_URL_SMART
            + "/location/getFamilyPosition.action";

    private final static String FAMILY_BRIEFLY = HttpUtil.BASE_URL_SMART + "family/getFamBriefly.action";
    private static HLManager instance;

    private ArrayList<MemberInfo> mList;

    private HttpJson mParams;

    private Timer mTimer;

    private DataTask mTask;

    private HLManager()
    {
    }

    /**
     * 获取weatherAndTime Widget的成员信息
     * @return
     */
    public MemberInfo getWeatherAndTimeWidgetMemberInfo(int position)
    {
        synchronized (mList)
        {
            if (mList.isEmpty())
            {
                return null;
            }
            else
            {
                if (position >= mList.size() || mList.size() <= 1)
                {
                    return null;
                }
                else {
                    return mList.get(position);
                }
            }
        }
    }

    private void initMembers()
    {
            getFamilyBriefly();
            FamilyMemberDao dao = new FamilyMemberDao(SmartHomeApplication.getInstance());
            mList = dao.queryAll();
            dao.close();
            if (mList.isEmpty())
            {
                Intent intent = new Intent(FsConstants.WIDGET_ACCOUNT_CHANGGE);
                SmartHomeApplication.getInstance().sendBroadcast(intent);
                return;
            }
            JSONArray jsonArray = new JSONArray();
            for (MemberInfo bean : mList)
            {
                jsonArray.put(bean.getId());
            }
            mParams = new HttpJson();
            mParams.put("userIds", jsonArray);
    }

    /**
     * 初始化数据，获取唯一实例
     * @return
     */
    public static HLManager getInstance()
    {
        if (instance == null)
        {
            LogUtil.i("HLManager", "beginWork");
            instance = new HLManager();
            instance.initMembers();
            instance.mTimer = new Timer();
            // 确保在主线程中初始化
            ImageDownLoader.getInstance();
            instance.mTask = instance.new DataTask();
            instance.mTimer.schedule(instance.mTask, 0, 600000);
        }
        return instance;
    }
    /**
     * 获取简要
     */
    private void getFamilyBriefly()
    {
        String familyBriefly = MyHttpUtil.post(FAMILY_BRIEFLY, null);
        if (familyBriefly == null || familyBriefly.isEmpty())
        {
            return;
        }
        try
        {
            JSONObject jObject = new JSONObject(familyBriefly);
            int retcode = jObject.getInt("retcode");
            // Token失效
            if (retcode == 101)
            {
                Preferences.getInstance(SmartHomeApplication.getInstance())
                        .setIsLogout(true);
                Intent intent = new Intent(FsConstants.WIDGET_ACCOUNT_CHANGGE);
                SmartHomeApplication.getInstance().sendBroadcast(intent);
                return;
            }
            if (retcode != 200)
            {
                return;
            }
            JSONArray jsonArray = jObject.getJSONArray("data");
            JSONObject childObject;
            for (int i = 1;i<jsonArray.length();i++)
            {
                childObject = jsonArray.getJSONObject(i);
                String address = childObject.getString("latestPos");
                String name = childObject.getString("cwName");
                String headPath = childObject.getString("headPicPath");
                MemberInfo member = mList.get(i);
                member.setAddress(address);
                member.setName(name);
                member.setHeadPath(headPath);
                mList.remove(i);
                mList.add(i,member);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 取消Timer，销毁instance
     */
    public static void endWork()
    {
        if (instance != null)
        {
            LogUtil.i("HLManager", "endWork");
            instance.mTimer.cancel();
            instance.mTimer.purge();
            instance.mTimer = null;
            instance = null;
        }
    }

    /**
     * 立即向服务器拉取信息，刷新数据
     */
    public void getDataImmediately()
    {
        mTimer.cancel();
        mTask.cancel();
        mTimer.purge();
        mTask = instance.new DataTask();
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 600000);  //10*60*1000(毫秒)=10分钟一次定位
    }



    ///////////////////////////////////////////////////////////////////////////
    // 下面的代码无用代码，是为了编译过以前的而没删除
    //////////////////////////////////////////////////////////////////////////
    /**
     * 家庭成员增减后，更新成员列表，立即刷新widget数据
     */
    public void updateMembers()
    {
        synchronized (mList)
        {
            getFamilyBriefly();
        }

        getDataImmediately();
    }

    /**
     * 获取家庭成员数量，以此设置widget上、下点击按钮可见性
     * @return
     */
    public int getMemberNum()
    {
        synchronized (mList)
        {
            return mList.size();
        }
    }

    /**
     * 获取4x2上方成员信息
     * @return
     */
    public MemberInfo getUpMemberInfo()
    {
        return null;
    }

    /**
     * 获取4x2下方成员信息
     * @return
     */
    public MemberInfo getDownMemberInfo()
    {
        return null;
    }

    /**
     * 获取4x1成员信息
     * @return
     */
    public MemberInfo getSingleMemberInfo()
    {
        return null;
    }


    /**
     * 修改widget加载数据的索引
     * @param single
     *            4x1窗格还是4x2窗格
     * @param down
     *            向上选择还是向下选择
     */
    public void changePosition(boolean single, boolean down)
    {
    }

    class DataTask extends TimerTask
    {
        @Override
        public void run()
        {
            synchronized (mList)
            {
                LogUtil.i("HLManager", "TimerTask");
                getFamilyBriefly();
            }
            Intent intent = new Intent(FsConstants.WIDGET_DATA_OK);
            SmartHomeApplication.getInstance().sendBroadcast(intent);
        }
    }

    /**
     * 获取运动数据
     */
    private void getSports()
    {
        String sports = MyHttpUtil.post(SPORTSURL, mParams);
        if (sports == null || sports.isEmpty())
        {
            return;
        }
        try
        {
            JSONObject jObject = new JSONObject(sports);
            JSONObject childObject;
            for (MemberInfo member : mList)
            {
                if (jObject.has(member.getId()))
                {
                    member.setHasSports(true);
                    childObject = jObject.getJSONObject(member.getId());
                    int calorie = childObject.getInt("calorie");
                    int steps = childObject.getInt("steps");
                    int targetSteps = childObject.getInt("targetSteps");
                    // 如果没有设定运动目标，默认为10000步
                    if (targetSteps == 0)
                    {
                        targetSteps = 10000;
                    }
                    double aim = 100.0 * steps / targetSteps;
                    member.setCalorie(calorie);
                    member.setStep(steps);
                    member.setAim((int) Math.round(aim));
                }
                else
                {
                    member.setHasSports(false);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取位置信息
     */
    private void getLocations()
    {
        String locations = MyHttpUtil.post(LOCATIONSURL, null);
        if (locations == null || locations.isEmpty())
        {
            return;
        }
        try
        {
            JSONObject jObject = new JSONObject(locations);
            int retcode = jObject.getInt("retcode");
            // Token失效
            if (retcode == 101)
            {
                Preferences.getInstance(SmartHomeApplication.getInstance())
                        .setIsLogout(true);
                Intent intent = new Intent(FsConstants.WIDGET_ACCOUNT_CHANGGE);
                SmartHomeApplication.getInstance().sendBroadcast(intent);
                return;
            }
            if (retcode != 200)
            {
                return;
            }
            jObject = jObject.getJSONObject("data");
            JSONObject childObject;
            for (MemberInfo member : mList)
            {
                if (jObject.getString(member.getId()).equals("null"))
                {
                    continue;
                }
                childObject = jObject.getJSONObject(member.getId());
                String address = childObject.getString("address");
                member.setAddress(address);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 下载头像
     */
    private void getHeadImgs()
    {
        ImageDownLoader loader = ImageDownLoader.getInstance();
        for (MemberInfo item : mList)
        {
            loader.downloadImage(item.getHeadUrl(), -1);
        }
    }

}
