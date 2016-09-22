package com.zuobiao.smarthome.smarthome3.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.fragment.EquipmentFragment;
import com.zuobiao.smarthome.smarthome3.fragment.SceneFragment;
import com.zuobiao.smarthome.smarthome3.fragment.SettingFragment;

public class MainActivity extends StatusActivity implements View.OnClickListener{

    private TextView tvEquipmentFragment;
    private TextView tvSceneFragment;
    private TextView tvSettingFragment;

    private Drawable equipment,equipmentPress;
    private Drawable scene,scenePress;
    private Drawable setting,settingPress;
    private static final int TEXT_COLOR_PRESS = 0xFF4B87D6;
    private static final int TEXT_COLOR = 0xFFA1A1A1;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private EquipmentFragment equipmentFragment;
    private SceneFragment sceneFragment;
    private SettingFragment settingFragment;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawable();
        initFindView();
        initListen();
        setDefault();

    }

    private void initDrawable() {
        equipmentPress= getResources().getDrawable(R.drawable.equipment_press);
        scenePress= getResources().getDrawable(R.drawable.scene_press);
        settingPress= getResources().getDrawable(R.drawable.setting_press);

        equipment= getResources().getDrawable(R.drawable.equipment_unpress);
        scene= getResources().getDrawable(R.drawable.scene_unpress);
        setting= getResources().getDrawable(R.drawable.setting_unpress);


        equipmentPress.setBounds(0, 0, equipmentPress.getMinimumWidth()*2/3, equipmentPress.getMinimumHeight()*2/3);
        scenePress.setBounds(0, 0, scenePress.getMinimumWidth()*2/3, scenePress.getMinimumHeight()*2/3);
        settingPress.setBounds(0, 0, settingPress.getMinimumWidth()*2/3, settingPress.getMinimumHeight()*2/3);

        equipment.setBounds(0, 0, equipment.getMinimumWidth()*2/3, equipment.getMinimumHeight()*2/3);
        scene.setBounds(0, 0, scene.getMinimumWidth()*2/3, scene.getMinimumHeight()*2/3);
        setting.setBounds(0, 0, setting.getMinimumWidth()*2/3, setting.getMinimumHeight()*2/3);
    }


    private void initFindView() {
        tvEquipmentFragment = (TextView)findViewById(R.id.tvEquipmentFragment);
        tvSceneFragment = (TextView)findViewById(R.id.tvSceneFragment);
        tvSettingFragment = (TextView)findViewById(R.id.tvSettingFragment);
    }

    private void initListen() {
        tvEquipmentFragment.setOnClickListener(this);
        tvSceneFragment.setOnClickListener(this);
        tvSettingFragment.setOnClickListener(this);
    }

    private void setDefault() {
        tvSetColor(tvEquipmentFragment, tvSceneFragment, tvSettingFragment);
        tvSetCompoundDrawables(tvEquipmentFragment, equipmentPress, tvSceneFragment, scene, tvSettingFragment, setting);
        equipmentFragment = new EquipmentFragment();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContent, equipmentFragment);
        fragmentTransaction.commit();
    }

    private void tvSetColor(TextView tvPress, TextView tvNormal1, TextView tvNormal2) {
        tvPress.setTextColor(TEXT_COLOR_PRESS);
        tvNormal1.setTextColor(TEXT_COLOR);
        tvNormal2.setTextColor(TEXT_COLOR);

    }

    private void tvSetCompoundDrawables(TextView tv1, Drawable press, TextView tv2, Drawable unpress2, TextView tv3, Drawable unpress3) {
        tv1.setCompoundDrawables(null, press, null, null);
        tv2.setCompoundDrawables(null, unpress2, null, null);
        tv3.setCompoundDrawables(null, unpress3, null, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (v.getId()){
            case R.id.tvEquipmentFragment:
                tvSetColor(tvEquipmentFragment, tvSceneFragment, tvSettingFragment);
                tvSetCompoundDrawables(tvEquipmentFragment, equipmentPress, tvSceneFragment, scene, tvSettingFragment, setting);
                if(equipmentFragment==null){
                    equipmentFragment = new EquipmentFragment();
                }
                fragmentTransaction.replace(R.id.fragmentContent,equipmentFragment);

                break;
            case R.id.tvSceneFragment:
                tvSetColor(tvSceneFragment, tvEquipmentFragment, tvSettingFragment);
                tvSetCompoundDrawables(tvEquipmentFragment, equipment, tvSceneFragment, scenePress, tvSettingFragment, setting);
                if(sceneFragment==null){
                    sceneFragment = new SceneFragment();
                }
                fragmentTransaction.replace(R.id.fragmentContent, sceneFragment);
                break;
            case R.id.tvSettingFragment:
                tvSetColor(tvSettingFragment, tvEquipmentFragment, tvSceneFragment);
                tvSetCompoundDrawables(tvEquipmentFragment, equipment, tvSceneFragment, scene, tvSettingFragment, settingPress);
                if(settingFragment==null){
                    settingFragment = new SettingFragment();
                }
                fragmentTransaction.replace(R.id.fragmentContent,settingFragment);
                break;
        }
        fragmentTransaction.commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU
                && event.getAction() == KeyEvent.ACTION_DOWN){
            Toast.makeText(getApplicationContext(),"菜单键",Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }


}
