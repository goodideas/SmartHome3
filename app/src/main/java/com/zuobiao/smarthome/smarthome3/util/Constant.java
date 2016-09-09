package com.zuobiao.smarthome.smarthome3.util;

import com.zuobiao.smarthome.smarthome3.R;

public class Constant {
    public static final int BTN_FLAG_EQUIPMENT = 0x01;
    public static final int BTN_FLAG_SCENE = 0x01 << 1;
    public static final int BTN_FLAG_SETTING = 0x01 << 3;
    public static final int HANDLER_ONLINE = 0x123;
    public static final int HANDLER_SEARCH_GATEWAY = 0x124;
    public static final int SEARCH_GATEWAY_WAIT_MAX_TIME = 2;//查找网关最大等待时间
    public static final int WEICOM_ACTIVITY_SEARCH_GATEWAY_WAIT_MAX_TIME = 1;//查找网关最大等待时间
    public static final int REFRESH_EQUIPMENT_WAIT_MAX_TIME = 2;//刷新设备最大等待时间
    public static final int ADD_EQUIPMENT_WAIT_MAX_TIME = 2;//添加设备最大等待时间
    public static final int HANDLER_REFRESH_EQUIPMENT_GATE_DATA = 0x125;
    public static final int HANDLER_REFRESH_EQUIPMENT_ADD_DATA = 0x126;
    public static final int HANDLER_REFRESH_EQUIPMENT_NO_ANSWER = 0x127;
    public static final int HANDLER_ADD_EQUIPMENT_NO_ANSWER = 0x128;
    public static final int HANDLER_ADD_EQUIPMENT_ADD_DATA = 0x129;
    public static final int HANDLER_SWITCHS_NO_ANSWER = 0x201;
    public static final int HANDLER_SWITCHS_HAS_ANSWER = 0x202;
    public static final int HANDLER_SWITCHS_HAS_ANSWER2 = 0x203;
    public static final int HANDLER_LIGHT_SERSON_HAS_ANSWER = 0x204;
    public static final int HANDLER_INFRARED_HAS_ANSWER = 0x205;
    public static final int HANDLER_SOCKETS_HAS_ANSWER = 0x206;
    public static final int HANDLER_SOCKETS_HAS_ANSWER2 = 0x207;
    public static final int HANDLER_TEMP_PM25_HAS_ANSWER = 0x208;
    public static final int HANDLER_INFLAMMABLE_GAS_HAS_ANSWER = 0x209;
    public static final int HANDLER_SWITCHS_HAS_ANSWER3 = 0x210;
    public static final int  HANDLER_SOCKETS_HAS_ANSWER3 = 0x211;
    public static final int  HANDLER_LIGHT_SERSON_HAS_ANSWER2 = 0x212;
    public static final int  HANDLER_INFRARED_HAS_ANSWER3 = 0x213;
    public static final int  HANDLER_TEMP_PM25_HAS_ANSWER3 = 0x214;
    public static final int  HANDLER_INFLAMMABLE_GAS_HAS_ANSWER3 = 0x215;
    public static final int  HANDLER_DOOR_MAGNET_HAS_ANSWER = 0x216;
    public static final int  HANDLER_DOOR_MAGNET_HAS_ANSWER2 = 0x217;
    public static final int  HANDLER_MODIFY_EQUIPMENT_NAME_HAS_ANSWER = 0x218;
    public static final int  HANDLER_CURTAINS_HAS_ANSWER = 0x219;
    public static final int  HANDLER_SCENE_SETTING_HAS_ANSWER = 0x220;
    public static final int  HANDLER_SCENE_SETTING_HAS_ANSWER2 = 0x221;

    public static final int  HANDLER_ADD_RFID_INFO_HAS_ANSWER = 0x222;
    public static final int  HANDLER_DEL_RFID_INFO_HAS_ANSWER = 0x223;


    public static final int QR_RFID_RESULT_CODE = 0x224;
    public static final String QR_RFID_RESULT_DATA = "rfidInfo";

    public static final int  HANDLER_WINDOW_NO_ANSWER = 0x225;
    public static final int  HANDLER_WINDOW_HAS_ANSWER = 0x226;
    public static final int  HANDLER_CURTAINS_NO_ANSWER = 0x227;

    public static final int  HANDLER_NOISE_SERSON_HAS_ANSWER = 0x228;
    public static final int  HANDLER_NOISE_SERSON_HAS_ANSWER2 = 0x229;

    public static final int BEFOREINTO_SWITCH_MAX_TIME = 1;
    public static final int BEFOREINTO_WINDOW_MAX_TIME = 1;
    public static final int BEFOREINTO_CURTAINS_MAX_TIME = 1;

    //Fragment的标识
    public static final String FRAGMENT_FLAG_EQUIPMENT = "设备";
    public static final String FRAGMENT_FLAG_SCENE = "场景";
    public static final String FRAGMENT_FLAG_SETTING = "设置";

    //设备标识
    public static final String GATEWAY = "0005fffe";     //网关
    public static final String CONTROL_MODULE = "1005fffe"; //智能控制模块 feff0510
    public static final String SWITCHES = "2005fffe";        //面板开关 feff0520 2005fffe
    public static final String SOCKETS = "3005fffe";        //插座 feff0530
    public static final String TEMP_PM25 = "4005fffe";    //温湿度、PM2.5 feff0540
    public static final String INFRARED = "5005fffe";        //热释传感器 feff0550
    public static final String INFLAMMABLE_GAS = "6005fffe";    //可燃性气体 //烟雾 feff0560
    public static final String LIGHT_SENSOR = "7005fffe";    //光照传感器 feff0570
    public static final String DOOR_MAGNET = "8005fffe";    //门磁 feff0580
    public static final String CURTAINS = "1105fffe";        //窗帘 feff0511
    public static final String WINDOW = "1205fffe";        //窗 feff0512
    public static final String NOISE_SENSOR = "9005fffe";//噪音传感器
//    public static final String TEST = "FFFFFFFF";        //测试


    //设备名字
    public static final String GATEWAY_STRING = "网关";        //网关
    public static final String CONTROL_MODULE_STRING = "智能控制模块";        //网关
    public static final String SWITCHES_STRING = "面板开关";        //面板开关
    public static final String SOCKETS_STRING = "插座";        //插座
    public static final String TEMP_PM25_STRING = "温湿度、PM2.5";    //温湿度、PM2.5
    public static final String INFRARED_STRING = "热释传感器";        //热释传感器
    public static final String INFLAMMABLE_GAS_STRING = "可燃性气体";    //可燃性气体
    public static final String LIGHT_SENSOR_STRING = "光照传感器";    //光照传感器
    public static final String DOOR_MAGNET_STRING = "门磁";            //门磁
    public static final String CURTAINS_STRING = "智能窗帘";        //窗帘
    public static final String WINDOW_STRING = "智能门/窗";        //窗
    public static final String NOISE_STRING = "噪音传感器";        //噪音

    //图片id
    public static final int DRAWABLE_CONTROL_MODULE = R.drawable.magnet;
    public static final int DRAWABLE_DOOR_MAGNET = R.drawable.door_magnet;
    public static final int DRAWABLE_CURTAINS = R.drawable.curtains;
    public static final int DRAWABLE_GATEWAY = R.drawable.gateway;
    public static final int DRAWABLE_INFLAMMABLE_GAS = R.drawable.inflammable_gas;
    public static final int DRAWABLE_INFRARED = R.drawable.infrared;
    public static final int DRAWABLE_LIGHT_SENSOR = R.drawable.light_sensor;
    public static final int DRAWABLE_SOCKETS = R.drawable.sockets;
    public static final int DRAWABLE_WINDOW = R.drawable.window;
    public static final int DRAWABLE_TEMP_PM25 = R.drawable.temp_pm25;
    public static final int DRAWABLE_SWITCHES = R.drawable.switches;
    public static final int DRAWABLE_NOISE = R.drawable.noise;


//    命令类型

    public static final String GATEWAY_RECV_COMMAND = "0110";
    public static final String REFRESH_EQUIPMENT_RECV_COMMAND = "0210";
    public static final String REFRESH_EQUIPMENT_RECV2_COMMAND = "0310";
    public static final String ADD_EQUIPMENT_RECV_COMMAND = "0410";
    public static final String MODIFY_EQUIPMENT_NAME_RECV_COMMAND = "0510";

    public static final String ADD_RFID_INFO_RECV_COMMAND = "0610";
    public static final String DEL_RFID_INFO_RECV_COMMAND = "0710";

    public static final String WINDOW_RECV_COMMAND = "1010";


    public static final String SWITCH_RECV_COMMAND = "2110";
    public static final String SWITCH_RECV2_COMMAND = "2150";
    public static final String SOCKETS_RECV_COMMAND = "3110";
    public static final String SOCKETS_RECV2_COMMAND = "3150";
    public static final String TEMP_PM25_RECV2_COMMAND = "4150";
    public static final String INFRARED_RECV_COMMAND = "5150";
    public static final String INFLAMMABLE_GAS_RECV2_COMMAND = "6150";
    public static final String LIGHT_SENSOR_RECV2_COMMAND = "7150";
    public static final String DOOR_MAGNET_RECV_COMMAND = "8150";
    public static final String NOISE_SENSOR_RECV2_COMMAND = "9150";
    public static final String SWITCH_RECV3_COMMAND = "2010";
    public static final String SOCKETS_RECV3_COMMAND = "3010";
    public static final String TEMP_PM25_RECV3_COMMAND = "4010";
    public static final String INFRARED_RECV3_COMMAND = "5010";
    public static final String INFLAMMABLE_GAS_RECV3_COMMAND = "6010";
    public static final String LIGHT_SONSER_RECV2_COMMAND = "7010";
    public static final String DOOR_MAGNET_RECV2_COMMAND = "8010";
    public static final String NOISE_SONSER_RECV2_COMMAND = "9010";
    public static final String CURTAINS_RECV_COMMAND = "1210";
    public static final String SCENE_SETTING_RECV_COMMAND = "F110";
    public static final String SCENE_SETTING_RECV2_COMMAND = "F210";


    public static final byte[] DATA_HEAD = {(byte)0xFF,(byte)0xAA};

    public static final byte[] DATA_TAIL = {(byte)0xFF,(byte)0x55};

    public static final byte[] GATEWAY_SEND_COMMAND = {(byte)0x01,(byte)0x00};

    public static final byte[] REFRESH_EQUIPMENT_SEND_COMMAND = {(byte)0x02,(byte)0x00};

    public static final byte[] ADD_EQUIPMENT_SEND_COMMAND = {(byte)0x04,(byte)0x00};

    public static final byte[] MODEFY_EQUIPMENT_NAME_SEND_COMMAND = {(byte)0x05,(byte)0x00};

    public static final byte[] SWITCHS_SEND_COMMAND = {(byte)0x21,(byte)0x00};
    public static final byte[] SOCKETS_SEND_COMMAND = {(byte)0x31,(byte)0x00};

    public static final byte[] SWITCHS_SEND2_COMMAND = {(byte)0x20,(byte)0x00};
    public static final byte[] SOCKETS_SEND2_COMMAND = {(byte)0x30,(byte)0x00};
    public static final byte[] TEMP_PM25_SEND2_COMMAND = {(byte)0x40,(byte)0x00};
    public static final byte[] INFRARED_SEND_COMMAND = {(byte)0x50,(byte)0x00};
    public static final byte[] INFLAMMABLE_GAS_SEND_COMMAND = {(byte)0x60,(byte)0x00};
    public static final byte[] LIGHT_SENSOR_SEND2_COMMAND = {(byte)0x70,(byte)0x00};
    public static final byte[] DOOR_MAGNET_SEND_COMMAND = {(byte)0x80,(byte)0x00};
    public static final byte[] NOISE_SENSOR_SEND2_COMMAND = {(byte)0x90,(byte)0x00};

    public static final byte[] WINDOW_SEND_COMMAND = {(byte)0x10,(byte)0x00};


    public static final byte[] SCENE_SETTING_SEND_COMMAND = {(byte)0xF1,(byte)0x00};

    public static final byte[] CURITAINS_SEND_COMMAND = {(byte)0x12,(byte)0x00};
    public static final byte[] SCENE_SETTING_SEND_COMMAND2 = {(byte)0xF2,(byte)0x00};
    public static final byte[]  RFID_INFO_SEND_ADD = {(byte)0x06,(byte)0x00};
    public static final byte[]  RFID_INFO_SEND_DEL = {(byte)0x07,(byte)0x00};

    /**
     * 根据设备标识得到图片id
     * @param deviceType 设备类型名字
     * @return imageId 返回的图片id
     */
    public static int getImageId(String deviceType){

        int imageId = 0;
        if(GATEWAY.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_GATEWAY;
        }
        if(CONTROL_MODULE.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_CONTROL_MODULE;
        }
        if(SWITCHES.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_SWITCHES;
        }
        if(SOCKETS.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_SOCKETS;
        }
        if(TEMP_PM25.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_TEMP_PM25;
        }
        if(INFRARED.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_INFRARED;
        }
        if(INFLAMMABLE_GAS.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_INFLAMMABLE_GAS;
        }
        if(LIGHT_SENSOR.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_LIGHT_SENSOR;
        }
        if(DOOR_MAGNET.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_DOOR_MAGNET;
        }
        if(CURTAINS.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_CURTAINS;
        }
        if(WINDOW.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_WINDOW;
        }
        if(NOISE_SENSOR.equalsIgnoreCase(deviceType)){
            imageId = DRAWABLE_NOISE;
        }

//        if(TEST.equalsIgnoreCase(deviceType)){
//            imageId = R.drawable.test;
//        }
        return imageId;

    }



    /**
     * 根据设备标识得到类型字符串
     * @param deviceType 设备类型名字
     * @return equipmentTypeName 返回的类型字符串
     */
    public static String getTypeName(String deviceType){
        String equipmentTypeName = "";
        if(GATEWAY.equalsIgnoreCase(deviceType)){
            equipmentTypeName = GATEWAY_STRING;
        }
        if(CONTROL_MODULE.equalsIgnoreCase(deviceType)){
            equipmentTypeName = CONTROL_MODULE_STRING;
        }
        if(SWITCHES.equalsIgnoreCase(deviceType)){
            equipmentTypeName = SWITCHES_STRING;
        }
        if(SOCKETS.equalsIgnoreCase(deviceType)){
            equipmentTypeName = SOCKETS_STRING;
        }
        if(TEMP_PM25.equalsIgnoreCase(deviceType)){
            equipmentTypeName = TEMP_PM25_STRING;
        }
        if(INFRARED.equalsIgnoreCase(deviceType)){
            equipmentTypeName = INFRARED_STRING;
        }
        if(INFLAMMABLE_GAS.equalsIgnoreCase(deviceType)){
            equipmentTypeName = INFLAMMABLE_GAS_STRING;
        }
        if(LIGHT_SENSOR.equalsIgnoreCase(deviceType)){
            equipmentTypeName = LIGHT_SENSOR_STRING;
        }
        if(DOOR_MAGNET.equalsIgnoreCase(deviceType)){
            equipmentTypeName = DOOR_MAGNET_STRING;
        }
        if(CURTAINS.equalsIgnoreCase(deviceType)){
            equipmentTypeName = CURTAINS_STRING;
        }
        if(WINDOW.equalsIgnoreCase(deviceType)){
            equipmentTypeName = WINDOW_STRING;
        }
        if(NOISE_SENSOR.equalsIgnoreCase(deviceType)){
            equipmentTypeName = NOISE_STRING;
        }

        return equipmentTypeName;
    }

    //点击的按钮是添加按钮
    public static final int INTENT_ENTER_ADD_SCENE_REQUEST_CODE = 0x301;
    //添加按钮activity返回的结果码
    public static final int INTENT_ENTER_ADD_SCENE_RESULT_CODE = 0x311;
    //点击的是ListView的item
    public static final int INTENT_ENTER_SCENE_REQUEST_CODE = 0x302;
    //ListView的item的activity返回的结果码
    public static final int INTENT_ENTER_SCENE_RESULT_CODE = 0x312;



//    public static final int EQUIPMENT_TYPE_SWITCH_REQUEST_CODE = 0x401;
//
//    public static final int EQUIPMENT_TYPE_SWITCH_RESULT_CODE = 0x411;
//
//    public static final int EQUIPMENT_TYPE_SOCKET_REQUEST_CODE = 0x402;
//
//    public static final int EQUIPMENT_TYPE_SOCKET_RESULT_CODE = 0x412;

    public static final int EQUIPMENT_TYPE_REQUEST_CODE = 0x403;
    public static final int EQUIPMENT_TYPE_RESULT_CODE = 0x413;

    public static final String ADD_SCENE_ACTIVITY_2_SOCKET_SETTING_ACTIVITY = "equipmentBean";
    public static final String ADD_SCENE_ACTIVITY_2_SOCKET_SETTING_ACTIVITY_POSITION = "position";


}

