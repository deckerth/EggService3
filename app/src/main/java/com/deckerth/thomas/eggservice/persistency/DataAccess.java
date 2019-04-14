package com.deckerth.thomas.eggservice.persistency;

import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;

public interface DataAccess {

    Boolean isOnline();

    void loadMembers();

    void addMember(MemberEntity member);

    void updateMember(MemberEntity member);

    void deleteMember(Member member);

}
