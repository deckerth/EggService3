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

    private long alarmTimeOffset = 0;
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
    private final String getAlarmTimeOffsetKey() {
        return "KEY_ALARM_TIME_OFFSET" + getId();
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

    public void start(int seconds) {
        alarmTimeOffset = seconds * 1000;
        setBase(SystemClock.elapsedRealtime() + alarmTimeOffset);
        // current duration the timer is running: SystemClock.elapsedRealtime() - getBase() + alarmTimeOffset
        //
        // Example:
        //
        // After start(10):
        //  T := SystemClock.elapsedRealtime()
        //  getBase() = T + 10000;
        //
        // after x seconds:
        //
        //  Tx := SystemClock.elapsedRealtime()
        //  Tx = T + x * 1000
        //
        //  Tx - getBase() = T + x*1000 - T - 100000 = x*1000 - 10000
        //  => x*1000 = Tx - getBase() + 100000
        setCountDown(true);
        isRunning = true;
        super.start();
    }

    @Override
    public void setBase(long base) {
        super.setBase(base);
    }

    @Override
    public long getBase() {
        return super.getBase();
    }

    @Override
    public void start() {
        alarmTimeOffset = 0;
        setBase(SystemClock.elapsedRealtime());
        // current duration the timer is running: SystemClock.elapsedRealtime() - getBase()
        // Example:
        //
        // After start():
        //  T := SystemClock.elapsedRealtime()
        //  getBase() = T;
        //
        // after x seconds:
        //
        //  Tx := SystemClock.elapsedRealtime()
        //  Tx = T + x * 1000
        //
        //  Tx - getBase() = T + x*1000 - T = x*1000
        //  => x*1000 = Tx - getBase()
        //
        setCountDown(false);
        isRunning = true;
        super.start();
    }

    public void resume() {
        isRunning = true;
        super.start();
    }

    public long getNormalizedBase() {
        return getBase() - alarmTimeOffset;
    }

    @Override
    public void stop() {
        isRunning = false;
        super.stop();
    }

    private boolean isRunning() {
        return isRunning;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putLong(getTimeKey(), getBase());
        outState.putLong(getAlarmTimeOffsetKey(), alarmTimeOffset);
        outState.putBoolean(getIsRunningKey(), isRunning());
    }

    public void restoreInstanceState(Bundle inState) {
        if (inState != null) {
            isRunning = inState.getBoolean(getIsRunningKey());
            alarmTimeOffset = inState.getLong(getAlarmTimeOffsetKey());
            if (isRunning) {
                setBase(inState.getLong(getTimeKey()));
                setCountDown(true);
                super.start();
            }
        }
    }
}