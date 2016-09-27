package com.zuobiao.smarthome.smarthome3.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author zhuangbinbin
 * Time 2016/1/16.
 */
public class SpHelper {


    private final String spFileName = "SmartHome";
    private final String spUsUDPPort = "usUDPPort";
    private final String spUsTCPPort = "usTCPPort";
    private final String spUiDeviceType = "uiDeviceType";
    private final String spUiSoftEdition = "uiSoftEdition";
    private final String spUiHardwareEdition = "uiHardwareEdition";
    private final String spPoint_Data = "Point_Data";
    private final String spM_ucName = "m_ucName";
    private final String spGateWayMac = "gateWayMac";

    private final String spHasGateWayInfo = "hasGateWayInfo";
    private final String spOnLine = "OnLine";

    private final String spGateWayIp = "gateWayIp";

    private final String spIsUseName = "isUseName";
    private final String spIsUseNumber = "isUseNumber";



    private final String spTemp = "temp";
    private final String spHumidity = "humidity";
    private final String spPm25 = "pm25";
    private final String spLightSensor = "lightSensor";
    private final String spInflammableGasSensor = "inflammableGasSensor";
    private final String spNoiseSensor = "noiseSensor";

    private final String spWifiPassword = "wifiPassword";

    private SharedPreferences gateWayInfoShared;
    SharedPreferences.Editor gateWayInfoEditor;

    public SpHelper(Context context) {
        gateWayInfoShared = context.getSharedPreferences(
                spFileName, Activity.MODE_PRIVATE);
        gateWayInfoEditor = gateWayInfoShared.edit();
    }


    //wifi密码
    public String getSpWifiPassword() {
        return gateWayInfoShared.getString(spWifiPassword, null);
    }

    public void saveSpWifiPassword(String wifiPassword) {
        gateWayInfoEditor.putString(spWifiPassword, wifiPassword);
        gateWayInfoEditor.commit();
    }


    //湿度
    public String getSpHumidity() {
        return gateWayInfoShared.getString(spHumidity, null);
    }

    public void saveSpHumidity(String humidity) {
        gateWayInfoEditor.putString(spHumidity, humidity);
        gateWayInfoEditor.commit();
    }

    //温度
    public String getSpTemp() {
        return gateWayInfoShared.getString(spTemp, null);
    }

    public void saveSpTemp(String temp) {
        gateWayInfoEditor.putString(spTemp, temp);
        gateWayInfoEditor.commit();
    }

    //pm2.5
    public String getSpPm25() {
        return gateWayInfoShared.getString(spPm25, null);
    }

    public void saveSpPm25(String pm25) {
        gateWayInfoEditor.putString(spPm25, pm25);
        gateWayInfoEditor.commit();
    }

    //光照
    public String getSpLightSensor() {
        return gateWayInfoShared.getString(spLightSensor, null);
    }

    public void saveSpLightSensor(String lightSensor) {
        gateWayInfoEditor.putString(spLightSensor, lightSensor);
        gateWayInfoEditor.commit();
    }
    //噪音
    public String getSpNoiseSensor() {
        return gateWayInfoShared.getString(spNoiseSensor, null);
    }

    public void saveSpNoiseSensor(String noiseSensor) {
        gateWayInfoEditor.putString(spNoiseSensor, noiseSensor);
        gateWayInfoEditor.commit();
    }


    //烟雾
    public String getSpInflammableGasSensor() {
        return gateWayInfoShared.getString(spInflammableGasSensor, null);
    }

    public void saveSpInflammableGasSensor(String inflammableGasSensor) {
        gateWayInfoEditor.putString(spInflammableGasSensor, inflammableGasSensor);
        gateWayInfoEditor.commit();
    }


    public void SaveSpIsUseNumber(int isUseNumber) {
        gateWayInfoEditor.putInt(spIsUseNumber, isUseNumber);
        gateWayInfoEditor.commit();
    }

    public int getSpIsUseNumber() {
        return gateWayInfoShared.getInt(spIsUseNumber, -1);
    }



    public void SaveSpIsUseName(String isUseName) {
        gateWayInfoEditor.putString(spIsUseName, isUseName);
        gateWayInfoEditor.commit();
    }

    public String getSpIsUseName() {
        return gateWayInfoShared.getString(spIsUseName, "");
    }


    public void SaveSpGateWayIp(String gateWayIp) {
        gateWayInfoEditor.putString(spGateWayIp, gateWayIp);
        gateWayInfoEditor.commit();
    }

    public String getSpGateWayIp() {
        return gateWayInfoShared.getString(spGateWayIp, null);
    }


    public void SaveSpOnLine(boolean onLine) {
        gateWayInfoEditor.putBoolean(spOnLine, onLine);
        gateWayInfoEditor.commit();
    }

    public boolean getSpOnLine() {
        return gateWayInfoShared.getBoolean(spOnLine, false);

    }


    public void SaveSpHasGateWayInfo(boolean hasGateWayInfo) {
        gateWayInfoEditor.putBoolean(spHasGateWayInfo, hasGateWayInfo);
        gateWayInfoEditor.commit();
    }

    public boolean getSpHasGateWayInfo() {
        return gateWayInfoShared.getBoolean(spHasGateWayInfo, false);

    }


    public void SaveSpGateWayMac(String gateWayMac) {
        gateWayInfoEditor.putString(spGateWayMac, gateWayMac);
        gateWayInfoEditor.commit();
    }

    public String getSpGateWayMac() {
        return gateWayInfoShared.getString(spGateWayMac, null);
    }


    public void SaveSpUsUDPPort(String udpPort) {
        gateWayInfoEditor.putString(spUsUDPPort, udpPort);
        gateWayInfoEditor.commit();
    }

    public String getSpUsUDPPort() {
        return gateWayInfoShared.getString(spUsUDPPort, null);
    }


    public void SaveSpUsTCPPort(String usTCPPort) {
        gateWayInfoEditor.putString(spUsTCPPort, usTCPPort);
        gateWayInfoEditor.commit();
    }

    public String getSpUsTCPPort() {
        return gateWayInfoShared.getString(spUsTCPPort, null);
    }


    public void SaveSpUiDeviceType(String uiDeviceType) {
        gateWayInfoEditor.putString(spUiDeviceType, uiDeviceType);
        gateWayInfoEditor.commit();
    }

    public String getSpUiDeviceType() {
        return gateWayInfoShared.getString(spUiDeviceType, null);
    }


    public void SaveSpUiSoftEdition(String uiSoftEdition) {
        gateWayInfoEditor.putString(spUiSoftEdition, uiSoftEdition);
        gateWayInfoEditor.commit();
    }

    public String getSpUiSoftEdition() {
        return gateWayInfoShared.getString(spUiSoftEdition, null);
    }


    public void SaveSpUiHardwareEdition(String uiHardwareEdition) {
        gateWayInfoEditor.putString(spUiHardwareEdition, uiHardwareEdition);
        gateWayInfoEditor.commit();
    }

    public String getSpUiHardwareEdition() {
        return gateWayInfoShared.getString(spUiHardwareEdition, null);
    }

    public void SaveSpPoint_Data(String point_Data) {
        gateWayInfoEditor.putString(spPoint_Data, point_Data);
        gateWayInfoEditor.commit();
    }

    public String getSpPoint_Data() {
        return gateWayInfoShared.getString(spPoint_Data, null);
    }


    public void SaveSpM_ucName(String m_ucName) {
        gateWayInfoEditor.putString(spM_ucName, m_ucName);
        gateWayInfoEditor.commit();
    }

    public String getSpM_ucName() {
        return gateWayInfoShared.getString(spM_ucName, null);
    }


    //暂定
    public void removeData() {

        //删除数据 spUsUDPPort
        gateWayInfoEditor.remove(spUsUDPPort);
        gateWayInfoEditor.commit();

    }


    public void saveGateWayInfo(String mac,String usUDPPort, String usTCPPort,
                                 String uiDeviceType, String uiSoftEdition,
                                 String uiHardwareEdition, String Point_Data, String m_ucName) {
        this.SaveSpGateWayMac(mac);
        this.SaveSpUsUDPPort(usUDPPort);
        this.SaveSpUsTCPPort(usTCPPort);
        this.SaveSpUiDeviceType(uiDeviceType);
        this.SaveSpUiSoftEdition(uiSoftEdition);
        this.SaveSpUiHardwareEdition(uiHardwareEdition);
        this.SaveSpPoint_Data(Point_Data);
        this.SaveSpM_ucName(m_ucName);

    }


}
