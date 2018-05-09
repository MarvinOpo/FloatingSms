package com.mvopo.floatingsms.Presenter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvopo.floatingsms.Interface.ServiceContract;
import com.mvopo.floatingsms.Model.Message;
import com.mvopo.floatingsms.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mvopo on 5/7/2018.
 */

public class ServicePresenter implements ServiceContract.serviceAction {

    ServiceContract.serviceView serviceView;

    public ServicePresenter(ServiceContract.serviceView seviceView) {
        this.serviceView = seviceView;
    }

    @Override
    public void setBubbleOnTouch(final View view) {
        view.findViewById(R.id.profile_image).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = serviceView.getParamX();
                        initialY = serviceView.getParamY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff < 10 && Ydiff < 10) {
                            int x = serviceView.getParamX();
                            int y = serviceView.getParamY();

                            serviceView.removeFloatingWindow();

                            if (view.findViewById(R.id.message_container).getVisibility() == View.GONE) {
                                serviceView.toggleMessageContainer(View.VISIBLE);
                                serviceView.initWindowFlagForKeyboard(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, x, y);
                                serviceView.displayMessages();
                                markMessageAsRead();
                            } else {
                                serviceView.toggleMessageContainer(View.GONE);
                                serviceView.initWindowFlagForKeyboard(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, x, y);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x = initialX + (int) (event.getRawX() - initialTouchX);
                        int y = initialY + (int) (event.getRawY() - initialTouchY);

                        serviceView.setParamXY(x, y);
                        serviceView.updateFloatingViewLayout();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void checkIntentAction(Intent intent) {
        if (intent.getAction().equals(ServiceContract.ACTION_FOREGROUND)) {
            serviceView.showNotification();
            serviceView.displayMessages();
        } else if (intent.getAction().equals(ServiceContract.ACTION_STOP)) {
            serviceView.stopService();
        }
    }

    @Override
    public ArrayList<Message> getMessages(String phoneNum) {
        ArrayList<Message> messages = new ArrayList<>();

        Cursor cur = serviceView.getMessageCursor(phoneNum);
        if (cur.moveToFirst()) {
            int index_Address = cur.getColumnIndex("address");
            int index_Body = cur.getColumnIndex("body");
            int index_Type = cur.getColumnIndex("type");
            int index_Person = cur.getColumnIndex("person");
            int index_Read = cur.getColumnIndex("read");
            do {
                String strAddress = cur.getString(index_Address);
                String strbody = cur.getString(index_Body);
                String strPerson = cur.getString(index_Person);
                String strRead = cur.getString(index_Read);
                int int_Type = cur.getInt(index_Type);

                String type = "in";
                if(int_Type == 2) type = "out";

                messages.add(new Message(strAddress, strbody, type, strPerson, strRead));
            }while (cur.moveToNext());
        }

        cur.close();
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public void setSendClickAction(ImageView btnSend) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    public void setCloseClickAction(TextView btnClose) {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceView.stopService();
            }
        });
    }

    @Override
    public void sendMessage() {
        String message = serviceView.getComposeMessage();
        String phoneNum = serviceView.getPhoneNum();

        if(!message.isEmpty()){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message, null, null);

            serviceView.setComposeMessage("");
            serviceView.addMessageItem(new Message(phoneNum, message, "out", "", ""));
            serviceView.refreshAdapter();
        }
    }

    @Override
    public void markMessageAsRead() {
        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("content://sms/inbox");
                String selection = "address = ? AND read = ?";
                String[] selectionArgs = {serviceView.getPhoneNum(), "0"};

                ContentValues values = new ContentValues();
                values.put("read", true);

                int rowsUpdated = serviceView.getResolver().update(uri, values, selection, selectionArgs);
                Log.i("TAGGG", "rows updated: " + rowsUpdated);
            }
        });
        waiter.start();
    }
}
