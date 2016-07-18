package com.zuobiao.smarthome.smarthome3.db;

import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneEquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneNameBean;

import java.util.List;

/**
 * Created by zhuangbinbin
 * on 2016/1/5.
 */
public interface InterfaceDBcurd {

    //添加网关发送过来的设备数据
    void addData(EquipmentBean equipment);
    //根据设备的mac删除设备数据
    void delData(String equipmentMac);
    //删除所有设备数据
    void delALL();
    //得到所有设备数据
    List<EquipmentBean> getAllData();


    void updataEquipmentName(String equipmentNameHexString,String mac);

    String getNickNameByMac(String mac);

    int getLength();
    //得到所有可以控制的设备
    List<EquipmentBean> getAllSceneEquipments();

    //得到所有产生条件的设备
    List<EquipmentBean> getAllSceneConditionEquipments();

    //=========================场景设置=============================
    void addSceneData(SceneEquipmentBean sceneEquipmentBean);
    void updateSceneData(SceneEquipmentBean sceneEquipmentBean);
    void delAllSceneData();
    boolean isSaveSceneDataByEquipmentMac(SceneEquipmentBean sceneEquipmentBean);
    //根据场景名搜索数据库的场景设置的所有设备
    List<SceneEquipmentBean> getAllSceneEquipmentBySceneName(String sceneName);

    List<SceneEquipmentBean> getSceneEquipmentSettingByEquipmentMac(String equipmentMac);

    void delAllEquipmentDataByEquipmentMac(String equipmentMac);

    //添加一个参数来串起来
    void updateTemp(SceneEquipmentBean sceneEquipmentBean);

    //最后删掉这个参数，添加上场景名
    void updateSceneAddSceneName(String temp,String sceneName);

    List<SceneNameBean> getAllSceneName();

    void addSceneNameData(String name,int length);

    void delSceneNameDataBySceneName(String sceneName);

    //场景名数据库
    void updataSceneNameData(String name,int length,String whereName);

    void delALLSceneNameData();

    //=========================最后的场景设置=============================
    void addSceneAllData(SceneEquipmentBean sceneEquipmentBean);
    void delSceneAllBySceneAllName(String sceneName);
    List<SceneEquipmentBean> getAllSceneEquipmentBySceneNameAll(String sceneName);

    //=========================RFID=============================
    void addRfidInfo(String rfidInfo);
    void delRfidInfo(String rfidInfo);
    List<String> getAllRfidInfo();

}
