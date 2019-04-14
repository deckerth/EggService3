package com.deckerth.thomas.eggservice.persistency;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.deckerth.thomas.eggservice.firebase.DataManagement;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;

import java.util.List;

public class DataRepository implements Persistency {

    private static DataRepository sInstance;

    final MutableLiveData<List<MemberEntity>> mObservableMembers;
    final MutableLiveData<Boolean> mIsLoading;
    final MutableLiveData<Integer> mSoftEggsCount;
    final MutableLiveData<Integer> mMediumEggsCount;
    final MutableLiveData<Integer> mHardEggsCount;

    private DataAccess mDataAccess;

    public DataRepository() {
        mObservableMembers = new MutableLiveData<>();
        // set by default null, until we get data from the database.
        mObservableMembers.setValue(null);

        mIsLoading = new MutableLiveData<>();
        mIsLoading.setValue(Boolean.FALSE);

        mSoftEggsCount = new MutableLiveData<>();
        mSoftEggsCount.setValue(0);
        mMediumEggsCount = new MutableLiveData<>();
        mMediumEggsCount.setValue(0);
        mHardEggsCount = new MutableLiveData<>();
        mHardEggsCount.setValue(0);

        mDataAccess = PersistencyFactory.getInstance(this);
    }

    static DataRepository getInstance() {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository();
                }
            }
        }
        sInstance.checkPersistencyComponent();
        return sInstance;
    }

    public static Persistency getPersistency() {
        return getInstance();
    }

    public void checkPersistencyComponent() {
        if (DataManagement.getInstance().isConnectedToFirebase(true) != mDataAccess.isOnline()) {
            mDataAccess = PersistencyFactory.getInstance(this);
        }
    }

    @Override
    public Boolean isOnline() {
        return mDataAccess.isOnline();
    }

    @Override
    public void loadMembers() {
        mDataAccess.loadMembers();
        computeGustoSummary();
    }

    @Override
    public MemberEntity getMember(String name) {
        List<MemberEntity> members = mObservableMembers.getValue();
        if (members == null) return null;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getName().contentEquals(name.trim())) {
                return members.get(i);
            }
        }
        return null;
    }

    @Override
    public void addMember(MemberEntity member) {
        mDataAccess.addMember(member);
        computeGustoSummary();
    }

    @Override
    public void updateMember(MemberEntity member) {
        mDataAccess.updateMember(member);
        computeGustoSummary();
    }

    @Override
    public void deleteMember(Member member) {
        mDataAccess.deleteMember(member);
        computeGustoSummary();
    }

    @Override
    public LiveData<List<MemberEntity>> getMembers() {
        return mObservableMembers;
    }

    @Override
    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    @Override
    public LiveData<Integer> getSoftEggsCount() {
        return mSoftEggsCount;
    }

    @Override
    public LiveData<Integer> getMediumEggsCount() {
        return mMediumEggsCount;
    }

    @Override
    public LiveData<Integer> getHardEggsCount() {
        return mHardEggsCount;
    }

    public void computeGustoSummary() {

        Integer softEggsCount = 0;
        Integer mediumEggsCount = 0;
        Integer hardEggsCount = 0;
        List<MemberEntity> members = mObservableMembers.getValue();
        if (members != null) {
            for (int i = 0; i < members.size(); i++) {
                Member.Gusto theGusto = members.get(i).getGusto();
                switch (theGusto) {
                    case SOFT:
                        softEggsCount++;
                        break;
                    case MEDIUM:
                        mediumEggsCount++;
                        break;
                    case HARD:
                        hardEggsCount++;
                        break;
                }
            }
        }
        mSoftEggsCount.setValue(softEggsCount);
        mMediumEggsCount.setValue(mediumEggsCount);
        mHardEggsCount.setValue(hardEggsCount);
    }

}
