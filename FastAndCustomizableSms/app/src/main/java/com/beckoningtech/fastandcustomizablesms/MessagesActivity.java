package com.beckoningtech.fastandcustomizablesms;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This class is designed to display a texting screen where the user can
 * text messages to another person and will automaticaly update when the
 * user receives a text from that person.
 *
 * @author Ric Rodriguez
 */

public class MessagesActivity extends ColoredCompatActivity implements
        MessagesActivityQuickTextAdapter.AdapterCallback, MessagesActivityMessageAdder,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Button at bottom right for sending sms. Activates sendSMSOrMMS function
     */
    ImageButton sendSmsButton;

    /**
     * Button at bottom left for adding in MMS mimetypes like images gifs etc.
     */
    ImageButton extrasButton;

    /**
     * Text editor for use inputted message to send.
     */
    EditText sendMessageEditText;
    private RecyclerView messagesRecyclerView;
    private MessagesActivityMessagesRecyclerAdapter messagesAdapter;
    private RecyclerView.LayoutManager messagesLayoutManager;
    private GoogleApiClient mGoogleApiClient;
    private double latitude;
    private double longitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private Set<String> allRecipients;
    private ArrayList<ContactContainer> allContactContainers;
    private static MessagesActivityMessageAdder messageAdder;

    private static String sender;
    /**
     * Used when onPause gets called and onResume will reset sender to this.
     */
    private static String prevSender;
    private String activityTitle;
    String threadId;
    private ThreadInfoContainer threadInfoContainer;

    public static boolean isActivityVisible;
    private static final String TAG = "MESSAGES_ACTIVITY:";

    private ContactContainer contactContainer;

    private HorizontalGridView quickTextRecyclerView;
    private RecyclerView.Adapter quickTextAdapter;

    private int primaryColor;
    private int darkColor;
    private int fontColor;
    private int backgroundColor;
    private LocationGetter mLocationGetter;

    private final int EDIT_QUICK_TEXT_REQUEST = 1;

    private static String curPhoneNumber;
    private static String curThreadId;

    private boolean isTest = false;

    /**
     * Creates the user interface for viewing text messages and quicktexts.
     * DO NOT SPLIT INTO SMALLER FUNCTIONS. THERE ARE ALREADY PERFORMANCE ISSUES HERE.
     *
     * @param savedInstanceState Android default.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        sendMessageEditText = (EditText) findViewById(R.id.txtMessage);
        messagesRecyclerView = (RecyclerView) findViewById(R.id.messages_activity_recycler_view);
        mLocationGetter = new LocationGetter(this);
        mLocationGetter.updateLocation();
        allRecipients = new HashSet<>();
        String[] passedNumbers = getIntent().getStringArrayExtra("PHONE_NUMBERS");
        String num;
        if(passedNumbers != null){
            for(int i = 0; i <passedNumbers.length; i++){
                String number = AllContacts.stripNumber(passedNumbers[i]);
                if(number != null && !number.equals("")) {
                    allRecipients.add(number);
                }
            }
            num = passedNumbers[0];
            for(String tmpNum : passedNumbers){
                num += ", "+tmpNum;
            }
        } else {
            num = getIntent().getStringExtra("PHONE");
            if (num == null) {
                num = curPhoneNumber;
            } else {
                curPhoneNumber = num;
            }
        }
        threadId = getIntent().getStringExtra("THREAD_ID");
        if (threadId == null) {
            threadId = curThreadId;
        } else {
            curThreadId = threadId;
        }
        threadInfoContainer = new ThreadInfoContainer(threadId, new ArrayList<ContactContainer>());
        if(num == null && threadId == null){
            ColorsUtil.setupColors(this);
            AllContacts.getInstance(
                getContentResolver(), getResources());
            contactContainer = new ContactContainer("2134567890");
            // Log.d("MessagesActivityTest", "In Test");
            isTest = true;
            return;
        }
//        // Log.d("CHECKING VALIDITY:", num);
//        // Log.d("CHECKING VALIDITY:", PhoneNumberUtils.isWellFormedSmsAddress(num) ? "YES" : "NO");
//        // Log.d("CHECK:", PhoneNumberUtils.formatNumber(num, "US"));
        final String passed_number;

        passed_number = num.replaceAll("[^0-9]", "");
        ColorsUtil.setupColors(this);
        contactContainer = AllContacts.getContactMap(
                getContentResolver(), getResources()).get(
                AllContacts.stripNumber(PhoneNumberUtils.normalizeNumber(num)));
        if (contactContainer == null) {
            contactContainer = new ContactContainer(passed_number);
        }
        sender = passed_number;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        setupMessagesRecyclerView();
//        // Log.d("STRIPPERS:", PhoneNumberUtils.normalizeNumber("584432"));
//
//
//        // Log.d(TAG, "PASSED_NUMBER=" + passed_number);

        ArrayList<String> msgs = new ArrayList<>();
        Uri mSmsinboxQueryUri = Uri.parse("content://sms");
        Uri mMmsinboxQueryUri = Uri.parse("content://mms");
        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri,
                new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"},
                "thread_id=" + threadId, null, null);
        Cursor cursorMMS = getContentResolver().query(mMmsinboxQueryUri,
                null,
                "thread_id=" + threadId, null, null);
        String[] columns = new String[]{"thread_id", "address", "type", "date", "body", "type", "_id"};
        SMSThread smsThread = null;
        if (cursor1 != null && cursor1.getCount() > 0) {
            String body, date, number, id, person;
            smsThread = new SMSThread(threadId);
            // Log.d(TAG, "thread_id =" + threadId);
            // iterate the cursors through each message and add em all to the SMS thread
            if (cursor1 != null) {
                while (cursor1.moveToNext()) {
                    /*Log.v(TAG, cursor1.getString(cursor1.getColumnIndex("_id")) + ";" +
                            cursor1.getString(cursor1.getColumnIndex("thread_id")) + ";" +
                            cursor1.getString(cursor1.getColumnIndex("address")) + ";" +
                            cursor1.getString(cursor1.getColumnIndex("person")) + ";" +
                            cursor1.getString(cursor1.getColumnIndex("date")) + ";" +
                            cursor1.getString(cursor1.getColumnIndex("body")) + ";" +
                            cursor1.getString(cursor1.getColumnIndex("type")) + ";"
                    );*/
                    body = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                    date = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                    number = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                    // = number;

                    number = AllContacts.stripNumber(number);
                    if(number != null && !number.equals("")) {
                        allRecipients.add(number);
                    }
                    id = cursor1.getString(cursor1.getColumnIndex(columns[6]));
                    person = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                    if (smsThread != null) {
                        smsThread.addMessage(id, number, date, body, person);
                    }
                    //// Log.d(TAG, "Msg:" + id + ";" + date + ";" + number + ";" + body + ";" + person);
                }
                cursor1.close();
            }
        }

        // Add in MMS to the SMSMMS thread
        MMSContainer mmsContainer = null;
        if (cursorMMS != null && cursorMMS.getCount() > 0) {
            String body, date, number, id = null, person;
            if (smsThread == null) {
                smsThread = new SMSThread(threadId);
            }
            // iterate the cursors through each message and add em all to the SMS thread
            if (cursorMMS.moveToFirst()) {
                do {
                    id = cursorMMS.getString(cursorMMS.getColumnIndex("_id"));
                    if (id != null) {
                        // Log.d("MA", "id of mms to add to thread" + id);
                        mmsContainer = new MMSContainer(id, getApplicationContext(), getResources());
//                    // Log.d("MA", "Text: " + mmsContainer.text);
                        // Log.d("MA", "Conversationalists:" + mmsContainer.getSendersDisplayed());
                        date = mmsContainer.timeStamp;
                        number = ""; // TODO maybe give this functionality, as is SMSThread.Message.originNumber is an unused field, number and originNumber is ambiguous especially in the case of MMS
                        smsThread.addMessage(id, number, date, mmsContainer);
                        // Log.d("MA", "Added MMS!");
                        id = null;
                    }
                } while (cursorMMS.moveToNext());
                cursorMMS.close();
            }
        }
        if(mmsContainer != null) {
            allContactContainers = mmsContainer.getContactContainers();
            for(ContactContainer contactContainer : allContactContainers){
                String number = AllContacts.stripNumber(contactContainer.getContactNumber());
                if(number!=null && !number.equals("")) {
                    allRecipients.add(number);
                }
            }
        }
        setupColors();

        if (smsThread != null) {
            ArrayList<SMSThread.Message> msgsSmsthread = smsThread.getMessageThread();
            if (msgsSmsthread != null) {
                for (SMSThread.Message m : msgsSmsthread) {
                    // The only thing that matters is the stuff that gets added into
                    // messagesAdapter. messagesAdapter is where we can add stuff to the display
                    // In order to add something to the SMSThread so it will get added in here is to
                    // call SMSThread.addMessage()
                    MMSContainer curMessageMMSContainer = m.getMMSContainer();
                    if (m.getMMSContainer() != null) {
                        // Log.d("MA", "Text: " + curMessageMMSContainer.text);
                        messagesAdapter.add(curMessageMMSContainer);
                    } else {
                        messagesAdapter.add(new MessageContainer((m.getGravity()), m.toString()));
                    }
                }

                messagesRecyclerView.scrollToPosition(
                        messagesAdapter.getItemCount() - 1);


            }
        }
        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = sendMessageEditText.getText().toString();

                if (message.length() > 0)
                    sendSMSOrMMS(message, true, false);
                else
                    Toast.makeText(getBaseContext(),
                            "Please enter a message",
                            Toast.LENGTH_SHORT
                    ).show();
                sendMessageEditText.setText("");
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        prevSender = sender;

    }

    /**
     * Initializes the send button.
     */
    private void setupSendButton() {
        sendSmsButton = (ImageButton) findViewById(R.id.messages_activity_send_sms_button);

    }

    /**
     * Initializes the extra buttons.
     */
    private void setupExtrasButton() {
        extrasButton = (ImageButton) findViewById(
                R.id.messages_activity_more_texting_options_button);
    }

    /**
     * Sets up the recycler view.
     */
    private void setupMessagesRecyclerView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        messagesAdapter = new MessagesActivityMessagesRecyclerAdapter(new ArrayList<MessageContainer>(),
                getApplicationContext(), contactContainer, displayMetrics);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesLayoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(messagesLayoutManager);
    }

    /**
     * Initializes colors and calls other initializations that relies on the colors being
     * set up properly.
     */
    protected void setupColors() {
        super.setupColors();
        primaryColor = contactContainer.getColor();
        darkColor = contactContainer.getDarkColor();
        fontColor = ColorsUtil.getFontColor();
        backgroundColor = ColorsUtil.getBackgroundColor();
        setupSendButton();
        setupExtrasButton();
        setupQuickTextBar();
        setupActionBar();
        if (contactContainer != null) {
            quickTextRecyclerView.setBackgroundColor(primaryColor);
            extrasButton.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
            sendSmsButton.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
            ScrollableOverscrollColorChange.setOverscrollEdgeColor(messagesRecyclerView,
                    primaryColor);
            setTaskDescription(new ActivityManager.TaskDescription(activityTitle,
                    null, primaryColor));
        }
        sendMessageEditText.setTextColor(fontColor);
        sendMessageEditText.setHintTextColor(fontColor);
    }

    private void setupQuickTextBar() {
        QuickTextContainer quickTextContainer =
                new QuickTextContainer(this, this.threadId);
        quickTextAdapter =
                new MessagesActivityQuickTextAdapter(quickTextContainer.getQuickTextItems(),
                        this, threadInfoContainer, getSupportFragmentManager(),
                        quickTextContainer);
        quickTextRecyclerView = (HorizontalGridView)
                findViewById(R.id.messages_activity_quicktext_grid_view);
        quickTextRecyclerView.setAdapter(quickTextAdapter);
    }


    /**
     * Initializes the action bar.
     */
    @SuppressWarnings("deprecation")
    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_activity_toolbar);
        setSupportActionBar(toolbar);
        CharSequence title;
        if (contactContainer != null) {
            title = contactContainer.getContactName();
            toolbar.setBackgroundColor(primaryColor);
            getWindow().setStatusBarColor(darkColor);
        } else {
            title = allRecipients.toString();
        }
        if(allRecipients.size()>1){
            title = allRecipients.toString();
        }
        this.activityTitle = title.toString();

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
     * Inflates the option menu
     *
     * @param menu
     * @return ifCreated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messages_activity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Gets Current thread identifier of the conversation to pass to quicktext adapter
     *
     * @return String thread_id
     */
    public String getThreadId() {
        return this.threadId;
    }

    /**
     * Sends messages from any referable instance
     *
     * @param messageBody     SMS Message to send
     * @param sendOnPress Should the message be loaded in to the text editor or sent immmediately?
     */
    public void sendSMSOrMMS(String messageBody, boolean sendOnPress, boolean appendLocation) {
        if (!sendOnPress) {
            sendMessageEditText.setText(messageBody);
            return;
        }

        if (appendLocation) {
            messageBody += "\nSent from " + mLocationGetter.getLocation();
        }
        messagesAdapter.add(new MessageContainer(false, messageBody));
        messagesAdapter.notifyItemInserted(messagesAdapter.getItemCount() - 1);
        String[] recipientArray = allRecipients.toArray(new String[0]);

        if(recipientArray.length>1) {
            com.klinker.android.send_message.Settings settings = new
                    com.klinker.android.send_message.Settings();
            settings.setUseSystemSending(true);
            Transaction transaction = new Transaction(this, settings);
            Message message = new Message(messageBody, recipientArray);
            transaction.sendNewMessage(message, Long.parseLong(threadId));
        } else {
            PhoneUtils.sendSMS(messageBody, recipientArray[0], this);
        }

        sendMessageEditText.setText("");
        scrollToBottom();
        if(allRecipients.size()==1) {
            Intent mainMenuUpdateIntent = new Intent("sms-received-event");
            mainMenuUpdateIntent.putExtra("message", "This is my message!");
            LocalBroadcastManager.getInstance(
                    getApplicationContext()).sendBroadcast(mainMenuUpdateIntent);
        }
    }

    /**
     * Gets the phone number of the current instance
     *
     * @return string phoneNumber, formatted with a prepended 1
     * @deprecated No longer used since we changed to threads
     */
    public static String getPhoneNum() {
        return sender;
    }

    /**
     * Sets curMessageActivity to this.
     */
    public void onResume() {
        super.onResume();
        isActivityVisible = true;
        messageAdder = this;
        sender = prevSender;
    }

    /**
     * Sets curMessageActivity to null to prevent a memory leak.
     */
    public void onPause() {
        super.onPause();
        // Log.d("MessagesActivity", "onPause called");
        prevSender = sender;
        sender = "";
        isActivityVisible = false;
    }

    /**
     * Adds message to the messages recycler view.
     *
     * @param from Sender of the message
     * @param body Text of the message.
     */
    public void addMsg(String from, String body) {
        // Log.d("MessagesActivity", "adding sendMessageEditText=" + from + ";" + body);
        if (from.equalsIgnoreCase(getPhoneNum())) {
            // If statement is correct. Commented out as it does nothing atm.
            //int last = listView.getLastVisiblePosition();
            //if(last < mAdapter.getCount()){
            // User is currently scrolled up above the old item.
            // TODO: display a message to ask the user if they want to view the new message.
            //}
            messagesAdapter.add(new MessageContainer(true, body));
            messagesAdapter.notifyItemInserted(messagesAdapter.getItemCount() - 1);
            scrollToBottom();

        }
    }

    /**
     * Smoothly scroll the messageRecyclerView to the bottom.
     */
    public void scrollToBottom() {
        messagesRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                messagesRecyclerView.smoothScrollToPosition(
                        messagesAdapter.getItemCount() - 1);
            }
        });

    }

    /**
     * Returns the MessagesActivityMessageAdder of the current activity.
     *
     * @return MessagesActivityMessageAdder
     */
    public static MessagesActivityMessageAdder getMessageAdder() {
        return messageAdder;
    }

    /**
     * Opens a QuickTextThreadEditActivity.
     *
     * @param quickTextNumber Number of the quick text, i.e. position from left.
     */
    public void startQuickTextThreadEditActivity(int quickTextNumber) {
        Intent intent = new Intent(MessagesActivity.this,
                QuickTextThreadEditActivity.class);
        intent.putExtra("THREAD_ID", threadInfoContainer.getThreadId());
        intent.putExtra("quickTextNumber", quickTextNumber);
        intent.putExtra("primaryColor", primaryColor);
        intent.putExtra("darkColor", darkColor);
        intent.putExtra("quickTextNumber", quickTextNumber);
        intent.putExtra("backgroundColor", backgroundColor);
        startActivityForResult(intent, EDIT_QUICK_TEXT_REQUEST);
    }

    /**
     * Checks to see if there was a quicktext added or not to update ui
     *
     * @param requestCode code for requesting action
     * @param resultCode  resulting end code of activity
     * @param data        data passed back from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EDIT_QUICK_TEXT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
        // Log.d("MESSAGES_ACTIVITY", "at results");
        setupQuickTextBar();
    }

    /**
     * updates the quick texts
     */
    public void updateQuickTexts() {
        this.quickTextAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        return;
    }

    public void onConnected(Bundle arg0) {
        //getLocation();
        return;
    }

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /**
     * For testing purposes only. Fills the RecyclerView for messages with fake data.
     * @param fakeData the fake data to fill the RecyclerView with.
     */
    public void fillMessagesRecyclerViewWithFakeData(ArrayList<MessageContainer> fakeData){
        if(isTest) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            messagesAdapter = new MessagesActivityMessagesRecyclerAdapter(fakeData, this,
                    new ContactContainer("1234567890"), displayMetrics);
            sender = "1234567890";
            messagesRecyclerView.setAdapter(messagesAdapter);
            messagesLayoutManager = new LinearLayoutManager(this);
            messagesRecyclerView.setLayoutManager(messagesLayoutManager);
        }
    }

    /**
     * For testing purposes only. Returns the adapter associated with the RecyclerView for Messages.
     * @return Adapter for the recycler view. Note returns null if not a test.
     */
    public MessagesActivityMessagesRecyclerAdapter getMessagesAdapterForTest(){
        if(isTest) {
            return messagesAdapter;
        } else {
            return null;
        }
    }
    /**
     * For testing purposes only. Returns the RecyclerView for Messages.
     * @return RecyclerView for messages.
     */
    public RecyclerView getMessagesRecyclerViewForTest(){
        if(isTest) {
            return messagesRecyclerView;
        } else {
            return null;
        }
    }

    /**
     * For testing purposes only. Fills the RecyclerView for quick texts with fake data.
     * @param fakeData the fake data to fill the RecyclerView with.
     */
    public void fillQuickTextRecyclerViewWithFakeData(ArrayList<QuickTextItem> fakeData){
        if(isTest) {
            quickTextRecyclerView = (HorizontalGridView)
                    findViewById(R.id.messages_activity_quicktext_grid_view);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            quickTextAdapter = new MessagesActivityQuickTextAdapter(fakeData, this,
                    null, null, null);
            sender = "1234567890";
            quickTextRecyclerView.setAdapter(quickTextAdapter);
        }
    }

    /**
     * For testing purposes only. Returns the adapter associated with the RecyclerView for
     * quick texts.
     * @return Adapter for the recycler view. Note returns null if not a test.
     */
    public RecyclerView.Adapter getQuickTextAdapterForTest(){
        if(isTest) {
            return quickTextAdapter;
        } else {
            return null;
        }
    }
    /**
     * For testing purposes only. Returns the RecyclerView for quick texts.
     * @return HorizontalGridView for quick texts.
     */
    public HorizontalGridView getQuickTextRecyclerViewForTest(){
        if(isTest) {
            return quickTextRecyclerView;
        } else {
            return null;
        }
    }
}
