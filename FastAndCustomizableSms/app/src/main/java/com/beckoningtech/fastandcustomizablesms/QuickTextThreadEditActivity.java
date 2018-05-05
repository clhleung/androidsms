package com.beckoningtech.fastandcustomizablesms;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Activity to edit Quick Texts.
 */
public class QuickTextThreadEditActivity extends ColoredCompatActivity {

    private int primaryColor;
    private int darkColor;
    private int fontColor;
    private int backgroundColor;
    private int quickTextNumber;
    private String threadId;
    private ScrollView scrollView;
    private Button saveQuickText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_text_thread_edit);
        scrollView = (ScrollView) findViewById(R.id.quick_text_thread_edit_activity_scroll_view);
        Intent intent = getIntent();
        primaryColor = intent.getIntExtra("primaryColor", ColorsUtil.getPrimaryColor());
        darkColor = intent.getIntExtra("darkColor", ColorsUtil.getDarkPrimaryColor());
        fontColor = intent.getIntExtra("fontColor", ColorsUtil.getFontColor());
        backgroundColor = intent.getIntExtra("backgroundColor", ColorsUtil.getBackgroundColor());
        quickTextNumber = intent.getIntExtra("quickTextNumber",-1);
        if(quickTextNumber == -1){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        threadId = intent.getStringExtra("THREAD_ID");
        if(Objects.equals(threadId, "")){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        saveQuickText = (Button) findViewById(R.id.saveButton);

        this.saveQuickText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveQuickTexts();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("added", "true");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        setupColors();
    }

    private void saveQuickTexts() {
        EditText recipientEditor = (EditText) findViewById(R.id.recipientNumber);
        EditText messageEditor = (EditText) findViewById(R.id.messageContent);
        EditText nameEditor = (EditText) findViewById(R.id.recipientNumber2);
        CheckBox sendOnPressEditor = (CheckBox) findViewById(R.id.sendOnPress);
        EditText tagEditor = (EditText) findViewById(R.id.quickTextTag);
        CheckBox locationEditor = (CheckBox) findViewById(R.id.showLocation);
//        if (!PhoneNumberUtils.isWellFormedSmsAddress(recipientEditor.getText().toString())) {
//            Log.d("MAKING SHIT", "address invalid:" + recipientEditor.toString());
//        }
//        if (messageEditor.toString().length() < 1) {
//            Log.d("MAKING SHIT", "msg too short:" + messageEditor.getText().toString());
//        }

        SharedPreferences sharedPreferences = getSharedPreferences(
                "QuickTextPreferences"+threadId, MODE_PRIVATE);
        QuickTextItem quickTextItem = new QuickTextItem(quickTextNumber, new ArrayList<String>(),
                0, messageEditor.getText().toString(), null,
                null, tagEditor.getText().toString(), sendOnPressEditor.isChecked(),
                locationEditor.isChecked(), nameEditor.getText().toString(), sharedPreferences);
        int numQuickTexts = sharedPreferences.getInt("num_quick_texts", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("num_quick_texts", numQuickTexts + 1);
        editor.apply();
    }

    /**
     * Setups colors and calls other setup functions.
     */
    protected void setupColors(){
        super.setupColors();
        AllViewsColorChanger.changeAllEditTextColorInLayout(scrollView, fontColor);
        AllViewsColorChanger.changeAllEditTextHintsColorInLayout(scrollView, fontColor);
        AllViewsColorChanger.changeAllTextViewColorInLayout(scrollView, fontColor);
        AllViewsColorChanger.changeAllTextViewHintsColorInLayout(scrollView, fontColor);
        scrollView.setBackgroundColor(backgroundColor);
        setupActionBar();
    }

    /**
     * Initializes the action bar.
     */
    @SuppressWarnings("deprecation")
    private void setupActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.quick_text_thread_edit_activity_toolbar);
        setSupportActionBar(toolbar);
        CharSequence title;
            toolbar.setBackgroundColor(primaryColor);
            getWindow().setStatusBarColor(darkColor);
        title = "Quick Text " + (1+quickTextNumber);

        ActionBar actionBar = getSupportActionBar();

        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(fontColor), 0, text.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        // Below code is if we want to change the primaryColor of the up arrow.
//        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_menu_back);
//        upArrow.setColorFilter(ContextCompat.getColor(this, R.primaryColor.opaque_white_color),
//                PorterDuff.Mode.SRC_ATOP);
//        actionBar.setHomeAsUpIndicator(upArrow);
    }

}
