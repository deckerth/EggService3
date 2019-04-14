package com.deckerth.thomas.eggservice.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.TimerController;
import com.deckerth.thomas.eggservice.persistency.Persistency;

public class TimerViewModel extends AndroidViewModel{

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<Integer> mSoftEggsCount = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> mMediumEggsCount = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> mHardEggsCount = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> mTimerRunning = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> mTimeElapsedForGusto = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> mRemainingTimeForGusto = new MediatorLiveData<>();
    private final MediatorLiveData<String> mTimerMessage = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> mSoftEggsInProgress = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> mMediumEggsInProgress = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> mHardEggsInProgress = new MediatorLiveData<>();

    public static TimerViewModel Current;

    public TimerViewModel(Application application) {
        super(application);
        Current = this;

        Persistency repo = DataRepository.getPersistency();
        TimerController timer = TimerController.getInstance();

        // observe the changes  from the database and forward them
        LiveData<Integer> soft = repo.getSoftEggsCount();
        mSoftEggsCount.setValue(soft.getValue());
        mSoftEggsCount.addSource(soft, mSoftEggsCount::setValue);
        LiveData<Integer> medium = repo.getMediumEggsCount();
        mMediumEggsCount.setValue(medium.getValue());
        mMediumEggsCount.addSource(medium, mMediumEggsCount::setValue);
        LiveData<Integer> hard = repo.getHardEggsCount();
        mHardEggsCount.setValue(hard.getValue());
        mHardEggsCount.addSource(hard, mHardEggsCount::setValue);

        LiveData<Boolean> running = timer.getTimerRunning();
        mTimerRunning.setValue(running.getValue());
        mTimerRunning.addSource(running,mTimerRunning::setValue);

        LiveData<Integer> elapsed = timer.getTimeElapsedForGusto();
        mTimeElapsedForGusto.setValue(elapsed.getValue());
        mTimeElapsedForGusto.addSource(elapsed,mTimeElapsedForGusto::setValue);
        LiveData<Integer> remaining = timer.getRemainingTimeForGusto();
        mRemainingTimeForGusto.setValue(remaining.getValue());
        mRemainingTimeForGusto.addSource(remaining,mRemainingTimeForGusto::setValue);

        LiveData<String> msg = timer.getTimerMessage();
        mTimerMessage.setValue(msg.getValue());
        mTimerMessage.addSource(msg,mTimerMessage::setValue);

        LiveData<Boolean> softInProgress = timer.getSoftEggsInProgress();
        mSoftEggsInProgress.setValue(softInProgress.getValue());
        mSoftEggsInProgress.addSource(softInProgress,mSoftEggsInProgress::setValue);
        LiveData<Boolean> mediumInProgress = timer.getMediumEggsInProgress();
        mMediumEggsInProgress.setValue(mediumInProgress.getValue());
        mMediumEggsInProgress.addSource(mediumInProgress,mMediumEggsInProgress::setValue);
        LiveData<Boolean> hardInProgress = timer.getHardEggsInProgress();
        mHardEggsInProgress.setValue(hardInProgress.getValue());
        mHardEggsInProgress.addSource(hardInProgress,mHardEggsInProgress::setValue);
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<Integer> getSoftEggsCount() {return mSoftEggsCount; }
    public LiveData<Integer> getMediumEggsCount() {return mMediumEggsCount; }
    public LiveData<Integer> getHardEggsCount() {return mHardEggsCount; }
    public LiveData<Boolean> getTimerRunning() {return  mTimerRunning; }
    public LiveData<Integer> getTimeElapsedForGusto() {return mTimeElapsedForGusto; }
    public LiveData<Integer> getRemainingTimeForGusto() {return mRemainingTimeForGusto; }
    public LiveData<String> getTimerMessage() {return mTimerMessage; }
    public LiveData<Boolean> getSoftEggsInProgress() {return  mSoftEggsInProgress; }
    public LiveData<Boolean> getMediumEggsInProgress() {return  mMediumEggsInProgress; }
    public LiveData<Boolean> getHardEggsInProgress() {return  mHardEggsInProgress; }

}
