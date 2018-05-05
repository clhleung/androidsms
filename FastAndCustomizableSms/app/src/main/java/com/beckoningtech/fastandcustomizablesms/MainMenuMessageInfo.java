package com.beckoningtech.fastandcustomizablesms;

import java.util.HashSet;

/**
 * Class to store information about what to display in the main menu.
 *
 * Created by wyjun on 9/12/2017.
 */
public class MainMenuMessageInfo {
    private ContactContainer mContactContainer;
    private String mLastMessage;
    private String mNumber;
    private String mRelativeTime;
    private String mThreadID;
    private String mUnixTime;
    private HashSet<String> mNumberSetMMS;
    private boolean mContainsMedia;
    private MMSContainer mMMSContainer;

    /**
     * Constructor that initializes a MMMI with null fields, filled out when gathering data
     */
    MainMenuMessageInfo() {
        mContactContainer = null;
        mLastMessage = null;
        mNumber = null;
        mRelativeTime = null;
        mThreadID = null;
        mUnixTime = null;
        mNumberSetMMS = null;
        mContainsMedia = false;
        mMMSContainer = null;
    }
    public void setMMSContainer(MMSContainer mmsContainer) {
        mMMSContainer = mmsContainer;
    }

    /**
     *  getter for MMSContainer, contains info about an MMS
     * @return an MMSContainer
     */
    public MMSContainer getMMSContainer() {
        return mMMSContainer;
    }

    /**
     * sets the contact container we'll use when the thread is associated with a single person
     * TODO Add one of these for any MMS which only has 2 partiicpants
     *
     * @param cc takes in a contact container
     */
    public void setContactContainer(ContactContainer cc) {
        mContactContainer = cc;
    }

    /**
     *  getter for contact container
     * @return the contact container associated with the thread
     */
    public ContactContainer getContactContainer() {
        return mContactContainer;
    }

    /**
     * setter for last messasge
     * @param lastMessage the most recent message of the thread
     */
    public void setLastMessage(String lastMessage) {
        mLastMessage = lastMessage;
    }

    /**
     * getter for the last message
     * @return the most recent message of the thread
     */
    public String getLastMessage() {
        return mLastMessage;
    }

    /**
     * getter for a stripped number we can use
     * @return Returns stripped number: eg: 15593676302
     */
    public String getNumber() {
        return mNumber;
    }

    /**
     * setter for a stripped number
     * @param number stripped number: eg: 15593676302
     */
    public void setNumber(String number){
        mNumber = number;
    }

    /**
     * Store relative time
     * @param time relative Time string
     */
    public void setRelativeTime(String time) {
        mRelativeTime = time;
    }

    /**
     * getter for relative time
     * @return relative time
     */
    public String getRelativeTime() {
        return mRelativeTime;
    }

    /**
     * Setter for unix time
     *
     * @param time unix time in milliseconds
     */
    public void setUnixTime(String time) {
        mUnixTime = time;
    }

    /**
     * Getter for unix time
     *
     * @return time in milliseconds
     */
    public String getUnixTime() {
        return mUnixTime;
    }

    /**
     * getter for thread id
     * @return the thread ID
     */
    public String getThreadID() {
        return mThreadID;
    }

    /**
     * Setter for threadid
     * @param threadID a thread id
     */
    public void setThreadID(String threadID) {
        mThreadID = threadID;
    }

    /**
     * sets hashset of every conversation participant
     * @param numbers a hashset of all the numbers that are part of the MMS including ourselves
     */
    public void setNumberSetMMS(HashSet<String> numbers) {
        mNumberSetMMS = numbers;

        // Create a formatted string to display as the number
        StringBuilder sb = new StringBuilder();
        boolean notFirst = false;
        int numbersLeft = mNumberSetMMS.size();
        for(String curNum : mNumberSetMMS) {
            if(numbersLeft!=1) {
                if (notFirst) {
                    sb.append(", ");
                } else {
                    notFirst = true;
                }
                sb.append(curNum);
            }
            numbersLeft--;
        }
        setNumber(sb.toString());
    }

    /**
     * get's the number of everyone in the conversation
     * @return returns the numbers used in the conversation
     */
    public HashSet<String> getNumberSetMMS() {
        return mNumberSetMMS;
    }

    /**
     * Checks if our number set contains a number
     * @param number number we're checking
     * @return boolean if we contain given number
     */
    public boolean numberSetMMSContain(String number) {
        return mNumberSetMMS.contains(number);
    }

    /**
     * getter for contains media
     * @return boolean if contains media
     */
    public boolean getContainsMedia() {
        return mContainsMedia;
    }

    /**
     * sets if the MMS contains media
     * @param newSetting whether it contains media
     */
    public void setContainsMedia(boolean newSetting) {
        mContainsMedia = newSetting;
    }

}
