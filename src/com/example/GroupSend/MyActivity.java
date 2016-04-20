package com.example.GroupSend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class MyActivity extends Activity {
    EditText numbers,content;
    Button select,send;
    SmsManager smsManager;
    ArrayList<String> sendList=new ArrayList<String>();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        smsManager=SmsManager.getDefault();
        numbers=(EditText)findViewById(R.id.numbers);
        content=(EditText)findViewById(R.id.content);
        select=(Button)findViewById(R.id.select);
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String number:sendList)
                {
                    PendingIntent pi=PendingIntent.getActivity(MyActivity.this,0,new Intent(),0);
                    smsManager.sendTextMessage(number,null,content.getText().toString(),pi,null);
                }
                Toast.makeText(MyActivity.this,"短信群发完成",Toast.LENGTH_SHORT).show();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                BaseAdapter adapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return cursor.getCount();
                    }

                    @Override
                    public Object getItem(int i) {
                        return i;
                    }

                    @Override
                    public long getItemId(int i) {
                        return i;
                    }

                    @Override
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        cursor.moveToPosition(i);
                        CheckBox rb = new CheckBox(MyActivity.this);
                        String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                .replace("-", "")
                                .replace("", "");
                        rb.setText(number);
                        if (isChecked(number)) {
                            rb.setChecked(true);
                        }
                        return rb;
                    }
                };
                View selectView = getLayoutInflater().inflate(R.layout.list, null);
                final ListView listView = (ListView) selectView.findViewById(R.id.list);
                listView.setAdapter(adapter);
                new AlertDialog.Builder(MyActivity.this).setView(selectView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendList.clear();
                        for (int i2 = 0; i2 < listView.getCount(); i2++) {
                            CheckBox checkBox = (CheckBox) listView.getChildAt(i2);
                            if (checkBox.isChecked()) {
                                sendList.add(checkBox.getText().toString());
                            }
                        }
                        numbers.setText(sendList.toString());
                    }
                }).show();
            }
        });
    }
    public boolean isChecked(String phone) {
        for (String s1 : sendList) {
            if (s1.equals(phone)) {
                return true;
            }
        }
        return false;
    }

}
