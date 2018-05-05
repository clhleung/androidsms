package com.beckoningtech.fastandcustomizablesms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for AllContacts.
 * Created by wyjun on 11/27/2017.
 */

public class AllContactsUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        // Test stripNumber
        assertEquals(AllContacts.stripNumber("asjd"),"");
        assertEquals(AllContacts.stripNumber(""),"");
        assertEquals(AllContacts.stripNumber(null),"");
        assertEquals(AllContacts.stripNumber("asjd123"),"123");
        assertEquals(AllContacts.stripNumber("(123)456-7890"),"11234567890");
        assertEquals(AllContacts.stripNumber("-7890"),"7890");
    }

}
