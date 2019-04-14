package com.deckerth.thomas.eggservice.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.settings.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OwnRequestsObserver {

    public interface OnAccessRequestAccepted {
        Boolean handleRequest(String requestingUserPath);
    }

    public interface OnAccessRequestRejected {
        Boolean handleRequest(String requestingUserPath);
    }

    public static OwnRequestsObserver sInstance = new OwnRequestsObserver();

    private DatabaseReference mDatabase;
    private OwnRequestsObserver.OnAccessRequestAccepted mAcceptedListener = null;
    private OwnRequestsObserver.OnAccessRequestRejected mRejectedListener = null;
    private String mPendingAccepedRequest = null;
    private String mPendingRejection = null;
    private Boolean mObserving = Boolean.FALSE;
    private String mOwnPath;
    private String mRequestedGroup;

    public OwnRequestsObserver() {
        mOwnPath = DataManagement.getPathFromEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    public void setOnAccessRequestAcceptedListener(OnAccessRequestAccepted listener) {
        mAcceptedListener = listener;
        handleAcceptedRequest();
    }

    public void setOnAccessRequestRejectedListener(OnAccessRequestRejected listener) {
        mRejectedListener = listener;
        handleRejectedRequest();
    }

    public static OwnRequestsObserver getInstance() {
        sInstance.observe();
        return sInstance;
    }

    public void observe() {
        mRequestedGroup = Settings.Current.getPendingGroupName(BasicApp.getContext());
        if ((!mObserving) && (mRequestedGroup != null) && (!mRequestedGroup.isEmpty()) && (!mRequestedGroup.contentEquals(mOwnPath))) {
            mObserving = Boolean.TRUE;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference requests = mDatabase.child(DataManagement.PENDING_REQUESTS).child(mRequestedGroup);
            requests.addChildEventListener(mPendingRequetsListener);
            requests = mDatabase.child(DataManagement.USERS).child(mRequestedGroup);
            requests.addChildEventListener(mUsersListener);
        }
    }

    private void handleRejectedRequest() {
        if ((mPendingAccepedRequest != null) && (mAcceptedListener != null)) {
            if (mAcceptedListener.handleRequest(mPendingAccepedRequest))
                mPendingAccepedRequest = null;
        }
    }

    private void handleAcceptedRequest() {
        if ((mPendingRejection != null) && (mRejectedListener != null)) {
            if (mRejectedListener.handleRequest(mPendingRejection)) mPendingRejection = null;
        }
    }

    public void informRequestAccepted(String groupName) {
        if (mAcceptedListener != null) {
            if (!mAcceptedListener.handleRequest(groupName)) {
                mPendingAccepedRequest = groupName;
            } else {
                mPendingAccepedRequest = null;
            }
        } else {
            mPendingAccepedRequest = groupName;
        }
    }

    ChildEventListener mUsersListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String path = dataSnapshot.getKey();
            if (path.contentEquals(mOwnPath)) {
                DataManagement.DatabaseState state = path.contentEquals(mRequestedGroup) ? DataManagement.DatabaseState.LIVE : DataManagement.DatabaseState.JOINED;
                DataManagement.getInstance().setConnectivityState(state,mRequestedGroup);
                if (mAcceptedListener != null) {
                    if (!mAcceptedListener.handleRequest(mRequestedGroup)) {
                        mPendingAccepedRequest = mRequestedGroup;
                    } else {
                        mPendingAccepedRequest = null;
                    }
                } else {
                    mPendingAccepedRequest = mRequestedGroup;
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    ChildEventListener mPendingRequetsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String path = dataSnapshot.getKey();
            if (path.contentEquals(mOwnPath)) {
                String currentGroup =  Settings.Current.getGroupName(BasicApp.getContext());
                if ((currentGroup == null) || (currentGroup.isEmpty())) {
                    DataManagement.getInstance().setConnectivityState(DataManagement.DatabaseState.NO_VALID_GROUP,null);
                    // rejected
                    if (mRejectedListener != null) {
                        if (!mRejectedListener.handleRequest(mRequestedGroup)) {
                            mPendingRejection = mRequestedGroup;
                        } else {
                            mPendingRejection = null;
                        }
                    } else {
                        mPendingRejection = mRequestedGroup;
                    }
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

}
