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

public class CheckConnectivityJob {

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
    private String mRequestedGroup;

    public CheckConnectivityJob() {
        mAuth = FirebaseAuth.getInstance();
        mJobState = DataManagement.JobState.IN_PROGRESS;
        mGroupName = Settings.Current.getGroupName(BasicApp.getContext());
        if ((mGroupName != null) && !mGroupName.isEmpty()) {
            mRequestedGroup = mGroupName;
        } else {
            mRequestedGroup = Settings.Current.getPendingGroupName(BasicApp.getContext());
        }

        if ((mRequestedGroup == null) || mRequestedGroup.isEmpty()) {
            DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NO_VALID_GROUP,null);
            mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NO_VALID_GROUP);
            mJobState = DataManagement.JobState.COMPLETE;
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference groupDatabaseEntry = mDatabase.child(DataManagement.GROUPS).child(mRequestedGroup);
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
                DatabaseReference userDatabaseEntry = mDatabase.child(DataManagement.USERS).child(mRequestedGroup).child(userPath);
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
                // The user is not authorized
                if ((mGroupName == null) || (mGroupName.isEmpty())) {
                    // check for pending requests
                    DatabaseReference pendingRequestEntry = mDatabase.child(DataManagement.PENDING_REQUESTS).child(mRequestedGroup).child(userPath);
                    pendingRequestEntry.addListenerForSingleValueEvent(mPendingRequestListener);
                } else {
                    DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NOT_AUTHORIZED,null);
                    mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NOT_AUTHORIZED);
                    mJobState = DataManagement.JobState.COMPLETE;
                }
            } else {
                // The user is authorized
                DataManagement.DatabaseState state = mRequestedGroup.contentEquals(userPath) ? DataManagement.DatabaseState.LIVE : DataManagement.DatabaseState.JOINED;
                DataManagement.getInstance().setConnectivityState(state,mRequestedGroup);
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

    private ValueEventListener mPendingRequestListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean value = dataSnapshot.getValue(Boolean.class);
            if (value == null) {
                DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NOT_AUTHORIZED,null);
                mResult = new GetMemberDatabaseResult(DataManagement.DatabaseState.NOT_AUTHORIZED);
                mJobState = DataManagement.JobState.COMPLETE;
            } else {
                DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.REQUEST_PENDING,mRequestedGroup);
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
