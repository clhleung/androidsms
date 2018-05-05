package com.beckoningtech.fastandcustomizablesms;

/**
 * Class to store information about a message to be displayed in MessagesActivity.
 *
 * Created by root on 10/27/17.
 */

public class MessageContainer {
    public boolean left;
    public boolean showTimeStamp = false;
    public String text;
    public String timeStamp;

    public MessageContainer(boolean left, String text){
        super();
        this.left = left;
        this.text = text;
    }

    public MessageContainer(boolean left, String text, String timeStamp){
        super();
        this.left = left;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public MessageContainer(boolean left, String text, String timeStamp, boolean showTimeStamp){
        super();
        this.left = left;
        this.text = text;
        this.timeStamp = timeStamp;
        this.showTimeStamp = showTimeStamp;
    }
}
