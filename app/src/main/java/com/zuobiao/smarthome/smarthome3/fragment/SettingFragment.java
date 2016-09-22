package com.zuobiao.smarthome.smarthome3.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "SettingFragment";
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated...");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart...");
        super.onStart();
    }


    @Override
    public void onPause() {
        Log.i(TAG, "onPause...");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop...");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView...");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy...");
        super.onDestroy();
    }
}
