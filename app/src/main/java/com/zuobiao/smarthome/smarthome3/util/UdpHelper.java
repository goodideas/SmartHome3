package com.zuobiao.smarthome.smarthome3.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.v7.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneEquipmentBean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Author zhuangbinbin
 * Time 2016/1/16.
 */
public class UdpHelper extends UdpMethod {

    private static final String TAG = "UdpHelper";
    private InetAddress inetAddress;
    private static final int PORT = 5678;
    private String udpIp;
    private DatagramSocket udpSocket;
    private DatagramPacket udpPacket;
    private DatagramPacket sendUdpPacket;

    private byte[] udpReceiveByte = new byte[512];
    private boolean udpReceiveWhile = true;
    private boolean getDataFlag = false;
    private MyHandler myHandler;
    private SpHelper spHelper;
    private DBcurd dBcurd;

    private ProgressDialog searchDialog;

    private Runnable runnable;
    private Runnable WelcomeRunnable;
    private Runnable refreshRunnable;
    private Runnable addRunnable;
    private Runnable switchsRunnable;
    private Runnable WindowRunnable;
    private Runnable CurtainsRunnable;
    private boolean searchGateWayFlag = false; //主要判断是欢迎界面的查找网关还是添加界面的查找网关
    private int addEquipmentNumber;
    private int countEquipment;

    private TextView tvLightSensor;
    private TextView tvInfrared;
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvPm25;

    private TextView tvInflammableGas;
    private TextView tvDoorMagnet;


    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private ToggleButton toggleButton3;
    private ToggleButton toggleButton;
    private String socketMac;
    private String switchMac;
    private String inflammableGasMac;
    private String doorMagnetMac;
    private String infraredMac;
    private String lightSensorMac;
    private String noiseSensorMac;
    private String tempPm25Mac;

    private ToggleButton tbDoor;
    private ToggleButton tbWindow;
    private ToggleButton tbCurtains;
    private ToggleButton tbSingleCurtains ;

    private TextView tvNoiseSensor;

    private boolean isSend = true;


    private static UdpHelper UdpInstance = new UdpHelper();

    public static UdpHelper getInstance() {
        if (UdpInstance == null) {
            UdpInstance = new UdpHelper();
            Log.e(TAG, "getInstance");
        }
        return UdpInstance;
    }

    private UdpHelper() {
    }

    Context mContext;

    public void startUdpWithIp(String ip, Context context) {
        Log.e(TAG, "startUdpWithIp ip=" + ip);
        this.udpIp = ip;
        myHandler = new MyHandler();
        spHelper = new SpHelper(context);
        dBcurd = new DBcurd(context);
        startUdp();
        mContext = context;
        getDataFlag = false;
    }

    //消息
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notifys() {
        NotificationManager nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext);
        notification.setSmallIcon(R.drawable.bulb);
        notification.setContentTitle("提示");
        notification.setContentText("有人来了");
        notification.setTicker("有人");//显示栏
        notification.setAutoCancel(true);        //点击自动消息
        notification.setDefaults(Notification.DEFAULT_ALL);            //铃声,振动,呼吸灯
        nm.notify(0, notification.build());
    }


    private void startUdp() {
        Log.e(TAG, "startUdp PORT=" + PORT);
        try {
            if (udpIp != null) {
                inetAddress = InetAddress.getByName(udpIp);
                udpSocket = new DatagramSocket(PORT);
                receiveUdp();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveUdp() {
        Log.e(TAG, "receiveUdp");
        udpPacket = new DatagramPacket(udpReceiveByte, udpReceiveByte.length);
        new Thread() {
            public void run() {
                while (udpReceiveWhile) {
                    try {
                        udpSocket.receive(udpPacket);
                        int len = udpPacket.getLength();
                        if (len > 0) {
                            String receiveStr = Util.bytes2HexString(udpReceiveByte, len);
                            Log.e("UdpHelper", "receiveStr=" + receiveStr + " ip=" + udpPacket.getAddress().toString());
                            //说明有接收到设备的数据
                            doWithRevData(receiveStr, udpPacket.getAddress().toString().substring(1));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    public void setLightSensorTv(TextView tvLightSensor,String lightSensorMac) {
        this.tvLightSensor = tvLightSensor;
        this.lightSensorMac = lightSensorMac;
    }


    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    //udp发送广播
    public void send(byte[] data) {
        Log.e(TAG, "udp发送方法");
        if (data != null) {
            if (sendUdpPacket == null) {
                sendUdpPacket = new DatagramPacket(data, data.length,
                        inetAddress, PORT);
                Log.e(TAG, "=============sendUdpPacket new=");
             }else{
                Log.e(TAG, "=============sendUdpPacket has=");
                sendUdpPacket.setData(data);
                sendUdpPacket.setLength(data.length);
            }

            Log.e(TAG, "=============发送的数据=" + Util.bytes2HexString(data,data.length));
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    if (udpSocket != null) {

                        if (isSend) {
                            udpSocket.send(sendUdpPacket);
                            Log.e(TAG, "udp发送");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //关掉udp，把单例一起关掉
    public void closeUdp() {
        udpReceiveWhile = false;
        if (udpSocket != null) {
            udpSocket.close();
        }
        UdpInstance = null;
        Log.e("UdpHelper", "closeUdp");
    }

    @Override
    public boolean getRevDataFlag() {
        return getDataFlag;
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.HANDLER_ONLINE) {
                getDataFlag = true;
                if (searchGateWayFlag) {
                    myHandler.removeCallbacks(runnable);
                    searchDialog.dismiss();
                    spHelper.SaveSpOnLine(getDataFlag);
                    showResultDialog("信息", "找到网关");
                }

            }

            if (msg.what == Constant.HANDLER_ADD_EQUIPMENT_ADD_DATA) {
                spHelper.SaveSpOnLine(true);
                myHandler.removeCallbacks(addRunnable);
                searchDialog.dismiss();
                showResultDialog("信息", "添加成功");
            }

            if (msg.what == Constant.HANDLER_REFRESH_EQUIPMENT_GATE_DATA) {
                dBcurd.delALL();
                spHelper.SaveSpOnLine(true);
                myHandler.removeCallbacks(refreshRunnable);
                searchDialog.dismiss();
                if (addEquipmentNumber == 0) {
//                    Toast.makeText(mContext, "没有设备连接", Toast.LENGTH_SHORT).show();
                    Util.showToast(mContext, "没有设备连接");
                } else {
                    showWaitDialog("一共有" + addEquipmentNumber + "个设备，正在添加。。。");
                    // TODO: 2016/1/21 这个可能要作一个时间上判断，如果超时了，就要dismiss
                }
            }

            if (msg.what == Constant.HANDLER_REFRESH_EQUIPMENT_ADD_DATA) {
                //这个会触发多次，主要看接收的设备哟多少。
                countEquipment++;
                //当读取的设备数一致时，就可以关闭了
                if (countEquipment == addEquipmentNumber) {
                    showResultDialog("信息", "刷新完毕");
                    searchDialog.dismiss();
                }

            }

            if (msg.what == Constant.HANDLER_REFRESH_EQUIPMENT_NO_ANSWER) {
                showResultDialog("信息", "网关不在线");
            }

            if (msg.what == Constant.HANDLER_ADD_EQUIPMENT_NO_ANSWER) {
                showResultDialog("信息", "发送完毕");
            }

            if (msg.what == Constant.HANDLER_SEARCH_GATEWAY) {
                if (getDataFlag) {
                    showResultDialog("信息", "找到网关");
                } else {
                    showResultDialog("信息", "找不到网关或者网关不在线");
                }
            }

            //面板开关
            if (msg.what == Constant.HANDLER_SWITCHS_HAS_ANSWER) {

                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(mContext, "成功");
                }
                if (handlerMessage.equalsIgnoreCase("00")||handlerMessage.equalsIgnoreCase("FF")) {
                    Util.showToast(mContext, "失败");
                }

            }

            //插座返回数据
            if (msg.what == Constant.HANDLER_SOCKETS_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(mContext, "成功");
                }
                if (handlerMessage.equalsIgnoreCase("00")) {
                    Util.showToast(mContext, "失败");
                }

                if (handlerMessage.equalsIgnoreCase("FF")) {
                    Util.showToast(mContext, "失败");
                }
            }


            if(msg.what == Constant.HANDLER_SCENE_SETTING_HAS_ANSWER){
                String handlerMessage = (String) msg.obj;
                String status = handlerMessage.substring(28,30);
                if (status.equalsIgnoreCase("01")) {
                    Util.showToast(mContext, "成功");
                }else if (status.equalsIgnoreCase("00")) {
                    Util.showToast(mContext, "失败");
                }
            }


            //窗帘
            if (msg.what == Constant.HANDLER_CURTAINS_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(mContext, "成功！");
                }
                if (handlerMessage.equalsIgnoreCase("00")) {
                    Util.showToast(mContext, "失败！");
                }

                if (handlerMessage.equalsIgnoreCase("FF")) {
                    Util.showToast(mContext, "失败！");
                }
            }

            //面板
            if (msg.what == Constant.HANDLER_SWITCHS_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);
                if(switchMac!=null&&mac.equalsIgnoreCase(switchMac)){
                    int state = Integer.parseInt(stat, 16);
                    Log.e(TAG, "state1=" + state);
                    if (toggleButton1 != null && toggleButton2 != null && toggleButton3 != null)
                        dowithTbState(state);
                }


            }
            //面板2
            if (msg.what == Constant.HANDLER_SWITCHS_HAS_ANSWER3) {
                String handlerMessage = (String) msg.obj;
                int state = Integer.parseInt(handlerMessage, 16);
                Log.e(TAG, "state2=" + state);
                if (toggleButton1 != null && toggleButton2 != null && toggleButton3 != null)
                    dowithTbState(state);
            }

            //人为按下插座时返回的数据
            if (msg.what == Constant.HANDLER_SOCKETS_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;
                String data1 = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);

                Log.e(TAG,"handler mac = "+mac);
                if (data1.equalsIgnoreCase("01")&&mac.equalsIgnoreCase(socketMac)) {
                    if (toggleButton != null) {
                        setIsSend(false);
                        toggleButton.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }

                }
                if (data1.equalsIgnoreCase("00")&&mac.equalsIgnoreCase(socketMac)) {

                    if (toggleButton != null) {
                        setIsSend(false);
                        toggleButton.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);
                    }


                }
            }

            //同步
            if (msg.what == Constant.HANDLER_SOCKETS_HAS_ANSWER3) {
                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    if (toggleButton != null) {
                        setIsSend(false);
                        toggleButton.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);
                    }


                }
                if (handlerMessage.equalsIgnoreCase("00")) {
                    if (toggleButton != null) {
                        setIsSend(false);
                        toggleButton.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);
                    }



                }
            }


            //同步窗户窗帘数据
            if (msg.what == Constant.HANDLER_WINDOW_HAS_ANSWER) {

                String handlerMessage = (String) msg.obj;
                // TODO: 2016/3/21 数据处理
                String doors = handlerMessage.substring(0,2);
                String window = handlerMessage.substring(2,4);



                if(doors.equalsIgnoreCase("00")){
                    if (tbDoor != null) {
                        setIsSend(false);
                        tbDoor.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }
                }else if(doors.equalsIgnoreCase("01")){
                    if (tbDoor != null) {
                        setIsSend(false);
                        tbDoor.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }
                }

                if(window.equalsIgnoreCase("00")){
                    if (tbWindow != null) {
                        setIsSend(false);
                        tbWindow.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }
                }else if(window.equalsIgnoreCase("01")){
                    if (tbWindow != null) {
                        setIsSend(false);
                        tbWindow.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }
                }

//==================================================================
                if(doors.equalsIgnoreCase("00")){
                    if (tbCurtains != null) {
                        setIsSend(false);
                        tbCurtains.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }
                    if (tbSingleCurtains!=null) {
                        setIsSend(false);
                        tbSingleCurtains.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }

                }else if(doors.equalsIgnoreCase("01")){
                    if (tbCurtains != null) {
                        setIsSend(false);
                        tbCurtains.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }

                    if (tbSingleCurtains!=null) {
                        setIsSend(false);
                        tbSingleCurtains.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIsSend(true);
                            }
                        }, 100);

                    }
                }

//                if(window.equalsIgnoreCase("00")){
//                    if (tbWindows != null) {
//                        setIsSend(false);
//                        tbWindows.setChecked(false);
//                        myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                setIsSend(true);
//                            }
//                        }, 100);
//
//                    }
//                }else if(window.equalsIgnoreCase("01")){
//                    if (tbWindows != null) {
//                        setIsSend(false);
//                        tbWindows.setChecked(true);
//                        myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                setIsSend(true);
//                            }
//                        }, 100);
//
//                    }
//                }
//
//
//                if (handlerMessage.equalsIgnoreCase("01")) {
//
//                }
//
//                if (handlerMessage.equalsIgnoreCase("01")) {
//                    if (toggleButton != null)
//                        toggleButton.setChecked(true);
//                }
//                if (handlerMessage.equalsIgnoreCase("00")) {
//                    if (toggleButton != null)
//                        toggleButton.setChecked(false);
//                }
            }


            //光照
            if (msg.what == Constant.HANDLER_LIGHT_SERSON_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);


                if (tvLightSensor != null&&mac.equals(lightSensorMac)){
                    int lightSensor = high + low * 256;
                    tvLightSensor.setText("光照强度 ：" + lightSensor + "lux");
                    if(spHelper!=null){
                        spHelper.saveSpLightSensor(String.valueOf(lightSensor));
                    }


                }

            }

            //光照2
            if (msg.what == Constant.HANDLER_LIGHT_SERSON_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvLightSensor != null&&mac.equals(lightSensorMac)){
                    int lightSensor = high + low * 256;
                    if(lightSensor == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpLightSensor())){
                            if(Integer.parseInt(spHelper.getSpLightSensor())==0){
                                tvLightSensor.setText("正在读取数据。。。");
                            }else{
                                tvLightSensor.setText("光照强度 ：" + spHelper.getSpLightSensor() + "lux");
                            }
                        }
                    }else{
                        //读到的数据不是为0
                        tvLightSensor.setText("光照强度 ：" + lightSensor + "lux");
                    }
                }
            }


            //噪音
            if (msg.what == Constant.HANDLER_NOISE_SERSON_HAS_ANSWER) {

                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvNoiseSensor != null&&mac.equals(noiseSensorMac)){
                    int noiseSensor = high + low * 256;
                    tvNoiseSensor.setText("音量 ：" + noiseSensor + "db");
                    if(spHelper!=null){
                        spHelper.saveSpNoiseSensor(String.valueOf(noiseSensor));
                    }


                }

            }

            //噪音2
            if (msg.what == Constant.HANDLER_NOISE_SERSON_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvNoiseSensor != null&&mac.equals(noiseSensorMac)){
                    int noiseSensor = high + low * 256;
                    if(noiseSensor == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpNoiseSensor())){
                            if(Integer.parseInt(spHelper.getSpNoiseSensor())==0){
                                tvNoiseSensor.setText("正在读取数据。。。");
                            }else{
                                tvNoiseSensor.setText("音量 ：" + spHelper.getSpNoiseSensor() + "db");
                            }
                        }
                    }else{
                        //读到的数据不是为0
                        tvNoiseSensor.setText("音量 ：" + noiseSensor + "db");
                    }
                }
            }



            //烟雾
            if (msg.what == Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);

                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);

                if (tvInflammableGas != null&&mac.equals(inflammableGasMac)){
                    int inflammableGasSensor = (high + low * 256);
                    tvInflammableGas.setText("数据 ：" + inflammableGasSensor+" PPM");
                    if(spHelper!=null){
                        spHelper.saveSpInflammableGasSensor(String.valueOf(inflammableGasSensor));
                    }
                }
            }

            //烟雾2
            if (msg.what == Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER3) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvInflammableGas != null&&mac.equals(inflammableGasMac)) {
                    int inflammableGasSensor = high + low * 256;
                    if(inflammableGasSensor == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpInflammableGasSensor())){
                            if(Integer.parseInt(spHelper.getSpInflammableGasSensor())==0){
                                tvInflammableGas.setText("正在读取数据。。。");
                            }else{
                                tvInflammableGas.setText("数据 ：" + spHelper.getSpInflammableGasSensor() + " PPM");
                            }
                        }
                    }else{
                        //读到的数据不是为0
                        tvInflammableGas.setText("数据 ：" + inflammableGasSensor + " PPM");
                    }

                }
            }


            //人体红外
            if (msg.what == Constant.HANDLER_INFRARED_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);
                if (tvInfrared != null&&mac.equals(infraredMac)) {
                    if (stat.equalsIgnoreCase("01")) {
                        tvInfrared.setText("有人");
                        new Thread() {
                            @Override
                            public void run() {
                                notifys();
                            }
                        }.start();
                    }
                    if (stat.equalsIgnoreCase("00")) {
                        tvInfrared.setText("没人");
                    }

                }

            }
            //人体红外2
            if (msg.what == Constant.HANDLER_INFRARED_HAS_ANSWER3) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);
                if (tvInfrared != null&&mac.equals(infraredMac)) {
                    if (stat.equalsIgnoreCase("01")) {
                        tvInfrared.setText("有人");
                        new Thread() {
                            @Override
                            public void run() {
                                notifys();
                            }
                        }.start();
                    }
                    if (stat.equalsIgnoreCase("00")) {
                        tvInfrared.setText("没人");
                    }

                }

            }


            //温湿度pm2传过啦的数据
            if (msg.what == Constant.HANDLER_TEMP_PM25_HAS_ANSWER) {
                // 是为3个16进制的数 第一个是temp，第二个是humi，第三个是pm2.5
                String data = (String) msg.obj;
                String stat = data.substring(0,12);
                String mac = data.substring(12);
                if (tvPm25 != null&&tvTemperature !=null && tvHumidity!=null&&mac.equals(tempPm25Mac)) {
                    int a = Integer.parseInt(stat.substring(0, 2), 16) * 256;
                    int b = Integer.parseInt(stat.substring(2, 4), 16);
                    int c = Integer.parseInt(stat.substring(4, 6), 16) * 256;
                    int d = Integer.parseInt(stat.substring(6, 8), 16);
                    int e = Integer.parseInt(stat.substring(8, 10), 16);
                    int f = Integer.parseInt(stat.substring(10, 12), 16) * 256;
                    int temp = (c+d) / 10;
                    int hum = (a+b) / 10;
                    int pm25 = (e+f) ;

                    tvTemperature.setText(String.valueOf(temp));
                    tvHumidity.setText(String.valueOf(hum));
                    tvPm25.setText(String.valueOf(pm25));
                    if(spHelper!=null){
                        spHelper.saveSpTemp(String.valueOf(temp));
                        spHelper.saveSpHumidity(String.valueOf(hum));
                        spHelper.saveSpPm25(String.valueOf(pm25));
                    }

                }
            }
            //温湿度pm2传过啦的数据2
            if (msg.what == Constant.HANDLER_TEMP_PM25_HAS_ANSWER3) {
                // TODO: 2016/1/26 data 是为3个16进制的数 第一个是temp，第二个是humi，第三个是pm2.5
                String data = (String) msg.obj;
                String stat = data.substring(0,12);
                String mac = data.substring(12);
                if (tvPm25 != null&&tvTemperature !=null && tvHumidity!=null&&mac.equals(tempPm25Mac)) {
                    int a = Integer.parseInt(stat.substring(0, 2), 16) * 256;
                    int b = Integer.parseInt(stat.substring(2, 4), 16);
                    int c = Integer.parseInt(stat.substring(4, 6), 16) * 256;
                    int d = Integer.parseInt(stat.substring(6, 8), 16);
                    int e = Integer.parseInt(stat.substring(8, 10), 16);
                    int f = Integer.parseInt(stat.substring(10, 12), 16) * 256;

                    int temp = (c+d) / 10;
                    int hum = (a+b) / 10;
                    int pm25 = (e+f);

                    if(temp == 0&&hum == 0 && pm25 == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpTemp())&&!TextUtils.isEmpty(spHelper.getSpHumidity())&&!TextUtils.isEmpty(spHelper.getSpPm25())){
                            tvTemperature.setText(spHelper.getSpTemp());
                            tvHumidity.setText(spHelper.getSpHumidity());
                            tvPm25.setText(spHelper.getSpPm25());
                        }else{
                            tvTemperature.setText("正在读取。。。");
                            tvHumidity.setText("正在读取。。。");
                            tvPm25.setText("正在读取。。。");
                        }
                    }else{
                        tvTemperature.setText(String.valueOf(temp));
                        tvHumidity.setText(String.valueOf(hum));
                        tvPm25.setText(String.valueOf(pm25));
                    }


                }
            }

            if (msg.what == Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);

                if (tvDoorMagnet != null&&mac.equalsIgnoreCase(doorMagnetMac)) {
                    if (stat.equalsIgnoreCase("01")) {
                        tvDoorMagnet.setText("开");
                    }
                    if (stat.equalsIgnoreCase("00")) {
                        tvDoorMagnet.setText("关");
                    }
                }

            }

            if (msg.what == Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);

                if (tvDoorMagnet != null&&mac.equalsIgnoreCase(doorMagnetMac)) {
                    if (stat.equalsIgnoreCase("01")) {
                        tvDoorMagnet.setText("开");
                    }
                    if (stat.equalsIgnoreCase("00")) {
                        tvDoorMagnet.setText("关");
                    }
                }

            }

            //修改设备名称
            if (msg.what == Constant.HANDLER_MODIFY_EQUIPMENT_NAME_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;

                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(mContext, "成功！");
                }
                if (handlerMessage.equalsIgnoreCase("00")) {
                    Util.showToast(mContext, "失败！");
                }

            }


            //添加RFID信息回复
            if (msg.what == Constant.HANDLER_ADD_RFID_INFO_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                int rfidNumbers = Integer.parseInt(handlerMessage);
                if(rfidNumbers == 0){
                    Util.showToast(mContext,"添加失败！");
                }else{
                    Util.showToast(mContext,"RFID数量为"+rfidNumbers);
                }
            }


            //删除RFID信息回复
            if (msg.what == Constant.HANDLER_DEL_RFID_INFO_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                int rfidNumbers = Integer.parseInt(handlerMessage);
                if(rfidNumbers == 0){
                    Util.showToast(mContext,"删除失败！改RFID不存在");
                }else{
                    Util.showToast(mContext,"RFID数量为"+rfidNumbers);
                }
            }


            if(msg.what == Constant.HANDLER_SCENE_SETTING_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;

                String sceneNameByte = handlerMessage.substring(28, 76);
                String sceneNameString = new String(Util.HexString2Bytes(sceneNameByte)).trim();
                int equipmentCount = Integer.parseInt(handlerMessage.substring(76, 78));

                if(equipmentCount!=0){

                for (int i = 0; i < equipmentCount; i++) {
                    String equipmentMac = handlerMessage.substring(i * 56 + 80, i * 56 + 96);  //一条设备数据是28字节 所以*56
                    String equipmentNumber = handlerMessage.substring(i * 56 + 96, i * 56 + 98);
                    String trigger = handlerMessage.substring(i * 56 + 98, i * 56 + 100);
                    String immediatelyTrig = handlerMessage.substring(i * 56 + 100, i * 56 + 102);
                    String timeTrig = handlerMessage.substring(i * 56 + 102, i * 56 + 110);
                    String timeSwitchCmd = handlerMessage.substring(i * 56 + 110, i * 56 + 112);
                    String conditionEquipmentMac = handlerMessage.substring(i * 56 + 112, i * 56 + 128);
                    String coditionNumber = handlerMessage.substring(i * 56 + 118, i * 56 + 130);
                    String conditionValue = handlerMessage.substring(i * 56 + 130, i * 56 + 134);
                    String trigSymbol = handlerMessage.substring(i * 56 + 134, i * 56 + 136);

                    SceneEquipmentBean sceneEquipmentBean = new SceneEquipmentBean();
                    sceneEquipmentBean.setSceneName(sceneNameString);

                    sceneEquipmentBean.setEquipmentMac(equipmentMac);

                    sceneEquipmentBean.setEquipmentNumber(equipmentNumber);

                    if(trigger.equalsIgnoreCase("00")){
                        trigger = "Immediately";
                    }else if(trigger.equalsIgnoreCase("01")){
                        trigger = "Time";
                    }else if(trigger.equalsIgnoreCase("02")){
                        trigger = "Condition";
                    }
                    sceneEquipmentBean.setTrigger(trigger);

                    if(!TextUtils.isEmpty(timeTrig)){
                        String timeHour =  timeTrig.substring(4,6);
                        String timeMinute =  timeTrig.substring(6,8);
                        timeTrig = Integer.parseInt(timeHour,16)+":"+Integer.parseInt(timeMinute,16);
                    }else if(timeTrig.equalsIgnoreCase("00000000")){
                        timeTrig = "";
                    }else{
                        timeTrig = "";
                    }
                    sceneEquipmentBean.setTimeTrig(timeTrig);

                    if(!TextUtils.isEmpty(timeSwitchCmd)){
                        if (timeSwitchCmd.equalsIgnoreCase("00")){
                            timeSwitchCmd = "关";
                        }else if(timeSwitchCmd.equalsIgnoreCase("01")){
                            timeSwitchCmd = "开";
                        }
                    }else{
                        timeSwitchCmd = "";
                    }

                    sceneEquipmentBean.setTimeSwitchCmd(timeSwitchCmd);

                    if(!TextUtils.isEmpty(immediatelyTrig)){
                        if(immediatelyTrig.equalsIgnoreCase("00")){
                            immediatelyTrig = "false";
                        }else if(immediatelyTrig.equalsIgnoreCase("01")){
                            immediatelyTrig = "true";
                        }
                    }else {
                        immediatelyTrig = "";
                    }
                    sceneEquipmentBean.setImmediatelyTrig(immediatelyTrig);


                    sceneEquipmentBean.setConditionEquipmentMac(conditionEquipmentMac);

                    if(coditionNumber.equalsIgnoreCase("000000000000")){
                        coditionNumber = "";
                    }else{
                        if(coditionNumber.substring(coditionNumber.length()-2,coditionNumber.length()).equalsIgnoreCase("00")){
                            coditionNumber = "温度";
                        }else if(coditionNumber.substring(coditionNumber.length()-2,coditionNumber.length()).equalsIgnoreCase("01")){
                            coditionNumber = "湿度";
                        }else if(coditionNumber.substring(coditionNumber.length()-2,coditionNumber.length()).equalsIgnoreCase("02")){
                            coditionNumber = "PM2.5";
                        }
                    }
                    sceneEquipmentBean.setCoditionNumber(coditionNumber);

                    conditionValue = (Integer.parseInt(conditionValue.substring(2,4),16)+Integer.parseInt(conditionValue.substring(0,2),16)*256)+"";
                    sceneEquipmentBean.setConditionValue(conditionValue);

                    if(trigSymbol.equalsIgnoreCase("00")){
                        trigSymbol = "等于";
                    }else if(trigSymbol.equalsIgnoreCase("01")){
                        trigSymbol = "大于";
                    }else if(trigSymbol.equalsIgnoreCase("02")){
                        trigSymbol = "小于";
                    }
                        sceneEquipmentBean.setTrigSymbol(trigSymbol);
                    dBcurd.addSceneAllData(sceneEquipmentBean);
                }
                dBcurd.addSceneNameData(sceneNameString, equipmentCount);
                }else{
                    Util.showToast(mContext,"没有场景数据！");
                }
            }


        }
    }

    private void dowithTbState(int state) {
        setIsSend(false);
        switch (state) {
            case 0:
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
                break;
            case 1:
                toggleButton1.setChecked(true);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
                break;
            case 2:
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(true);
                toggleButton3.setChecked(false);
                break;
            case 3:
                toggleButton1.setChecked(true);
                toggleButton2.setChecked(true);
                toggleButton3.setChecked(false);
                break;
            case 4:
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(true);
                break;
            case 5:
                toggleButton1.setChecked(true);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(true);
                break;
            case 6:
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(true);
                toggleButton3.setChecked(true);
                break;
            case 7:
                toggleButton1.setChecked(true);
                toggleButton2.setChecked(true);
                toggleButton3.setChecked(true);
                break;
            default:
                break;
        }
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setIsSend(true);
            }
        }, 100);
//        setIsSend(true);
        Log.e(TAG, "switch-case end");
    }


    //在进入页面之前做一些判断
    public void doSwitchs(int maxTime) {
        showWaitDialog("正在同步");
        switchsRunnable = new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
                myHandler.sendEmptyMessage(Constant.HANDLER_SWITCHS_NO_ANSWER);
            }
        };
        myHandler.postDelayed(switchsRunnable, maxTime);
    }


    public void doWindow(int maxTime){
        showWaitDialog("正在同步");
        WindowRunnable = new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
                myHandler.sendEmptyMessage(Constant.HANDLER_WINDOW_NO_ANSWER);
            }
        };
        myHandler.postDelayed(WindowRunnable, maxTime);
    }

    public void doCurtains(int maxTime){
        showWaitDialog("正在同步");
        CurtainsRunnable = new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
                myHandler.sendEmptyMessage(Constant.HANDLER_CURTAINS_NO_ANSWER);
            }
        };
        myHandler.postDelayed(CurtainsRunnable,maxTime);
    }


    /**
     * 欢迎界面的查找网关
     *
     * @param maxTime 等待的最大时间 单位毫秒
     */
    public void doSearchGateWayOnWelcom(int maxTime) {
        WelcomeRunnable = new Runnable() {
            @Override
            public void run() {
                spHelper.SaveSpOnLine(getDataFlag);
                closeUdp();
            }
        };
        myHandler.postDelayed(WelcomeRunnable, maxTime);

    }


    public void doAddEquipment(int maxTime) {
        showWaitDialog("正在添加设备");
        addRunnable = new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
                myHandler.sendEmptyMessage(Constant.HANDLER_ADD_EQUIPMENT_NO_ANSWER);
            }
        };
        myHandler.postDelayed(addRunnable, maxTime);

    }


    public void doRefreshEquipment(int maxTime) {
        showWaitDialog("正在刷新设备");
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
                spHelper.SaveSpOnLine(false);
                myHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_EQUIPMENT_NO_ANSWER);
            }
        };
        myHandler.postDelayed(refreshRunnable, maxTime);
    }

    /**
     * 添加界面的查找网关
     */
    public void doSearchGateWay() {
        searchGateWayFlag = true;
        showWaitDialog("正在查找网关");
        runnable = new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
                spHelper.SaveSpOnLine(getDataFlag);
                myHandler.sendEmptyMessage(Constant.HANDLER_SEARCH_GATEWAY);
            }
        };
        myHandler.postDelayed(runnable, Constant.SEARCH_GATEWAY_WAIT_MAX_TIME * 1000);
    }

    /**
     * 等待对话框
     *
     * @param msg 显示的信息
     */
    private void showWaitDialog(String msg) {
        searchDialog = new ProgressDialog(mContext);
        searchDialog.setMessage(msg);
        searchDialog.onStart();
        searchDialog.show();
    }

    /**
     * 结果显示对话框
     *
     * @param title 标题
     * @param msg   显示的信息
     */
    private void showResultDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeUdp();
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }


    /**
     * 验证数据的真实
     *
     * @param data 接收的数据
     * @param ip   发送方的ip
     */
    private void doWithRevData(String data, String ip) {
        if (data.length() >= 4) {
            // 数据头ffaa
            if ("FF".equalsIgnoreCase(data.substring(0, 2))
                    && "AA".equalsIgnoreCase(data.substring(2, 4))) {
                if (data.length() >= 26) {
                    //数据内容长度
                    int dataLength = Integer.parseInt(data.substring(24, 26), 16);
                    if (data.length() >= 34) {
                        //数据尾ff55
                        if (data.substring(dataLength * 2 + 30,
                                dataLength * 2 + 32).equalsIgnoreCase("FF")
                                && data.substring(dataLength * 2 + 32,
                                dataLength * 2 + 34).equalsIgnoreCase("55")) {
                            int sum = 0;
                            for (int i = 0; i < (dataLength); i++) {
                                sum = sum + Integer.parseInt(data.substring(28 + i * 2, 30 + i * 2), 16);
                            }
                            String temp = "0" + Integer.toHexString(sum);
                            if (data.substring(data.length() - 6, data.length() - 4).equalsIgnoreCase(temp.substring(temp.length() - 2, temp.length()).toUpperCase())) {
                                //数据验证通过
                                Log.e("udpHelper", "数据时真的=" + data.substring(20, 24));
                                doWithDataByCommd(data.substring(20, 24), data, ip);
                            }
                        }
                    }

                }

            }
        }

    }

    /**
     * 处理接收的数据
     *
     * @param commd
     * @param data
     * @param ip
     */
    private void doWithDataByCommd(String commd, String data, String ip) {
        //网关

        if (commd.equalsIgnoreCase(Constant.GATEWAY_RECV_COMMAND)) {
            if(Constant.GATEWAY.equalsIgnoreCase(data.substring(36, 44))) {
                String gateWayMac = data.substring(4, 20);
                String udpPort = data.substring(28, 32);
                String tcpPort = data.substring(32, 36);
                String deviceType = data.substring(36, 44);
                String softEdition = data.substring(44, 52);
                String hardEdition = data.substring(52, 60);
                String pointData = data.substring(60, 64);
                String ucName = data.substring(64, 112);
                spHelper.SaveSpGateWayIp(ip);
                spHelper.saveGateWayInfo(gateWayMac, udpPort, tcpPort, deviceType, softEdition, hardEdition, pointData, ucName);
                spHelper.SaveSpHasGateWayInfo(true);
                Log.e("udpHelper", "处理网关包");

                myHandler.sendEmptyMessage(Constant.HANDLER_ONLINE);
            }
        }
        //刷新时网关发送过来的告知有几个包 02 10
        if (commd.equalsIgnoreCase(Constant.REFRESH_EQUIPMENT_RECV_COMMAND)) {
            //解析数据
            addEquipmentNumber = Integer.parseInt(data.substring(28, 30), 16);
            Log.e("udpHelper", "将要来的number=" + addEquipmentNumber);
            myHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_EQUIPMENT_GATE_DATA);

        }

        //n个设备包 03 10
        if (commd.equalsIgnoreCase(Constant.REFRESH_EQUIPMENT_RECV2_COMMAND)) {
            //解析数据
            //设备表
            EquipmentBean equipmentBean = new EquipmentBean();
            //设备的mac地址，不是网关的mac地址
            String mac = data.substring(28, 44);
            String shortAddr = data.substring(44, 48);
            String CoordShortAddr = data.substring(48, 52);
            String deviceType = data.substring(52, 60);
            String se = data.substring(60, 68);
            String he = data.substring(68, 76);
            String pd = data.substring(76, 80);
            String remark = data.substring(80, 128);

            equipmentBean.setMac_ADDR(mac);
            equipmentBean.setShort_ADDR(shortAddr);
            equipmentBean.setCoord_Short_ADDR(CoordShortAddr);
            equipmentBean.setDevice_Type(deviceType);
            equipmentBean.setSoftware_Edition(se);
            equipmentBean.setHardware_Edition(he);
            equipmentBean.setPoint_Data(pd);
            equipmentBean.setRemark(remark);
            dBcurd.addData(equipmentBean);
            myHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_EQUIPMENT_ADD_DATA);
        }

        //单独添加设备
        if (commd.equalsIgnoreCase(Constant.ADD_EQUIPMENT_RECV_COMMAND)) {
            Log.e("udpHelper", "处理添加设备包");
            //设备表
            EquipmentBean equipmentBean = new EquipmentBean();
            String mac = data.substring(28, 44);
            String shortAddr = data.substring(44, 48);
            String CoordShortAddr = data.substring(48, 52);
            String deviceType = data.substring(52, 60);
            String se = data.substring(60, 68);
            String he = data.substring(68, 76);
            String pd = data.substring(76, 80);
            String remark = data.substring(80, 128);
            equipmentBean.setMac_ADDR(mac);
            equipmentBean.setShort_ADDR(shortAddr);
            equipmentBean.setCoord_Short_ADDR(CoordShortAddr);
            equipmentBean.setDevice_Type(deviceType);
            equipmentBean.setSoftware_Edition(se);
            equipmentBean.setHardware_Edition(he);
            equipmentBean.setPoint_Data(pd);
            equipmentBean.setRemark(remark);
            dBcurd.addData(equipmentBean);
            myHandler.sendEmptyMessage(Constant.HANDLER_ADD_EQUIPMENT_ADD_DATA);

        }

        if (commd.equalsIgnoreCase(Constant.SWITCH_RECV_COMMAND)) {
            String handlerMessage = data.substring(44, 46);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_SWITCHS_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }

        //面板开关人为去触摸时会发送指令过来
        if (commd.equalsIgnoreCase(Constant.SWITCH_RECV2_COMMAND)) {
            String handlerMessage = data.substring(56, 58);
            String mac  = data.substring(28,44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_SWITCHS_HAS_ANSWER2;
            myHandler.sendMessage(msg);
        }

        //光照
        if (commd.equalsIgnoreCase(Constant.LIGHT_SENSOR_RECV2_COMMAND)) {


            if (Constant.LIGHT_SENSOR.equalsIgnoreCase(data.substring(48, 56))) {
                String mac = data.substring(28, 44);
                String handlerMessage = data.substring(56, 60);
                Message msg = new Message();
                msg.obj = handlerMessage+mac;
                msg.what = Constant.HANDLER_LIGHT_SERSON_HAS_ANSWER;
                myHandler.sendMessage(msg);
            }


        }
        //噪音
        if (commd.equalsIgnoreCase(Constant.NOISE_SENSOR_RECV2_COMMAND)) {


            if (Constant.NOISE_SENSOR.equalsIgnoreCase(data.substring(48, 56))) {
                String mac = data.substring(28, 44);
                String handlerMessage = data.substring(56, 60);
                Message msg = new Message();
                msg.obj = handlerMessage+mac;
                msg.what = Constant.HANDLER_NOISE_SERSON_HAS_ANSWER;
                myHandler.sendMessage(msg);
            }


        }

        if (commd.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECV2_COMMAND)) {
            //如果是烟雾的
            if (Constant.INFLAMMABLE_GAS.equalsIgnoreCase(data.substring(48, 56))) {
                String handlerMessage = data.substring(56, 60);
                String mac = data.substring(28, 44);
                Message msg = new Message();
                msg.obj = handlerMessage+mac;
                msg.what = Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER;
                myHandler.sendMessage(msg);
            }
        }

        if (commd.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECV3_COMMAND)) {
            String handlerMessage = data.substring(56, 60);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER3;
            myHandler.sendMessage(msg);
        }


        if (commd.equalsIgnoreCase(Constant.INFRARED_RECV_COMMAND)) {

            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_INFRARED_HAS_ANSWER;
            myHandler.sendMessage(msg);

        }
        if (commd.equalsIgnoreCase(Constant.INFRARED_RECV3_COMMAND)) {
            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_INFRARED_HAS_ANSWER3;
            myHandler.sendMessage(msg);
        }


        //插座返回数据
        if (commd.equalsIgnoreCase(Constant.SOCKETS_RECV_COMMAND)) {
            String handlerMessage = data.substring(44, 46);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_SOCKETS_HAS_ANSWER;
            myHandler.sendMessage(msg);

        }

        if(commd.equalsIgnoreCase(Constant.SCENE_SETTING_RECV2_COMMAND)){
            Message msg = new Message();
            msg.obj = data;
            msg.what = Constant.HANDLER_SCENE_SETTING_HAS_ANSWER2;
            myHandler.sendMessage(msg);

        }

        //应用场景返回的信息
        if(commd.equalsIgnoreCase(Constant.SCENE_SETTING_RECV_COMMAND)){
            Message msg = new Message();
            msg.obj = data;
            msg.what = Constant.HANDLER_SCENE_SETTING_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }




        if (commd.equalsIgnoreCase(Constant.SOCKETS_RECV2_COMMAND)) {
            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Log.e(TAG,"rev mac = "+mac);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_SOCKETS_HAS_ANSWER2;
            myHandler.sendMessage(msg);
        }

        if (commd.equalsIgnoreCase(Constant.TEMP_PM25_RECV2_COMMAND)) {

            String handlerMessage = data.substring(56, 68);
            String mac = data.substring(28,44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_TEMP_PM25_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }
        if (commd.equalsIgnoreCase(Constant.SWITCH_RECV3_COMMAND)) {
            String handlerMessage = data.substring(56, 58);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_SWITCHS_HAS_ANSWER3;
            myHandler.sendMessage(msg);
        }

        if (commd.equalsIgnoreCase(Constant.SOCKETS_RECV3_COMMAND)) {
            String handlerMessage = data.substring(56, 58);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_SOCKETS_HAS_ANSWER3;
            myHandler.sendMessage(msg);
        }

        //光照
        if (commd.equalsIgnoreCase(Constant.LIGHT_SONSER_RECV2_COMMAND)) {
            String handlerMessage = data.substring(56, 60);
            String mac = data.substring(28,44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_LIGHT_SERSON_HAS_ANSWER2;
            myHandler.sendMessage(msg);
        }

        //噪音
        if (commd.equalsIgnoreCase(Constant.NOISE_SONSER_RECV2_COMMAND)) {
            String handlerMessage = data.substring(56, 60);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_NOISE_SERSON_HAS_ANSWER2;
            myHandler.sendMessage(msg);
        }

        if (commd.equalsIgnoreCase(Constant.TEMP_PM25_RECV3_COMMAND)) {
            String handlerMessage = data.substring(56, 68);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_TEMP_PM25_HAS_ANSWER3;
            myHandler.sendMessage(msg);
        }

        if (commd.equalsIgnoreCase(Constant.DOOR_MAGNET_RECV_COMMAND)) {

            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }

        if (commd.equalsIgnoreCase(Constant.DOOR_MAGNET_RECV2_COMMAND)) {
            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER2;
            myHandler.sendMessage(msg);
        }

        if (commd.equalsIgnoreCase(Constant.MODIFY_EQUIPMENT_NAME_RECV_COMMAND)) {
            String handlerMessage = data.substring(44, 46);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_MODIFY_EQUIPMENT_NAME_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }


        //窗帘
        if (commd.equalsIgnoreCase(Constant.CURTAINS_RECV_COMMAND)) {
            String handlerMessage = data.substring(44, 46);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_CURTAINS_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }


        //添加RFID信息回复
        if (commd.equalsIgnoreCase(Constant.ADD_RFID_INFO_RECV_COMMAND)) {
            String handlerMessage = data.substring(28,30);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_ADD_RFID_INFO_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }

        //删除RFID信息回复
        if (commd.equalsIgnoreCase(Constant.DEL_RFID_INFO_RECV_COMMAND)) {
            String handlerMessage = data.substring(28,30);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_DEL_RFID_INFO_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }

        if(commd.equalsIgnoreCase(Constant.WINDOW_RECV_COMMAND)){

            String handlerMessage = data.substring(60, 64);

            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_WINDOW_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }



    }

    /**
     * ui处理
     *
     * @param toggleButton1 第1个按钮
     * @param toggleButton2 第2个按钮
     * @param toggleButton3 第3个按钮
     */
    public void setSwitchUI(ToggleButton toggleButton1, ToggleButton toggleButton2, ToggleButton toggleButton3,String switchMac) {
        this.toggleButton1 = toggleButton1;
        this.toggleButton2 = toggleButton2;
        this.toggleButton3 = toggleButton3;
        this.switchMac = switchMac;

    }

    public void setSocketsUI(ToggleButton toggleButton,String socketMac) {
        this.toggleButton = toggleButton;
        this.socketMac = socketMac;
    }

    public void setInflammableGasUI(TextView tvInflammableGas,String inflammableGasMac) {
        this.tvInflammableGas = tvInflammableGas;
        this.inflammableGasMac = inflammableGasMac;
    }

    public void setInfraredTv(TextView tvInfrared,String infraredMac) {
        this.tvInfrared = tvInfrared;
        this.infraredMac = infraredMac;
    }


    public void setTempPm25Tv(TextView tvTemperature,TextView tvHumidity,TextView tvPm25,String tempPm25Mac) {
        this.tvTemperature = tvTemperature;
        this.tvHumidity= tvHumidity;
        this.tvPm25 = tvPm25;
        this.tempPm25Mac = tempPm25Mac;

    }

    public void setDoorMagnetUI(TextView tvDoorMagnet,String doorMagnetMac) {
        this.tvDoorMagnet = tvDoorMagnet;
        this.doorMagnetMac = doorMagnetMac;
    }

    public void setWindowUI(ToggleButton tbDoor,ToggleButton tbWindow){
        this.tbDoor = tbDoor;
        this.tbWindow = tbWindow;
    }



//    public void setCurtainsUI(ToggleButton tbWindows,ToggleButton tbCurtains){
//        this.tbWindows = tbWindows;
//        this.tbCurtains = tbCurtains;
//    }


    public void setCurtainsUI(ToggleButton tbCurtains){
        this.tbCurtains = tbCurtains;
    }
    public void setSingleCurtainsUI(ToggleButton tbSingleCurtains){
        this.tbSingleCurtains = tbSingleCurtains;
    }


    public void setNoiseSensorTv(TextView tvNoiseSensor,String noiseSensorMac){
        this.tvNoiseSensor = tvNoiseSensor;
        this.noiseSensorMac = noiseSensorMac;
    }

}
