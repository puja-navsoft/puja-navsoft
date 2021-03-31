package com.afieat.ini.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

public class OtpReceiver extends BroadcastReceiver {

    private static EditText editText;

    public void setEditText (EditText editText){
        OtpReceiver.editText = editText;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);


            for (SmsMessage sms : messages){
                String message = sms.getMessageBody();
                String otp = message.split(": ")[1];
                editText.setText(otp);
            }


        }
    }
}
