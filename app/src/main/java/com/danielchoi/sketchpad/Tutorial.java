package com.danielchoi.sketchpad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class Tutorial extends AppCompatActivity {

    private int slides[] = {
            R.drawable.menutut,     R.drawable.pallete_tut,
            R.drawable.clear_tut,   R.drawable.save_tut,
            R.drawable.util_tut,    R.drawable.shapes_tut,
            R.drawable.eraser_tut,  R.drawable.tip_tut,
            R.drawable.alias_tut,   R.drawable.endtut};

    private int slideCount = 0;
    RelativeLayout rl;
    ImageButton ib;
    Vibrator vb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ib = (ImageButton)findViewById(R.id.click_imageButton);
        Animation an = AnimationUtils.loadAnimation(this, R.anim.blink);
        ib.startAnimation(an);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences sp = getSharedPreferences("NEW",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("NEW", false);
        editor.apply();

        rl = (RelativeLayout) (findViewById(R.id.activity_tutorial));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vb.vibrate(10);
                if (slideCount == 0)
                    ib.clearAnimation();
                    ib.setVisibility(View.GONE);

                if (slideCount < 10) {
                    rl.setBackgroundResource(slides[slideCount]);
                    slideCount++;
                }else if(slideCount == 10){
                    Intent startIntent = new Intent(getApplicationContext(), SketchActivity.class);
                    startActivity(startIntent);
                }
            }
        });

    }

}
