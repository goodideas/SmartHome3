package com.zuobiao.smarthome.smarthome3.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
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
import com.zuobiao.smarthome.smarthome3.activity.NoiseActivity;
import com.zuobiao.smarthome.smarthome3.activity.SingleCurtainsActivity;
import com.zuobiao.smarthome.smarthome3.activity.SocketsActivity;
import com.zuobiao.smarthome.smarthome3.activity.SwitchsActivity;
import com.zuobiao.smarthome.smarthome3.activity.TempPm25Activity;
import com.zuobiao.smarthome.smarthome3.activity.WindowActivity;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.ImageListViewAdapter;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.util.ArrayList;
import java.util.List;

public class EquipmentFragment extends Fragment {


    private static final String TAG = "EquipmentFragment";

    private Button btnCamera;
    private GridView gridView;
    private ImageListViewAdapter adapter;
    private List<EquipmentBean> list;//数据源
    private Button btnAdd;
    private SpHelper spHelper;
    private DBcurd DBcurd;
    private SwipeRefreshLayout mSwipeLayout;
    private MyHandler myHandler;
    private static final int REFRESH_DATA = 0x123;
    private UdpHelper udpHelper;

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
                }else{
                    Util.showToast(getActivity(),"请安装摄像头插件！");
                }

            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                udpHelper = UdpHelper.getInstance();
                spHelper = new SpHelper(getActivity());
                udpHelper.startUdpWithIp(Constant.BROADCAST_IP, getActivity());
                udpHelper.setIsSend(true);
                udpHelper.send(Util.broadcastData());
                udpHelper.doSearchGateWay();
                myHandler.sendEmptyMessageDelayed(REFRESH_DATA, Constant.REFRESH_GATE_TIME);

            }
        });
        list = new ArrayList<>();
        DBcurd = new DBcurd(getActivity());
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
                showDialogs(list.get(position), position);
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
            Intent intent = new Intent();
            switch (deviceType.toLowerCase()){
                case Constant.SWITCHES:
                    //面板开关
                    intent.setClass(getActivity(), SwitchsActivity.class);
                    break;
                 case Constant.CONTROL_MODULE:
                     //智能控制模块
                     intent.setClass(getActivity(), ControlModuleActivity.class);
                    break;
                 case Constant.SOCKETS:
                     //插座
                     intent.setClass(getActivity(), SocketsActivity.class);
                    break;
                 case Constant.TEMP_PM25:
                     //温湿度pm2.5
                     intent.setClass(getActivity(), TempPm25Activity.class);
                    break;
                 case Constant.INFRARED:
                     //人体红外
                     intent.setClass(getActivity(), InfraredActivity.class);
                    break;
                 case Constant.INFLAMMABLE_GAS:
                     //烟雾传感器
                     intent.setClass(getActivity(), InflammableGasActivity.class);
                    break;
                 case Constant.LIGHT_SENSOR:
                     //光照
                     intent.setClass(getActivity(), LightSensorActivity.class);
                    break;
                 case Constant.DOOR_MAGNET:
                     //门磁
                     intent.setClass(getActivity(), DoorMagnetActivity.class);
                    break;
                 case Constant.CURTAINS:
                     //窗帘
                     intent.setClass(getActivity(), CurtainsActivity.class);
                    break;
                 case Constant.SINGLE_CURTAINS:
                     //单个窗帘
                     intent.setClass(getActivity(), SingleCurtainsActivity.class);
                    break;
                 case Constant.WINDOW:
                     //窗
                     intent.setClass(getActivity(), WindowActivity.class);
                    break;
                 case Constant.NOISE_SENSOR:
                     //噪音
                     intent.setClass(getActivity(), NoiseActivity.class);
                    break;
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("equipmentBean", equipmentBean);
            intent.putExtras(bundle);
            startActivity(intent);

        } else {
            Util.showToast(getActivity(),"网关不在线");
        }
    }

    private void showDialogs(final EquipmentBean equipmentBean,final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除");
        builder.setMessage("确定删除吗？");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBcurd.delData(equipmentBean.getMac_ADDR());
                list.remove(position);
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
        list = DBcurd.getAllData();//数据源 不管什么时候，都是从这里读取所有设备
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

}
