package com.zuobiao.smarthome.smarthome3.entity;

import java.io.Serializable;

/**
 * Author Administrator
 * Time 2016/3/5.
 */
public class SceneEquipmentBean implements Serializable {


    public SceneEquipmentBean(){}

    private String sceneName;//场景名
    private String equipmentMac; //场景设置中的设备Mac
    private String equipmentNumber;//场景设置中的设备序号，比如面板开关，有3个控制。
    private String Trigger;//触发方式
    private String immediatelyTrig;//立即触发
    private String timeTrig;//时间触发
    private String timeSwitchCmd;//conditionTrig 条件触发//修改为时间开关命令
    private String conditionEquipmentMac;//条件触发设备Mac
    private String coditionNumber;//条件触发传感号
    private String conditionValue;//条件值
    private String trigSymbol;//触发符号 具体有 大于、小于、等于；有人、没人；开、关。

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    private String temp;


    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getConditionEquipmentMac() {
        return conditionEquipmentMac;
    }

    public void setConditionEquipmentMac(String conditionEquipmentMac) {
        this.conditionEquipmentMac = conditionEquipmentMac;
    }

    public String getEquipmentMac() {
        return equipmentMac;
    }

    public void setEquipmentMac(String equipmentMac) {
        this.equipmentMac = equipmentMac;
    }

    public String getEquipmentNumber() {
        return equipmentNumber;
    }

    public void setEquipmentNumber(String equipmentNumber) {
        this.equipmentNumber = equipmentNumber;
    }

    public String getTrigger() {
        return Trigger;
    }

    public void setTrigger(String trigger) {
        Trigger = trigger;
    }

    public String getImmediatelyTrig() {
        return immediatelyTrig;
    }

    public void setImmediatelyTrig(String immediatelyTrig) {
        this.immediatelyTrig = immediatelyTrig;
    }

    public String getTimeTrig() {
        return timeTrig;
    }

    public void setTimeTrig(String timeTrig) {
        this.timeTrig = timeTrig;
    }

    public String getTimeSwitchCmd() {
        return timeSwitchCmd;
    }

    public void setTimeSwitchCmd(String timeSwitchCmd) {
        this.timeSwitchCmd = timeSwitchCmd;
    }

    public String getCoditionNumber() {
        return coditionNumber;
    }

    public void setCoditionNumber(String coditionNumber) {
        this.coditionNumber = coditionNumber;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getTrigSymbol() {
        return trigSymbol;
    }

    public void setTrigSymbol(String trigSymbol) {
        this.trigSymbol = trigSymbol;
    }


    @Override
    public String toString() {
        return "sceneName="+sceneName+",equipmentMac="+equipmentMac+",equipmentNumber="+equipmentNumber+
                ",Trigger="+Trigger+",immediatelyTrig="+immediatelyTrig+",timeTrig="+timeTrig+
                ",timeSwitchCmd="+timeSwitchCmd+",conditionEquipmentMac="+conditionEquipmentMac+
                ",coditionNumber="+coditionNumber+",conditionValue="+conditionValue+",trigSymbol="+trigSymbol;
    }
}
