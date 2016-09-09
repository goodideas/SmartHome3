package com.zuobiao.smarthome.smarthome3.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

public class TestActivity extends StatusActivity {

    private Button btnBack;
    private TextView tvEquipmentShow;
    private TextView tvTestTemp;
    private TextView tvTestHumidity;
    private TextView tvTestPm25;
    private TextView tvTestFlam;
    private TextView tvTestLight;
    private TextView tvTestStatus;
    private ToggleButton tbTest;

    private UdpHelper udpHelper;
    private SpHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tvTestTemp = (TextView)findViewById(R.id.tvTestTemp);
        tvTestHumidity = (TextView)findViewById(R.id.tvTestHumidity);
        tvTestPm25 = (TextView)findViewById(R.id.tvTestPm25);
        tvTestFlam = (TextView)findViewById(R.id.tvTestFlam);
        tvTestLight = (TextView)findViewById(R.id.tvTestLight);
        tvTestStatus = (TextView)findViewById(R.id.tvTestStatus);
        tbTest = (ToggleButton)findViewById(R.id.tbTest);
//        spHelper = new SpHelper(this);
        btnBack = (Button)findViewById(R.id.btnEquipmentTitleBarBack);
        tvEquipmentShow = (TextView)findViewById(R.id.tvEquipmentShow);
//        tvEquipmentShow.setText("EnOcean 无线空气净化传感控制系统");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        udpHelper = UdpHelper.getInstance();
//        udpHelper.setTestUI(tvTestTemp, tvTestHumidity, tvTestPm25, tvTestFlam, tvTestLight, tvTestStatus, tbTest, "7307EA08004B1200");
//        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), TestActivity.this);
//        udpHelper.setIsSend(true);
//        udpHelper.send(getDataOfBeforeDo());getDataOfBeforeDoSocket
//        udpHelper.send(getDataOfBeforeDoSocket());

//        tbTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    udpHelper.send(getSocketsSendData(new byte[]{(byte) 0x01, (byte) 0x00}));
//                } else {
//                    udpHelper.send(getSocketsSendData(new byte[]{(byte) 0x00, (byte) 0x00}));
//                }
//            }
//        });



    }


//    private byte[] getSocketsSendData(byte[] status){
//        byte[] data = new byte[33];
//
//        data[0] = Constant.DATA_HEAD[0];
//        data[1] = Constant.DATA_HEAD[1];
//
//        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
//        int macByteLength = macByte.length;
//        System.arraycopy(macByte,0,data,2,macByteLength);
//        //命令类型
//        data[10] = Constant.SOCKETS_SEND_COMMAND[0];
//        data[11] = Constant.SOCKETS_SEND_COMMAND[1];
//
//        //数据内容长度
//        data[12] = (byte)0x10;
//        data[13] = (byte)0x00;
//
//        byte[] euipmentMacByte = Util.HexString2Bytes("7307EA08004B1200");
//        int euipmentMacByteLength = macByte.length;
//        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);
//
//
//        byte[] shortAddr = Util.HexString2Bytes("4BCC"); //2个字节
//        data[22] = shortAddr[0];
//        data[23] = shortAddr[1];
//
//        byte[] deviceType = Util.HexString2Bytes("3005FFFE"); //4个字节
//        data[24] = deviceType[0];
//        data[25] = deviceType[1];
//        data[26] = deviceType[2];
//        data[27] = deviceType[3];
//
//        data[28] = status[0];
//        data[29] = status[1];
//
//        String checkData = Util.bytes2HexString(data, data.length);
//        data[30] = Util.checkData(checkData.substring(28, 60));//校验位
//
//        data[31] = Constant.DATA_TAIL[0];
//        data[32] = Constant.DATA_TAIL[1];
//
//        return data;
//    }


//    private byte[] getDataOfBeforeDoSocket(){
//        byte[] data = new byte[25];
//
//        data[0] = Constant.DATA_HEAD[0];
//        data[1] = Constant.DATA_HEAD[1];
//        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
//        int macByteLength = macByte.length;
//        System.arraycopy(macByte, 0, data, 2, macByteLength);
//        data[10] = Constant.SOCKETS_SEND2_COMMAND[0];
//        data[11] = Constant.SOCKETS_SEND2_COMMAND[1];
//        //数据内容长度
//        data[12] = (byte) 0x08;
//        data[13] = (byte) 0x00;
//        byte[] euipmentMacByte = Util.HexString2Bytes("7307EA08004B1200");
//        int euipmentMacByteLength = macByte.length;
//        System.arraycopy(euipmentMacByte, 0, data, 14, euipmentMacByteLength);
//        String checkData = Util.bytes2HexString(data, data.length);
//
//        data[22] = Util.checkData(checkData.substring(28, 44));//校验位
//        data[23] = Constant.DATA_TAIL[0];
//        data[24] = Constant.DATA_TAIL[1];
//        return data;
//    }

}
