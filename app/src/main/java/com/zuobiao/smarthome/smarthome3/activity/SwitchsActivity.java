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

/**
 * 面板开关
 */
public class SwitchsActivity extends StatusActivity {

    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private ToggleButton toggleButton3;
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private EquipmentBean equipmentBean;
    private static final String TAG = "SwitchsActivity";
    private byte state = (byte) 0x00;//保存当前的状态

    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd DBcurd;
    private ProgressDialog searchDialog;
    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;
    private MyHandler myHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switchs);

        btnModifyName = (Button)findViewById(R.id.btnModifyName);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentName);

        tvEquipmentShow = (TextView)findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button)findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = this.getIntent();
        equipmentBean = (EquipmentBean) intent.getSerializableExtra("equipmentBean");
        Log.e("SwitchsActivity", "equipmentBean=" + equipmentBean.getDevice_Type() + " " + equipmentBean.getShort_ADDR());
        DBcurd = new DBcurd(SwitchsActivity.this);
        myHandler = new MyHandler(getMainLooper());

        udpHelper = UdpHelper.getInstance();
        spHelper = new SpHelper(SwitchsActivity.this);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), SwitchsActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.SWITCHES_SEND2_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                //成功或者失败
                if (command.equalsIgnoreCase(Constant.SWITCH_RECEIVE_COMMAND)) {
                    String handlerMessage = data.substring(44, 46);
                    Message msg = new Message();
                    msg.obj = handlerMessage;
                    msg.what = Constant.HANDLER_SWITCHES_HAS_ANSWER;
                    myHandler.sendMessage(msg);
                }

                //面板开关人为去触摸时会发送指令过来
                //同步
                if (command.equalsIgnoreCase(Constant.SWITCH_RECEIVE_COMMAND2)
                        ||command.equalsIgnoreCase(Constant.SWITCH_RECEIVE_COMMAND3)) {
                    String handlerMessage = data.substring(56, 58);
                    String mac  = data.substring(28,44);
                    Message msg = new Message();
                    msg.obj = handlerMessage+mac;
                    msg.what = Constant.HANDLER_SWITCHES_HAS_ANSWER2;
                    myHandler.sendMessage(msg);
                }

            }
        });
        searchDialog = new ProgressDialog(this);
        searchDialog.setMessage("正在同步");
        searchDialog.onStart();
        searchDialog.show();
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase(Constant.EQUIPMENT_NAME_ALL_FF)) {

            String equipmentName = new String(Util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
            if(TextUtils.isEmpty(equipmentName)){
                etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            }else{
                etEquipmentName.setText(equipmentName);
            }
        }else{
            etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchDialog.dismiss();
            }
        }, Constant.BEFORE_INTO_SWITCH_MAX_TIME);

        clicks();

    }

    private void clicks() {
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte b = (byte) 0x01;
                if (isChecked) {
                    state |= b; //打开用 或运算
                    Log.e(TAG, "1开");
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                } else {
                    state ^= b;//关闭用 异或运算
                    Log.e(TAG, "1关");
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));

                }
            }
        });

        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte b = (byte) 0x02;
                if (isChecked) {
                    state |= b;
                    Log.e(TAG, "2开");
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                } else

                {
                    state ^= b;
                    Log.e(TAG, "2关");
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
            }
        }
    });


        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte b = (byte) 0x04;
                if (isChecked) {
                    state |= b;
                    Log.e(TAG,"3开");
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                } else {
                    state ^= b;
                    Log.e(TAG,"3关");
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                }
            }
        });


        btnModifyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isModify = !isModify;
                if (isModify) {
                    btnModifyName.setText("完成");
                    etEquipmentName.setEnabled(true);
                } else {

                    if (etEquipmentName.getText().toString().getBytes().length > 24) {
                        isModify = !isModify;
                        Util.showToast(getApplicationContext(), "不要超过24位");
                    } else {
                        btnModifyName.setText("修改设备名称");
                        etEquipmentName.setEnabled(false);
                        String modifyString = etEquipmentName.getText().toString();
                        udpHelper.setIsSend(true);
                        udpHelper.send(Util.getModifyData(modifyString, spHelper.getSpGateWayMac(),equipmentBean));
                        DBcurd.updataEquipmentName(Util.bytes2HexString(modifyString.getBytes(), modifyString.getBytes().length), equipmentBean.getMac_ADDR());
                    }
                }


            }
        });

    }

//    Short_ADDR ：该节点短地址
//    Device_Type ：见表
//    Switch_State：三个开关的状态，该字节的0位1位2位分别代表开关1开关2开关3。0代表断开，1代表闭合。
//  FF AA
//  B7 59 0B 7F CF 5C 00 00
//  21 00 10 00

//  2E 73 EA 08 00 4B 12 00
//  C4 EE
//  20 05 FF FE
//  01 00

    //  C5
//  FF 55
//  FFAA B7590B7FCF5C0000 2100 1000 2E73EA08004B1200 C4EE 2005FFFE 0001000000
    private byte[] getSwitchsSendData(byte[] states) {
        byte[] data = new byte[33];
        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        //命令类型
        data[10] = Constant.SWITCHES_SEND_COMMAND[0];
        data[11] = Constant.SWITCHES_SEND_COMMAND[1];

        //数据内容长度
        data[12] = (byte) 0x10;
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

        data[28] = states[0];
        data[29] = states[1];

        //28 - 60     32 16
        String checkData = Util.bytes2HexString(data, data.length);
        data[30] = Util.checkData(checkData.substring(28, 60));//校验位
        data[31] = Constant.DATA_TAIL[0];
        data[32] = Constant.DATA_TAIL[1];
        Log.e(TAG,"发送的数据="+Util.bytes2HexString(data,data.length));
        return data;
    }

    private void doWithTbState(int state) {
        udpHelper.setIsSend(false);
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
                udpHelper.setIsSend(true);
            }
        }, 100);
        Log.e(TAG, "switch-case end");
    }



    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //面板开关
            if (msg.what == Constant.HANDLER_SWITCHES_HAS_ANSWER) {

                String handlerMessage = (String) msg.obj;
                if (handlerMessage.equalsIgnoreCase("01")) {
                    Util.showToast(SwitchsActivity.this, "成功");
                }
                if (handlerMessage.equalsIgnoreCase("00")||handlerMessage.equalsIgnoreCase("FF")) {
                    Util.showToast(SwitchsActivity.this, "失败");
                }

            }

            //面板
            if (msg.what == Constant.HANDLER_SWITCHES_HAS_ANSWER2) {
                if(searchDialog!=null)
                searchDialog.dismiss();
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);
                if(mac.equalsIgnoreCase(equipmentBean.getMac_ADDR())){
                    int state = Integer.parseInt(stat, 16);
                    Log.e(TAG, "state1=" + state);
                    if (toggleButton1 != null && toggleButton2 != null && toggleButton3 != null)
                        doWithTbState(state);
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
