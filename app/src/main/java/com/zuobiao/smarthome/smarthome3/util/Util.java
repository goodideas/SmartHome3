package com.zuobiao.smarthome.smarthome3.util;

import android.content.Context;
import android.widget.Toast;

import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator
 * on 2016/1/12.
 */
public class Util {

    private static Toast toast;
    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 把密度转换为像素
     */
    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }

    /**
     * 16进制字符串转byte数组
     */
    public static byte[] HexString2Bytes(String hexString){

        int stringLength = hexString.length();
        byte[] data = new byte[(stringLength/2)];
        for(int i = 0,j = 0;i<data.length;i++,j=j+2)
        {
            data[i] = (byte)Integer.parseInt(hexString.substring(j,(j+2)), 16);
        }
        return data;
    }

    /**
     * byte数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b, int byteLength) {
        String ret = "";
        for (int i = 0; i < byteLength; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    //数据校验码
    public static byte checkData(String data) {
        byte reData;
        int sum = 0;
        int dataLength = data.length();
        for (int i = 0; i < (dataLength); i = i + 2) {
            sum = sum + Integer.parseInt(data.substring(i, 2 + i), 16);
        }
        String temp = "0" + Integer.toHexString(sum);
        reData = (byte) Integer.parseInt(temp.substring(temp.length() - 2, temp.length()).toUpperCase(), 16);
        return reData;
    }

    public static String hexString2Characters(String hexString){
        String tvNameText = null;
        try {
            tvNameText = new String(HexString2Bytes(hexString),"utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tvNameText;
    }


    public static byte[] getLocalTime() {
        byte[] time = new byte[7];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String timeForm = formatter.format(curDate);
        String times[] = timeForm.split(":");

        String year = Integer.toHexString(Integer.parseInt(times[0]));
        if (year.length() != 4) {
            year = "0" + year;
        }
        //year == 07e0
        byte[] yearByte = HexString2Bytes(year);

        time[0] = yearByte[1];
        time[1] = yearByte[0];

        for (int i = 1; i < 6; i++) {
            time[i + 1] = Byte.parseByte(times[i]);
        }
        return time;
    }

    public static byte[] broadcastData(){
        //数据头
        byte[] searchGateWay = new byte[17+7];
        searchGateWay[0] = Constant.DATA_HEAD[0];
        searchGateWay[1] = Constant.DATA_HEAD[1];

        searchGateWay[2] = (byte)0x00;
        searchGateWay[3] = (byte)0x00;
        searchGateWay[4] = (byte)0x00;
        searchGateWay[5] = (byte)0x00;
        searchGateWay[6] = (byte)0x00;
        searchGateWay[7] = (byte)0x00;
        searchGateWay[8] = (byte)0x00;
        searchGateWay[9] = (byte)0x00;

        //命令类型
        searchGateWay[10] = Constant.GATEWAY_SEND_COMMAND[0];
        searchGateWay[11] = Constant.GATEWAY_SEND_COMMAND[1];

        //数据内容长度
        searchGateWay[12] = (byte)0x07;
        //数据内容
        searchGateWay[13] = (byte)0x00;

        byte[] timeByte = getLocalTime();
        // yyyy MM dd HH mm ss
        searchGateWay[14] = timeByte[0];//年
        searchGateWay[15] = timeByte[1];//年
        searchGateWay[16] = timeByte[2];//月
        searchGateWay[17] = timeByte[3];//日
        searchGateWay[18] = timeByte[4];//时
        searchGateWay[19] = timeByte[5];//分
        searchGateWay[20] = timeByte[6];//秒
        //数据校验

        searchGateWay[21] = checkData(bytes2HexString(timeByte,timeByte.length));

        //数据尾
        searchGateWay[22] = Constant.DATA_TAIL[0];
        searchGateWay[23] = Constant.DATA_TAIL[1];
        return searchGateWay;
    }


    public static byte[] getModifyData(String equipmentName,String gateWayMac,EquipmentBean equipment){
        byte[] data = new byte[51];
        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = HexString2Bytes(gateWayMac);
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.MODIFY_EQUIPMENT_NAME_SEND_COMMAND[0];
        data[11] = Constant.MODIFY_EQUIPMENT_NAME_SEND_COMMAND[1];
        //数据内容长度
        data[12] = (byte) 0x22;
        data[13] = (byte) 0x00;

        byte[] equipmentMacByte = HexString2Bytes(equipment.getMac_ADDR());
        int equipmentMacByteLength = equipmentMacByte.length;
        System.arraycopy(equipmentMacByte, 0, data, 14, equipmentMacByteLength);

        byte[] equipmentShortMacByte = HexString2Bytes(equipment.getShort_ADDR());
        int equipmentShortMacByteLength = equipmentShortMacByte.length;
        System.arraycopy(equipmentShortMacByte, 0, data, 22, equipmentShortMacByteLength);

        byte[] etNameByte = equipmentName.getBytes();
        int etNameByteLength = etNameByte.length;
        System.arraycopy(etNameByte, 0, data, 24, etNameByteLength);

        String checkData = bytes2HexString(data, data.length);
        data[48] = checkData(checkData.substring(28, 96));//校验位
        data[49] = Constant.DATA_TAIL[0];
        data[50] = Constant.DATA_TAIL[1];
        return data;

    }


    public static byte[] getDataOfBeforeDo(String gateWayMac,byte[] equipmentSendCommand,EquipmentBean equipment) {
        byte[] data = new byte[25];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = HexString2Bytes(gateWayMac);
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = equipmentSendCommand[0];
        data[11] = equipmentSendCommand[1];
        //数据内容长度
        data[12] = (byte) 0x08;
        data[13] = (byte) 0x00;
        byte[] equipmentMacByte = HexString2Bytes(equipment.getMac_ADDR());
        int equipmentMacByteLength = macByte.length;
        System.arraycopy(equipmentMacByte, 0, data, 14, equipmentMacByteLength);
        String checkData = bytes2HexString(data, data.length);

        data[22] = checkData(checkData.substring(28, 44));//校验位
        data[23] = Constant.DATA_TAIL[0];
        data[24] = Constant.DATA_TAIL[1];
        return data;
    }



    public static void showToast(Context context,String info){
        if(toast!=null){
            toast.setText(info);
        }else{
            toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context,String info,int gravity,int xOffset,int yOffset){

        if(toast!=null){
            toast.setText(info);

        }else{
            toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        }
        toast.setGravity(gravity,xOffset,yOffset);
        toast.show();
    }

    public static void showToast(Context context,int info){

        if(toast!=null){
            toast.setText(info);

        }else{
            toast = Toast.makeText(context, context.getResources().getString(info), Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context,int info,int gravity,int xOffset,int yOffset){

        if(toast!=null){
            toast.setText(info);
        }else{
            toast = Toast.makeText(context, context.getResources().getString(info), Toast.LENGTH_SHORT);
        }
        toast.setGravity(gravity,xOffset,yOffset);
        toast.show();
    }







}


