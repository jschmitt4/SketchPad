package com.danielchoi.sketchpad;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import static android.R.attr.onClick;

public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{

    public int display[] = {R.id.pencil_imageButton,  R.id.rect_imageButton, R.id.new_imageButton, R.id.open_imageButton, R.id.eraser_imageButton, R.id.save_imageButton, R.id.brush_imageButton, R.id.marker_imageButton, R.id.line_imageButton};
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

        drawView = (DrawingView)findViewById(R.id.drawingView);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paintSwatchLayoutRow2); // Paint Swatch Layout
        currPaint = (ImageButton)paintLayout.getChildAt(5); //Black color
        currPaint.setImageResource(R.drawable.paint_pressed);

        displayButtons();
        setOnClicks();
    }

    @Override
    public void onClick(View view) {
        vb.vibrate(10);

        if(view.getId() != R.id.menuButton){
            updateStrokeSelectView(view);
        }else{
            if(menuOpen)menuOpen = false;
            else menuOpen = true;
            displayButtons();
        }

        if(view.getId() == R.id.pencil_imageButton){
            drawView.changeStrokeWidth(1);
        }else if(view.getId() == R.id.marker_imageButton){
            drawView.changeStrokeWidth(8);
        }else if(view.getId() == R.id.brush_imageButton){
            drawView.changeStrokeWidth(20);
        }else if(view.getId() == R.id.line_imageButton){


        }else if(view.getId() == R.id.rect_imageButton){


        }else if(view.getId() == R.id.eraser_imageButton){
            drawView.eraser();
        }



    }

    /**
     * This takes the view from on click to show which option is clicked.
     * @param v
     */
    public void updateStrokeSelectView(View v){

        for(int id : display){
            if(v.getId() == id){
                v.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }else{
                findViewById(id).setBackgroundColor(Color.parseColor("#009999"));
            }

        }


    }

    /**
     * Each swatch imageview has a Tag with a string value of the color
     * Imagebutton onClick calls paintClicked
     * The view is the view of the image clicked
     * @param view
     */
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
            currPaint = (ImageButton)view;
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
