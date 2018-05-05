package com.beckoningtech.fastandcustomizablesms;


import android.os.Build;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.beckoningtech.fastandcustomizablesms.MessagesActivity.getPhoneNum;

/**
 * Tests the RecyclerView for QuickTexts in MessagesActivity.
 *
 * Created by wyjun on 11/27/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessagesActivityMessagesRecyclerViewExpressoTest {

    @Before
    public void grantPhonePermission() {
        // In M+, trying to call a number will trigger a runtime dialog. Make sure
        // the permission is granted before running this test.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.SEND_SMS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.READ_SMS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.WRITE_SMS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.RECEIVE_MMS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.CALL_PHONE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.provider.Telephony.SMS_RECEIVED");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.WAKE_LOCK");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.INTERNET");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.ACCESS_WIFI_STATE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.CHANGE_WIFI_STATE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.CHANGE_NETWORK_STATE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.ACCESS_NETWORK_STATE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.WRITE_SETTINGS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.READ_CONTACTS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.WRITE_CONTACTS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.RECEIVE_BOOT_COMPLETED");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.ACCESS_FINE_LOCATION");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.ACCESS_COARSE_LOCATION");
        }
    }
    @Rule
    public ActivityTestRule<MessagesActivity> mActivityRule =
            new ActivityTestRule(MessagesActivity.class);

    @Test
    public void testMessagesRecyclerView() throws Throwable {
        // Tests that if we display
        final ArrayList<MessageContainer> messageContainers = new ArrayList<>();
        messageContainers.add(new MessageContainer(false, "YoloS@ck"));
        messageContainers.add(new MessageContainer(false, "BurntMeat1"));
        messageContainers.add(new MessageContainer(true, "BurntMeat2"));
        messageContainers.add(new MessageContainer(false, "BurntMeat3"));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().fillMessagesRecyclerViewWithFakeData(messageContainers);
            }
        });
        final MessagesActivityMessagesRecyclerAdapter adapter =
                mActivityRule.getActivity().getMessagesAdapterForTest();
        final RecyclerView recyclerView =
                mActivityRule.getActivity().getMessagesRecyclerViewForTest();
        mActivityRule.getActivity().scrollToBottom();
        onView(withText("YoloS@ck")).check(matches(isDisplayed()));
        onView(withText("BurntMeat1")).check(matches(isDisplayed()));
        onView(withText("BurntMeat2")).check(matches(isDisplayed()));
        onView(withText("BurntMeat3")).check(matches(isDisplayed()));

        // Testing to see if receiving messages can correctly update received messages.
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().addMsg("1234567890", "Hello!");
            }
        });
        onView(withText("Hello!")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().addMsg("1234567890", "Hello!1");
            }
        });
        onView(withText("Hello!1")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().addMsg("1234567890", "Hello!2");
            }
        });
        onView(withText("Hello!2")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().addMsg("1234567890", "Hello!3");
            }
        });
        onView(withText("Hello!3")).check(matches(isDisplayed()));

        // Testing to see if receiving messages can correctly update sent messages.
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(new MessageContainer(false, "BurningLake"));
                adapter.notifyItemInserted(adapter.getItemCount()-1);
                recyclerView.scrollToPosition(
                        adapter.getItemCount() - 1);
            }
        });
        onView(withText("BurningLake")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(new MessageContainer(false, "BurningLake1"));
                adapter.notifyItemInserted(adapter.getItemCount()-1);
                recyclerView.scrollToPosition(
                        adapter.getItemCount() - 1);
            }
        });
        onView(withText("BurningLake1")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(new MessageContainer(false, "BurningLake2"));
                adapter.notifyItemInserted(adapter.getItemCount()-1);
                recyclerView.scrollToPosition(
                        adapter.getItemCount() - 1);
            }
        });
        onView(withText("BurningLake2")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(new MessageContainer(false, "BurningLake3"));
                adapter.notifyItemInserted(adapter.getItemCount()-1);
                recyclerView.scrollToPosition(
                        adapter.getItemCount() - 1);
            }
        });
        onView(withText("BurningLake3")).check(matches(isDisplayed()));


    }


}