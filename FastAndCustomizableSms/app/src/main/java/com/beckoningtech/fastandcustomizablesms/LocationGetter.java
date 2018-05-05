package com.beckoningtech.fastandcustomizablesms;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 12/3/17.
 */

public class LocationGetter {

    private double latitude;
    private double longitude;
    private String location;
    private Activity mActivity;

    private FusedLocationProviderClient mFusedLocationClient;

    public LocationGetter(Activity activity){
        mActivity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
    }

    public void updateLocation(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("LOCATION:", "success");
                        if (location != null) {
                            Log.d("LOCATION:", "doing somthieng");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.d("LOCATION:", latitude + ";" + longitude);
                        }
                    }
                });
    }

    public String getLocation() {

        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.size()<1){
            return location==null?"":location;
        }
        Address address = addresses.get(0);
        ArrayList<String> addressFragments = new ArrayList<>();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        Log.d("LOCATION:", TextUtils.join(System.getProperty("line.separator"), addressFragments));
        return TextUtils.join(System.getProperty("line.separator"), addressFragments);
    }

}
