package com.deckerth.thomas.eggservice.persistency;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.ArraySet;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class OfflineAccess implements DataAccess {

    SharedPreferences mSharedPref;
    private DataRepository mRepo;

    private static final String MEMBERS = "members";

    OfflineAccess(DataRepository repo) {
        mRepo = repo;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(BasicApp.getContext());
    }

    @Override
    public Boolean isOnline() {
        return Boolean.FALSE;
    }

    @Override
    public void loadMembers() {
        Set<String> members = mSharedPref.getStringSet(MEMBERS, null);
        if (members != null) {
            List<MemberEntity> list = new ArrayList<MemberEntity>();
            Iterator<String> iterator = members.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                String valueStr = mSharedPref.getString(name,"NO_EGG");
                MemberEntity newMember = new MemberEntity(name, valueStr);
                list.add(newMember);
            }
            mRepo.mObservableMembers.setValue(list);
        }
    }

    @Override
    public void addMember(MemberEntity member) {
        // update settings
        Set<String> memberNames = mSharedPref.getStringSet(MEMBERS, null);
        if (memberNames == null) memberNames = new ArraySet<>();
        memberNames.add(member.getName());
        mSharedPref.edit().putStringSet(MEMBERS, memberNames).apply();
        mSharedPref.edit().putString(member.getName(),member.getGustoSymbol()).apply();

        // update collection
        List<MemberEntity> members = mRepo.mObservableMembers.getValue();
        if (members == null) members = new ArrayList<MemberEntity>();
        members.add(member);
        mRepo.mObservableMembers.setValue(members);
    }

    @Override
    public void updateMember(MemberEntity member) {
        // update settings
        mSharedPref.edit().putString(member.getName(),member.getGustoSymbol()).apply();

        // update collection
        List<MemberEntity> members = mRepo.mObservableMembers.getValue();
        List<MemberEntity> list = new ArrayList<MemberEntity>();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getName().contentEquals(member.getName())) {
                list.add(member);
            } else {
                list.add(members.get(i));
            }
        }
        mRepo.mObservableMembers.setValue(list);
    }

    @Override
    public void deleteMember(Member member) {
        // delete from name list
        Set<String> memberNames = mSharedPref.getStringSet(MEMBERS, null);
        if (memberNames != null) {
            String nameToDelete = null;
            Iterator<String> iterator = memberNames.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                if (name.contentEquals(member.getName())) {
                    nameToDelete = name;
                    break;
                }
            }
            if (nameToDelete != null) memberNames.remove(nameToDelete);
            mSharedPref.edit().putStringSet(MEMBERS, memberNames).apply();
        }

        // delete gusto setting
        mSharedPref.edit().remove(member.getName()).apply();

        // delete from collection
        List<MemberEntity> members = mRepo.mObservableMembers.getValue();
        List<MemberEntity> list = new ArrayList<MemberEntity>();
        for (int i = 0; i < members.size(); i++) {
            if (!members.get(i).getName().contentEquals(member.getName())) {
                list.add(members.get(i));
            }
        }
        mRepo.mObservableMembers.setValue(list);
    }
}
