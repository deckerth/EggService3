package com.deckerth.thomas.eggservice;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.SystemClock;
import android.widget.Chronometer;

import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.settings.Settings;
import com.deckerth.thomas.eggservice.views.MainActivity;

public class TimerController {

    private static TimerController sInstance;

    public enum TimerEvent {CONTINUE, RING, RING_AND_STOP};

    private final MutableLiveData<Boolean> mTimerRunning;
    private final MutableLiveData<Integer> mTimeElapsedForGusto;
    private final MutableLiveData<Integer> mRemainingTimeForGusto;
    private final MutableLiveData<String> mTimerMessage;
    private final MutableLiveData<Boolean> mSoftEggsInProgress;
    private final MutableLiveData<Boolean> mMediumEggsInProgress;
    private final MutableLiveData<Boolean> mHardEggsInProgress;

    private long mTimerBase = 0;
    public long getTimerBase() {
        return mTimerBase;
    }
    public void setTimerBase(long timerBase) {
        this.mTimerBase = timerBase;
    }

    private long mTimerBaseForCurrentGusto = 0;
    public long getTimerBaseForCurrentGusto() {
        return mTimerBaseForCurrentGusto;
    }
    public void setmTimerBaseForCurrentGusto(long base) {
        this.mTimerBaseForCurrentGusto = base;
    }

    private Member.Gusto mLastGusto;

    public TimerController() {
        sInstance = this;

        mTimerRunning = new MutableLiveData<>();
        mTimerRunning.setValue(Boolean.FALSE);

        mTimeElapsedForGusto = new MutableLiveData<>();
        mTimeElapsedForGusto.setValue(0);
        mRemainingTimeForGusto = new MutableLiveData<>();
        mRemainingTimeForGusto.setValue(0);

        mTimerMessage = new MutableLiveData<>();
        mTimerMessage.setValue("");

        mSoftEggsInProgress = new MutableLiveData<>();
        mSoftEggsInProgress.setValue(Boolean.FALSE);
        mMediumEggsInProgress = new MutableLiveData<>();
        mMediumEggsInProgress.setValue(Boolean.FALSE);
        mHardEggsInProgress = new MutableLiveData<>();
        mHardEggsInProgress.setValue(Boolean.FALSE);
    }

    public static TimerController getInstance() {
        if (sInstance == null) {
            synchronized (TimerController.class) {
                if (sInstance == null) {
                    sInstance = new TimerController();
                }
            }
        }
        return sInstance;
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<Boolean> getTimerRunning() {return  mTimerRunning; }
    public LiveData<Integer> getTimeElapsedForGusto() {return mTimeElapsedForGusto; }
    public LiveData<Integer> getRemainingTimeForGusto() {return mRemainingTimeForGusto; }
    public LiveData<String> getTimerMessage() {return mTimerMessage; }
    public LiveData<Boolean> getSoftEggsInProgress() {return  mSoftEggsInProgress; }
    public LiveData<Boolean> getMediumEggsInProgress() {return  mMediumEggsInProgress; }
    public LiveData<Boolean> getHardEggsInProgress() {return  mHardEggsInProgress; }

    private long mSoftEggTiming = 0;
    private long mMediumEggTiming = 0;
    private long mHardEggTiming = 0;
    private boolean mSoftEggsDone = Boolean.FALSE;
    private boolean mMediumEggsDone = Boolean.FALSE;
    private boolean mHardEggsDone = Boolean.FALSE;
    private long mRemainingSeconds = 0;
    private long mLastStartBase = 0;

    public void timerStartStopClicked() {
        mSoftEggTiming = Settings.Current.getSoftTimingSeconds();
        mMediumEggTiming = Settings.Current.getMediumTimingSeconds();
        mHardEggTiming = Settings.Current.getHardTimingSeconds();
        mSoftEggsDone = Boolean.FALSE;
        mMediumEggsDone = Boolean.FALSE;
        mHardEggsDone = Boolean.FALSE;
        mTimerRunning.setValue(!mTimerRunning.getValue());
        mTimerMessage.setValue("");
        mSoftEggsInProgress.setValue(Boolean.FALSE);
        mMediumEggsInProgress.setValue(Boolean.FALSE);
        mHardEggsInProgress.setValue(Boolean.FALSE);
        mTimeElapsedForGusto.setValue(0);
        if (mTimerRunning.getValue()) {
            checkRemainingSeconds(0);
        }
    }

    private Boolean checkTime(boolean doneFlag, Integer count, long duration, long elapsedSeconds) {
        if ((!doneFlag) && (count > 0) && (elapsedSeconds >= duration)) {
            String msg = "";
            if (count == 1) {
                msg = MainActivity.Current.getString(R.string.egg_done);
            } else {
                msg = MainActivity.Current.getString(R.string.eggs_done);
            }
            mTimerMessage.setValue(msg.replace("#",count.toString()));
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public TimerEvent ChronometerTick(Chronometer chronometer) {
        mTimerBase = chronometer.getBase();
        long now = SystemClock.elapsedRealtime();
        long elapsedSeconds = (now - mTimerBase) / 1000;
        long lastElapsed = (now - mLastStartBase) / 1000;

        mTimeElapsedForGusto.setValue((int)lastElapsed);

        if (checkTime(mSoftEggsDone, DataRepository.getPersistency().getSoftEggsCount().getValue(),mSoftEggTiming,elapsedSeconds)) {
            mSoftEggsDone= Boolean.TRUE;
            mSoftEggsInProgress.setValue(Boolean.FALSE);
            mLastGusto = Member.Gusto.SOFT;
            return checkRemainingSeconds(elapsedSeconds) ? TimerEvent.RING : TimerEvent.RING_AND_STOP;
        }
        if (checkTime(mMediumEggsDone, DataRepository.getPersistency().getMediumEggsCount().getValue(),mMediumEggTiming,elapsedSeconds)) {
            mMediumEggsDone = Boolean.TRUE;
            mMediumEggsInProgress.setValue(Boolean.FALSE);
            mLastGusto = Member.Gusto.MEDIUM;
            return checkRemainingSeconds(elapsedSeconds) ? TimerEvent.RING : TimerEvent.RING_AND_STOP;
        }
        if (checkTime(mHardEggsDone, DataRepository.getPersistency().getHardEggsCount().getValue(),mHardEggTiming,elapsedSeconds)) {
            mHardEggsDone = Boolean.TRUE;
            mHardEggsInProgress.setValue(Boolean.FALSE);
            mLastGusto = Member.Gusto.HARD;
            return checkRemainingSeconds(elapsedSeconds) ? TimerEvent.RING : TimerEvent.RING_AND_STOP;
        }
        return TimerEvent.CONTINUE;
    }

    public Boolean checkRemainingSeconds(long elapsedSeconds) {
        mLastStartBase = SystemClock.elapsedRealtime();
        if ((DataRepository.getPersistency().getSoftEggsCount().getValue() > 0) && (!mSoftEggsDone)) {
            mRemainingSeconds = mSoftEggTiming >= elapsedSeconds ? mSoftEggTiming - elapsedSeconds : 0;
            mTimeElapsedForGusto.setValue(0);
            mRemainingTimeForGusto.setValue((int) mRemainingSeconds);
            mSoftEggsInProgress.setValue(Boolean.TRUE);
            return Boolean.TRUE;
        }
        if ((DataRepository.getPersistency().getMediumEggsCount().getValue() > 0) && (!mMediumEggsDone)) {
            mRemainingSeconds = mMediumEggTiming >= elapsedSeconds ? mMediumEggTiming - elapsedSeconds : 0;
            mTimeElapsedForGusto.setValue(0);
            mRemainingTimeForGusto.setValue((int) mRemainingSeconds);
            mMediumEggsInProgress.setValue(Boolean.TRUE);
            return Boolean.TRUE;
        }
        if ((DataRepository.getPersistency().getHardEggsCount().getValue() > 0) && (!mHardEggsDone)){
            mRemainingSeconds = mHardEggTiming >= elapsedSeconds ? mHardEggTiming - elapsedSeconds : 0;
            mTimeElapsedForGusto.setValue(0);
            mRemainingTimeForGusto.setValue((int) mRemainingSeconds);
            mHardEggsInProgress.setValue(Boolean.TRUE);
            return Boolean.TRUE;
        }
        mTimerRunning.setValue(Boolean.FALSE);
        return Boolean.FALSE;
    }

    public Member.Gusto getLastGusto() {
        return mLastGusto;
    }

}
