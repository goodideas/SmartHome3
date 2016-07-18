package com.zuobiao.smarthome.smarthome3.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.entity.SceneNameBean;

import java.util.List;

/**
 * Author Kevin
 * Time 2016/2/29.
 */
public class SceneAdapter extends BaseAdapter{

    private Context mContext;
    private List<SceneNameBean> mList;
    private SpHelper spHelper;
    private static final String TAG = "SceneAdapter";

    private int[] bg = {R.drawable.list_view_item_bg_green,R.drawable.list_view_item_bg_yellow,R.drawable.list_view_item_bg_red};

    public  SceneAdapter(Context context,List<SceneNameBean> list){
        mContext = context;
        mList = list;
        spHelper = new SpHelper(context);
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
        SceneNameBean sceneNameBean = mList.get(position);
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.scene_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvSceneItem = (TextView)view.findViewById(R.id.tvSceneItem);
            viewHolder.rl_listview_item = (RelativeLayout)view.findViewById(R.id.rl_listview_item);
            viewHolder.isUseCheck = (CheckBox)view.findViewById(R.id.isUseCheck);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        if(spHelper.getSpIsUseNumber() == position ){
//            Log.e(TAG,"spHelper.getSpIsUseNumber()="+spHelper.getSpIsUseNumber()+" position="+position);
            viewHolder.isUseCheck.setVisibility(View.VISIBLE);
            viewHolder.isUseCheck.setChecked(true);
        }else{
            viewHolder.isUseCheck.setVisibility(View.GONE);
            viewHolder.isUseCheck.setChecked(false);
        }

        viewHolder.rl_listview_item.setBackgroundResource(bg[position%3]);
        viewHolder.tvSceneItem.setText(sceneNameBean.getSceneName());
        viewHolder.tvSceneItem.setTextColor(Color.WHITE);

        return view;
    }


    private class ViewHolder{
        RelativeLayout rl_listview_item;
        TextView tvSceneItem;
        CheckBox isUseCheck;
    }
}
