package com.mvopo.floatingsms.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mvopo.floatingsms.Interface.ServiceContract;
import com.mvopo.floatingsms.View.FloatingSmsService;
import com.mvopo.floatingsms.View.MainActivity;

/**
 * Created by mvopo on 5/8/2018.
 */

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

//                    Log.e("SmsReceiver", phoneNumber);

//                    Intent serviceIntent = new Intent(context, FloatingSmsService.class);
//                    serviceIntent.putExtra("phoneNum", phoneNumber);
//                    context.startService(serviceIntent);

                    Intent foregroundIntent = new Intent(ServiceContract.ACTION_FOREGROUND);
                    foregroundIntent.putExtra("phoneNum", phoneNumber);
                    foregroundIntent.setClass(context, FloatingSmsService.class);
                    context.startService(foregroundIntent);
                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}