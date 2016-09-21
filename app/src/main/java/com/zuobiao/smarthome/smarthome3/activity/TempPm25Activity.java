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
 * 温湿度、PM2.5
 */
public class TempPm25Activity extends StatusActivity {

    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvPm25;

    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnTempPm25RefreSh;
    private EquipmentBean equipmentBean;


    private Button btnModifyName;
    private EditText etEquipmentName;
    private boolean isModify = false;
    private DBcurd dBcurd;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_pm25);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvPm25 = (TextView) findViewById(R.id.tvPm25);
        btnTempPm25RefreSh = (Button) findViewById(R.id.btnTempPm25RefreSh);
        tvEquipmentShow = (TextView) findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button) findViewById(R.id.btnEquipmentTitleBarBack);
        btnModifyName = (Button) findViewById(R.id.btnModifyNameTempPm25);
        etEquipmentName = (EditText) findViewById(R.id.etEquipmentNameTempPm25);
        dBcurd = new DBcurd(TempPm25Activity.this);
        spHelper = new SpHelper(TempPm25Activity.this);
        udpHelper = UdpHelper.getInstance();

        Intent intent = this.getIntent();
        equipmentBean = (EquipmentBean) intent.getSerializableExtra("equipmentBean");


        if(!TextUtils.isEmpty(spHelper.getSpTemp())&&!TextUtils.isEmpty(spHelper.getSpHumidity())&&!TextUtils.isEmpty(spHelper.getSpPm25())){
            tvTemperature.setText(spHelper.getSpTemp());
            tvHumidity.setText(spHelper.getSpHumidity());
            tvPm25.setText(spHelper.getSpPm25());
        }

        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        udpHelper.setTempPm25Tv(tvTemperature, tvHumidity, tvPm25,equipmentBean.getMac_ADDR());
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), TempPm25Activity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(getDataOfBeforeDo());
        btnTempPm25RefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (udpHelper != null) {
                    udpHelper.setIsSend(true);
                    udpHelper.send(getDataOfBeforeDo());
                }
            }
        });
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if (!dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) {
            String equipmentName = new String(Util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
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
                        udpHelper.send(getModifyData(modifyString));
                        dBcurd.updataEquipmentName(Util.bytes2HexString(modifyString.getBytes(), modifyString.getBytes().length), equipmentBean.getMac_ADDR());
                    }
                }


            }
        });


    }

    private byte[] getDataOfBeforeDo() {
        byte[] data = new byte[25];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.TEMP_PM25_SEND2_COMMAND[0];
        data[11] = Constant.TEMP_PM25_SEND2_COMMAND[1];
//数据内容长度
        data[12] = (byte) 0x08;
        data[13] = (byte) 0x00;
        byte[] euipmentMacByte = Util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int euipmentMacByteLength = macByte.length;
        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);
        String checkData = Util.bytes2HexString(data, data.length);

        data[22] = Util.checkData(checkData.substring(28, 44));//校验位
        data[23] = Constant.DATA_TAIL[0];
        data[24] = Constant.DATA_TAIL[1];
        return data;
    }

    private byte[] getModifyData(String equipmentName) {
        byte[] data = new byte[51];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = Constant.MODEFY_EQUIPMENT_NAME_SEND_COMMAND[0];
        data[11] = Constant.MODEFY_EQUIPMENT_NAME_SEND_COMMAND[1];
        //数据内容长度
        data[12] = (byte) 0x22;
        data[13] = (byte) 0x00;

        byte[] euipmentMacByte = Util.HexString2Bytes(equipmentBean.getMac_ADDR());
        int euipmentMacByteLength = euipmentMacByte.length;
        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);

        byte[] equipmentShorMacByte = Util.HexString2Bytes(equipmentBean.getShort_ADDR());
        int equipmentShorMacByteLength = equipmentShorMacByte.length;
        System.arraycopy(equipmentShorMacByte, 0, data, 22, equipmentShorMacByteLength);

        byte[] etNameByte = equipmentName.getBytes();
        int etNameByteLength = etNameByte.length;
        System.arraycopy(etNameByte, 0, data, 24, etNameByteLength);

        String checkData = Util.bytes2HexString(data, data.length);
        data[48] = Util.checkData(checkData.substring(28, 96));//校验位
        data[49] = Constant.DATA_TAIL[0];
        data[50] = Constant.DATA_TAIL[1];
        return data;

    }


}
