package com.zuobiao.smarthome.smarthome3.util;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

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
public class UdpHelper {

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
    private DBcurd DBcurd;
    private OnReceive onReceive;
    private ProgressDialog searchDialog;

    private Runnable runnable;
    private Runnable WelcomeRunnable;
    private Runnable refreshRunnable;
    private Runnable addRunnable;
    private boolean searchGateWayFlag = false; //主要判断是欢迎界面的查找网关还是添加界面的查找网关
    private int addEquipmentNumber;
    private int countEquipment;

    private boolean isSend = true;
    private Context mContext;


    private static UdpHelper UdpInstance = new UdpHelper();

    /**
     * 静态单例
     * @return 单例
     */
    public static UdpHelper getInstance() {
        if (UdpInstance == null) {
            UdpInstance = new UdpHelper();
            Log.e(TAG, "getInstance");
        }
        return UdpInstance;
    }

    private UdpHelper() {
    }



    public void startUdpWithIp(String ip, Context context) {
        Log.e(TAG, "startUdpWithIp ip=" + ip);
        this.udpIp = ip;
        myHandler = new MyHandler();
        spHelper = new SpHelper(context);
        DBcurd = new DBcurd(context);
        startUdp();
        mContext = context;
        getDataFlag = false;
    }


    private void startUdp() {
        Log.e(TAG, "startUdp PORT=" + PORT);
        try {
            if (udpIp != null) {
                inetAddress = InetAddress.getByName(udpIp);
                udpSocket = new DatagramSocket(PORT+1);
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
                DBcurd.delALL();
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



            if(msg.what == Constant.HANDLER_SCENE_SETTING_HAS_ANSWER){
                String handlerMessage = (String) msg.obj;
                String status = handlerMessage.substring(28,30);
                if (status.equalsIgnoreCase("01")) {
                    Util.showToast(mContext, "成功");
                }else if (status.equalsIgnoreCase("00")) {
                    Util.showToast(mContext, "失败");
                }
            }



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
                    DBcurd.addSceneAllData(sceneEquipmentBean);
                }
                DBcurd.addSceneNameData(sceneNameString, equipmentCount);
                }else{
                    Util.showToast(mContext,"没有场景数据！");
                }
            }


        }
    }


    /**
     * 欢迎界面的查找网关
     *
     * @param maxTime 等待的最大时间 单位毫秒
     */
    public void doSearchGateWayOnWelcome(int maxTime) {
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
        myHandler.postDelayed(runnable, Constant.SEARCH_GATEWAY_WAIT_MAX_TIME);
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
                                doWithDataByCommand(data.substring(20, 24), data, ip);
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
     * @param command
     * @param data
     * @param ip
     */
    private void doWithDataByCommand(String command, String data, String ip) {

       //数据接收接口
        if(onReceive!=null){
            onReceive.receive(command, data, ip);
        }

        //网关
        if (command.equalsIgnoreCase(Constant.GATEWAY_RECV_COMMAND)) {
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
        if (command.equalsIgnoreCase(Constant.REFRESH_EQUIPMENT_RECV_COMMAND)) {
            //解析数据
            addEquipmentNumber = Integer.parseInt(data.substring(28, 30), 16);
            Log.e("udpHelper", "将要来的number=" + addEquipmentNumber);
            myHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_EQUIPMENT_GATE_DATA);

        }

        //n个设备包 03 10
        if (command.equalsIgnoreCase(Constant.REFRESH_EQUIPMENT_RECV2_COMMAND)) {
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
            DBcurd.addData(equipmentBean);
            myHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_EQUIPMENT_ADD_DATA);
        }

        //单独添加设备
        if (command.equalsIgnoreCase(Constant.ADD_EQUIPMENT_RECV_COMMAND)) {
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
            DBcurd.addData(equipmentBean);
            myHandler.sendEmptyMessage(Constant.HANDLER_ADD_EQUIPMENT_ADD_DATA);

        }


        //光照
        if (command.equalsIgnoreCase(Constant.LIGHT_SENSOR_RECEIVE_COMMAND)) {
            if (Constant.LIGHT_SENSOR.equalsIgnoreCase(data.substring(48, 56))) {
                String mac = data.substring(28, 44);
                String handlerMessage = data.substring(56, 60);
                Message msg = new Message();
                msg.obj = handlerMessage+mac;
                msg.what = Constant.HANDLER_LIGHT_SENSOR_HAS_ANSWER;
                myHandler.sendMessage(msg);
            }
        }


        if (command.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECEIVE_COMMAND)) {
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

        if (command.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECEIVE_COMMAND2)) {
            String handlerMessage = data.substring(56, 60);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER2;
            myHandler.sendMessage(msg);
        }


        if (command.equalsIgnoreCase(Constant.INFRARED_RECEIVE_COMMAND)) {

            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_INFRARED_HAS_ANSWER;
            myHandler.sendMessage(msg);

        }
        if (command.equalsIgnoreCase(Constant.INFRARED_RECEIVE_COMMAND2)) {
            String handlerMessage = data.substring(56, 58);
            String mac = data.substring(28, 44);
            Message msg = new Message();
            msg.obj = handlerMessage+mac;
            msg.what = Constant.HANDLER_INFRARED_HAS_ANSWER3;
            myHandler.sendMessage(msg);
        }




        if(command.equalsIgnoreCase(Constant.SCENE_SETTING_RECEIVE_COMMAND2)){
            Message msg = new Message();
            msg.obj = data;
            msg.what = Constant.HANDLER_SCENE_SETTING_HAS_ANSWER2;
            myHandler.sendMessage(msg);

        }

        //应用场景返回的信息
        if(command.equalsIgnoreCase(Constant.SCENE_SETTING_RECEIVE_COMMAND)){
            Message msg = new Message();
            msg.obj = data;
            msg.what = Constant.HANDLER_SCENE_SETTING_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }


//        FFAA 1E13007FCF5C0000 1210 0900 3F1AEA08004B1200 01 A9 FF55
        if (command.equalsIgnoreCase(Constant.MODIFY_EQUIPMENT_NAME_RECV_COMMAND)) {
            String handlerMessage = data.substring(44, 46);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_MODIFY_EQUIPMENT_NAME_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }



        //添加RFID信息回复
        if (command.equalsIgnoreCase(Constant.ADD_RFID_INFO_RECV_COMMAND)) {
            String handlerMessage = data.substring(28,30);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_ADD_RFID_INFO_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }

        //删除RFID信息回复
        if (command.equalsIgnoreCase(Constant.DEL_RFID_INFO_RECV_COMMAND)) {
            String handlerMessage = data.substring(28,30);
            Message msg = new Message();
            msg.obj = handlerMessage;
            msg.what = Constant.HANDLER_DEL_RFID_INFO_HAS_ANSWER;
            myHandler.sendMessage(msg);
        }



    }

    /**
     * 接收接口 方便后面要添加设备时，可以直接调用此接口
     * @param receive 接收接口
     */
    public void setOnReceive(OnReceive receive){
        this.onReceive = receive;
    }



}
