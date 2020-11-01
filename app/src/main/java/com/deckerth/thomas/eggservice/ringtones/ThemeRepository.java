package com.deckerth.thomas.eggservice.ringtones;

import androidx.lifecycle.MutableLiveData;
import android.util.ArraySet;
import android.util.Log;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.settings.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ThemeRepository {

    public static ThemeRepository Current = new ThemeRepository();

    public List<SoundTheme> mThemes = new ArrayList<>();
    private Boolean mRandomThemeSetHasChanged = false;

    private Random mRandom;
    final private MutableLiveData<List<SoundTheme>> mObservableThemes;

    public ThemeRepository() {
        mRandom = new Random();
        mThemes.add(new SoundTheme("STANDARD", BasicApp.getContextString(R.string.sound_theme_standard), R.raw.ring, R.raw.ring, R.raw.ring));
        mThemes.add(new SoundTheme("MOZART", BasicApp.getContextString(R.string.sound_theme_mozart), R.raw.blaue_donau, R.raw.mozart_divertimento, R.raw.mozart_nachtmusik));
        mThemes.add(new SoundTheme("BEETHOVEN", BasicApp.getContextString(R.string.sound_theme_beethoven), R.raw.fuer_elise, R.raw.beethoven_5, R.raw.ode_and_die_freude));
        mThemes.add(new SoundTheme("OPERA", BasicApp.getContextString(R.string.sound_theme_opera), R.raw.traviata, R.raw.valkuere, R.raw.polowetzer_taenze));

        // custom themes
        Set<String> customThemes = Settings.Current.getCustomThemes(BasicApp.getContext());
        if (customThemes != null) {
            Iterator<String> iterator = customThemes.iterator();
            while (iterator.hasNext()) {
                String theme = iterator.next();
                mThemes.add(new SoundTheme(theme));
            }
        }

        mThemes.add(new SoundTheme("RANDOM", BasicApp.getContextString(R.string.sound_theme_random), 0, 0, 0));

        // themes for random play
        Set<String> themesForRandomPlay = Settings.Current.getThemesForRandomAlarm(BasicApp.getContext());
        if (themesForRandomPlay != null) {
            Log.v(">>>ThemeRepository"," Storing random themes #"+themesForRandomPlay.size());
            Iterator<String> iterator = themesForRandomPlay.iterator();
            while (iterator.hasNext()) {
                String theme = iterator.next();
                Log.v(">>>ThemeRepository"," Storing "+theme);
                getTheme(theme).isRelevantForRandomSelection = true;
            }
        } else {
            for (int i = 0; i < mThemes.size() - 1; i++)
                mThemes.get(i).isRelevantForRandomSelection = true;
        }

        mObservableThemes = new MutableLiveData<>();
        mObservableThemes.setValue(mThemes);
    }

    private void setCanBeSelected(boolean value) {
        List<SoundTheme> list = new ArrayList<>();
        for (int i = 0; i < mThemes.size(); i++) {
            if ((mThemes.get(i).isRandomTheme()) || (mThemes.get(i).canBeSelected() == value)) {
                list.add(mThemes.get(i));
            } else {
                SoundTheme clone = mThemes.get(i).clone();
                clone.setCanBeSelected(value);
                list.add(clone);
            }
        }
        mThemes = list;
        mObservableThemes.setValue(list);
    }

    public void setIsSelected(SoundTheme theme) {
        mRandomThemeSetHasChanged = true;
        for (int i = 0; i < mThemes.size(); i++) {
            if (mThemes.get(i).mId.contentEquals(theme.mId)) {
                mThemes.get(i).isRelevantForRandomSelection = theme.isRelevantForRandomSelection;
                return;
            }
        }
    }

    public void storeThemesForRandomSelection(){
        if (mRandomThemeSetHasChanged) {
            Settings.Current.setThemesForRandomAlarm(BasicApp.getContext(), getThemesForRandomAlarm());
            mRandomThemeSetHasChanged = false;
        }
    }

    public Boolean randomThemeSetHasChanged() {
        return mRandomThemeSetHasChanged;
    }

    public void enableThemeSelection() {
        setCanBeSelected(true);
    }

    public void disableThemeSelection() {
        setCanBeSelected(false);
    }

    public SoundTheme getRandomTheme() {
        List<SoundTheme> enabledThemes = new ArrayList<>();

        for (int i = 0; i < mThemes.size() - 1; i++) {
            if (mThemes.get(i).isRelevantForRandomSelection) enabledThemes.add(mThemes.get(i));
        }

        if (enabledThemes.size() == 0) {
            return mThemes.get(0);
        } else if (enabledThemes.size() == 1) {
            return enabledThemes.get(0);
        } else {
            int lastPlayed = Settings.Current.getLastUsedSoundThemeIndex(BasicApp.getContext());
            int themeNo = mRandom.nextInt(enabledThemes.size());
            while ( themeNo == lastPlayed) {
                themeNo = mRandom.nextInt(enabledThemes.size());
            }
            Settings.Current.setLastUsedSoundThemeIndex(BasicApp.getContext(),themeNo);
            return enabledThemes.get(themeNo);
        }
    }

    public SoundTheme getStandardTheme() {
        return mThemes.get(0);
    }

    public SoundTheme getTheme(String id) {
        SoundTheme result = mThemes.get(0);
        if ((id == null) || (id.isEmpty())) return result;
        for (int i = 0; i < mThemes.size(); i++) {
            if (mThemes.get(i).mId.contentEquals(id)) return mThemes.get(i);
        }
        return result;
    }

    public MutableLiveData<List<SoundTheme>> getThemeList() {
        return mObservableThemes;
    }

    public Set<String> getThemesForRandomAlarm() {
        List<SoundTheme> list = mObservableThemes.getValue();
        Set<String> result = new ArraySet<>();

        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).isRelevantForRandomSelection) result.add(list.get(i).mId);
        }
        if (result.size() == 0) {
            return null;
        } else {
            return result;
        }
    }

    public int getNumberOfSchemesForRandomPlay() {
        List<SoundTheme> list = mObservableThemes.getValue();
        int result = 0;
        for (int i = 0; i < list.size() - 1; i++)
            if (list.get(i).isRelevantForRandomSelection) result++;
        return result;
    }

    public void addTheme(SoundTheme theme) {
        Set<String> customThemes = new ArraySet<>();
        List<SoundTheme> list = new ArrayList<>();
        for (int i = 0; i < mThemes.size() - 1; i++) {
            list.add(mThemes.get(i));
            if (mThemes.get(i).isCustomTheme()) {
                customThemes.add(mThemes.get(i).getId());
            }
        }
        list.add(theme);
        if (theme.isCustomTheme()) customThemes.add(theme.getId());
        list.add(mThemes.get(mThemes.size() - 1));
        mThemes = list;
        Settings.Current.setCustomThemes(BasicApp.getContext(), customThemes);
        mObservableThemes.setValue(mThemes);
    }

    public void deleteTheme(SoundTheme theme) {
        Set<String> customThemes = new ArraySet<>();
        List<SoundTheme> list = new ArrayList<>();
        for (int i = 0; i < mThemes.size() - 1; i++) {
            if (mThemes.get(i) != theme) {
                list.add(mThemes.get(i));
                if (mThemes.get(i).isCustomTheme()) {
                    customThemes.add(mThemes.get(i).getId());
                }
            }
        }
        list.add(mThemes.get(mThemes.size() - 1));
        mThemes = list;
        Settings.Current.setCustomThemes(BasicApp.getContext(), customThemes);
        mObservableThemes.setValue(mThemes);
    }
}
