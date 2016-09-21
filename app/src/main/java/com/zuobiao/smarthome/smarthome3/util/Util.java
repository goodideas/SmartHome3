package com.zuobiao.smarthome.smarthome3.util;

import android.content.Context;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

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
            tvNameText = new String(Util.HexString2Bytes(hexString),"utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tvNameText;
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


