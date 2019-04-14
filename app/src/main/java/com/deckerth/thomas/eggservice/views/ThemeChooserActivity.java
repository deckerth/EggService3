package com.deckerth.thomas.eggservice.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.adapters.ThemeAdapter;
import com.deckerth.thomas.eggservice.databinding.ActivityThemeChooserBinding;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;
import com.deckerth.thomas.eggservice.ringtones.ThemesController;
import com.deckerth.thomas.eggservice.viewmodel.ThemeListViewModel;

import static com.deckerth.thomas.eggservice.model.Member.Gusto.HARD;
import static com.deckerth.thomas.eggservice.model.Member.Gusto.MEDIUM;
import static com.deckerth.thomas.eggservice.model.Member.Gusto.SOFT;

public class ThemeChooserActivity extends AppCompatActivity {

    public static final String TAG = "ThemeChooserActivity";
    public static ThemeChooserActivity Current = null;

    private ThemeAdapter mThemeAdapter;
    ActivityThemeChooserBinding mBinding;
    private ThemeListViewModel mViewModel;

    private Spinner mGustoChooser;
    private ImageButton mPlayButton;
    private ImageButton mStopButton;
    private Button mSetSoundTheme;
    private FloatingActionButton mAddTheme;
    private ThemesController mController;

    private boolean mThemeSelectionListenerEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Current = this;
        mController = ThemesController.getInstance();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_theme_chooser);
        mThemeAdapter = new ThemeAdapter(mThemeCallback, mEditThemeCallback, mThemeSelectedCallback);
        mBinding.themesList.setAdapter(mThemeAdapter);

        if (mController.isSelectedThemeRandom()) {
            ThemeRepository.Current.enableThemeSelection();
        } else {
            ThemeRepository.Current.disableThemeSelection();
        }

        mPlayButton = findViewById(R.id.play_button);
        mPlayButton.setOnClickListener(mPlayClicked);
        mPlayButton.setEnabled(!mController.isSelectedThemeRandom());

        mStopButton = findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(mStopClicked);

        mGustoChooser = findViewById(R.id.gusto_chooser);
        String[] gusti = {getString(R.string.label_soft_eggs), getString(R.string.label_medium_eggs), getString(R.string.label_hard_eggs)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, gusti);
        mGustoChooser.setAdapter(adapter);
        mGustoChooser.setEnabled(!mController.isSelectedThemeRandom());
        mGustoChooser.setOnItemSelectedListener(mGustoSpinnerClicked);

        mSetSoundTheme = findViewById(R.id.set_sound_theme);
        mSetSoundTheme.setOnClickListener(mSetSettingsClickListener);
        mSetSoundTheme.setEnabled(mController.selectedThemeCanBeSet());

        mAddTheme = findViewById(R.id.add_theme);
        mAddTheme.setOnClickListener(mAddThemeClicked);

        mViewModel = ViewModelProviders.of(this).get(ThemeListViewModel.class);

        subscribeUi(mViewModel);

        mThemeAdapter.setThemeList(ThemeRepository.Current.mThemes);
    }

    private void subscribeUi(ThemeListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getThemes().observe(this, myThemes -> {
            if (myThemes != null) {
                mThemeAdapter.setThemeList(myThemes);

                viewModel.getSelectedTheme().observe(this, theme -> {
                    mBinding.setPickedTheme(theme);
                });
            } else {
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    public void chooseTheme(SoundTheme theme) {
        mController.chooseTheme(theme);
        mSetSoundTheme.setEnabled(mController.selectedThemeCanBeSet());
        mPlayButton.setEnabled(!mController.isSelectedThemeRandom());
        mGustoChooser.setEnabled(!mController.isSelectedThemeRandom());
        stop();
        if (theme.isRandomTheme()) {
            ThemeRepository.Current.enableThemeSelection();
        } else {
            ThemeRepository.Current.disableThemeSelection();
        }
    }

    ThemeClickCallback mThemeCallback = theme -> chooseTheme(theme);

    ThemeClickCallback mThemeSelectedCallback = theme -> {
        if (mThemeSelectionListenerEnabled) {
            theme.isRelevantForRandomSelection = !theme.isRelevantForRandomSelection;
            ThemeRepository.Current.setIsSelected(theme);
            mSetSoundTheme.setEnabled(mController.selectedThemeCanBeSet());
        }
    };

    ThemeClickCallback mEditThemeCallback = theme -> {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(ThemeChooserActivity.this, theme.getMenuButton());
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_edit_theme, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    startActivity(EditThemeActivity.getIntent(ThemeChooserActivity.this, theme.getId()));
                    break;
                case R.id.delete:
                    ThemeRepository.Current.deleteTheme(theme);
                    break;
            }
            return true;
        });

        popup.show(); //showing popup menu
    };

    private MediaPlayer mPlayer = new MediaPlayer();

    private void stop() {
        if ((mPlayer != null) && mPlayer.isPlaying()) mPlayer.stop();
        mPlayButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);
    }

    private void play(Member.Gusto gusto) {
        stop();
        switch (gusto) {
            case SOFT:
                mPlayer = mController.getSelectedTheme().getValue().playSoftEggsAlarm(this, mPlayer);
                break;
            case MEDIUM:
                mPlayer = mController.getSelectedTheme().getValue().playMediumEggsAlarm(this, mPlayer);
                break;
            case HARD:
                mPlayer = mController.getSelectedTheme().getValue().playHardEggsAlarm(this, mPlayer);
                break;
        }
        if (mPlayer != null) {
            mPlayButton.setVisibility(View.GONE);
            mStopButton.setVisibility(View.VISIBLE);
            mPlayer.setOnCompletionListener(mp -> {
                mPlayButton.setVisibility(View.VISIBLE);
                mStopButton.setVisibility(View.GONE);
            });
        } else {
            Toast.makeText(this, R.string.error_playing, Toast.LENGTH_LONG).show();
        }
    }

    ImageButton.OnClickListener mPlayClicked = v -> {
        // play selected sound
        switch (mGustoChooser.getSelectedItemPosition()) {
            case 0:
                play(SOFT);
                break;
            case 1:
                play(MEDIUM);
                break;
            case 2:
                play(HARD);
                break;
        }
    };

    ImageButton.OnClickListener mStopClicked = v -> {
        stop();
    };

    Button.OnClickListener mSetSettingsClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mController.selectTheme();
            mSetSoundTheme.setEnabled(false);
            Toast.makeText(ThemeChooserActivity.this, R.string.theme_changed, Toast.LENGTH_SHORT).show();
        }
    };

    private Spinner.OnItemSelectedListener mGustoSpinnerClicked = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            stop();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private FloatingActionButton.OnClickListener mAddThemeClicked = v -> {
        CreateThemeDialog dialog = new CreateThemeDialog();
        dialog.show(getSupportFragmentManager(), "CreateThemeDialog");
    };

    public void enableThemeSelection() {
        mThemeSelectionListenerEnabled = true;
    }

    public void disableThemeSelection() {
        mThemeSelectionListenerEnabled = false;
    }

}
