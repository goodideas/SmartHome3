package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.fragment.SceneFragment;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;
import com.zuobiao.smarthome.smarthome3.entity.SceneEquipmentBean;
import com.zuobiao.smarthome.smarthome3.util.SceneEquipmentSettingAdapter;
import com.zuobiao.smarthome.smarthome3.entity.SceneNameBean;
import com.zuobiao.smarthome.smarthome3.util.SpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AddSceneActivity extends StatusActivity {

    private static final String TAG = "AddSceneActivity";

    private Button btnAddSceneBack;
    private Button btnAddSceneSave;
    private Button btnUseScene;
    private EditText etSceneName;
    private ListView scene_setting_listview;
    private SceneEquipmentSettingAdapter sceneSettingAdapter;
    private List<EquipmentBean> sceneEquipments;
    private DBcurd dBcurd;
    private Button btnTest;
    private SpHelper spHelper;
    private int intentRequest = -1;
    private int listViewItemNumber;
    private List<Integer> listInt = new ArrayList<>();
    private String listviewItemSceneName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scene);
        init();
        dBcurd = new DBcurd(AddSceneActivity.this);
        sceneEquipments = dBcurd.getAllSceneEquipments();
        sceneSettingAdapter = new SceneEquipmentSettingAdapter(AddSceneActivity.this,sceneEquipments,this);
        scene_setting_listview.setAdapter(sceneSettingAdapter);
        spHelper = new SpHelper(AddSceneActivity.this);
        Intent intent = getIntent();
        try {
            intentRequest = intent.getExtras().getInt(SceneFragment.INTENT_ADD_FLAG_STRING);
            if(intentRequest == SceneFragment.INTENT_ADD_FLAG){
                Log.e(TAG,"跳转="+intentRequest);
            }else if(intentRequest == SceneFragment.INTENT_LIST_VIEW_ITEM_FLAG){
                Log.e(TAG,"item跳转="+intentRequest);
                listViewItemNumber =  intent.getExtras().getInt(SceneFragment.INTENT_LIST_VIEW_ITEM_FLAG_GET_ITEM_NUMBER);
                listviewItemSceneName = intent.getExtras().getString(SceneFragment.INTENT_LIST_VIEW_ITEM_FLAG_GET_ITEM_SCENE_NAME);
//                如果是从ListView类表上点击过来的，那就是已经保存过的数据，现在要做的就是还原数据到UI上。
                setDataOnSave(listviewItemSceneName);
                setChecked();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        btnUseScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(intentRequest == SceneFragment.INTENT_ADD_FLAG){
                    List<SceneNameBean> list = dBcurd.getAllSceneName();
                    int length = list.size();
                    spHelper.SaveSpIsUseNumber(length);
                }else if(intentRequest == SceneFragment.INTENT_LIST_VIEW_ITEM_FLAG){
                     spHelper.SaveSpIsUseNumber(listViewItemNumber);
                }

            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dBcurd.delAllSceneData();

            }
        });

        btnAddSceneBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dBcurd.delAllSceneData();
                finish();
            }
        });

        btnUseScene.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CheckBox cb2 = (CheckBox) scene_setting_listview.getChildAt(1).findViewById(R.id.addSceneListViewItemCheckBox);
                cb2.setVisibility(View.VISIBLE);
                cb2.setChecked(true);
                Log.e(TAG, "测试");
                return true;
            }
        });

        /**
           设计思路
           有2个数据表
           数据表一 scene_equipment_list
           数据表二 scene_equipment_list_all
         首先是存下返回过来的数据到数据库表scene_equipment_list，同一个设备第一次返回来的数据进行插入操作，多次返回的数据进行更新操作。
         这样处理过后，不管场景设备是第一次设置，还是之后的修改设置，数据都只有一个，保证单一数据，没有重复的数据。
         之后再把这些数据添加上场景名，串起来，放到另一个数据库表scene_equipment_list_all，之后查询场景的数据，就是根据场景名来查找这些数据。
         然后再删除数据库表scene_equipment_list里面的场景设备的数据。
        **/
        btnAddSceneSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intentRequest == SceneFragment.INTENT_ADD_FLAG) {
                    //这是添加按钮
                    if (TextUtils.isEmpty(etSceneName.getText().toString())) {
//                        Toast.makeText(getApplicationContext(), "场景还没有名字？", Toast.LENGTH_SHORT).show();
                        Util.showToast(getApplicationContext(), "场景还没有名字？");
                    } else {

                        //根据temp 查找所有的数据，然后把temp的字符串换掉 然后添加场景名
                        dBcurd.updateSceneAddSceneName(SocketSettingActivity.DB_TEMP, etSceneName.getText().toString());

                        List<SceneEquipmentBean> list = dBcurd.getAllSceneEquipmentBySceneName(etSceneName.getText().toString());
                        int equipmentsListLength = list.size();
                        if (equipmentsListLength != 0) {
                            dBcurd.addSceneNameData(etSceneName.getText().toString(), equipmentsListLength);
                            for (int i = 0; i < list.size(); i++) {
                                dBcurd.addSceneAllData(list.get(i));
                                Log.e(TAG, "list.size()=" + list.size() + " " + list.get(i).getEquipmentMac());
                            }
                            //删除之前的数据
                            dBcurd.delAllSceneData();
                            finish();
                        } else {
//                            Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                            t.setText("没有任何设置，如果不设置，请按返回！");
//                            t.setGravity(Gravity.CENTER, 0, 0);
//                            t.show();
                            Util.showToast(getApplicationContext(), "没有任何设置，如果不设置，请按返回！",Gravity.CENTER, 0, 0);
                        }

                    }

                } else if (intentRequest == SceneFragment.INTENT_LIST_VIEW_ITEM_FLAG) {

                    // TODO: 2016/3/12 当已经设置过的场景，如果再进去修改，场景内的设备可以修改，但是场景名修改有bug
                    //这个是ListView的item按钮

                    //*****
                    if (TextUtils.isEmpty(etSceneName.getText().toString())) {
//                        Toast.makeText(getApplicationContext(), "场景还没有名字？", Toast.LENGTH_SHORT).show();
                        Util.showToast(getApplicationContext(), "场景还没有名字？");
                    } else {

//                        dBcurd.updateSceneAddSceneName(SocketSettingActivity.DB_TEMP, etSceneName.getText().toString());
//                        List<SceneEquipmentBean> list = dBcurd.getAllSceneEquipmentBySceneName(etSceneName.getText().toString());

                        //根据temp 查找所有的数据，然后把temp的字符串换掉 然后添加场景名
                        dBcurd.updateSceneAddSceneName(SocketSettingActivity.DB_TEMP, etSceneName.getText().toString());

                        List<SceneEquipmentBean> list = dBcurd.getAllSceneEquipmentBySceneName(etSceneName.getText().toString());
                        int equipmentsListLength = list.size();

//                        dBcurd.addSceneNameData(etSceneName.getText().toString(), equipmentsListLength);
                        /**
                         不同于添加按钮的是这个
                         需要做更新操作
                         **/
//                        dBcurd.updataSceneNameData(etSceneName.getText().toString(), equipmentsListLength);
//                        dBcurd.delSceneAllBySceneAllName(etSceneName.getText().toString());
                        dBcurd.updataSceneNameData(etSceneName.getText().toString(), equipmentsListLength, listviewItemSceneName);
                        //删除数据库之前存的信息
                        dBcurd.delSceneAllBySceneAllName(listviewItemSceneName);
                        if (equipmentsListLength != 0) {
                            for (int i = 0; i < list.size(); i++) {
                                dBcurd.addSceneAllData(list.get(i));
                                Log.e(TAG, "list.size()=" + list.size() + " " + list.get(i).getEquipmentMac());
                            }
                            //删除之前的数据
                            dBcurd.delAllSceneData();
                            finish();
                        } else {
//                            Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
//                            t.setText("没有任何设置，如果不设置，请按返回！");
//                            t.setGravity(Gravity.CENTER, 0, 0);
//                            t.show();
                            Util.showToast(getApplicationContext(), "没有任何设置，如果不设置，请按返回！", Gravity.CENTER, 0, 0);
                        }
                    }
                    //*****

                }


            }
        });



    }

    private void init() {
        btnAddSceneBack = (Button)findViewById(R.id.btnAddSceneBack);
        btnAddSceneSave = (Button)findViewById(R.id.btnAddSceneSave);
        btnUseScene = (Button)findViewById(R.id.btnUseScene);
        scene_setting_listview = (ListView)findViewById(R.id.scene_setting_listview);
        etSceneName = (EditText)findViewById(R.id.etSceneName);
        btnTest = (Button)findViewById(R.id.btnTest);
    }


    private void setChecked(){
        //为什么要用这个函数
        //因为scene_setting_listview.getChildAt(listInt.get(i)) 会为空，必须等到UI，就是ListView加载完成后，才能使用。
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i< listInt.size();i++){
                    CheckBox cb2 = (CheckBox)scene_setting_listview.getChildAt(listInt.get(i)).findViewById(R.id.addSceneListViewItemCheckBox);
                    cb2.setVisibility(View.VISIBLE);
                    cb2.setChecked(true);
                }
            }
        }, 20);
    }


    private void setDataOnSave(String sceneName){


        etSceneName.setText(sceneName);
        int size = sceneEquipments.size();

//        2重循环。第一重是数据库的储存的场景设置的信息，第二重是列表的数据
        List<SceneEquipmentBean> sceneEquipmentBeanLists = dBcurd.getAllSceneEquipmentBySceneNameAll(sceneName);
        int sceneEquipmentBeanListsLength = sceneEquipmentBeanLists.size();

        /**
         * 已经设置过的数据
         * 把场景设置的数据（scene_equipment_list_all）的信息（根据场景名）插入到temp的数据库表（scene_equipment_list）中
         * **/
        for(int k = 0;k<sceneEquipmentBeanListsLength;k++){
//            sceneEquipmentBeanLists.get(k).setTemp(SocketSettingActivity.DB_TEMP);

            dBcurd.addSceneData(sceneEquipmentBeanLists.get(k));

        }

        for (int n= 0 ;n <sceneEquipmentBeanListsLength;n++)
        {
            sceneEquipmentBeanLists.get(n).setTemp(SocketSettingActivity.DB_TEMP);
            dBcurd.updateTemp(sceneEquipmentBeanLists.get(n));
        }
        //添加temp数据



        Log.e(TAG, "size=" + size + " sceneEquipmentBeanListsLength=" +sceneEquipmentBeanListsLength);
            for(int i = 0;i< size;i++){
                for(int j = 0;j < sceneEquipmentBeanListsLength;j++){
                Log.e(TAG,"sceneEquipments.get(i).getMac_ADDR()="+sceneEquipments.get(i).getMac_ADDR()+" sceneEquipmentBeanLists.get(j).getEquipmentMac()="+sceneEquipmentBeanLists.get(j).getEquipmentMac());
                //如果从数据库得来的设备信息有和现在设备符合的，那就是认为这个设备被设置过了，可以显示“已设置”.
                if(sceneEquipments.get(i).getMac_ADDR().equalsIgnoreCase(sceneEquipmentBeanLists.get(j).getEquipmentMac())){
                    listInt.add(i);
                    Log.e(TAG,"一样的 i="+i);
                }
            }

        }






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("AddSceneActivity","requestCode="+requestCode+"resultCode="+resultCode);
        /**
         如果在设备设置中没有进行设置，是不会返回到这边的，如果有返回，就一定会有数据。
         在这边可以得到所有设备设置的数据，然后存储在数据库当中
         */
        if(requestCode == Constant.EQUIPMENT_TYPE_REQUEST_CODE&&resultCode == Constant.EQUIPMENT_TYPE_RESULT_CODE){
            boolean booleancode = data.getExtras().getBoolean("resultIsSwitchs");
            int sceneEquipmentCount = data.getExtras().getInt("sceneEquipmentCount");
            String resultEquipmentMac = data.getExtras().getString("equipmentMac");
            int addSceneActivityListViewItem = data.getExtras().getInt("addSceneActivityListViewItem");
            int isEmptySetting = data.getExtras().getInt("isEmptySetting");//如果没有传过来信息，默认为0
            Log.e(TAG,"isEmptySetting="+isEmptySetting);
           if(addSceneActivityListViewItem!=-1){
               if(isEmptySetting!=0&&isEmptySetting == -1){
                   String clearEquipmentMac =  data.getExtras().getString("clearSetting");
                   CheckBox cb2 = (CheckBox)scene_setting_listview.getChildAt(addSceneActivityListViewItem).findViewById(R.id.addSceneListViewItemCheckBox);
                   cb2.setChecked(false);
                   cb2.setVisibility(View.GONE);
                   dBcurd.delAllEquipmentDataByEquipmentMac(clearEquipmentMac);
               }else {
                   CheckBox cb = (CheckBox) scene_setting_listview.getChildAt(addSceneActivityListViewItem).findViewById(R.id.addSceneListViewItemCheckBox);
                   cb.setVisibility(View.VISIBLE);
                   cb.setChecked(true);
               }
           }
            Log.e(TAG,"sceneEquipmentCount="+sceneEquipmentCount);
            if(!booleancode){
                //单个
                    SceneEquipmentBean sceneEquipmentBean0 = (SceneEquipmentBean) data.getExtras().get("sceneEquipmentBean0");
                    if (sceneEquipmentBean0 != null) {
                        if (dBcurd.isSaveSceneDataByEquipmentMac(sceneEquipmentBean0)) {
                            dBcurd.updateSceneData(sceneEquipmentBean0);
                            dBcurd.updateTemp(sceneEquipmentBean0);
                            Log.e(TAG, "设备1更新");
                        } else {
                            dBcurd.addSceneData(sceneEquipmentBean0);
                            dBcurd.updateTemp(sceneEquipmentBean0);
                            Log.e(TAG, "设备1添加");
                        }
                    }

            }else {
                //多个
                SceneEquipmentBean sceneEquipmentBean0 = (SceneEquipmentBean)data.getExtras().get("sceneEquipmentBean0");
                SceneEquipmentBean sceneEquipmentBean1 = (SceneEquipmentBean)data.getExtras().get("sceneEquipmentBean1");
                SceneEquipmentBean sceneEquipmentBean2 = (SceneEquipmentBean)data.getExtras().get("sceneEquipmentBean2");
                /**
                 因为这个设备是3控，所以设备数量返回的结果数量不唯一，
                 如果之前返回的是3个数量，这次返回2个数量，那就要删掉之前的3个数量，然后再存入数据库。
                 */
                dBcurd.delAllEquipmentDataByEquipmentMac(resultEquipmentMac);
                if(sceneEquipmentBean0!=null){
                    if(dBcurd.isSaveSceneDataByEquipmentMac(sceneEquipmentBean0)){
                        dBcurd.updateSceneData(sceneEquipmentBean0);
                        dBcurd.updateTemp(sceneEquipmentBean0);
                        Log.e(TAG, "设备1更新");
                    }else {
                        dBcurd.addSceneData(sceneEquipmentBean0);
                        dBcurd.updateTemp(sceneEquipmentBean0);
                        Log.e(TAG, "设备1添加");
                    }
                }
                if(sceneEquipmentBean1!=null){
                    if(dBcurd.isSaveSceneDataByEquipmentMac(sceneEquipmentBean1)){
                        dBcurd.updateSceneData(sceneEquipmentBean1);
                        dBcurd.updateTemp(sceneEquipmentBean1);
                        Log.e(TAG, "设备2更新");
                    }else {
                        dBcurd.addSceneData(sceneEquipmentBean1);
                        dBcurd.updateTemp(sceneEquipmentBean1);
                        Log.e(TAG, "设备2添加");
                    }
                }

                if(sceneEquipmentBean2!=null){
                    if(dBcurd.isSaveSceneDataByEquipmentMac(sceneEquipmentBean2)){
                        dBcurd.updateSceneData(sceneEquipmentBean2);
                        dBcurd.updateTemp(sceneEquipmentBean2);
                        Log.e(TAG, "设备3更新");
                    }else {
                        dBcurd.addSceneData(sceneEquipmentBean2);
                        dBcurd.updateTemp(sceneEquipmentBean2);
                        Log.e(TAG, "设备3添加");
                    }
                }

            }//else
        }

    }

    @Override
    public void onBackPressed() {
        dBcurd.delAllSceneData();
        super.onBackPressed();
    }
}
