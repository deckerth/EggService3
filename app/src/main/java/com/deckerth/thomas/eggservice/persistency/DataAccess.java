package com.deckerth.thomas.eggservice.persistency;

import android.os.AsyncTask;

import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;

public interface DataAccess {

    Boolean isOnline();

    AsyncTask.Status getLoadMembersTaskState();

    void loadMembers(Boolean fromScratch);

    void addMember(MemberEntity member);

    void updateMember(MemberEntity member);

    void deleteMember(Member member);

    void startFirebaseListeners();
    void stopFirebaseListeners();

}
