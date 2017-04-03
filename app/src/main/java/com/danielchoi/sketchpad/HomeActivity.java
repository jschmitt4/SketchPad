package com.danielchoi.sketchpad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Vibrator vb;
    boolean spb;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        findViewById(R.id.startImageButton).setOnClickListener(this);
        findViewById(R.id.aboutButton).setOnClickListener(this);
        findViewById(R.id.tutorialButton).setOnClickListener(this);
        ImageButton startIb = (ImageButton) findViewById(R.id.startImageButton);
        Animation animationShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        startIb.startAnimation(animationShake);
    }

    @Override
    protected void onResume() {
        Log.i("ON", " RESUME");
        SharedPreferences sp = getSharedPreferences("NEW",Context.MODE_PRIVATE);
        spb = sp.getBoolean("NEW", true);

        if(spb){
            Log.i("NEW", " TRUE");
            findViewById(R.id.tutorialButton).setVisibility(View.INVISIBLE);
        }else {
            Log.i("NEW", " FALSE");
            findViewById(R.id.tutorialButton).setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @Override
    public void onClick (View view){
        vb.vibrate(10);
        if (view.getId() == R.id.startImageButton) {
            if(spb) {
                Intent startIntent = new Intent(getApplicationContext(), Tutorial.class);
                startActivity(startIntent);
            }else{
                Intent startIntent = new Intent(getApplicationContext(), SketchActivity.class);
                startActivity(startIntent);
            }

        }else if(view.getId() == R.id.aboutButton){
            Intent startIntent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(startIntent);
        } else if(view.getId() == R.id.tutorialButton){
            Intent startIntent = new Intent(getApplicationContext(), Tutorial.class);
            startActivity(startIntent);
        }
    }

}
