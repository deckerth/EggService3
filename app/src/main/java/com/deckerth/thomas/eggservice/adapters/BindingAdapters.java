package com.deckerth.thomas.eggservice.adapters;

import android.content.res.ColorStateList;
import androidx.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.ringtones.SoundTheme;
import com.deckerth.thomas.eggservice.views.MainActivity;

public class BindingAdapters {

    private static ColorStateList mTextColors = null;

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("setView")
    public static void setView(View view, SoundTheme theme) {
        theme.setMenuButton(view);
    }

    @BindingAdapter("startStop")
    public static void startStop(TextView view, boolean isRunning) {
        view.setText(isRunning ? MainActivity.Current.getText(R.string.stop) : MainActivity.Current.getText(R.string.start));
    }

    @BindingAdapter("inProgress")
    public static void highlightInProgress(TextView view, boolean inProgress) {
        if (mTextColors == null) {
            TextView dummy = new TextView(MainActivity.Current.getApplicationContext());
            mTextColors = dummy.getTextColors();
        }
        view.setTextColor(inProgress ? MainActivity.Current.getColor(R.color.colorAccent) :
                                       mTextColors.getDefaultColor());
        view.setTypeface(null, inProgress ? Typeface.BOLD : Typeface.NORMAL);
    }

}