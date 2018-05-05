package com.beckoningtech.fastandcustomizablesms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.klinker.android.send_message.ApnUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main menu. This activity pulls all older messages sent or received by us and displays the
 * last message sent or received in each thread, i.e., a list of conversation is shown.
 */
public class MainMenuActivityOlder extends ColoredCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback{
    public static boolean isActivityVisible;

    private static boolean isDefault = false;
    private static final int DEF_SMS_REQ = 1001;


    private Settings settings;

    private int colorPrimary;
    private int colorPrimaryDark;


    private static final int REQUEST_PERMISSIONS = 20;
    private ListView mListView;
    private MainMenuListAdapter mAdapter;
    private int selectedPosition = -1;

    private enum ContactMenu {global, contactSpecific}
    private ContactMenu whichContextMenu = ContactMenu.global;
    private Activity activity;

    // For handling drag events (May not be needed)
    private float origDownX;
    private float origDownY;
    private int origPosition;
    private boolean isLongClick;
    private boolean isDrag;
    private final float SCROLL_THRESHOLD = 10;
    //private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_older);
        mContext = this;
        activity = this;
        setupColors();

        // Setup the broadcast manager to update upon receiving a text message
        setupBroadcastFilter();
        initSettings();
        mListView = (ListView) findViewById(R.id.main_menu_list_view);
        setupListView();
        setupFab();
    }

    /**
     * Sets a filter up to listen for new mms and sms broadcasts sent from the listenining portions
     * of our app so we can update the main menu
     */
    private void setupBroadcastFilter() {
        IntentFilter textingIntentFilter = new IntentFilter();
        textingIntentFilter.addAction("mms-received-event");
        textingIntentFilter.addAction("sms-received-event");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                textingIntentFilter);
    }

    /**
     * Setups colors and calls functions that require colors to be setup.
     */
    protected void setupColors(){
        super.setupColors();
        AllContacts.setContentResolverAndResources(getContentResolver(), getResources());
        int backgroundColor = ContextCompat.getColor(this, R.color.grey_background_color);
        colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        findViewById(android.R.id.content).setBackgroundColor(backgroundColor);
        setupActionBar();

    }

    private void setupListView(){
        ArrayList<MainMenuMessageInfo> myDataset = createDataset();
        // Add the adapter to the ListView
        mAdapter = new MainMenuListAdapter(myDataset, mContext);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // TODO: Implement MMS, refactor to thread ID's
                MainMenuMessageInfo mainMenuMessageInfo1 = mAdapter.getItem(position);

                if(mainMenuMessageInfo1 != null) {
                    ContactContainer contactContainer = mainMenuMessageInfo1.getContactContainer();

                    Intent intent = new Intent(MainMenuActivityOlder.this, MessagesActivity.class);
                    intent.putExtra("PHONE", mainMenuMessageInfo1.getNumber());
                    intent.putExtra("THREAD_ID", mainMenuMessageInfo1.getThreadID());
                    startActivity(intent);

                    if (contactContainer != null) {
                        String phoneNumber = contactContainer.getContactNumber();
                        //AllContacts.getInstance().openContactsEditor(mContext, phoneNumber);
                    }
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent2, View v,
                                           int position, long id) {
                isLongClick = true;
                selectedPosition = position;
                whichContextMenu = MainMenuActivityOlder.ContactMenu.contactSpecific;

                return false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setupActionBar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu_toolbar);
        setSupportActionBar(myToolbar);
        CharSequence title= getResources().getString(R.string.title_activity_main_menu_older);

        ActionBar actionBar = getSupportActionBar();

        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(ColorsUtil.sTileFontColor), 0, text.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
    }

    private void setupFab(){
        FloatingActionButton newConversationButton =
                (FloatingActionButton) findViewById(R.id.main_menu_activity_new_conversation_fab);
        newConversationButton.setBackgroundColor(colorPrimary);
        newConversationButton.setBackgroundTintList(ColorStateList.valueOf(colorPrimary));
        newConversationButton.setColorFilter(ColorsUtil.sTileFontColor, PorterDuff.Mode.SRC_ATOP);
        newConversationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivityOlder.this,
                        NewConversationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;

        final String myPackageName = getPackageName();

        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
            b.setTitle(R.string.not_default_app);
            b.setMessage(R.string.not_default_app_message);
            b.setNegativeButton(android.R.string.cancel, null);
            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(final DialogInterface dialogInterface, final int i) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                            BuildConfig.APPLICATION_ID);
                    startActivityForResult(intent,DEF_SMS_REQ);
                }
            });
            b.show();
        } else {
            isDefault = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case DEF_SMS_REQ:
                isDefault = resultCode == Activity.RESULT_OK;
        }
    }

    /**
     * Is our app the default app
     *
     * @return boolean about whether our app is default or not
     */
    public static boolean isDefault() {
        return isDefault;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_action_bar_items, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Override from Activity.class
     * The standard options menu behavior is controlled here
     * @param item menuItem
     * @return returns true so the menu is displayed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.setting:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                this.startActivity(intent1);
                //finish();
                break;
        }
        return true;
    }

    /**
     * Context menu behavior when we do a long press on main menu items
     * @param menu a context menu
     * @param v View that is being held
     * @param menuInfo info about the menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (whichContextMenu) {
            case contactSpecific:
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.main_menu_long_click_listview, menu);
                break;
            case global:
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_long_click_edit_contacts:
                MainMenuMessageInfo mainMenuMessageInfo1 = mAdapter.getItem(selectedPosition);
                if(mainMenuMessageInfo1 != null) {
                    String phoneNumber = mainMenuMessageInfo1.getNumber();
                    AllContacts.getInstance().openContactsEditor(mContext, phoneNumber);
                } else {
                    // This should ideally never happen. I am not sure of a case where it would
                    // happen unless something in memory gets messed up.
                    Toast.makeText(this, "Error with finding contact.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_menu_long_click_settings:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                this.startActivity(intent1);
                break;
        }
        return true;
    }

    private String getMMSSenderNumber(String id) {
        Cursor cursor = getContentResolver().query(Uri.parse("content://mms/"+id+"/addr"), null, null, null, null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("address"));
            }
        }
        return "";
    }

    /**
     * creates a dataset when called that contains information about number, sms text, time and date
     * Stores everything in a mainMenuMessageInfo
     *
     * WARNING: Do not break into functions as this will cause a big performance hit.
     *
     * @return An ArrayList of MainMenuMessageInfo that should go into the UI Main menu
     */
    public ArrayList<MainMenuMessageInfo> createDataset() {

        HashMap<String, ContactContainer> contactMap =
                AllContacts.getContactMap(getContentResolver(),getResources());

        // Make sure we only show each person once
        HashMap<String, MainMenuMessageInfo> threadIDMap = new HashMap<String, MainMenuMessageInfo>();

        // Problem: MMS and SMS have different inboxes
        // Details: If MMS and SMS have same sender, we only want the most recent message between em
        // The final dataset must be ordered from last messages
        // Algorithm:
        // Go through both the cursors at the same time
        // if part of hashset already, iterate the cursor
        // when cursors are at reset, compare the timings
        // if MMS last message was received first, add to data set and move mms cursor
        // else SMS was received first or at the same time and should be added to data
        // set and have its cursor moved


        // Dataset
        ArrayList<MainMenuMessageInfo> myDataset = new ArrayList<>();

        // SMS cursor
        Cursor cursorSMS = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
        // MMS cursor
        Cursor cursorMMS = getContentResolver().query(Uri.parse("content://mms"), null, null, null, null);

        String threadIDSMS = null;
        String threadIDMMS = null;
        boolean atLeastOneSMS = false;
        boolean atLeastOneMMS = false;

        // main menu info
        MainMenuMessageInfo mainMenuMessageInfo;
        String senderNumber;
        String textBody;
        String dateTime;
        String strippedNumber;

        String dateTimeSMS = "0";
        String dateTimeMMS = "0";

        // attempt to move both the cursors to the start
        if (cursorSMS != null) {
            atLeastOneSMS = cursorSMS.moveToFirst();
        }
        if (cursorMMS!=null) {
            atLeastOneMMS = cursorMMS.moveToFirst();
        }

        boolean notAtLastSMSMMS = true;
        while(notAtLastSMSMMS) {

            boolean cursorsUpdated = false;

            // Loop to the next valid threadID for SMS
            boolean validIDNotFound = true;
            while (validIDNotFound && atLeastOneSMS) {
                // if the thread id is already part of the hashset move the cursor
                // If getString throws exception the thread somehow doesn't have an ID.
                // This should not happen in standard texting applications.
                try {
                    threadIDSMS = cursorSMS.getString(cursorSMS.getColumnIndex("thread_id"));
                } catch (Exception e) {
                    atLeastOneSMS = cursorSMS.moveToNext();
                    if (!atLeastOneSMS) {
                        cursorSMS.close();
                    }
                    // If there is no thread id we must go to th next cursor location.
                    continue;
                }
                // A thread ID has been found and we are now going to see if the hashset
                // contains the thread id already. if it does, mvoe the cursor again.
                // If the hashset doesn't contain the threadid, we are good to use this threadid
                // in the main menu list
                if (threadIDMap.containsKey(threadIDSMS)) {
                    atLeastOneSMS = cursorSMS.moveToNext();
                    if (!atLeastOneSMS) {
                        cursorSMS.close();
                    }
                } else {
                    validIDNotFound = false;
                }

            }
            validIDNotFound = true;
            while (validIDNotFound && atLeastOneMMS) {
                try {
                    threadIDMMS = cursorMMS.getString(cursorMMS.getColumnIndex("thread_id"));
                } catch (Exception e) {
                    atLeastOneMMS = cursorMMS.moveToNext();
                    if (!atLeastOneMMS) {
                        cursorMMS.close();
                    }
                    continue;
                }
                if (threadIDMap.containsKey(threadIDMMS)) {

                    MainMenuMessageInfo curMessageInfo = threadIDMap.get(threadIDMMS);
                    atLeastOneMMS = cursorMMS.moveToNext();
                    if (!atLeastOneMMS) {
                        cursorMMS.close();
                    }
                } else {
                    validIDNotFound = false;
                }

            }

            // If neither cursor is on something, break out
            if (!atLeastOneMMS && !atLeastOneSMS) {
                break;
            }

            // compare the times and whichever is sooner, add it in to the data set
            boolean SMSMoreRecentOrEqual;
            if(!atLeastOneSMS) {
                SMSMoreRecentOrEqual = false;
                dateTimeMMS = cursorMMS.getString(cursorMMS.getColumnIndex("date"));
            } else if(!atLeastOneMMS) {
                SMSMoreRecentOrEqual = true;
                dateTimeSMS = cursorSMS.getString(cursorSMS.getColumnIndex("date"));
            } else {
                dateTimeSMS = cursorSMS.getString(cursorSMS.getColumnIndex("date"));
                dateTimeMMS = cursorMMS.getString(cursorMMS.getColumnIndex("date"))+"000";
                SMSMoreRecentOrEqual = (Long.parseLong(dateTimeSMS) >= Long.parseLong(dateTimeMMS));
            }
            mainMenuMessageInfo = new MainMenuMessageInfo();
            if (SMSMoreRecentOrEqual) {
                // Solve for data
                senderNumber = cursorSMS.getString(cursorSMS.getColumnIndex("address"));
                textBody = cursorSMS.getString(cursorSMS.getColumnIndex("body"));
                dateTime = dateTimeSMS;
                strippedNumber = AllContacts.stripNumber(senderNumber);

                // Store the info in a MainMenuMessageInfo
                mainMenuMessageInfo = new MainMenuMessageInfo();
                mainMenuMessageInfo.setRelativeTime(DateUtils.getRelativeTimeSpanString(Long.parseLong(dateTime), (System.currentTimeMillis() / 1000L), DateUtils.MINUTE_IN_MILLIS).toString());
                mainMenuMessageInfo.setUnixTime(dateTime);
                mainMenuMessageInfo.setLastMessage(textBody);
                mainMenuMessageInfo.setNumber(strippedNumber);
                mainMenuMessageInfo.setThreadID(threadIDSMS);

                // Determine if we have the person added as a contact
                if (contactMap != null) {
                    ContactContainer contactContainer = contactMap.get(AllContacts.stripNumber(senderNumber));
                    if (contactContainer != null) {
                        mainMenuMessageInfo.setContactContainer(contactContainer);
                    } else {
                        mainMenuMessageInfo.setContactContainer(
                                new ContactContainer(AllContacts.stripNumber(senderNumber)));
                    }
                }

                // Add to the hashset
                threadIDMap.put(threadIDSMS,mainMenuMessageInfo);

                // Increment the cursor
                atLeastOneSMS = cursorSMS.moveToNext();
                if(!atLeastOneSMS) {
                    cursorSMS.close();
                }


            } else {

                MMSContainer mmsContainer = new MMSContainer(cursorMMS.getString(cursorMMS.getColumnIndex("_id")), mContext, getResources());

                // Store the info in a MainMenuMessageInfo
                mainMenuMessageInfo = new MainMenuMessageInfo();
                mainMenuMessageInfo.setNumberSetMMS(MMSReader.getMMSSenderNumberArray(cursorMMS.getString(cursorMMS.getColumnIndex("_id")), mContext));
                mainMenuMessageInfo.setRelativeTime(DateUtils.getRelativeTimeSpanString(Long.parseLong(dateTimeMMS), (System.currentTimeMillis() / 1000L), DateUtils.MINUTE_IN_MILLIS).toString());
                mainMenuMessageInfo.setUnixTime(dateTimeMMS);
                mainMenuMessageInfo.setLastMessage("mmstestmessage");
                mainMenuMessageInfo.setThreadID(threadIDMMS);
                mainMenuMessageInfo.setMMSContainer(mmsContainer);

                // Add to the hashset
                threadIDMap.put(threadIDMMS, mainMenuMessageInfo);

                // Increment the cursor
                atLeastOneMMS = cursorMMS.moveToNext();
                if(!atLeastOneMMS) {
                    cursorMMS.close();
                }
            }
            // Add the info to the dataset
            myDataset.add(mainMenuMessageInfo);
        }
        return myDataset;
    }

    // updateDataset()
    // tells the updater to update to the new dataset

    /**
     * Updates the dataset for the main menu in an AsyncTask
     */
    public void updateDataset() {
        new updateTask(this).execute("");
    }

    /**
     * AsyncTask to update the main menu's dataset
     */
    private static class updateTask extends AsyncTask<String, Void, String> {
        private WeakReference<MainMenuActivityOlder> activityReference;

        updateTask(MainMenuActivityOlder context) {
            activityReference = new WeakReference<MainMenuActivityOlder>(context);

        }
        private ArrayList<MainMenuMessageInfo> newDataset;
        @Override
        protected String doInBackground(String... params) {
            MainMenuActivityOlder activity = activityReference.get();
            newDataset = activity.createDataset();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            MainMenuActivityOlder activity = activityReference.get();
            // Run on ui thread
            activity.mAdapter.updateDataset(newDataset);
        }
    }

    /**
     * a broadcast receiver that listens for when we receive SMS or MMS and makes toasts
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            switch(intent.getAction()){
                case "sms-received-event":
                    updateDataset();
                    break;
                case "mms-received-event":
                    updateDataset();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    /**
     * From klinker under Aoache 2.0 codebase to handle MMS
     */
    private void initSettings() {
        settings = Settings.get(this);

        if (TextUtils.isEmpty(settings.getMmsc()) &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initApns();
        }
    }

    public void onPause()
    {
        super.onPause();
        isActivityVisible = false;
    }
    /**
     * From klinker under Aoache 2.0 codebase to handle MMS
     */
    private void initApns() {
        ApnUtils.initDefaultApns(this, new ApnUtils.OnApnFinishedListener() {
            @Override
            public void onFinished() {
                settings = Settings.get(MainMenuActivityOlder.this, true);
            }
        });
    }
    /**
     * Minimizes the app on backpress
     */
    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

}
