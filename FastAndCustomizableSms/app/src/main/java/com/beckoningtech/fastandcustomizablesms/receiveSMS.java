package com.beckoningtech.fastandcustomizablesms;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to receive SMS, create notifications for said SMS,  and update the rest of the app
 * about said SMS
 */
public class receiveSMS extends BroadcastReceiver {
    public static String REPLY_ACTION = "com.beckoningtech.fastandcustomizablesms.REPLY_ACTION";
    public static final String ACTION_Button1 = "action_Button1";
    public static final String ACTION_Button2 = "action_Button2";

    private static String KEY_NOTIFICATION_ID = "key_noticiation_id";
    private static String KEY_MESSAGE_ID = "key_message_id";
    public static String msg_from;
    public static String OriginatingAddress;
    public static String msgBody;
    public static long msgTime;
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final String LOG_TAG = "SMSApp";
    public static boolean smsNoti;
    private int mNotificationId;
    private static QuickTextContainer mQuickTextContainer;
    private int mMessageId;
    private static ArrayList<QuickTextItem> quickTextItems;
    private static String thread_id;
    private final static String GROUP_KEY_BUNDLED = "group_key_bundled";
    private LocationGetter mLocationGetter;
    EditText sendMessageEditText;

    //private static List<CharSequence> responseHistory = new LinkedList<>();
    private static List<CharSequence> responseHistory = new ArrayList<>(10);
    private static List<CharSequence> phoneNums = new LinkedList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationId=0;
        mMessageId=12;

        // TODO Auto-generated method stub
        if (!Telephony.Sms.getDefaultSmsPackage(context).equals(context.getPackageName())){
            return;
        }
        /**
         * When reply button is clicked. Send messaage
         */
        if (REPLY_ACTION.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            CharSequence message = getMessageText(intent);

            String msg = message.toString();

            sendSMS(OriginatingAddress,  msg, context);
            inlineReplyNotification(context);

        }
        /**
         * when button1 is clicked
         */
        if (ACTION_Button1.equals(intent.getAction())) {
            CharSequence text = "Button1 is clicked!";
            int duration = Toast.LENGTH_SHORT;

            QuickTextItem quickTextItem = quickTextItems.get(0);
            PhoneUtils.sendSMS(quickTextItem.getMessageBody(), OriginatingAddress, context);

        }
        /**
         * when button2 is clicked
         */
        if (ACTION_Button2.equals(intent.getAction())) {
            CharSequence text = "Button2 is clicked!";
            int duration = Toast.LENGTH_SHORT;

            QuickTextItem quickTextItem = quickTextItems.get(1);

            PhoneUtils.sendSMS(quickTextItem.getMessageBody(), OriginatingAddress, context);

        }

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            // Update the main menu UI by sending a intent to the LocalBroadcastManager
            // TODO will need to implement a way to update the UI when we send a message
            Intent mainMenuUpdateIntent = new Intent("sms-received-event");
            mainMenuUpdateIntent.putExtra("message", "This is my message!");
            LocalBroadcastManager.getInstance(context).sendBroadcast(mainMenuUpdateIntent);

            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs;
            //mLocationGetter = new LocationGetter((Activity)context);
            //mLocationGetter.updateLocation();

            if (bundle != null){
                //---retrieve the SMS message received---

                try{
                    //Object[] pdus = (Object[]) bundle.get("pdus");
                    //msgs = new SmsMessage[pdus.length];
                    msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                    for (SmsMessage msg : msgs) {
                        //msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                        msg_from = msg.getOriginatingAddress();
                        OriginatingAddress = msg.getOriginatingAddress();
                        msgBody = msg.getMessageBody();
                        msgTime = msg.getTimestampMillis();

                        Log.d("recieveSMS:", "got;" + msg_from + ";" + msgBody + ";" + msgTime);
                        ContentValues values = new ContentValues();
                        values.put("address", msg_from);
                        values.put("body", msgBody);
                        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                        //Log.i("SmsReceiver", "senderNum: "+ msg_from + "; message: " + msgBody);
                        // Show Alert
                        Log.d("help me", "im about to open");

                        try {
                            Cursor cursor1 = context.getContentResolver().query(Uri.parse("content://sms"),
                                    new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"},
                                    "address=" + AllContacts.stripNumber(OriginatingAddress), null, null);
                            Log.d("help me", "im openeing");

                            if (cursor1 != null && cursor1.getCount() > 0) {
                                try {
                                    cursor1.moveToNext();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.d("help me", "im starving");
                                thread_id = cursor1.getString(cursor1.getColumnIndex("thread_id"));
                            } else {
                                cursor1 = context.getContentResolver().query(Uri.parse("content://sms"),
                                        new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"},
                                        "address=" + (OriginatingAddress), null, null);
                                Log.d("help me", "im folding");

                                if (cursor1 != null && cursor1.getCount() > 0) {
                                    try {
                                        cursor1.moveToNext();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("help me", "im starving");
                                    thread_id = cursor1.getString(cursor1.getColumnIndex("thread_id"));
                                }
                            }
                            cursor1.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.d("FOUND THREAD ID TO BE:", thread_id);
                        QuickTextContainer quickTextContainer = new QuickTextContainer(context, thread_id);
                        quickTextItems = quickTextContainer.getQuickTextItems();
                        Log.d("quicktexct", quickTextItems.size() + ";");
                        Log.d("quicktexct", OriginatingAddress + ";");
                        Log.d("quicktexct", PhoneNumberUtils.isWellFormedSmsAddress(OriginatingAddress) + ";");
                        Log.d("quicktexct", quickTextItems.get(0).getMessageBody() + ";");

                        Log.d("receiveSMS", "senderNum: " + OriginatingAddress + ", message: " + msgBody);
                        Log.d("receiveSMS", "current MessagesActivity number: " +
                                MessagesActivity.getPhoneNum());
                        MessagesActivityMessageAdder messageAdder =
                                MessagesActivity.getMessageAdder();
                        if (messageAdder != null) {
                            if (AllContacts.stripNumber(msg_from).equals(
                                    AllContacts.stripNumber(MessagesActivity.getPhoneNum()))) {
                                Log.d("receiveSMS", "Calling addMsg");
                                messageAdder.addMsg(AllContacts.stripNumber(msg_from), msgBody);
                                return;
                            }
                        }
                        /**
                         * looks up contacts name
                         */
                        Uri uri;
                        String[] projection;
                        // If targeting Donut or below, use
                        // Contacts.Phones.CONTENT_FILTER_URL and
                        // Contacts.Phones.DISPLAY_NAME
                        uri = Uri.withAppendedPath(
                                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                Uri.encode(msg_from));
                        projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
                        // Query the filter URI
                        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst())
                                msg_from = cursor.getString(0);
                            cursor.close();
                        }
                        //abortBroadcast();
                        if (intent.getAction().equals("android.provider.Telephony.SEND_SMS")) {
                            Toast toast = Toast.makeText(context, "SMS SENT: " + intent.getAction(), Toast.LENGTH_LONG);
                            toast.show();
                            abortBroadcast();
                        }


                        responseHistory.add(0,msg_from + ": " + msgBody);
                        phoneNums.add(msg_from);
                        /**
                         * clear array list of responses when received text from different recipient.
                         */
                        inlineReplyBuilder(context, mNotificationId);
                        for(int i=0; i <phoneNums.size(); i++){

                            if (!phoneNums.get(i+1).equals(phoneNums.get(i))) {
                                phoneNums.clear();
                                responseHistory.clear();
                            }else {
                                // inlineReplyNotification(context);
                            }
                        }
                    }

                }catch(Exception e){
                }

            }

        }

    }

    /**
     *
     * @param context
     * @param notificationId
     * @param messageId
     * @return Intent for reply button action
     */
    public static Intent getReplyMessageIntent(Context context, int notificationId, String messageId) {
        Intent intent = new Intent(context, receiveSMS.class);
        intent.setAction(REPLY_ACTION);
        // intent.setAction(ACTION_DONE);
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        return intent;
    }

    /**
     *
     * @param context
     * @param notificationId
     * @param messageId
     * @return intent for button1
     */
    public static Intent getButton1Intent(Context context, int notificationId, String messageId) {
        Intent intent = new Intent(context, receiveSMS.class);
        intent.setAction(ACTION_Button1);
        return intent;
    }

    /**
     *
     * @param context
     * @param notificationId
     * @param messageId
     * @return intent for button2
     */
    public static Intent getButton2Intent(Context context, int notificationId, String messageId) {
        Intent intent = new Intent(context, receiveSMS.class);
        intent.setAction(ACTION_Button2);
        return intent;
    }

    /**
     *
     * @param intent
     * @return this return the input characters in the remoteinput for reply action
     */
    public static CharSequence getMessageText(Intent intent) {
        CharSequence reply = null;
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence(KEY_TEXT_REPLY);
            responseHistory.add(0, "You: " + reply);
            return reply;
        }

        return null;
    }

    /**
     *
     * @param phoneNumber
     * @param message
     * @param context
     * Sends and saves text message to database
     */
    private void sendSMS(String phoneNumber, String message, Context context) {
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", message);
        context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);

        SmsManager sms = SmsManager.getDefault();
        Log.d("SENDSMS:", phoneNumber + ";" + message);
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    /**
     *
     * @param context
     * @param mNotificationId
     * @return this returns a second notification when message text is send
     */
    private static NotificationCompat.Builder inlineReplyBuilder2( Context context,  int mNotificationId) {
        Intent replyIntent = getReplyMessageIntent(context, mNotificationId, msg_from);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent button1Intent = getButton1Intent(context, mNotificationId, msg_from);
        PendingIntent pendingIntent_button1 = PendingIntent.getBroadcast(context, 0,
                button1Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent button2Intent = getButton2Intent(context, mNotificationId, msg_from);
        PendingIntent pendingIntent_button2 = PendingIntent.getBroadcast(context, 0,
                button2Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Type message")
                .build();

        NotificationCompat.Action replyaction =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_get, "Respond", pendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();
        /**
         * go to message list activity when noti is clicked
         */
        Log.d("what the fucl", "fucking");
        Intent resultIntent = new Intent(context, MessagesActivity.class);
        resultIntent.putExtra("PHONE", msg_from);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.addAction(R.drawable.bubble_green, "BUTTON 1",pendingIntent_button1);
        mBuilder.addAction(R.drawable.bubble_green, "BUTTON 2",pendingIntent_button2);
        mBuilder.addAction(replyaction);
        mBuilder.setSmallIcon(R.drawable.ic_sms);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setColor(ContextCompat.getColor(context,
                R.color.colorPrimary));
        mBuilder.setGroupSummary(true);
        mBuilder.setGroup(GROUP_KEY_BUNDLED);
        //  mBuilder.setOngoing(true);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;


        NotificationManager notifManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //  mBuilder.setOngoing(true);
        notifManager.notify(mNotificationId, note);

        return mBuilder;
    }

    /**
     * This is a inline notification with response history.
     * @param context
     */
    public static void inlineReplyNotification( Context context) {
        //Context context = NougatNotificationApplication.context();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder =inlineReplyBuilder2(context,0);
        CharSequence[] history = new CharSequence[responseHistory.size()];


        builder.setRemoteInputHistory(responseHistory.toArray(history));

        notificationManager.notify(0, builder.build());
    }

    /**
     *
     * @param context
     * @param mNotificationId
     * @return This return a notification builder. This is the main notification builder when message text is received.
     */
    private static NotificationCompat.Builder inlineReplyBuilder( Context context,  int mNotificationId) {
        Intent replyIntent = getReplyMessageIntent(context, mNotificationId, msg_from);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent button1Intent = getButton1Intent(context, mNotificationId, msg_from);
        PendingIntent pendingIntent_button1 = PendingIntent.getBroadcast(context, 0,
                button1Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent button2Intent = getButton2Intent(context, mNotificationId, msg_from);
        PendingIntent pendingIntent_button2 = PendingIntent.getBroadcast(context, 0,
                button2Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Type message")
                .build();

        NotificationCompat.Action replyaction =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_get, "Respond", pendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();
        /**
         * go to message list activity when noti is clicked
         */
        Log.d("what the shit", "shitter");
        Intent resultIntent = new Intent(context, MessagesActivity.class);
        resultIntent.putExtra("PHONE", msg_from);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        QuickTextContainer quickTextContainer = new QuickTextContainer(context, thread_id);

        // Context context = NougatNotificationApplication.context();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context);
        mBuilder.setAutoCancel(true);
        mQuickTextContainer = quickTextContainer;
        mBuilder.addAction(R.drawable.bubble_green, quickTextItems.get(0).getQuickTextTag(), pendingIntent_button1);
        mBuilder.addAction(R.drawable.bubble_green, quickTextItems.get(1).getQuickTextTag(), pendingIntent_button2);
        mBuilder.addAction(replyaction);
        mBuilder.setContentTitle(msg_from);
        mBuilder.setContentText(msgBody);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_sms);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msgBody));
        mBuilder.setColor(ContextCompat.getColor(context,
                R.color.colorPrimary));

        //  mBuilder.setOngoing(true);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;


        NotificationManager notifManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //  mBuilder.setOngoing(true);
        notifManager.notify(mNotificationId, note);

        return mBuilder;
    }

}
