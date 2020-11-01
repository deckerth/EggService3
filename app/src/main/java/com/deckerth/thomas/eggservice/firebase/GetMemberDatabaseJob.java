package com.deckerth.thomas.eggservice.firebase;

import androidx.annotation.NonNull;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.settings.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetMemberDatabaseJob {

    private DataManagement.JobState mJobState;

    public DataManagement.JobState getJobState() {
        return mJobState;
    }

    private GetMemberDatabaseResult mResult;

    public GetMemberDatabaseResult getResult() {
        return mResult;
    }

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String mGroupName;

    public GetMemberDatabaseJob() {
        mAuth = FirebaseAuth.getInstance();
        mGroupName = Settings.Current.getGroupName(BasicApp.getContext());
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
                // check for pending requests
                DatabaseReference pendingRequestEntry = mDatabase.child(DataManagement.PENDING_REQUESTS).child(mGroupName).child(userPath);
                pendingRequestEntry.addListenerForSingleValueEvent(mPendingRequestListener);
            } else {
                // The user is authorized, return reference to member list
                DataManagement.DatabaseState state = mGroupName.contentEquals(userPath) ? DataManagement.DatabaseState.LIVE : DataManagement.DatabaseState.JOINED;
                DataManagement.getInstance().setConnectivityState(state,mGroupName);
                mResult = new GetMemberDatabaseResult(state,mDatabase.child(DataManagement.MEMBERS).child(mGroupName));
                mJobState = DataManagement.JobState.COMPLETE;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NO_VALID_GROUP,databaseError);
            mJobState = DataManagement.JobState.FAILED;
        }
    };

    private ValueEventListener mPendingRequestListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean value = dataSnapshot.getValue(Boolean.class);
            if (value == null) {
                DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NOT_AUTHORIZED,null);
                mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NOT_AUTHORIZED);
                mJobState = DataManagement.JobState.COMPLETE;
            } else {
                DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.REQUEST_PENDING,mGroupName);
                mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.REQUEST_PENDING);
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
