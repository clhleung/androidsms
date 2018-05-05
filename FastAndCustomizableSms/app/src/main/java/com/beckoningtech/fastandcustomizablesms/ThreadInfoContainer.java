package com.beckoningtech.fastandcustomizablesms;

import java.util.ArrayList;

/**
 * Created by wyjun on 11/27/2017.
 */

public class ThreadInfoContainer {
    private String threadId;
    private ArrayList<ContactContainer> contactContainersInThread;

    public  ThreadInfoContainer(String threadId,
                                ArrayList<ContactContainer> contactContainersInThread) {
        this.threadId = threadId;
        this.contactContainersInThread = contactContainersInThread;
    }

    public ArrayList<ContactContainer> getContactContainersInThread() {
        return contactContainersInThread;
    }

    public void setContactContainersInThread(ArrayList<ContactContainer> contactContainersInThread) {
        this.contactContainersInThread = contactContainersInThread;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
