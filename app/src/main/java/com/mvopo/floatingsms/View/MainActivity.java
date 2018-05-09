package com.mvopo.floatingsms.View;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mvopo.floatingsms.Helper.ListAdapter;
import com.mvopo.floatingsms.Interface.MainActivityContract;
import com.mvopo.floatingsms.Interface.ServiceContract;
import com.mvopo.floatingsms.Model.Message;
import com.mvopo.floatingsms.Presenter.MainActivityPresenter;
import com.mvopo.floatingsms.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityContract.mainActivityView,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private MainActivityPresenter mPresenter;

    private ListView lvThreads;
    private ArrayList<Message> messageThreads = new ArrayList<>();
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvThreads = findViewById(R.id.main_lv);

        mPresenter = new MainActivityPresenter(this);
        mPresenter.checkPermission();
        mPresenter.setListOnClick(lvThreads);
    }

    @Override
    public void startService(String phoneNum) {
        Intent foregroundIntent = new Intent(ServiceContract.ACTION_FOREGROUND);
        foregroundIntent.putExtra("phoneNum", phoneNum);
        foregroundIntent.setClass(this, FloatingSmsService.class);
        startService(foregroundIntent);
        finish();
    }

    @Override
    public boolean smsAllowed() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean phoneStateAllowed() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean contactAllowed() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean shouldShowPermission() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS);
    }

    @Override
    public void displayMessageThreads() {
        messageThreads.clear();
        messageThreads = mPresenter.getMessageThreads();
        adapter = new ListAdapter(this, R.layout.message_thread_layout, messageThreads);
        lvThreads.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getListSelectedPhoneNum(int position) {
        return messageThreads.get(position).getAddress();
    }

    @Override
    public ContentResolver getResolver() {
        return getContentResolver();
    }

    @Override
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                   displayMessageThreads();
                } else {
                   this.finish();
                }
            }
        }
    }
}
