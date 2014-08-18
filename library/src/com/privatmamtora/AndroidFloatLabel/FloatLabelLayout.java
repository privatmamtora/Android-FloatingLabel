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
import android.content.res.ColorStateList;
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

    // Enum for the "typeface" XML parameter.
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    private EditText mEditText;
    private TextView mLabel;

    int mLabelGap;
    int mLabelGravity;
    //int mLabelSidePadding;

    int mLabelPadding;
    int mLabelPaddingLeft;
    int mLabelPaddingRight;
    int mLabelPaddingTop;
    int mLabelPaddingBottom;
    int mLabelPaddingStart;
    int mLabelPaddingEnd;

    int mLabelAppearance;
    int mTypeface;
    String mFontFamily;
    int mTextStyle;
    private ColorStateList mLabelColor;

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
            mFontFamily = a.getString(R.styleable.FloatLabelLayout_flFontFamily);
            mTypeface = a.getInt(R.styleable.FloatLabelLayout_flTypeface, -1);
            mTextStyle = a.getInt(R.styleable.FloatLabelLayout_flTextStyle, -1);

            mLabelPadding = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPadding, 0);
            mLabelPaddingLeft = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPaddingLeft, mLabelPadding == 0 ? dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP) : mLabelPadding);
            mLabelPaddingRight = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPaddingRight, mLabelPadding == 0 ? dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP) : mLabelPadding);
            mLabelPaddingTop = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPaddingTop, mLabelPadding);
            mLabelPaddingBottom = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPaddingBottom, mLabelPadding);

            if(isJb1OrAbove()) {
                mLabelPaddingStart = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPaddingStart, mLabelPadding == 0 ? dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP) : mLabelPadding);
                mLabelPaddingEnd = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flPaddingEnd, mLabelPadding == 0 ? dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP) : mLabelPadding);
            }

            //mLabelSidePadding = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_sidePadding, dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP));
            mLabelGap = a.getDimensionPixelSize(R.styleable.FloatLabelLayout_flGapSize, 0);
            mLabelGravity = a.getInt(R.styleable.FloatLabelLayout_flGravity, 0x03);

            mLabelColor = a.getColorStateList(R.styleable.FloatLabelLayout_flTextColor);

            mLabelAnimationDuration = a.getInt(R.styleable.FloatLabelLayout_flAnimationDuration, -1);
        } finally {
            a.recycle();
        }
    }

    private void initialize() {

        mLabel = new TextView(mContext);

        // Set style first so that everything else will overwrite it
        setLabelAppearance(mContext, mLabelAppearance);
        setTypefaceFromAttrs(mFontFamily, mTypeface, mTextStyle);

        //Default Label is Single Line
        setLabelSingleLine(true);

        if(isJb1OrAbove()) {
            setLabelPaddingRelative(mLabelPaddingStart, mLabelPaddingTop, mLabelPaddingEnd, mLabelPaddingBottom);
        } else {
            setLabelPadding(mLabelPaddingLeft, mLabelPaddingTop, mLabelPaddingRight, mLabelPaddingBottom);
        }

        setLabelGravity(mLabelGravity);
        mLabel.setTextColor(mLabelColor != null ? mLabelColor : getResources().getColorStateList(R.color.floatlabel_default));

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

    private void setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        if (familyName != null) {
            tf = Typeface.create(familyName, styleIndex);
            if (tf != null) {
                setLabelTypeface(tf);
                return;
            }
        }
        switch (typefaceIndex) {
            case SANS:
                tf = Typeface.SANS_SERIF;
                break;

            case SERIF:
                tf = Typeface.SERIF;
                break;

            case MONOSPACE:
                tf = Typeface.MONOSPACE;
                break;
        }

        setLabelTypeface(tf, styleIndex);
    }

    /**
     * Label helper function
     */
    public void setLabelTypeface(Typeface tf) {
        mLabel.setTypeface(tf);
    }

    public void setLabelTypeface(Typeface tf, int style) {
        mLabel.setTypeface(tf, style);
    }

    public void setLabelAppearance(Context c, int styleResourceId) {
        mLabel.setTextAppearance(c, styleResourceId);
    }

    public void setLabelPadding(int left, int top, int right, int bottom) {
        mLabel.setPadding(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLabelPaddingRelative(int start, int top, int end, int bottom) {
        mLabel.setPaddingRelative(start, top, end, bottom);
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
     * Method to simplify version checking.
     */
    private static boolean isJb1OrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    private static boolean isIcsOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private static boolean isHcOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

}
