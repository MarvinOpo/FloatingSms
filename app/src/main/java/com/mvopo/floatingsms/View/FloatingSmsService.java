package com.mvopo.floatingsms.View;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mvopo.floatingsms.Helper.ListAdapter;
import com.mvopo.floatingsms.Interface.ServiceContract;
import com.mvopo.floatingsms.Model.Message;
import com.mvopo.floatingsms.Presenter.ServicePresenter;
import com.mvopo.floatingsms.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mvopo on 5/7/2018.
 */

public class FloatingSmsService extends Service implements ServiceContract.serviceView {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;

    private View mFloatingWindow;

    private CircleImageView civBubble;
    private LinearLayout message_container;

    private EditText txtMessage;
    private ImageView btnSend;
    private ListView lvMessages;
    private TextView btnClose;

    private ServicePresenter mPresenter;

    private ArrayList<Message> messages = new ArrayList<>();
    private ListAdapter adapter;

    private String phoneNum = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPresenter = new ServicePresenter(this);

        mFloatingWindow = LayoutInflater.from(this).inflate(R.layout.floating_window_layout, null);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        initWindowFlagForKeyboard(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, 0, 100);

        civBubble = mFloatingWindow.findViewById(R.id.profile_image);
        message_container = mFloatingWindow.findViewById(R.id.message_container);
        txtMessage = mFloatingWindow.findViewById(R.id.message_txt);
        btnSend = mFloatingWindow.findViewById(R.id.message_send);
        lvMessages = mFloatingWindow.findViewById(R.id.messages_lv);
        btnClose = mFloatingWindow.findViewById(R.id.floating_view_close);

        mPresenter.setBubbleOnTouch(mFloatingWindow);
        mPresenter.setSendClickAction(btnSend);
        mPresenter.setCloseClickAction(btnClose);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        phoneNum = intent.getExtras().getString("phoneNum");
        mPresenter.checkIntentAction(intent);

        return START_STICKY;
    }

    @Override
    public void toggleMessageContainer(int visibility) {
        message_container.setVisibility(visibility);
    }

    @Override
    public void initWindowFlagForKeyboard(int flag, int x, int y) {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                flag,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = x;
        params.y = y;

        addFloatingWindow();
    }

    @Override
    public void addFloatingWindow() {
        mWindowManager.addView(mFloatingWindow, params);
    }

    @Override
    public void removeFloatingWindow() {
        try {
            mWindowManager.removeViewImmediate(mFloatingWindow);
        } catch (Exception e) {
        }
    }

    @Override
    public void updateFloatingViewLayout() {
        mWindowManager.updateViewLayout(mFloatingWindow, params);
    }

    @Override
    public void setParamXY(int x, int y) {
        params.x = x;
        params.y = y;
    }

    @Override
    public int getParamX() {
        return params.x;
    }

    @Override
    public int getParamY() {
        return params.y;
    }

    @Override
    public void addMessageItem(Message message) {
        messages.add(message);
    }

    @Override
    public void refreshAdapter() {
        adapter.notifyDataSetChanged();
        lvMessages.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void setComposeMessage(String text) {
        txtMessage.setText(text);
    }

    @Override
    public String getComposeMessage() {
        return txtMessage.getText().toString().trim();
    }

    @Override
    public String getPhoneNum() {
        return phoneNum;
    }

    @Override
    public Cursor getMessageCursor(String number) {
        Uri uri = Uri.parse("content://sms/");
        String[] projection = new String[]{"_id", "address", "body", "type", "person", "read"};
        Cursor cur = getContentResolver().query(uri, projection, "address=? and (type=? or type=?)", new String[]{number, "1", "2"}, "date desc limit 10");

        return cur;
    }

    @Override
    public void displayMessages() {
        messages.clear();
        messages = mPresenter.getMessages(phoneNum);
        adapter = new ListAdapter(this, R.layout.message_in_layout, messages);
        lvMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lvMessages.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void stopService() {
        removeFloatingWindow();
        stopForeground(true);
        stopSelf();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showNotification() {
        Intent stopSelf = new Intent(this, FloatingSmsService.class);
        stopSelf.putExtra("phoneNum", phoneNum);
        stopSelf.setAction(ServiceContract.ACTION_STOP);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Floating Message")
                .setContentText("Tap to stop application")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pStopSelf)
                .setOngoing(true)
                .build();

        startForeground(100, notification);
    }

    @Override
    public ContentResolver getResolver() {
        return getContentResolver();
    }
}
