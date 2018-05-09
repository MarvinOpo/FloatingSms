package com.mvopo.floatingsms.Interface;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvopo.floatingsms.Model.Message;

import java.util.ArrayList;

/**
 * Created by mvopo on 5/7/2018.
 */

public class ServiceContract {

    public static String ACTION_FOREGROUND = "com.mvopo.floatingsms.FloatingSmsService.SERVICE_FOREGROUND";
    public static String ACTION_STOP = "com.mvopo.floatingsms.FloatingSmsService.SERVICE_STOP";

    public interface serviceView{
        void toggleMessageContainer(int visibility);
        void initWindowFlagForKeyboard(int flag, int x, int y);

        void addFloatingWindow();
        void removeFloatingWindow();
        void updateFloatingViewLayout();

        void setParamXY(int x, int y);
        int getParamX();
        int getParamY();

        void addMessageItem(Message message);
        void refreshAdapter();

        void setComposeMessage(String text);
        String getComposeMessage();

        String getPhoneNum();
        Cursor getMessageCursor(String number);

        void displayMessages();
        void stopService();
        void showNotification();
        ContentResolver getResolver();
    }

    public interface serviceAction{
        void setBubbleOnTouch(View view);
        void checkIntentAction(Intent intent);
        ArrayList<Message> getMessages(String phoneNum);

        void setSendClickAction(ImageView btnSend);
        void setCloseClickAction(TextView btnClose);

        void sendMessage();
        void markMessageAsRead();
    }
}
