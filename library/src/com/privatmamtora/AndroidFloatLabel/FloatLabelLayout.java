/*
 * Copyright (C) 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.privatmamtora.AndroidFloatLabel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Layout which an {@link android.widget.EditText} to show a floating label when the hint is hidden
 * due to the user inputting text.
 */
public class FloatLabelLayout extends FrameLayout {

    private static final long ANIMATION_DURATION = 150;
    private static final float DEFAULT_PADDING_LEFT_RIGHT_DP = 12f;

    private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
    private static final String SAVED_LABEL_VISIBILITY = "SAVED_LABEL_VISIBILITY";
    private static final String SAVED_HINT = "SAVED_HINT";

    private EditText mEditText;
    private TextView mLabel;

    int mLabelGap;
    int mLabelGravity;
    int mLabelSidePadding;
    int mLabelAppearance;
    int mLabelAnimationDuration;


    private CharSequence mHint;

    private Context mContext;

    public FloatLabelLayout(Context context) {
        super(context);
        mContext = context;
        initialize();
    }

    public FloatLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setAttributes(attrs);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FloatLabelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setAttributes(attrs);
        initialize();
    }

    private void setAttributes(AttributeSet attrs) {
        final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.FloatLabelLayout);
        try {
            mLabelAppearance = a.getResourceId(R.styleable.FloatLabelLayout_flTextAppearance, android.R.style.TextAppearance_Small);
            mLabelSidePadding = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flSidePadding, dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP));

            mLabelGap = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flGapSize, 0);
            mLabelGravity = a.getInt(R.styleable.FloatLabelLayout_flGravity, 0x03);

            mLabelAnimationDuration = a.getInt(R.styleable.FloatLabelLayout_flAnimationDuaration, -1);
        } finally {
            a.recycle();
        }
    }

    private void initialize() {

        mLabel = new TextView(mContext);

        // Set style first so that everything else will overwrite it
        setLabelAppearance(mContext, mLabelAppearance);
        setLabelSingleLine(true);

        setLabelPadding(mLabelSidePadding, 0, mLabelSidePadding, 0);

        setLabelGravity(mLabelGravity);

        setLabelVisibility(INVISIBLE);
        addView(mLabel, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(SAVED_LABEL_VISIBILITY, mLabel.getVisibility());
        bundle.putCharSequence(SAVED_HINT, mHint);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            int vis = bundle.getInt(SAVED_LABEL_VISIBILITY);
            setLabelVisibility(vis);

            mHint = bundle.getCharSequence(SAVED_HINT);

            // retrieve super state
            state = bundle.getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            // If we already have an EditText, throw an exception
            if (mEditText != null) {
                throw new IllegalArgumentException("We already have an EditText, can only have one");
            }

            // Update the layout params so that the EditText is at the bottom, with enough top
            // margin to show the label
            final LayoutParams lp = new LayoutParams(params);

            lp.gravity = Gravity.BOTTOM;
            lp.topMargin = (int) mLabel.getTextSize() + mLabelGap;
            params = lp;

            setEditText((EditText) child);
        }

        // Carry on adding the View...
        super.addView(child, index, params);
    }

    protected void setEditText(EditText editText) {
        mEditText = editText;

        setLabelText(mEditText.getHint());

        if (mHint == null) {
            mHint = mEditText.getHint();
        }

        // Add a TextWatcher so that we know when the text input has changed
        mEditText.addTextChangedListener(mTextWatcher);

        // Add focus listener to the EditText so that we can notify the label that it is activated.
        // Allows the use of a ColorStateList for the text color on the label
        mEditText.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    /**
     * @return the {@link android.widget.EditText} text input
     */
    public EditText getEditText() {
        return mEditText;
    }

    /**
     * @return the {@link android.widget.TextView} label
     */
    public TextView getLabel() {
        return mLabel;
    }

    private void showLabel(final boolean show) {
        AnimatorSet animation = null;
        if ((mLabel.getVisibility() == VISIBLE) && !show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mLabel, "translationY", 0,
                    mLabel.getHeight() / 8);
            ObjectAnimator fade = ObjectAnimator.ofFloat(mLabel, "alpha", 1, 0);
            animation.playTogether(move, fade);
        }
        else if ((mLabel.getVisibility() != VISIBLE) && show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mLabel, "translationY",
                    mLabel.getHeight() / 8, 0);
            ObjectAnimator fade = ObjectAnimator.ofFloat(mLabel, "alpha", 0, 1);
            animation.playTogether(move, fade);
        }

        if (animation != null) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mLabel.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mLabel.setVisibility(show ? VISIBLE : INVISIBLE);
                }
            });

            if(mLabelAnimationDuration >= 0) {
                animation.setDuration(mLabelAnimationDuration);
            }
            animation.start();
        }
    }

    /**
     * Label helper function
     */
    public void setLabelTypeFace(Typeface tf) {
        mLabel.setTypeface(tf);
    }

    public void setLabelTypeFace(Typeface tf, int style) {
        mLabel.setTypeface(tf, style);
    }

    public void setLabelAppearance(Context c, int styleResourceId) {
        mLabel.setTextAppearance(c, styleResourceId);
    }

    public void setLabelPadding(int left, int top, int right, int bottom) {
        mLabel.setPadding(left, top, right, bottom);
    }

    public void setLabelGravity(int gravity) {
        mLabel.setGravity(gravity);
    }

    public void setLabelSingleLine(boolean singleLine) {
        mLabel.setSingleLine(singleLine);
    }

    public void setLabelVisibility(int visibility) {
        mLabel.setVisibility(visibility);
    }

    public void setLabelText(CharSequence text) {
        mLabel.setText(text);
    }
    /**
     * Helper method to convert dips to pixels.
     */
    private int dipsToPix(float dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                getResources().getDisplayMetrics());
    }

    /**
     * Listeners
     */
    final private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean focused) {
            if(isHcOrAbove()) {
                mLabel.setActivated(focused);
            }
        }
    };

    final private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            showLabel(s.length() != 0);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    };

    /**
     * Helper method to simplify version checking.
     */
    private static boolean isIcsOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private static boolean isHcOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
