package com.deckerth.thomas.eggservice.firebase;

import com.google.firebase.database.DatabaseError;

public class DataManagementResult {


    protected DataManagement.DatabaseState mState;

    public DataManagement.DatabaseState getState() {
        return mState;
    }

    protected DatabaseError mDatabaseError;

    public DatabaseError getDatabaseError() {
        return mDatabaseError;
    }

    public DataManagementResult (DataManagement.DatabaseState state) {
        mState = state;
    }

    public DataManagementResult (DataManagement.DatabaseState state, DatabaseError error) {
        mState = state;
        mDatabaseError = error;
    }

}
