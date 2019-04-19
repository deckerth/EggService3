package com.deckerth.thomas.eggservice.firebase;

/*
{
  "groups": {
    "groupowner1@email.com" : TRUE,
    "groupowner2@email.com" : TRUE
  },
  "users": {
    "groupowner1@email.com" : {
      "groupowner1@email.com" : TRUE,
      "groupmember2@email.com" : TRUE
    },
    "groupowner2@email.com" : {
      "groupowner2@email.com" : TRUE
    }
  },
  "pending_requests": {
    "groupowner1@email.com" : {
      "askingmember1@email.com" : "Name 1"
    }
  },
  "members" : {
    "groupowner1@email.com" : {
      "Person 1" : "SOFT",
      "Person 2" : "Medium"
    },
    "groupowner2@email.com" : {
      "Person 3" : "SOFT",
      "Person 4" : "Medium"
    }
  }
}
*/

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.settings.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class DataManagement {

    public enum DatabaseState {LIVE, JOINED, NO_VALID_GROUP, REQUEST_PENDING, NOT_AUTHORIZED};
    public enum JobState {FAILED, COMPLETE, IN_PROGRESS};

    public static final String GROUPS = "groups";
    public static final String USERS = "users";
    public static final String MEMBERS = "members";
    public static final String PENDING_REQUESTS = "pending_requests";

    private static DataManagement sInstance;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseState mConnectivityState = DatabaseState.NO_VALID_GROUP;

    public DatabaseState getConnectivityState() {
        return mConnectivityState;
    }

    public String getStateText() {
        switch (mConnectivityState) {
            case JOINED:
                return BasicApp.getContext().getString(R.string.state_joined, Settings.Current.getGroupName(BasicApp.getContext()));
            case LIVE:
                return BasicApp.getContext().getString(R.string.state_live, Settings.Current.getGroupName(BasicApp.getContext()));
            case NO_VALID_GROUP: case NOT_AUTHORIZED:
                return BasicApp.getContext().getString(R.string.state_not_connected);
            case REQUEST_PENDING:
                return BasicApp.getContext().getString(R.string.state_request_pending,Settings.Current.getPendingGroupName(BasicApp.getContext()));
        }
        return "";
    }

    public String getGroupName() {
        switch (mConnectivityState) {
            case JOINED: case LIVE:
                return Settings.Current.getGroupName(BasicApp.getContext());
            case NO_VALID_GROUP: case NOT_AUTHORIZED:
                return "";
            case REQUEST_PENDING:
                return Settings.Current.getPendingGroupName(BasicApp.getContext());
        }
        return "";
    }

    public void setConnectivityState(DatabaseState connectivityState, String groupName) {
        this.mConnectivityState = connectivityState;
        switch (mConnectivityState) {
            case JOINED:
            case LIVE:
                Settings.Current.setGroupName(BasicApp.getContext(), groupName);
                Settings.Current.setPendingGroupName(BasicApp.getContext(), null);
                OwnRequestsObserver.getInstance().informRequestAccepted(groupName);
                break;
            case NO_VALID_GROUP:
            case NOT_AUTHORIZED:
                Settings.Current.setGroupName(BasicApp.getContext(), null);
                Settings.Current.setPendingGroupName(BasicApp.getContext(), null);
                break;
            case REQUEST_PENDING:
                Settings.Current.setGroupName(BasicApp.getContext(), null);
                Settings.Current.setPendingGroupName(BasicApp.getContext(), groupName);
                OwnRequestsObserver.getInstance().observe();
                break;
        }
    }

    public DataManagement() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Boolean isConnectedToFirebase(Boolean checkGroup) {
        mAuth = FirebaseAuth.getInstance();
        String groupName = Settings.Current.getGroupName(BasicApp.getContext());
        if (checkGroup) {
            return (mAuth != null) && (mAuth.getUid() != null) &&
                    (groupName != null) && (!groupName.isEmpty());
        } else {
            return (mAuth != null) && (mAuth.getUid() != null);
        }
    }

    public static DataManagement getInstance() {
        if (sInstance == null) sInstance = new DataManagement();
        return sInstance;
    }

   public static String getPathFromEmail(String email) {
        // Firebase Database paths must not contain '.', '#', '$', '[', or ']'
       return email.replace(".", ",");
   }

    public static String getEmailFromPath(String path) {
        return path.replace(",", ".");
    }

    public void createGroup() {
        String group = getPathFromEmail(mAuth.getCurrentUser().getEmail());
        mDatabase.child(GROUPS).child(group).setValue(Boolean.TRUE);
        mDatabase.child(USERS).child(group).child(group).setValue(Boolean.TRUE);
        DataManagement.getInstance().setConnectivityState(DatabaseState.LIVE,group);
   }

   public void removeRequest(String path) {
        String group = getPathFromEmail(mAuth.getCurrentUser().getEmail());
        mDatabase.child(PENDING_REQUESTS).child(group).child(path).removeValue();
   }

   public void deleteGroup() {
       String ownPath = getPathFromEmail(mAuth.getCurrentUser().getEmail());
       String group = Settings.Current.getGroupName(BasicApp.getContext());
       if ((group != null) && group.contentEquals(ownPath)) {
           mDatabase.child(GROUPS).child(group).removeValue();
           mDatabase.child(USERS).child(group).removeValue();
           mDatabase.child(PENDING_REQUESTS).child(group).removeValue();
           mDatabase.child(MEMBERS).child(group).removeValue();
           DataManagement.getInstance().setConnectivityState(DatabaseState.NO_VALID_GROUP,null);
       }
   }

    public void acceptRequest(String path) {
        String group = getPathFromEmail(mAuth.getCurrentUser().getEmail());
        mDatabase.child(USERS).child(group).child(path).setValue(Boolean.TRUE);
    }

    public void leaveGroup() {
        String userPath = getPathFromEmail(mAuth.getCurrentUser().getEmail());
        String group = Settings.Current.getGroupName(BasicApp.getContext());
        mDatabase.child(USERS).child(group).child(userPath).removeValue();
        DataManagement.getInstance().setConnectivityState(DatabaseState.NO_VALID_GROUP,null);
    }

}
