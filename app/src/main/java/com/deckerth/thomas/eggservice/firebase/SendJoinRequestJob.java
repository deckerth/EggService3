package com.deckerth.thomas.eggservice.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SendJoinRequestJob {
    private DataManagement.JobState mJobState;

    public DataManagement.JobState getJobState() {
        return mJobState;
    }

    private DataManagementResult mResult;

    public DataManagementResult getResult() {
        return mResult;
    }

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String mGroupName;

    public SendJoinRequestJob(String email) {
        mAuth = FirebaseAuth.getInstance();
        mGroupName = DataManagement.getPathFromEmail(email);
        if ((mGroupName == null) || mGroupName.isEmpty()) {
            DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NO_VALID_GROUP,null);
            mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NO_VALID_GROUP);
            mJobState = DataManagement.JobState.COMPLETE;
        } else {
            mJobState = DataManagement.JobState.IN_PROGRESS;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference groupDatabaseEntry = mDatabase.child(DataManagement.GROUPS).child(mGroupName);
            groupDatabaseEntry.addListenerForSingleValueEvent(mGroupListener);
        }
    }

    private ValueEventListener mGroupListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean value = dataSnapshot.getValue(Boolean.class);
            if (value == null) {
                DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NO_VALID_GROUP,null);
                mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NO_VALID_GROUP);
                mJobState = DataManagement.JobState.COMPLETE;
            } else {
                // The group exists, now check access permission
                String userPath = DataManagement.getPathFromEmail(mAuth.getCurrentUser().getEmail());
                DatabaseReference userDatabaseEntry = mDatabase.child(DataManagement.USERS).child(mGroupName).child(userPath);
                userDatabaseEntry.addListenerForSingleValueEvent(mUserListener);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NO_VALID_GROUP,databaseError);
            mJobState = DataManagement.JobState.FAILED;
        }
    };


    private ValueEventListener mUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean value = dataSnapshot.getValue(Boolean.class);
            String userPath = DataManagement.getPathFromEmail(mAuth.getCurrentUser().getEmail());
            if (value == null) {
                // create request
                mDatabase.child(DataManagement.PENDING_REQUESTS).child(mGroupName).child(userPath).setValue(Boolean.TRUE);
                mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.REQUEST_PENDING);
                DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.REQUEST_PENDING,mGroupName);
                mJobState = DataManagement.JobState.COMPLETE;
            } else {
                // The user is authorized
                DataManagement.DatabaseState state = mGroupName.contentEquals(userPath) ? DataManagement.DatabaseState.LIVE : DataManagement.DatabaseState.JOINED;
                DataManagement.getInstance().setConnectivityState(state,mGroupName);
                mResult = new GetMemberDatabaseResult(state);
                mJobState = DataManagement.JobState.COMPLETE;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NO_VALID_GROUP,databaseError);
            mJobState = DataManagement.JobState.FAILED;
        }
    };
}
