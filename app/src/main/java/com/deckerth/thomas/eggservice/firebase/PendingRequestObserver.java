package com.deckerth.thomas.eggservice.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestObserver {

    public interface OnAccessRequestArrived {
        Boolean handleRequest(String requestingUserPath);
    }

    private static PendingRequestObserver sInstance = new PendingRequestObserver();

    private DatabaseReference mDatabase;
    private OnAccessRequestArrived mListener = null;
    private List<String> mPendingRequests = new ArrayList<>();
    private Boolean mObserving = Boolean.FALSE;

    public void setOnAccessRequestArrivedListener(OnAccessRequestArrived listener) {
        mListener = listener;
        handleNextRequest();
    }

    public void observe() {
        if ((!mObserving) && (DataManagement.getInstance().getConnectivityState() == DataManagement.DatabaseState.LIVE)) {
            mObserving = Boolean.TRUE;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference requests =  mDatabase.child(DataManagement.PENDING_REQUESTS).child(DataManagement.getPathFromEmail(email));
            requests.addChildEventListener(mChildEventListener);
        }
    }

    public void addRequest(String request) {
        mPendingRequests.add(request);
    }

    public static PendingRequestObserver getInstance() {
        sInstance.observe();
        return sInstance;
    }

    public void handleNextRequest() {
        if (mPendingRequests.size() > 0) {
            String request = mPendingRequests.get(0);
            if ((mListener != null) && (mListener.handleRequest(request))) mPendingRequests.remove(request);
        }
    }

    ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            String path = dataSnapshot.getKey();
            if (mListener != null) {
                if (mListener.handleRequest(path)) return;
            }
            mPendingRequests.add(path);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String path = dataSnapshot.getKey();
            String toDelete = null;
            for (int i=0; i<mPendingRequests.size(); i++) {
                if (mPendingRequests.get(i).contentEquals(path)) {
                    toDelete = mPendingRequests.get(i);
                    break;
                }
            }
            if (toDelete != null) mPendingRequests.remove(toDelete);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

}
