package com.beckoningtech.fastandcustomizablesms;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.android.ex.chips.RecipientEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton class to get and store a hashmap of phone numbers to ContactContainers.
 * Also stores colors.
 *
 * Created by wyjun on 10/17/2017.
 */

public class AllContacts {
    private static AllContacts instance;
    private ContentResolver contentResolver;
    private Resources resources;
    private static HashMap<String, ContactContainer> contactMap;

    /**
     * Function to override the contentResolve and resources.
     * @param contentResolver ContentResolver for retrieving contacts.
     * @param resources       Deprecated. Can pass in null.
     */
    public static void setContentResolverAndResources(@NonNull ContentResolver contentResolver,
                                                      @NonNull Resources resources) {
        if(instance == null){
            instance = new AllContacts(contentResolver,resources);
        } else {
            instance.contentResolver = contentResolver;
            instance.resources = resources;
        }
    }

    /**
     * Constructor for singleton. Never called outside this class.
     * @param contentResolver ContentResolver for retrieving contacts.
     * @param resources       Deprecated. Can pass in null.
     */
    protected AllContacts(ContentResolver contentResolver, Resources resources) {
        contactMap = new HashMap<>();
        this.contentResolver = contentResolver;
        this.resources = resources;
        getAllContacts();
    }


    /**
     * Getter for the instance. Creates a new instance if instance is null.
     * @param contentResolver ContentResolver for retrieving contacts.
     * @param resources       Deprecated. Can pass in null.
     * @return AllContacts instance
     */
    public static AllContacts getInstance(
            @NonNull ContentResolver contentResolver, @NonNull Resources resources){
        if (instance == null){
            instance = new AllContacts(contentResolver, resources);
        }
        return instance;
    }

    /**
     * Getter for the instance
     * @return AllContacts instance if instance != null; null otherwise
     */
    public static AllContacts getInstance(){
        if (instance == null){
            return null;
        }
        return instance;
    }


    /**
     * Get the instance's contactMap
     * @return instance.contactMap
     */
    public static HashMap<String, ContactContainer> getContactMap(){
        if(instance == null){
            return null;
        }
        return instance.contactMap;
    }

    /**
     * Gets the instance's contactMap. Creates the instance if the instance is null.
     * @param contentResolver ContentResolver for retrieving contacts.
     * @param resources       Deprecated. Can pass in null.
     * @return instance.contactMap
     */
    public static HashMap<String, ContactContainer> getContactMap(
            @NonNull ContentResolver contentResolver, @NonNull Resources resources){
        if(instance == null){
            instance = new AllContacts(contentResolver, resources);
        }
        return instance.contactMap;
    }

    /**
     * Gets the instance's contactMap as an ArrayList. Creates the instance if the instance is null.
     * @param contentResolver ContentResolver for retrieving contacts.
     * @param resources       Deprecated. Can pass in null.
     * @return contactArrayList
     */
    public static ArrayList<ContactContainer> getContactArrayList(
            @NonNull ContentResolver contentResolver, @NonNull Resources resources){
        if(instance == null){
            instance = new AllContacts(contentResolver, resources);
        }

        return new ArrayList<>(instance.contactMap.values());
    }

    /**
     * Returns the contentResolver.
     * @return contentResolver
     */
    public ContentResolver getContentResolver(){
        return contentResolver;
    }

    /**
     * Updates AllContacts' contact information if instance is not null.
     */
    public static void updateAllContacts(){
        if(instance != null){
            instance.getAllContacts();
        }
    }

    /**
     * Retrieves all the contacts and store them in the instance.
     */
    private void getAllContacts() {
        ArrayList<ContactContainer> contactContainerList;
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null,
                null, null);

        if (cursor!=null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    long id = cursor.getLong(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String lookupKey = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    String photoThumbnailUri = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    int displayNameSource = cursor.getInt(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_SOURCE));
                    contactContainerList = createContactContainerPerContactNumber(
                            id, name, lookupKey, photoThumbnailUri, displayNameSource);
                    assignContactContainerImages(contactContainerList, cursor);
                    //TODO: add emails to ContactContainer if we want app to send emails
                    // Not relevant now. Will be relevant later with MMS.
//                    Cursor emailCur = contentResolver.query(
//                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                            new String[] { id }, null);
//                    while (emailCur.moveToNext()) {
////                        emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
////                        emailType = emailCur .getString(emailCur .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//                    }


                }
            }
        }
    }

    /**
     * This method takes in information about a specific contact and build contact containers
     * using both the given information and the phone number specific information associated
     * with that contact.
     * @param id Contact Id
     * @param name Contact name
     * @param lookupKey Contact Lookup Key
     * @param photoThumbnailUri Contact photo thumbnail Uri
     * @param displayNameSource Contact display name source
     * @return List of newly created ContactContainers, one per phone number.
     */
    private ArrayList<ContactContainer> createContactContainerPerContactNumber(
            long id, String name, String lookupKey, String photoThumbnailUri,
            int displayNameSource){
        ArrayList<ContactContainer> contactContainerList = new ArrayList<>();
        Cursor phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?",
            new String[]{lookupKey},
            null);
        boolean isFirstLevel = true;
        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            do {
                long contactId = phoneCursor.getLong(
                        phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String phoneNumber = phoneCursor.getString(
                        phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phoneTypeInt = phoneCursor.getString(
                        phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE
                        )
                );
                String phoneLabel = phoneCursor.getString(
                        phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.LABEL
                        )
                );
                String phoneType = getPhoneNumberType(phoneTypeInt, phoneLabel);
                RecipientEntry recipientEntry = RecipientEntry.constructTopLevelEntry(
                        name, displayNameSource, phoneNumber,
                        Integer.parseInt(phoneTypeInt), phoneLabel, contactId,
                        null, id, photoThumbnailUri, true,
                        lookupKey);
                ContactContainer contactContainer = new ContactContainer(name,phoneNumber,
                        phoneType, lookupKey,contactId, recipientEntry);
                contactMap.put(AllContacts.stripNumber(phoneNumber), contactContainer);

                contactContainerList.add(contactContainer);
            } while (phoneCursor.moveToNext());
            phoneCursor.close();
        }
        return contactContainerList;
    }

    /**
     * Looks for a contact images and assigns each ContactContainer
     * in contactContainers the contact image if found.
     * @param contactContainers List of ContactContainers each with a different phone number
     * @param cursor Cursor for the contact associated with the list of ContactContainers
     */
    private void assignContactContainerImages(ArrayList<ContactContainer> contactContainers,
                                              Cursor cursor){
        String image_uri = cursor.getString(cursor.
            getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
        if (image_uri != null) {
            System.out.println(Uri.parse(image_uri));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver,
                        Uri.parse(image_uri));
                for(ContactContainer contactContainer : contactContainers) {
                    contactContainer.setContactImage(bitmap);
                }
                System.out.println(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Returns a string representing the phone number type.
     * @param phoneTypeInt
     * @param phoneLabel
     * @return phoneTypeString
     */
    private String getPhoneNumberType(String phoneTypeInt, String phoneLabel){
        String phoneType = "Unknown";
        switch (Integer.parseInt(phoneTypeInt)){
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM: {
                phoneType = phoneLabel;
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME: {
                phoneType = "Home";
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE: {
                phoneType = "Mobile";
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK: {
                phoneType = "Work";
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE: {
                phoneType = "Work Mobile";
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN: {
                phoneType = "Company Main";
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT: {
                phoneType = "Assistant";
                break;
            }
        }
        return phoneType;
    }


    /**
     * Opens the system's contact editor for a specific phone number. If the phone number does
     * not pair with a contact, a new contact menu is created.
     * @param context
     * @param phoneNumber
     */
    public void openContactsEditor(Context context, String phoneNumber){
        phoneNumber = stripNumber(phoneNumber);
        ContactContainer contactContainer = contactMap.get(phoneNumber);
        // Creates a new Intent to edit a contact
        Intent intent;
        if(contactContainer!= null){
            Uri mSelectedContactUri;
            mSelectedContactUri =
                    ContactsContract.Contacts.getLookupUri(contactContainer.getContactId(),
                            contactContainer.getContactLookupKey());
            intent = new Intent(Intent.ACTION_EDIT);
            /*
             * Sets the contact URI to edit, and the data type that the
             * Intent must match
             */
            intent.setDataAndType(mSelectedContactUri,
                    ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        } else {
            // Creates a new Intent to insert a contact
            intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            // Sets the MIME type to match the Contacts Provider
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        }
        context.startActivity(intent);
    }

    /**
     * Removes all non numerical values and if there are exactly 10 digits, add a 1 to the front.
     * @param origNumber
     * @return number without only numerical values
     */
    public static String stripNumber(String origNumber){
        if(origNumber == null){
            return "";
        }
        String tmp = origNumber.replaceAll("[^\\d]", "");
        if(tmp.length()==10){
            tmp = "1"+tmp;
        }
        return tmp;
    }

    /**
     * Returns a contact container for a specific number.
     * @param phoneNumber Phone number of the ContactContainer we are looking for.
     * @return contactContainer
     */
    public static ContactContainer getContactContainer(String phoneNumber){
        if(contactMap!=null) {
            return contactMap.get(stripNumber(phoneNumber));
        } else {
            return null;
        }
    }


}
