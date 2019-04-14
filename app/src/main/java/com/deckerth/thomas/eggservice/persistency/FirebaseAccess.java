package com.deckerth.thomas.eggservice.persistency;

import android.util.Log;
import android.widget.Toast;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.firebase.GetMemberDatabaseResult;
import com.deckerth.thomas.eggservice.firebase.GetMemberDatabaseTask;
import com.deckerth.thomas.eggservice.firebase.PendingRequestObserver;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;
import com.deckerth.thomas.eggservice.model.MemberGusto;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

class FirebaseAccess implements DataAccess{

    public static final String TAG = "DataRepository";

    private DatabaseReference mDatabase;
    private DataRepository mRepo;

    FirebaseAccess(DataRepository repo) {
        mRepo = repo;
    }

    @Override
    public Boolean isOnline() {
        return Boolean.TRUE;
    }

    GetMemberDatabaseTask.Callback mGetMemberDatabaseCallback = new GetMemberDatabaseTask.Callback() {
        @Override
        public void onGetComplete(GetMemberDatabaseResult result) {
            switch (result.getState()) {
                case JOINED:
                    mDatabase = result.getMemberDatabase();
                    mDatabase.addChildEventListener(mChildEventListener);
                    break;
                case LIVE:
                    mDatabase = result.getMemberDatabase();
                    mDatabase.addChildEventListener(mChildEventListener);
                    PendingRequestObserver.getInstance().observe();
                    break;
                case NO_VALID_GROUP:
                    Toast.makeText(BasicApp.getContext(), R.string.unknown_group, Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_PENDING:
                    Toast.makeText(BasicApp.getContext(), R.string.request_still_peding, Toast.LENGTH_SHORT).show();
                    break;
                case NOT_AUTHORIZED:
                    Toast.makeText(BasicApp.getContext(), R.string.not_authorized, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Exception e) {
            Toast.makeText(BasicApp.getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
        }
    };

    ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            String name = dataSnapshot.getKey();
            MemberGusto value = dataSnapshot.getValue(MemberGusto.class);
            if (value.gusto == null) {
                value.gusto = Member.Gusto.NO_EGG;
            }
            MemberEntity newMember = new MemberEntity(name, value.gusto);

            List<MemberEntity> members = mRepo.mObservableMembers.getValue();
            List<MemberEntity> list = new ArrayList<MemberEntity>();
            if (members != null) {
                for (int i = 0; i < members.size(); i++) {
                    if (members.get(i).getName().contentEquals(name)) {
                        list.add(newMember);
                        newMember = null;
                    } else {
                        list.add(members.get(i));
                    }
                }
            }
            if (newMember != null) {
                list.add(newMember);
            }
            mRepo.mObservableMembers.setValue(list);
            mRepo.computeGustoSummary();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            String name = dataSnapshot.getKey();
            MemberGusto value = dataSnapshot.getValue(MemberGusto.class);
            MemberEntity changedMember = new MemberEntity(name, value.gusto);

            List<MemberEntity> members = mRepo.mObservableMembers.getValue();
            List<MemberEntity> list = new ArrayList<MemberEntity>();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getName().contentEquals(name)) {
                    list.add(changedMember);
                } else {
                    list.add(members.get(i));
                }
            }
            mRepo.mObservableMembers.setValue(list);
            mRepo.computeGustoSummary();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String name = dataSnapshot.getKey();

            List<MemberEntity> members = mRepo.mObservableMembers.getValue();
            List<MemberEntity> list = new ArrayList<MemberEntity>();
            for (int i = 0; i < members.size(); i++) {
                if (!members.get(i).getName().contentEquals(name)) {
                    list.add(members.get(i));
                }
            }
            mRepo.mObservableMembers.setValue(list);
            mRepo.computeGustoSummary();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            /*Toast.makeText(mContext, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show();*/
        }
    };

    @Override
    public void loadMembers() {
        try {
            mRepo.mObservableMembers.setValue(null);
            mRepo.computeGustoSummary();
            new GetMemberDatabaseTask(BasicApp.getContext(),mGetMemberDatabaseCallback).execute();
        } catch (Exception e) {  }
    }

    @Override
    public void addMember(MemberEntity member) {
        updateMember(member);
    }

    @Override
    public void updateMember(MemberEntity member) {
        mDatabase.child(member.getName()).setValue(new MemberGusto(member.getGusto()));
        ;
    }

    @Override
    public void deleteMember(Member member) {
        mDatabase.child(member.getName()).removeValue();
    }
}
