package com.mvopo.floatingsms.Presenter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mvopo.floatingsms.Interface.MainActivityContract;
import com.mvopo.floatingsms.Model.Message;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mvopo on 5/8/2018.
 */

public class MainActivityPresenter implements MainActivityContract.mainActivityAction {

    MainActivityContract.mainActivityView mainActivityView;

    public MainActivityPresenter(MainActivityContract.mainActivityView mainActivityView) {
        this.mainActivityView = mainActivityView;
    }

    @Override
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!mainActivityView.smsAllowed() && !mainActivityView.phoneStateAllowed() && !mainActivityView.contactAllowed()) {
                if(mainActivityView.shouldShowPermission()) {

                }else{
                    mainActivityView.requestPermission();
                }

                return;
            }

        }

        mainActivityView.displayMessageThreads();
//        mainActivityView.startService();
    }

    @Override
    public ArrayList<Message> getMessageThreads() {
        ArrayList<Message> messageThreads = new ArrayList<>();
        ContentResolver contentResolver = mainActivityView.getResolver();
        final String[] projection = new String[]{"*"};
        Uri uri = Uri.parse("content://mms-sms/conversations/");
        Cursor cur = contentResolver.query(uri, projection, null, null, "date asc");

        if (cur.moveToFirst()) {
            int index_Address = cur.getColumnIndex("address");
            int index_Body = cur.getColumnIndex("body");
            int index_Type = cur.getColumnIndex("type");
            int index_Read = cur.getColumnIndex("read");
            do {
                String strAddress = cur.getString(index_Address);
                String strbody = cur.getString(index_Body);
                String strRead = cur.getString(index_Read);
                String strPerson = getContactName(strAddress);
                int int_Type = cur.getInt(index_Type);

                String type = "in";
                if(int_Type == 2) type = "out";

                if(strPerson == null) strPerson = "";

                messageThreads.add(new Message(strAddress, strbody, type, strPerson, strRead));
            }while (cur.moveToNext());
        }

        cur.close();
        Collections.reverse(messageThreads);
        return messageThreads;
    }

    @Override
    public String getContactName(String phoneNumber) {
        ContentResolver cr = mainActivityView.getResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    @Override
    public void setListOnClick(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String phoneNum = mainActivityView.getListSelectedPhoneNum(i);
                mainActivityView.startService(phoneNum);
            }
        });
    }
}
