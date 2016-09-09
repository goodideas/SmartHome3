package com.zuobiao.smarthome.smarthome3.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhuangbinbin
 * on 2016/1/5.
 */
public class SpConfig {
    private Context context;
    private final String SharedPreferencesName = "smarthome";
    //TODO 保存的名字或者属性
    private final String SharedPreferencesId = "armId";
    //TODO 保存的名字或者属性
    private final String SharedPreferencesNumber = "armNumber";


    public SpConfig(){}

    public SpConfig(Context context){
        this.context = context;
    }


    public void SaveSharedPreferencesId(String id) {
        SharedPreferences idShared = context.getSharedPreferences(
                SharedPreferencesName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor idEditor = idShared.edit();
        idEditor.putString(SharedPreferencesId, id);
        idEditor.commit();


    }


    public String getIdText() {
        SharedPreferences idShared = context.getSharedPreferences(
                SharedPreferencesName, Activity.MODE_PRIVATE);
        return idShared.getString(SharedPreferencesId, "");
    }

    public void SaveSharedPreferencesNumber(String number) {
        SharedPreferences numberShared = context.getSharedPreferences(
                SharedPreferencesName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor numberEditor = numberShared.edit();
        numberEditor.putString(SharedPreferencesNumber,number);
        numberEditor.commit();
    }


    public String getNumberText() {
        SharedPreferences numberShared = context.getSharedPreferences(
                SharedPreferencesName, Activity.MODE_PRIVATE);
        return numberShared.getString(SharedPreferencesNumber, "");
    }

}
