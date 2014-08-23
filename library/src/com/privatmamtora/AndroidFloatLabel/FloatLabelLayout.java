/*
 * Copyright (C) 2014 Privat Krish Mamtora Atmaram
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Layout which an {@link android.widget.EditText} to show a floating label when the hint is hidden
 * due to the user inputting text.
 * <p/>
 * <b>XML attributes</b>
 * <p/>
 * android.R.styleable#TextAppearance_textColor
 * android.R.styleable#TextAppearance_textSize
 * android.R.styleable#TextAppearance_typeface
 * android.R.styleable#TextAppearance_fontFamily
 * android.R.styleable#TextAppearance_textStyle
 * android.R.styleable#TextAppearance_textAllCaps
 * android.R.styleable#TextAppearance_shadowColor
 * android.R.styleable#TextAppearance_shadowDx
 * android.R.styleable#TextAppearance_shadowDy
 * android.R.styleable#TextAppearance_shadowRadius
 * <p/>
 * R.styleable#FloatLabelLayout_flTextColor
 * R.styleable#FloatLabelLayout_flTextSize
 * R.styleable#FloatLabelLayout_flTypeface
 * R.styleable#FloatLabelLayout_flTextStyle
 * R.styleable#FloatLabelLayout_flFontFamily
 * R.styleable#FloatLabelLayout_flTextAllCaps
 * R.styleable#FloatLabelLayout_flShadowColor
 * R.styleable#FloatLabelLayout_flShadowDx
 * R.styleable#FloatLabelLayout_flShadowDy
 * R.styleable#FloatLabelLayout_flShadowRadius
 * <p/>
 * TextView or View Attributes
 * R.styleable#FloatLabelLayout_flGravity
 * R.styleable#FloatLabelLayout_flText
 * R.styleable#FloatLabelLayout_flScrollHorizontally
 * R.styleable#FloatLabelLayout_flEllipsize
 * R.styleable#FloatLabelLayout_flIncludeFontPadding
 * R.styleable#FloatLabelLayout_flTextScaleX
 * R.styleable#FloatLabelLayout_flPadding
 * R.styleable#FloatLabelLayout_flPaddingLeft
 * R.styleable#FloatLabelLayout_flPaddingRight
 * R.styleable#FloatLabelLayout_flPaddingTop
 * R.styleable#FloatLabelLayout_flPaddingBottom
 * R.styleable#FloatLabelLayout_flGapSize
 * R.styleable#FloatLabelLayout_flAnimationDuration
 */
public class FloatLabelLayout extends FrameLayout {

	private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
	private static final String SAVED_LABEL_VISIBILITY = "SAVED_LABEL_VISIBILITY";
	private static final String SAVED_HINT = "SAVED_HINT";

	// Enum for the "typeface" XML parameter.
	private static final int SANS = 1;
	private static final int SERIF = 2;
	private static final int MONOSPACE = 3;

	private static final int DEFAULT_TEXT_SIZE = 14;

	private EditText mEditText;
	private TextView mLabel;

	// Text Appearance
	//
	private ColorStateList mLabelTextColor = null;
	private int mLabelTextSize = DEFAULT_TEXT_SIZE;
	private int mLabelTypeface = -1;
	private String mLabelFontFamily = null;
	private int mLabelTextStyle = -1;
	private boolean mLabelAllCaps = false;
	private int mLabelShadowColor = 0;
	private float mLabelShadowDx = 0;
	private float mLabelShadowDy = 0;
	private float mLabelShadowRadius = 0;

	private int mLabelPadding = 0;
	private int mLabelPaddingLeft = 0;
	private int mLabelPaddingRight = 0;
	private int mLabelPaddingTop = 0;
	private int mLabelPaddingBottom = 0;

	private int mLabelGravity = 0x03;
	private CharSequence mLabelText = "";
	private int mLabelEllipsize = -1;

	private int mLabelGap = 0;
	private int mLabelAnimationDuration = -1;

	private CharSequence mHint;

	private Context mContext;

	public FloatLabelLayout(Context context) {
		super(context);
		mContext = context;
	}

	public FloatLabelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setupLabel(attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FloatLabelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setupLabel(attrs);
	}

	private void setupLabel(AttributeSet attrs) {
		mLabel = new TextView(mContext);

		setupAttributes(attrs);

		//Default Label is Single Line
		setLabelSingleLine(true);

		setLabelGravity(mLabelGravity);

		if (mLabelEllipsize < 0) {
			mLabelEllipsize = 3; // END
		}

		if (mLabelEllipsize == 1) {
			setLabelEllipsize(TextUtils.TruncateAt.START);
		} else if (mLabelEllipsize == 2) {
			setLabelEllipsize(TextUtils.TruncateAt.MIDDLE);
		} else if (mLabelEllipsize == 3) {
			setLabelEllipsize(TextUtils.TruncateAt.END);
		}

		setLabelColor(mLabelTextColor != null ? mLabelTextColor : getResources().getColorStateList(R.color.floatlabel_default));
		setLabelSize(mLabelTextSize);
		setLabelAllCaps(mLabelAllCaps);

		setTypefaceFromAttrs(mLabelFontFamily, mLabelTypeface, mLabelTextStyle);

		if (mLabelShadowColor != 0) {
			setLabelShadowLayer(mLabelShadowRadius, mLabelShadowDx, mLabelShadowDy, mLabelShadowColor);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			setLabelPaddingRelative(mLabelPaddingLeft, mLabelPaddingTop, mLabelPaddingRight, mLabelPaddingBottom);
		} else {
			setLabelPadding(mLabelPaddingLeft, mLabelPaddingTop, mLabelPaddingRight, mLabelPaddingBottom);
		}

		setLabelVisibility(INVISIBLE);
		addView(mLabel, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	private void setupAttributes(AttributeSet attrs) {

		final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.FloatLabelLayout);

		int styleResId = a.getResourceId(R.styleable.FloatLabelLayout_flTextAppearance, -1);
		setupTextAppearanceAttr(styleResId);

		setupCustomAttr(a);

		a.recycle();
	}

	private void setupTextAppearanceAttr(int styleResourceId) {
		if (styleResourceId != -1) {
			final TypedArray appearance = mContext.obtainStyledAttributes(styleResourceId, R.styleable.TextAppearance);

			mLabelTextColor = appearance.getColorStateList(R.styleable.TextAppearance_android_textColor);
			mLabelTextSize = appearance.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, mLabelTextSize);
			mLabelTypeface = appearance.getInt(R.styleable.TextAppearance_android_typeface, mLabelTypeface);
			mLabelFontFamily = appearance.getString(R.styleable.TextAppearance_android_fontFamily);
			mLabelTextStyle = appearance.getInt(R.styleable.TextAppearance_android_textStyle, mLabelTextStyle);
			mLabelAllCaps = appearance.getBoolean(R.styleable.TextAppearance_android_textAllCaps, mLabelAllCaps);
			mLabelShadowColor = appearance.getInt(R.styleable.TextAppearance_android_shadowColor, mLabelShadowColor);
			mLabelShadowDx = appearance.getFloat(R.styleable.TextAppearance_android_shadowDx, mLabelShadowDx);
			mLabelShadowDy = appearance.getFloat(R.styleable.TextAppearance_android_shadowDy, mLabelShadowDy);
			mLabelShadowRadius = appearance.getFloat(R.styleable.TextAppearance_android_shadowRadius, mLabelShadowRadius);

			appearance.recycle();
		}
	}

	private void setupCustomAttr(TypedArray a) {
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);

			//TextAppearance Attributes
			if (attr == R.styleable.FloatLabelLayout_flTextColor) {
				mLabelTextColor = a.getColorStateList(attr);

			} else if (attr == R.styleable.FloatLabelLayout_flTextSize) {
				mLabelTextSize = a.getDimensionPixelSize(attr, mLabelTextSize);

			} else if (attr == R.styleable.FloatLabelLayout_flTypeface) {
				mLabelTypeface = a.getInt(attr, mLabelTypeface);

			} else if (attr == R.styleable.FloatLabelLayout_flTextStyle) {
				mLabelTextStyle = a.getInt(attr, mLabelTextStyle);

			} else if (attr == R.styleable.FloatLabelLayout_flFontFamily) {
				mLabelFontFamily = a.getString(attr);

			} else if (attr == R.styleable.FloatLabelLayout_flTextAllCaps) {
				mLabelAllCaps = a.getBoolean(attr, mLabelAllCaps);

			} else if (attr == R.styleable.FloatLabelLayout_flShadowColor) {
				mLabelShadowColor = a.getInt(attr, mLabelShadowColor);

			} else if (attr == R.styleable.FloatLabelLayout_flShadowDx) {
				mLabelShadowDx = a.getFloat(attr, mLabelShadowDx);

			} else if (attr == R.styleable.FloatLabelLayout_flShadowDy) {
				mLabelShadowDy = a.getFloat(attr, mLabelShadowDy);

			} else if (attr == R.styleable.FloatLabelLayout_flShadowRadius) {
				mLabelShadowRadius = a.getFloat(attr, mLabelShadowRadius);

				//TextView Attributes
			} else if (attr == R.styleable.FloatLabelLayout_flGravity) {
				mLabelGravity = a.getInt(attr, 0x03);

			} else if (attr == R.styleable.FloatLabelLayout_flText) {
				mLabelText = a.getText(attr);

			} else if (attr == R.styleable.FloatLabelLayout_flScrollHorizontally) {
				if (a.getBoolean(attr, false)) {
					setLabelHorizontallyScrolling(true);
				}

			} else if (attr == R.styleable.FloatLabelLayout_flEllipsize) {
				mLabelEllipsize = a.getInt(attr, -1);

			} else if (attr == R.styleable.FloatLabelLayout_flIncludeFontPadding) {
				if (!a.getBoolean(attr, true)) {
					setLabelIncludeFontPadding(false);
				}

			} else if (attr == R.styleable.FloatLabelLayout_flTextScaleX) {
				setLabelTextScaleX(a.getFloat(attr, 1.0f));

				//View Attributes
			} else if (attr == R.styleable.FloatLabelLayout_flPadding) {
				mLabelPadding = a.getDimensionPixelSize(attr, 0);
			} else if (attr == R.styleable.FloatLabelLayout_flPaddingLeft) {
				mLabelPaddingLeft = a.getDimensionPixelSize(attr, mLabelPadding);
			} else if (attr == R.styleable.FloatLabelLayout_flPaddingRight) {
				mLabelPaddingRight = a.getDimensionPixelSize(attr, mLabelPadding);
			} else if (attr == R.styleable.FloatLabelLayout_flPaddingTop) {
				mLabelPaddingTop = a.getDimensionPixelSize(attr, mLabelPadding);
			} else if (attr == R.styleable.FloatLabelLayout_flPaddingBottom) {
				mLabelPaddingBottom = a.getDimensionPixelSize(attr, mLabelPadding);
			} else if (attr == R.styleable.FloatLabelLayout_flGapSize) {
				mLabelGap = a.getDimensionPixelSize(attr, 0);
			} else if (attr == R.styleable.FloatLabelLayout_flAnimationDuration) {
				mLabelAnimationDuration = a.getInt(attr, -1);
			}
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

		mHint = mLabelText.length() != 0 ? mLabelText : mEditText.getHint();
		setLabelText(mHint);

		// Add a TextWatcher so that we know when the text input has changed
		//mEditText.addTextChangedListener(mTextWatcher);
		mEditText.addTextChangedListener(new EditTextWatcher());

		// Add focus listener to the EditText so that we can notify the label that it is activated.
		// Allows the use of a ColorStateList for the text color on the label
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				mLabel.setActivated(focused);
			}
		});
	}

	/**
	 * @return the {@link android.widget.EditText} text input
	 */
	public final EditText getEditText() {
		return mEditText;
	}

	/**
	 * @return the {@link android.widget.TextView} label
	 */
	public final TextView getLabel() {
		return mLabel;
	}

	/**
	 * Helper functions
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

	public void setLabelAnimator(LabelAnimator labelAnimator) {
		if (labelAnimator == null) {
			mLabelAnimator = new DefaultLabelAnimator();
		} else {
			mLabelAnimator = labelAnimator;
		}
	}

	public void setLabelHorizontallyScrolling(boolean whether) {
		mLabel.setHorizontallyScrolling(whether);
	}

	public void setLabelTextScaleX(float size) {
		mLabel.setTextScaleX(size);
	}

	public void setLabelIncludeFontPadding(boolean includepad) {
		mLabel.setIncludeFontPadding(includepad);
	}

	public void setLabelPadding(int left, int top, int right, int bottom) {
		mLabel.setPadding(left, top, right, bottom);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void setLabelPaddingRelative(int start, int top, int end, int bottom) {
		mLabel.setPaddingRelative(start, top, end, bottom);
	}

	public void setLabelEllipsize(TextUtils.TruncateAt where) {
		mLabel.setEllipsize(where);
	}

	public void setLabelColor(int color) {
		mLabel.setTextColor(color);
	}

	public void setLabelColor(ColorStateList colors) {
		mLabel.setTextColor(colors);
	}

	public void setLabelShadowLayer(float radius, float dx, float dy, int color) {
		mLabel.setShadowLayer(radius, dx, dy, color);
	}

	/**
	 * Used to set label's text size
	 * setTextSize(int unit, float size) is not provided
	 * Only sp(dimesion) allowed
	 *
	 * @param size
	 */
	public void setLabelSize(float size) {
		mLabel.setTextSize(size);
	}

	public void setLabelGravity(int gravity) {
		mLabel.setGravity(gravity);
	}

	public void setLabelSingleLine(boolean singleLine) {
		mLabel.setSingleLine(singleLine);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void setLabelAllCaps(boolean allCaps) {
		mLabel.setAllCaps(allCaps);
	}

	public void setLabelVisibility(int visibility) {
		mLabel.setVisibility(visibility);
	}

	public void setLabelText(CharSequence text) {
		mLabel.setText(text);
	}

	/**
	 * Animation Instance
	 */
	private LabelAnimator mLabelAnimator = new DefaultLabelAnimator();

	private class EditTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				mLabelAnimator.onHideLabel(mLabel, mLabelAnimationDuration);
			} else {
				mLabelAnimator.onDisplayLabel(mLabel, mLabelAnimationDuration);
			}
		}
	}
}
