package com.beckoningtech.fastandcustomizablesms;

import android.Manifest;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by wyjun on 11/27/2017.
 */

public class AllContactsInstrumentedTest {

    @Test
    @Before
    public void getPerms() {
        GrantPermissionRule.grant("android.permission.SEND_SMS");
        GrantPermissionRule.grant("android.permission.READ_SMS");
        GrantPermissionRule.grant("android.permission.WRITE_SMS");
        GrantPermissionRule.grant("android.permission.RECEIVE_SMS");
        GrantPermissionRule.grant("android.provider.Telephony.SMS_RECEIVED");
        GrantPermissionRule.grant("android.permission.WAKE_LOCK");
        GrantPermissionRule.grant("android.permission.INTERNET");
        GrantPermissionRule.grant("android.permission.ACCESS_WIFI_STATE");
        GrantPermissionRule.grant("android.permission.CHANGE_WIFI_STATE");
        GrantPermissionRule.grant("android.permission.CHANGE_NETWORK_STATE");
        GrantPermissionRule.grant("android.permission.ACCESS_NETWORK_STATE");
        GrantPermissionRule.grant("android.permission.WRITE_EXTERNAL_STORAGE");
        GrantPermissionRule.grant("android.permission.WRITE_SETTINGS");
        GrantPermissionRule.grant("android.permission.READ_CONTACTS");
        GrantPermissionRule.grant("android.permission.WRITE_CONTACTS");
        GrantPermissionRule.grant("android.permission.RECEIVE_BOOT_COMPLETED");
        GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");
        GrantPermissionRule.grant("android.permission.ACCESS_COARSE_LOCATION");
    }

    @Test
    public void useAppContext() throws Exception {
        getPerms();
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Makes sure that AllContacts does not crash without inputs.
        assertEquals(AllContacts.getContactMap(), null);
        assertEquals(AllContacts.getInstance(), null);
        assertEquals(AllContacts.getContactContainer("123"), null);


        ColorsUtil.setupColors(appContext);
        AllContacts.getContactMap(appContext.getContentResolver(),appContext.getResources());
        AllContacts.getInstance(appContext.getContentResolver(),appContext.getResources());
        // Can't test more than this automatically because the rest is
        // reliant on the phone's contact.
    }
}