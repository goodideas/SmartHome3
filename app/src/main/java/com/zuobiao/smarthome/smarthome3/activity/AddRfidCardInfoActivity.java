package com.zuobiao.smarthome.smarthome3.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.zuobiao.smarthome.smarthome3.R;
import com.zuobiao.smarthome.smarthome3.db.DBcurd;
import com.zuobiao.smarthome.smarthome3.util.Constant;
import com.zuobiao.smarthome.smarthome3.util.Util;

import java.lang.reflect.Field;
import java.util.List;

public class AddRfidCardInfoActivity extends AppCompatActivity {


    private Button btnBack;
    private Button btnAddRfidInfo;
    private ListView rfidListView;

    private DBcurd dBcurd;
    private List<String> list;
    private String rfidInfo;
    private int positioned;
    private ArrayAdapter adapter;

    private static final int SCAN_RFID_GREQUEST_CODE = 0x111;
    private String qrRfidResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rfid_card_info);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnAddRfidInfo = (Button)findViewById(R.id.btnAddRfidInfo);
        rfidListView = (ListView)findViewById(R.id.rfidListView);
        dBcurd = new DBcurd(AddRfidCardInfoActivity.this);
        list = dBcurd.getAllRfidInfo();
        adapter = new ArrayAdapter<>(AddRfidCardInfoActivity.this,android.R.layout.simple_list_item_1,list);
        rfidListView.setAdapter(adapter);
        rfidListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                rfidInfo = (String)parent.getItemAtPosition(position);
                positioned = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(AddRfidCardInfoActivity.this);
                builder.setMessage("删除信息");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(positioned);
                        adapter.notifyDataSetChanged();
                        dBcurd.delRfidInfo(rfidInfo);

                    }
                });
                builder.create();
                builder.show();
                return false;
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddRfidInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddRfidCardInfoActivity.this);
                final EditText etRfid = new EditText(AddRfidCardInfoActivity.this);
                builder.setView(etRfid);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(etRfid.getText().toString())) {
                            Util.showToast(getApplicationContext(), "信息为空！");
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Util.showToast(getApplicationContext(), etRfid.getText().toString());
                            dBcurd.addRfidInfo(etRfid.getText().toString());
                            list = dBcurd.getAllRfidInfo();
                            adapter = new ArrayAdapter<>(AddRfidCardInfoActivity.this, android.R.layout.simple_list_item_1, list);
                            rfidListView.setAdapter(adapter);
//                            dialog.dismiss();
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });
                builder.setCancelable(false);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                builder.setNeutralButton("二维码扫描", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(AddRfidCardInfoActivity.this, MipcaActivityCapture.class);
                        startActivityForResult(intent, SCAN_RFID_GREQUEST_CODE);
                    }
                });
                builder.setMessage("添加RFID卡信息");
                builder.create();
                builder.show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_RFID_GREQUEST_CODE&&resultCode == Constant.QR_RFID_RESULT_CODE){
            qrRfidResult = data.getExtras().getString(Constant.QR_RFID_RESULT_DATA);
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRfidCardInfoActivity.this);
            final EditText etRfid = new EditText(AddRfidCardInfoActivity.this);
            etRfid.setText(qrRfidResult);
            builder.setView(etRfid);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (TextUtils.isEmpty(etRfid.getText().toString())) {
                        Util.showToast(getApplicationContext(), "信息为空！");
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Util.showToast(getApplicationContext(), etRfid.getText().toString());
                        dBcurd.addRfidInfo(etRfid.getText().toString());
                        list = dBcurd.getAllRfidInfo();
                        adapter = new ArrayAdapter<>(AddRfidCardInfoActivity.this, android.R.layout.simple_list_item_1, list);
                        rfidListView.setAdapter(adapter);
//                            dialog.dismiss();
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            builder.setNeutralButton("二维码扫描", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setClass(AddRfidCardInfoActivity.this, MipcaActivityCapture.class);
                    startActivityForResult(intent, SCAN_RFID_GREQUEST_CODE);
                }
            });
            builder.setMessage("添加RFID卡信息");
            builder.create();
            builder.show();
            Log.e("AddRfifCarInfoActivity","qrRfidResult="+qrRfidResult);
        }
    }
}
