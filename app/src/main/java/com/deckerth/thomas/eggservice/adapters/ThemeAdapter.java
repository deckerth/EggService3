package com.deckerth.thomas.eggservice.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.databinding.ThemeItemBinding;
import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.views.ThemeChooserActivity;
import com.deckerth.thomas.eggservice.views.ThemeClickCallback;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    List<? extends SoundTheme> mThemes;

    @Nullable
    private final ThemeClickCallback mThemeClickCallback;

    @Nullable
    private final ThemeClickCallback mEditClickCallback;

    @Nullable
    private final ThemeClickCallback mCheckedClickCallback;

    public ThemeAdapter(@Nullable ThemeClickCallback clickCallback, @Nullable ThemeClickCallback editCallback, @Nullable ThemeClickCallback checkedCallback) {
        this.mThemeClickCallback = clickCallback;
        this.mEditClickCallback = editCallback;
        this.mCheckedClickCallback = checkedCallback;
    }

    public void setThemeList(final List<? extends SoundTheme> themeList) {
        if (mThemes == null) {
            mThemes = themeList;
            notifyItemRangeInserted(0, themeList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mThemes.size();
                }

                @Override
                public int getNewListSize() {
                    return themeList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mThemes.get(oldItemPosition).mId.contentEquals(themeList.get(newItemPosition).mId);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mThemes.get(oldItemPosition).mId.contentEquals(themeList.get(newItemPosition).mId) &&
                            mThemes.get(oldItemPosition).mName.contentEquals(themeList.get(newItemPosition).mName) &&
                            mThemes.get(oldItemPosition).canBeSelected() == themeList.get(newItemPosition).canBeSelected() &&
                            mThemes.get(oldItemPosition).isRelevantForRandomSelection == themeList.get(newItemPosition).isRelevantForRandomSelection;
                }
            });
            mThemes = themeList;
            result.dispatchUpdatesTo(this);
        }
    }

    static class ThemeViewHolder extends RecyclerView.ViewHolder {

        final ThemeItemBinding binding;

        public ThemeViewHolder(ThemeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ThemeItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.theme_item, parent, false);
        binding.setCallback(mThemeClickCallback);
        binding.setEditCallback(mEditClickCallback);
        binding.setCheckedCallback(mCheckedClickCallback);
        return new ThemeViewHolder((binding));
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        holder.binding.setTheme(mThemes.get(position));
        ThemeChooserActivity.Current.disableThemeSelection();
        holder.binding.executePendingBindings();
        ThemeChooserActivity.Current.enableThemeSelection();
    }


    @Override
    public int getItemCount() {
        return (mThemes == null) ? 0 : mThemes.size();
    }
}
