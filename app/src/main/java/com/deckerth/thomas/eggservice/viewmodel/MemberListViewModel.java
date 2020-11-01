package com.deckerth.thomas.eggservice.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.model.MemberEntity;
import com.deckerth.thomas.eggservice.persistency.Persistency;

import java.util.List;

public class MemberListViewModel extends AndroidViewModel {

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<MemberEntity>> mObservableMembers;
    private final MediatorLiveData<Boolean> mIsLoading;

    public static MemberListViewModel Current;

    public MemberListViewModel(Application application) {
        super(application);
        Current = this;

        mObservableMembers = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableMembers.setValue(null);

        mIsLoading = new MediatorLiveData<>();
        mIsLoading.setValue(false);

        Persistency repo = DataRepository.getPersistency();

        // observe the changes  from the database and forward them
        LiveData<List<MemberEntity>> members = repo.getMembers();
        mObservableMembers.addSource(members, mObservableMembers::setValue);

        LiveData<Boolean> flag = repo.getIsLoading();
        mIsLoading.addSource(flag, mIsLoading::setValue);
    }

    /**
     * Expose the LiveData Bookings query so the UI can observe it.
     */
    public LiveData<List<MemberEntity>> getMembers() {
        return mObservableMembers;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

}
