package com.zuobiao.smarthome.smarthome3.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.activity.AddEquipmentActivity;
import com.zuobiao.smarthome.smarthome3.activity.ControlModuleActivity;
import com.zuobiao.smarthome.smarthome3.activity.CurtainsActivity;
import com.zuobiao.smarthome.smarthome3.activity.DoorMagnetActivity;
import com.zuobiao.smarthome.smarthome3.activity.InflammableGasActivity;
import com.zuobiao.smarthome.smarthome3.activity.InfraredActivity;
import com.zuobiao.smarthome.smarthome3.activity.LightSensorActivity;
import com.zuobiao.smarthome.smarthome3.activity.MainActivity;
import com.zuobiao.smarthome.smarthome3.activity.SocketsActivity;
import com.zuobiao.smarthome.smarthome3.activity.SwitchsActivity;
import com.zuobiao.smarthome.smarthome3.activity.TempPm25Activity;
import com.zuobiao.smarthome.smarthome3.activity.TestActivity;
import com.zuobiao.smarthome.smarthome3.activity.WindowActivity;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.ImageListViewAdapter;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EquipmentFragment extends BaseFragment {


    private static final String TAG = "EquipmentFragment";
//    private Button btnMenu;
//    private CustomPopWindow titlePopup;
    private Button btnCamera;
    private byte[] searchGateWay = new byte[17+7];
    private GridView gridView;
    private ImageListViewAdapter adapter;
    private List<EquipmentBean> list;//数据源
    private Button btnAdd;
    private SpHelper spHelper;
    private DBcurd dBcurd;
    private SwipeRefreshLayout mSwipeLayout;
    private MyHandler myHandler;
    private static final int REFRESH_DATA = 0x123;
    private UdpHelper udpHelper;
    private static final String broadcastIP = "255.255.255.255";
    private byte[] bddata = {(byte)0xff,(byte)0xaa,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
            ,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xff,(byte)0x55};

    private Util util = new Util();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.equipment_layout,
                container, false);
        btnAdd = (Button) messageLayout.findViewById(R.id.btnAdd);
        btnCamera= (Button) messageLayout.findViewById(R.id.btnCamera);
        mSwipeLayout = (SwipeRefreshLayout) messageLayout.findViewById(R.id.id_swipe_ly);
        myHandler = new MyHandler();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getActivity().getPackageManager();
                Intent intent =packageManager.getLaunchIntentForPackage("vstc.eye4zx.client");
                if(intent!=null){
                    startActivity(intent);
                }

            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                udpHelper = UdpHelper.getInstance();
                spHelper = new SpHelper(getActivity());
                udpHelper.startUdpWithIp(broadcastIP, getActivity());
//                udpHelper.send(broadcastData());
                udpHelper.setIsSend(true);
//                udpHelper.send(bddata);
                udpHelper.send(broadcastData());
                udpHelper.doSearchGateWay();
                myHandler.sendEmptyMessageDelayed(REFRESH_DATA, 2000);

            }
        });
        list = new ArrayList<>();
        dBcurd = new DBcurd(getActivity());
        gridView = (GridView) messageLayout.findViewById(R.id.imageGridView);
        spHelper = new SpHelper(getActivity());
        Log.e(TAG, "spHelper.getSpHasGateWayInfo()=" + spHelper.getSpHasGateWayInfo() + "  spHelper.getSpOnLine()=" + spHelper.getSpOnLine());

        //4种情况
        //第1种 网关在线 手机没存 onLine = true，hasGateWayInfo = false

        //第2种 网关在线 手机存了 onLine = true，hasGateWayInfo = true

        //第3种 网关不在线 手机没存 onLine = false，hasGateWayInfo = false

        //第4种 网关不在线 手机存了 onLine = false，hasGateWayInfo = true

        //测试
//        EquipmentBean equipmentBean = new EquipmentBean();
//        equipmentBean.setMac_ADDR("B7590B7FCF5C0000");
//        equipmentBean.setShort_ADDR("C4EE");
//        equipmentBean.setCoord_Short_ADDR("EF5E");
//        equipmentBean.setSoftware_Edition("00000000");
//        equipmentBean.setHardware_Edition("00000000");
//        equipmentBean.setDevice_Type("2005FFFE");
//        equipmentBean.setPoint_Data("0000");
//        equipmentBean.setRemark("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
//        dBcurd.addData(equipmentBean);
//
//        list = dBcurd.getAllData();//数据源 不管什么时候，都是从这里读取所有设备
//        if (list.size() == 0) {
//            Log.e(TAG, "list ==null");
//            //可以做一些文字或者图片来显示，告知用户
//        }
//
//        if (!spHelper.getSpHasGateWayInfo()) {
//            //没有网关的信息，显示空白或者只显示网关
//            if (list.size() != 0) {
//                EquipmentBean equipmentBeans = list.get(0);
//                equipmentBeans.setOnLine(spHelper.getSpOnLine());
//            }
//
//        } else {
//            //有网关信息，显示设备，在线显示在线，不在线显示不在线
//            for (int i = 0; i < list.size(); i++) {
//                EquipmentBean equipmentBeans = list.get(i);
//                equipmentBeans.setOnLine(spHelper.getSpOnLine());
//            }
//        }

        adapter = new ImageListViewAdapter(getActivity().getApplicationContext(), list,false);
        gridView.setAdapter(adapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogs(list.get(position),position);
                Log.e(TAG, "长按");
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EquipmentBean equipmentBean;
                equipmentBean = list.get(position);
                selectEquipment(equipmentBean.getDevice_Type(), equipmentBean.isOnLine(), equipmentBean);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddEquipmentActivity.class));
            }
        });

//        btnMenu = (Button)messageLayout.findViewById(R.id.btnMenu);
//        btnMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                titlePopup.show(v);
//                Log.e(TAG,"anniu");
//            }
//        });
//        titlePopup = new CustomPopWindow(getActivity(), LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        //给标题栏弹窗添加子类
//        titlePopup.addAction(new ActionItem(getActivity(), "删除", R.drawable.delete));
//        titlePopup.setItemOnClickListener(new CustomPopWindow.OnItemOnClickListener() {
//            @Override
//            public void onItemClick(ActionItem item, int position) {
//                Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        return messageLayout;
    }


    public void selectEquipment(String deviceType, boolean onLine, EquipmentBean equipmentBean) {

        if (onLine) {
            if (Constant.SWITCHES.equalsIgnoreCase(deviceType)) {
                //面板开关
                Intent intent = new Intent();
                intent.setClass(getActivity(), SwitchsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.CONTROL_MODULE.equalsIgnoreCase(deviceType)) {
                //智能控制模块
                Intent intent = new Intent();
                intent.setClass(getActivity(), ControlModuleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            if (Constant.SOCKETS.equalsIgnoreCase(deviceType)) {
                //插座
                Intent intent = new Intent();
                intent.setClass(getActivity(), SocketsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.TEMP_PM25.equalsIgnoreCase(deviceType)) {
                //温湿度pm2.5
                Intent intent = new Intent();
                intent.setClass(getActivity(), TempPm25Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }


            if (Constant.INFRARED.equalsIgnoreCase(deviceType)) {
                //人体红外
                Intent intent = new Intent();
                intent.setClass(getActivity(), InfraredActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.INFLAMMABLE_GAS.equalsIgnoreCase(deviceType)) {
                //烟雾传感器
                Intent intent = new Intent();
                intent.setClass(getActivity(), InflammableGasActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.LIGHT_SENSOR.equalsIgnoreCase(deviceType)) {
                //光照
                Intent intent = new Intent();
                intent.setClass(getActivity(), LightSensorActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.DOOR_MAGNET.equalsIgnoreCase(deviceType)) {
                //门磁
                Intent intent = new Intent();
                intent.setClass(getActivity(), DoorMagnetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.CURTAINS.equalsIgnoreCase(deviceType)) {
                //窗帘
                Intent intent = new Intent();
                intent.setClass(getActivity(), CurtainsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.WINDOW.equalsIgnoreCase(deviceType)) {
                //窗
                Intent intent = new Intent();
                intent.setClass(getActivity(), WindowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            if (Constant.TEST.equalsIgnoreCase(deviceType)) {
                startActivity(new Intent(getActivity(),TestActivity.class));
            }

        } else {
            Util.showToast(getActivity(),"网关不在线");
        }
    }

    private void showDialogs(final EquipmentBean equipmentBean,final int postion){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除");
        builder.setMessage("确定删除吗？");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dBcurd.delData(equipmentBean.getMac_ADDR());
                list.remove(postion);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create();

        builder.show();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach-----");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate------");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated-------");
    }

    @Override
    public void onStart() {
        Log.e(TAG, "spHelper.getSpHasGateWayInfo()=" + spHelper.getSpHasGateWayInfo() + "  spHelper.getSpOnLine()=" + spHelper.getSpOnLine());
        //这种做法不是很好，但能实现功能，暂时这么做。
        list = dBcurd.getAllData();//数据源 不管什么时候，都是从这里读取所有设备
        EquipmentBean equipmentBeanT = new EquipmentBean();
        equipmentBeanT.setDevice_Type("FFFFFFFF");
        list.add(equipmentBeanT);
        adapter = new ImageListViewAdapter(getActivity().getApplicationContext(), list,false);
        gridView.setAdapter(adapter);
        if (spHelper.getSpHasGateWayInfo()) {
            Log.e(TAG, "hasGateWayInfo=" + spHelper.getSpHasGateWayInfo());
            for (int i = 0; i < list.size(); i++) {
                EquipmentBean equipmentBean = list.get(i);
                equipmentBean.setOnLine(spHelper.getSpOnLine());
                list.set(i, equipmentBean);
            }
            adapter.notifyDataSetChanged();
        }

        super.onStart();

        Log.e(TAG, "onStart----->");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onresume---->");
        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_EQUIPMENT;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onpause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "ondestoryView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "ondestory");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach------");

    }

//    private byte[] broadcastData(){
//        //数据头
//        searchGateWay[0] = Constant.DATA_HEAD[0];
//        searchGateWay[1] = Constant.DATA_HEAD[1];
//
//        searchGateWay[2] = (byte)0x00;
//        searchGateWay[3] = (byte)0x00;
//        searchGateWay[4] = (byte)0x00;
//        searchGateWay[5] = (byte)0x00;
//        searchGateWay[6] = (byte)0x00;
//        searchGateWay[7] = (byte)0x00;
//        searchGateWay[8] = (byte)0x00;
//        searchGateWay[9] = (byte)0x00;
//
//        //命令类型
//        searchGateWay[10] = Constant.GATEWAY_SEND_COMMAND[0];
//        searchGateWay[11] = Constant.GATEWAY_SEND_COMMAND[1];
//
//        //数据内容长度
//        searchGateWay[12] = (byte)0x06;
//
//        //数据内容
//        searchGateWay[13] = (byte)0x00;
//
//        byte[] timeByte = getLocalTime();
//
//        searchGateWay[14] = timeByte[0];//年
//        searchGateWay[15] = timeByte[1];//月
//        searchGateWay[16] = timeByte[2];//日
//        searchGateWay[17] = timeByte[3];//时
//        searchGateWay[18] = timeByte[4];//分
//        searchGateWay[19] = timeByte[5];//秒
//        //数据校验
//
//        searchGateWay[20] = util.checkData(util.bytes2HexString(timeByte,timeByte.length));
//
//        //数据尾
//        searchGateWay[21] = Constant.DATA_TAIL[0];
//        searchGateWay[22] = Constant.DATA_TAIL[1];
//        return searchGateWay;
//    }
//
//    private byte[] getLocalTime(){
//        byte[] time = new byte[6];
//        SimpleDateFormat formatter = new SimpleDateFormat ("yy:MM:dd:HH:mm:ss");
//        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String timeForm = formatter.format(curDate);
//        String times[] = timeForm.split(":");
//        for(int i =0;i< 6;i++){
//            time[i] = Byte.parseByte(times[i]);
//        }
//
//        return time;
//    }

    private byte[] broadcastData(){
        //数据头
        searchGateWay[0] = Constant.DATA_HEAD[0];
        searchGateWay[1] = Constant.DATA_HEAD[1];
//        ff aa 00 00 00 00 00 00 00 00 01 00 00 00 00 ff 55

        //mac地址
//        byte[] macByte = util.HexString2Bytes(getMacAddress());
//        int macByteLength = macByte.length;
//        System.arraycopy(macByte,0,searchGateWay,2,macByteLength);
        searchGateWay[2] = (byte)0x00;
        searchGateWay[3] = (byte)0x00;
        searchGateWay[4] = (byte)0x00;
        searchGateWay[5] = (byte)0x00;
        searchGateWay[6] = (byte)0x00;
        searchGateWay[7] = (byte)0x00;
        searchGateWay[8] = (byte)0x00;
        searchGateWay[9] = (byte)0x00;

        //命令类型
        searchGateWay[10] = Constant.GATEWAY_SEND_COMMAND[0];
        searchGateWay[11] = Constant.GATEWAY_SEND_COMMAND[1];

        //数据内容长度
        searchGateWay[12] = (byte)0x07;
        //数据内容
        searchGateWay[13] = (byte)0x00;

        byte[] timeByte = getLocalTime();
        // yyyy MM dd HH mm ss
        searchGateWay[14] = timeByte[0];//年
        searchGateWay[15] = timeByte[1];//年
        searchGateWay[16] = timeByte[2];//月
        searchGateWay[17] = timeByte[3];//日
        searchGateWay[18] = timeByte[4];//时
        searchGateWay[19] = timeByte[5];//分
        searchGateWay[20] = timeByte[6];//秒
        //数据校验

        searchGateWay[21] = util.checkData(util.bytes2HexString(timeByte,timeByte.length));

        //数据尾
        searchGateWay[22] = Constant.DATA_TAIL[0];
        searchGateWay[23] = Constant.DATA_TAIL[1];
        return searchGateWay;
    }

    //    FFAA 0000000000000000 0100 0800  07 E0 03 14 0B 0E 0C 23 FF55
//    FFAA 0000000000000000 0100 0800  07 E0 03 14 0B 0E 36 4D FF55
    private byte[] getLocalTime(){
        byte[] time = new byte[7];
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy:MM:dd:HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String timeForm = formatter.format(curDate);
        String times[] = timeForm.split(":");

        String year = Integer.toHexString(Integer.parseInt(times[0]));
        if(year.length()!=4){
            year = "0"+year;
        }
        //year == 07e0
        byte[] yearByte = util.HexString2Bytes(year);
//        int yearByteLength = yearByte.length;
//        System.arraycopy(yearByte,0,time,0,yearByteLength);

        time[0] = yearByte[1];
        time[1] = yearByte[0];

        for(int i =1;i< 6;i++){
            time[i+1] = Byte.parseByte(times[i]);
        }

        return time;
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == REFRESH_DATA){
//                Toast t = Toast.makeText(getActivity(),"刷新",Toast.LENGTH_SHORT);
//                t.setGravity(Gravity.CENTER, 0, 0);
//                t.show();
                mSwipeLayout.setRefreshing(false);

//                list = dBcurd.getAllData();//数据源 不管什么时候，都是从这里读取所有设备
//                adapter = new ImageListViewAdapter(getActivity().getApplicationContext(), list);
                gridView.setAdapter(adapter);
                if (spHelper.getSpHasGateWayInfo()) {
                    Log.e(TAG, "hasGateWayInfo=" + spHelper.getSpHasGateWayInfo());
                    for (int i = 0; i < list.size(); i++) {
                        EquipmentBean equipmentBean = list.get(i);
                        equipmentBean.setOnLine(spHelper.getSpOnLine());
                        list.set(i, equipmentBean);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        }
    }


}
