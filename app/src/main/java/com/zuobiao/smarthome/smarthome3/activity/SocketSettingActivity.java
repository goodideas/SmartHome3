package com.zuobiao.smarthome.smarthome3.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.ConditionSpinnerAdapter;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneEquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 根据传过来的参数来决定显示什么界面
 * 有两种类型，一种是只有一个控制开关的，另外一种是多种控制开关的。
 * 设备进行详细控制的页面，设置完成后传递给上个activity。第二次进来时，？显示上次的设置还是未设置页面
 */
public class SocketSettingActivity extends StatusActivity {

    private static final String TAG = "SocketSettingActivity";
    //设置一个参数，用来把这些数据串起来，之后再把这个参数删掉，最好是比较唯一的参数
    public static final String DB_TEMP = "com.zuobiao.smarthome";

    private SceneEquipmentBean sceneEquipmentBean0;
    private SceneEquipmentBean sceneEquipmentBean1;
    private SceneEquipmentBean sceneEquipmentBean2;

    private static final String[] TRIGGERS = {"无", "立即", "时间", "条件"};

    private static final String[] SYMBOLS = {"大于", "等于", "小于"};
    private static final String[] SYMBOLS2 = {"开", "关"};
    private static final String[] SYMBOLS3 = {"有人", "没人"};
    private static final String[] TEMP25 = {"温度", "湿度", "PM2.5"};

    private static final String TRIGGER_OF_TIME = "Time";
    private static final String TRIGGER_OF_IMMEDIATELY = "Immediately";
    private static final String TRIGGER_OF_CONDITION = "Condition";


//    0：立即触发；1：时间触发；2：条件触发

    private LinearLayout llSwitchOne;
    private LinearLayout llSwitchTwo;
    private LinearLayout llSwitchThree;
    private Button btnAddSceneBack;
    private Button btnAddSceneSure;

    private List<String> triggerList;
    private List<String> symbolList;
    private List<String> temp25List;
    private ConditionSpinnerAdapter conditionSpinnerAdapter;
    private DBcurd DBcurd;
    private EquipmentBean equipmentBeanActivity;
    private TextView tvSettingEquipmentName;
    private Util util;
    private boolean isSwitchs = false;
    private int sceneEquipmentCount = 0;
    private int addSceneActivityListViewItem = -1;
    private int isEmptySetting = -1;

    private List<EquipmentBean> conditionSpinnerAdapterList;
    private int conditionSpinnerAdapterListSize;

    private int spinnerConditionEquipmentOneSelected = 0;
    private int spinnerConditionEquipmentOneSelectedTwo = 0;
    private int spinnerConditionEquipmentOneSelectedThree = 0;

    //设置错误检查
    //如果出现错误，就设置为false
    //如果没有错，就设置true
    private boolean isNoProblemOne = false;
    private boolean isNoProblemTwo = false;
    private boolean isNoProblemThree = false;


    //    开关一
    private Spinner spinnerSwitchOne;
    private LinearLayout llTimeSwitchOne;
    private LinearLayout llImmediatelySwitchOne;
    private LinearLayout llConditionSwitchOne;
    //        立即触发
    private ToggleButton tbOne;
    //        时间触发
    private Button btnTimePickerOpenOne;
    private TextView tvTimePickerOpenOne;
    //        条件触发
    private Spinner spinnerConditionEquipmentOne;
    private Spinner spinnerSymbolOne;
    private Spinner spinnerTemp25One;
    private EditText etConditionValueOne;

    private String equipmentMacOne = ""; //8字节
    private String equipmentNumberOne = ""; //1字节
    private String triggerOne = ""; //1字节
    private String timeTrigOne = ""; //4字节 00备用   eg：00 00 12 34
    private String timeSwitchCmdOne = "开";
    private String immediatelyTrigOne = ""; //1字节
    private String conditionEquipmentMacOne = ""; //8字节
    private String conditionEquipmentNumberOne = ""; //1字节
    private String conditionValueOne = ""; //1字节
    private String symbolOne = ""; //1字节
    private int isImmediatelyTrigOne = 0;
    private int isTimeTrigOne = 0;
    private int isTvConditionValueOne = 0;

    private boolean isConditionValueEmptyOne = false;
    private boolean isTimeTextViewEmptyOne = false;
    private int isConditionTrigOne = 0;

    private Handler spinnerHandler = new Handler();
    private String setUiConditionNumberOne = "";
    private String setUiSymbolOne = "";
    private String setUiConditionValueOne = "";

    //    开关二
    private Spinner spinnerSwitchTwo;
    private LinearLayout llTimeSwitchTwo;
    private LinearLayout llImmediatelySwitchTwo;
    private LinearLayout llConditionSwitchTwo;
    //        立即触发
    private ToggleButton tbTwo;
    //        时间触发
    private Button btnTimePickerOpenTwo;
    private TextView tvTimePickerOpenTwo;
    //        条件触发
    private Spinner spinnerConditionEquipmentTwo;
    private Spinner spinnerSymbolTwo;
    private Spinner spinnerTemp25Two;
    private EditText etConditionValueTwo;

    private String equipmentMacTwo = ""; //8字节
    private String equipmentNumberTwo = ""; //1字节
    private String triggerTwo = ""; //1字节
    private String timeTrigTwo = ""; //4字节 00备用   eg：00 00 12 34
    private String timeSwitchCmdTwo = "开";
    private String immediatelyTrigTwo = ""; //1字节
    private String conditionEquipmentMacTwo = ""; //8字节
    private String conditionEquipmentNumberTwo = ""; //1字节
    private String conditionValueTwo = ""; //1字节
    private String symbolTwo = ""; //1字节
    private int isImmediatelyTrigTwo = 0;
    private int isTimeTrigTwo = 0;
    private int isTvConditionValueTwo = 0;
    private boolean isConditionValueEmptyTwo = false;
    private boolean isTimeTextViewEmptyTwo = false;
    private int isConditionTrigTwo = 0;

    private Handler spinnerHandlerTwo = new Handler();
    private String setUiConditionNumberTwo = "";
    private String setUiSymbolTwo = "";
    private String setUiConditionValueTwo = "";

    //    开关三
    private Spinner spinnerSwitchThree;
    private LinearLayout llTimeSwitchThree;
    private LinearLayout llImmediatelySwitchThree;
    private LinearLayout llConditionSwitchThree;
    //        立即触发
    private ToggleButton tbThree;
    //        时间触发
    private Button btnTimePickerOpenThree;
    private TextView tvTimePickerOpenThree;
    //        条件触发
    private Spinner spinnerConditionEquipmentThree;
    private Spinner spinnerSymbolThree;
    private Spinner spinnerTemp25Three;
    private EditText etConditionValueThree;

    private String equipmentMacThree = ""; //8字节
    private String equipmentNumberThree = ""; //1字节
    private String triggerThree = ""; //1字节
    private String timeTrigThree = ""; //4字节 00备用   eg：00 00 12 34
    private String timeSwitchCmdThree = "开";
    private String immediatelyTrigThree = ""; //1字节
    private String conditionEquipmentMacThree = ""; //8字节
    private String conditionEquipmentNumberThree = ""; //1字节
    private String conditionValueThree = ""; //1字节
    private String symbolThree = ""; //1字节
    private int isImmediatelyTrigThree = 0;
    private int isTimeTrigThree = 0;
    private int isTvConditionValueThree = 0;
    private boolean isConditionValueEmptyThree = false;
    private boolean isTimeTextViewEmptyThree = false;
    private int isConditionTrigThree = 0;

    private Handler spinnerHandlerThree = new Handler();
    private String setUiConditionNumberThree = "";
    private String setUiSymbolThree = "";
    private String setUiConditionValueThree = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_equipment);
        triggerList = new ArrayList<>();
        Collections.addAll(triggerList, TRIGGERS);
        symbolList = new ArrayList<>();
        Collections.addAll(symbolList, SYMBOLS);
        temp25List = new ArrayList<>();
        Collections.addAll(temp25List, TEMP25);
        util = new Util();
        DBcurd = new DBcurd(SocketSettingActivity.this);
        conditionSpinnerAdapterList = DBcurd.getAllSceneConditionEquipments();
        conditionSpinnerAdapterListSize = conditionSpinnerAdapterList.size();
        conditionSpinnerAdapter = new ConditionSpinnerAdapter(SocketSettingActivity.this, conditionSpinnerAdapterList);

        llSwitchOne = (LinearLayout) findViewById(R.id.llSwitchOne);
        llSwitchTwo = (LinearLayout) findViewById(R.id.llSwitchTwo);
        llSwitchThree = (LinearLayout) findViewById(R.id.llSwitchThree);
        tvSettingEquipmentName = (TextView) findViewById(R.id.tvSettingEquipmentName);
        btnAddSceneBack = (Button) findViewById(R.id.btnAddSceneBack);
        btnAddSceneSure = (Button) findViewById(R.id.btnAddSceneSure);

//        开关一
        spinnerSwitchOne = (Spinner) findViewById(R.id.spinnerSwitchOne);
        llTimeSwitchOne = (LinearLayout) findViewById(R.id.llTimeSwitchOne);
        llImmediatelySwitchOne = (LinearLayout) findViewById(R.id.llImmediatelySwitchOne);
        llConditionSwitchOne = (LinearLayout) findViewById(R.id.llConditionSwitchOne);
//                立即触发
        tbOne = (ToggleButton) findViewById(R.id.tbOne);
//                时间触发
        btnTimePickerOpenOne = (Button) findViewById(R.id.btnTimePickerOpenOne);
        tvTimePickerOpenOne = (TextView) findViewById(R.id.tvTimePickerOpenOne);
//                条件触发
        spinnerConditionEquipmentOne = (Spinner) findViewById(R.id.spinnerConditionEquipmentOne);
        spinnerSymbolOne = (Spinner) findViewById(R.id.spinnerSymbolOne);
        spinnerTemp25One = (Spinner) findViewById(R.id.spinnerTemp25One);
        etConditionValueOne = (EditText) findViewById(R.id.etConditionValueOne);

//        开关二
        spinnerSwitchTwo = (Spinner) findViewById(R.id.spinnerSwitchTwo);
        llTimeSwitchTwo = (LinearLayout) findViewById(R.id.llTimeSwitchTwo);
        llImmediatelySwitchTwo = (LinearLayout) findViewById(R.id.llImmediatelySwitchTwo);
        llConditionSwitchTwo = (LinearLayout) findViewById(R.id.llConditionSwitchTwo);
//                立即触发
        tbTwo = (ToggleButton) findViewById(R.id.tbTwo);
//                时间触发
        btnTimePickerOpenTwo = (Button) findViewById(R.id.btnTimePickerOpenTwo);
        tvTimePickerOpenTwo = (TextView) findViewById(R.id.tvTimePickerOpenTwo);
//                条件触发
        spinnerConditionEquipmentTwo = (Spinner) findViewById(R.id.spinnerConditionEquipmentTwo);
        spinnerSymbolTwo = (Spinner) findViewById(R.id.spinnerSymbolTwo);
        spinnerTemp25Two = (Spinner) findViewById(R.id.spinnerTemp25Two);
        etConditionValueTwo = (EditText) findViewById(R.id.etConditionValueTwo);

//        开关三
        spinnerSwitchThree = (Spinner) findViewById(R.id.spinnerSwitchThree);
        llTimeSwitchThree = (LinearLayout) findViewById(R.id.llTimeSwitchThree);
        llImmediatelySwitchThree = (LinearLayout) findViewById(R.id.llImmediatelySwitchThree);
        llConditionSwitchThree = (LinearLayout) findViewById(R.id.llConditionSwitchThree);
//                立即触发
        tbThree = (ToggleButton) findViewById(R.id.tbThree);
//                时间触发
        btnTimePickerOpenThree = (Button) findViewById(R.id.btnTimePickerOpenThree);
        tvTimePickerOpenThree = (TextView) findViewById(R.id.tvTimePickerOpenThree);
//                条件触发
        spinnerConditionEquipmentThree = (Spinner) findViewById(R.id.spinnerConditionEquipmentThree);
        spinnerSymbolThree = (Spinner) findViewById(R.id.spinnerSymbolThree);
        spinnerTemp25Three = (Spinner) findViewById(R.id.spinnerTemp25Three);
        etConditionValueThree = (EditText) findViewById(R.id.etConditionValueThree);


        btnAddSceneBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddSceneSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAddSceneOnclicks();

//                Log.e(TAG,"tbOne="+tbOne.isChecked());

            }
        });


        //根据设备类型来显示界面
        Intent intent = this.getIntent();
        equipmentBeanActivity = (EquipmentBean) intent.getSerializableExtra(Constant.ADD_SCENE_ACTIVITY_2_SOCKET_SETTING_ACTIVITY);
        if (Constant.SWITCHES.equalsIgnoreCase(equipmentBeanActivity.getDevice_Type())) {
            //面板开关
            llSwitchOne.setVisibility(View.VISIBLE);
            llSwitchTwo.setVisibility(View.VISIBLE);
            llSwitchThree.setVisibility(View.VISIBLE);
            isSwitchs = true;

        } else {

            llSwitchOne.setVisibility(View.VISIBLE);
            llSwitchTwo.setVisibility(View.GONE);
            llSwitchThree.setVisibility(View.GONE);
        }

        addSceneActivityListViewItem = intent.getIntExtra(Constant.ADD_SCENE_ACTIVITY_2_SOCKET_SETTING_ACTIVITY_POSITION, -1);
        Log.e(TAG, "addSceneActivityListViewItem=" + addSceneActivityListViewItem);

        //设置设备名
        if (!DBcurd.getNickNameByMac(equipmentBeanActivity.getMac_ADDR()).equalsIgnoreCase(Constant.EQUIPMENT_NAME_ALL_FF)) {
            String tvNameText = new String(util.HexString2Bytes(DBcurd.getNickNameByMac(equipmentBeanActivity.getMac_ADDR()))).trim();
            if (TextUtils.isEmpty(tvNameText)) {
                tvSettingEquipmentName.setText(Constant.getTypeName(equipmentBeanActivity.getDevice_Type()));
            } else {
                tvSettingEquipmentName.setText(tvNameText);
            }
        } else {
            tvSettingEquipmentName.setText(Constant.getTypeName(equipmentBeanActivity.getDevice_Type()));
        }
        equipmentMacOne = equipmentBeanActivity.getMac_ADDR();
        equipmentMacTwo = equipmentBeanActivity.getMac_ADDR();
        equipmentMacThree = equipmentBeanActivity.getMac_ADDR();


        //开关一
        spinnerSwitchOne.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, triggerList));
        spinnerSwitchOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equipmentNumberOne = "0";
                if (position == 0) {
                    //无
                    triggerOne = "";
                    Log.e(TAG, "position=无");
                    llTimeSwitchOne.setVisibility(View.GONE);
                    llImmediatelySwitchOne.setVisibility(View.GONE);
                    llConditionSwitchOne.setVisibility(View.GONE);
                    //选择无触发方式，发送为零

                    timeTrigOne = "";
                    immediatelyTrigOne = "";
                    isImmediatelyTrigOne = 0;
                    isTimeTrigOne = 0;
                    timeSwitchCmdOne = "";
                    conditionEquipmentMacOne = "";
                    conditionEquipmentNumberOne = "";
                    conditionValueOne = "";
                    isTvConditionValueOne = 0;
                    symbolOne = "";
                    isConditionTrigOne = 0;

                } else if (position == 1) {
                    //立即触发
                    triggerOne = "Immediately";
                    llTimeSwitchOne.setVisibility(View.GONE);
                    llImmediatelySwitchOne.setVisibility(View.VISIBLE);
                    llConditionSwitchOne.setVisibility(View.GONE);

                    //选择立即触发，其他的触发方式为零
                    isTvConditionValueOne = 0;
                    timeTrigOne = "";
                    conditionEquipmentMacOne = "";
                    conditionEquipmentNumberOne = "";
                    conditionValueOne = "";
                    isImmediatelyTrigOne = 1;
                    isTimeTrigOne = 0;
                    timeSwitchCmdOne = "";
                    isConditionTrigOne = 0;
                    tbOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.e(TAG, "isChecked=" + isChecked);
                            immediatelyTrigOne = "" + isChecked;
                        }
                    });


                } else if (position == 2) {


                    //时间触发
                    triggerOne = "Time";
                    llTimeSwitchOne.setVisibility(View.VISIBLE);
                    llImmediatelySwitchOne.setVisibility(View.GONE);
                    llConditionSwitchOne.setVisibility(View.GONE);
                    //选择时间触发，其他的触发方式为零
                    isTvConditionValueOne = 0;
                    immediatelyTrigOne = "";
                    isImmediatelyTrigOne = 0;
                    isTimeTrigOne = 1;
                    timeSwitchCmdOne = "开";
                    conditionEquipmentMacOne = "";
                    conditionEquipmentNumberOne = "";
                    conditionValueOne = "";
                    isConditionTrigOne = 0;

                    btnTimePickerOpenOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(SocketSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvTimePickerOpenOne.setText(hourOfDay + ":" + minute);
//                                    timeTrigOne = hourOfDay + ":" + minute;
                                }
                            }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                            timePickerDialog.show();
                        }
                    });

                } else if (position == 3) {

                    if (conditionSpinnerAdapterListSize == 0) {
//                        Toast.makeText(getApplicationContext(), "没有可用设备", Toast.LENGTH_SHORT).show();
                        Util.showToast(getApplicationContext(), "没有可用设备");
                        etConditionValueOne.setEnabled(false);
                    } else {
                        etConditionValueOne.setEnabled(true);
                    }
//                    else {

                    //条件触发
                    triggerOne = "Condition";
                    llTimeSwitchOne.setVisibility(View.GONE);
                    llImmediatelySwitchOne.setVisibility(View.GONE);
                    llConditionSwitchOne.setVisibility(View.VISIBLE);

                    //选择条件触发，其他的触发方式为零

                    timeTrigOne = "";
                    immediatelyTrigOne = "";
                    isImmediatelyTrigOne = 0;
                    isTimeTrigOne = 0;
                    timeSwitchCmdOne = "";
                    isConditionTrigOne = 1;
                    spinnerConditionEquipmentOne.setAdapter(conditionSpinnerAdapter);
                    spinnerConditionEquipmentOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            EquipmentBean equipmentBeanSpinnerOne = (EquipmentBean) parent.getItemAtPosition(position);
                            List<String> list = new ArrayList<>();
                            conditionEquipmentMacOne = equipmentBeanSpinnerOne.getMac_ADDR();
                            if (Constant.DOOR_MAGNET.equalsIgnoreCase(equipmentBeanSpinnerOne.getDevice_Type())) {
                                //如果是门磁的话，spinner显示 开或者关
                                Collections.addAll(list, SYMBOLS2);
                                etConditionValueOne.setVisibility(View.GONE);
                                isTvConditionValueOne = 0;
                                spinnerTemp25One.setVisibility(View.GONE);
                                conditionEquipmentNumberOne = "0";
                            } else if (Constant.INFRARED.equalsIgnoreCase(equipmentBeanSpinnerOne.getDevice_Type())) {
                                //如果是红外的话，spinner显示 有人或者没人
                                Collections.addAll(list, SYMBOLS3);
                                etConditionValueOne.setVisibility(View.GONE);
                                isTvConditionValueOne = 0;
                                spinnerTemp25One.setVisibility(View.GONE);
                                conditionEquipmentNumberOne = "0";
                            } else if (Constant.TEMP_PM25.equalsIgnoreCase(equipmentBeanSpinnerOne.getDevice_Type())) {
                                //如果是温湿度PM2.5的话，spinner显示 温度、湿度、PM2.5
                                Collections.addAll(list, SYMBOLS);
                                etConditionValueOne.setVisibility(View.VISIBLE);
                                isTvConditionValueOne = 1;
                                spinnerTemp25One.setVisibility(View.VISIBLE);
                                spinnerTemp25One.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, temp25List));
                                spinnerTemp25One.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if ("温度".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "温度");
                                            conditionEquipmentNumberOne = "温度";
                                        }
                                        if ("湿度".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "湿度");
                                            conditionEquipmentNumberOne = "湿度";
                                        }
                                        if ("PM2.5".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "PM2.5");
                                            conditionEquipmentNumberOne = "PM2.5";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } else {
                                Collections.addAll(list, SYMBOLS);
                                etConditionValueOne.setVisibility(View.VISIBLE);
                                isTvConditionValueOne = 1;
                                spinnerTemp25One.setVisibility(View.GONE);
                                conditionEquipmentNumberOne = "0";
                            }


                            spinnerSymbolOne.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, list));
                            spinnerSymbolOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Log.e(TAG, "spinnerSymbolOne选择了" + parent.getItemAtPosition(position).toString());
                                    symbolOne = parent.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            Log.e(TAG, "spinnerConditionEquipmentOne选择了" + equipmentBeanSpinnerOne.getDevice_Type());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

//                    }

                }//sdsd


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //开关二
        spinnerSwitchTwo.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, triggerList));
        spinnerSwitchTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equipmentNumberTwo = "1";
                if (position == 0) {
                    //无
                    triggerTwo = "";
                    Log.e(TAG, "position=无");
                    llTimeSwitchTwo.setVisibility(View.GONE);
                    llImmediatelySwitchTwo.setVisibility(View.GONE);
                    llConditionSwitchTwo.setVisibility(View.GONE);


                    timeTrigTwo = "";
                    immediatelyTrigTwo = "";
                    isImmediatelyTrigTwo = 0;
                    isTimeTrigTwo = 0;
                    timeSwitchCmdTwo = "";
                    conditionEquipmentMacTwo = "";
                    conditionEquipmentNumberTwo = "";
                    conditionValueTwo = "";
                    isTvConditionValueTwo = 0;
                    symbolTwo = "";
                    isConditionTrigTwo = 0;
                } else if (position == 1) {
                    //立即触发
                    triggerTwo = "Immediately";
                    llTimeSwitchTwo.setVisibility(View.GONE);
                    llImmediatelySwitchTwo.setVisibility(View.VISIBLE);
                    llConditionSwitchTwo.setVisibility(View.GONE);

                    isTvConditionValueTwo = 0;
                    timeTrigTwo = "";
                    conditionEquipmentMacTwo = "";
                    conditionEquipmentNumberTwo = "";
                    conditionValueTwo = "";
                    isImmediatelyTrigTwo = 1;
                    isTimeTrigTwo = 0;
                    timeSwitchCmdTwo = "";
                    isConditionTrigTwo = 0;
                    tbTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.e(TAG, "tbTwo isChecked=" + isChecked);
                            immediatelyTrigTwo = "" + isChecked;
                        }
                    });

                } else if (position == 2) {
                    //时间触发
                    triggerTwo = "Time";
                    llTimeSwitchTwo.setVisibility(View.VISIBLE);
                    llImmediatelySwitchTwo.setVisibility(View.GONE);
                    llConditionSwitchTwo.setVisibility(View.GONE);

                    isTvConditionValueTwo = 0;
                    immediatelyTrigTwo = "";
                    isImmediatelyTrigTwo = 0;
                    isTimeTrigTwo = 1;
                    timeSwitchCmdTwo = "开";
                    conditionEquipmentMacTwo = "";
                    conditionEquipmentNumberTwo = "";
                    conditionValueTwo = "";
                    isConditionTrigTwo = 0;
                    btnTimePickerOpenTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(SocketSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvTimePickerOpenTwo.setText(hourOfDay + ":" + minute);
//                                    timeTrigTwo = hourOfDay + ":" + minute;
                                }
                            }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                            timePickerDialog.show();
                        }
                    });

                } else if (position == 3) {

                    if (conditionSpinnerAdapterListSize == 0) {
//                        Toast.makeText(getApplicationContext(), "没有可用设备", Toast.LENGTH_SHORT).show();
                        Util.showToast(getApplicationContext(), "没有可用设备");
                        etConditionValueTwo.setEnabled(false);
                    } else {
                        etConditionValueTwo.setEnabled(true);
                    }

                    //条件触发
                    triggerTwo = "Condition";
                    llTimeSwitchTwo.setVisibility(View.GONE);
                    llImmediatelySwitchTwo.setVisibility(View.GONE);
                    llConditionSwitchTwo.setVisibility(View.VISIBLE);

                    timeTrigTwo = "";
                    immediatelyTrigTwo = "";
                    isImmediatelyTrigTwo = 0;
                    isTimeTrigTwo = 0;
                    timeSwitchCmdTwo = "";
                    isConditionTrigTwo = 1;
                    spinnerConditionEquipmentTwo.setAdapter(conditionSpinnerAdapter);
                    spinnerConditionEquipmentTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            EquipmentBean equipmentBeanSpinnerTwo = (EquipmentBean) parent.getItemAtPosition(position);
                            List<String> list = new ArrayList<>();
                            conditionEquipmentMacTwo = equipmentBeanSpinnerTwo.getMac_ADDR();
                            if (Constant.DOOR_MAGNET.equalsIgnoreCase(equipmentBeanSpinnerTwo.getDevice_Type())) {
                                //如果是门磁的话，spinner显示 开或者关
                                Collections.addAll(list, SYMBOLS2);
                                etConditionValueTwo.setVisibility(View.GONE);
                                isTvConditionValueTwo = 0;
                                spinnerTemp25Two.setVisibility(View.GONE);
                                conditionEquipmentNumberTwo = "0";
                            } else if (Constant.INFRARED.equalsIgnoreCase(equipmentBeanSpinnerTwo.getDevice_Type())) {
                                //如果是红外的话，spinner显示 有人或者没人
                                Collections.addAll(list, SYMBOLS3);
                                spinnerTemp25Two.setVisibility(View.GONE);
                                isTvConditionValueTwo = 0;
                                etConditionValueTwo.setVisibility(View.GONE);
                                conditionEquipmentNumberTwo = "0";
                            } else if (Constant.TEMP_PM25.equalsIgnoreCase(equipmentBeanSpinnerTwo.getDevice_Type())) {
                                Collections.addAll(list, SYMBOLS);
                                etConditionValueTwo.setVisibility(View.VISIBLE);
                                isTvConditionValueTwo = 1;
                                spinnerTemp25Two.setVisibility(View.VISIBLE);
                                spinnerTemp25Two.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, temp25List));
                                spinnerTemp25Two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if ("温度".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "温度");
                                            conditionEquipmentNumberTwo = "温度";
                                        }
                                        if ("湿度".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "湿度");
                                            conditionEquipmentNumberTwo = "湿度";
                                        }
                                        if ("PM2.5".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "PM2.5");
                                            conditionEquipmentNumberTwo = "PM2.5";
                                        }


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } else {
                                Collections.addAll(list, SYMBOLS);
                                spinnerTemp25Two.setVisibility(View.GONE);
                                isTvConditionValueTwo = 1;
                                etConditionValueTwo.setVisibility(View.VISIBLE);
                                conditionEquipmentNumberTwo = "0";
                            }

                            spinnerSymbolTwo.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, list));
                            spinnerSymbolTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Log.e(TAG, "spinnerSymboltwo选择了" + parent.getItemAtPosition(position).toString());
                                    symbolTwo = parent.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            Log.e(TAG, "spinnerConditionEquipmentTwo选择了" + equipmentBeanSpinnerTwo.getDevice_Type());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //开关三
        spinnerSwitchThree.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, triggerList));
        spinnerSwitchThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equipmentNumberThree = "2";
                if (position == 0) {
                    //无
                    triggerThree = "";
                    Log.e(TAG, "position=无");
                    llTimeSwitchThree.setVisibility(View.GONE);
                    llImmediatelySwitchThree.setVisibility(View.GONE);
                    llConditionSwitchThree.setVisibility(View.GONE);

                    timeTrigThree = "";
                    immediatelyTrigThree = "";
                    isImmediatelyTrigThree = 0;
                    isTimeTrigThree = 0;
                    timeSwitchCmdThree = "";
                    conditionEquipmentMacThree = "";
                    conditionEquipmentNumberThree = "";
                    conditionValueThree = "";
                    isTvConditionValueThree = 0;
                    symbolThree = "";
                    isConditionTrigThree = 0;
                } else if (position == 1) {
                    //立即触发
                    triggerThree = "Immediately";
                    llTimeSwitchThree.setVisibility(View.GONE);
                    llImmediatelySwitchThree.setVisibility(View.VISIBLE);
                    llConditionSwitchThree.setVisibility(View.GONE);

                    isTvConditionValueThree = 0;
                    timeTrigThree = "";
                    conditionEquipmentMacThree = "";
                    conditionEquipmentNumberThree = "";
                    conditionValueThree = "";
                    isImmediatelyTrigThree = 1;
                    isTimeTrigThree = 0;
                    timeSwitchCmdThree = "";
                    isConditionTrigThree = 0;
                    tbThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.e(TAG, "tbThree isChecked=" + isChecked);
                            immediatelyTrigThree = "" + isChecked;
                        }
                    });

                } else if (position == 2) {
                    //时间触发
                    triggerThree = "Time";
                    llTimeSwitchThree.setVisibility(View.VISIBLE);
                    llImmediatelySwitchThree.setVisibility(View.GONE);
                    llConditionSwitchThree.setVisibility(View.GONE);

                    isTvConditionValueThree = 0;
                    immediatelyTrigThree = "";
                    isImmediatelyTrigThree = 0;
                    isTimeTrigThree = 1;
                    timeSwitchCmdThree = "开";
                    conditionEquipmentMacThree = "";
                    conditionEquipmentNumberThree = "";
                    conditionValueThree = "";
                    isConditionTrigThree = 0;
                    btnTimePickerOpenThree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(SocketSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvTimePickerOpenThree.setText(hourOfDay + ":" + minute);
//                                    timeTrigThree = hourOfDay + ":" + minute;
                                }
                            }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                            timePickerDialog.show();
                        }
                    });

                } else if (position == 3) {

                    if (conditionSpinnerAdapterListSize == 0) {
//                        Toast.makeText(getApplicationContext(), "没有可用设备", Toast.LENGTH_SHORT).show();
                        Util.showToast(getApplicationContext(), "没有可用设备");
                        etConditionValueThree.setEnabled(false);
                    } else {
                        etConditionValueThree.setEnabled(true);
                    }

                    //条件触发
                    triggerThree = "Condition";
                    llTimeSwitchThree.setVisibility(View.GONE);
                    llImmediatelySwitchThree.setVisibility(View.GONE);
                    llConditionSwitchThree.setVisibility(View.VISIBLE);

                    timeTrigThree = "";
                    immediatelyTrigThree = "";
                    isImmediatelyTrigThree = 0;
                    isTimeTrigThree = 0;
                    timeSwitchCmdThree = "";
                    isConditionTrigThree = 1;
                    spinnerConditionEquipmentThree.setAdapter(conditionSpinnerAdapter);
                    spinnerConditionEquipmentThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            EquipmentBean equipmentBeanSpinnerThree = (EquipmentBean) parent.getItemAtPosition(position);
                            List<String> list = new ArrayList<>();
                            conditionEquipmentMacThree = equipmentBeanSpinnerThree.getMac_ADDR();
                            if (Constant.DOOR_MAGNET.equalsIgnoreCase(equipmentBeanSpinnerThree.getDevice_Type())) {
                                //如果是门磁的话，spinner显示 开或者关
                                Collections.addAll(list, SYMBOLS2);
                                etConditionValueThree.setVisibility(View.GONE);
                                spinnerTemp25Three.setVisibility(View.GONE);
                                isTvConditionValueThree = 0;
                                conditionEquipmentNumberThree = "0";
                            } else if (Constant.INFRARED.equalsIgnoreCase(equipmentBeanSpinnerThree.getDevice_Type())) {
                                //如果是红外的话，spinner显示 有人或者没人
                                Collections.addAll(list, SYMBOLS3);
                                etConditionValueThree.setVisibility(View.GONE);
                                spinnerTemp25Three.setVisibility(View.GONE);
                                isTvConditionValueThree = 0;
                                conditionEquipmentNumberThree = "0";
                            } else if (Constant.TEMP_PM25.equalsIgnoreCase(equipmentBeanSpinnerThree.getDevice_Type())) {
                                Collections.addAll(list, SYMBOLS);
                                etConditionValueThree.setVisibility(View.VISIBLE);
                                isTvConditionValueThree = 1;
                                spinnerTemp25Three.setVisibility(View.VISIBLE);
                                spinnerTemp25Three.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, temp25List));
                                spinnerTemp25Three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if ("温度".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "温度");
                                            conditionEquipmentNumberThree = "温度";
                                        }
                                        if ("湿度".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "湿度");
                                            conditionEquipmentNumberThree = "湿度";
                                        }
                                        if ("PM2.5".equalsIgnoreCase(parent.getItemAtPosition(position).toString())) {
                                            Log.e(TAG, "PM2.5");
                                            conditionEquipmentNumberThree = "PM2.5";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } else {
                                Collections.addAll(list, SYMBOLS);
                                spinnerTemp25Three.setVisibility(View.GONE);
                                isTvConditionValueThree = 1;
                                etConditionValueThree.setVisibility(View.VISIBLE);
                                conditionEquipmentNumberThree = "0";
                            }

                            spinnerSymbolThree.setAdapter(new ArrayAdapter<>(SocketSettingActivity.this, android.R.layout.simple_list_item_1, list));
                            spinnerSymbolThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Log.e(TAG, "spinnerSymbolThree选择了" + parent.getItemAtPosition(position).toString());
                                    symbolThree = parent.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            Log.e(TAG, "spinnerConditionEquipmentThree选择了" + equipmentBeanSpinnerThree.getDevice_Type());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         如果是第一次进来的，
         如果是不是第一次进来的
         */

        List<SceneEquipmentBean> setList = DBcurd.getSceneEquipmentSettingByEquipmentMac(equipmentBeanActivity.getMac_ADDR());
        if (setList.size() != 0) {
            for (int i = 0; i < setList.size(); i++) {
                Log.e(TAG, "setList.size()=" + setList.size());
                SceneEquipmentBean sceneEquipmentBeanSetting = setList.get(i);
                Log.e(TAG, "sceneEquipmentBeanSetting.getEquipmentMac()=" + sceneEquipmentBeanSetting.getEquipmentMac());
                if (Integer.parseInt(sceneEquipmentBeanSetting.getEquipmentNumber()) == 0) {
                    //开关一
                    //处理开关一开始
                    if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_TIME)) {
                        spinnerSwitchOne.setSelection(2);
                        String timeTrig = sceneEquipmentBeanSetting.getTimeTrig();
                        tvTimePickerOpenOne.setText(timeTrig);

                    } else if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_IMMEDIATELY)) {
                        spinnerSwitchOne.setSelection(1);
                        String immediatelyTrig = sceneEquipmentBeanSetting.getImmediatelyTrig();
                        if (immediatelyTrig.equalsIgnoreCase("true")) {
                            tbOne.setChecked(true);
                        } else if (immediatelyTrig.equalsIgnoreCase("false")) {
                            tbOne.setChecked(false);
                        }


                    } else if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_CONDITION)) {
                        spinnerSwitchOne.setSelection(3);
                        String equipmentMac = sceneEquipmentBeanSetting.getConditionEquipmentMac();
                        for (int j = 0; j < conditionSpinnerAdapterListSize; j++) {
                            if (equipmentMac.equalsIgnoreCase(conditionSpinnerAdapterList.get(j).getMac_ADDR())) {
                                spinnerConditionEquipmentOneSelected = j;
                                spinnerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinnerConditionEquipmentOne.setSelection(spinnerConditionEquipmentOneSelected);
                                        Log.e(TAG, " spinnerConditionEquipmentOne.setSelection(spinnerConditionEquipmentOneSelected);");
                                    }
                                }, 500);


                                setUiConditionNumberOne = sceneEquipmentBeanSetting.getCoditionNumber();

                                setUiSymbolOne = sceneEquipmentBeanSetting.getTrigSymbol();

                                setUiConditionValueOne = sceneEquipmentBeanSetting.getConditionValue();

                                Log.e(TAG, "j=" + j + " conditionNumber=" + setUiConditionNumberOne + " symbol=" + setUiSymbolOne + " conditionValue=" + setUiConditionValueOne);

                                spinnerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!TextUtils.isEmpty(setUiConditionNumberOne)) {
                                            if (setUiConditionNumberOne.equalsIgnoreCase("温度")) {
                                                spinnerTemp25One.setSelection(0);
                                                if (setUiSymbolOne.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolOne.setSelection(0);
                                                } else if (setUiSymbolOne.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolOne.setSelection(1);
                                                } else if (setUiSymbolOne.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolOne.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueOne)) {
                                                    etConditionValueOne.setText(setUiConditionValueOne);
                                                }
                                                Log.e(TAG, "conditionNumber.equalsIgnoreCase(温度)");
                                            } else if (setUiConditionNumberOne.equalsIgnoreCase("湿度")) {
                                                spinnerTemp25One.setSelection(1);
                                                if (setUiSymbolOne.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolOne.setSelection(0);
                                                } else if (setUiSymbolOne.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolOne.setSelection(1);
                                                } else if (setUiSymbolOne.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolOne.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueOne)) {
                                                    etConditionValueOne.setText(setUiConditionValueOne);
                                                }
                                                Log.e(TAG, "conditionNumber.equalsIgnoreCase(湿度)");
                                            } else if (setUiConditionNumberOne.equalsIgnoreCase("PM2.5")) {
                                                spinnerTemp25One.setSelection(2);
                                                if (setUiSymbolOne.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolOne.setSelection(0);
                                                } else if (setUiSymbolOne.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolOne.setSelection(1);
                                                } else if (setUiSymbolOne.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolOne.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueOne)) {
                                                    etConditionValueOne.setText(setUiConditionValueOne);
                                                }
                                                Log.e(TAG, "conditionNumber.equalsIgnoreCase(PM2.5)");

                                            } else if (setUiConditionNumberOne.equalsIgnoreCase("0")) {

                                                if (TextUtils.isEmpty(setUiConditionValueOne)) {
                                                    if (setUiSymbolOne.equalsIgnoreCase("有人")) {
                                                        spinnerSymbolOne.setSelection(0);
                                                    } else if (setUiSymbolOne.equalsIgnoreCase("没人")) {
                                                        spinnerSymbolOne.setSelection(1);
                                                    }
                                                } else {
                                                    if (setUiSymbolOne.equalsIgnoreCase("大于")) {
                                                        spinnerSymbolOne.setSelection(0);

                                                    } else if (setUiSymbolOne.equalsIgnoreCase("等于")) {
                                                        spinnerSymbolOne.setSelection(1);
                                                    } else if (setUiSymbolOne.equalsIgnoreCase("小于")) {
                                                        spinnerSymbolOne.setSelection(2);
                                                    }
                                                    if (!TextUtils.isEmpty(setUiConditionValueOne)) {
                                                        etConditionValueOne.setText(setUiConditionValueOne);
                                                    }

                                                }

                                            }

                                        }// if(!TextUtils.isEmpty(setUiConditionNumberOne))
                                    }
                                }, 600);

                            }//if(equipmentMac.equalsIgnoreCase(conditionSpinnerAdapterList.get(j).getMac_ADDR()))
                        } //


                    }
                    //处理开关一结束

                } else if (Integer.parseInt(sceneEquipmentBeanSetting.getEquipmentNumber()) == 1) {
                    //开关二
                    //*****
                    //处理开关二开始
                    if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_TIME)) {
                        spinnerSwitchTwo.setSelection(2);
                        String timeTrig = sceneEquipmentBeanSetting.getTimeTrig();
                        tvTimePickerOpenTwo.setText(timeTrig);

                    } else if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_IMMEDIATELY)) {
                        spinnerSwitchTwo.setSelection(1);
                        String immediatelyTrig = sceneEquipmentBeanSetting.getImmediatelyTrig();
                        if (immediatelyTrig.equalsIgnoreCase("true")) {
                            tbTwo.setChecked(true);
                        } else if (immediatelyTrig.equalsIgnoreCase("false")) {
                            tbTwo.setChecked(false);
                        }


                    } else if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_CONDITION)) {
                        spinnerSwitchTwo.setSelection(3);
                        //*************
                        String equipmentMac = sceneEquipmentBeanSetting.getConditionEquipmentMac();
                        for (int two = 0; two < conditionSpinnerAdapterListSize; two++) {
                            if (equipmentMac.equalsIgnoreCase(conditionSpinnerAdapterList.get(two).getMac_ADDR())) {
                                spinnerConditionEquipmentOneSelectedTwo = two;
                                spinnerHandlerTwo.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinnerConditionEquipmentTwo.setSelection(spinnerConditionEquipmentOneSelectedTwo);
                                    }
                                }, 500);


                                setUiConditionNumberTwo = sceneEquipmentBeanSetting.getCoditionNumber();

                                setUiSymbolTwo = sceneEquipmentBeanSetting.getTrigSymbol();

                                setUiConditionValueTwo = sceneEquipmentBeanSetting.getConditionValue();

                                spinnerHandlerTwo.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!TextUtils.isEmpty(setUiConditionNumberTwo)) {
                                            if (setUiConditionNumberTwo.equalsIgnoreCase("温度")) {
                                                spinnerTemp25Two.setSelection(0);

                                                if (setUiSymbolTwo.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolTwo.setSelection(0);
                                                } else if (setUiSymbolTwo.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolTwo.setSelection(1);
                                                } else if (setUiSymbolTwo.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolTwo.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueTwo)) {
                                                    etConditionValueTwo.setText(setUiConditionValueTwo);
                                                }
                                                Log.e(TAG, "conditionNumberTwo.equalsIgnoreCase(温度)");
                                            } else if (setUiConditionNumberTwo.equalsIgnoreCase("湿度")) {
                                                spinnerTemp25Two.setSelection(1);
                                                if (setUiSymbolTwo.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolTwo.setSelection(0);
                                                } else if (setUiSymbolTwo.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolTwo.setSelection(1);
                                                } else if (setUiSymbolTwo.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolTwo.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueTwo)) {
                                                    etConditionValueTwo.setText(setUiConditionValueTwo);
                                                }
                                                Log.e(TAG, "conditionNumberTwo.equalsIgnoreCase(湿度)");
                                            } else if (setUiConditionNumberTwo.equalsIgnoreCase("PM2.5")) {
                                                spinnerTemp25Two.setSelection(2);
                                                if (setUiSymbolTwo.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolTwo.setSelection(0);
                                                } else if (setUiSymbolTwo.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolTwo.setSelection(1);
                                                } else if (setUiSymbolTwo.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolTwo.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueTwo)) {
                                                    etConditionValueTwo.setText(setUiConditionValueTwo);
                                                }
                                                Log.e(TAG, "conditionNumberTwo.equalsIgnoreCase(PM2.5)");

                                            } else if (setUiConditionNumberTwo.equalsIgnoreCase("0")) {

                                                if (TextUtils.isEmpty(setUiConditionValueTwo)) {
                                                    if (setUiSymbolTwo.equalsIgnoreCase("有人")) {
                                                        spinnerSymbolTwo.setSelection(0);
                                                    } else if (setUiSymbolTwo.equalsIgnoreCase("没人")) {
                                                        spinnerSymbolTwo.setSelection(1);
                                                    }
                                                } else {
                                                    if (setUiSymbolTwo.equalsIgnoreCase("大于")) {
                                                        spinnerSymbolTwo.setSelection(0);
                                                    } else if (setUiSymbolTwo.equalsIgnoreCase("等于")) {
                                                        spinnerSymbolTwo.setSelection(1);
                                                    } else if (setUiSymbolTwo.equalsIgnoreCase("小于")) {
                                                        spinnerSymbolTwo.setSelection(2);
                                                    }
                                                    if (!TextUtils.isEmpty(setUiConditionValueTwo)) {
                                                        etConditionValueTwo.setText(setUiConditionValueTwo);
                                                    }

                                                }

                                            }

                                        }// if(!TextUtils.isEmpty(setUiConditionNumberOne))
                                    }
                                }, 600);

                            }//if(equipmentMac.equalsIgnoreCase(conditionSpinnerAdapterList.get(j).getMac_ADDR()))
                        } //
                        //*************



                    }
                    //处理开关二结束
                    //*****


                } else if (Integer.parseInt(sceneEquipmentBeanSetting.getEquipmentNumber()) == 2) {
                    //开关三

                    //****
                    //处理开关三开始
                    if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_TIME)) {
                        spinnerSwitchThree.setSelection(2);
                        String timeTrig = sceneEquipmentBeanSetting.getTimeTrig();
                        tvTimePickerOpenThree.setText(timeTrig);

                    } else if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_IMMEDIATELY)) {
                        spinnerSwitchThree.setSelection(1);
                        String immediatelyTrig = sceneEquipmentBeanSetting.getImmediatelyTrig();
                        if (immediatelyTrig.equalsIgnoreCase("true")) {
                            tbThree.setChecked(true);
                        } else if (immediatelyTrig.equalsIgnoreCase("false")) {
                            tbThree.setChecked(false);
                        }


                    } else if (sceneEquipmentBeanSetting.getTrigger().equalsIgnoreCase(TRIGGER_OF_CONDITION)) {
                        spinnerSwitchThree.setSelection(3);

                        //*************
                        String equipmentMac = sceneEquipmentBeanSetting.getConditionEquipmentMac();
                        for (int three = 0; three < conditionSpinnerAdapterListSize; three++) {
                            if (equipmentMac.equalsIgnoreCase(conditionSpinnerAdapterList.get(three).getMac_ADDR())) {
                                spinnerConditionEquipmentOneSelectedThree = three;
                                spinnerHandlerThree.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinnerConditionEquipmentThree.setSelection(spinnerConditionEquipmentOneSelectedThree);
                                    }
                                }, 500);


                                setUiConditionNumberThree = sceneEquipmentBeanSetting.getCoditionNumber();

                                setUiSymbolThree = sceneEquipmentBeanSetting.getTrigSymbol();

                                setUiConditionValueThree = sceneEquipmentBeanSetting.getConditionValue();

                                spinnerHandlerThree.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!TextUtils.isEmpty(setUiConditionNumberThree)) {
                                            if (setUiConditionNumberThree.equalsIgnoreCase("温度")) {
                                                spinnerTemp25Three.setSelection(0);

                                                if (setUiSymbolThree.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolThree.setSelection(0);
                                                } else if (setUiSymbolThree.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolThree.setSelection(1);
                                                } else if (setUiSymbolThree.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolThree.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueThree)) {
                                                    etConditionValueThree.setText(setUiConditionValueThree);
                                                }
                                                Log.e(TAG, "conditionNumberThree.equalsIgnoreCase(温度)");
                                            } else if (setUiConditionNumberThree.equalsIgnoreCase("湿度")) {
                                                spinnerTemp25Three.setSelection(1);
                                                if (setUiSymbolThree.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolThree.setSelection(0);
                                                } else if (setUiSymbolThree.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolThree.setSelection(1);
                                                } else if (setUiSymbolThree.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolThree.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueThree)) {
                                                    etConditionValueThree.setText(setUiConditionValueThree);
                                                }
                                                Log.e(TAG, "conditionNumberThree.equalsIgnoreCase(湿度)");
                                            } else if (setUiConditionNumberThree.equalsIgnoreCase("PM2.5")) {
                                                spinnerTemp25Three.setSelection(2);
                                                if (setUiSymbolThree.equalsIgnoreCase("大于")) {
                                                    spinnerSymbolThree.setSelection(0);
                                                } else if (setUiSymbolThree.equalsIgnoreCase("等于")) {
                                                    spinnerSymbolThree.setSelection(1);
                                                } else if (setUiSymbolThree.equalsIgnoreCase("小于")) {
                                                    spinnerSymbolThree.setSelection(2);
                                                }
                                                if (!TextUtils.isEmpty(setUiConditionValueThree)) {
                                                    etConditionValueThree.setText(setUiConditionValueThree);
                                                }
                                                Log.e(TAG, "conditionNumberThree.equalsIgnoreCase(PM2.5)");

                                            } else if (setUiConditionNumberThree.equalsIgnoreCase("0")) {

                                                if (TextUtils.isEmpty(setUiConditionValueThree)) {
                                                    if (setUiSymbolThree.equalsIgnoreCase("有人")) {
                                                        spinnerSymbolThree.setSelection(0);
                                                    } else if (setUiSymbolThree.equalsIgnoreCase("没人")) {
                                                        spinnerSymbolThree.setSelection(1);
                                                    }
                                                } else {
                                                    if (setUiSymbolThree.equalsIgnoreCase("大于")) {
                                                        spinnerSymbolThree.setSelection(0);
                                                    } else if (setUiSymbolThree.equalsIgnoreCase("等于")) {
                                                        spinnerSymbolThree.setSelection(1);
                                                    } else if (setUiSymbolThree.equalsIgnoreCase("小于")) {
                                                        spinnerSymbolThree.setSelection(2);
                                                    }
                                                    if (!TextUtils.isEmpty(setUiConditionValueThree)) {
                                                        etConditionValueThree.setText(setUiConditionValueThree);
                                                    }

                                                }

                                            }

                                        }// if(!TextUtils.isEmpty(setUiConditionNumberOne))
                                    }
                                }, 600);

                            }//if(equipmentMac.equalsIgnoreCase(conditionSpinnerAdapterList.get(j).getMac_ADDR()))
                        } //
                        //*************


                    }
                    //处理开关一结束
                    //****

                }


            }

        }


    }


    private void btnAddSceneOnclicks() {
        Intent intent = new Intent();
        sceneEquipmentCount = 0;
        if (isSwitchs) {
            isNoProblemThree = false;
            isNoProblemTwo = false;
            isNoProblemOne = false;

            doWithConditionStringOne();
            doWithConditionStringTwo();
            doWithConditionStringThree();
            Bundle mBundle = new Bundle();

            if (!TextUtils.isEmpty(sceneEquipmentBean0.getTrigger())) {
//                mBundle.putSerializable("sceneEquipmentBean0", sceneEquipmentBean0);
//                sceneEquipmentCount++;
                //t

                if (isConditionValueEmptyOne && isTvConditionValueOne == 1 || (isTvConditionValueOne == 1 && isConditionTrigOne == 1 && TextUtils.isEmpty(etConditionValueOne.getText().toString()))) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关一的条件值为空！");
//                    t.setGravity(Gravity.CENTER, 0, 0);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关一的条件值为空！",Gravity.CENTER, 0, 0);
                    isNoProblemOne = false;

                } else if (isTimeTrigOne == 1 && isTimeTextViewEmptyOne) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关一没有设置时间");
//                    t.setGravity(Gravity.CENTER, 0, 0);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关一没有设置时间", Gravity.CENTER, 0, 0);
                    isNoProblemOne = false;
                } else {
                    mBundle.putSerializable("sceneEquipmentBean0", sceneEquipmentBean0);
                    sceneEquipmentCount++;
                    isNoProblemOne = true;
                }

                //t
            } else {
                isNoProblemOne = true;
            }

            if (!TextUtils.isEmpty(sceneEquipmentBean1.getTrigger())) {
//                mBundle.putSerializable("sceneEquipmentBean1", sceneEquipmentBean1);
//                sceneEquipmentCount++;
                if (isConditionValueEmptyTwo && isTvConditionValueTwo == 1 || (isTvConditionValueTwo == 1 && isConditionTrigTwo == 1 && TextUtils.isEmpty(etConditionValueTwo.getText().toString()))) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关二的条件值为空！");
//                    t.setGravity(Gravity.CENTER, 0, 100);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关二的条件值为空！",Gravity.CENTER, 0, 0);
                    isNoProblemTwo = false;

                } else if (isTimeTrigTwo == 1 && isTimeTextViewEmptyTwo) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关二没有设置时间");
//                    t.setGravity(Gravity.CENTER, 0, 100);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关二没有设置时间",Gravity.CENTER, 0, 0);
                    isNoProblemTwo = false;
                } else {

                    mBundle.putSerializable("sceneEquipmentBean1", sceneEquipmentBean1);
                    sceneEquipmentCount++;
                    isNoProblemTwo = true;

                }
            } else {
                isNoProblemTwo = true;
            }
            if (!TextUtils.isEmpty(sceneEquipmentBean2.getTrigger())) {
//                mBundle.putSerializable("sceneEquipmentBean2", sceneEquipmentBean2);
//                sceneEquipmentCount++;
                if (isConditionValueEmptyThree && isTvConditionValueThree == 1 || (isTvConditionValueThree == 1 && isConditionTrigThree == 1 && TextUtils.isEmpty(etConditionValueThree.getText().toString()))) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关三的条件值为空！");
//                    t.setGravity(Gravity.CENTER, 0, 200);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关三的条件值为空！",Gravity.CENTER, 0, 0);
                    isNoProblemThree = false;

                } else if (isTimeTrigThree == 1 && isTimeTextViewEmptyThree) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关三没有设置时间");
//                    t.setGravity(Gravity.CENTER, 0, 200);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关三没有设置时间",Gravity.CENTER, 0, 0);
                    isNoProblemThree = false;
                } else {
                    mBundle.putSerializable("sceneEquipmentBean2", sceneEquipmentBean2);
                    sceneEquipmentCount++;
                    isNoProblemThree = true;
                }

            } else {
                isNoProblemThree = true;
            }

            if (sceneEquipmentCount != 0) {
                if (isNoProblemOne && isNoProblemTwo && isNoProblemThree) {
                    intent.putExtras(mBundle);
                    intent.putExtra("equipmentMac", equipmentBeanActivity.getMac_ADDR());
                    intent.putExtra("sceneEquipmentCount", sceneEquipmentCount);
                    intent.putExtra("addSceneActivityListViewItem", addSceneActivityListViewItem);
                    intent.putExtra("resultIsSwitchs", isSwitchs);
                    SocketSettingActivity.this.setResult(Constant.EQUIPMENT_TYPE_RESULT_CODE, intent);
                    finish();
                }

            } else if ((TextUtils.isEmpty(sceneEquipmentBean0.getTrigger())) && (TextUtils.isEmpty(sceneEquipmentBean1.getTrigger())) && (TextUtils.isEmpty(sceneEquipmentBean2.getTrigger()))) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SocketSettingActivity.this);
                builder.setMessage("没有任何设置，确定要保存?");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent();
                        intent2.putExtra("isEmptySetting", isEmptySetting);
                        intent2.putExtra("clearSetting", equipmentBeanActivity.getMac_ADDR());
                        intent2.putExtra("addSceneActivityListViewItem", addSceneActivityListViewItem);
                        SocketSettingActivity.this.setResult(Constant.EQUIPMENT_TYPE_RESULT_CODE, intent2);
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setPositiveButton("取消", null);
                builder.create();
                builder.show();


            }


        } else {
            doWithConditionStringOne();
            if (!TextUtils.isEmpty(sceneEquipmentBean0.getTrigger())) {

                if (isConditionValueEmptyOne && isTvConditionValueOne == 1 || (isTvConditionValueOne == 1 && isConditionTrigOne == 1 && TextUtils.isEmpty(etConditionValueOne.getText().toString()))) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关一的条件值为空！");
//                    t.setGravity(Gravity.CENTER, 0, 0);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关一的条件值为空！", Gravity.CENTER, 0, 0);

                } else if (isTimeTrigOne == 1 && isTimeTextViewEmptyOne) {
//                    Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                    t.setText("开关一没有设置时间");
//                    t.setGravity(Gravity.CENTER, 0, 0);
//                    t.show();
                    Util.showToast(getApplicationContext(), "开关一没有设置时间", Gravity.CENTER, 0, 0);
                } else {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("sceneEquipmentBean0", sceneEquipmentBean0);
                    intent.putExtras(mBundle);
                    sceneEquipmentCount++;
                    intent.putExtra("equipmentMac", equipmentBeanActivity.getMac_ADDR());
                    intent.putExtra("sceneEquipmentCount", sceneEquipmentCount);
                    intent.putExtra("addSceneActivityListViewItem", addSceneActivityListViewItem);
                    intent.putExtra("resultIsSwitchs", isSwitchs);
                    SocketSettingActivity.this.setResult(Constant.EQUIPMENT_TYPE_RESULT_CODE, intent);
                    finish();
                }

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SocketSettingActivity.this);
                builder.setMessage("没有任何设置，确定要保存?");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent();
                        intent2.putExtra("isEmptySetting", isEmptySetting);
                        intent2.putExtra("clearSetting", equipmentBeanActivity.getMac_ADDR());
                        intent2.putExtra("addSceneActivityListViewItem", addSceneActivityListViewItem);
                        SocketSettingActivity.this.setResult(Constant.EQUIPMENT_TYPE_RESULT_CODE, intent2);
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setPositiveButton("取消", null);
                builder.create();
                builder.show();

            }

        }
    }


    //包装和处理数据
    private void doWithConditionStringOne() {
        sceneEquipmentBean0 = new SceneEquipmentBean();
        //处理发送的数据
        equipmentMacOne = equipmentBeanActivity.getMac_ADDR();
//        String data;

        //如果isTvConditionValueOne为0，说明没有显示，不在发送范围
        //如果isTvConditionValueOne为1，说明显示，要获取数据
        if (isTvConditionValueOne == 0) {
            conditionValueOne = "";
        } else if (isTvConditionValueOne == 1) {
            Log.e(TAG, "isTvConditionValue == 1");
            conditionValueOne = etConditionValueOne.getText().toString();
            if (TextUtils.isEmpty(conditionValueOne)) {
                //数据不能为空。
                //最好有提示。
                Log.e(TAG, "conditionValueOne isEmpty");
                isConditionValueEmptyOne = true;
            } else {
                isConditionValueEmptyOne = false;
            }
        }

//        判断是不是在时间触发方式里面
        if (isTimeTrigOne == 0) {
            timeTrigOne = "";
//            timeSwitchCmdOne = "";
        } else if (isTimeTrigOne == 1) {
            timeTrigOne = tvTimePickerOpenOne.getText().toString();
            //在时间触发方式里面获取时间的信息不能为空。
            if (TextUtils.isEmpty(timeTrigOne)) {
//                数据不能为空
//                最好有提示
                Log.e(TAG, "timeTrigOne isEmpty");
                isTimeTextViewEmptyOne = true;
            } else {
                isTimeTextViewEmptyOne = false;
            }
        }

        //如果是在立即触发方式里面，immediatelyTrigOne的值为空，那就是默认的值（false）。
        if (isImmediatelyTrigOne == 1 && TextUtils.isEmpty(immediatelyTrigOne)) {
            immediatelyTrigOne = "false";
        }

        sceneEquipmentBean0.setEquipmentMac(equipmentMacOne);
        sceneEquipmentBean0.setEquipmentNumber(equipmentNumberOne);
        sceneEquipmentBean0.setTrigger(triggerOne);
        sceneEquipmentBean0.setTimeTrig(timeTrigOne);
        sceneEquipmentBean0.setTimeSwitchCmd(timeSwitchCmdOne);
        sceneEquipmentBean0.setImmediatelyTrig(immediatelyTrigOne);
        sceneEquipmentBean0.setConditionEquipmentMac(conditionEquipmentMacOne);
        sceneEquipmentBean0.setCoditionNumber(conditionEquipmentNumberOne);
        sceneEquipmentBean0.setConditionValue(conditionValueOne);
        sceneEquipmentBean0.setTrigSymbol(symbolOne);
        sceneEquipmentBean0.setTemp(DB_TEMP);//添加的

    }


    private void doWithConditionStringTwo() {
//        String data;
        sceneEquipmentBean1 = new SceneEquipmentBean();
        equipmentMacTwo = equipmentBeanActivity.getMac_ADDR();
        if (isTvConditionValueTwo == 0) {
            conditionValueTwo = "";
        } else if (isTvConditionValueTwo == 1) {
            Log.e(TAG, "isTvConditionValueTwo == 1");
            conditionValueTwo = etConditionValueTwo.getText().toString();
            if (TextUtils.isEmpty(conditionValueTwo)) {
                Log.e(TAG, "conditionValueTwo isEmpty");
                isConditionValueEmptyTwo = true;
            } else {
                isConditionValueEmptyTwo = false;
            }
        }

        if (isTimeTrigTwo == 0) {
            timeTrigTwo = "";
//            timeSwitchCmdTwo = "";
        } else if (isTimeTrigTwo == 1) {
            timeTrigTwo = tvTimePickerOpenTwo.getText().toString();
            if (TextUtils.isEmpty(timeTrigTwo)) {
                Log.e(TAG, "timeTrigTwo isEmpty");
                isTimeTextViewEmptyTwo = true;
            } else {
                isTimeTextViewEmptyTwo = false;
            }
        }

        if (isImmediatelyTrigTwo == 1 && TextUtils.isEmpty(immediatelyTrigTwo)) {
            immediatelyTrigTwo = "false";
        }

//        data = "equipmentMacTwo="+equipmentMacTwo+",equipmentNumberTwo="+equipmentNumberTwo+",triggerTwo="+triggerTwo
//                +",timeTrigTwo="+timeTrigTwo+",immediatelyTrigTwo="+immediatelyTrigTwo+",conditionEquipmentMacTwo="+conditionEquipmentMacTwo
//                +",conditionEquipmentNumberTwo="+conditionEquipmentNumberTwo+",conditionValueTwo="+conditionValueTwo + ",symbolTwo=" + symbolTwo;
//
//        sceneEquipmentBean1.setEquipmentMac(equipmentMacTwo);
//        sceneEquipmentBean1.setEquipmentNumber(equipmentNumberTwo);
//        sceneEquipmentBean1.setTrigger(triggerTwo);
//        sceneEquipmentBean1.setTimeTrig(timeTrigTwo);
//        sceneEquipmentBean1.setImmediatelyTrig(immediatelyTrigTwo);
////        sceneEquipmentBean1.setConditionTrig(conditionEquipmentMacTwo);
//        sceneEquipmentBean1.setCoditionNumber(conditionEquipmentNumberTwo);
//        sceneEquipmentBean1.setConditionValue(conditionValueTwo);
//        sceneEquipmentBean1.setTrigSymbol(symbolTwo);
//        sceneEquipmentBean1.setTemp(DB_TEMP);//添加的
//        return data;

        sceneEquipmentBean1.setEquipmentMac(equipmentMacTwo);
        sceneEquipmentBean1.setEquipmentNumber(equipmentNumberTwo);
        sceneEquipmentBean1.setTrigger(triggerTwo);
        sceneEquipmentBean1.setTimeTrig(timeTrigTwo);
        sceneEquipmentBean1.setTimeSwitchCmd(timeSwitchCmdTwo);
        sceneEquipmentBean1.setImmediatelyTrig(immediatelyTrigTwo);
        sceneEquipmentBean1.setConditionEquipmentMac(conditionEquipmentMacTwo);
        sceneEquipmentBean1.setCoditionNumber(conditionEquipmentNumberTwo);
        sceneEquipmentBean1.setConditionValue(conditionValueTwo);
        sceneEquipmentBean1.setTrigSymbol(symbolTwo);
        sceneEquipmentBean1.setTemp(DB_TEMP);//添加的

    }


    //第三个开关数据处理
    private void doWithConditionStringThree() {
//        String data;
        sceneEquipmentBean2 = new SceneEquipmentBean();
        equipmentMacThree = equipmentBeanActivity.getMac_ADDR();
        if (isTvConditionValueThree == 0) {
            conditionValueThree = "";
        } else if (isTvConditionValueThree == 1) {
            Log.e(TAG, "isTvConditionValueThree == 1");
            conditionValueThree = etConditionValueThree.getText().toString();
            if (TextUtils.isEmpty(conditionValueThree)) {
                Log.e(TAG, "conditionValueThree isEmpty");
                isConditionValueEmptyThree = true;
            } else {
                isConditionValueEmptyThree = false;
            }
        }

        if (isTimeTrigThree == 0) {
            timeTrigThree = "";
        } else if (isTimeTrigThree == 1) {
            timeTrigThree = tvTimePickerOpenThree.getText().toString();
            if (TextUtils.isEmpty(timeTrigThree)) {
                Log.e(TAG, "timeTrigThree isEmpty");
                isTimeTextViewEmptyThree = true;
            } else {
                isTimeTextViewEmptyThree = false;
            }
        }

        if (isImmediatelyTrigThree == 1 && TextUtils.isEmpty(immediatelyTrigThree)) {
            immediatelyTrigThree = "false";
        }

        sceneEquipmentBean2.setEquipmentMac(equipmentMacThree);
        sceneEquipmentBean2.setEquipmentNumber(equipmentNumberThree);
        sceneEquipmentBean2.setTrigger(triggerThree);
        sceneEquipmentBean2.setTimeTrig(timeTrigThree);
        sceneEquipmentBean2.setTimeSwitchCmd(timeSwitchCmdThree);
        sceneEquipmentBean2.setImmediatelyTrig(immediatelyTrigThree);
        sceneEquipmentBean2.setConditionEquipmentMac(conditionEquipmentMacThree);
        sceneEquipmentBean2.setCoditionNumber(conditionEquipmentNumberThree);
        sceneEquipmentBean2.setConditionValue(conditionValueThree);
        sceneEquipmentBean2.setTrigSymbol(symbolThree);
        sceneEquipmentBean2.setTemp(DB_TEMP);//添加的

    }


}
