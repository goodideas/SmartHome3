package com.zuobiao.smarthome.smarthome3.entity;

/**
 * Author Administrator
 * Time 2016/3/16.
 */
public class SceneNameBean {

    public SceneNameBean(){

    }

    private int sid;
    private String sceneName;
    private int equipmentNumbers;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public int getEquipmentNumbers() {
        return equipmentNumbers;
    }

    public void setEquipmentNumbers(int equipmentNumbers) {
        this.equipmentNumbers = equipmentNumbers;
    }
}
