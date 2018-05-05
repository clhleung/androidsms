package com.beckoningtech.fastandcustomizablesms;

import android.app.Application;

/**
 * In case we want to change do anything in the application life cycle. Not needed at the moment,
 * but kept for future functionality.
 *
 * Created by wyjun on 11/7/2017.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate(){
        // If we ever need to listen for multiple activities in foreground or background, uncomment.
        //registerActivityLifecycleCallbacks(new MyActivityLifeCycleCallbacks());
        super.onCreate();
    }
}
