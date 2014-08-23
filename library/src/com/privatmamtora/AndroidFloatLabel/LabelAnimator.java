package com.privatmamtora.AndroidFloatLabel;

import android.view.View;

/**
 * Created by Privat Krish Mamtora Atmaram
 */
public interface LabelAnimator {

	/**
	 * Called when the label should become visible
	 *
	 * @param label TextView to animate to visible
	 */
	public void onDisplayLabel(final View label, int duration);

	/**
	 * Called when the label should become invisible
	 *
	 * @param label TextView to animate to invisible
	 */
	public void onHideLabel(final View label, int duration);
}
