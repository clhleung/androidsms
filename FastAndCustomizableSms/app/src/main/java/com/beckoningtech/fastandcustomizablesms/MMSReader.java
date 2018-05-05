package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashSet;

/**
 * Gets numbers from an MMS.
 */
public class MMSReader {
    /**
     * get mms from id and context
     *
     * @param id MMSID
     * @param context any context
     * @return a HashSet of strings that contains the numbers of everyone involved in the sms
     */
    public static HashSet<String> getMMSSenderNumberArray(String id, Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mms/"+id+"/addr"),
                null, null, null, null);
        HashSet<String> numberSet = new HashSet<>();
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    numberSet.add(AllContacts.stripNumber(cursor.getString(
                            cursor.getColumnIndex("address"))));
                } while(cursor.moveToNext());
            }
        }
        return numberSet;
    }

}
