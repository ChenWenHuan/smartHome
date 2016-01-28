package com.smarthome.client2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.bean.CameraInfoItem;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.FamilyRelativeBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.unit.dao.MessageDB;
import com.baidu.mapapi.SDKInitializer;
//import com.squareup.leakcanary.LeakCanary;


public class SmartHomeApplication extends Application
{
    public static boolean PRINT_LOG = true;

    private static SmartHomeApplication instance;
    
    private MainActivity mainActivity = null;
    
    private MessageDB mMsgDB;
    
    public boolean isLinkTopAccout = false;
    
    public String jiTuiRegisterID = "";
    
    public String openID = "";

    private MemBean loginMember = new MemBean();

	private ArrayList<FamilyClassBean> familyClassList = new ArrayList<FamilyClassBean>();
    
    private ArrayList<FamilyRelativeBean> relativeList = new ArrayList<FamilyRelativeBean>();
    
    private   HashMap<String, List<CameraInfoItem>>  cameraMap = new HashMap<String, List<com.smarthome.client2.bean.CameraInfoItem>>();

    public boolean isCameraLibLoaded = false;
    		

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("smarthome", "SmartHomeApplication-oncreate");
        instance = this;
        SDKInitializer.initialize(getApplicationContext());
        mMsgDB = new MessageDB(this);
        makeUsefulDirs();
//        LeakCanary.install(this);
    }

    public static SmartHomeApplication getInstance()
    {
        return instance;
    }
    
    public FamilyClassBean getClassDataByClassID(String classID){
    	for (int i = 0; i < familyClassList.size(); i++){
    		if (familyClassList.get(i).type.equals("1")){
    			if (familyClassList.get(i).id.equals(classID)){
    				return familyClassList.get(i);
    			}
    		}
    	}
    	return null;
    }
    
	public synchronized MessageDB getMessageDB() {
		if (mMsgDB == null)
			mMsgDB = new MessageDB(this);
		return mMsgDB;
	}
    
    public FamilyClassBean getFamilyDataByFamilyID(String familyID){
    	for (int i = 0; i < familyClassList.size(); i++){
    		if (familyClassList.get(i).type.equals("0")){
    			if (familyClassList.get(i).id.equals(familyID)){
    				return familyClassList.get(i);
    			}
    		}
    	}
    	return null;
    }
    
    public HashMap<String, List<CameraInfoItem>> getFamilyCameraMap(){
    	return cameraMap;
    }
    
    public ArrayList<FamilyRelativeBean> getRelativeData(){
    	
    	return relativeList;
    }
    
    public FamilyClassBean getFirstClassData(){
    	
    	for (int i = 0; i < familyClassList.size(); i++){
    		if (familyClassList.get(i).type.equals("1")){
    				return familyClassList.get(i);
    		}
    	}
    	return null;
    }
    
    private void makeUsefulDirs(){
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if(sdCardExist){
            File dirFile = new File(Constants.WATCH_REACORD_AUDIO_PATH);
            if (!dirFile.exists()){
                dirFile.mkdirs();
            }
            dirFile = new File(Constants.CAMERA_VIDEO_RECORD_PATH);
            if (!dirFile.exists()){
                dirFile.mkdirs();
            }
        }
	}
    
    public ArrayList<FamilyClassBean> getDataList(){
    	return familyClassList;
    }

    public  HashMap<String, List<CameraInfoItem>> getCameraMap () {
        return cameraMap;
    }

    public MemBean getLoginMemberInfo() {
        return loginMember;
    }

    public void setLoginMemberInfo (MemBean me) {
        this.loginMember = me;
    }

    public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
    
    public ArrayList<FamilyClassBean> removeFamilyData(){
    	if (familyClassList == null)
    		return null;
    	if (familyClassList.size() <= 0)
    		return familyClassList;
    	for (int i = 0; i < familyClassList.size(); i++){
    		if (familyClassList.get(i).type.equals("1")){
    			familyClassList.remove(i);
    		}
    	}
    	return familyClassList;
    }
    
    public ArrayList<FamilyClassBean> removeClassData(){
    	if (familyClassList == null)
    		return null;
    	if (familyClassList.size() <= 0)
    		return familyClassList;
    	for (int i = 0; i < familyClassList.size(); i++){
    		if (familyClassList.get(i).type.equals("2")){
    			familyClassList.remove(i);
    		}
    	}
    	return familyClassList;
    }

    /**
     * 网络是否可用
     * @return
     */
    public boolean isNetworkAvailable()
    {
        try
        {
            ConnectivityManager connectivity = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()){
                    if (info.getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

}
