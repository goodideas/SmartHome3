package com.zuobiao.smarthome.smarthome3.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.activity.AddSceneActivity;
import com.zuobiao.smarthome.smarthome3.activity.MainActivity;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.SceneAdapter;
import com.zuobiao.smarthome.smarthome3.entity.SceneEquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneNameBean;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.util.List;

public class SceneFragment extends BaseFragment {

    private static final String TAG = "SceneFragment";
    private UdpHelper udpHelper;
	private ListView scene_listview;
    private SceneAdapter sceneAdapter;
    private List<SceneNameBean> list;
    private Button btnAddScene;
    private Button btnRefreshSceneSetting;

    private TextView tvShowInUseScene;
    private int postioned;
    private DBcurd dBcurd;
    public static final int INTENT_ADD_FLAG = 100;
    public static final int INTENT_LIST_VIEW_ITEM_FLAG = 200;
    public static final String INTENT_ADD_FLAG_STRING = "intentSetting";
    public static final String INTENT_LIST_VIEW_ITEM_FLAG_GET_ITEM_NUMBER = "getItemNumber";
    public static final String INTENT_LIST_VIEW_ITEM_FLAG_GET_ITEM_SCENE_NAME = "getItemSceneName";

    private static int DEFAULTITEM = 0;
    private static int DEFAULTITEM2 = 0;
    private int choiceItem = DEFAULTITEM;//默认为0
    private int choiceItem2 = DEFAULTITEM2;//默认为0
    private String dialogItem[] = new String[]{"应用","删除"};
    private String dialogItem2[] = new String[]{"取消应用","删除","应用"};
    private SpHelper spHelper;
    private SceneNameBean showInUseScene;
    private int positioned;
    private Util util;

    @Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contactsLayout = inflater.inflate(R.layout.scene_layout,
				container, false);
        dBcurd = new DBcurd(getActivity());
        udpHelper = UdpHelper.getInstance();
        spHelper = new SpHelper(getActivity());
        udpHelper.startUdpWithIp(spHelper.getSpGateWayIp(), getActivity());
        udpHelper.setIsSend(true);
        list = dBcurd.getAllSceneName();
        btnRefreshSceneSetting = (Button)contactsLayout.findViewById(R.id.btnRefreshSceneSetting);
        util = new Util();
        sceneAdapter = new SceneAdapter(getActivity(),list);
        scene_listview = (ListView)contactsLayout.findViewById(R.id.scene_listview);
        tvShowInUseScene = (TextView)contactsLayout.findViewById(R.id.tvShowInUseScene);
        tvShowInUseScene.setText(spHelper.getSpIsUseName());
        scene_listview.setAdapter(sceneAdapter);
        scene_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positioned = position;
                SceneNameBean sceneNameBean = (SceneNameBean) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddSceneActivity.class);
                intent.putExtra(INTENT_ADD_FLAG_STRING, INTENT_LIST_VIEW_ITEM_FLAG);
                intent.putExtra(INTENT_LIST_VIEW_ITEM_FLAG_GET_ITEM_NUMBER, position);
                intent.putExtra(INTENT_LIST_VIEW_ITEM_FLAG_GET_ITEM_SCENE_NAME, sceneNameBean.getSceneName());
                startActivityForResult(intent, Constant.INTENT_ENTER_SCENE_REQUEST_CODE);
                Log.e("onItemClick", "position=" + position);

            }
        });

        scene_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                postioned = position;
                choiceItem = 0;
                showInUseScene = (SceneNameBean) parent.getItemAtPosition(position);

                if (postioned == spHelper.getSpIsUseNumber()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setSingleChoiceItems(dialogItem2, DEFAULTITEM2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            choiceItem2 = which;
                        }
                    });
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (choiceItem2 == 1) {
                                //删除
                                dBcurd.delSceneNameDataBySceneName(list.get(postioned).getSceneName());
                                dBcurd.delSceneAllBySceneAllName(list.get(postioned).getSceneName());
                                list.remove(postioned);
                                sceneAdapter.notifyDataSetChanged();
                                if (postioned == spHelper.getSpIsUseNumber()) {
                                    spHelper.SaveSpIsUseNumber(-1);
                                    spHelper.SaveSpIsUseName("无");
                                    tvShowInUseScene.setText("无");
                                }
                            } else if (choiceItem2 == 0) {
//                                取消应用场景
                                spHelper.SaveSpIsUseNumber(-1);
                                spHelper.SaveSpIsUseName("无");
                                sceneAdapter.notifyDataSetChanged();
                                tvShowInUseScene.setText("无");

                                List<SceneEquipmentBean> List = dBcurd.getAllSceneEquipmentBySceneNameAll(showInUseScene.getSceneName());
                                Util util = new Util();
                                udpHelper.send(getCloseSceneData(List));
                                String eee = util.bytes2HexString(getCloseSceneData(List), getCloseSceneData(List).length);
                                Log.e(TAG, "eee=" + eee);
//                                spHelper.SaveSpIsUseNumber(postioned);
//                                spHelper.SaveSpIsUseName(showInUseScene);
//                                sceneAdapter.notifyDataSetChanged();
//                                tvShowInUseScene.setText(showInUseScene);
//                                List<SceneEquipmentBean> allList = dBcurd.getAllSceneEquipmentBySceneNameAll(showInUseScene);
//                                Log.e(TAG, "allList.size()=" + allList.size());
//
//                                //数据内容的处理
//                                byte[] doWithSendData = doWithSendDataToGateWay(allList);
//
//                                //发送数据的处理
//                                byte[] doWithSendDataToGw = sendToGateWay(doWithSendData);
//                                Util util = new Util();
//                                String ddd = util.bytes2HexString(doWithSendDataToGw,doWithSendDataToGw.length);
//                                Log.e(TAG,"ddd="+ddd);
//                                udpHelper.send(doWithSendDataToGw);
                            } else if (choiceItem2 == 2) {
                                //应用
                                spHelper.SaveSpIsUseNumber(postioned);
                                spHelper.SaveSpIsUseName(showInUseScene.getSceneName());
                                sceneAdapter.notifyDataSetChanged();
                                tvShowInUseScene.setText(showInUseScene.getSceneName());
                                List<SceneEquipmentBean> allList = dBcurd.getAllSceneEquipmentBySceneNameAll(showInUseScene.getSceneName());
                                Log.e(TAG, "allList.size()=" + allList.size());
                                //数据内容的处理
                                byte[] doWithSendData = doWithSendDataToGateWay(allList);
                                //发送数据的处理
                                byte[] doWithSendDataToGw = sendToGateWay(doWithSendData);
                                Util util = new Util();
                                String ddd = util.bytes2HexString(doWithSendDataToGw, doWithSendDataToGw.length);
                                Log.e(TAG, "ddd=" + ddd);
                                udpHelper.send(doWithSendDataToGw);

                            }

                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("取消", null);
                    builder.create();
                    builder.show();
                } else {
                    ///****
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setSingleChoiceItems(dialogItem, DEFAULTITEM, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            choiceItem = which;
                        }
                    });
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "choiceItem=" + choiceItem);
                            if (choiceItem == 1) {
                                dBcurd.delSceneNameDataBySceneName(list.get(postioned).getSceneName());
                                dBcurd.delSceneAllBySceneAllName(list.get(postioned).getSceneName());
                                list.remove(postioned);
                                sceneAdapter.notifyDataSetChanged();
                                if (postioned == spHelper.getSpIsUseNumber()) {
//                                    if(spHelper.getSpIsUseNumber()>positioned){
//                                        spHelper.SaveSpIsUseNumber(positioned--);
//                                    }

                                    spHelper.SaveSpIsUseName("无");
                                    tvShowInUseScene.setText("无");
                                }

                            } else if (choiceItem == 0) {
//                            应用场景
                                spHelper.SaveSpIsUseNumber(postioned);
                                spHelper.SaveSpIsUseName(showInUseScene.getSceneName());
                                sceneAdapter.notifyDataSetChanged();
                                tvShowInUseScene.setText(showInUseScene.getSceneName());
                                List<SceneEquipmentBean> allList = dBcurd.getAllSceneEquipmentBySceneNameAll(showInUseScene.getSceneName());
                                Log.e(TAG, "allList.size()=" + allList.size());

                                //数据内容的处理
                                byte[] doWithSendData = doWithSendDataToGateWay(allList);
                                //发送数据的处理
                                byte[] doWithSendDataToGw = sendToGateWay(doWithSendData);
                                Util util = new Util();
                                String ddd = util.bytes2HexString(doWithSendDataToGw, doWithSendDataToGw.length);
                                Log.e(TAG, "ddd=" + ddd);
                                udpHelper.send(doWithSendDataToGw);
                            }

                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("取消", null);
                    builder.create();
                    builder.show();
                    ///****
                }


                return true;
            }
        });

        btnAddScene = (Button)contactsLayout.findViewById(R.id.btnAddScene);
        btnAddScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddSceneActivity.class);
                intent.putExtra(INTENT_ADD_FLAG_STRING,INTENT_ADD_FLAG);
                startActivityForResult(intent, Constant.INTENT_ENTER_ADD_SCENE_REQUEST_CODE);
            }
        });

        btnRefreshSceneSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpHelper.setIsSend(true);
                udpHelper.send(refreshSceneSetting());
                String logS = util.bytes2HexString(refreshSceneSetting(),refreshSceneSetting().length);
                Log.e(TAG,"logS = "+logS);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list = dBcurd.getAllSceneName();
                        sceneAdapter = new SceneAdapter(getActivity(), list);
                        scene_listview.setAdapter(sceneAdapter);
                    }
                }, 500);



            }
        });


		return contactsLayout;
	}

 // FFAA 66C5CF34FE1800 00F1 0036 E6B58BE8AF953530 3100 0000000000000000000000000000000198FFE908004B1200010000000000000100000000000000000000000000D0FF55

//  FFAA 66C5CF34FE1800 00F1 001A E6B58BE8AF953530 3100 0000000000000000000000000000000000FF55
    private byte[] getCloseSceneData(List<SceneEquipmentBean> allList){
        byte[] data = new byte[43];

        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        //Mac
        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, data, 2, macByteLength);

        //cmd
        data[10] = Constant.SCENE_SETTING_SEND_COMMAND[0];
        data[11] = Constant.SCENE_SETTING_SEND_COMMAND[1];


        data[12] = (byte)0x1A;
        data[13] = (byte)0x00;

        byte[] sceneNameByte = allList.get(0).getSceneName().getBytes();
        int sceneNameByteLength = sceneNameByte.length;
        System.arraycopy(sceneNameByte, 0, data, 14, sceneNameByteLength);

        //设备数量
        data[38] = (byte)0x00;
        data[39] = (byte)0x00;


        data[40] = (byte)0x00;
        data[41] = Constant.DATA_TAIL[0];
        data[42] = Constant.DATA_TAIL[1];
        return data;
    }

//    FFAA 66C5CF34FE180000 F110 0100 01 01 FF55
    //FFAA 66C5CF34FE180000 F100 3600 E6B58BE8AF95353033000000000000000000000000000000 0001 98FFE908004B1200 0200 000000000001000000000000000000000000D3FF5500

//    FFAA 66C5CF34FE180000 F110 0100 01 01 FF55
    @Override
	public void onResume() {
		super.onResume();
		MainActivity.currFragTag = Constant.FRAGMENT_FLAG_SCENE;
	}

    private byte[] doWithSendDataToGateWay(List<SceneEquipmentBean> allList) {
        int size = allList.size();
        byte[] equipmentByte = new byte[size*28+26];

        byte[] sceneNameByte = allList.get(0).getSceneName().getBytes();
        int sceneNameByteLength = sceneNameByte.length;
        System.arraycopy(sceneNameByte,0,equipmentByte,0,sceneNameByteLength);


        equipmentByte[24] = Byte.parseByte(""+size);
        equipmentByte[25] = (byte)0x00;
        // TODO: 2016/3/13 2个字节


        for(int i=0;i<size;i++) {
            //28字节

            SceneEquipmentBean sceneEquipmentBean = allList.get(i);
            sceneEquipmentBean.getEquipmentMac(); //8字节
//            sql存的是 16进制mac地址
//            equipmentByte[0] +i*28  equipmentByte[7] i*28+7
            byte[] macByte = util.HexString2Bytes(sceneEquipmentBean.getEquipmentMac());
            int macByteLength = macByte.length;
            System.arraycopy(macByte,0,equipmentByte,i*28+26,macByteLength);

            sceneEquipmentBean.getEquipmentNumber(); //1字节
//            sql存的是 String 数字
//            equipmentByte[8] +i*28+8

            equipmentByte[i*28+26+8] = Byte.parseByte(sceneEquipmentBean.getEquipmentNumber());

            String tigger = sceneEquipmentBean.getTrigger();
            if(tigger.equalsIgnoreCase("Time")){



                equipmentByte[i*28+26+9] = (byte)0x01;
                equipmentByte[i*28+26+10] = (byte)0x00;
                equipmentByte[i*28+26+11] = (byte)0x00; //备用
                equipmentByte[i*28+26+12] = (byte)0x00; //备用
                String time = sceneEquipmentBean.getTimeTrig();//4字节
                String[] times = time.split(":");
                equipmentByte[i*28+26+13] = Byte.parseByte(times[0]);
                equipmentByte[i*28+26+14] = Byte.parseByte(times[1]);
                sceneEquipmentBean.getTimeSwitchCmd(); //1字节
                equipmentByte[i*28+26+15] = (byte)0x00; //开


                equipmentByte[i*28+26+16] = (byte)0x00;
                equipmentByte[i*28+26+17] = (byte)0x00;
                equipmentByte[i*28+26+18] = (byte)0x00;
                equipmentByte[i*28+26+19] = (byte)0x00;
                equipmentByte[i*28+26+20] = (byte)0x00;
                equipmentByte[i*28+26+21] = (byte)0x00;
                equipmentByte[i*28+26+22] = (byte)0x00;
                equipmentByte[i*28+26+23] = (byte)0x00;
                equipmentByte[i*28+26+24] = (byte)0x00;
                equipmentByte[i*28+26+25] = (byte)0x00;
                equipmentByte[i*28+26+26] = (byte)0x00;
                equipmentByte[i*28+26+27] = (byte)0x00;


            }else if(tigger.equalsIgnoreCase("Immediately")){
                Log.e(TAG,"ImmediatelyImmediatelyImmediatelyImmediatelyImmediately");
                /**
                 8字节：		场景设备的MAC
                 1字节：		场景设备的的开关序号（比如面板开关，有3个开关）
                 1字节：		触发方式（0：立即触发；1：时间触发；2：条件触发
                 1字节：		立即触发开关命令（0关）
                 4字节：		时间：（备用：2字节，时：1字节，分:1字节）
                 1字节：		时间开关命令（0关）
                 8字节：		条件触发条件触发设备的MAC地址
                 1字节		条件触发的设备的传感号（多个传感器的，比如温湿度PM2.5）
                 2字节：		门限值
                 1字节：		0：相等时开，1：大于时开，2小于时开
                 */

                equipmentByte[i*28+26+9] = (byte)0x00;
                String immediatelyt =sceneEquipmentBean.getImmediatelyTrig();
                if(immediatelyt.equalsIgnoreCase("false")){
                    equipmentByte[i*28+26+10] = (byte)0x00;
                }else if(immediatelyt.equalsIgnoreCase("true")){
                    equipmentByte[i*28+26+10] =(byte)0x01;
                }
                //时间
                equipmentByte[i*28+26+11] = (byte)0x00;
                equipmentByte[i*28+26+12] = (byte)0x00;
                equipmentByte[i*28+26+13] = (byte)0x00;
                equipmentByte[i*28+26+14] = (byte)0x00;
                equipmentByte[i*28+26+15] = (byte)0x00;


                //                                                                            触发方式
//                E6B58BE8AF95353033000000000000000000000000000000 0001 98FFE908004B1200 02 00 00       00 00 00 00 01 000000000000000000000000
                //E6B58BE8AF95353033000000000000000000000000000000 0001 98FFE908004B1200 02 00 01       00 00 00 00 00 0000000000000000 00 000000

                equipmentByte[i*28+26+16] = (byte)0x00;
                equipmentByte[i*28+26+17] = (byte)0x00;
                equipmentByte[i*28+26+18] = (byte)0x00;
                equipmentByte[i*28+26+19] = (byte)0x00;
                equipmentByte[i*28+26+20] = (byte)0x00;
                equipmentByte[i*28+26+21] = (byte)0x00;
                equipmentByte[i*28+26+22] = (byte)0x00;
                equipmentByte[i*28+26+23] = (byte)0x00;
                equipmentByte[i*28+26+24] = (byte)0x00;
                equipmentByte[i*28+26+25] = (byte)0x00;
                equipmentByte[i*28+26+26] = (byte)0x00;
                equipmentByte[i*28+26+27] = (byte)0x00;

            }else if(tigger.equalsIgnoreCase("Condition")){
                equipmentByte[i*28+26+9] = (byte)0x02;

                //立即
                equipmentByte[i*28+26+10] = (byte)0x00;

                //时间
                equipmentByte[i*28+26+11] = (byte)0x00;
                equipmentByte[i*28+26+12] = (byte)0x00;
                equipmentByte[i*28+26+13] = (byte)0x00;
                equipmentByte[i*28+26+14] = (byte)0x00;
                equipmentByte[i*28+26+15] = (byte)0x00;

                byte[] macByte2 = util.HexString2Bytes(sceneEquipmentBean.getConditionEquipmentMac());
                int macByteLength2 = macByte.length;
                System.arraycopy(macByte2,0,equipmentByte,i*28+26+16,macByteLength2);
                String conditionNumber = sceneEquipmentBean.getCoditionNumber();
                if(conditionNumber.equalsIgnoreCase("温度")){
                    equipmentByte[i*28+26+24] = (byte)0x00;
                }else if(conditionNumber.equalsIgnoreCase("湿度")){
                    equipmentByte[i*28+26+24] = (byte)0x01;
                }else if(conditionNumber.equalsIgnoreCase("PM2.5")){
                    equipmentByte[i*28+26+24] = (byte)0x02;
                }
                String conditionValue = sceneEquipmentBean.getConditionValue();
                equipmentByte[i*28+26+25]  =(byte)0x00;
                if(TextUtils.isEmpty(conditionValue)){
                    equipmentByte[i*28+26+26]  =(byte)0x00;
                }else {
                    equipmentByte[i * 28 + 26 + 26] = Byte.parseByte(conditionValue);
                }
                String symbol = sceneEquipmentBean.getTrigSymbol();
                if(symbol.equalsIgnoreCase("大于")){
                    equipmentByte[i*28+26+27]  = (byte)0x01;
                }else if(symbol.equalsIgnoreCase("小于")){
                    equipmentByte[i*28+26+27]  = (byte)0x02;
                }else if(symbol.equalsIgnoreCase("等于")){
                    equipmentByte[i*28+26+27]  = (byte)0x00;
                }
            }

        }

//        测试
        String data = util.bytes2HexString(equipmentByte,equipmentByte.length);
        Log.e(TAG,"data="+data);

        return equipmentByte;

    }

    //                             E6B58BE8AF95353031000000000000000000000000000000000198FFE908004B12000100000000000001000000000000000000000000
    //FFAA66C5CF34FE180000F1003600 E6B58BE8AF95353031000000000000000000000000000000000198FFE908004B12000100000000000001000000000000000000000000 00D0FF55

//    E6B58BE8AF95353031000000000000000000000000000000000198FFE908004B12000100000000000001000000000000000000000000

//    FFAA 66C5CF34FE180000 F100 3600 E6B58BE8AF95353033000000000000000000000000000000 0001 98FFE908004B1200 01 00 00   00 00 00 00 01 0000000000000000 00  00 00 00 00D2FF55
//    FFAA 66C5CF34FE180000 F100 3600 E6B58BE8AF95353031000000000000000000000000000000 0001 98FFE908004B1200 01 00 00   00 00 00 00 01 0000000000000000 00  00 00 00 00D0FF55

    /**
     *
     * @param doWithSendDataToGateWay 数据的内容
     * @return  处理好的数据（16进制）
     */
    private byte[] sendToGateWay(byte[] doWithSendDataToGateWay){
        int dataContextLength = doWithSendDataToGateWay.length;

        byte[] sendData = new byte[dataContextLength+17];
        //head
        sendData[0] = Constant.DATA_HEAD[0];
        sendData[1] = Constant.DATA_HEAD[1];

        //Mac
        byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
        int macByteLength = macByte.length;
        System.arraycopy(macByte, 0, sendData, 2, macByteLength);

        //cmd
        sendData[10] = Constant.SCENE_SETTING_SEND_COMMAND[0];
        sendData[11] = Constant.SCENE_SETTING_SEND_COMMAND[1];

        //数据长度 2字节
//        sendData[12] = Byte.parseByte(""+dataContextLength); //超出范围166
//        sendData[12] = (byte) Integer.parseInt(Integer.toHexString(Integer.parseInt(""+dataContextLength)), 16);

        sendData[12] = (byte) Integer.parseInt(Integer.toHexString(dataContextLength), 16);
        sendData[13] = (byte)0x00;
        //数据内容

        System.arraycopy(doWithSendDataToGateWay, 0, sendData, 14, dataContextLength);

        //数据校验 1字节
        sendData[(dataContextLength-1)+15] = util.checkData(util.bytes2HexString(doWithSendDataToGateWay,dataContextLength));//校验位

        //tail

        sendData[(dataContextLength-1)+16] = Constant.DATA_TAIL[0];
        sendData[(dataContextLength-1)+17] = Constant.DATA_TAIL[1];

        return sendData;
    }

//    FFAA 7412007FCF5C0000 F20001000101FF55

    ////
    private byte[] refreshSceneSetting(){

        byte[] data = new byte[18];
        //head
        data[0] = Constant.DATA_HEAD[0];
        data[1] = Constant.DATA_HEAD[1];

        //Mac
        if(!TextUtils.isEmpty(spHelper.getSpGateWayMac())){
            byte[] macByte = util.HexString2Bytes(spHelper.getSpGateWayMac());
            int macByteLength = macByte.length;
            System.arraycopy(macByte, 0, data, 2, macByteLength);
        }else{
            Util.showToast(getActivity(),"没有网关的信息，请先添加设备！", Gravity.CENTER,0,0);
        }


        //cmd
        data[10] = Constant.SCENE_SETTING_SEND_COMMAND2[0];
        data[11] = Constant.SCENE_SETTING_SEND_COMMAND2[1];

        //数据长度 2字节
        data[12] = (byte)0x01;
        data[13] = (byte)0x00;
        //数据内容
        data[14] = (byte)0x01;

        //数据校验 1字节

        data[15] = (byte)0x01;//校验位

        //tail

        data[16] = Constant.DATA_TAIL[0];
        data[17] = Constant.DATA_TAIL[1];

        return data;
    }

    @Override
    public void onStart() {
        super.onStart();
        list = dBcurd.getAllSceneName();
        sceneAdapter = new SceneAdapter(getActivity(),list);
        scene_listview.setAdapter(sceneAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //添加按钮的返回结果码
        if(requestCode == Constant.INTENT_ENTER_ADD_SCENE_REQUEST_CODE && resultCode == Constant.INTENT_ENTER_ADD_SCENE_RESULT_CODE ){

        }
        //ListView的item的结果返回码
        if(requestCode == Constant.INTENT_ENTER_SCENE_REQUEST_CODE && resultCode == Constant.INTENT_ENTER_SCENE_RESULT_CODE ){

        }

    }



}
