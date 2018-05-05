package com.beckoningtech.fastandcustomizablesms;

import android.graphics.Bitmap;

import com.android.ex.chips.RecipientEntry;

import java.util.Comparator;

/**
 * Stores information about one specific contact. Note we can have multiple of these per
 * contact if there are multiple phone numbers stored for that contact.
 *
 * Created by wyjun on 9/12/2017.
 */

public class ContactContainer {
    private Long contactId;
    private Bitmap contactImage;
    private String contactName;
    private String contactNumber;
    private String contactLookupKey;
    private String contactNumberType;
    private int color;
    private int darkColor;
    private int fontColor;
    private boolean isContact = true;
    private RecipientEntry recipientEntry;


    /**
     * Constructor for ContactContainer
     * @param contactName
     * @param contactNumber
     * @param contactNumberType
     * @param contactLookupKey
     * @param contactId
     * @param contactImage
     */
    public ContactContainer(String contactName, String contactNumber, String contactNumberType,
                            String contactLookupKey, Long contactId, Bitmap contactImage,
                            RecipientEntry recipientEntry){
        this(contactName, contactNumber, contactNumberType, contactLookupKey, contactImage);
        this.contactId = contactId;
        this.recipientEntry = recipientEntry;
    }


    /**
     * Constructor for ContactContainer
     * @param contactName        Name of the contact
     * @param contactNumber      Phone Number of the contact
     * @param contactNumberType  Type of the number, i.e., home, mobile, etc.
     * @param contactLookupKey   Lookup key for the contact.
     */
    public ContactContainer(String contactName, String contactNumber, String contactNumberType,
                            String contactLookupKey){
        this.contactNumber = contactNumber;
        darkColor = ColorsUtil.pickDarkColor(contactLookupKey);
        color = ColorsUtil.pickColor(contactLookupKey);
        this.contactName = contactName;
        this.contactLookupKey = contactLookupKey;
        if(contactNumberType != "null"){
            this.contactNumberType = contactNumberType;
        }
    }

    /**
     * Constructor for ContactContainer
     * @param contactName        Name of the contact
     * @param contactNumber      Phone Number of the contact
     * @param contactNumberType  Type of the number, i.e., home, mobile, etc.
     * @param contactLookupKey   Lookup key for the contact.
     * @param contactImage       Image for the contact.
     */
    public ContactContainer(String contactName, String contactNumber, String contactNumberType,
                            String contactLookupKey, Bitmap contactImage){
        this(contactName, contactNumber, contactNumberType, contactLookupKey);
        this.contactImage = contactImage;
    }

    /**
     * Constructor for ContactContainer
     * @param contactName        Name of the contact
     * @param contactNumber      Phone Number of the contact
     * @param contactNumberType  Type of the number, i.e., home, mobile, etc.
     * @param contactLookupKey   Lookup key for the contact
     * @param contactId          Id for the contact
     * @param recipientEntry     RecipientEntry for the contact
     */
    public ContactContainer(String contactName, String contactNumber, String contactNumberType,
                            String contactLookupKey,
                            Long contactId, RecipientEntry recipientEntry){
        this(contactName, contactNumber, contactNumberType, contactLookupKey);
        this.contactId = contactId;
        this.recipientEntry = recipientEntry;
    }

    /**
     * Constructor for ContactContainer that takes in a number and builds the default color based
     * off of that number.
     * @param contactNumber  Phone number we are building a dummy ContactContainer for.
     */
    public ContactContainer(String contactNumber){
        int length = contactNumber.length();
        if (length == 11 && contactNumber.charAt(0)=='1'){
            contactNumber = contactNumber.substring(1);
            length = 10;
        }
        this.contactNumber = contactNumber;
        if(length >= 10){
            contactName = new StringBuilder(contactNumber).insert(length-4,"-").
                    insert(length-7,") ").insert(length-10,"(").toString();
        } else {
            contactName = contactNumber;
        }
        darkColor = ColorsUtil.pickDarkColor(contactName);
        color = ColorsUtil.pickColor(contactName);
        isContact = false;
    }

    /**
     * Returns the type of the contact's phone number.
     * @return contactNumberType
     */
    public String getContactNumberType() {
        return contactNumberType;
    }

    /**
     * Sets the contact's phone number type to another type.
     * @param contactNumberType  Type to set the contact's phone number type to
     */
    public void setContactNumberType(String contactNumberType) {
        this.contactNumberType = contactNumberType;
    }

    /**
     * Gets the RecipientEntry from the ContactContainer
     * @return recipientEntry
     */
    public RecipientEntry getRecipientEntry() {
        return recipientEntry;
    }

    /**
     * Sets the RecipientEntry
     * @param recipientEntry  RecipientEntry to set contact's RecipientEntry to
     */
    public void setRecipientEntry(RecipientEntry recipientEntry) {
        this.recipientEntry = recipientEntry;
    }

    /**
     * Comparator used in sorting the contacts in alphabetical order.
     */
    public static class AlphabeticalComparator implements Comparator<ContactContainer> {
        /**
         * Returns -1 if ContactContainer1 should appear before ContactContainer2 when sorted
         * alphabetically. Returns 0, if they are the same. Returns -1 if ContactContainer2
         * should appear first. Note that numbers go after letters in this sorting.
         * @param contactContainer1  ContainerContainer to be compared with ContainerContainer2
         * @param contactContainer2  ContainerContainer to be compared with ContainerContainer1
         * @return
         */
        @Override
        public int compare(ContactContainer contactContainer1, ContactContainer contactContainer2) {
            boolean isContact1 = contactContainer1.isContact;
            boolean isContact2 = contactContainer2.isContact;
            if(isContact1 && !isContact2){
                return -1;
            } else if (!isContact1 && isContact2){
                return 1;
            } else if(!isContact1 && !isContact2){
                String number1 = contactContainer1.contactNumber;
                String number2 = contactContainer2.contactNumber;
                return number1.compareTo(number2);
            } else {
                String name1 = contactContainer1.getContactName();
                String name2 = contactContainer2.getContactName();
                return name1.compareTo(name2);
            }
        }
    }


    /**
     * Gets the contactImages. In most cases we want to get the circular contact image instead.
     * @return Bitmap for ContactImage
     */
    public Bitmap getContactImage() {
        return contactImage;
    }

    /**
     * Gets the circular contact image instead.
     * @return Bitmap for circular ContactImage
     */
    public Bitmap getCircularContactImage(){
        return BitmapModifier.getCircularBitmap(contactImage);
    }

    /**
     * Sets the contact image to a bitmap
     * @param contactImage Bitmap to set the contact image to.
     */
    public void setContactImage(Bitmap contactImage) {
        this.contactImage = contactImage;
    }

    /**
     * Gets the contact name
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the contact name
     * @param contactName  String to set the contact name to.
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Gets the contact number.
     * @return contactNumber
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the contact number.
     * @param contactNumber  String to set the contact number to.
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Gets the contact ID. Note that this value can change as the contacts database gets modified
     * and updated. Use contactLookupKey if a static value is needed.
     * @return contactId
     */
    public Long getContactId() {
        return contactId;
    }

    /**
     * Sets the contact ID. Note that this value can change as the contacts database gets modified
     * and updated. Use contactLookupKey if a static value is needed.
     * @param contactId Long to set the contact ID to.
     */
    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }


    /**
     * Gets the contact lookup key. This value unlike the contact ID never changes.
     * @return contactLookupKey.
     */
    public String getContactLookupKey() {
        return contactLookupKey;
    }

    /**
     * Sets the contactLookupKey. This value unlike the contact ID never changes.
     * @param contactLookupKey String to set the contact lookup key to.
     */
    public void setContactLookupKey(String contactLookupKey) {
        this.contactLookupKey = contactLookupKey;
    }

    /**
     * Gets the color for the contact.
     * @return color
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color for the contact.
     * @param color Color to set the contact color to.
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Gets the dark color for the contact.
     * @return darkColor
     */
    public int getDarkColor() {
        return darkColor;
    }

    /**
     * Sets the dark color for the contact.
     * @param darkColor Color to set the contact dark color to.
     */
    public void setDarkColor(int darkColor) {
        this.darkColor = darkColor;
    }

    /**
     * Gets the font color for the contact.
     * @return fontColor
     */
    public int getFontColor() {
        return fontColor;
    }

    /**
     * Sets the color for the contact.
     * @param fontColor Color to set the contact font color to.
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Returns if the ContactContainer is an actual contact or a dummy one.
     * @return isContact
     */
    public boolean isContact() {
        return isContact;
    }

    /**
     * Sets whether or not the ContactContainer is an actual contact or a dummy one.
     * @param contact Is the contact real or fake?
     */
    public void setContact(boolean contact) {
        isContact = contact;
    }
}
