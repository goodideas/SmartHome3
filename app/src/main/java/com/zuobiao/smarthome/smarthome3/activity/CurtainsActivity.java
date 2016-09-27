package com.zuobiao.smarthome.smarthome3.activity;

import android.app.ProgressDialog;
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

public class CurtainsActivity extends StatusActivity {


    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private EquipmentBean equipmentBean;

    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd DBcurd;
    private byte statusCurtains = (byte) 0x00;
    private byte statusWindow = (byte) 0x00;

    private ToggleButton tbCurtains;
    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;
    private ProgressDialog searchDialog;
    private MyHandler myHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtains);
        btnModifyName = (Button) findViewById(R.id.btnModifyNameCurtains);
        etEquipmentName = (EditText) findViewById(R.id.etEquipmentNameCurtains);
        tbCurtains = (ToggleButton) findViewById(R.id.tbCurtains);

        tvEquipmentShow = (TextView) findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button) findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myHandler = new MyHandler(getMainLooper());

        DBcurd = new DBcurd(CurtainsActivity.this);
        Intent intent = this.getIntent();
        equipmentBean = (EquipmentBean) intent.getSerializableExtra("equipmentBean");
        Log.e("SocketsActivity", "equipmentBean=" + equipmentBean.getDevice_Type() + " " + equipmentBean.getShort_ADDR());
        spHelper = new SpHelper(CurtainsActivity.this);
        udpHelper = UdpHelper.getInstance();
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), CurtainsActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.WINDOW_SEND_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {

                if (command.equalsIgnoreCase(Constant.WINDOW_RECV_COMMAND)) {
                    String handlerMessage = data.substring(60, 64);
                    Message msg = new Message();
                    msg.obj = handlerMessage;
                    msg.what = Constant.HANDLER_WINDOW_HAS_ANSWER;
                    myHandler.sendMessage(msg);
                }

                //窗帘
                if (command.equalsIgnoreCase(Constant.CURTAINS_RECEIVE_COMMAND)) {
                    String handlerMessage = data.substring(44, 46);
                    Message msg = new Message();
                    msg.obj = handlerMessage;
                    msg.what = Constant.HANDLER_CURTAINS_HAS_ANSWER;
                    myHandler.sendMessage(msg);
                }
            }
        });

        searchDialog = new ProgressDialog(this);
        searchDialog.setMessage("正在同步");
        searchDialog.onStart();
        searchDialog.show();

        tbCurtains.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    statusCurtains = (byte) 0x01;
                    udpHelper.send(getCurtainsSendData(statusCurtains, statusWindow));
                } else {
                    statusCurtains = (byte) 0x00;
                    udpHelper.send(getCurtainsSendData(statusCurtains, statusWindow));
                }
            }
        });


        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if (!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase(Constant.EQUIPMENT_NAME_ALL_FF)) {

            String equipmentName = new String(Util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
            if (TextUtils.isEmpty(equipmentName)) {
                etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            } else {
                etEquipmentName.setText(equipmentName);
            }

        } else {
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
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
            }
        }, Constant.BEFORE_INTO_CURTAINS_MAX_TIME);


    }


    private byte[] getCurtainsSendData(byte status, byte status2) {
        byte[] data = new byte[17 + 19];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        //命令类型
        data[10] = Constant.CURTAINS_SEND_COMMAND[0];
        data[11] = Constant.CURTAINS_SEND_COMMAND[1];

        //数据内容长度
        data[12] = (byte) 0x13; //19长度
        data[13] = (byte) 0x00;

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

        //不同的地方

        data[28] = (byte) 0x00; //inputData
        data[29] = (byte) 0x00; //inputData

        data[30] = status; //outData
        data[31] = status2; //outData

        data[32] = (byte) 0x00;

        String checkData = Util.bytes2HexString(data, data.length);
        data[33] = Util.checkData(checkData.substring(28, 64));//校验位

        data[34] = Constant.DATA_TAIL[0];
        data[35] = Constant.DATA_TAIL[1];

        return data;
    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.HANDLER_WINDOW_HAS_ANSWER) {
                if(searchDialog!=null){
                    searchDialog.dismiss();
                }
                String handlerMessage = (String) msg.obj;
                String doors = handlerMessage.substring(0, 2);
                if (doors.equalsIgnoreCase("00")) {
                    if (tbCurtains != null&&udpHelper!=null) {
                        udpHelper.setIsSend(false);
                        tbCurtains.setChecked(false);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                udpHelper.setIsSend(true);
                            }
                        }, 100);

                    }

                } else if (doors.equalsIgnoreCase("01")) {
                    if (tbCurtains != null&&udpHelper!=null) {
                        udpHelper.setIsSend(false);
                        tbCurtains.setChecked(true);
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                udpHelper.setIsSend(true);
                            }
                        }, 100);

                    }

                }
            }

            //窗帘
            if (msg.what == Constant.HANDLER_CURTAINS_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(CurtainsActivity.this, "成功！");
                }
                if (handlerMessage.equalsIgnoreCase("00")) {
                    Util.showToast(CurtainsActivity.this, "失败！");
                }

                if (handlerMessage.equalsIgnoreCase("FF")) {
                    Util.showToast(CurtainsActivity.this, "失败！");
                }
            }
        }

    }


    @Override
    protected void onDestroy() {
        if (udpHelper != null) {
            udpHelper.closeUdp();
        }
        super.onDestroy();
    }

}