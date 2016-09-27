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

public class NoiseActivity extends StatusActivity {
    private static final String TAG = "NoiseActivity";
    private TextView tvNoiseSensor;
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnNoiseSensorRefreSh;
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
        myHandler = new MyHandler(getMainLooper());
        Intent intent = this.getIntent();
        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");

        btnModifyName = (Button)findViewById(R.id.btnModifyNameNoiseSensor);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameNoiseSensor);

        DBcurd = new DBcurd(NoiseActivity.this);
        spHelper = new SpHelper(NoiseActivity.this);
        udpHelper = UdpHelper.getInstance();
        if(!TextUtils.isEmpty(spHelper.getSpNoiseSensor())){
            tvNoiseSensor.setText("音量 ：" + spHelper.getSpNoiseSensor() + "db");
        }

        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), NoiseActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.NOISE_SENSOR_SEND2_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                if (command.equalsIgnoreCase(Constant.NOISE_SENSOR_RECEIVE_COMMAND)
                        ||command.equalsIgnoreCase(Constant.NOISE_SENSOR_RECEIVE_COMMAND2)) {
                    if (Constant.NOISE_SENSOR.equalsIgnoreCase(data.substring(48, 56))) {
                        String mac = data.substring(28, 44);
                        String handlerMessage = data.substring(56, 60);
                        Message msg = new Message();
                        msg.obj = handlerMessage+mac;
                        msg.what = Constant.HANDLER_NOISE_SENSOR_HAS_ANSWER;
                        myHandler.sendMessage(msg);
                    }
                }
            }
        });
        btnNoiseSensorRefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (udpHelper != null) {
                    udpHelper.setIsSend(true);
                    udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.NOISE_SENSOR_SEND2_COMMAND, equipmentBean));
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
                        udpHelper.send(Util.getModifyData(modifyString, spHelper.getSpGateWayMac(), equipmentBean));
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
            //噪音
            if (msg.what == Constant.HANDLER_NOISE_SENSOR_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvNoiseSensor != null&&mac.equals(equipmentBean.getMac_ADDR())){
                    int noiseSensor = high + low * 256;
                    if(noiseSensor == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpNoiseSensor())){
                            if(Integer.parseInt(spHelper.getSpNoiseSensor())==0){
                                tvNoiseSensor.setText("正在读取数据。。。");
                            }else{
                                tvNoiseSensor.setText("音量 ：" + spHelper.getSpNoiseSensor() + "db");
                            }
                        }
                    }else{
                        spHelper.saveSpNoiseSensor(String.valueOf(noiseSensor));
                        tvNoiseSensor.setText("音量 ：" + noiseSensor + "db");
                    }
                }

            }


        }

    }



}
