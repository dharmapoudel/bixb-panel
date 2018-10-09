package com.dharmapoudel.bxpanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    boolean mTrackingTouch;
    private SeekBar mSeekBar;
    private TextView mSeekBarValue;
    private int mProgress;
    private TypedArray ta;
    private int mMax, mMin;


    public SeekBarPreference(Context context) {
        this(context, null, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ta = getContext().obtainStyledAttributes(attrs, R.styleable.bxpanel);
        try {
            mMax = ta.getInteger(R.styleable.bxpanel_max, 100);
            mMin = ta.getInteger(R.styleable.bxpanel_min, 0);
            mProgress = ta.getInteger(R.styleable.bxpanel_current, mMin);
        } finally {
            ta.recycle();
        }
        setLayoutResource(R.layout.preference_seekbar);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mSeekBarValue = view.findViewById(R.id.seekbarValue);
        mSeekBar = view.findViewById(R.id.seekbar);

        mSeekBar.setMax(mMax);
        mSeekBar.setMin(mMin);

        mSeekBar.setProgress(mProgress);
        mSeekBarValue.setText(mProgress + "");
        mSeekBar.setOnSeekBarChangeListener(this);

    }


    @Override
    public void onProgressChanged(
            SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && !mTrackingTouch) {
            syncProgress(seekBar);
        } else syncProgress(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mTrackingTouch = true;
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mTrackingTouch = false;
        if (seekBar.getProgress() != mProgress) {
            syncProgress(seekBar);
        }
    }


    // updates the preference once the user lets go of the seekbar
    private void setProgress(int progress, boolean notifyChanged) {
        if (progress != mProgress) {
            mProgress = progress;
            mSeekBarValue.setText(progress + "");
            persistInt(progress);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    void syncProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress != mProgress) {
            if (callChangeListener(progress)) {
                setProgress(progress, false);
            } else {
                seekBar.setProgress(mProgress);
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mProgress) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        if (shouldPersist()) {
            persistInt(value);
        }

        if (value != mProgress) {
            mProgress = value;
            notifyChanged();
        }
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

}