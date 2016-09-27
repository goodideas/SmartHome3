package com.zuobiao.smarthome.smarthome3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.UdpHelper;
import com.zuobiao.smarthome.smarthome3.util.Util;

/**
 *欢迎界面，主要判断手机里面有没有保存过之前的网关信息和判断网关在不在线
 * 2个参数
 * hasGateWayInfo 网关的信息保存状态。有存过就是true，没有就是false
 * onLine   网关在线状态。在线就是true，不在线就是false
 * 这2个boolean型变量保存在sp文件中，可以随时更改，保存
 */
public class WelcomeActivity extends AppCompatActivity {

    private ImageView ivWelcome;
    private ScaleAnimation scaleAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ivWelcome = (ImageView)findViewById(R.id.ivWelcome);
        UdpHelper udpHelper = UdpHelper.getInstance();
        udpHelper.startUdpWithIp(Constant.BROADCAST_IP, WelcomeActivity.this);
        udpHelper.setIsSend(true);
        udpHelper.send(Util.broadcastData());
        udpHelper.doSearchGateWayOnWelcome(Constant.WELCOME_ACTIVITY_SEARCH_GATEWAY_WAIT_MAX_TIME);

        scaleAnimation = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(Constant.BEFORE_INTO_MAIN_ACTIVITY_MAX_TIME);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivWelcome.startAnimation(scaleAnimation);
            }
        }, Constant.WELCOME_ACTIVITY_SEARCH_GATEWAY_WAIT_MAX_TIME);

    }

}
