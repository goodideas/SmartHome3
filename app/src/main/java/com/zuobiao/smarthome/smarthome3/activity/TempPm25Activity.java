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
    private DBcurd DBcurd;

    private Button btnEquipmentTitleBarBack;
    private TextView tvEquipmentShow;
    private MyHandler myHandler;

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
        myHandler = new MyHandler(getMainLooper());
        DBcurd = new DBcurd(TempPm25Activity.this);
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
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), TempPm25Activity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.TEMP_PM25_SEND2_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
            @Override
            public void receive(String command, String data, String ip) {
                if (command.equalsIgnoreCase(Constant.TEMP_PM25_RECEIVE_COMMAND2)
                        ||command.equalsIgnoreCase(Constant.TEMP_PM25_RECEIVE_COMMAND3)) {
                    String handlerMessage = data.substring(56, 68);
                    String mac = data.substring(28,44);
                    Message msg = new Message();
                    msg.obj = handlerMessage+mac;
                    msg.what = Constant.HANDLER_TEMP_PM25_HAS_ANSWER;
                    myHandler.sendMessage(msg);
                }

            }
        });
        btnTempPm25RefreSh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (udpHelper != null) {
                    udpHelper.setIsSend(true);
                    udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.TEMP_PM25_SEND2_COMMAND, equipmentBean));
                }
            }
        });
        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if (!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase(Constant.EQUIPMENT_NAME_ALL_FF)) {
            String equipmentName = new String(Util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
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

            //温湿度pm2传过啦的数据2
            if (msg.what == Constant.HANDLER_TEMP_PM25_HAS_ANSWER) {
                //是为3个16进制的数 第一个是temp，第二个是humi，第三个是pm2.5
                String data = (String) msg.obj;
                String stat = data.substring(0,12);
                String mac = data.substring(12);
                if (tvPm25 != null&&tvTemperature !=null && tvHumidity!=null&&mac.equals(equipmentBean.getMac_ADDR())) {
                    int a = Integer.parseInt(stat.substring(0, 2), 16) * 256;
                    int b = Integer.parseInt(stat.substring(2, 4), 16);
                    int c = Integer.parseInt(stat.substring(4, 6), 16) * 256;
                    int d = Integer.parseInt(stat.substring(6, 8), 16);
                    int e = Integer.parseInt(stat.substring(8, 10), 16);
                    int f = Integer.parseInt(stat.substring(10, 12), 16) * 256;

                    int temp = (c+d) / 10;
                    int hum = (a+b) / 10;
                    int pm25 = (e+f);

                    if(temp == 0&&hum == 0 && pm25 == 0){
                        if(!TextUtils.isEmpty(spHelper.getSpTemp())&&!TextUtils.isEmpty(spHelper.getSpHumidity())&&!TextUtils.isEmpty(spHelper.getSpPm25())){
                            tvTemperature.setText(spHelper.getSpTemp());
                            tvHumidity.setText(spHelper.getSpHumidity());
                            tvPm25.setText(spHelper.getSpPm25());
                        }else{
                            tvTemperature.setText("正在读取。。。");
                            tvHumidity.setText("正在读取。。。");
                            tvPm25.setText("正在读取。。。");
                        }
                    }else{
                        spHelper.saveSpTemp(String.valueOf(temp));
                        spHelper.saveSpPm25(String.valueOf(pm25));
                        spHelper.saveSpHumidity(String.valueOf(hum));
                        tvTemperature.setText(String.valueOf(temp));
                        tvHumidity.setText(String.valueOf(hum));
                        tvPm25.setText(String.valueOf(pm25));
                    }


                }
            }

        }

    }


}
//FFAA 1E13007FCF5C0000 4010 1400 CE6FEA08004B1200 3A08 4005FFFE 0000 0000 0000 10 FF55
//FFAA 1E13007FCF5C0000 4150 1400 CE6FEA08004B1200 3A08 4005FFFE 0284 0130 4300 0A FF55