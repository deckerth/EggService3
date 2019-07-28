package com.deckerth.thomas.eggservice.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.ArraySet;

import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;
import com.deckerth.thomas.eggservice.views.MainActivity;
import com.deckerth.thomas.eggservice.views.SettingsActivity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class Settings {

    public static final Settings Current = new Settings();

    public static enum SignInMethod {
        GOOGLE("google"),
        EMAIL("email");

        private final String mSignInMethod;

        SignInMethod(String method) { this.mSignInMethod = method; }

        static SignInMethod fromString(String method) {
            if (method == null) {
                return null;
            } else {
                return SignInMethod.valueOf(method.toUpperCase(Locale.US));
            }
        }
    }

    public long getSoftTimingSeconds() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.Current.getApplicationContext());
        String timing_str = sharedPref.getString(SettingsActivity.TIMING_SOFT, "6");
        if (timing_str == null) return 0;
        try {
            return Long.parseLong(timing_str)*60;
        } catch (NumberFormatException ex) { return 0; }
    }

    public long getMediumTimingSeconds() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.Current.getApplicationContext());
        String timing_str = sharedPref.getString(SettingsActivity.TIMING_MEDIUM, "7");
        if (timing_str == null) return 0;
        try {
            return Long.parseLong(timing_str)*60;
        } catch (NumberFormatException ex) { return 0; }
    }

    public long getHardTimingSeconds() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.Current.getApplicationContext());
        String timing_str = sharedPref.getString(SettingsActivity.TIMING_HARD, "12");
        if (timing_str == null) return 0;
        try {
            return Long.parseLong(timing_str)*60;
        } catch (NumberFormatException ex) { return 0; }
    }

    public Boolean getDisplayStartDialog(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(SettingsActivity.DISPLAY_START_AVTIVITY, Boolean.TRUE);
    }

    public void setDisplayStartDialog(Context context, Boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putBoolean(SettingsActivity.DISPLAY_START_AVTIVITY, value).apply();
    }

    public String getGroupName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(SettingsActivity.GROUP_NAME, "");
    }

    public void setGroupName(Context context, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == null) {
            sharedPref.edit().remove(SettingsActivity.GROUP_NAME).apply();
        } else {
            sharedPref.edit().putString(SettingsActivity.GROUP_NAME, value).apply();
        }
        if ((SettingsActivity.AccountPreferenceFragment.Current != null)) {
            SettingsActivity.AccountPreferenceFragment.Current.updateSummary(SettingsActivity.GROUP_SETUP);
        }
    }

    public Integer getLastUsedSoundThemeIndex(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(SettingsActivity.LAST_PLAYED_SOUND_THEME_INDEX, -1);
    }

    public void setLastUsedSoundThemeIndex(Context context, Integer value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPref.edit().putInt(SettingsActivity.LAST_PLAYED_SOUND_THEME_INDEX, value).apply();
    }

    public String getPendingGroupName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(SettingsActivity.PENDING_GROUP_NAME, "");
    }

    public void setPendingGroupName(Context context, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == null) {
            sharedPref.edit().remove(SettingsActivity.PENDING_GROUP_NAME).apply();
        } else {
            sharedPref.edit().putString(SettingsActivity.PENDING_GROUP_NAME, value).apply();
        }
        if ((SettingsActivity.AccountPreferenceFragment.Current != null)) {
            SettingsActivity.AccountPreferenceFragment.Current.updateSummary(SettingsActivity.GROUP_SETUP);
        }
    }

    public SignInMethod getSignInMethod(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return SignInMethod.fromString(sharedPref.getString(SettingsActivity.SIGN_IN_METHOD,null));
    }

    public void setSignInMethod(Context context, SignInMethod value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == null) {
            sharedPref.edit().remove(SettingsActivity.SIGN_IN_METHOD).apply();
        } else {
            sharedPref.edit().putString(SettingsActivity.SIGN_IN_METHOD, value.toString()).apply();
        }
    }

    public SoundTheme getSoundTheme(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return ThemeRepository.Current.getTheme(sharedPref.getString(SettingsActivity.SOUND_THEME,"STANDARD"));
    }

    public void setSoundTheme(Context context, SoundTheme value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == null) {
            sharedPref.edit().remove(SettingsActivity.SOUND_THEME).apply();
        } else {
            sharedPref.edit().putString(SettingsActivity.SOUND_THEME, value.mId).apply();
        }
        if (SettingsActivity.ThemePreferenceFragment.Current != null) {
            SettingsActivity.ThemePreferenceFragment.Current.updateSummary(SettingsActivity.SOUND_THEME);
        }
    }

    public Set<String> getCustomThemes(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return  sharedPref.getStringSet(SettingsActivity.CUSTOM_THEMES, null);
    }

    public void setCustomThemes(Context context, Set<String> value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == null) {
            sharedPref.edit().remove(SettingsActivity.CUSTOM_THEMES).apply();
        } else {
            sharedPref.edit().putStringSet(SettingsActivity.CUSTOM_THEMES, value).apply();
        }
    }

    public Set<String> getThemesForRandomAlarm(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return  sharedPref.getStringSet(SettingsActivity.THEMES_FOR_RANDOM_ALARM, null);
    }

    public void setThemesForRandomAlarm(Context context, Set<String> value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == null) {
            sharedPref.edit().remove(SettingsActivity.THEMES_FOR_RANDOM_ALARM).apply();
        } else {
            sharedPref.edit().putStringSet(SettingsActivity.THEMES_FOR_RANDOM_ALARM, value).commit();
        }
    }

    public void setThemeRelevantForRandomAlarm(Context context, String themeId, boolean value) {
        Set<String> relevantThemes = getThemesForRandomAlarm(context);
        if (relevantThemes == null) {
            if (!value) {
                // create new list without the deselected one
                setThemesForRandomAlarm(context,ThemeRepository.Current.getThemesForRandomAlarm());
                return;
            }
        }

        // cloning here is essential as changes done directly in relevantThemes are
        // not correctly saved
        Set<String> updatedSet = new HashSet<>(relevantThemes);

        Iterator<String> iterator = relevantThemes.iterator();
        if (value) {
            // add theme
            while (iterator.hasNext()) {
                String current = iterator.next();
                if (current.contentEquals(themeId)) return; // Already in list
            }
            updatedSet.add(themeId);
            setThemesForRandomAlarm(context,updatedSet);
        } else {
            // remove theme
            Set<String> newSet = new ArraySet<>();
            boolean found = false;
            while (iterator.hasNext()) {
                String current = iterator.next();
                if (current.contentEquals(themeId)) {
                    found = true;
                } else {
                    newSet.add(current);
                }
            }
            if (found) setThemesForRandomAlarm(context,newSet);
        }
    }


    private String getGustoAlarmFilename(Context context, String theme, String gusto) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString("SoundFile_"+gusto+":"+theme,null);

    }

    private void setGustoAlarmFilename(Context context, String theme, String gusto, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String key = "SoundFile_"+gusto+":"+theme;
        if (value == null) {
            sharedPref.edit().remove(key).apply();
        } else {
            sharedPref.edit().putString(key, value).apply();
        }
    }

    public String getSoftEggsFilename(Context context, String theme) {
        return getGustoAlarmFilename(context,theme,"SOFT");
    }

    public String getMediumEggsFilename(Context context, String theme) {
        return getGustoAlarmFilename(context,theme,"MEDIUM");
    }

    public String getHardEggsFilename(Context context, String theme) {
        return getGustoAlarmFilename(context,theme,"HARD");
    }

    public void setSoftEggsFilename(Context context, String theme, String value) {
        setGustoAlarmFilename(context,theme,"SOFT",value);
    }

    public void setMediumEggsFilename(Context context, String theme, String value) {
        setGustoAlarmFilename(context,theme,"MEDIUM",value);
    }

    public void setHardEggsFilename(Context context, String theme, String value) {
        setGustoAlarmFilename(context,theme,"HARD",value);
    }

}
