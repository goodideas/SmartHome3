package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
 * 人体红外
 */
public class InfraredActivity extends StatusActivity {

    private TextView tvInfrared;
    private static final String TAG = "InfraredActivity";
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnInfraredRefreSh;
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
        myHandler = new MyHandler(getMainLooper());
        Intent intent = this.getIntent();
        equipmentBean=(EquipmentBean)intent.getSerializableExtra("equipmentBean");
        btnModifyName = (Button)findViewById(R.id.btnModifyNameInfrared);
        etEquipmentName = (EditText)findViewById(R.id.etEquipmentNameInfrared);
        DBcurd = new DBcurd(InfraredActivity.this);
        spHelper = new SpHelper(InfraredActivity.this);
        udpHelper = UdpHelper.getInstance();
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), InfraredActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.INFRARED_SEND_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                if (command.equalsIgnoreCase(Constant.INFRARED_RECEIVE_COMMAND)
                        ||command.equalsIgnoreCase(Constant.INFRARED_RECEIVE_COMMAND2)) {
                    String handlerMessage = data.substring(56, 58);
                    String mac = data.substring(28, 44);
                    Message msg = new Message();
                    msg.obj = handlerMessage+mac;
                    msg.what = Constant.HANDLER_INFRARED_HAS_ANSWER;
                    myHandler.sendMessage(msg);

                }
            }
        });

        btnInfraredRefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpHelper.setIsSend(true);
                udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.INFRARED_SEND_COMMAND, equipmentBean));
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
            if (msg.what == Constant.HANDLER_INFRARED_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0,2);
                String mac = handlerMessage.substring(2);
                if (tvInfrared != null&&mac.equals(equipmentBean.getMac_ADDR())) {
                    if (stat.equalsIgnoreCase("01")) {
                        tvInfrared.setText("有人");
                    }
                    if (stat.equalsIgnoreCase("00")) {
                        tvInfrared.setText("没人");
                    }

                }
            }


        }

    }


}
