package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

/**
 * 人体红外
 */
public class InfraredActivity extends StatusActivity {

    private TextView tvInfrared;
    private static final String TAG = "InfraredActivity";
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnInfraredRefreSh;
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
        setContentView(R.layout.activity_infrared);
        tvInfrared = (TextView)findViewById(R.id.tvInfrared);
        btnInfraredRefreSh = (Button)findViewById(R.id.btnInfraredRefreSh);
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
        btnModifyName = (Button)findViewById(R.id.btnModifyNameInfrared);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameInfrared);

        dBcurd = new DBcurd(InfraredActivity.this);
        spHelper = new SpHelper(InfraredActivity.this);
        udpHelper = UdpHelper.getInstance();
        udpHelper.setInfraredTv(tvInfrared,equipmentBean.getMac_ADDR());
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), InfraredActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(getDataOfBeforeDo());
        btnInfraredRefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpHelper.setIsSend(true);
                udpHelper.send(getDataOfBeforeDo());
            }
        });
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if(!dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {
//            etEquipmentName.setText(new String(util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim());
            String equipmentName = new String(util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
//            etEquipmentName.setText(new String(util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim());
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
        data[10] = Constant.INFRARED_SEND_COMMAND[0];
        data[11] = Constant.INFRARED_SEND_COMMAND[1];
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
