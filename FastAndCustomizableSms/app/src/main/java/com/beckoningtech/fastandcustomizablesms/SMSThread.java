package com.beckoningtech.fastandcustomizablesms;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by root on 10/29/17.
 */

public class SMSThread {
    public class Message implements Comparable<Message>{
        private String date;
        private String body;
        private String _id;
        private String originNumber;
        private boolean gravity;
        private MMSContainer mmsContainer;

        /**
         * used to sort the added messages in the appropriate order
         * @param m
         * @return return 1 the current date is more recent than the given date
         */
        @Override
        public int compareTo(Message m){
            Long result = Long.parseLong(this.getDate())-Long.parseLong(m.getDate());
            if (result > 0) {
                return 1;
            } else if (result < 0) {
                return -1;
            } else {
                return 0;
            }
        }

        public Message(String date, String body, String _id, String num, String person) {
            this.date = date;
            this.body = body;
            this._id = _id;
            this.originNumber = num;
            this.gravity = person.equalsIgnoreCase("1");
        }
        public Message(String date, String id, String num, MMSContainer container) {
            this.date = date;
            this._id = id;
            this.originNumber = num;
            mmsContainer = container;
            this.gravity = mmsContainer.left;
        }

        public MMSContainer getMMSContainer() {
            return mmsContainer;
        }

        public String getBody(){
            return this.body;
        }

        public String getDate(){
            return this.date;
        }

        public String getId(){
            return this._id;
        }

        public String getNumber(){
            return this.originNumber;
        }

        @Override
        public boolean equals(Object o){
            return (o instanceof Message) && (((Message) o).getId()) == this.getId();

        }

        @Override
        public int hashCode(){
            return this.getId().hashCode();
        }

        public boolean getGravity() {
            return this.gravity;
        }

        @Override
        public String toString(){
            return this.body;
        }
    }

    private String threadId;
    private ArrayList<Message> msgs;

    public SMSThread(String id){
        this.threadId = id;
        msgs = new ArrayList<>();
    }

    public void addMessage(String id, String number, String date, String body, String person) {
        msgs.add(new Message(date, body, id, number, person));
    }
    public void addMessage(String id, String number, String date, MMSContainer mmsContainer) {
        msgs.add(new Message(date, id, number, mmsContainer));
    }
    public ArrayList<Message> getMessageThread(){
        Collections.sort(msgs);
        return msgs;
    }

    public String getThreadId() {
        return this.threadId;
    }
}
