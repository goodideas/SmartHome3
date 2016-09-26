package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
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
 * 智能插座
 */
public class SocketsActivity extends StatusActivity {

    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private EquipmentBean equipmentBean;
    private ToggleButton tbSockets;
    private Util util;


    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd DBcurd;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sockets);
        tbSockets = (ToggleButton)findViewById(R.id.tbSockets);
        util = new Util();
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
        DBcurd = new DBcurd(SocketsActivity.this);
        Intent intent = this.getIntent();
        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");
        Log.e("SocketsActivity", "equipmentBean=" + equipmentBean.getDevice_Type() + " " + equipmentBean.getShort_ADDR());
        udpHelper = UdpHelper.getInstance();
        udpHelper.setSocketsUI(tbSockets, equipmentBean.getMac_ADDR());
        Log.e("soketActivity","mac="+equipmentBean.getMac_ADDR());
        spHelper = new SpHelper(SocketsActivity.this);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), SocketsActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(getDataOfBeforeDo());
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
        if(!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {
//            String equipmentName =    new String(util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
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
                if(isModify){
                    btnModifyName.setText("完成");
                    etEquipmentName.setEnabled(true);
                }else{

                    if(etEquipmentName.getText().toString().length()>24){
//                        Toast.makeText(getApplicationContext(), "不要超过24位", Toast.LENGTH_SHORT).show();
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



    }

    private byte[] getDataOfBeforeDo(){
        byte[] data = new byte[25];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.SOCKETS_SEND2_COMMAND[0];
        data[11] = Constant.SOCKETS_SEND2_COMMAND[1];
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

    private byte[] getSocketsSendData(byte[] status){
        byte[] data = new byte[33];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        byte[] macByte = HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte,0,data,2,macByteLength);
        //命令类型
        data[10] = Constant.SOCKETS_SEND_COMMAND[0];
        data[11] = Constant.SOCKETS_SEND_COMMAND[1];

        //数据内容长度
        data[12] = (byte)0x10;
        data[13] = (byte)0x00;

        byte[] euipmentMacByte = util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int euipmentMacByteLength = macByte.length;
        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);


        byte[] shortAddr = HexString2Bytes(equipmentBean.getShort_ADDR()); //2个字节
        data[22] = shortAddr[0];
        data[23] = shortAddr[1];

        byte[] deviceType = HexString2Bytes(equipmentBean.getDevice_Type()); //4个字节
        data[24] = deviceType[0];
        data[25] = deviceType[1];
        data[26] = deviceType[2];
        data[27] = deviceType[3];

        data[28] = status[0];
        data[29] = status[1];

        String checkData = util.bytes2HexString(data, data.length);
        data[30] = util.checkData(checkData.substring(28, 60));//校验位

        data[31] = Constant.DATA_TAIL[0];
        data[32] = Constant.DATA_TAIL[1];
        Log.e("插座","发送的数据="+util.bytes2HexString(data,data.length));

        return data;
    }

    private byte[] HexString2Bytes(String hexString){

        int stringLength = hexString.length();
        byte[] data = new byte[(stringLength/2)];
        for(int i = 0,j = 0;i<data.length;i++,j=j+2)
        {
            data[i] = (byte)Integer.parseInt(hexString.substring(j,(j+2)), 16);
        }
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

//    @Override
//    protected void onDestroy() {
//        if(udpHelper!=null){
//            udpHelper.closeUdp();
//        }
//        super.onDestroy();
//    }
}

