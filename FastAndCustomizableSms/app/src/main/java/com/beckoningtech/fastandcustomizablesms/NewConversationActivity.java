package com.beckoningtech.fastandcustomizablesms;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Activity for making a new conversation.
 */
public class NewConversationActivity extends ColoredCompatActivity
        implements NewConversationActivityAdapterInterface {

    private RecyclerView contactsRecyclerView;
    private NewConversationActivityContactAdapter contactsAdapter;
    private RecyclerView.LayoutManager contactsLayoutManager;
    private int primaryColor;
    private int darkColor;
    private int fontColor;
    private int backgroundColor;
    private RecipientEditTextView recipientEditTextView;
    private BaseRecipientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);


        setupColors();
    }

    /**
     * Sets up the recycler view.
     */
    private void setupContactsRecyclerView(){
        contactsRecyclerView = (RecyclerView)
                findViewById(R.id.new_conversation_activity_recycler_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ArrayList<ContactContainer> contactContainers =
                AllContacts.getContactArrayList(getContentResolver(), getResources());
        Collections.sort(contactContainers, new ContactContainer.AlphabeticalComparator());

        contactsAdapter =
                new NewConversationActivityContactAdapter(contactContainers, getResources(),
                        fontColor, this);
        contactsRecyclerView.setAdapter(contactsAdapter);
        contactsLayoutManager = new LinearLayoutManager(this);
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);
    }

    private void setupRecipientEditTextView(){
        recipientEditTextView = (RecipientEditTextView)
                findViewById(R.id.new_conversation_activity_recipient_view);
        recipientEditTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        adapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);
        recipientEditTextView.setAdapter(adapter);
    }

    /**
     * Initializes colors and calls other initializations that relies on the colors being
     * set up properly.
     */
    protected void setupColors(){
        super.setupColors();
        primaryColor = ColorsUtil.getPrimaryColor();
        darkColor = ColorsUtil.getDarkPrimaryColor();
        fontColor = ColorsUtil.getFontColor();
        backgroundColor = ColorsUtil.getBackgroundColor();

        RelativeLayout relativeLayout = (RelativeLayout)
                findViewById(R.id.new_conversation_activity_relative_layout);
        relativeLayout.setBackgroundColor(backgroundColor);
        AllViewsColorChanger.
                changeAllTextViewAndEditTextHintAndFontColorInLayout(relativeLayout, fontColor);
        setupContactsRecyclerView();
        setupActionBar();
        setupRecipientEditTextView();
        setupButton();
    }

    /**
     * Sets up the start conversation button.
     */
    private void setupButton(){
        ImageButton imageButton = (ImageButton)
                findViewById(R.id.new_conversation_activity_start_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] phoneNumbers = getNumbersArrayFromRecipientChips();
                if(phoneNumbers.length > 0) {
                    String threadId = checkForThreadId(phoneNumbers);
                    Intent intent = new Intent(NewConversationActivity.this,
                            MessagesActivity.class);
                    intent.putExtra("PHONE", phoneNumbers[0]);
                    intent.putExtra("PHONE_NUMBERS", phoneNumbers);
                    intent.putExtra("THREAD_ID", threadId);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Initializes the action bar.
     */
    @SuppressWarnings("deprecation")
    private void setupActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_conversation_activity_toolbar);
        setSupportActionBar(toolbar);
        CharSequence title;
        title = getString(R.string.new_conversation_activity_title);
        toolbar.setBackgroundColor(primaryColor);
        getWindow().setStatusBarColor(darkColor);

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

    /**
     * Returns a string array of phone numbers found in the recipient chips.
     * Note that this is only public for testing. At the moment, there is no reason to call this
     * function outside of the activity except for testing.
     * @return String Array of phone numbers
     */
    public String[] getNumbersArrayFromRecipientChips(){
        DrawableRecipientChip[] chips = recipientEditTextView.getSortedRecipients();
        String[] phoneNumbers = new String[chips.length];
        for (int i = 0; i < chips.length; i++) {
            phoneNumbers[i] = AllContacts.stripNumber(chips[i].getEntry().getDestination());
        }
        return phoneNumbers;
    }

    /**
     * Checks for a thread ID given the chips in the RecipientEditTextView.
     * @return a thread ID, if found; "" if not
     */
    private String checkForThreadId(String[] numbers){
        Uri THREAD_ID_CONTENT_URI = Uri.parse(
                "content://mms-sms/threadID");
        final Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();
        String[] ID_PROJECTION = { BaseColumns._ID };
        for (String recipient : numbers) {
//            if (isEmailAddress(recipient)) {
//                recipient = extractAddrSpec(recipient);
//            }

            uriBuilder.appendQueryParameter("recipient", recipient);
        }
        final Uri uri = uriBuilder.build();
        final Cursor cursor = getContentResolver().query(
                uri, ID_PROJECTION, null, null, null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                String threadId = cursor.getString(cursor.getColumnIndex("_id"));
                Log.d("NewConversationThreadId", threadId);
                return threadId;
            }
        }
        return "";
    }


    /**
     * Adds the phone number to the search bar. Called when user clicks on a contact.
     * @param contactContainer ContactContainer to be displayed.
     */
    public void addNumberToSearchBar(ContactContainer contactContainer){
        recipientEditTextView.appendRecipientEntry(contactContainer.getRecipientEntry());
    }

    /**
     * Returns an ArrayList of ContactContainers from numbers found in the chips.
     * @return ArrayList of ContactContainers
     */
    private ArrayList<ContactContainer> getContactContainersFromChips(){
        ArrayList <ContactContainer> contactContainers = new ArrayList<>();
        DrawableRecipientChip[] chips = recipientEditTextView.getSortedRecipients();
        for (DrawableRecipientChip chip : chips) {
            String phoneNumber = AllContacts.stripNumber(chip.getEntry().getDestination());
            contactContainers.add(AllContacts.getContactContainer(phoneNumber));
        }
        return contactContainers;
    }

    /**
     * Sets up the recycler view with fake data.
     */
    public void setupFakeContactsRecyclerView(ArrayList<ContactContainer> contactContainers){
        contactsRecyclerView = (RecyclerView)
                findViewById(R.id.new_conversation_activity_recycler_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Collections.sort(contactContainers, new ContactContainer.AlphabeticalComparator());

        contactsAdapter =
                new NewConversationActivityContactAdapter(contactContainers, getResources(),
                        fontColor, this);
        contactsRecyclerView.setAdapter(contactsAdapter);
        contactsLayoutManager = new LinearLayoutManager(this);
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);
    }

}
