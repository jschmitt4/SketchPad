package com.danielchoi.sketchpad;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import static android.R.attr.onClick;

public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{

    public int display[] = {R.id.pencil_imageButton, R.id.rect_imageButton, R.id.red_imageButton, R.id.blue_imageButton, R.id.yellow_imageButton, R.id.white_imageButton, R.id.black_imageButton, };
    private boolean menuOpen = false;
    Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        displayButtons();
        setOnClicks();
    }

    @Override
    public void onClick(View view) {
        vb.vibrate(10);

        if(view.getId() == R.id.menuButton){

            if(menuOpen){
                menuOpen = false;
            }else menuOpen = true;

            displayButtons();

        }

    }

    private void displayButtons(){

        if(menuOpen) {
            for (int id : display) {
                findViewById(id).setVisibility(View.VISIBLE);
            }
        }else {
            for(int id : display){
                findViewById(id).setVisibility(View.INVISIBLE);
            }
        }

    }
    private void setOnClicks(){
        findViewById(R.id.menuButton).setOnClickListener(this);
        for (int id : display) {
            findViewById(id).setOnClickListener(this);
        }

    }

}
