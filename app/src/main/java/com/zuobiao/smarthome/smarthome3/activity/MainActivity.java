package com.zuobiao.smarthome.smarthome3.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.fragment.BaseFragment;
import com.zuobiao.smarthome.smarthome3.ui.BottomControlPanel;
import com.zuobiao.smarthome.smarthome3.util.Constant;

public class MainActivity extends StatusActivity implements BottomControlPanel.BottomPanelCallback {
    BottomControlPanel bottomPanel = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    public static String currFragTag = "";
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initUI();
        fragmentManager = getFragmentManager();
        setDefaultFirstFragment(Constant.FRAGMENT_FLAG_EQUIPMENT);
    }



    private void initUI(){
        bottomPanel = (BottomControlPanel)findViewById(R.id.bottom_layout);
        if(bottomPanel != null){
            bottomPanel.initBottomPanel();
            bottomPanel.setBottomCallback(this);
        }
    }

    @Override
    public void onBottomPanelClick(int itemId) {
        String tag = "";
        if((itemId & Constant.BTN_FLAG_EQUIPMENT) != 0){
            tag = Constant.FRAGMENT_FLAG_EQUIPMENT;
        }else if((itemId & Constant.BTN_FLAG_SCENE) != 0){
            tag = Constant.FRAGMENT_FLAG_SCENE;
        }else if((itemId & Constant.BTN_FLAG_SETTING) != 0){
            tag = Constant.FRAGMENT_FLAG_SETTING;
        }
        setTabSelection(tag); //切换Fragment
    }

    private  void switchFragment(String tag){
        if(TextUtils.equals(tag, currFragTag)){
            return;
        }
        //把上一个fragment detach掉
        if(currFragTag != null && !currFragTag.equals("")){
            detachFragment(getFragment(currFragTag));
        }
        attachFragment(R.id.fragment_content, getFragment(tag), tag);
        commitTransactions(tag);
    }
    private Fragment getFragment(String tag){

        Fragment f = fragmentManager.findFragmentByTag(tag);

        if(f == null){
//            Toast.makeText(getApplicationContext(), "fragment = null tag = " + tag, Toast.LENGTH_SHORT).show();
            f = BaseFragment.newInstance(getApplicationContext(), tag);
        }
        return f;

    }

    private void commitTransactions(String tag){
        if (fragmentTransaction != null && !fragmentTransaction.isEmpty()) {
            fragmentTransaction.commit();
            currFragTag = tag;
            fragmentTransaction = null;
        }
    }

    private FragmentTransaction ensureTransaction( ){
        if(fragmentTransaction == null){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        }
        return fragmentTransaction;

    }

    private void attachFragment(int layout, Fragment f, String tag){
        if(f != null){
            if(f.isDetached()){
                ensureTransaction();
                fragmentTransaction.attach(f);

            }else if(!f.isAdded()){
                ensureTransaction();
                fragmentTransaction.add(layout, f, tag);
            }
        }
    }
    private void detachFragment(Fragment f){

        if(f != null && !f.isDetached()){
            ensureTransaction();
            fragmentTransaction.detach(f);
        }
    }


    public  void setTabSelection(String tag) {
        // 开启一个Fragment事务
        fragmentTransaction = fragmentManager.beginTransaction();
/*		 if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_EQUIPMENT)){
		   if (messageFragment == null) {
				messageFragment = new EquipmentFragment();
			}

		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_SCENE)){
			if (contactsFragment == null) {
				contactsFragment = new SceneFragment();
			}

		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_NEWS)){
			if (newsFragment == null) {

			}

		}else if(TextUtils.equals(tag,Constant.FRAGMENT_FLAG_SETTING)){
			if (settingFragment == null) {
				settingFragment = new SettingFragment();
			}
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_SIMPLE)){
			if (simpleFragment == null) {
				simpleFragment = new SimpleFragment();
			}

		}*/
        switchFragment(tag);

    }


    @Override
    protected void onStop() {
        super.onStop();
        currFragTag = "";
    }

    private void setDefaultFirstFragment(String tag){
        Log.i("yan", "setDefaultFirstFragment enter... currFragTag = " + currFragTag);
        setTabSelection(tag);
        bottomPanel.defaultBtnChecked();
        Log.i("yan", "setDefaultFirstFragment exit...");
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
