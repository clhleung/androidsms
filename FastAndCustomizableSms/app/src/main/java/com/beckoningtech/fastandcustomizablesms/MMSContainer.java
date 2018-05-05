package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Extension of MessageContainer to store MMS data.
 *
 * Created by jungk on 11/24/2017.
 */

public class MMSContainer extends MessageContainer {

    //private HashSet<String> mNumberSetMMS; // Everyone involved in the conversation
    private String ourPhoneNumber; // Our own phone number
    private String sendersDisplayed; // Sender info that gets displayed on the main menu
    private LinkedList<String> conversationParticipants; // Contains everyone in the convo
    private Resources mResources;
    private boolean containsMedia;
    private ArrayList<Object> media;
    private Bitmap preview;
    private ArrayList<ContactContainer> contactContainers; // Contains every one in the conversation
    private String senderNumber;
    private ContactContainer senderContactContainer;

    // Initialize the MMSContainer
    // Takes in a MMS message _id and a context
    // to get an MMS message's ID,

    /**
     * Initialize the MMSContainer
     * Takes in a MMS message _id and a context
     * to get an MMS message's ID,
     * @param id The message id
     * @param context Any context
     * @param resources Any resources
     */
    MMSContainer(String id, Context context, Resources resources) {
        super(false, "");
        contactContainers = new ArrayList<ContactContainer>();
        media = new ArrayList<Object>();
        mResources = resources;
        // examine the header for information about the senderNumber
        pullHeader(id, context);
        // populate contents of MMSContainer
        pullContents(id, context);

    }

    /**
     * getter for ContactContainers
     *
     * @return ArrayList f contact containers for everyone in the conversation including ourselves
     */
    public ArrayList<ContactContainer> getContactContainers(){
        return contactContainers;
    }

    /**
     * May return null if we never sent an SMS before. This is relevant when we have
     * only sent and received MMS
     *
     * @return Our phone number if we could find it
     */
    public String getOurPhoneNumber() {
        return ourPhoneNumber;
    }

    /**
     * Does not have our name in it
     * @return the list of senders to display on the menu
     */
    public String getSendersDisplayed() {
        return sendersDisplayed;
    }

    /**
     * Getter for conversation participants
     * @return a linked list of all members of the conversation
     */
    public LinkedList<String> getConversationParticipants() {
        return conversationParticipants;
    }

    /**
     * gets whether there is a non text portion of the MMS
     * @return boolean about whether there's images
     */
    public boolean getContainsMedia() {
        return containsMedia;
    }

    /**
     * A small thumbnail for the mainmenu
     * TODO Will need to be resized to lower resolution later
     *
     * @return Bitmap preview
     */
    public Bitmap getPreview() {
        return preview;
    }

    /**
     * getter for sender number
     * @return the sender number of the MMS
     */
    public String getSenderNumber() {
        return senderNumber;
    }
    /**
     * media ArrayList contains bitmaps for still images and uris for other file formats
     * @return ArrayList of bitmaps, and uris for playable file formats
     */
    public ArrayList<Object> getMedia() {
        return media;
    }

    /**
     * Figures out who is a part of the conversation and store the info
     * @param id MMSID
     * @param context any context
     */
    private void pullHeader(String id, Context context) {
        // Initiate data structures
        HashSet<String> numberSet = new HashSet<>();
        StringBuilder sbSendersDisplayed = new StringBuilder();
        conversationParticipants = new LinkedList<String>();
        String curNum;
        String type = "";

        // Get our phone number
        storeOurPhoneNumber(context);

        // Get the time stamp in milliseconds
        Cursor cursorDate = context.getContentResolver().query(Uri.parse("content://mms/"+id), null, null, null, null);
        if(cursorDate!=null) {
            if(cursorDate.moveToFirst()) {
                super.timeStamp = cursorDate.getString(cursorDate.getColumnIndex("date")) + "000";
            }
        }

        // Iterate through the address information
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mms/"+id+"/addr"), null, null, null, null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                }
                do {
                    // Store the number information
                    curNum = AllContacts.stripNumber(cursor.getString(cursor.getColumnIndex("address")));
                    conversationParticipants.add(curNum);
                    numberSet.add(curNum);

                    // Determine if we're the senderNumber or receiver of the message
                    if (curNum.equals(ourPhoneNumber)) {
                        type = cursor.getString(cursor.getColumnIndex("type"));
                    }
                    if(cursor.getString(cursor.getColumnIndex("type")).equals("137")) {
                        senderNumber = curNum;
                        senderContactContainer = AllContacts.getContactContainer(senderNumber);
                    }
                } while(cursor.moveToNext());

                formatSendersDisplayed(context);

                // If the type is 0x89 or 137, then we are the ones who sent the current MMS we're looking at
                // https://android.googlesource.com/platform/frameworks/opt/mms/+/4bfcd8501f09763c10255442c2b48fad0c796baa/src/java/com/google/android/mms/pdu/PduHeaders.java
                if(type.equals("137")) {
                    super.left = false;
                } else {
                    super.left = true;
                }
            }
            cursor.close();
        }


    }
    //

    /**
     * Formats the sendersDisplayed for the main menu
     * Adds all the numbers to the sendersDisplayed string minus the user's own number
     *
     * @param context any context
     */
    private void formatSendersDisplayed(Context context) {
        // Setup contact maps
        HashMap<String, ContactContainer> contactMap =
                AllContacts.getContactMap(context.getContentResolver(),mResources);


        // Setup
        StringBuilder sbSendersDisplayed = new StringBuilder();
        boolean first = true;
        ContactContainer contactContainer;
        for(String curNum : conversationParticipants) {
            if (!this.ourPhoneNumber.equals(curNum)) {
                if (first){
                    first = false;
                } else {
                    sbSendersDisplayed.append(", ");
                }
                if (contactMap != null) {
                    contactContainer = contactMap.get(curNum);
                    if (contactContainer != null) {
                        sbSendersDisplayed.append(contactContainer.getContactName());
                        contactContainers.add(contactContainer);
                    } else {
                        contactContainer = new ContactContainer(AllContacts.stripNumber(curNum));
                        sbSendersDisplayed.append(contactContainer.getContactName());
                        contactContainers.add(contactContainer);
                    }
                }

            }
        }

        // Determine if we have the person added as a contact
        sendersDisplayed = sbSendersDisplayed.toString();
    }

    /**
     * Goes through all the parts of the MMS associated with the given MMSID
     * Sets the class vars to the appropriate values
     * @param id the MMSID
     * @param context any context
     */
    private void pullContents (String id, Context context) {
        long curID = Long.parseLong(id);
        String type;
        containsMedia = false;
        // Set the cursor to the start of content://mms/ + id / part
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mms/" + Long.toString(curID) + "/part"), null, null, null, null);

        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                do {
                    type = cursor.getString(cursor.getColumnIndex("ct"));
                    String partID = cursor.getString(cursor.getColumnIndex("_id"));
                    if(pullContainsMedia(type)) {

                        containsMedia = true;
                    }
                    // Parse the message part
                    switch (type) {
                        case "application/smil":

                            break;
                        case "text/plain":
                            // TODO open an input stream at the text location in case it's an old phone
                            super.text = cursor.getString(cursor.getColumnIndex("text"));
                            break;
                        case "image/jpeg":
                            handleImage(cursor, context, partID);
                            break;
                        case "image/gif":
                            handleImage(cursor, context, partID);
                            break;
                        case "image/bmp":
                            handleImage(cursor, context, partID);
                            break;
                        case "video/mp4":
                            break;

                        default:
                            break;
                    }
                } while(cursor.moveToNext());
                if (containsMedia) {
                    setupPreview();
                }
            }
        }

    }

    /**
     * gets the MIME type of the MMS part
     * @param id MMSID
     * @param context Any context
     * @return String of the type of content
     */
    public HashSet<String> getType(String id, Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mms/"+id+"/part"), null, null, null, null);
        HashSet<String> typeSet = new HashSet<>();
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    typeSet.add(cursor.getString(cursor.getColumnIndex("ct")));
                } while(cursor.moveToNext());
            }

        }
        return typeSet;
    }

    /**
     * Goes backwards through our ArrayList and tries to find a suitable preview for the MMS
     */
    private void setupPreview() {
        for(int i = media.size()-1; i >= 0; i--) {
            if(media.get(i) instanceof Bitmap){
                preview = (Bitmap)media.get(i);
            }
        }
    }

    /**
     * A helper method for pullContents that extracts an image and adds it to the media
     * linkedlist of objects
     * @param cursor A cursor as to where we currently are in content://mms/x/part
     * @param context Given context
     * @param partID the partID of the MMS containing the image
     */
    private void handleImage(Cursor cursor, Context context, String partID) {
        // Obtain the location of the jpeg and open an input stream
        String location = cursor.getString(cursor.getColumnIndex("_data"));
        InputStream is = null;
        File jpegFile = new File(location);
        Bitmap extractedBitmap;
        try {
            is = context.getContentResolver().openInputStream(Uri.parse("content://mms/part/" + partID));
            InputStream is2 = context.getContentResolver().openInputStream(Uri.parse("content://mms/part/" + partID));

            BitmapFactory.Options calculationOptions = new BitmapFactory.Options();
            calculationOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, calculationOptions);
            final int REQUIRED_SIZE=150;
            int scale = 1;
            while(calculationOptions.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    calculationOptions.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
            bitmapFactoryOptions.inSampleSize = scale;

            extractedBitmap = BitmapFactory.decodeStream(is2, null, bitmapFactoryOptions);

            media.add(extractedBitmap);
        } catch (IOException e) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
    }


    private void handleVideo() {

    }

    private boolean pullContainsMedia(String type) {

        if (Pattern.compile("image").matcher(type).find() || Pattern.compile("audio").matcher(type).find() || Pattern.compile("video").matcher(type).find() || Pattern.compile("application").matcher(type).find()) {
            return true;
        }
        return false;
    }
    // The algorithm to extract text is as follows:




    private void storeOurPhoneNumber(Context context) {
        Cursor cursorSent = context.getContentResolver().query(Uri.parse("content://mms/sent"), null, null, null, null);
        String curNum = "";
        if(cursorSent!=null) {
            if (cursorSent.moveToFirst()) {
                String id = cursorSent.getString(cursorSent.getColumnIndex("_id"));
                Cursor cursorAddr = context.getContentResolver().query(Uri.parse("content://mms/" + id + "/addr"), null, null, null, null);
                if (cursorAddr != null) {
                    if (cursorAddr.moveToFirst()) {
                        if ("137".equals(cursorAddr.getString(cursorAddr.getColumnIndex("type")))) {
                            ourPhoneNumber = AllContacts.stripNumber(cursorAddr.getString(cursorAddr.getColumnIndex("address")));
                        }
                    }
                }
            }
        }
    }

}
