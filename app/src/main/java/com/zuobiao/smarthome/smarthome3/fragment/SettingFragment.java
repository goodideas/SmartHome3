package com.zuobiao.smarthome.smarthome3.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.activity.MainActivity;
import com.zuobiao.smarthome.smarthome3.activity.SmartLinkActivity;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.Util;

public class SettingFragment extends BaseFragment {

    private RelativeLayout rlSmartLink;
    private RelativeLayout aboutRelativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingLayout = inflater.inflate(R.layout.setting_layout,
                container, false);
        rlSmartLink = (RelativeLayout)settingLayout.findViewById(R.id.smartLinkRelativeLayout);
        rlSmartLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SmartLinkActivity.class));
            }
        });

        aboutRelativeLayout = (RelativeLayout)settingLayout.findViewById(R.id.aboutRelativeLayout);
        aboutRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast t = Toast.makeText(getActivity(),"南翔智地创客学院智能家居系统",Toast.LENGTH_SHORT);
//                t.setGravity(Gravity.CENTER, 0, 0);
//                t.show();
                Util.showToast(getActivity(),R.string.app_name, Gravity.CENTER, 0, 0);
            }
        });



        return settingLayout;
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_SETTING;
    }


}
