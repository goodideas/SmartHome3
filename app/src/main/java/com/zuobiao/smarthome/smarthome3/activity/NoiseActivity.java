package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

public class NoiseActivity extends StatusActivity {
    private static final String TAG = "NoiseActivity";
    private TextView tvNoiseSensor;
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnNoiseSensorRefreSh;
    private EquipmentBean equipmentBean;
    private Util util;

    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd dBcurd;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise);
        tvNoiseSensor = (TextView)findViewById(R.id.tvNoiseSensor);
        btnNoiseSensorRefreSh = (Button)findViewById(R.id.btnNoiseSensorRefreSh);
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
        util = new Util();

        btnModifyName = (Button)findViewById(R.id.btnModifyNameNoiseSensor);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameNoiseSensor);

        dBcurd = new DBcurd(NoiseActivity.this);
        spHelper = new SpHelper(NoiseActivity.this);
        udpHelper = UdpHelper.getInstance();
        if(!TextUtils.isEmpty(spHelper.getSpNoiseSensor())){
            tvNoiseSensor.setText("音量 ：" + spHelper.getSpNoiseSensor() + "db");
        }

        udpHelper.setNoiseSensorTv(tvNoiseSensor);
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), NoiseActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(getDataOfBeforeDo());
        btnNoiseSensorRefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (udpHelper != null) {
                    udpHelper.setIsSend(true);
                    udpHelper.send(getDataOfBeforeDo());
                } else {
                    Log.e(TAG, "==null");
                }
            }
        });
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {
            String equipmentName = new String(Util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
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
                        dBcurd.updataEquipmentName(Util.bytes2HexString(modifyString.getBytes(), modifyString.getBytes().length), equipmentBean.getMac_ADDR());
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
        data[10] = Constant.NOISE_SENSOR_SEND2_COMMAND[0];
        data[11] = Constant.NOISE_SENSOR_SEND2_COMMAND[1];
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

}
