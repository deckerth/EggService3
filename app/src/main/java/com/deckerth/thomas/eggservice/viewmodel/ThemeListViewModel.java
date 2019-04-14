package com.deckerth.thomas.eggservice.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;
import com.deckerth.thomas.eggservice.ringtones.ThemesController;

import java.util.List;

public class ThemeListViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<SoundTheme>> mObservableThemes;
    private final MediatorLiveData<SoundTheme>  mSelectedTheme;
    private final MediatorLiveData<SoundTheme> mCurrentTheme;

    public ThemeListViewModel(Application application) {
        super(application);

        mObservableThemes = new MediatorLiveData<>();
        mObservableThemes.setValue(null);

        mSelectedTheme = new MediatorLiveData<>();
        mSelectedTheme.setValue(null);

        mCurrentTheme = new MediatorLiveData<>();
        mCurrentTheme.setValue(null);

        LiveData<List<SoundTheme>> themes = ThemeRepository.Current.getThemeList();
        mObservableThemes.addSource(themes, mObservableThemes::setValue);

        LiveData<SoundTheme> selected = ThemesController.getInstance().getSelectedTheme();
        mSelectedTheme.addSource(selected,mSelectedTheme::setValue);

        LiveData<SoundTheme> curremt = ThemesController.getInstance().getCurrentTheme();
        mCurrentTheme.addSource(curremt, mSelectedTheme::setValue);
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<List<SoundTheme>> getThemes() {return mObservableThemes;}
    public LiveData<SoundTheme> getSelectedTheme() {return mSelectedTheme;}
    public LiveData<SoundTheme> getCurrentTheme() {return mCurrentTheme;}

}
