package com.beckoningtech.fastandcustomizablesms;
import android.support.v7.preference.PreferenceCategory;
import 	android.support.v7.preference.PreferenceFragmentCompat;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import	android.preference.SwitchPreference;
import android.media.AudioManager;
import android.widget.Toast;

/**
 * Activity that transitions from main menu to the settings fragment
 */
public class SettingsActivity extends AppCompatActivity  {
    PreferenceScreen defaultApp;
    SwitchPreference notify;
    
    /**
     * Does the actual initialization of Settings Fragment that handles
     * the settings interaction
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }
}
   






