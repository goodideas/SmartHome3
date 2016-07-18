package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *欢迎界面，主要判断手机里面有没有保存过之前的网关信息和判断网关在不在线
 * 2个参数
 * hasGateWayInfo 网关的信息保存状态。有存过就是true，没有就是false
 * onLine   网关在线状态。在线就是true，不在线就是false
 * 这2个boolean型变量保存在sp文件中，可以随时更改，保存
 */
public class WelcomeActivity extends AppCompatActivity {

    private byte[] searchGateWay = new byte[17+7];
    private static final String broadcastIP = "255.255.255.255";
    private Util util;

    private byte[] bddata =
            {(byte)0xff,(byte)0xaa
            ,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
            ,(byte)0x01,(byte)0x00
            ,(byte)0x00,(byte)0x00
            ,(byte)0x00
            ,(byte)0xff,(byte)0x55};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        UdpHelper udpHelper = UdpHelper.getInstance();
        udpHelper.startUdpWithIp(broadcastIP,WelcomeActivity.this);
        util = new Util();

        udpHelper.setIsSend(true);
//        udpHelper.send(bddata);
        udpHelper.send(broadcastData());
        udpHelper.doSearchGateWayOnWelcom(Constant.WEICOM_ACTIVITY_SEARCH_GATEWAY_WAIT_MAX_TIME *1000);
        Handler  handler = new Handler();
        //2秒之后跳转到主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }

    private String getMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        int length = info.getMacAddress().length();
        String macAddress = "";
        for (int i = 0; i < length; i = i + 3) {
            macAddress = macAddress + info.getMacAddress().substring(i, (i + 2));
        }
        return macAddress;
    }

    private byte[] broadcastData(){
        //数据头
        searchGateWay[0] = Constant.DATA_HEAD[0];
        searchGateWay[1] = Constant.DATA_HEAD[1];
//        ff aa 00 00 00 00 00 00 00 00 01 00 00 00 00 ff 55

        //mac地址
//        byte[] macByte = util.HexString2Bytes(getMacAddress());
//        int macByteLength = macByte.length;
//        System.arraycopy(macByte,0,searchGateWay,2,macByteLength);
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

        searchGateWay[21] = util.checkData(util.bytes2HexString(timeByte,timeByte.length));

        //数据尾
        searchGateWay[22] = Constant.DATA_TAIL[0];
        searchGateWay[23] = Constant.DATA_TAIL[1];
        return searchGateWay;
    }

//    FFAA 0000000000000000 0100 0800  07 E0 03 14 0B 0E 0C 23 FF55
//    FFAA 0000000000000000 0100 0800  07 E0 03 14 0B 0E 36 4D FF55
    private byte[] getLocalTime(){
        byte[] time = new byte[7];
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy:MM:dd:HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String timeForm = formatter.format(curDate);
        String times[] = timeForm.split(":");

        String year = Integer.toHexString(Integer.parseInt(times[0]));
        if(year.length()!=4){
            year = "0"+year;
        }
        //year == 07e0
        byte[] yearByte = util.HexString2Bytes(year);
//        int yearByteLength = yearByte.length;
//        System.arraycopy(yearByte,0,time,0,yearByteLength);

        time[0] = yearByte[1];
        time[1] = yearByte[0];

        for(int i =1;i< 6;i++){
            time[i+1] = Byte.parseByte(times[i]);
        }

        return time;
    }

//    FF AA B7 59 0B 7F CF 5C 00 00 01 10 2A 00 2E 16 90 1F 00 05 FF FE 01 00 00 00 01 00 00 00 00 00 48 6F 6D 69 64 65 61 D6 C7 C4 DC CD F8 B9 D8 00 00 00 00 00 00 00 00 00 41 FF 55

}
