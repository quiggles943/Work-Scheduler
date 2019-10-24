package com.quigglesproductions.paulq.calendartest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by paulq on 14/10/2019.
 */

public class Settings extends AppActivity {
    static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getLayoutInflater().inflate(R.layout.content_settings, layout);
        context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        layout.removeView(fab);
        if(getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction().replace(R.id.settingsList, new SettingsFragment()).commit();
        }

    }

    public static class SettingsFragment extends PreferenceFragment implements AdapterView.OnItemLongClickListener {
        private ArrayList<Preference> mPreferences = new ArrayList<>();
        private String[] mPreferenceKeys = new String[] {"account_name","account_type","owner_account", "version_Info"};
        private SharedPreferences.OnSharedPreferenceChangeListener mListener;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    for (Preference pref : mPreferences) {
                        if (pref.getKey().equals(key)) {
                            if (pref instanceof EditTextPreference) {
                                pref.setSummary(sharedPreferences.getString(key, "The URL for the calendar used"));
                            }

                            //break;
                        }

                    }
                }
            };
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            prefs.registerOnSharedPreferenceChangeListener(mListener);
            for (String prefKey : mPreferenceKeys) {
                Preference pref = (Preference) getPreferenceManager().findPreference(prefKey);
                mPreferences.add(pref);
                mListener.onSharedPreferenceChanged(prefs, prefKey);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            if(view != null)
            {
                View lv = view.findViewById (android.R.id.list);
                if (lv instanceof ListView)
                {
                    ((ListView)lv).setOnItemLongClickListener(this);
                }
                else
                {
                    //The view created is not a list view!
                }
            }
            //view.setBackgroundResource(R.drawable.background);
            return view;
        }

        @Override
        public void onResume()
        {
            super.onResume();
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            prefs.registerOnSharedPreferenceChangeListener(mListener);
            for (String prefKey : mPreferenceKeys) {
                Preference pref = (Preference) getPreferenceManager().findPreference(prefKey);
                mPreferences.add(pref);
                mListener.onSharedPreferenceChanged(prefs, prefKey);
            }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}