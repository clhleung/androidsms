package com.beckoningtech.fastandcustomizablesms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service respond to quick response introduced since Android 4.0
 */

/**
 * Dummy service to make sure this app can be default SMS app
 */
public class HeadlessSmsSendService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}