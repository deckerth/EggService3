package com.deckerth.thomas.eggservice.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.persistency.FileUtil;
import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class EditThemeActivity extends AppCompatActivity {

    public final static String EXTRA_THEME_NAME = "EditThemeActivity_ThemeName";
    private final static String SELECTED_GUSTO = "selected_gusto";
    private static final int CHOOSE_FILE_REQUEST_CODE = 999;

    public static Intent getIntent(Context context, String themeName) {
        Intent filesIntent = new Intent(context, EditThemeActivity.class);
        filesIntent.putExtra(EditThemeActivity.EXTRA_THEME_NAME, themeName);
        return filesIntent;
    }

    private SoundTheme mTheme;

    private TextView mSoftEggsFile;
    private TextView mMediumEggsFile;
    private TextView mHardEggsFile;

    private ImageButton mPlaySoftEggAlarm;
    private ImageButton mStopSoftEggAlarm;
    private ImageButton mPlayMediumEggAlarm;
    private ImageButton mStopMediumEggAlarm;
    private ImageButton mPlayHardEggAlarm;
    private ImageButton mStopHardEggAlarm;

    private ImageButton mChangeSoftEggsAlarm;
    private ImageButton mChangeMediumEggsAlarm;
    private ImageButton mChangeHardEggsAlarm;

    private MediaPlayer mPlayer = new MediaPlayer();
    private Member.Gusto mCurrentlyPlaying = Member.Gusto.NO_EGG;

    private Member.Gusto mSelectedGusto = Member.Gusto.NO_EGG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themeName = getIntent().getStringExtra(EXTRA_THEME_NAME);
        setContentView(R.layout.activity_edit_theme);

        setupActionBar();

        mTheme = ThemeRepository.Current.getTheme(themeName);

        if (savedInstanceState != null) {
            mSelectedGusto = Member.Gusto.fromString(savedInstanceState.getString(SELECTED_GUSTO, "NO_EGG"));
        }

        TextView themeTitle = findViewById(R.id.selected_theme_name);
        themeTitle.setText(themeName);

        mSoftEggsFile = findViewById(R.id.soft_eggs_file);
        mSoftEggsFile.setText(mTheme.getSoftEggsSoundFilename());

        mMediumEggsFile = findViewById(R.id.medium_eggs_file);
        mMediumEggsFile.setText(mTheme.getMediumEggsSoundFilename());

        mHardEggsFile = findViewById(R.id.hard_eggs_file);
        mHardEggsFile.setText(mTheme.getHardEggsSoundFilename());

        mPlaySoftEggAlarm =  findViewById(R.id.play_soft_sound_button);
        mPlaySoftEggAlarm.setOnClickListener(v -> play(Member.Gusto.SOFT));
        mStopSoftEggAlarm =  findViewById(R.id.stop_soft_sound_button);
        mStopSoftEggAlarm.setOnClickListener(v -> stop());

        mPlayMediumEggAlarm =  findViewById(R.id.play_medium_sound_button);
        mPlayMediumEggAlarm.setOnClickListener(v -> play(Member.Gusto.MEDIUM));
        mStopMediumEggAlarm =  findViewById(R.id.stop_medium_sound_button);
        mStopMediumEggAlarm.setOnClickListener(v -> stop());

        mPlayHardEggAlarm =  findViewById(R.id.play_hard_sound_button);
        mPlayHardEggAlarm.setOnClickListener(v -> play(Member.Gusto.HARD));
        mStopHardEggAlarm =  findViewById(R.id.stop_hard_sound_button);
        mStopHardEggAlarm.setOnClickListener(v -> stop());

        mChangeSoftEggsAlarm = findViewById(R.id.change_soft_sound);
        mChangeSoftEggsAlarm.setOnClickListener(v -> choose_sound(Member.Gusto.SOFT));

        mChangeMediumEggsAlarm = findViewById(R.id.change_medium_sound);
        mChangeMediumEggsAlarm.setOnClickListener(v -> choose_sound(Member.Gusto.MEDIUM));

        mChangeHardEggsAlarm = findViewById(R.id.change_hard_sound);
        mChangeHardEggsAlarm.setOnClickListener(v -> choose_sound(Member.Gusto.HARD));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_GUSTO, mSelectedGusto.toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to previous activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private void stop() {
        switch (mCurrentlyPlaying) {
            case SOFT:
                if ((mPlayer != null) && mPlayer.isPlaying()) mPlayer.stop();
                mPlaySoftEggAlarm.setVisibility(View.VISIBLE);
                mStopSoftEggAlarm.setVisibility(View.GONE);
                break;
            case MEDIUM:
                if ((mPlayer != null) && mPlayer.isPlaying()) mPlayer.stop();
                mPlayMediumEggAlarm.setVisibility(View.VISIBLE);
                mStopMediumEggAlarm.setVisibility(View.GONE);
                break;
            case HARD:
                if ((mPlayer != null) && mPlayer.isPlaying()) mPlayer.stop();
                mPlayHardEggAlarm.setVisibility(View.VISIBLE);
                mStopHardEggAlarm.setVisibility(View.GONE);
                break;
        }
    }

    private void play(Member.Gusto gusto) {
        stop();
        mCurrentlyPlaying = gusto;
        switch (mCurrentlyPlaying) {
            case SOFT:
                mPlayer = mTheme.playSoftEggsAlarm(EditThemeActivity.this, mPlayer);
                if ( mPlayer != null ){
                    mPlaySoftEggAlarm.setVisibility(View.GONE);
                    mStopSoftEggAlarm.setVisibility(View.VISIBLE);
                    mPlayer.setOnCompletionListener(mp -> {
                        mPlaySoftEggAlarm.setVisibility(View.VISIBLE);
                        mStopSoftEggAlarm.setVisibility(View.GONE);
                        mCurrentlyPlaying = Member.Gusto.NO_EGG;
                    });
                }
                break;
            case MEDIUM:
                mPlayer = mTheme.playMediumEggsAlarm(EditThemeActivity.this, mPlayer);
                if ( mPlayer != null ){
                    mPlayMediumEggAlarm.setVisibility(View.GONE);
                    mStopMediumEggAlarm.setVisibility(View.VISIBLE);
                    mPlayer.setOnCompletionListener(mp -> {
                        mPlayMediumEggAlarm.setVisibility(View.VISIBLE);
                        mStopMediumEggAlarm.setVisibility(View.GONE);
                        mCurrentlyPlaying = Member.Gusto.NO_EGG;
                    });
                }
                break;
            case HARD:
                mPlayer = mTheme.playHardEggsAlarm(EditThemeActivity.this, mPlayer);
                if ( mPlayer != null ){
                    mPlayHardEggAlarm.setVisibility(View.GONE);
                    mStopHardEggAlarm.setVisibility(View.VISIBLE);
                    mPlayer.setOnCompletionListener(mp -> {
                        mPlayHardEggAlarm.setVisibility(View.VISIBLE);
                        mStopHardEggAlarm.setVisibility(View.GONE);
                        mCurrentlyPlaying = Member.Gusto.NO_EGG;
                    });
                }
                break;
        }
        if (mPlayer == null) {
            Toast.makeText(this, R.string.error_playing,Toast.LENGTH_LONG).show();
        }
    }

    private void choose_sound(Member.Gusto gusto) {
        mSelectedGusto = gusto;
        openFile("audio/*");
    }

    public void openFile(String mimeType) {

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        //Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        // if you want any file type, you can skip next line
        //sIntent.putExtra("CONTENT_TYPE", mimeType);
        //sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        //if (getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with Samsung file manager
        //    chooserIntent = Intent.createChooser(sIntent, "Open file");
        //    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        //} else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        //}

        try {
            startActivityForResult(chooserIntent, CHOOSE_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri currentUri = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_REQUEST_CODE) {
                if (data != null) {
                    currentUri = data.getData();
                    getContentResolver().takePersistableUriPermission(currentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    String fileName = savefile(currentUri);
                    switch (mSelectedGusto) {
                        case SOFT:
                            mTheme.setSoftEggsSoundFilename(fileName);
                            mSoftEggsFile.setText(fileName);
                            break;
                        case MEDIUM:
                            mTheme.setMediumEggsSoundFilename(fileName);
                            mMediumEggsFile.setText(fileName);
                            break;
                        case HARD:
                            mTheme.setHardEggsSoundFilename(fileName);
                            mHardEggsFile.setText(fileName);
                            break;
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String savefile(Uri sourceuri)
    {
        String sourceFilePath= sourceuri.getPath();
        String sourceFileName = FileUtil.getFileName(this,sourceuri);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(getContentResolver().openInputStream(sourceuri));
            bos = new BufferedOutputStream(openFileOutput(sourceFileName,Context.MODE_PRIVATE));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) {
                    bos.close();
                    File file = new File(getFilesDir(), sourceFileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sourceFileName;
    }

}
