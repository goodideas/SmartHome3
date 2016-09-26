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
import android.widget.EditText;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.OnReceive;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

/**
 * 门磁
 */
public class DoorMagnetActivity extends StatusActivity {

    private TextView tvDoorMagnet;
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private EquipmentBean equipmentBean;

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
        setContentView(R.layout.activity_door_magnet);
        tvDoorMagnet = (TextView)findViewById(R.id.tvDoorMagnet);
        btnModifyName = (Button)findViewById(R.id.btnModifyNameDoorMagnet);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameDoorMagnet);
        myHandler = new MyHandler(getMainLooper());
        DBcurd = new DBcurd(DoorMagnetActivity.this);
        tvEquipmentShow = (TextView)findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button)findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = this.getIntent();
        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");
        Log.e("DoorMagnetActivity", "equipmentBean=" + equipmentBean.getDevice_Type() + " " + equipmentBean.getShort_ADDR());
        udpHelper = UdpHelper.getInstance();
        spHelper = new SpHelper(DoorMagnetActivity.this);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), DoorMagnetActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.DOOR_MAGNET_SEND_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                if (command.equalsIgnoreCase(Constant.DOOR_MAGNET_RECV_COMMAND)) {
                    String handlerMessage = data.substring(56, 58);
                    String mac = data.substring(28, 44);
                    Message msg = new Message();
                    msg.obj = handlerMessage + mac;
                    msg.what = Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER;
                    myHandler.sendMessage(msg);
                }
                if (command.equalsIgnoreCase(Constant.DOOR_MAGNET_RECV2_COMMAND)) {
                    String handlerMessage = data.substring(56, 58);
                    String mac = data.substring(28, 44);
                    Message msg = new Message();
                    msg.obj = handlerMessage + mac;
                    msg.what = Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER2;
                    myHandler.sendMessage(msg);
                }
            }
        });
//        udpHelper.setDoorMagnetUI(tvDoorMagnet,equipmentBean.getMac_ADDR());
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {
            String equipmentName = new String(Util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
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
                        udpHelper.send(Util.getModifyData(modifyString,spHelper.getSpGateWayMac(),equipmentBean));
                        DBcurd.updataEquipmentName(Util.bytes2HexString(modifyString.getBytes(), modifyString.getBytes().length), equipmentBean.getMac_ADDR());
                    }
                }


            }
        });

    }


    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.HANDLER_DOOR_MAGNET_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);

                if (tvDoorMagnet != null&&mac.equalsIgnoreCase(equipmentBean.getMac_ADDR())) {
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

                if (tvDoorMagnet != null&&mac.equalsIgnoreCase(equipmentBean.getMac_ADDR())) {
                    if (stat.equalsIgnoreCase("01")) {
                        tvDoorMagnet.setText("开");
                    }
                    if (stat.equalsIgnoreCase("00")) {
                        tvDoorMagnet.setText("关");
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
