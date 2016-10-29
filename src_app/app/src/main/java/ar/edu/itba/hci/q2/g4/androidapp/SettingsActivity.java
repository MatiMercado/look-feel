package ar.edu.itba.hci.q2.g4.androidapp;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;
import java.util.Locale;

import ar.edu.itba.hci.q2.g4.androidapp.R.string;
import ar.edu.itba.hci.q2.g4.androidapp.R.xml;

import static android.preference.Preference.*;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupActionBar();
        //this.setDefaultLanguage();
    }

    /**
     * Set up the {@link ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // this is item ID of that back part which you want
            case android.R.id.home:
                // Control will come here when back/or app icon is pressed
                Intent parentActivityIntent = new Intent(this, Main.class);
                parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return SettingsActivity.isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        this.loadHeadersFromResource(xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.d("__HDEBUG", "Default Listener");
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            }else{
                preference.setSummary(stringValue);
            }
           /* else if(preference instanceof SwitchPreference){
                if(((SwitchPreference) preference).isChecked()){
                    Context c = getApplicationContext();
                    //BootReceiver.setAlarm(getApplicationContext());
                }
            }*/
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(SettingsActivity.sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        SettingsActivity.sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || SettingsActivity.GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || SettingsActivity.DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || SettingsActivity.NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(xml.pref_general);
            this.setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            SettingsActivity.bindPreferenceSummaryToValue(this.findPreference("example_text"));
            SettingsActivity.bindPreferenceSummaryToValue(this.findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
          /*  if (id == id.home) {
                this.startActivity(new Intent(this.getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);*/
            return true;
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(xml.pref_notification);
            this.setHasOptionsMenu(true);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_notification, false);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //SettingsActivity.bindPreferenceSummaryToValue(this.findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.switch_notifications) {
                this.startActivity(new Intent(this.getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            //Preference pref = findPreference(getString(R.ke));
            //Preference pref = findPreference(getString(string.pref_header_notifications));
            Preference pref = this.findPreference("notifications_new_message");
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // do whatever you want with new value

                    // true to update the state of the Preference with the new value
                    // in case you want to disallow the change return false
                    if(newValue.equals(true)){
                        Log.d("__HDEBUG", "SETTINGS: TURNING NOTIFICATIONS ON");
                        User.getInstance().setNotications();
                    }
                    else{
                        Log.d("__HDEBUG", "SETTINGS: TURNING NOTIFICATIONS OFF");
                        User.getInstance().clearNotifications();
                    }

                    return true;
                }
            });
           // getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener();
            Log.d("__HDEBUG", "Settings: OnResume");
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            Log.d("__HDEBUG", "Settings: OnPause");
            //getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        ////////////////////////////////
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(xml.pref_change_language);
            this.setHasOptionsMenu(true);

            this.getPreferenceScreen().setSummary("deafult");
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            SettingsActivity.bindPreferenceSummaryToValue(this.findPreference("listpref"));
            final Preference pref = findPreference("listpref");
            pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (Integer.parseInt((String) newValue) == 1){
                        setLocale("es");
                        // getPreferenceScreen().setSummary("Español");
                    }else{
                        setLocale("en");
                        //getPreferenceScreen().setSummary("English");
                    }


                    return false;
                }
            });

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        public void setLocale(String lan) {
            Locale locale = new Locale(lan);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getActivity().getFragmentManager().popBackStack();

            getResources().updateConfiguration(config, null);
            Intent refresh = new Intent(getActivity(), Main.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().finish();
            startActivity(refresh);

        }
    }




}

