package com.zuobiao.smarthome.smarthome3.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

public class WindowActivity extends StatusActivity {

    private static final String TAG = "WindowActivity";

    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private EquipmentBean equipmentBean;
    private Util util;

//    private Button btnWindowOpen;
//    private Button btnWindowClose;
    private ToggleButton tbDoor;
    private ToggleButton tbWindows;


    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd DBcurd;


    private byte statusDoor  = (byte)0x00;
    private byte statusWindows  = (byte)0x00;

    private ProgressDialog searchDialog;
    private Handler handler;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);

        util = new Util();
        btnModifyName = (Button)findViewById(R.id.btnModifyNameWindow);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameWindow);
//        btnWindowOpen = (Button)findViewById(R.id.btnWindowOpen);
//        btnWindowClose = (Button)findViewById(R.id.btnWindowClose);
        tbDoor = (ToggleButton)findViewById(R.id.tbDoor);
        tbWindows = (ToggleButton)findViewById(R.id.tbWindows);
        tvEquipmentShow = (TextView)findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button)findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler = new Handler();

        DBcurd = new DBcurd(WindowActivity.this);
        Intent intent = this.getIntent();
        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");
        udpHelper = UdpHelper.getInstance();
        spHelper = new SpHelper(WindowActivity.this);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), WindowActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.setWindowUI(tbDoor, tbWindows);
        udpHelper.send(getDataOfBeforeDo());
        searchDialog = new ProgressDialog(this);
        searchDialog.setMessage("");
        searchDialog.onStart();
        searchDialog.show();
        udpHelper.doWindow(Constant.BEFORE_INTO_WINDOW_MAX_TIME);


//        btnWindowOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                udpHelper.send(getWindowSendData((byte) 0x01));
//            }
//        });
//
//        btnWindowClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                udpHelper.send(getWindowSendData((byte) 0x00));
//            }
//        });

        tbDoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    statusDoor = (byte) 0x01;
                    udpHelper.send(getWindowSendData(statusDoor,statusWindows));
                }else{
                    statusDoor = (byte) 0x00;
                    udpHelper.send(getWindowSendData(statusDoor,statusWindows));
                }
            }
        });

        tbWindows.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    statusWindows = (byte) 0x01;
                    udpHelper.send(getWindowSendData(statusDoor,statusWindows));
                }else{
                    statusWindows = (byte) 0x00;
                    udpHelper.send(getWindowSendData(statusDoor,statusWindows));
                }
            }
        });
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {

            String equipmentName = new String(util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
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
                if(isModify){
                    btnModifyName.setText("完成");
                    etEquipmentName.setEnabled(true);
                }else{

                    if(etEquipmentName.getText().toString().length()>24){
                        Util.showToast(getApplicationContext(), "不要超过24位");
                    }else {
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                listens = true;
                searchDialog.dismiss();
            }
        }, 1000);


    }

    private byte[] getWindowSendData(byte status,byte status2){
        byte[] data = new byte[17+19];
        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte,0,data,2,macByteLength);
        //命令类型
        data[10] = Constant.CURITAINS_SEND_COMMAND[0];
        data[11] = Constant.CURITAINS_SEND_COMMAND[1];

        //数据内容长度
        data[12] = (byte)0x13; //31
        data[13] = (byte)0x00;

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

        //不同的地方

        data[28] = (byte)0x00; //inputData
        data[29] = (byte)0x00; //inputData

        data[30] = status; //outData
        data[31] = status2; //outData

        data[32] = (byte)0x00;

        String checkData = util.bytes2HexString(data, data.length);
        data[33] = util.checkData(checkData.substring(28, 64));//校验位

        data[34] = Constant.DATA_TAIL[0];
        data[35] = Constant.DATA_TAIL[1];

        return data;
    }

    private byte[] getModifyData(String equipmentName){
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
        System.arraycopy(etNameByte, 0, data, 24, etNameByteLength);

        String checkData = util.bytes2HexString(data, data.length);
        data[48] = util.checkData(checkData.substring(28, 96));//校验位
        data[49] = Constant.DATA_TAIL[0];
        data[50] = Constant.DATA_TAIL[1];
//        发送的修改的数据 FFAA B7590B7FCF5C0000 0500 2200 2E73EA08004B1200 C4EE 6162636465666768696A6B6C6D6E00000000000000000000 4B FF55

        return data;

    }

    private byte[] getDataOfBeforeDo() {
        byte[] data = new byte[25];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.WINDOW_SEND_COMMAND[0];
        data[11] = Constant.WINDOW_SEND_COMMAND[1];
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



    @Override
    protected void onDestroy() {
        if(udpHelper!=null){
            udpHelper.closeUdp();
        }
        super.onDestroy();
    }


}
