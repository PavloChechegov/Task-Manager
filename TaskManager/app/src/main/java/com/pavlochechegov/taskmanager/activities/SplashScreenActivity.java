package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.pavlochechegov.taskmanager.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_splash_screen);
        StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        FrameLayout frameLayout= (FrameLayout) findViewById(R.id.frameSplashScreen);
        assert frameLayout != null;
        frameLayout.clearAnimation();
        frameLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewSplashScreen);
        assert imageViewLogo != null;
        imageViewLogo.clearAnimation();
        imageViewLogo.startAnimation(anim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_from_up, R.anim.slide_out_down);
            }
        }, 2*1000);

    }

}
