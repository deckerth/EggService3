package com.deckerth.thomas.eggservice.persistency;

import androidx.lifecycle.LiveData;

import com.deckerth.thomas.eggservice.model.MemberEntity;

import java.util.List;

public interface Persistency extends DataAccess{

    LiveData<List<MemberEntity>> getMembers();

    LiveData<Boolean> getIsLoading();

    LiveData<Integer> getSoftEggsCount();

    LiveData<Integer> getMediumEggsCount();

    LiveData<Integer> getHardEggsCount();

    MemberEntity getMember(String name);

}
