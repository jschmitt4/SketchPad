package com.danielchoi.sketchpad;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import static android.R.attr.onClick;

public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{

    public int display[] = {R.id.pencil_imageButton, R.id.rect_imageButton, R.id.new_imageButton, R.id.open_imageButton, R.id.eraser_imageButton, R.id.save_imageButton };
    private boolean menuOpen = false;
    // Used for the Drawing and color paint
    private DrawingView drawView;
    private ImageButton currPaint;
    Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paintSwatchLayoutRow2); // to select color
        currPaint = (ImageButton)paintLayout.getChildAt(5);
        currPaint.setImageResource(R.drawable.paint_pressed);

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

    // XML onClick event
    public void paintClicked(View view){
        //user chosen color
        if(view!=currPaint){
            // Update color to user selection
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);

            // Update UI to reflect chosen paint and set previous back to normal
            imgView.setImageResource(R.drawable.paint_pressed);
            currPaint.setImageResource(R.drawable.paint);
            currPaint=(ImageButton)view;
        }
    }

    private void displayButtons(){

        if(menuOpen) {
            findViewById(R.id.optionsLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.swatches).setVisibility(View.VISIBLE);

        }else {
            findViewById(R.id.optionsLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.swatches).setVisibility(View.INVISIBLE);
        }

    }
    private void setOnClicks(){
        findViewById(R.id.menuButton).setOnClickListener(this);
        for (int id : display) {
            findViewById(id).setOnClickListener(this);
        }

    }

}
