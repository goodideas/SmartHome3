package com.zuobiao.smarthome.smarthome3.db;

/**
 * Created by zhuangbinbin
 * on 2016/1/5.
 */
public class DBconstant {
    public static final String TABLENAME = "equipmentlist";
    public static final String DBNAME = "EquipmentBean.db";

    public static int DBVERSION = 6;

    public static final String EQUIPMENT_MAC_ADDR = "mac_addr";
    public static final String EQUIPMENT_SHORT_ADDR = "short_addr";
    public static final String EQUIPMENT_COORD_SHORT_ADDR = "coord_short_addr";
    public static final String EQUIPMENT_DEVICE_TYPE = "device_type";
    public static final String EQUIPMENT_SOFTWARE_EDITION = "software_edition";
    public static final String EQUIPMENT_HARDWARE_EDITION = "hardware_edition";
    public static final String EQUIPMENT_POINT_DATA = "point_data";
    public static final String EQUIPMENT_REMARK = "remark";

    public static final String CREATEE_QUIPMENT_DB_SQL = "create table "+TABLENAME+"("
//            +ID+" integer primary key autoincrement,"
//            +EQUIPMENT_MAC_ADDR+" varchar(20),"
            +EQUIPMENT_MAC_ADDR+" varchar(20) primary key,"
            +EQUIPMENT_SHORT_ADDR+" varchar(20),"
            +EQUIPMENT_COORD_SHORT_ADDR+" varchar(20),"
            +EQUIPMENT_DEVICE_TYPE+" varchar(20),"
            +EQUIPMENT_SOFTWARE_EDITION+" varchar(20),"
            +EQUIPMENT_HARDWARE_EDITION+" varchar(20),"
            +EQUIPMENT_POINT_DATA+" varchar(20),"
            +EQUIPMENT_REMARK+" varchar(48)"
            +")";

    public static final String INSERT_EQUIPMENT_SQL = "insert into "+ DBconstant.TABLENAME+"("
            +DBconstant.EQUIPMENT_MAC_ADDR+","
            +DBconstant.EQUIPMENT_SHORT_ADDR+","
            +DBconstant.EQUIPMENT_COORD_SHORT_ADDR+","
            +DBconstant.EQUIPMENT_DEVICE_TYPE+","
            +DBconstant.EQUIPMENT_SOFTWARE_EDITION+","
            +DBconstant.EQUIPMENT_HARDWARE_EDITION+","
            +DBconstant.EQUIPMENT_POINT_DATA+","
            +DBconstant.EQUIPMENT_REMARK+
            ") values (?,?,?,?,?,?,?,?)";

    public static final String SELECT_ALL_EQUIPMENT_SQL = "select * from "+DBconstant.TABLENAME;

    public static final String UPDATA_EQUIPMENT_SQL = "update "+DBconstant.TABLENAME+" set "+DBconstant.EQUIPMENT_REMARK +" = ? where "+DBconstant.EQUIPMENT_MAC_ADDR+" = ?";

    //根据mac查找数据库中的数据
    public static final String SELECT_ONE_EQUIPMENT_BY_MAC_SQL = "select * from "+DBconstant.TABLENAME+" where "+DBconstant.EQUIPMENT_MAC_ADDR+" = ?";

    public static final String DEL_ALL_EQUIPMENT_SQL = "delete from "+DBconstant.TABLENAME;

    public static final String DEL_EQUIPMENT_SQL = "delete from "+DBconstant.TABLENAME
            +" where "
            +DBconstant.EQUIPMENT_MAC_ADDR+" = ?";



    //场景设置DB ==========================================================================

    public static final String DB_SCCENE_TABLENAME = "scene_equipments_list";

    public static final String DB_SCENE_ID = "sid";
    public static final String DB_SCENE_NAME = "sceneName";
    public static final String DB_EQUIPMENT_MAC = "equipmentMac";
    public static final String DB_EQUIPMENT_NUMBER = "equipmentNumber";
    public static final String DB_TRIGGER = "Trigger";
    public static final String DB_IMMEDIATELY_TRIG = "immediatelyTrig";
    public static final String DB_TIME_TRIG = "timeTrig";
    public static final String DB_TIME_SWITCH_CMD = "timeSwitchCmd";
    public static final String DB_CONDITION_EQUIPMENT_MAC = "conditionEquipmentMac";
    public static final String DB_CONDITION_NUMBER = "coditionNumber";
    public static final String DB_CONDITION_VALUE = "conditionValue";
    public static final String DB_TRIG_SYMBOL = "trigSymbol";


    public static final String DB_CREATEE_SCENE_SQL = "create table "+DB_SCCENE_TABLENAME+"("
            +DB_SCENE_ID+" integer primary key autoincrement,"
            +DB_SCENE_NAME+" varchar(48),"
            +DB_EQUIPMENT_MAC+" varchar(20),"
            +DB_EQUIPMENT_NUMBER+" varchar(20),"
            +DB_TRIGGER+" varchar(20),"
            +DB_IMMEDIATELY_TRIG+" varchar(20),"
            +DB_TIME_TRIG+" varchar(20),"
            + DB_TIME_SWITCH_CMD +" varchar(20),"
            +DB_CONDITION_EQUIPMENT_MAC+" varchar(20),"
            +DB_CONDITION_NUMBER+" varchar(20),"
            +DB_CONDITION_VALUE+" varchar(20),"
            +DB_TRIG_SYMBOL+" varchar(20)"
            +")";

    public static final String DB_INSERT_SCENE_SQL = "insert into "+ DBconstant.DB_SCCENE_TABLENAME+"("
            +DBconstant.DB_SCENE_NAME+","
            +DBconstant.DB_EQUIPMENT_MAC+","
            +DBconstant.DB_EQUIPMENT_NUMBER+","
            +DBconstant.DB_TRIGGER+","
            +DBconstant.DB_IMMEDIATELY_TRIG+","
            +DBconstant.DB_TIME_TRIG+","
            +DBconstant.DB_TIME_SWITCH_CMD +","
            +DBconstant.DB_CONDITION_EQUIPMENT_MAC+","
            +DBconstant.DB_CONDITION_NUMBER+","
            +DBconstant.DB_CONDITION_VALUE+","
            +DBconstant.DB_TRIG_SYMBOL+
            ") values (?,?,?,?,?,?,?,?,?,?,?)";

    public static final String DB_ALTER_DB_COLUMN = "temp";

    public static final String DB_ALTER_SCENE_SQL = "alter table "+ DBconstant.DB_SCCENE_TABLENAME +" add column "+DB_ALTER_DB_COLUMN +" varchar(20)";


    //根据场景名查找数据库中的数据
    public static final String DB_SELECT_EQUIPMENT_SETTTINGS_BY_SCENA_NAME_SQL = "select * from "+DBconstant.DB_SCCENE_TABLENAME+" where "+DBconstant.DB_SCENE_NAME+" = ?";

    public static final String DB_SELECT_ALL_EQUIPMENT_SETTTINGS_BY_EQUIPMENT_MAC_SQL = "select * from "+DBconstant.DB_SCCENE_TABLENAME+" where "+DBconstant.DB_EQUIPMENT_MAC+" = ?";


    //查询数据库有没有存过这个数据
    public static final String DB_SELECT_EQUIPMENT_SETTTINGS_BY_EQUIPMENT_MAC_SQL = "select * from "+DBconstant.DB_SCCENE_TABLENAME+" where "+DBconstant.DB_EQUIPMENT_MAC+" = ? and "+DBconstant.DB_EQUIPMENT_NUMBER + " = ?" ;

    //更改场景设备设置的数据
    public static final String UPDATA_EQUIPMENT_SETTING_SQL = "update "+DBconstant.DB_SCCENE_TABLENAME+" set "+


            DBconstant.DB_TRIGGER +" = ? ,"+
            DBconstant.DB_TIME_TRIG +" = ? ,"+
            DBconstant.DB_TIME_SWITCH_CMD +" = ?,"+
            DBconstant.DB_IMMEDIATELY_TRIG +" = ? ,"+
            DBconstant.DB_CONDITION_EQUIPMENT_MAC +" = ? ,"+
            DBconstant.DB_CONDITION_NUMBER +" = ? ,"+
            DBconstant.DB_CONDITION_VALUE +" = ? ,"+
            DBconstant.DB_TRIG_SYMBOL +" = ? "
            +"where "+DBconstant.DB_EQUIPMENT_MAC+" = ?"+" and "
            + DBconstant.DB_EQUIPMENT_NUMBER+" = ?";


    public static final String UPDATA_EQUIPMENT_SETTING_TEMP_SQL = "update "+DBconstant.DB_SCCENE_TABLENAME+" set "+
            DBconstant.DB_ALTER_DB_COLUMN +" = ?"+
            "where "+DBconstant.DB_EQUIPMENT_MAC+" = ?"+" and "
            + DBconstant.DB_EQUIPMENT_NUMBER+" = ?";

    public static final String UPDATE_EQUIPMENT_SETTING_BY_TEMP = "update "+DBconstant.DB_SCCENE_TABLENAME+" set "+
            DBconstant.DB_SCENE_NAME +" = ?,"+
            DBconstant.DB_ALTER_DB_COLUMN +" = ?"+
            "where "+DBconstant.DB_ALTER_DB_COLUMN+" = ?";

    //删除同一设备的设置信息
    public static final String DEL_ALL_EQUIPMENT_DATA_BY_EQUIPMENT_MAC_SQL = "delete from "+DBconstant.DB_SCCENE_TABLENAME+" where "
            +DBconstant.DB_EQUIPMENT_MAC +" = ?";

    public static final String DEL_ALL_SCENE_SETTING_SQL = "delete from "+DBconstant.DB_SCCENE_TABLENAME;
    public static final String SELECT_ALL_SCENE_SQL = "select * from "+DBconstant.TABLENAME+" where "+DBconstant.DB_SCENE_NAME+" = ?";


    //场景名DB================================================================
    public static final String DB_SCCENE_NAME_TABLENAME = "scene_name_list";
    public static final String DB_SCENE_NAME_ID = "sid";
    public static final String DB_SCENE_NAME_NAME = "sceneName";
    public static final String DB_SCENE_NAME_EQUIPMENT_LENGTH = "equipmentNumbers";
    public static final String DB_CREATEE_SCENE_NAME_SQL = "create table "+DB_SCCENE_NAME_TABLENAME+"("
            +DB_SCENE_NAME_ID+" integer primary key autoincrement,"
            +DB_SCENE_NAME_NAME+" varchar(48),"
            +DB_SCENE_NAME_EQUIPMENT_LENGTH+" integer"
            +")";

    public static final String DB_INSERT_SCENE_NAME_SQL = "insert into "+ DBconstant.DB_SCCENE_NAME_TABLENAME+"("
            +DBconstant.DB_SCENE_NAME_NAME+","
            +DBconstant.DB_SCENE_NAME_EQUIPMENT_LENGTH
            +") values (?,?)";



    public static final String DB_UPDATE_SCENE_NAME_SQL = "update "+ DBconstant.DB_SCCENE_NAME_TABLENAME+" set "+
            DBconstant.DB_SCENE_NAME_NAME +" = ?,"+
            DBconstant.DB_SCENE_NAME_EQUIPMENT_LENGTH +" = ?"+
            " where "+DBconstant.DB_SCENE_NAME_NAME +" = ?";


    public static final String SELECT_ALL_SCENE_NAME_SQL = "select * from "+DBconstant.DB_SCCENE_NAME_TABLENAME;

    public static final String DEL_ALL_SCENE_NAME_SQL = "delete from "+DBconstant.DB_SCCENE_NAME_TABLENAME;

    public static final String DEL_SCENE_NAME_BY_SCENE_NAME_SQL = "delete from "+DBconstant.DB_SCCENE_NAME_TABLENAME
            +" where "
            +DBconstant.DB_SCENE_NAME_NAME+" = ?";


    //最后要存储的场景DB================================================================
    public static final String DB_SCENE_ALL_TABLENAME = "scene_equipments_list_all";

    public static final String DB_SCENE_ALL_ID = "said";
    public static final String DB_SCENE_ALL_NAME = "sceneAllName";
    public static final String DB_EQUIPMENT_ALL_MAC = "equipmentAllMac";
    public static final String DB_EQUIPMENT_ALL_NUMBER = "equipmentAllNumber";
    public static final String DB_TRIGGER_ALL = "TriggerAll";
    public static final String DB_IMMEDIATELY_TRIG_ALL = "immediatelyTrigAll";
    public static final String DB_TIME_TRIG_ALL = "timeTrigAll";
    public static final String DB_TIME_SWITCH_CMD_ALL = "timeSwitchCmdAll";
    public static final String DB_CONDITION_EQUIPMENT_MAC_ALL = "conditionEquipmentMacAll";
    public static final String DB_CONDITION_NUMBER_ALL = "coditionNumberAll";
    public static final String DB_CONDITION_VALUE_ALL = "conditionValueAll";
    public static final String DB_TRIG_SYMBOL_ALL = "trigSymbolAll";

    public static final String DB_CREATEE_SCENE_ALL_SQL = "create table "+DB_SCENE_ALL_TABLENAME+"("
            +DB_SCENE_ALL_ID+" integer primary key autoincrement,"
            +DB_SCENE_ALL_NAME+" varchar(48),"
            +DB_EQUIPMENT_ALL_MAC+" varchar(20),"
            +DB_EQUIPMENT_ALL_NUMBER+" varchar(20),"
            +DB_TRIGGER_ALL+" varchar(20),"
            +DB_IMMEDIATELY_TRIG_ALL+" varchar(20),"
            +DB_TIME_TRIG_ALL+" varchar(20),"
            + DB_TIME_SWITCH_CMD_ALL +" varchar(20),"
            +DB_CONDITION_EQUIPMENT_MAC_ALL+" varchar(20),"
            +DB_CONDITION_NUMBER_ALL+" varchar(20),"
            +DB_CONDITION_VALUE_ALL+" varchar(20),"
            +DB_TRIG_SYMBOL_ALL+" varchar(20)"
            +")";

    public static final String DB_INSERT_SCENE_ALL_SQL = "insert into "+ DBconstant.DB_SCENE_ALL_TABLENAME+"("
            +DBconstant.DB_SCENE_ALL_NAME+","
            +DBconstant.DB_EQUIPMENT_ALL_MAC+","
            +DBconstant.DB_EQUIPMENT_ALL_NUMBER+","
            +DBconstant.DB_TRIGGER_ALL+","
            +DBconstant.DB_IMMEDIATELY_TRIG_ALL+","
            +DBconstant.DB_TIME_TRIG_ALL+","
            +DBconstant.DB_TIME_SWITCH_CMD_ALL +","
            +DBconstant.DB_CONDITION_EQUIPMENT_MAC_ALL+","
            +DBconstant.DB_CONDITION_NUMBER_ALL+","
            +DBconstant.DB_CONDITION_VALUE_ALL+","
            +DBconstant.DB_TRIG_SYMBOL_ALL+
            ") values (?,?,?,?,?,?,?,?,?,?,?)";

    //根据场景名来删除数据
    public static final String DEL_SCENE_ALL_DATA_BY_SCENE_NAME_SQL = "delete from "+DBconstant.DB_SCENE_ALL_TABLENAME+" where "
            +DBconstant.DB_SCENE_ALL_NAME +" = ?";

    public static final String DB_SELECT_EQUIPMENT_SETTTINGS_BY_SCENA_ALL_NAME_SQL = "select * from "+DBconstant.DB_SCENE_ALL_TABLENAME+" where "+DBconstant.DB_SCENE_ALL_NAME+" = ?";


    //======================================================================================================
    public static final String DB_RFID_INFO_TABLENAME = "rfidInfo";
    public static final String DB_RFID_INFO_ID = "rid";
    public static final String DB_RFID_INFO_CARD_INFO = "rfids";



    public static final String DB_CREATEE_RFID_INFO_SQL = "create table "+DB_RFID_INFO_TABLENAME+"("
            +DB_RFID_INFO_ID+" integer primary key autoincrement,"
            +DB_RFID_INFO_CARD_INFO+" varchar(20)"
            +")";

    public static final String DB_INSERT_RFID_INFO_SQL = "insert into "+ DBconstant.DB_RFID_INFO_TABLENAME+"("
            +DBconstant.DB_RFID_INFO_CARD_INFO+
            ") values (?)";
    public static final String DB_DEL_RFID_INFO_BY_CARD_INFO_SQL = "delete from "+DBconstant.DB_RFID_INFO_TABLENAME+" where "
            +DBconstant.DB_RFID_INFO_CARD_INFO +" = ?";

    public static final String DB_SELECT_ALL_RFID_INFO_SQL = "select * from "+DBconstant.DB_RFID_INFO_TABLENAME;
}
