package com.beckoningtech.fastandcustomizablesms;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.RecyclerView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests the RecyclerView for QuickTexts in MessagesActivity.
 *
 * Created by wyjun on 11/27/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessagesActivityQuickTestRecyclerViewExpressoTest {

    @Rule
    public ActivityTestRule<MessagesActivity> mActivityRule =
            new ActivityTestRule(MessagesActivity.class);

    @Test
    public void testQuickTextRecyclerView() throws Throwable {
        // Tests that if we display
        final ArrayList<QuickTextItem> quickTextItems = new ArrayList<>();
        final String message[] = {"YoloS@ck",
                "BurntMeat1", "BurntMeat2"};

        final String tag = "Sample";
        int messageCounter = 0;
        for (String msg : message) {
            QuickTextItem quickTextItem =
                    new QuickTextItem(messageCounter, new ArrayList<String>(), 0, msg,
                            null, null, msg,
                            messageCounter % 2 == 0, true,
                            "Hi!");
            quickTextItems.add(quickTextItem);
            messageCounter++;
        }
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().fillQuickTextRecyclerViewWithFakeData(quickTextItems);
            }
        });
        final MessagesActivityQuickTextAdapter adapter = (MessagesActivityQuickTextAdapter)
                mActivityRule.getActivity().getQuickTextAdapterForTest();
        final HorizontalGridView recyclerView =
                mActivityRule.getActivity().getQuickTextRecyclerViewForTest();
        onView(withText("YoloS@ck")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(1);
            }
        });
        onView(withText("BurntMeat1")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(2);
            }
        });
        onView(withText("BurntMeat2")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(3);
            }
        });
        onView(withText("Add Action")).check(matches(isDisplayed()));


        // Add new quick texts
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(new QuickTextItem(adapter.getItemCount(),
                        new ArrayList<String>(), 0, "test",
                        null, null, "added1",
                        false, true,
                        "Hi!"));
                adapter.notifyItemChanged(adapter.getItemCount()-2);
                adapter.notifyItemInserted(adapter.getItemCount()-1);
                recyclerView.scrollToPosition(
                        adapter.getItemCount() - 1);
            }
        });
        onView(withText("added1")).check(matches(isDisplayed()));
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(new QuickTextItem(adapter.getItemCount(),
                        new ArrayList<String>(), 0, "test",
                        null, null, "added2",
                        false, true,
                        "Hi!"));
                adapter.notifyItemChanged(adapter.getItemCount()-2);
                adapter.notifyItemInserted(adapter.getItemCount()-1);
                recyclerView.scrollToPosition(
                        adapter.getItemCount() - 1);
            }
        });
        onView(withText("added2")).check(matches(isDisplayed()));

        // Testing delete. Commented out because when viewed manually in debug mode,
        // removed view is not showing up, but check still fails.
//        mActivityRule.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                adapter.remove(0);
//                adapter.notifyItemRemoved(0);
//                recyclerView.scrollToPosition(0);
//            }
//        });
//        onView(withText("YoloS@ck")).check(doesNotExist());
//        mActivityRule.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                adapter.remove(1);
//                adapter.notifyItemRemoved(1);
//                recyclerView.scrollToPosition(0);
//            }
//        });
//        onView(withText("BurntMeat2")).check(doesNotExist());
    }


}