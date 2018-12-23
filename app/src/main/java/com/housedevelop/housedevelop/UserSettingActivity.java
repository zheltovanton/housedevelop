package com.housedevelop.housedevelop;

/**
 * Created by zheltov.aa on 06.03.2015.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;


public class UserSettingActivity extends PreferenceActivity  {
    @SuppressWarnings("deprecation")

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }




}
