package com.deckerth.thomas.eggservice.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.firebase.DataManagement;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;
import com.deckerth.thomas.eggservice.settings.Settings;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String DISPLAY_START_AVTIVITY = "display_start_activity";
    public static final String TIMING_SOFT = "timing_soft";
    public static final String TIMING_MEDIUM = "timing_medium";
    public static final String TIMING_HARD = "timing_hard";
    public static final String FIREBASE_CONNECTION = "firebase_connection";
    public static final String GROUP_SETUP = "group_setup";
    public static final String GROUP_NAME = "group_name";
    public static final String PENDING_GROUP_NAME = "pending_group_name";
    public static final String SIGN_IN_METHOD = "sign_in_method";
    public static final String SOUND_THEME = "sound_theme";
    public static final String CUSTOM_THEMES = "custom_themes";
    public static final String THEMES_FOR_RANDOM_ALARM = "themes_for_rnd_alarm";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

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

            } else if (preference.getKey().contentEquals(FIREBASE_CONNECTION)) {
                if (DataManagement.getInstance().isConnectedToFirebase(false)) {
                    preference.setSummary(R.string.pref_value_connected);
                } else {
                    preference.setSummary(R.string.pref_value_not_connected);
                }
            } else if (preference.getKey().contentEquals(GROUP_SETUP)) {
                if (DataManagement.getInstance().isConnectedToFirebase(false)) {
                    preference.setSummary(DataManagement.getInstance().getStateText());
                } else {
                    preference.setSummary(R.string.pref_value_not_connected);
                }
            } else if (preference.getKey().contentEquals(SOUND_THEME)) {
                preference.setSummary(ThemeRepository.Current.getTheme(stringValue).mName);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

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
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AccountPreferenceFragment.class.getName().equals(fragmentName)
                || TimingPreferenceFragment.class.getName().equals(fragmentName)
                || ThemePreferenceFragment.class.getName().equals(fragmentName);
    }


    /**
     * This fragment shows account preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {

        public static AccountPreferenceFragment Current;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Current = this;
            addPreferencesFromResource(R.xml.pref_account);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            Preference connection = findPreference(FIREBASE_CONNECTION);
            connection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (DataManagement.getInstance().isConnectedToFirebase(false)) {
                        Settings.SignInMethod method = Settings.Current.getSignInMethod(BasicApp.getContext());
                        if (method == Settings.SignInMethod.GOOGLE) {
                            startActivity(GoogleAccountActivity.getIntent(BasicApp.getContext()));
                        } else if (method == Settings.SignInMethod.EMAIL) {
                            startActivity(EmailPasswordAccountActivity.getIntent(BasicApp.getContext()));
                        }
                    } else {
                        startActivity(LoginActivity.getIntent(BasicApp.getContext()));
                    }
                    return true;
                }
            });

            bindPreferenceSummaryToValue(connection);

            Preference groups = findPreference(GROUP_SETUP);
            groups.setSelectable(DataManagement.getInstance().isConnectedToFirebase(false));
            bindPreferenceSummaryToValue(groups);
        }

        public void updateSummary(String key) {
            Preference groups = findPreference(GROUP_SETUP);
            groups.setSelectable(DataManagement.getInstance().isConnectedToFirebase(false));
            bindPreferenceSummaryToValue(findPreference(key));
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
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ThemePreferenceFragment extends PreferenceFragment {

        public static ThemePreferenceFragment Current;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Current = this;
            addPreferencesFromResource(R.xml.pref_theme);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference(SOUND_THEME));
        }

        public void updateSummary(String key) {
            bindPreferenceSummaryToValue(findPreference(key));
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
    }

    /**
     * This fragment shows timing preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TimingPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_timing);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(TIMING_SOFT));
            bindPreferenceSummaryToValue(findPreference(TIMING_MEDIUM));
            bindPreferenceSummaryToValue(findPreference(TIMING_HARD));
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
    }
}
