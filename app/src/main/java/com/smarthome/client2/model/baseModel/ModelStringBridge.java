
/**
 * Created by wenhuanchen on 12/8/15.
 */
package com.smarthome.client2.model.baseModel;

public interface ModelStringBridge {
    String modelToString();
    BaseModel stringToModel(String s_model);
}