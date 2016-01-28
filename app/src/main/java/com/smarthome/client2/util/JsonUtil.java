package com.smarthome.client2.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
	
	public static void JsonObject2HashMap(JSONObject jo, List<Map<String, String>> rstList) {  
       
		for (Iterator<String> keys = jo.keys(); keys.hasNext();) {  
            try {  
                String key1 = keys.next();  

                if (jo.get(key1) instanceof JSONObject) {  
  
                    continue;  
                }  
                if (jo.get(key1) instanceof JSONArray) {  
                    continue;  
                }  
  
                json2HashMap(key1, jo.getString(key1), rstList);  
  
            } catch (JSONException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
	
	public static void json2HashMap(String key, String value,  
            List<Map<String, String>> rstList) {  
        HashMap<String, String> map = new HashMap<String, String>();  
        map.put(key, value);  
        rstList.add(map);  
    }  

}
