package com.deckerth.thomas.eggservice.views;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.ringtones.ThemeRepository;

public class CreateThemeDialog extends androidx.fragment.app.DialogFragment {

    private Dialog mDialogInstance;
    private boolean mCancelled = false;
    private SoundTheme mNewTheme = null;

    public CreateThemeDialog() {
        // Required empty public constructor
    }

    public static CreateMemberDialog newInstance() {
        return new CreateMemberDialog();
    }

    private void storeResult() {
        EditText nameToStore = mDialogInstance.findViewById(R.id.themeName);
        mNewTheme = new SoundTheme(nameToStore.getText().toString());
        ThemeRepository.Current.addTheme(mNewTheme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        View content = inflater.inflate(R.layout.create_theme, null);
        builder.setTitle(getString(R.string.title_create_theme))
                .setView(content)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> storeResult())
                .setNegativeButton("Abbrechen", (dialog, id) -> mCancelled = true);
        // Create the AlertDialog object and return it
        mDialogInstance = builder.create();

        return mDialogInstance;
    }

    public boolean hasBeenCancelled() {
        return mCancelled;
    }

    public SoundTheme getCreatedTheme() {
        return mNewTheme;
    }

}
