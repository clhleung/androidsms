package com.beckoningtech.fastandcustomizablesms;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * If we want to check what activity is in the foreground/background or if our application is in
 * the foreground/background we can register this in MainApplication. Not always needed, as shown
 * in MessagesActivity, in which I just had a static reference to the activity and set it
 * onResume and cleared it onPause. Kept as a placeholder for future functionality.
 *
 * Created by wyjun on 11/7/2017.
 */

public class MyActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {
    //private static Activity curActivity;

    public MyActivityLifeCycleCallbacks(){

    }

//    public static Activity getCurActivity() {
//        return curActivity;
//    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        //curActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //curActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
