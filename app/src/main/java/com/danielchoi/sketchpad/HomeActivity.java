package com.danielchoi.sketchpad;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Vibrator vb;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        findViewById(R.id.startImageButton).setOnClickListener(this);
        findViewById(R.id.aboutButton).setOnClickListener(this);

        ImageButton startIb = (ImageButton) findViewById(R.id.startImageButton);
        Animation animationShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        startIb.startAnimation(animationShake);


    }

    @Override
    public void onClick (View view){
        vb.vibrate(10);
        if (view.getId() == R.id.startImageButton) {
            Intent startIntent = new Intent(getApplicationContext(), SketchActivity.class);
            startActivity(startIntent);

        }else if(view.getId() == R.id.aboutButton){
            Intent startIntent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(startIntent);
        }
    }

}
