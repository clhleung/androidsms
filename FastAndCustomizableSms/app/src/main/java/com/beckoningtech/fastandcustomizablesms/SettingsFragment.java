package com.beckoningtech.fastandcustomizablesms;


import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference;
import	android.preference.SwitchPreference;
import android.media.AudioManager;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.Toast;
import android.content.Context;
import android.support.v7.preference.PreferenceManager;


/**
 * Fragment class responsile for all logic interactions user makes with the setting page 
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {   
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private static final int DEF_SMS_REQ = 1001;
    /**
     * Actions to do once preferences has been created. 
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        
        addPreferencesFromResource(R.xml.settingsmanifest);        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference preference = this.findPreference("default_sms");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            /**
             * Enables user to set this app as default SMS app
             */
            @Override
            public boolean onPreferenceClick(Preference preference){

                final String myPackageName = getActivity().getPackageName();
                if (!Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(myPackageName)) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                            BuildConfig.APPLICATION_ID);
                    startActivityForResult(intent, DEF_SMS_REQ);
                }
                return true;
            }
        });

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            /**
             * Listens for changes user makes to settings and act accordingly
             */
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Boolean notifsounder = prefs.getBoolean("all_notification_switch",false);
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                
                if(key.equals("all_notification_switch")){
                    if (notifsounder == false) {                       
                        findPreference("notifSound").setEnabled(false);
                        findPreference("outgoing_switch").setEnabled(false);
                        findPreference("vibrate_switch").setEnabled(false);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else {
                        findPreference("notifSound").setEnabled(true);
                        findPreference("outgoing_switch").setEnabled(true);
                        findPreference("vibrate_switch").setEnabled(true);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                }


            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);       
    }

        /**
         * Actions to take for changed preferences depending on their keys
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {            
            AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            Boolean sharedPref = prefs.getBoolean("all_notification_switch",false);
            Boolean vibrate = prefs.getBoolean("vibrate_switch",false);
            Boolean notifsounder = prefs.getBoolean("notifsound",false);
            if (key.equals("all_notification_switch")) {
                if (sharedPref == false){
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {

                }

            } else if (key.equals("notifsound")) {
                if (notifsounder == false) {                   
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }

            } else if (key.equals("vibrate_switch")) {
                if (vibrate == false) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            } else {
                Toast.makeText(getActivity().getBaseContext(),
                        "Nothing happenning dumbass",
                        Toast.LENGTH_SHORT
                ).show();
            }


        };
}
