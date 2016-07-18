package com.zuobiao.smarthome.smarthome3.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;

import java.util.List;

/**
 * Author Administrator
 * Time 2016/3/5.
 */
public class ConditionSpinnerAdapter extends BaseAdapter{

    private Context mContext;
    private List<EquipmentBean> mList;
    private DBcurd dBcurd;
    private Util util;
    public ConditionSpinnerAdapter(Context context,List<EquipmentBean> list){
        mContext = context;
        mList = list;
        dBcurd = new DBcurd(context);
        util = new Util();
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
        final EquipmentBean equipmentBean = mList.get(position);
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.scene_equipment_spinner_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvSceneEquipmentSettingSpinnerItem = (TextView)view.findViewById(R.id.tvSceneEquipmentSettingSpinnerItem);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        if(!dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")){
            String tvNameText = new String(util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();
            if(TextUtils.isEmpty(tvNameText)){
                viewHolder.tvSceneEquipmentSettingSpinnerItem.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            }else {
                viewHolder.tvSceneEquipmentSettingSpinnerItem.setText(tvNameText);
            }
        }else{
            viewHolder.tvSceneEquipmentSettingSpinnerItem.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }


        return  view;
    }

    private class ViewHolder{
        TextView tvSceneEquipmentSettingSpinnerItem;
    }
}
