package com.zuobiao.smarthome.smarthome3.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.activity.AddSceneActivity;
import com.zuobiao.smarthome.smarthome3.activity.SocketSettingActivity;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;

import java.util.List;

/**
 * Author Administrator
 * Time 2016/3/5.
 */
public class SceneEquipmentSettingAdapter extends BaseAdapter{


    private Context mContext;
    private List<EquipmentBean> mList;
    private DBcurd dBcurd;
    private Util util;
    private AddSceneActivity mAddSceneActivity;
    private int setCheckPostion = -1;
    private List<Integer> ilists;


    public SceneEquipmentSettingAdapter(Context context, List<EquipmentBean> list,AddSceneActivity addSceneActivity){
        mContext = context;
        mList = list;
        dBcurd = new DBcurd(mContext);
        util = new Util();
        mAddSceneActivity = addSceneActivity;

    }

    public void setSetCheckPostion(int CheckPostion){
        this.setCheckPostion = CheckPostion;
    }

    public void setSetCheckPostions(List<Integer> ilist){
        this.ilists = ilist;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view ;
        ViewHolder viewHolder;
        final int positioned = position;
        final EquipmentBean equipmentBean = mList.get(position);
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.add_scene_list_view_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvAddSceneListViewItem = (TextView)view.findViewById(R.id.tvAddSceneListViewItem);
            viewHolder.btnAddSceneListViewItem = (Button)view.findViewById(R.id.btnAddSceneListViewItem);
            viewHolder.addSceneListViewItemCheckBox = (CheckBox)view.findViewById(R.id.addSceneListViewItemCheckBox);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }


        if(!dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")){
            String tvNameText = new String(util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
            if(TextUtils.isEmpty(tvNameText)){
                viewHolder.tvAddSceneListViewItem.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            }else {
                viewHolder.tvAddSceneListViewItem.setText(tvNameText);
            }
        }else{
            viewHolder.tvAddSceneListViewItem.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }
        viewHolder.btnAddSceneListViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(mContext, SocketSettingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.ADD_SCENE_ACTIVITY_2_SOCKET_SETTING_ACTIVITY, equipmentBean);
                    intent.putExtras(bundle);
                    intent.putExtra(Constant.ADD_SCENE_ACTIVITY_2_SOCKET_SETTING_ACTIVITY_POSITION,positioned);
                    mAddSceneActivity.startActivityForResult(intent, Constant.EQUIPMENT_TYPE_REQUEST_CODE);

            }
        });

        return view;
    }


    private class ViewHolder{
        TextView tvAddSceneListViewItem;
        Button btnAddSceneListViewItem;
        CheckBox addSceneListViewItemCheckBox;
    }
}
