package com.beckoningtech.fastandcustomizablesms;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;

import com.android.i18n.phonenumbers.PhoneNumberUtil;

/**
 * Global utilities for standardizing phone number formatting
 * @author Ric Rodriguez
 */

/**
 * Contains a static function to send SMS.
 */
public class PhoneUtils {
    /**
     * Sends an SMS with the given parameters.
     * @param messageBody Body of sms message to be send
     * @param phoneNumber Phone number formatted without leading 1
     * @param context Context for accessing the database
     */
    public static void sendSMS(String messageBody, String phoneNumber, Context context){
        Log.d("PHONEUTILS:", phoneNumber);
        if(!PhoneNumberUtils.isWellFormedSmsAddress(phoneNumber)){
            phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, "US");
        }
        Log.d("PHONEUTILS:", phoneNumber);
        SmsManager sms= SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, messageBody, null, null);

        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", messageBody);
        context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }
}
