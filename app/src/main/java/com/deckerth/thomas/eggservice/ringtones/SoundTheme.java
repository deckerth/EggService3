package com.deckerth.thomas.eggservice.ringtones;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.settings.Settings;

import java.io.File;

public class SoundTheme {

    public String mId;
    public String mName;
    public boolean isRelevantForRandomSelection = false;
    private boolean mCanBeSelected = false;
    private boolean isCustomTheme = false;
    private int mSoftEggsSound = 0;
    private int mMediumEggsSound = 0;
    private int mHardEggsSound = 0;
    private String mSoftEggsSoundFilename = "";
    private String mMediumEggsSoundFilename = "";
    private String mHardEggsSoundFilename = "";

    private View mMenuButton;

    public SoundTheme(String id, String name, int softEggsSound, int mediumEggsSound, int hardEggsSound) {
        mId = id;
        mName = name;
        mSoftEggsSound = softEggsSound;
        mMediumEggsSound = mediumEggsSound;
        mHardEggsSound = hardEggsSound;
    }

    public SoundTheme(String name) {
        mId = name;
        mName = name;
        isCustomTheme = true;
        mSoftEggsSoundFilename = Settings.Current.getSoftEggsFilename(BasicApp.getContext(), name);
        mMediumEggsSoundFilename = Settings.Current.getMediumEggsFilename(BasicApp.getContext(), name);
        mHardEggsSoundFilename = Settings.Current.getHardEggsFilename(BasicApp.getContext(), name);
    }

    public Boolean isRandomTheme() {
        return mId.contentEquals("RANDOM");
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public boolean isCustomTheme() {
        return isCustomTheme;
    }

    private MediaPlayer playAlarm(Context context, MediaPlayer player, int id, String fileName) {
        MediaPlayer newPlayer;

        if (player != null) player.release();
        if (id != 0) {
            newPlayer = MediaPlayer.create(context, id);
        } else if (fileName != null) {
            File audioFile = BasicApp.getContext().getFileStreamPath(fileName);
            Uri audioUri = Uri.fromFile(audioFile);
            newPlayer = MediaPlayer.create(context, audioUri );
        } else {
            return null;
        }
        if (newPlayer != null) {
            newPlayer.start();
        }
        return newPlayer;
    }

    public MediaPlayer playSoftEggsAlarm(Context context, MediaPlayer player) {
        return playAlarm(context, player, getSoftEggsSound(), mSoftEggsSoundFilename);
    }

    public MediaPlayer playMediumEggsAlarm(Context context, MediaPlayer player) {
        return playAlarm(context, player, getMediumEggsSound(), mMediumEggsSoundFilename);
    }

    public MediaPlayer playHardEggsAlarm(Context context, MediaPlayer player) {
        return playAlarm(context, player, getHardEggsSound(), mHardEggsSoundFilename);
    }

    public int getSoftEggsSound() {
        if (isCustomTheme && (mSoftEggsSoundFilename == null)) {
            return R.raw.ring;
        } else {
            return mSoftEggsSound;
        }
    }

    public void setSoftEggsSound(int softEggsSound) {
        this.mSoftEggsSound = softEggsSound;
    }

    public int getMediumEggsSound() {
        if (isCustomTheme && (mMediumEggsSoundFilename == null)) {
            return R.raw.ring;
        } else {
            return mMediumEggsSound;
        }
    }

    public void setMediumEggsSound(int mediumEggsSound) {
        this.mMediumEggsSound = mediumEggsSound;
    }

    public int getHardEggsSound() {
        if (isCustomTheme && (mHardEggsSoundFilename == null)) {
            return R.raw.ring;
        } else {
            return mHardEggsSound;
        }
    }

    public void setHardEggsSound(int hardEggsSound) {
        this.mHardEggsSound = hardEggsSound;
    }

    public String getSoftEggsSoundFilename() {
        if ((mSoftEggsSoundFilename == null) || mSoftEggsSoundFilename.isEmpty()) {
            return BasicApp.getContextString(R.string.sound_theme_standard);
        } else {
            return mSoftEggsSoundFilename;
        }
    }

    public void setSoftEggsSoundFilename(String softEggsSoundFilename) {
        this.mSoftEggsSoundFilename = softEggsSoundFilename;
        Settings.Current.setSoftEggsFilename(BasicApp.getContext(), mId, softEggsSoundFilename);
    }

    public String getMediumEggsSoundFilename() {
        if ((mMediumEggsSoundFilename == null) || mMediumEggsSoundFilename.isEmpty()) {
            return BasicApp.getContextString(R.string.sound_theme_standard);
        } else {
            return mMediumEggsSoundFilename;
        }
    }

    public void setMediumEggsSoundFilename(String mediumEggsSoundFilename) {
        this.mMediumEggsSoundFilename = mediumEggsSoundFilename;
        Settings.Current.setMediumEggsFilename(BasicApp.getContext(), mId, mediumEggsSoundFilename);
    }

    public String getHardEggsSoundFilename() {
        if ((mHardEggsSoundFilename == null) || mHardEggsSoundFilename.isEmpty()) {
            return BasicApp.getContextString(R.string.sound_theme_standard);
        } else {
            return mHardEggsSoundFilename;
        }
    }

    public void setHardEggsSoundFilename(String hardEggsSoundFilename) {
        this.mHardEggsSoundFilename = hardEggsSoundFilename;
        Settings.Current.setHardEggsFilename(BasicApp.getContext(), mId, hardEggsSoundFilename);
    }

    public View getMenuButton() {
        return mMenuButton;
    }

    public void setMenuButton(View view) {
        this.mMenuButton = view;
    }

    public boolean canBeSelected() {
        return mCanBeSelected;
    }

    public SoundTheme clone() {
        SoundTheme result = new SoundTheme(mId, mName, mSoftEggsSound, mMediumEggsSound, mHardEggsSound);
        result.isRelevantForRandomSelection = isRelevantForRandomSelection;
        result.setCanBeSelected(canBeSelected());
        result.setSoftEggsSoundFilename(mSoftEggsSoundFilename);
        result.setMediumEggsSoundFilename(mMediumEggsSoundFilename);
        result.setHardEggsSoundFilename(mHardEggsSoundFilename);
        result.isCustomTheme = isCustomTheme;
        return result;
    }

    public void setCanBeSelected(boolean canBeSelected) {
        if (!isRandomTheme())  this.mCanBeSelected = canBeSelected;
    }
}
