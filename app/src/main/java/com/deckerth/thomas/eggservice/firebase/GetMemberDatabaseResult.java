package com.deckerth.thomas.eggservice.firebase;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class GetMemberDatabaseResult extends DataManagementResult {

    private DatabaseReference mMemberDatabase;

    public DatabaseReference getMemberDatabase() {
        return mMemberDatabase;
    }

    public GetMemberDatabaseResult(DataManagement.DatabaseState state) {
        super(state);
    }

    public GetMemberDatabaseResult(DataManagement.DatabaseState state, DatabaseError error) {
        super(state,error);
    }

    public GetMemberDatabaseResult(DataManagement.DatabaseState state, DatabaseReference database) {
        super(state);
        mMemberDatabase = database;
    }

}
