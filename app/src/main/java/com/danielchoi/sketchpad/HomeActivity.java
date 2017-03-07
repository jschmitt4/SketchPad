package com.danielchoi.sketchpad;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Vibrator vb;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ImageButton startIb = (ImageButton) findViewById(R.id.startImageButton);
        startIb.setOnClickListener(this);

    }

    @Override
    public void onClick (View view){

        if (view.getId() == R.id.startImageButton) {
            vb.vibrate(10);
            Intent startIntent = new Intent(getApplicationContext(), SketchActivity.class);
            startActivity(startIntent);
        }
    }

}
