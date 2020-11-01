package com.deckerth.thomas.eggservice.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.model.MemberEntity;

public class CreateMemberDialog extends androidx.fragment.app.DialogFragment {

    private Dialog mDialogInstance;

    private static final String MEMBER_NAME = "memberName";
    private MemberEntity originalMember;

    public CreateMemberDialog() {
        // Required empty public constructor
    }

    public static CreateMemberDialog newInstance(String memberName) {
        CreateMemberDialog fragment = new CreateMemberDialog();
        Bundle args = new Bundle();
        args.putString(MEMBER_NAME,memberName);
        fragment.setArguments(args);
        return fragment;
    }

    private void storeResult() {
        EditText nameToStore = mDialogInstance.findViewById(R.id.memberName);
        String newMemberName = nameToStore.getText().toString().trim();
        if (!newMemberName.isEmpty()) {
            if (originalMember == null) {
                if (DataRepository.getPersistency().getMember(newMemberName) == null) {
                    MemberEntity member = new MemberEntity(newMemberName);
                    DataRepository.getPersistency().addMember(member);
                } else {
                    Toast.makeText(MainActivity.Current.getApplicationContext(), getResources().getString(R.string.name_already_exists), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                // Renaming is not yet implemented
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (getArguments() != null) {
            String originalMemberName = getArguments().getString(MEMBER_NAME);
            if ((originalMemberName != null) && (!originalMemberName.isEmpty())) {
                originalMember = DataRepository.getPersistency().getMember(originalMemberName);
            }
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        View content = inflater.inflate(R.layout.create_member, null);
        builder.setTitle(getString(R.string.title_create_member))
                .setView(content)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storeResult();
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        mDialogInstance =  builder.create();

        if (originalMember != null) {
            EditText memberBox = content.findViewById(R.id.memberName);
            memberBox.setText(originalMember.getName());
        }

        return  mDialogInstance;
    }

}
