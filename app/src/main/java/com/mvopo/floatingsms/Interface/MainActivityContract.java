package com.mvopo.floatingsms.Interface;

import android.content.ContentResolver;
import android.widget.ListView;

import com.mvopo.floatingsms.Model.Message;

import java.util.ArrayList;

/**
 * Created by mvopo on 5/7/2018.
 */

public class MainActivityContract {

    public interface mainActivityView{
        void startService(String phoneNum);

        void requestPermission();
        boolean smsAllowed();
        boolean phoneStateAllowed();
        boolean contactAllowed();
        boolean shouldShowPermission();

        void displayMessageThreads();
        void refreshAdapter();

        String getListSelectedPhoneNum(int position);

        ContentResolver getResolver();
    }

    public interface mainActivityAction{
        void checkPermission();

        ArrayList<Message> getMessageThreads();
        String getContactName(String number);

        void setListOnClick(ListView listView);

    }
}
