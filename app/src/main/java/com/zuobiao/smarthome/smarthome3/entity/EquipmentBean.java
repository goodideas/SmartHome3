package com.zuobiao.smarthome.smarthome3.entity;

import java.io.Serializable;

/**
 * Created by Administrator
 * on 2016/1/5.
 */
public class EquipmentBean implements Serializable{

//    Short_ADDR ：节点短地址
//    Coord_Short_ADDR ：父节点短地址
//    Device_Type ：设备型号
//    Software_Edition ：节点设备嵌入式软件版本号
//    Hardware_Edition ：节点设备嵌入式电路版本号
//    Point_Data：节点设备数据，根据具体节点类型，代表不同含义
//    Remark ：节电设备备注信息，用于人机交互

    private int id;
    private String mac_ADDR; //设备的mac地址
    private String short_ADDR;
    private String coord_Short_ADDR;
    private String device_Type;
    private String software_Edition;
    private String hardware_Edition;
    private String point_Data;
    private String remark;
    private int imageId;
    private boolean onLine;


    public String getMac_ADDR() {
        return mac_ADDR;
    }

    public void setMac_ADDR(String mac_ADDR) {
        this.mac_ADDR = mac_ADDR;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShort_ADDR() {
        return short_ADDR;
    }

    public void setShort_ADDR(String short_ADDR) {
        this.short_ADDR = short_ADDR;
    }

    public String getCoord_Short_ADDR() {
        return coord_Short_ADDR;
    }

    public void setCoord_Short_ADDR(String coord_Short_ADDR) {
        this.coord_Short_ADDR = coord_Short_ADDR;
    }

    public String getDevice_Type() {
        return device_Type;
    }

    public void setDevice_Type(String device_Type) {
        this.device_Type = device_Type;
    }

    public String getSoftware_Edition() {
        return software_Edition;
    }

    public void setSoftware_Edition(String software_Edition) {
        this.software_Edition = software_Edition;
    }

    public String getHardware_Edition() {
        return hardware_Edition;
    }

    public void setHardware_Edition(String hardware_Edition) {
        this.hardware_Edition = hardware_Edition;
    }

    public String getPoint_Data() {
        return point_Data;
    }

    public void setPoint_Data(String point_Data) {
        this.point_Data = point_Data;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
