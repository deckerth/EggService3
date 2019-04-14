package com.deckerth.thomas.eggservice.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.adapters.MemberAdapter;
import com.deckerth.thomas.eggservice.databinding.MemberListFragmentBinding;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.model.MemberEntity;
import com.deckerth.thomas.eggservice.viewmodel.MemberListViewModel;

import java.util.List;

public class MemberListFragment extends Fragment {

    public static final String TAG = "MemberListFragment";

    private MemberAdapter mMemberAdapter;
    private MemberListFragmentBinding mBinding;
    private MemberListViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.member_list_fragment, container, false);

        mMemberAdapter = new MemberAdapter(mMemberClickCallback, mDeleteMemberClickCallback);
        mBinding.membersList.setAdapter(mMemberAdapter);

        FloatingActionButton fab = mBinding.getRoot().findViewById(R.id.fab);
        fab.setOnClickListener(mFloatingButtonListener);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MemberListViewModel.class);

        subscribeUi(mViewModel);
    }

    private void subscribeUi(MemberListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getMembers().observe(this, new Observer<List<MemberEntity>>() {
            @Override
            public void onChanged(@Nullable List<MemberEntity> myMembers) {
                if (myMembers != null) {
                    mMemberAdapter.setMemberList(myMembers);
                } else {
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                mBinding.executePendingBindings();
            }
        });
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean myIsLoading) {
                if (myIsLoading != null) {
                    mBinding.setIsLoading(myIsLoading);
                }
                mBinding.executePendingBindings();
            }
        });
    }

    private FloatingActionButton.OnClickListener mFloatingButtonListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            CreateMemberDialog dialog = CreateMemberDialog.newInstance("");
            FragmentManager manager = getFragmentManager();
            if (manager != null) dialog.show(manager, TAG);
        }
    };

    private final MemberClickCallback mMemberClickCallback = new MemberClickCallback() {
        @Override
        public void onClick(Member member) {
            EditGustoDialog dialog = EditGustoDialog.newInstance(member.getName());
            FragmentManager manager = getFragmentManager();
            if (manager != null) dialog.show(manager, TAG);
        }
    };

    private final MemberClickCallback mDeleteMemberClickCallback = new MemberClickCallback() {
        @Override
        public void onClick(Member member) {
            DataRepository.getPersistency().deleteMember(member);
        }
    };
}
