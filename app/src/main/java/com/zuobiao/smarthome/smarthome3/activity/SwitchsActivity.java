package com.zuobiao.smarthome.smarthome3.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    private Util util;
    private static final String TAG = "SwitchsActivity";
    private byte state = (byte) 0x00;//保存当前的状态
    private Handler handler;

    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd DBcurd;
//    private boolean listens = false;
    private ProgressDialog searchDialog;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;

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
        util = new Util();
        DBcurd = new DBcurd(SwitchsActivity.this);
        handler = new Handler();

        udpHelper = UdpHelper.getInstance();
        spHelper = new SpHelper(SwitchsActivity.this);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), SwitchsActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(getDataOfBeforeDo());

        searchDialog = new ProgressDialog(this);
        searchDialog.setMessage("");
        searchDialog.onStart();
        searchDialog.show();
        udpHelper.doSwitchs(Constant.BEFORE_INTO_SWITCH_MAX_TIME);
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        udpHelper.setSwitchUI(toggleButton1, toggleButton2, toggleButton3,equipmentBean.getMac_ADDR());
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {

            String equipmentName = new String(Util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
            if(TextUtils.isEmpty(equipmentName)){
                etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            }else{
                etEquipmentName.setText(equipmentName);
            }
//            etEquipmentName.setText(new String(util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).substring(4))).trim());
//            etEquipmentName.setText(new String(util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim());
        }else{
            etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                listens = true;
                searchDialog.dismiss();
            }
        }, 1000);

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
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(listens) {
//                                udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
//                            }
//                        }
//                    }, 100);

                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                } else {
                    state ^= b;//关闭用 异或运算
                    Log.e(TAG, "1关");
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(listens){
//                            udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
//                            }
//                        }
//                    }, 100);
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
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (listens) {
//                                udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
//                            }
//                        }
//                    }, 100);
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                } else

                {
                    state ^= b;
                    Log.e(TAG, "2关");
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (listens){
//                                udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
//                        }
//                    }
//                },100);
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
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(listens){
//                                udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
//                            }
//
//                        }
//                    }, 100);
                    udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
                } else {
                    state ^= b;
                    Log.e(TAG,"3关");
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(listens){
//                                udpHelper.send(getSwitchsSendData(new byte[]{state, (byte) 0x00}));
//                            }
//
//                        }
//                    }, 100);
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
//                        Toast.makeText(getApplicationContext(), "不要超过24位", Toast.LENGTH_SHORT).show();
                        Util.showToast(getApplicationContext(), "不要超过24位");
                    } else {
                        btnModifyName.setText("修改设备名称");
                        etEquipmentName.setEnabled(false);
                        String modifyString = etEquipmentName.getText().toString();
                        udpHelper.setIsSend(true);
                        udpHelper.send(getModifyData(modifyString));
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

        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        //命令类型
        data[10] = Constant.SWITCHS_SEND_COMMAND[0];
        data[11] = Constant.SWITCHS_SEND_COMMAND[1];

        //数据内容长度
        data[12] = (byte) 0x10;
        data[13] = (byte) 0x00;

        byte[] euipmentMacByte = util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int euipmentMacByteLength = macByte.length;
        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);


        byte[] shortAddr = util.HexString2Bytes(equipmentBean.getShort_ADDR()); //2个字节
        data[22] = shortAddr[0];
        data[23] = shortAddr[1];

        byte[] deviceType = util.HexString2Bytes(equipmentBean.getDevice_Type()); //4个字节
        data[24] = deviceType[0];
        data[25] = deviceType[1];
        data[26] = deviceType[2];
        data[27] = deviceType[3];

        data[28] = states[0];
        data[29] = states[1];

        //28 - 60     32 16
        String checkData = util.bytes2HexString(data, data.length);
        data[30] = util.checkData(checkData.substring(28, 60));//校验位
        data[31] = Constant.DATA_TAIL[0];
        data[32] = Constant.DATA_TAIL[1];
        Log.e(TAG,"发送的数据="+util.bytes2HexString(data,data.length));
        return data;
    }

//    FF AA B7 59 0B 7F CF 5C 00 00 20 00 08 00 2E 73 EA 08 00 4B 12 00 F0 FF 55
    private byte[] getDataOfBeforeDo() {
        byte[] data = new byte[25];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.SWITCHS_SEND2_COMMAND[0];
        data[11] = Constant.SWITCHS_SEND2_COMMAND[1];
        //数据内容长度
        data[12] = (byte) 0x08;
        data[13] = (byte) 0x00;
        byte[] euipmentMacByte = util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int euipmentMacByteLength = macByte.length;
        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);
        String checkData = util.bytes2HexString(data, data.length);

        data[22] = util.checkData(checkData.substring(28, 44));//校验位
        data[23] = Constant.DATA_TAIL[0];
        data[24] = Constant.DATA_TAIL[1];
        return data;
    }


    private byte[] getModifyData(String equipmentName){
//        设备号：   网关的MAC地址
//        命令类型： 主机发送命令： 0x0005
//        数据长度： 34字节
//        数据内容：
//        设备的MAC地址：  8字节
//        设备的短地址：	 2字节
//        设备的名称：		 24字节

        byte[] data = new byte[51];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.MODEFY_EQUIPMENT_NAME_SEND_COMMAND[0];
        data[11] = Constant.MODEFY_EQUIPMENT_NAME_SEND_COMMAND[1];
        //数据内容长度
        data[12] = (byte) 0x22;
        data[13] = (byte) 0x00;

        byte[] euipmentMacByte = util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int euipmentMacByteLength = euipmentMacByte.length;
        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);

        byte[] equipmentShorMacByte = util.HexString2Bytes(equipmentBean.getShort_ADDR());
        int equipmentShorMacByteLength = equipmentShorMacByte.length;
        System.arraycopy(equipmentShorMacByte, 0, data, 22, equipmentShorMacByteLength);

        byte[] etNameByte = equipmentName.getBytes();
        int etNameByteLength = etNameByte.length;

        Log.e(TAG,"输入框的String "+equipmentName+" 16进制字符串 "+util.bytes2HexString(etNameByte,etNameByte.length)+" 再转为字符串 "+new String(util.HexString2Bytes(util.bytes2HexString(etNameByte, etNameByte.length).trim())));
        System.arraycopy(etNameByte, 0, data, 24, etNameByteLength);

        String checkData = util.bytes2HexString(data, data.length);
        data[48] = util.checkData(checkData.substring(28, 96));//校验位
        data[49] = Constant.DATA_TAIL[0];
        data[50] = Constant.DATA_TAIL[1];
//        发送的修改的数据 FFAA B7590B7FCF5C0000 0500 2200 2E73EA08004B1200 C4EE 6162636465666768696A6B6C6D6E00000000000000000000 4B FF55
        Log.e(TAG,"发送的修改的数据"+util.bytes2HexString(data,data.length));
        return data;

    }

    @Override
    protected void onDestroy() {
        if(udpHelper!=null){
            udpHelper.closeUdp();
        }
        super.onDestroy();
    }
}
