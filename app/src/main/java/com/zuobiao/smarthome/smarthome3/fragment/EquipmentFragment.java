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
import com.zuobiao.smarthome.smarthome3.activity.NoiseActivity;
import com.zuobiao.smarthome.smarthome3.activity.SingleCurtainsActivity;
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
                udpHelper.setIsSend(true);
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

            if (Constant.SINGLE_CURTAINS.equalsIgnoreCase(deviceType)) {
                //单个窗帘
                Intent intent = new Intent();
                intent.setClass(getActivity(), SingleCurtainsActivity.class);
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

            if (Constant.NOISE_SENSOR.equalsIgnoreCase(deviceType)) {
                //噪音
                Intent intent = new Intent();
                intent.setClass(getActivity(), NoiseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("equipmentBean", equipmentBean);
                intent.putExtras(bundle);
                startActivity(intent);
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
    public void onStart() {
        Log.e(TAG,"onStart");
        Log.e(TAG, "spHelper.getSpHasGateWayInfo()=" + spHelper.getSpHasGateWayInfo() + "  spHelper.getSpOnLine()=" + spHelper.getSpOnLine());
        //这种做法不是很好，但能实现功能，暂时这么做。
        list = dBcurd.getAllData();//数据源 不管什么时候，都是从这里读取所有设备
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

    private byte[] broadcastData(){
        //数据头
        searchGateWay[0] = Constant.DATA_HEAD[0];
        searchGateWay[1] = Constant.DATA_HEAD[1];

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

                mSwipeLayout.setRefreshing(false);
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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated...");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume...");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause...");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop...");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView...");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy...");
        super.onDestroy();
    }


}
