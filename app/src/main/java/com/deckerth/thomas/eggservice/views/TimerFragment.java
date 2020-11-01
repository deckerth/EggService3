package com.deckerth.thomas.eggservice.views;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.TimerController;
import com.deckerth.thomas.eggservice.controls.ChronometerWithPause;
import com.deckerth.thomas.eggservice.controls.Hourglass;
import com.deckerth.thomas.eggservice.databinding.TimerFragmentBinding;
import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;
import com.deckerth.thomas.eggservice.settings.Settings;
import com.deckerth.thomas.eggservice.viewmodel.TimerViewModel;


public class TimerFragment extends Fragment {
    public static final String TAG = "TimerFragment";

    private TimerFragmentBinding mBinding = null;
    private TimerViewModel mViewModel;
    private View mView;

    private Hourglass mHourglass;
    private Button mStartButton;
    private TextView mMessage;
    private ChronometerWithPause mChronometer;

    private SoundTheme mTheme;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.timer_fragment, container, false);
            mView = mBinding.getRoot();

            mHourglass = mView.findViewById(R.id.progress_bar);

            mStartButton = mView.findViewById(R.id.start_button);
            mStartButton.setOnClickListener(mStartButtonListener);

            mMessage = mView.findViewById(R.id.message);

            mChronometer = mView.findViewById(R.id.chronometer);
            mChronometer.setOnChronometerTickListener(mOnChronometerTickListener);
            mChronometer.restoreInstanceState(savedInstanceState);
        }
        return mView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mChronometer.saveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        subscribeUi(mViewModel);
    }


    private void subscribeUi(TimerViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getSoftEggsCount().observe(this, count -> {
            if (count != null) {
                mBinding.setNoOfSoftEggs(count);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getMediumEggsCount().observe(this, count -> {
            if (count != null) {
                mBinding.setNoOfMediumEggs(count);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getHardEggsCount().observe(this, count -> {
            if (count != null) {
                mBinding.setNoOfHardEggs(count);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getTimerRunning().observe(this, running -> {
            if (running != null) {
                mBinding.setTimerIsRunning(running);
            }
            mBinding.executePendingBindings();
        });
        if (viewModel.getTimerRunning().getValue()) {
            mChronometer.setBase(TimerController.getInstance().getTimerBase());
            mChronometer.resume();
        }
        viewModel.getTimeElapsedForGusto().observe(this, elapsed -> {
            if (elapsed != null) {
                mBinding.setProgressCurrent(elapsed);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getRemainingTimeForGusto().observe(this, remaining -> {
            if (remaining != null) {
                mBinding.setProgressMax(remaining);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getTimerMessage().observe(this, s -> {
            if (s != null) {
                mBinding.setMessageText(s);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getSoftEggsInProgress().observe(this, inProgress -> {
            if (inProgress != null) {
                mBinding.setSoftInProgress(inProgress);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getMediumEggsInProgress().observe(this, inProgress -> {
            if (inProgress != null) {
                mBinding.setMediumInProgress(inProgress);
            }
            mBinding.executePendingBindings();
        });
        viewModel.getHardEggsInProgress().observe(this, inProgress -> {
            if (inProgress != null) {
                mBinding.setHardInProgress(inProgress);
            }
            mBinding.executePendingBindings();
        });
    }

    private MediaPlayer mPlayer = new MediaPlayer();


    private Button.OnClickListener mStartButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimerController.getInstance().timerStartStopClicked();
            if (TimerController.getInstance().getTimerRunning().getValue()) {
                mTheme = Settings.Current.getSoundTheme(BasicApp.getContext());
                if (mTheme.isRandomTheme()) {
                    mTheme = ThemeRepository.Current.getRandomTheme();
                }
                mHourglass.animate().rotation(360F).setDuration(1000).start();
                mChronometer.start();
            } else {
                mChronometer.stop();
            }
        }
    };

    private void start_alarm() {
        switch (TimerController.getInstance().getLastGusto()) {
            case SOFT:
                mPlayer = mTheme.playSoftEggsAlarm(getContext(), mPlayer);
                break;
            case MEDIUM:
                mPlayer = mTheme.playMediumEggsAlarm(getContext(), mPlayer);
                break;
            case HARD:
                mPlayer = mTheme.playHardEggsAlarm(getContext(), mPlayer);
                break;
        }
        // Fallback if alarm cannot be played:
        if (mPlayer == null) mPlayer = ThemeRepository.Current.getStandardTheme().playSoftEggsAlarm(getContext(), null);
    }

    public Chronometer.OnChronometerTickListener mOnChronometerTickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            switch (TimerController.getInstance().ChronometerTick(mChronometer)) {
                case RING:
                    start_alarm();
                    break;
                case RING_AND_STOP:
                    start_alarm();
                    mChronometer.stop();
                    break;
            }
        }
    };

}
