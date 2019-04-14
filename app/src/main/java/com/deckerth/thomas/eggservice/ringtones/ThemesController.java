package com.deckerth.thomas.eggservice.ringtones;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.settings.Settings;

public class ThemesController {

    private static ThemesController sInstance;

    private final MutableLiveData<SoundTheme>  mSelectedTheme;
    private final MutableLiveData<SoundTheme> mCurrentTheme;

    public ThemesController() {
        sInstance = this;
        mSelectedTheme = new MutableLiveData<>();
        mSelectedTheme.setValue(null);

        mCurrentTheme = new MutableLiveData<>();
        mCurrentTheme.setValue(Settings.Current.getSoundTheme(BasicApp.getContext()));
        mSelectedTheme.setValue(mCurrentTheme.getValue());
    }

    public static ThemesController getInstance() {
        if (sInstance == null) {
            synchronized (ThemesController.class) {
                if (sInstance == null) {
                    sInstance = new ThemesController();
                }
            }
        }
        return sInstance;
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<SoundTheme> getSelectedTheme() {return mSelectedTheme;}
    public LiveData<SoundTheme> getCurrentTheme() {return mCurrentTheme;}

    public void chooseTheme(SoundTheme theme) {
        mSelectedTheme.setValue(theme);
    }

    public void selectTheme(){
        Settings.Current.setSoundTheme(BasicApp.getContext(), mSelectedTheme.getValue());
        ThemeRepository.Current.storeThemesForRandomSelection();
        mCurrentTheme.setValue(mSelectedTheme.getValue());
    }

    public Boolean isSelectedThemeRandom() {return mSelectedTheme.getValue().isRandomTheme();}

    public Boolean selectedThemeIsCurrentTheme() {return mSelectedTheme.getValue() == mCurrentTheme.getValue();}

    public Boolean selectedThemeCanBeSet() {
        if (isSelectedThemeRandom()) {
            if (ThemeRepository.Current.getNumberOfSchemesForRandomPlay() == 0) {
                return false;
            } else if (selectedThemeIsCurrentTheme()) {
                return ThemeRepository.Current.randomThemeSetHasChanged();
            } else {
                return true;
            }
        } else {
            return !selectedThemeIsCurrentTheme();
        }
    }
}
