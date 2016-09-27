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
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.OnReceive;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

/**
 * 光照传感器
 */
public class LightSensorActivity extends StatusActivity {
    private static final String TAG = "LightSensorActivity";
    private TextView tvLightSensor;
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnLightSensorRefreSh;
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
        setContentView(R.layout.activity_light_sensor);
        tvLightSensor = (TextView)findViewById(R.id.tvLightSensor);
        btnLightSensorRefreSh = (Button)findViewById(R.id.btnLightSensorRefreSh);
        tvEquipmentShow = (TextView)findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button)findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myHandler = new MyHandler(getMainLooper());
        Intent intent = this.getIntent();
        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");

        btnModifyName = (Button)findViewById(R.id.btnModifyNameLightSensor);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameLightSensor);

        DBcurd = new DBcurd(LightSensorActivity.this);
        spHelper = new SpHelper(LightSensorActivity.this);
        udpHelper = UdpHelper.getInstance();
        if(!TextUtils.isEmpty(spHelper.getSpLightSensor())){
            tvLightSensor.setText("光照强度 ：" + spHelper.getSpLightSensor() + "lux");
        }

        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), LightSensorActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.LIGHT_SENSOR_SEND2_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                if (command.equalsIgnoreCase(Constant.LIGHT_SENSOR_RECEIVE_COMMAND)
                        ||command.equalsIgnoreCase(Constant.LIGHT_SENSOR_RECEIVE_COMMAND2)) {
                    if (Constant.LIGHT_SENSOR.equalsIgnoreCase(data.substring(48, 56))) {
                        String mac = data.substring(28, 44);
                        String handlerMessage = data.substring(56, 60);
                        Message msg = new Message();
                        msg.obj = handlerMessage+mac;
                        msg.what = Constant.HANDLER_LIGHT_SENSOR_HAS_ANSWER;
                        myHandler.sendMessage(msg);
                    }
                }

            }
        });

        btnLightSensorRefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (udpHelper != null) {
                    udpHelper.setIsSend(true);
                    udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.LIGHT_SENSOR_SEND2_COMMAND, equipmentBean));
                } else {
                    Log.e(TAG, "==null");
                }
            }
        });
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
                        udpHelper.send(Util.getModifyData(modifyString, spHelper.getSpGateWayMac(),equipmentBean));
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

            if (msg.what == Constant.HANDLER_LIGHT_SENSOR_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvLightSensor != null&&mac.equals(equipmentBean.getMac_ADDR())){
                    int lightSensor = high + low * 256;
                    if(lightSensor == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpLightSensor())){
                            if(Integer.parseInt(spHelper.getSpLightSensor())==0){
                                tvLightSensor.setText("正在读取数据。。。");
                            }else{
                                tvLightSensor.setText("光照强度 ：" + spHelper.getSpLightSensor() + "lux");
                            }
                        }
                    }else{
                        tvLightSensor.setText("光照强度 ：" + lightSensor + "lux");
                    }
                }
            }

        }

    }


}

