package com.beckoningtech.fastandcustomizablesms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Dummy class to launch the main menu and initialize async tasks after requesting permissions (if
 * needed).
 */
public class MainActivity extends AppCompatActivity {

    private boolean cannotReadSms = true;
    private boolean cannotReadContacts = true;
    private boolean cannotWriteExternalStorage = true;
    private boolean cannotReadLocationState = true;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;

    private Button exitButton;
    private Button grantPermissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exitButton = (Button) findViewById(R.id.main_activity_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        grantPermissionButton = (Button) findViewById(R.id.main_activity_ask_permission);
        grantPermissionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestPermissions();
            }
        });

        checkPermissions();
    }

    /**
     * Setup flags to determine if permissions has been granted
     */
    private void checkPermissions(){
        cannotReadSms = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED;
        cannotReadContacts = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED;
        cannotWriteExternalStorage = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        cannotReadLocationState = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        if(cannotReadSms){
            findViewById(R.id.main_activity_sms).setVisibility(View.VISIBLE);
            return;
        }
        if(cannotReadContacts) {
            findViewById(R.id.main_activity_contact).setVisibility(View.VISIBLE);
            return;
        }
        if(cannotWriteExternalStorage){
            findViewById(R.id.main_activity_storage).setVisibility(View.VISIBLE);
            return;
        }
        if(cannotReadLocationState){
            findViewById(R.id.main_activity_location).setVisibility(View.VISIBLE);
            return;
        }
        startMainMenuActivity();
    }

    /**
     * Actually request permissions.
     */
    private void requestPermissions(){
        if(cannotReadSms){
            requestSMS();
            return;
        }
        if(cannotReadContacts) {
            requestContacts();
            return;
        }
        if(cannotWriteExternalStorage){
            requestStorage();
            return;
        }
        if(cannotReadLocationState){
            requestLocationState();
            return;
        }
        startMainMenuActivity();
    }

    private void startMainMenuActivity(){
        Intent intent2 = new Intent(this, MainMenuActivityOlder.class);
        startActivity(intent2);
    }

    private void setGrantPermissionButtonToGoToSettings(){
        grantPermissionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        grantPermissionButton.setText("SETTINGS");
    }

    private void requestSMS(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.SEND_SMS)) {
            setGrantPermissionButtonToGoToSettings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    private void requestContacts(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            setGrantPermissionButtonToGoToSettings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    private void requestStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            setGrantPermissionButtonToGoToSettings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestLocationState(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            setGrantPermissionButtonToGoToSettings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Sees if permission was granted or denied. If denied, ask again. If granted, move onto next
     * permission.
     * @param requestCode   Which permission was requested
     * @param permissions   Which permissions
     * @param grantResults  Results of the request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(grantResults.length > 0) {
            // If request is cancelled, the result arrays are empty.
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        findViewById(R.id.main_activity_sms).setVisibility(View.GONE);
                        findViewById(R.id.main_activity_contact).setVisibility(View.VISIBLE);
                        cannotReadSms = false;
                    }
                    break;
                }
                case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        cannotReadContacts = false;
                        findViewById(R.id.main_activity_contact).setVisibility(View.GONE);
                        findViewById(R.id.main_activity_storage).setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        findViewById(R.id.main_activity_storage).setVisibility(View.GONE);
                        findViewById(R.id.main_activity_location).setVisibility(View.VISIBLE);
                        cannotWriteExternalStorage = false;
                    }
                    break;
                }
                case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        cannotReadLocationState = false;
                        findViewById(R.id.main_activity_location).setVisibility(View.GONE);
                        exitButton.setVisibility(View.GONE);
                        grantPermissionButton.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
        requestPermissions();
    }


}
