package com.deckerth.thomas.eggservice.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.util.Locale;

public class ChronometerWithPause extends Chronometer {

    private static final String sScheme =
            "http://schemas.android.com/apk/res-auto";
    private static final String sAttribute = "customFont";

    private long timeWhenStopped = 0;
    private boolean isRunning = false;

    static enum CustomFont {
        DIGITAL_MONO("fonts/digital-7-mono.ttf");

        private final String fileName;

        CustomFont(String fileName) {
            this.fileName = fileName;
        }

        static ChronometerWithPause.CustomFont fromString(String fontName) {
            return ChronometerWithPause.CustomFont.valueOf(fontName.toUpperCase(Locale.US));
        }

        public Typeface asTypeface(Context context) {
            return Typeface.createFromAsset(context.getAssets(), fileName);
        }
    }

    private final String getTimeKey() {
        return "KEY_TIMER_TIME" + getId();
    }
    private final String getIsRunningKey() {
        return "KEY_TIMER_RUNNING" + getId();
    }

    public ChronometerWithPause(Context context)
    {
        super(context);
    }

    public ChronometerWithPause(Context context, AttributeSet attrs) {
        super(context, attrs);
        final String fontName = attrs.getAttributeValue(sScheme, sAttribute);

        if (fontName != null) {
            final Typeface customTypeface = ChronometerWithPause.CustomFont.fromString(fontName).asTypeface(context);
            setTypeface(customTypeface);
        }
    }

    public ChronometerWithPause(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void start() {
        setBase(SystemClock.elapsedRealtime());
        isRunning = true;
        super.start();
    }

    public void resume() {
        isRunning = true;
        super.start();
    }

    @Override
    public void stop() {
        isRunning = false;
        timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
        super.stop();
    }

    public void reset() {
        stop();
        isRunning = false;
        setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getCurrentTime() {
        return timeWhenStopped;
    }

    public void setCurrentTime(long time) {
        timeWhenStopped = time;
        setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
    }

    public void saveInstanceState(Bundle outState) {
        if (isRunning) {
            timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
        }
        outState.putLong(getTimeKey(), getCurrentTime());
        outState.putBoolean(getIsRunningKey(), isRunning());
    }

    public void restoreInstanceState(Bundle inState) {
        if (inState != null) {
            isRunning = inState.getBoolean(getIsRunningKey());
            setCurrentTime(inState.getLong(getTimeKey()));
            timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
            if (isRunning) {
                super.start();
            }
        }
    }
}