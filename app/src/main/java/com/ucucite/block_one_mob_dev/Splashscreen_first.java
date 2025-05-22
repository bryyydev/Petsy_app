package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Splashscreen_first extends AppCompatActivity {

    private ImageView gradientCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen_first);

        gradientCircle = findViewById(R.id.gradientCircle);

        ScaleAnimation scaleAnim = new ScaleAnimation(
                1f, 9.5f, // From X to X
                1f, 9.5f, // From Y to Y
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X
                Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y
        );
        scaleAnim.setDuration(1000); // 2 seconds
        scaleAnim.setFillAfter(true);

        gradientCircle.startAnimation(scaleAnim);

        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Go to Splashscreen_s
                Intent intent = new Intent(Splashscreen_first.this, Splashscreen_second.class);
                startActivity(intent);

                // Apply fade-in and fade-out transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
