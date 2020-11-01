package com.deckerth.thomas.eggservice.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.databinding.MemberItemBinding;
import com.deckerth.thomas.eggservice.model.Member;
import com.deckerth.thomas.eggservice.views.MemberClickCallback;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder>{

    List<? extends Member> mMemberList;

    @Nullable
    private final MemberClickCallback mMemberClickCallback;

    @Nullable
    private final MemberClickCallback mDeleteMemberClickCallback;

    public MemberAdapter(@Nullable MemberClickCallback clickCallback, @Nullable MemberClickCallback deleteCallback) {
        mMemberClickCallback = clickCallback;
        mDeleteMemberClickCallback = deleteCallback;
    }

    public void setMemberList(final List<? extends Member> memberList) {
        if (mMemberList == null) {
            mMemberList = memberList;
            notifyItemRangeInserted(0, memberList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mMemberList.size();
                }

                @Override
                public int getNewListSize() {
                    return memberList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mMemberList.get(oldItemPosition).getName().contentEquals(memberList.get(newItemPosition).getName());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Member newMember = memberList.get(newItemPosition);
                    Member oldMember = mMemberList.get(oldItemPosition);
                    return newMember.getName().contentEquals(oldMember.getName())
                            && newMember.getGusto() == oldMember.getGusto();
                }
            });
            mMemberList = memberList;
            result.dispatchUpdatesTo(this);
        }
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {

        final MemberItemBinding binding;

        public MemberViewHolder(MemberItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MemberItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.member_item,
                        parent, false);
        binding.setCallback(mMemberClickCallback);
        binding.setDeleteCallback(mDeleteMemberClickCallback);
        return new MemberViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        holder.binding.setMember(mMemberList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mMemberList == null ? 0 : mMemberList.size();
    }
}
