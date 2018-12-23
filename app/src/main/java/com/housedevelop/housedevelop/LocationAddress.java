package com.housedevelop.housedevelop;

/**
 * Created by zheltov.aa on 13.03.2015.
 */
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.content.res.Resources;

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String addresss = null;
                String region = null;
                String city = null;
                String country = null;

                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            if (i==0) addresss = address.getAddressLine(i);
                            if (i==1) city = address.getAddressLine(i);
                            if (i==2) region = address.getAddressLine(i);
                            if (i==3) country = address.getAddressLine(i);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (addresss != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", addresss);
                        bundle.putString("region", region);
                        bundle.putString("city", city);
                        bundle.putString("country", country);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        addresss = " ";
                        bundle.putString("address", addresss);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
