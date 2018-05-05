package com.beckoningtech.fastandcustomizablesms;

import android.support.v7.app.AppCompatActivity;

/**
 * Extension of AppCompatActivity with function setupColors.
 *
 * Created by wyjun on 12/1/2017.
 */

public abstract class ColoredCompatActivity extends AppCompatActivity {

    /**
     * Initializes colors.
     */
    protected void setupColors(){
        ColorsUtil.setupColors(this);
    }
}
