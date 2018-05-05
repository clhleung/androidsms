package com.beckoningtech.fastandcustomizablesms;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by root on 12/5/17.
 */

public class PermissionGranter {
    public static void grantPhonePermission() {
        // In M+, trying to call a number will trigger a runtime dialog. Make sure
        // the permission is granted before running this test.
        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.SEND_SMS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.READ_SMS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.WRITE_SMS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.RECEIVE_MMS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.CALL_PHONE");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.provider.Telephony.SMS_RECEIVED");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.WAKE_LOCK");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.INTERNET");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.ACCESS_WIFI_STATE");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.CHANGE_WIFI_STATE");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.CHANGE_NETWORK_STATE");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.ACCESS_NETWORK_STATE");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.WRITE_EXTERNAL_STORAGE");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.WRITE_SETTINGS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.READ_CONTACTS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.WRITE_CONTACTS");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.RECEIVE_BOOT_COMPLETED");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.ACCESS_FINE_LOCATION");

        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.ACCESS_COARSE_LOCATION");

    }
}
