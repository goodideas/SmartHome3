package com.zuobiao.smarthome.smarthome3.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.ImageListViewAdapter;
import com.zuobiao.smarthome.smarthome3.util.OnReceive;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.util.List;


public class AddEquipmentActivity extends StatusActivity {

    private Button btnQr;
    private Button btnRefreshEquipment;
    private Button btnAddEquipment;
    private Button btnSearchGateWay;
    private Button btnSendAddRfidCard;
    private Button btnSendDelRfidCard;
    private Button btnAddRfidCardInfo;
    private ListView lvEquipments;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Button btnBack;
    private DBcurd DBcurd;
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private UdpHelper udpHelper;
    private SpHelper spHelper;
    private static final String broadcastIP = "255.255.255.255";
    private List<String> rfidList;
    private String[] rfids;
    private int dialogAddWhich;
    private int dialogDelWhich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);
        btnQr = (Button) findViewById(R.id.btnQr);
        btnRefreshEquipment = (Button) findViewById(R.id.btnRefreshEquipment);
        btnAddEquipment = (Button) findViewById(R.id.btnAddEquipment);
        btnSearchGateWay = (Button) findViewById(R.id.btnSearchGateWay);
        btnSendAddRfidCard = (Button) findViewById(R.id.btnSendAddRfidCard);
        btnSendDelRfidCard = (Button) findViewById(R.id.btnSendDelRfidCard);
        btnAddRfidCardInfo = (Button) findViewById(R.id.btnAddRfidCardInfo);

        DBcurd = new DBcurd(AddEquipmentActivity.this);
        lvEquipments = (ListView) findViewById(R.id.lvEquipments);
        clicks();
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Toolbar");//设置Toolbar标题
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.e("Main", "open");
                lvEquipments.setAdapter(new ImageListViewAdapter(AddEquipmentActivity.this, DBcurd.getAllData(), true));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.e("Main", "close");
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }

    }


    private void clicks() {

        btnAddRfidCardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddEquipmentActivity.this, AddRfidCardInfoActivity.class));
            }
        });

        //添加Rfid卡
        btnSendAddRfidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfidList = DBcurd.getAllRfidInfo();
                rfids = new String[rfidList.size()];
                for (int i = 0; i < rfidList.size(); i++) {
                    rfids[i] = rfidList.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEquipmentActivity.this);
                builder.setSingleChoiceItems(rfids, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogAddWhich = which;

                    }
                });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.showToast(getApplicationContext(), rfids[dialogAddWhich]);
                        spHelper = new SpHelper(AddEquipmentActivity.this);
                        String ip = spHelper.getSpGateWayIp();
                        if (ip != null) {
                            if (udpHelper != null) {
                                udpHelper.closeUdp();
                            }
                            udpHelper = UdpHelper.getInstance();
                            udpHelper.startUdpWithIp(ip, AddEquipmentActivity.this);
                            udpHelper.setIsSend(true);
                            udpHelper.send(getSendRfidByte(rfids[dialogAddWhich], Constant.RFID_INFO_SEND_ADD));

                        } else {
                            Util.showToast(getApplicationContext(), "没有网关信息!");

                        }

                    }
                });

                builder.setPositiveButton("取消", null);
                builder.create();
                builder.show();


            }
        });

        //删除Rfid卡
        btnSendDelRfidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfidList = DBcurd.getAllRfidInfo();
                rfids = new String[rfidList.size()];
                for (int i = 0; i < rfidList.size(); i++) {
                    rfids[i] = rfidList.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEquipmentActivity.this);
                builder.setSingleChoiceItems(rfids, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogDelWhich = which;
                    }
                });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.showToast(getApplicationContext(), rfids[dialogDelWhich]);
                        spHelper = new SpHelper(AddEquipmentActivity.this);
                        String ip = spHelper.getSpGateWayIp();
                        if (ip != null) {
                            if (udpHelper != null) {
                                udpHelper.closeUdp();
                            }
                            udpHelper = UdpHelper.getInstance();
                            udpHelper.startUdpWithIp(ip, AddEquipmentActivity.this);
                            udpHelper.setIsSend(true);
                            udpHelper.send(getSendRfidByte(rfids[dialogDelWhich], Constant.RFID_INFO_SEND_DEL));
                        } else {
                            Util.showToast(getApplicationContext(), "没有网关信息!");
                        }
                    }
                });

                builder.setPositiveButton("取消", null);
                builder.create();
                builder.show();


            }
        });


        //二维码
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddEquipmentActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });

        //查找网关
        btnSearchGateWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpHelper = UdpHelper.getInstance();
                spHelper = new SpHelper(AddEquipmentActivity.this);
                udpHelper.startUdpWithIp(broadcastIP, AddEquipmentActivity.this);
                udpHelper.setIsSend(true);
                udpHelper.send(Util.broadcastData());
                udpHelper.setOnReceive(new OnReceive() {
                    @Override
                    public void receive(String command,String data, String ip) {
                        Log.e("接收接口", "receive() data=" + data);
                    }
                });
                udpHelper.doSearchGateWay();
            }
        });

        //刷新设备
        btnRefreshEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spHelper = new SpHelper(AddEquipmentActivity.this);
                String ip = spHelper.getSpGateWayIp();
                if (ip != null) {
                    if (udpHelper != null) {
                        udpHelper.closeUdp();
                    }
                    udpHelper = UdpHelper.getInstance();
                    udpHelper.startUdpWithIp(ip, AddEquipmentActivity.this);
                    udpHelper.setIsSend(true);
                    udpHelper.send(getRefreshSendData());
                    udpHelper.doRefreshEquipment(Constant.REFRESH_EQUIPMENT_WAIT_MAX_TIME);

                } else {
                    Util.showToast(getApplicationContext(), "没有网关信息!");

                }
            }
        });

        //添加设备
        btnAddEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spHelper = new SpHelper(AddEquipmentActivity.this);
                String ip = spHelper.getSpGateWayIp();
                if (ip != null) {
                    if (udpHelper != null) {
                        udpHelper.closeUdp();
                    }
                    udpHelper = UdpHelper.getInstance();
                    udpHelper.startUdpWithIp(ip, AddEquipmentActivity.this);
                    udpHelper.setIsSend(true);
                    udpHelper.send(getAddSendData());
                    udpHelper.doAddEquipment(Constant.ADD_EQUIPMENT_WAIT_MAX_TIME);
                } else {
                    Util.showToast(getApplicationContext(), "没有网关信息！");
                }
            }
        });

    }


    //刷新设备
    private byte[] getRefreshSendData() {
//        ff aa B7 59 0B 7F CF 5C 00 00 02 00 00 00 00 ff 55
        byte[] refreshSendData = new byte[17];
        refreshSendData[0] = Constant.DATA_HEAD[0];
        refreshSendData[1] = Constant.DATA_HEAD[1];
        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, refreshSendData, 2, macByteLength);

        refreshSendData[10] = Constant.REFRESH_EQUIPMENT_SEND_COMMAND[0];
        refreshSendData[11] = Constant.REFRESH_EQUIPMENT_SEND_COMMAND[1];

        //数据内容长度
        refreshSendData[12] = (byte) 0x00;
        refreshSendData[13] = (byte) 0x00;
        //数据校验

        refreshSendData[14] = (byte) 0x00;
        //数据尾
        refreshSendData[15] = Constant.DATA_TAIL[0];
        refreshSendData[16] = Constant.DATA_TAIL[1];
        return refreshSendData;

    }


    //添加设备
    private byte[] getAddSendData() {
        byte[] refreshSendData = new byte[17];
        refreshSendData[0] = Constant.DATA_HEAD[0];
        refreshSendData[1] = Constant.DATA_HEAD[1];
        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, refreshSendData, 2, macByteLength);
        refreshSendData[10] = Constant.ADD_EQUIPMENT_SEND_COMMAND[0];
        refreshSendData[11] = Constant.ADD_EQUIPMENT_SEND_COMMAND[1];

        //数据内容长度
        refreshSendData[12] = (byte) 0x00;
        refreshSendData[13] = (byte) 0x00;
        //数据校验

        refreshSendData[14] = (byte) 0x00;

        //数据尾
        refreshSendData[15] = Constant.DATA_TAIL[0];
        refreshSendData[16] = Constant.DATA_TAIL[1];
        return refreshSendData;

    }


    @Override
    protected void onStop() {
        if (udpHelper != null) {
            udpHelper.closeUdp();
        }
        super.onStop();
    }



    private byte[] getSendRfidByte(String rfidInfo, byte[] sendCommand) {
        byte[] data = new byte[17 + 12];
        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];
        byte[] macByte = Util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);
        data[10] = sendCommand[0];
        data[11] = sendCommand[1];

        //数据内容长度
        data[12] = (byte) 0x0C; //12字节
        data[13] = (byte) 0x00;

        byte[] rfidByte = Util.HexString2Bytes(rfidInfo);
        System.arraycopy(rfidByte, 0, data, 14, rfidByte.length);
        //数据校验
        data[26] = Util.checkData(rfidInfo);

        //数据尾
        data[27] = Constant.DATA_TAIL[0];
        data[28] = Constant.DATA_TAIL[1];
        return data;
    }
}
