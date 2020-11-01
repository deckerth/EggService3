package com.deckerth.thomas.eggservice.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;

public class EditGustoDialog extends androidx.fragment.app.DialogFragment {

    private Dialog mDialogInstance;

    private static final String MEMBER_NAME = "memberName";
    private MemberEntity originalMember;

    public EditGustoDialog() {
        // Required empty public constructor
    }

    public static EditGustoDialog newInstance(String memberName) {
        EditGustoDialog fragment = new EditGustoDialog();
        Bundle args = new Bundle();
        args.putString(MEMBER_NAME,memberName);
        fragment.setArguments(args);
        return fragment;
    }

    private void storeResult() {
        RadioButton choiceSoft = mDialogInstance.findViewById(R.id.choice_soft);
        RadioButton choiceMedium = mDialogInstance.findViewById(R.id.choice_medium);
        RadioButton choiceHard = mDialogInstance.findViewById(R.id.choice_hard);
        RadioButton choiceFried = mDialogInstance.findViewById(R.id.choice_fried);
        RadioButton choiceScrambled = mDialogInstance.findViewById(R.id.choice_scrambled);

        MemberEntity updatedMember = new MemberEntity(originalMember.getName());
        Boolean changed = Boolean.FALSE;
        if ( choiceSoft.isChecked() ) {
            if (originalMember.getGusto() != Member.Gusto.SOFT) {
                updatedMember.setGusto(Member.Gusto.SOFT);
                changed = Boolean.TRUE;
            }
        } else if ( choiceMedium.isChecked() ) {
            if (originalMember.getGusto() != Member.Gusto.MEDIUM) {
                updatedMember.setGusto(Member.Gusto.MEDIUM);
                changed = Boolean.TRUE;
            }
        } else if ( choiceHard.isChecked() ) {
            if (originalMember.getGusto() != Member.Gusto.HARD) {
                updatedMember.setGusto(Member.Gusto.HARD);
                changed = Boolean.TRUE;
            }
        } else if ( choiceFried.isChecked() ) {
            if (originalMember.getGusto() != Member.Gusto.FRIED_EGG) {
                updatedMember.setGusto(Member.Gusto.FRIED_EGG);
                changed = Boolean.TRUE;
            }
        } else if ( choiceScrambled.isChecked() ) {
            if (originalMember.getGusto() != Member.Gusto.SCRAMBLED_EGG) {
                updatedMember.setGusto(Member.Gusto.SCRAMBLED_EGG);
                changed = Boolean.TRUE;
            }
        } else {
            if (originalMember.getGusto() != Member.Gusto.NO_EGG) {
                updatedMember.setGusto(Member.Gusto.NO_EGG);
                changed = Boolean.TRUE;
            }
        }

        if (changed) DataRepository.getPersistency().updateMember(updatedMember);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainActivity.Current.setIdle(Boolean.FALSE);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (getArguments() != null) {
            String originalMemberName = getArguments().getString(MEMBER_NAME);
            originalMember = DataRepository.getPersistency().getMember(originalMemberName);
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        View content = inflater.inflate(R.layout.edit_gusto, null);
        builder.setTitle(getString(R.string.title_gusto))
                .setView(content)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storeResult();
                        MainActivity.Current.setIdle(Boolean.TRUE);
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.Current.setIdle(Boolean.TRUE);
                    }
                });
        // Create the AlertDialog object and return it
        mDialogInstance =  builder.create();

        RadioButton choice = null;
        switch (originalMember.getGusto()) {
            case SOFT:
                choice = content.findViewById(R.id.choice_soft);
                break;
            case MEDIUM:
                choice = content.findViewById(R.id.choice_medium);
                break;
            case HARD:
                choice = content.findViewById(R.id.choice_hard);
                break;
            case FRIED_EGG:
                choice = content.findViewById(R.id.choice_fried);
                break;
            case SCRAMBLED_EGG:
                choice = content.findViewById(R.id.choice_scrambled);
                break;
            case NO_EGG:
                choice = content.findViewById(R.id.choice_no_egg);
                break;
        }
        if (choice != null) choice.setChecked(Boolean.TRUE);

        return  mDialogInstance;
    }

}
