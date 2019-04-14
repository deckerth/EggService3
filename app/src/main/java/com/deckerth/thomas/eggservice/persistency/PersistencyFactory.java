package com.deckerth.thomas.eggservice.persistency;

import com.deckerth.thomas.eggservice.firebase.DataManagement;

public class PersistencyFactory {

    private static DataAccess sInstance;

    public static DataAccess getInstance(DataRepository repo) {
        if (DataManagement.getInstance().isConnectedToFirebase(true)) {
            if ((sInstance == null) || !sInstance.isOnline()) sInstance = new FirebaseAccess(repo);
        } else {
            if ((sInstance == null) || sInstance.isOnline()) sInstance = new OfflineAccess(repo);
        }
        return sInstance;
    }
}
