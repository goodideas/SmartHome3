package com.zuobiao.smarthome.smarthome3.ui;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class BottomControlPanel extends LinearLayout implements View.OnClickListener {
	private static final String TAG = "BottomControlPanel";
	private Context mContext;
	private ImageText mMsgBtn = null;
	private ImageText mContactsBtn = null;
	private ImageText mSettingBtn = null;
	private int DEFALUT_BACKGROUND_COLOR = Color.rgb(243, 243, 243); //Color.rgb(192, 192, 192)
	private BottomPanelCallback mBottomCallback = null;
	private List<ImageText> viewList = new ArrayList<ImageText>();

	public interface BottomPanelCallback{
		void onBottomPanelClick(int itemId);
	}
	public BottomControlPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@SuppressLint("MissingSuperCall")
	@Override
	protected void onFinishInflate() {
		mMsgBtn = (ImageText)findViewById(R.id.btn_message);
		mContactsBtn = (ImageText)findViewById(R.id.btn_contacts);
		mSettingBtn = (ImageText)findViewById(R.id.btn_setting);
		setBackgroundColor(DEFALUT_BACKGROUND_COLOR);
		setOrientation(LinearLayout.HORIZONTAL);
		viewList.add(mMsgBtn);
		viewList.add(mContactsBtn);
		viewList.add(mSettingBtn);

	}
	public void initBottomPanel(){
		if(mMsgBtn != null){
			mMsgBtn.setImage(R.drawable.equipment_unpress);
			mMsgBtn.setText("设备");
		}
		if(mContactsBtn != null){
			mContactsBtn.setImage(R.drawable.scene_unpress);
			mContactsBtn.setText("场景");
		}
		if(mSettingBtn != null){
			mSettingBtn.setImage(R.drawable.setting_unpress);
			mSettingBtn.setText("设置");
		}
		setBtnListener();

	}
	private void setBtnListener(){
		int num = this.getChildCount();
		for(int i = 0; i < num; i++){
			View v = getChildAt(i);
			if(v != null){
				v.setOnClickListener(this);
			}
		}
	}
	public void setBottomCallback(BottomPanelCallback bottomCallback){
		mBottomCallback = bottomCallback;
	}
	@Override
	public void onClick(View v) {
		initBottomPanel();
		int index = -1;
		switch(v.getId()){
			case R.id.btn_message:
				index = Constant.BTN_FLAG_EQUIPMENT;
				mMsgBtn.setChecked(Constant.BTN_FLAG_EQUIPMENT);
				break;
			case R.id.btn_contacts:
				index = Constant.BTN_FLAG_SCENE;
				mContactsBtn.setChecked(Constant.BTN_FLAG_SCENE);
				break;
			case R.id.btn_setting:
				index = Constant.BTN_FLAG_SETTING;
				mSettingBtn.setChecked(Constant.BTN_FLAG_SETTING);
				break;
			default:break;
		}
		if(mBottomCallback != null){
			mBottomCallback.onBottomPanelClick(index);
		}
	}
	public void defaultBtnChecked(){
		if(mMsgBtn != null){
			mMsgBtn.setChecked(Constant.BTN_FLAG_EQUIPMENT);
		}
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		layoutItems(left, top, right, bottom);
	}
	/**最左边和最右边的view由母布局的padding进行控制位置。这里需对第2、3个view的位置重新设置
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	private void layoutItems(int left, int top, int right, int bottom){
		int n = getChildCount();
		if(n == 0){
			return;
		}
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
//		Log.i(TAG, "paddingLeft = " + paddingLeft + " paddingRight = " + paddingRight);
		int width = right - left;
		int height = bottom - top;
//		Log.i(TAG, "width = " + width + " height = " + height);
		int allViewWidth = 0;
		for(int i = 0; i< n; i++){
			View v = getChildAt(i);
//			Log.i(TAG, "v.getWidth() = " + v.getWidth());
			allViewWidth += v.getWidth();
		}
		int blankWidth = (width - allViewWidth - paddingLeft - paddingRight) / (n - 1);
//		Log.i(TAG, "blankV = " + blankWidth );

		LayoutParams params1 = (LayoutParams) viewList.get(1).getLayoutParams();
		params1.leftMargin = blankWidth;
		viewList.get(1).setLayoutParams(params1);

		LayoutParams params2 = (LayoutParams) viewList.get(2).getLayoutParams();
		params2.leftMargin = blankWidth;
		viewList.get(2).setLayoutParams(params2);
	}



}
