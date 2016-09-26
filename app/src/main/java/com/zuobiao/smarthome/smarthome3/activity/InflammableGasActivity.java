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
 * 烟雾传感器
 */
public class InflammableGasActivity extends StatusActivity {

    private TextView tvInflammableGas;
    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private Button btnInflammableGasRefreSh;
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
        setContentView(R.layout.activity_inflammable_gas);
        tvInflammableGas = (TextView) findViewById(R.id.tvInflammableGas);
        btnInflammableGasRefreSh = (Button) findViewById(R.id.btnInflammableGasRefreSh);
        tvEquipmentShow = (TextView) findViewById(R.id.tvEquipmentShow);
        btnEquipmentTitleBarBack = (Button) findViewById(R.id.btnEquipmentTitleBarBack);
        btnEquipmentTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = this.getIntent();
        myHandler = new MyHandler(getMainLooper());
        equipmentBean = (EquipmentBean) intent.getSerializableExtra("equipmentBean");
        btnModifyName = (Button) findViewById(R.id.btnModifyNameInflammableGas);
        etEquipmentName = (EditText) findViewById(R.id.etEquipmentNameInflammableGas);

        DBcurd = new DBcurd(InflammableGasActivity.this);

        spHelper = new SpHelper(InflammableGasActivity.this);
        udpHelper = UdpHelper.getInstance();

        if (!TextUtils.isEmpty(spHelper.getSpInflammableGasSensor())) {
            tvInflammableGas.setText("数据 ：" + spHelper.getSpInflammableGasSensor() + " PPM");
        }

//        udpHelper.setInflammableGasUI(tvInflammableGas,equipmentBean.getMac_ADDR());
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), InflammableGasActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.INFLAMMABLE_GAS_SEND_COMMAND, equipmentBean));
        udpHelper.setOnReceive(new OnReceive() {
                                   @Override
                                   public void receive(String command, String data, String ip) {
                                       if (command.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECV_COMMAND)
                                               ||command.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECV_COMMAND2)) {
                                           //如果是烟雾的
                                           if (Constant.INFLAMMABLE_GAS.equalsIgnoreCase(data.substring(48, 56))) {
                                               String handlerMessage = data.substring(56, 60);
                                               String mac = data.substring(28, 44);
                                               Message msg = new Message();
                                               msg.obj = handlerMessage + mac;
                                               msg.what = Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER;
                                               myHandler.sendMessage(msg);
                                           }
                                       }

//                                       if (command.equalsIgnoreCase(Constant.INFLAMMABLE_GAS_RECV_COMMAND2)) {
//                                           if (Constant.INFLAMMABLE_GAS.equalsIgnoreCase(data.substring(48, 56))) {
//                                               String handlerMessage = data.substring(56, 60);
//                                               String mac = data.substring(28, 44);
//                                               Message msg = new Message();
//                                               msg.obj = handlerMessage + mac;
//                                               msg.what = Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER2;
//                                               myHandler.sendMessage(msg);
//                                           }
//                                       }
                                   }
                               }

        );

        btnInflammableGasRefreSh.setOnClickListener(new View.OnClickListener()

                                                    {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (udpHelper != null) {
                                                                udpHelper.setIsSend(true);
                                                                udpHelper.send(Util.getDataOfBeforeDo(spHelper.getSpGateWayMac(), Constant.INFLAMMABLE_GAS_SEND_COMMAND, equipmentBean));
                                                            }
                                                        }
                                                    }

        );

        tvEquipmentShow.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if (!DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).

                equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")

                )

        {
            String equipmentName = new String(Util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
            if (TextUtils.isEmpty(equipmentName)) {
                etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            } else {
                etEquipmentName.setText(equipmentName);
            }
        } else

        {
            etEquipmentName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }

        btnModifyName.setOnClickListener(new View.OnClickListener()

                                         {
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
                                         }

        );

    }


    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER) {
                String handlerMessage = (String) msg.obj;
                String stat = handlerMessage.substring(0, 4);
                String mac = handlerMessage.substring(4);
                int high = Integer.parseInt(stat.substring(0, 2), 16);
                int low = Integer.parseInt(stat.substring(2, 4), 16);
                if (tvInflammableGas != null && mac.equals(equipmentBean.getMac_ADDR())) {
                    int inflammableGasSensor = high + low * 256;
                    if (inflammableGasSensor == 0) {
                        if (!TextUtils.isEmpty(spHelper.getSpInflammableGasSensor())) {
                            if (Integer.parseInt(spHelper.getSpInflammableGasSensor()) == 0) {
                                tvInflammableGas.setText("正在读取数据。。。");
                            } else {
                                tvInflammableGas.setText("数据 ：" + spHelper.getSpInflammableGasSensor() + " PPM");
                            }
                        }
                    } else {
                        //读到的数据不是为0
                        tvInflammableGas.setText("数据 ：" + inflammableGasSensor + " PPM");
                    }

                }
//                String handlerMessage = (String) msg.obj;
//                String stat = handlerMessage.substring(0, 4);
//                String mac = handlerMessage.substring(4);
//                int high = Integer.parseInt(stat.substring(0, 2), 16);
//                int low = Integer.parseInt(stat.substring(2, 4), 16);
//
//                if (tvInflammableGas != null && mac.equals(equipmentBean.getMac_ADDR())) {
//                    int inflammableGasSensor = (high + low * 256);
//                    tvInflammableGas.setText("数据 ：" + inflammableGasSensor + " PPM");
//                    if (spHelper != null) {
//                        spHelper.saveSpInflammableGasSensor(String.valueOf(inflammableGasSensor));
//                    }
//                }
            }

//            if (msg.what == Constant.HANDLER_INFLAMMABLE_GAS_HAS_ANSWER2) {
//                String handlerMessage = (String) msg.obj;
//                String stat = handlerMessage.substring(0, 4);
//                String mac = handlerMessage.substring(4);
//                int high = Integer.parseInt(stat.substring(0, 2), 16);
//                int low = Integer.parseInt(stat.substring(2, 4), 16);
//                if (tvInflammableGas != null && mac.equals(equipmentBean.getMac_ADDR())) {
//                    int inflammableGasSensor = high + low * 256;
//                    if (inflammableGasSensor == 0) {
//                        if (!TextUtils.isEmpty(spHelper.getSpInflammableGasSensor())) {
//                            if (Integer.parseInt(spHelper.getSpInflammableGasSensor()) == 0) {
//                                tvInflammableGas.setText("正在读取数据。。。");
//                            } else {
//                                tvInflammableGas.setText("数据 ：" + spHelper.getSpInflammableGasSensor() + " PPM");
//                            }
//                        }
//                    } else {
//                        //读到的数据不是为0
//                        tvInflammableGas.setText("数据 ：" + inflammableGasSensor + " PPM");
//                    }
//
//                }
//            }

        }

    }


}
