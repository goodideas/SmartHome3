package com.zuobiao.smarthome.smarthome3.util;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.entity.EquipmentBean;

import java.util.List;

/**
 * Created by zhuangbinbin
 * on 2016/1/4.
 */
public class ImageListViewAdapter extends BaseAdapter{

    Context mContext;
    List<EquipmentBean> mList;
    private DBcurd dBcurd;
    private Util util;
    private boolean mIsShow;

    public ImageListViewAdapter(Context context,List<EquipmentBean> list,boolean isShow){
        mContext = context;
        mList = list;
        dBcurd = new DBcurd(context);
        util = new Util();
        mIsShow = isShow;
    }

    @Override
    public int getCount() {
        if(mList.size()>=0)
        return mList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("12a1s21a2s1","position="+position);
        View view ;
        ViewHolder viewHolder;
        EquipmentBean equipmentBean;
        equipmentBean = mList.get(position);
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_item,null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView)view.findViewById(R.id.imageItem);
            viewHolder.tvName = (TextView)view.findViewById(R.id.tvName);
            viewHolder.tvOnLine = (TextView)view.findViewById(R.id.tvOnLine);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        if(!dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()).equalsIgnoreCase("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")){
            String tvNameText = new String(Util.HexString2Bytes(dBcurd.getNickNameByMac(equipmentBean.getMac_ADDR()))).trim();

            if(TextUtils.isEmpty(tvNameText)){
                viewHolder.tvName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
            }else {
                viewHolder.tvName.setText(tvNameText);
            }
        }else{
            viewHolder.tvName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        }
        viewHolder.tvName.setTextSize(16);
//        viewHolder.tvName.setText(Constant.getTypeName(equipmentBean.getDevice_Type()));
        if (Constant.GATEWAY.equalsIgnoreCase(equipmentBean.getDevice_Type())){
            if(equipmentBean.isOnLine()){
                viewHolder.tvOnLine.setText("在线");
                viewHolder.image.setImageResource(Constant.getImageId(equipmentBean.getDevice_Type()));
            }else{
                viewHolder.tvOnLine.setText("不在线");
                viewHolder.image.setImageResource(R.drawable.off_line);
            }
        }else{
            if(mIsShow){
                viewHolder.tvOnLine.setText("");
                viewHolder.tvOnLine.setVisibility(View.INVISIBLE);
                viewHolder.tvName.setTextColor(Color.argb(120, 120, 100, 100));
                viewHolder.tvName.setTextSize(14);

                viewHolder.image.setImageResource(Constant.getImageId(equipmentBean.getDevice_Type()));
            }else{
                if(equipmentBean.isOnLine()){
                    viewHolder.tvOnLine.setText("");
                    viewHolder.image.setImageResource(Constant.getImageId(equipmentBean.getDevice_Type()));
                }else{
                    viewHolder.tvOnLine.setText("");
                    viewHolder.image.setImageResource(R.drawable.off_line);
                }
            }
        }

        return view;
    }
    private class ViewHolder{
        ImageView image;
        TextView tvName;
        TextView tvOnLine;
    }
}
