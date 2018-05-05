package com.beckoningtech.fastandcustomizablesms;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Class for holding quicktext information. Should be used in conjunction with quicktextContainer
 * @see QuickTextContainer
 * @author Ric Rodriguez
 */
public class QuickTextItem{
    private String messageBody;
    private int delayTime;
    private String sendAtTime;
    private ArrayList<String> recipients;
    private String imageFile;
    private boolean showImage;
    private String quickTextTag;
    private boolean sendOnPress;
    private boolean appendLocation;
    private String header;
    private int position;

    /**
     * QuickTextItem Constructor for manual entry
     *
     * @param position              position to display as
     * @param recipients            recipients of text ";" delimited
     * @param delayTime             time in seconds to delay the text
     * @param messageBody           body of SMS message
     * @param sendAtTime            time to send message at
     * @param file                  imageFile of image to represent quickText
     * @param quickTextTag          short tag to refer
     * @param sendOnPress           should the message be loaded in or send immediately
     * @param sharedPreferences     SharedPreferences to save the quick text in.
     */
    public QuickTextItem(int position, ArrayList<String> recipients, int delayTime,
                         String messageBody, String sendAtTime,
                         String file, String quickTextTag, boolean sendOnPress,
                         boolean appendLocation, String header, SharedPreferences sharedPreferences
    ) {
        this.quickTextTag = quickTextTag;
        this.messageBody= messageBody;
        this.delayTime = delayTime;
        this.recipients = recipients;

        this.sendAtTime = sendAtTime;
        this.imageFile = file;
        this.sendOnPress = sendOnPress;
        this.appendLocation = appendLocation;
        this.header = header;
        this.position = position;
        storeInSharedPreference(sharedPreferences);
    }

    /**
     * QuickTextItem Constructor for testing purposes. Does not get stored in SharedPreferences.
     *
     * @param position              position to display as
     * @param recipients            recipients of text ";" delimited
     * @param delayTime             time in seconds to delay the text
     * @param messageBody           body of SMS message
     * @param sendAtTime            time to send message at
     * @param file                  imageFile of image to represent quickText
     * @param quickTextTag          short tag to refer
     * @param sendOnPress           should the message be loaded in or send immediately
     */
    public QuickTextItem(int position, ArrayList<String> recipients, int delayTime,
                         String messageBody, String sendAtTime,
                         String file, String quickTextTag, boolean sendOnPress,
                         boolean appendLocation, String header
    ) {
        this.quickTextTag = quickTextTag;
        this.messageBody= messageBody;
        this.delayTime = delayTime;
        this.recipients = recipients;

        this.sendAtTime = sendAtTime;
        this.imageFile = file;
        this.sendOnPress = sendOnPress;
        this.appendLocation = appendLocation;
        this.header = header;
        this.position = position;
    }


    /**
     * Copies all info, excluding position, from another QuickTextItem.
     * @param quickTextItem         QuickTextItem to be copied, except for position.
     * @param sharedPreferences     SharedPreferences to save the quick text in.
     */
    public void copy(QuickTextItem quickTextItem, SharedPreferences sharedPreferences){
        this.quickTextTag = quickTextItem.quickTextTag;
        this.messageBody= quickTextItem.messageBody;
        this.delayTime = quickTextItem.delayTime;
        this.recipients = quickTextItem.recipients;

        this.sendAtTime = quickTextItem.sendAtTime;
        this.imageFile = quickTextItem.imageFile;
        this.sendOnPress = quickTextItem.sendOnPress;
        this.appendLocation = quickTextItem.appendLocation;
        this.header = quickTextItem.header;
        this.position = quickTextItem.position;
    }


    public void storeInSharedPreference(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title"+position,quickTextTag);
        editor.putString("message_body"+position, messageBody);
        editor.putInt("delay_time"+position, delayTime);
        editor.putInt("num_recipients"+position, recipients.size());
        for(int i = 0; i < recipients.size(); i++){
            editor.putString("recipient"+position+"number"+i, recipients.get(i));
        }
        editor.putString("send_at_time"+position, sendAtTime);
        editor.putString("image_file"+position, imageFile);
        editor.putBoolean("send_on_press"+position, sendOnPress);
        editor.putBoolean("append_location"+position, appendLocation);
        editor.putString("header"+position, header);
        editor.apply();
    }

    /**
     * QuickTextContructor for quickly loading in database quicktexts
     * @param sharedPreferences SharedPreferences containing the quick text
     * @param position          position the quick text item will be displayed in
     */
    public QuickTextItem(SharedPreferences sharedPreferences, int position){
        this.position = position;
        quickTextTag = sharedPreferences.getString("title"+position, "title"+position);
        messageBody = sharedPreferences.getString("message_body"+position, "");
        delayTime = sharedPreferences.getInt("delay_time"+position,0);
        int numRecipients = sharedPreferences.getInt("num_recipients"+position, 0);
        recipients = new ArrayList<>();
        for(int i = 0; i<numRecipients; i++){
            recipients.add(sharedPreferences.getString(
                    "recipient"+position+"number"+i,""));
        }
        sendAtTime = sharedPreferences.getString("send_at_time"+position, "0");
        imageFile = sharedPreferences.getString("image_file"+position, "");
        sendOnPress = sharedPreferences.getBoolean("send_on_press"+position, false);
        appendLocation = sharedPreferences.getBoolean("append_location"+position, false);
        header = sharedPreferences.getString("header"+position,"");
    }

    public boolean isAppendLocation() {
        return this.appendLocation;
    }


    public void setShowImage(boolean showImage){
        this.showImage = showImage;
    }

    public boolean isShowImage(){
        return (imageFile != null && !imageFile.equals(""));
    }

    public void setQuickTextTag(String quickTextTag){
        this.quickTextTag = quickTextTag;
    }

    public String getQuickTextTag(){
        return this.quickTextTag;
    }

    public void setImageFile(String file){
        this.imageFile = file;
    }

    public void setDelayTime(int delayTime){
        this.delayTime = delayTime;
    }

    public void setMessageBody(String messageBody){
        this.messageBody = messageBody;
    }

    public void setSendAtTime(String sendAtTime){
        this.sendAtTime = sendAtTime;
    }

    public void setRecipients(String recipients){
        this.recipients.clear();
        for(String recipient: recipients.split(",")){
            this.recipients.add(recipient);
        }
    }

    public int getDelayTime(){
        return this.delayTime;
    }

    public String getSendAtTime(){
        return this.sendAtTime;
    }

    /**
     * Compiles the message body with the header and returns it
     * @return the message body
     */
    public String getMessageBody(){
        if(header == null || header.equals("")){
            return messageBody;
        }
        return this.header + "\n" + this.messageBody;//(this.appendLocation?this.location():"");
    }

    public String getImageFile(){
        return this.imageFile;
    }

    /**
     * Gets a bitmap from the image file and returns it. (Not yet implemented).
     * @return Bitmap
     */
    public Bitmap getBitmap(){
        //TODO: Get the bitmap from the image file.
        return  null;
    }

    /**
     * checks if this quicktext should be loaded in or send immediately
     * @return true if is to send immediately
     */
    public boolean isSendOnPress() {
        return this.sendOnPress;
    }

    /**
     * Gets first recipient of the quicktext
     * @return phone number formatted without prepended 1
     */
    public String getRecipient(){
        return this.recipients.get(0);
    }

    public String previewText(){
        return this.messageBody.substring(0, 20);
    }

}