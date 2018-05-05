package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import android.util.Log;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * QuickTextContainer is a wrapper class for storing and pulling quick texts.
 * Usage: Simply instantiate the QuickTextContainer with a context and a threadID. Then,
 * simply call getQuickTextItems() to get all quick texts for that thread ID.
 *
 * Created by Ric Rodriguez and William Jung on 11/4/17.
 */

public class QuickTextContainer {
    private static final String PREFS_NAME = "QuickTextFile";
    private ArrayList<QuickTextItem> quickTextItems;
    private String threadId;
    private String conversationPhoneNumber;
    private SharedPreferences sharedPreferences;

    /**
     * Constructor for QuickTextContainer
     * @param context  Context to get SharedPreferences.
     * @param threadId thread id we want to get quick texts for.
     */
    public QuickTextContainer(Context context, String threadId) {
        this.threadId = threadId;
        this.sharedPreferences = context.getSharedPreferences(
                "QuickTextPreferences"+threadId, MODE_PRIVATE);
        //clearprefs();

        quickTextItems = new ArrayList<>();
        if (!sharedPreferences.contains("header")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("header", "version1");
            editor.apply();
            generateDefaultQuickTexts();
            saveQuickTextData();
        } else {
            getAllQuickTextData();
        }
    }

    private void getAllQuickTextData(){
        int numQuickTexts = sharedPreferences.getInt("num_quick_texts",0);
        for(int position = 0; position < numQuickTexts; position++){
            quickTextItems.add(new QuickTextItem(sharedPreferences, position));
        }
    }

    private void clearprefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Adds this quicktextitem to the arraylist
     *
     * @param quickTextItem quicktextitem to be added
     */
    public void addNewQuickText(QuickTextItem quickTextItem) {
        this.quickTextItems.add(quickTextItem);
    }

    /**
     * Saves all quick text data
     */
    public void saveQuickTextData(){
        saveNumQuickTexts();
    }

    /**
     * Generates default quick texts for new text threads.
     */
    private void generateDefaultQuickTexts() {

      final String message[] = {"Hello! Quick text here",
              "On my way \uD83C\uDF46\uD83D\uDCA6\uD83D\uDCA6!", "Please try again later"};

      final String tag = "Sample";
        int messageCounter = 0;
        this.quickTextItems = new ArrayList<>();
        for (String msg : message) {
            QuickTextItem quickTextItem =
                    new QuickTextItem(messageCounter, new ArrayList<String>(), 0, msg,
                            null, null, tag + messageCounter,
                            messageCounter % 2 == 0, true,
                            "Hi!", sharedPreferences);
            this.quickTextItems.add(quickTextItem);
            messageCounter++;
        }
    }


    /** Retrieves bitmaps from each quickTextItem of this conversation
     * @return Set of bitmaps representing each individual quickTextItem
     */
    public ArrayList<Bitmap> getQuickTextItemRepresentations() {
        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        //TODO: Get bitmaps from files.
        //        for (QuickTextItem quickTextItem : this.quickTextItems) {
//            bitmapArrayList.add(quickTextItem.getBitmap());
//        }
        return bitmapArrayList;
    }

    /**
     * removes quicktext given by position from recyclerview
     *
     * @param position int from getPosition in recycler adapter
     */
    public void remove(int position) {
        for(int i = position; i < quickTextItems.size()-1; i++){
            QuickTextItem quickTextItem = quickTextItems.get(i);
            quickTextItem.copy(quickTextItems.get(i+1), sharedPreferences);
        }
        quickTextItems.remove(position);
        saveNumQuickTexts();
    }

    /** returns array of quicktext items, iterable for easy display
     * @return ArrayList of QuickTextItems which correspond to this thread_id
     */
    public ArrayList<QuickTextItem> getQuickTextItems() {
        return this.quickTextItems;
    }


    private void saveNumQuickTexts(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("num_quick_texts", quickTextItems.size());
        editor.apply();
    }

    /**
     * Sends message for this quick text
     *
     * @param position        position of quick text
     * @param context         Context to send message from
     * @param  recipientArray  String array of phone numbers to send to
     */
    public void sendSMSOrMMS(int position, Context context, String[] recipientArray) {
        QuickTextItem quickTextItem = quickTextItems.get(position);
        String messageBody = quickTextItem.getMessageBody();
//        if (!sendOnPress) {
//            sendMessageEditText.setText(messageBody);
//            return;
//        }

//        if (quickTextItem.isAppendLocation()) {
//            messageBody += "\nSent from " + getLocation();
//        }
        com.klinker.android.send_message.Settings settings = new
                com.klinker.android.send_message.Settings();
        settings.setUseSystemSending(true);
        Transaction transaction = new Transaction(context, settings);
        Message message = new Message(messageBody, recipientArray);
        transaction.sendNewMessage(message, Long.parseLong(threadId));

    }
}
