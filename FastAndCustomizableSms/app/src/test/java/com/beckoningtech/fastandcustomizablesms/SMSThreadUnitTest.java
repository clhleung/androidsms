package com.beckoningtech.fastandcustomizablesms;

import org.junit.Test;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.beckoningtech.fastandcustomizablesms.SMSThread;
import com.beckoningtech.fastandcustomizablesms.RandomStringGenerator;


/**
 * Created by root on 11/22/17.
 */

public class SMSThreadUnitTest {

    @Test
    public void messageConstructorTest() {
        String testString = RandomStringGenerator.getSaltedString(10);
        SMSThread smsThread = new SMSThread(testString);
        assertTrue(smsThread.getThreadId().equalsIgnoreCase(testString));
    }

    @Test
    public void compareToTest() {
        String testString = RandomStringGenerator.getSaltedString(10);
        SMSThread smsThread = new SMSThread(testString);
        ArrayList<ArrayList<String>> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArrayList<String> params = new ArrayList<>();
            params.add(RandomStringGenerator.getRandomNumber(100));
            params.add(RandomStringGenerator.getRandomPhoneNumber());
            params.add(RandomStringGenerator.getRandomNumber(10));
            params.add(RandomStringGenerator.getSaltedString(100));
            params.add(RandomStringGenerator.getSaltedString(10));
            input.add(params);
            smsThread.addMessage(params.get(0), params.get(1), params.get(2),
                    params.get(3), params.get(4)
            );
        }
        HashMap<String, ArrayList<String>> easyIdentify = new HashMap<>();

        for (ArrayList<String> verifyInput : input) {
            easyIdentify.put(verifyInput.get(0), verifyInput);
        }

        ArrayList<SMSThread.Message> messages = smsThread.getMessageThread();

        for (int i = 0; i < messages.size(); i++) {
            String msgId = messages.get(i).getId();
            assertTrue(messages.get(i).getNumber().equalsIgnoreCase((easyIdentify.get(msgId)).get(1)));
            assertTrue(messages.get(i).getDate().equalsIgnoreCase((easyIdentify.get(msgId)).get(2)));
            assertTrue(messages.get(i).getBody().equalsIgnoreCase((easyIdentify.get(msgId)).get(3)));
        }
    }

    @Test
    public void compareToStressTest() {
        String testString = RandomStringGenerator.getSaltedString(10);
        SMSThread smsThread = new SMSThread(testString);
        for (int i = 0; i < 1000000; i++) {
            ArrayList<String> params = new ArrayList<>();
            params.add(RandomStringGenerator.getRandomNumber(100));
            params.add(RandomStringGenerator.getRandomPhoneNumber());
            params.add(RandomStringGenerator.getRandomNumber(10));
            params.add(RandomStringGenerator.getSaltedString(100));
            params.add(RandomStringGenerator.getSaltedString(10));
            smsThread.addMessage(params.get(0), params.get(1), params.get(2),
                    params.get(3), params.get(4)
            );
        }

        long startTime = System.nanoTime();
        smsThread.getMessageThread();
        long totalTime = System.nanoTime() - startTime;

        assertTrue(totalTime / 1000000000 < 10);
    }
}
