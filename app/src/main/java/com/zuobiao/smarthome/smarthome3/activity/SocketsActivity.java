package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.OnReceive;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

/**
 * 智能插座
 */
public class SocketsActivity extends StatusActivity {

    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private EquipmentBean equipmentBean;
    private ToggleButton tbSockets;

    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd DBcurd;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sockets);
        tbSockets = (ToggleButton)findViewById(R.id.tbSockets);
        btnModifyName = (Button)findViewById(R.id.btnModifyNameSockets);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameSockets);
        tvEquipmentShow = (TextView)findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button)findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myHandler = new MyHandler(getMainLooper());
        DBcurd = new DBcurd(SocketsActivity.this);
        Intent intent = this.getIntent();

        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");
        Log.e("SocketsActivity", "equipmentBean=" + equipmentBean.getDevice_Type() + " " + equipmentBean.getShort_ADDR());
        udpHelper = UdpHelper.getInstance();

        spHelper = new SpHelper(SocketsActivity.this);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), SocketsActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.SOCKETS_SEND2_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                //总共3种接收类型
                //第一种是开始同步时
                //第二种是人为按下开关时，返回的数据
                //第三种是手机操作时，返回的数据

                //插座返回数据，成功或者失败
                if (command.equalsIgnoreCase(Constant.SOCKETS_RECEIVE_COMMAND)) {
                    String handlerMessage = data.substring(44, 46);
                    Message msg = new Message();
                    msg.obj = handlerMessage;
                    msg.what = Constant.HANDLER_SOCKETS_HAS_ANSWER;
                    myHandler.sendMessage(msg);

                }
                if (command.equalsIgnoreCase(Constant.SOCKETS_RECEIVE_COMMAND2)
                        ||command.equalsIgnoreCase(Constant.SOCKETS_RECEIVE_COMMAND3)) {
                    String handlerMessage = data.substring(56, 58);
                    String mac = data.substring(28, 44);
                    Message msg = new Message();
                    msg.obj = handlerMessage+mac;
                    msg.what = Constant.HANDLER_SOCKETS_HAS_ANSWER2;
                    myHandler.sendMessage(msg);
                }

            }
        });
        tbSockets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    udpHelper.send(getSocketsSendData(new byte[]{(byte) 0x01, (byte) 0x00}));
                } else {
                    udpHelper.send(getSocketsSendData(new byte[]{(byte) 0x00, (byte) 0x00}));
                }
            }
        });
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase(Constant.EQUIPMENT_NAME_ALL_FF)) {
            String equipmentName = Util.hexString2Characters(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()));
            if(TextUtils.isEmpty(equipmentName)){
                etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            }else{
                etEquipmentName.setText(equipmentName);
            }
        }else{
            etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }

        btnModifyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isModify = !isModify;
                if (isModify) {
                    btnModifyName.setText("完成");
                    etEquipmentName.setEnabled(true);
                } else {

                    if (etEquipmentName.getText().toString().length() > 24) {
                        Util.showToast(getApplicationContext(), "不要超过24位");
                    } else {
                        btnModifyName.setText("修改设备名称");
                        etEquipmentName.setEnabled(false);
                        String modifyString = etEquipmentName.getText().toString();
                        udpHelper.setIsSend(true);
                        udpHelper.send(Util.getModifyData(modifyString, spHelper.getSpGateWayMac(), equipmentBean));
                        DBcurd.updataEquipmentName(Util.bytes2HexString(modifyString.getBytes(), modifyString.getBytes().length), equipmentBean.getMac_ADDR());
                    }
                }


            }
        });



    }


    private byte[] getSocketsSendData(byte[] status){
        byte[] data = new byte[33];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte,0,data,2,macByteLength);
        //命令类型
        data[10] = Constant.SOCKETS_SEND_COMMAND[0];
        data[11] = Constant.SOCKETS_SEND_COMMAND[1];

        //数据内容长度
        data[12] = (byte)0x10;
        data[13] = (byte)0x00;

        byte[] equipmentMacByte = Util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int equipmentMacByteLength = macByte.length;
        System.arraycopy(equipmentMacByte, 0, data, 14, equipmentMacByteLength);


        byte[] shortAddr = Util.HexString2Bytes(equipmentBean.getShort_ADDR()); //2个字节
        data[22] = shortAddr[0];
        data[23] = shortAddr[1];

        byte[] deviceType = Util.HexString2Bytes(equipmentBean.getDevice_Type()); //4个字节
        data[24] = deviceType[0];
        data[25] = deviceType[1];
        data[26] = deviceType[2];
        data[27] = deviceType[3];

        data[28] = status[0];
        data[29] = status[1];

        String checkData = Util.bytes2HexString(data, data.length);
        data[30] = Util.checkData(checkData.substring(28, 60));//校验位

        data[31] = Constant.DATA_TAIL[0];
        data[32] = Constant.DATA_TAIL[1];
        Log.e("插座","发送的数据="+Util.bytes2HexString(data,data.length));

        return data;
    }


    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {



            if (msg.what == Constant.HANDLER_SOCKETS_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(SocketsActivity.this, "成功");
                }
                if (handlerMessage.equalsIgnoreCase("00")) {
                    Util.showToast(SocketsActivity.this, "失败");
                }

                if (handlerMessage.equalsIgnoreCase("FF")) {
                    Util.showToast(SocketsActivity.this, "失败");
                }
            }


            //人为按下插座时返回的数据
            //同步
            if (msg.what == Constant.HANDLER_SOCKETS_HAS_ANSWER2) {
                String handlerMessage = (String) msg.obj;
                String data1 = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);

                if (data1.equalsIgnoreCase("01")&&mac.equalsIgnoreCase(equipmentBean.getMac_ADDR())) {
                    if (tbSockets != null) {
                        udpHelper.setIsSend(false);
                        tbSockets.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                udpHelper.setIsSend(true);
                            }
                        }, 100);

                    }

                }
                if (data1.equalsIgnoreCase("00")&&mac.equalsIgnoreCase(equipmentBean.getMac_ADDR())) {

                    if (tbSockets != null) {
                        udpHelper.setIsSend(false);
                        tbSockets.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                udpHelper.setIsSend(true);
                            }
                        }, 100);
                    }


                }
            }


        }

    }

    @Override
    protected void onDestroy() {
        if(udpHelper!=null){
            udpHelper.closeUdp();
        }
        super.onDestroy();
    }
}

