package com.privatmamtora.example.AndroidFloatLabel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.privatmamtora.AndroidFloatLabel.FloatLabelLayout;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatLabelLayout f = (FloatLabelLayout) findViewById(R.id.f4);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "RobotoCondensed-LightItalic.ttf");
        f.setLabelTypeface(typeface);
        TextView tv = f.getLabel();
        f.setLabelAnimator(new NewLabelAnimation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class NewLabelAnimation implements FloatLabelLayout.LabelAnimator {

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

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(label, "rotationX", 180);
                animation.playTogether(move, fade, rotationAnim);

            }
            else if (vis == View.INVISIBLE && show) {
                animation = new AnimatorSet();
                ObjectAnimator move = ObjectAnimator.ofFloat(label, tranY, label.getHeight() / 8, 0);
                ObjectAnimator fade = ObjectAnimator.ofFloat(label, a, 0, 1);
                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(label, "rotationX", -180);
                animation.playTogether(move, fade, rotationAnim);
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
}
