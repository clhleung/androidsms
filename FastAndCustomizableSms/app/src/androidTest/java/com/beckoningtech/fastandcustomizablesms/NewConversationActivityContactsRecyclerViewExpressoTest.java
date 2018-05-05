package com.beckoningtech.fastandcustomizablesms;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v17.leanback.widget.HorizontalGridView;

import com.android.ex.chips.RecipientEntry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import android.support.test.espresso.contrib.RecyclerViewActions;

/**
 * Tests the RecyclerView for QuickTexts in MessagesActivity.
 *
 * Created by wyjun on 11/27/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewConversationActivityContactsRecyclerViewExpressoTest {

    @Rule
    public ActivityTestRule<NewConversationActivity> mActivityRule =
            new ActivityTestRule(NewConversationActivity.class);

    @Test
    public void testContactsRecyclerView() throws Throwable {
        // Tests that if we display
        final ArrayList<ContactContainer> contactContainers = new ArrayList<>();
        contactContainers.add(new ContactContainer("test hello","+1234567890",
                "Mobile", "4564",(long)13,
                RecipientEntry.constructTopLevelEntry(
                        "test hello", 0, "1234567890",
                        2, "asjd", 13,
                        null, 0, "", true,
                        "4564")));
        contactContainers.add(new ContactContainer("abc adsf","(123)676-7890",
                "Mobile", "45644",(long)15,
                RecipientEntry.constructTopLevelEntry(
                        "test hello", 0, "1236767890",
                        2, "asjd", 15,
                        null, 0, "", true,
                        "45644")));
        ContactContainer contactContainer = new ContactContainer("3dfsfvb asdf",
                "153ac45d67890",
                "Mobile", "45624",(long)135,
                RecipientEntry.constructTopLevelEntry(
                        "test hello", 0, "1534567890",
                        2, "asjd", 135,
                        null, 0, "", true,
                        "45624"));
        Drawable drawable = mActivityRule.getActivity().getResources().getDrawable
                (R.drawable.common_full_open_on_phone);
        Bitmap bitmap =  ((BitmapDrawable)drawable).getBitmap();
        contactContainer.setContactImage(bitmap);
        contactContainers.add(contactContainer);
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().setupFakeContactsRecyclerView(contactContainers);
            }
        });

        onView(withText("3dfsfvb asdf")).check(matches(isDisplayed()));
        onView(withText("153ac45d67890")).check(matches(isDisplayed()));
        onView(withText("(123)676-7890")).check(matches(isDisplayed()));
        onView(withText("test hello")).check(matches(isDisplayed()));
        onView(withText("abc adsf")).check(matches(isDisplayed()));
        onView(withText("+1234567890")).check(matches(isDisplayed()));

        onView(withId(R.id.new_conversation_activity_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.new_conversation_activity_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.new_conversation_activity_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        String [] numbers = mActivityRule.getActivity().getNumbersArrayFromRecipientChips();
        assert(numbers.length == 3);
        Set<String> numbersSet = new HashSet<>();
        Collections.addAll(numbersSet, numbers);
        assert(numbersSet.contains(AllContacts.stripNumber("153ac45d67890")));
        assert(numbersSet.contains(AllContacts.stripNumber("(123)676-7890")));
        assert(numbersSet.contains(AllContacts.stripNumber("+1234567890")));
    }


}