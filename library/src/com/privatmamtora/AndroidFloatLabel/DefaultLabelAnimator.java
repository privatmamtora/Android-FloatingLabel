package com.privatmamtora.AndroidFloatLabel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by Krish on 8/21/2014.
 */
public class DefaultLabelAnimator implements LabelAnimator {
    @Override
    public void onDisplayLabel(final View label, int duration) {
        showLabel(label, duration, true);
    }

    @Override
    public void onHideLabel(final View label, int duration) {
        showLabel(label, duration, false);
    }

    private void showLabel(final View label, int duration, final boolean show) {
        AnimatorSet animation = null;
        String tranY = "translationY";
        String a = "alpha";

        int vis = label.getVisibility();
        if (vis == View.VISIBLE && !show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(label, tranY, 0, label.getHeight() / 8);
            ObjectAnimator fade = ObjectAnimator.ofFloat(label, a, 1, 0);
            animation.playTogether(move, fade);

        }
        else if (vis == View.INVISIBLE && show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(label, tranY, label.getHeight() / 8, 0);
            ObjectAnimator fade = ObjectAnimator.ofFloat(label, a, 0, 1);
            animation.playTogether(move, fade);
        }

        if (animation != null) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //super.onAnimationStart(animation);
                    label.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //super.onAnimationEnd(animation);
                    label.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });

            if(duration >= 0) {
                animation.setDuration(duration);
            }
            animation.start();
        }
    }
}
