package com.zuobiao.smarthome.smarthome3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneEquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneNameBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuangbinbin
 * on 2016/1/5.
 */
public class DBcurd implements InterfaceDBcurd {

    private static final String TAG = "DBcurd";
    private DBHelper dbHelper;
    private SQLiteDatabase databaseWrite,databaseRead;

    public DBcurd(Context context){
        dbHelper = new DBHelper(context, DBconstant.DBNAME,null,DBconstant.DBVERSION);
        //读和写分开处理
        databaseRead = dbHelper.getReadableDatabase();
        databaseWrite = dbHelper.getWritableDatabase();
    }

    @Override
    public void addData(EquipmentBean equipment) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.INSERT_EQUIPMENT_SQL, new Object[]{
                        equipment.getMac_ADDR(),
                        equipment.getShort_ADDR(),
                        equipment.getCoord_Short_ADDR(),
                        equipment.getDevice_Type(),
                        equipment.getSoftware_Edition(),
                        equipment.getHardware_Edition(),
                        equipment.getPoint_Data(),
                        equipment.getRemark()});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }

    @Override
    public void delData(String equipmentMac) {
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.DEL_EQUIPMENT_SQL, new Object[]{equipmentMac});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }

        }
    }

    @Override
    public void delALL() {
        if(databaseWrite!=null){
            databaseWrite.execSQL(DBconstant.DEL_ALL_EQUIPMENT_SQL);
        }
    }

//网关数据
// FF AA
// B7 59 0B 7F CF 5C 00 00 mac
// 01 10
// 2A 00
// 2E 16    udpPort
// 90 1F    tcpPort
// 00 05 FF FE
// 01 00 00 00
// 01 00 00 00
// 00 00
// 48 6F 6D 69 64 65 61 D6 C7 C4 DC CD F8 B9 D8 00 00 00 00 00 00 00 00 00
// 41
// FF 55

//设备数据
// FF AA 数据头
// B7 59 0B 7F CF 5C 00 00  协调器MAC
// 03 10  命令类型
// 32 00  数据长度
// 4B FF C3 07 00 4B 12 00  设备的mac
// E8 81    短地址
// A9 C9    父地址
// 12 05 FF FE  设备标识
// 00 00 00 00  软版
// 00 00 00 00  硬版
// 00 00 节点设备数据
// FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 节电设备备注信息，用于人机交互
// 48 数据校验
// FF 55 数据尾
    @Override
    public List<EquipmentBean> getAllData() {
        List<EquipmentBean> equipmentBeans = new ArrayList<>();
        EquipmentBean equipment;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.SELECT_ALL_EQUIPMENT_SQL,null);
            if(cursor.moveToFirst()){
                do{
                    equipment = new EquipmentBean();
                    equipment.setMac_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_MAC_ADDR)));
                    equipment.setShort_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_SHORT_ADDR)));
                    equipment.setCoord_Short_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_COORD_SHORT_ADDR)));
                    equipment.setDevice_Type(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_DEVICE_TYPE)));
                    equipment.setSoftware_Edition(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_SOFTWARE_EDITION)));
                    equipment.setHardware_Edition(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_HARDWARE_EDITION)));
                    equipment.setPoint_Data(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_POINT_DATA)));
                    equipment.setRemark(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_REMARK)));
                    equipmentBeans.add(equipment);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return equipmentBeans;
    }

    @Override
    public int getLength() {
        int length = 0;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.SELECT_ALL_EQUIPMENT_SQL,null);
            if(cursor.moveToFirst()){
                do{
                    length++;
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return length;
    }




    @Override
    public void updataEquipmentName(String equipmentNameHexString,String mac){
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.UPDATA_EQUIPMENT_SQL, new Object[]{equipmentNameHexString,mac});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }

        }
    }

    //暂时暂停
    @Override
    public String getNickNameByMac(String mac){
        String data = "";
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.SELECT_ONE_EQUIPMENT_BY_MAC_SQL,new String[]{ mac });
            if(cursor.moveToFirst()){
                do{
                    data =  cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_REMARK));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return data;
    }


    @Override
    public List<EquipmentBean> getAllSceneEquipments() {
        List<EquipmentBean> equipmentBeans = new ArrayList<>();
        EquipmentBean equipment;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.SELECT_ALL_EQUIPMENT_SQL,null);
            if(cursor.moveToFirst()){
                do{
                    //如果是可控的设备，才加进去
                    String deviceType = cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_DEVICE_TYPE));
                    /**
                     可控设备
                        1、面板开关 0xfeff0520 2005FFFE
                        2、智能插座 0xfeff0530 3005FFFE
                        3、窗帘 0xfeff0511 1105FFFE
                        4、窗户 0xfeff0512 1205FFFE
                        5、门锁 未知
                     */
                    if(     "2005FFFE".equalsIgnoreCase(deviceType)||
                            "3005FFFE".equalsIgnoreCase(deviceType)||
                            "1105FFFE".equalsIgnoreCase(deviceType)||
                            "1205FFFE".equalsIgnoreCase(deviceType)
                            ){

                    equipment = new EquipmentBean();
                    equipment.setMac_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_MAC_ADDR)));
                    equipment.setShort_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_SHORT_ADDR)));
                    equipment.setCoord_Short_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_COORD_SHORT_ADDR)));
                    equipment.setDevice_Type(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_DEVICE_TYPE)));
                    equipment.setSoftware_Edition(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_SOFTWARE_EDITION)));
                    equipment.setHardware_Edition(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_HARDWARE_EDITION)));
                    equipment.setPoint_Data(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_POINT_DATA)));
                    equipment.setRemark(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_REMARK)));
                    equipmentBeans.add(equipment);
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return equipmentBeans;
    }

    @Override
    public List<EquipmentBean> getAllSceneConditionEquipments() {
        List<EquipmentBean> equipmentBeans = new ArrayList<>();
        EquipmentBean equipment;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.SELECT_ALL_EQUIPMENT_SQL,null);
            if(cursor.moveToFirst()){
                do{
                    String deviceType = cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_DEVICE_TYPE));
                    /**
                     1、温湿度PM2.5 0xfeff0540 4005FFFE
                     2、人体红外 0xfeff0550 5005FFFE
                     3、烟雾传感器 0xfeff0560 6005FFFE
                     4、光照传感器 0xfeff0570 7005FFFE
                     5、门磁 0xfeff0580 8005FFFE
                     */
                    if(     "4005FFFE".equalsIgnoreCase(deviceType)||
                            "5005FFFE".equalsIgnoreCase(deviceType)||
                            "6005FFFE".equalsIgnoreCase(deviceType)||
                            "7005FFFE".equalsIgnoreCase(deviceType)||
                            "8005FFFE".equalsIgnoreCase(deviceType)
                            ){

                        equipment = new EquipmentBean();
                        equipment.setMac_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_MAC_ADDR)));
                        equipment.setShort_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_SHORT_ADDR)));
                        equipment.setCoord_Short_ADDR(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_COORD_SHORT_ADDR)));
                        equipment.setDevice_Type(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_DEVICE_TYPE)));
                        equipment.setSoftware_Edition(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_SOFTWARE_EDITION)));
                        equipment.setHardware_Edition(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_HARDWARE_EDITION)));
                        equipment.setPoint_Data(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_POINT_DATA)));
                        equipment.setRemark(cursor.getString(cursor.getColumnIndex(DBconstant.EQUIPMENT_REMARK)));
                        equipmentBeans.add(equipment);
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return equipmentBeans;
    }

//==================================场景设置数据库=========================================

    @Override
    public void addSceneData(SceneEquipmentBean sceneEquipmentBean) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.DB_INSERT_SCENE_SQL, new Object[]{
                        sceneEquipmentBean.getSceneName(),
                        sceneEquipmentBean.getEquipmentMac(),
                        sceneEquipmentBean.getEquipmentNumber(),
                        sceneEquipmentBean.getTrigger(),
                        sceneEquipmentBean.getImmediatelyTrig(),
                        sceneEquipmentBean.getTimeTrig(),
                        sceneEquipmentBean.getTimeSwitchCmd(),
                        sceneEquipmentBean.getConditionEquipmentMac(),
                        sceneEquipmentBean.getCoditionNumber(),
                        sceneEquipmentBean.getConditionValue(),
                        sceneEquipmentBean.getTrigSymbol()
                });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }

    @Override
    public void updateSceneData(SceneEquipmentBean sceneEquipmentBean) {
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();

                /**
                 DBconstant.DB_TRIGGER +" = ? ,"+
                 DBconstant.DB_TIME_TRIG +" = ? ,"+
                 DBconstant.DB_TIME_SWITCH_CMD +" = ?"+
                 DBconstant.DB_IMMEDIATELY_TRIG +" = ? ,"+
                 DBconstant.DB_CONDITION_EQUIPMENT_MAC +" = ? ,"+
                 DBconstant.DB_CONDITION_NUMBER +" = ? ,"+
                 DBconstant.DB_CONDITION_VALUE +" = ? ,"+
                 DBconstant.DB_TRIG_SYMBOL +" = ? "
                 +"where "+DBconstant.DB_EQUIPMENT_MAC+" = ?"+" and "
                 + DBconstant.DB_EQUIPMENT_NUMBER+" = ?";
                 */
                databaseWrite.execSQL(DBconstant.UPDATA_EQUIPMENT_SETTING_SQL, new Object[]{sceneEquipmentBean.getTrigger(),
                        sceneEquipmentBean.getTimeTrig(),sceneEquipmentBean.getTimeSwitchCmd(),sceneEquipmentBean.getImmediatelyTrig(),sceneEquipmentBean.getConditionEquipmentMac(),
                        sceneEquipmentBean.getCoditionNumber(),sceneEquipmentBean.getConditionValue(),sceneEquipmentBean.getTrigSymbol(),
                        sceneEquipmentBean.getEquipmentMac(), sceneEquipmentBean.getEquipmentNumber()
                });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }

        }
    }

    @Override
    public void delAllSceneData() {
        if(databaseWrite!=null){
            databaseWrite.execSQL(DBconstant.DEL_ALL_SCENE_SETTING_SQL);
        }
    }

    @Override
    public boolean isSaveSceneDataByEquipmentMac(SceneEquipmentBean sceneEquipmentBean) {
        boolean is = false;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.DB_SELECT_EQUIPMENT_SETTTINGS_BY_EQUIPMENT_MAC_SQL,new String[]{ sceneEquipmentBean.getEquipmentMac(),sceneEquipmentBean.getEquipmentNumber() });
            if(cursor.moveToFirst()){
                do{
                   is  = true;
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return is;
    }

    @Override
    public List<SceneEquipmentBean> getAllSceneEquipmentBySceneName(String sceneName){
        List<SceneEquipmentBean> list = new ArrayList<>();
        SceneEquipmentBean sceneEquipmentBean;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.DB_SELECT_EQUIPMENT_SETTTINGS_BY_SCENA_NAME_SQL,new String[]{ sceneName });
            if(cursor.moveToFirst()){
                do{
                    /**
                     private String sceneName;//场景名
                     private String equipmentMac; //场景设置中的设备Mac
                     private String equipmentNumber;//场景设置中的设备序号，比如面板开关，有3个控制。
                     private String Trigger;//触发方式
                     private String immediatelyTrig;//立即触发
                     private String timeTrig;//时间触发
                     private String conditionTrig;//条件触发
                     private String conditionEquipmentMac;//条件触发设备Mac
                     private String coditionNumber;//条件触发传感号
                     private String conditionValue;//条件值
                     private String trigSymbol;//触发符号 具体有 大于、小于、等于。
                     */
                    //11个
                    sceneEquipmentBean = new SceneEquipmentBean();
                    sceneEquipmentBean.setSceneName(cursor.getString(cursor.getColumnIndex(DBconstant.DB_SCENE_NAME)));
                    sceneEquipmentBean.setEquipmentMac(cursor.getString(cursor.getColumnIndex(DBconstant.DB_EQUIPMENT_MAC)));
                    sceneEquipmentBean.setEquipmentNumber(cursor.getString(cursor.getColumnIndex(DBconstant.DB_EQUIPMENT_NUMBER)));
                    sceneEquipmentBean.setTrigger(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TRIGGER)));
                    sceneEquipmentBean.setImmediatelyTrig(cursor.getString(cursor.getColumnIndex(DBconstant.DB_IMMEDIATELY_TRIG)));
                    sceneEquipmentBean.setTimeTrig(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TIME_TRIG)));
                    sceneEquipmentBean.setTimeSwitchCmd(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TIME_SWITCH_CMD)));
                    sceneEquipmentBean.setConditionEquipmentMac(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_EQUIPMENT_MAC)));
                    sceneEquipmentBean.setCoditionNumber(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_NUMBER)));
                    sceneEquipmentBean.setConditionValue(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_VALUE)));
                    sceneEquipmentBean.setTrigSymbol(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TRIG_SYMBOL)));
                    list.add(sceneEquipmentBean);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public List<SceneEquipmentBean> getSceneEquipmentSettingByEquipmentMac(String equipmentMac) {
        List<SceneEquipmentBean> list = new ArrayList<>();
        SceneEquipmentBean sceneEquipmentBean;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.DB_SELECT_ALL_EQUIPMENT_SETTTINGS_BY_EQUIPMENT_MAC_SQL,new String[]{ equipmentMac });
            if(cursor.moveToFirst()){
                do{
                    /**
                     private String sceneName;//场景名
                     private String equipmentMac; //场景设置中的设备Mac
                     private String equipmentNumber;//场景设置中的设备序号，比如面板开关，有3个控制。
                     private String Trigger;//触发方式
                     private String immediatelyTrig;//立即触发
                     private String timeTrig;//时间触发
                     private String conditionTrig;//条件触发
                     private String conditionEquipmentMac;//条件触发设备Mac
                     private String coditionNumber;//条件触发传感号
                     private String conditionValue;//条件值
                     private String trigSymbol;//触发符号 具体有 大于、小于、等于。
                     */
                    //11个
                    sceneEquipmentBean = new SceneEquipmentBean();
                    sceneEquipmentBean.setSceneName(cursor.getString(cursor.getColumnIndex(DBconstant.DB_SCENE_NAME)));
                    sceneEquipmentBean.setEquipmentMac(cursor.getString(cursor.getColumnIndex(DBconstant.DB_EQUIPMENT_MAC)));
                    sceneEquipmentBean.setEquipmentNumber(cursor.getString(cursor.getColumnIndex(DBconstant.DB_EQUIPMENT_NUMBER)));
                    sceneEquipmentBean.setTrigger(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TRIGGER)));
                    sceneEquipmentBean.setImmediatelyTrig(cursor.getString(cursor.getColumnIndex(DBconstant.DB_IMMEDIATELY_TRIG)));
                    sceneEquipmentBean.setTimeTrig(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TIME_TRIG)));
                    sceneEquipmentBean.setTimeSwitchCmd(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TIME_SWITCH_CMD)));
                    sceneEquipmentBean.setConditionEquipmentMac(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_EQUIPMENT_MAC)));
                    sceneEquipmentBean.setCoditionNumber(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_NUMBER)));
                    sceneEquipmentBean.setConditionValue(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_VALUE)));
                    sceneEquipmentBean.setTrigSymbol(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TRIG_SYMBOL)));
                    list.add(sceneEquipmentBean);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public void delAllEquipmentDataByEquipmentMac(String equipmentMac) {

        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.DEL_ALL_EQUIPMENT_DATA_BY_EQUIPMENT_MAC_SQL, new Object[]{equipmentMac});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }

    @Override
    public void updateSceneAddSceneName(String temp,String sceneName) {
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.UPDATE_EQUIPMENT_SETTING_BY_TEMP,
                        new Object[]{sceneName,"", temp});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }


    }


    @Override
    public void updateTemp(SceneEquipmentBean sceneEquipmentBean) {
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.UPDATA_EQUIPMENT_SETTING_TEMP_SQL, new Object[]{sceneEquipmentBean.getTemp(),
                        sceneEquipmentBean.getEquipmentMac(), sceneEquipmentBean.getEquipmentNumber()
                });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }

        }
    }

    @Override
    public List<SceneNameBean> getAllSceneName(){
        List<SceneNameBean> list = new ArrayList<>();
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.SELECT_ALL_SCENE_NAME_SQL,null);
            if(cursor.moveToFirst()){
                do{
                    SceneNameBean sceneNameBean = new SceneNameBean();
                    String data =  cursor.getString(cursor.getColumnIndex(DBconstant.DB_SCENE_NAME_NAME));
                    Log.e(TAG,"data="+data);
                    sceneNameBean.setSceneName(data);
                    list.add(sceneNameBean);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public void addSceneNameData(String name,int length) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.DB_INSERT_SCENE_NAME_SQL, new Object[]{name,length });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }



    @Override
    public void delSceneNameDataBySceneName(String sceneName) {
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.DEL_SCENE_NAME_BY_SCENE_NAME_SQL, new Object[]{sceneName});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }

        }
    }

    //场景名数据库

    //根据场景名来更改数据信息
    @Override
    public void updataSceneNameData(String name, int length,String whereName) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.DB_UPDATE_SCENE_NAME_SQL, new Object[]{name,length,whereName});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }

    @Override
    public void delALLSceneNameData() {
        if(databaseWrite!=null){
            databaseWrite.execSQL(DBconstant.DEL_ALL_SCENE_NAME_SQL);
        }
    }


    //=========================最后的场景设置=============================

    @Override
    public void addSceneAllData(SceneEquipmentBean sceneEquipmentBean) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.DB_INSERT_SCENE_ALL_SQL, new Object[]{
                        sceneEquipmentBean.getSceneName(),
                        sceneEquipmentBean.getEquipmentMac(),
                        sceneEquipmentBean.getEquipmentNumber(),
                        sceneEquipmentBean.getTrigger(),
                        sceneEquipmentBean.getImmediatelyTrig(),
                        sceneEquipmentBean.getTimeTrig(),
                        sceneEquipmentBean.getTimeSwitchCmd(),
                        sceneEquipmentBean.getConditionEquipmentMac(),
                        sceneEquipmentBean.getCoditionNumber(),
                        sceneEquipmentBean.getConditionValue(),
                        sceneEquipmentBean.getTrigSymbol()
                });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }

    @Override
    public void delSceneAllBySceneAllName(String sceneName) {
        if(databaseWrite!=null){
            try{
                databaseWrite.beginTransaction();
                databaseWrite.execSQL(DBconstant.DEL_SCENE_ALL_DATA_BY_SCENE_NAME_SQL, new Object[]{sceneName});
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }

        }
    }


    /**
     * 最后的场景数据表 根据场景名来查找数据
     * @param sceneName 场景名
     * @return 场景设置的设备信息
     */
    @Override
    public List<SceneEquipmentBean> getAllSceneEquipmentBySceneNameAll(String sceneName) {
        List<SceneEquipmentBean> list = new ArrayList<>();
        SceneEquipmentBean sceneEquipmentBean;
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.DB_SELECT_EQUIPMENT_SETTTINGS_BY_SCENA_ALL_NAME_SQL,new String[]{ sceneName });
            if(cursor.moveToFirst()){
                do{
                    /**
                     private String sceneName;//场景名
                     private String equipmentMac; //场景设置中的设备Mac
                     private String equipmentNumber;//场景设置中的设备序号，比如面板开关，有3个控制。
                     private String Trigger;//触发方式
                     private String immediatelyTrig;//立即触发
                     private String timeTrig;//时间触发
                     private String conditionTrig;//条件触发
                     private String conditionEquipmentMac;//条件触发设备Mac
                     private String coditionNumber;//条件触发传感号
                     private String conditionValue;//条件值
                     private String trigSymbol;//触发符号 具体有 大于、小于、等于。
                     */
                    //11个
                    sceneEquipmentBean = new SceneEquipmentBean();
                    sceneEquipmentBean.setSceneName(cursor.getString(cursor.getColumnIndex(DBconstant.DB_SCENE_ALL_NAME)));
                    sceneEquipmentBean.setEquipmentMac(cursor.getString(cursor.getColumnIndex(DBconstant.DB_EQUIPMENT_ALL_MAC)));
                    sceneEquipmentBean.setEquipmentNumber(cursor.getString(cursor.getColumnIndex(DBconstant.DB_EQUIPMENT_ALL_NUMBER)));
                    sceneEquipmentBean.setTrigger(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TRIGGER_ALL)));
                    sceneEquipmentBean.setImmediatelyTrig(cursor.getString(cursor.getColumnIndex(DBconstant.DB_IMMEDIATELY_TRIG_ALL)));
                    sceneEquipmentBean.setTimeTrig(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TIME_TRIG_ALL)));
                    sceneEquipmentBean.setTimeSwitchCmd(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TIME_SWITCH_CMD_ALL)));
                    sceneEquipmentBean.setConditionEquipmentMac(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_EQUIPMENT_MAC_ALL)));
                    sceneEquipmentBean.setCoditionNumber(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_NUMBER_ALL)));
                    sceneEquipmentBean.setConditionValue(cursor.getString(cursor.getColumnIndex(DBconstant.DB_CONDITION_VALUE_ALL)));
                    sceneEquipmentBean.setTrigSymbol(cursor.getString(cursor.getColumnIndex(DBconstant.DB_TRIG_SYMBOL_ALL)));
                    list.add(sceneEquipmentBean);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public void addRfidInfo(String rfidInfo) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.DB_INSERT_RFID_INFO_SQL, new Object[]{
                        rfidInfo
                });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }

    @Override
    public void delRfidInfo(String rfidInfo) {
        if(databaseWrite!=null){
            databaseWrite.beginTransaction();//开启事务
            try{
                databaseWrite.execSQL(DBconstant.DB_DEL_RFID_INFO_BY_CARD_INFO_SQL, new Object[]{
                        rfidInfo
                });
                databaseWrite.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                databaseWrite.endTransaction();
            }
        }
    }



    @Override
    public List<String> getAllRfidInfo() {
        List<String> list = new ArrayList<>();
        Cursor cursor;
        if(databaseRead!=null){
            cursor = databaseRead.rawQuery(DBconstant.DB_SELECT_ALL_RFID_INFO_SQL,null);
            if(cursor.moveToFirst()){
                do{
                    String rfidInfo =   cursor.getString(cursor.getColumnIndex(DBconstant.DB_RFID_INFO_CARD_INFO));
                    list.add(rfidInfo);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }


}
