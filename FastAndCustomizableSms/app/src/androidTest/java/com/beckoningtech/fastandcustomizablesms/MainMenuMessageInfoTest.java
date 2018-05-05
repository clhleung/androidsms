package com.beckoningtech.fastandcustomizablesms;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jungk on 12/3/2017.
 */

public class MainMenuMessageInfoTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Make sure the main menu message info is initialized correctly
        MainMenuMessageInfo testMessage = new MainMenuMessageInfo();
        assertEquals(testMessage.getLastMessage(), null);
        assertEquals(testMessage.getContactContainer(), null);
        assertEquals(testMessage.getContainsMedia(), false);
        assertEquals(testMessage.getMMSContainer(), null);
        assertEquals(testMessage.getNumberSetMMS(), null);
        assertEquals(testMessage.getNumber(), null);
        assertEquals(testMessage.getThreadID(), null);
        assertEquals(testMessage.getUnixTime(), null);
        assertEquals(testMessage.getRelativeTime(), null);

        // All the functionality for stripping information is in all contacts
        // It's irrelevant to add more than this

        // TODO Consider refactoring setters and add in tests to make sure inputs are sanitized
    }
}
