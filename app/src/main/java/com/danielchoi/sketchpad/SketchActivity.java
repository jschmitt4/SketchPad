package com.danielchoi.sketchpad;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ResourceCursorTreeAdapter;

import static android.R.attr.onClick;

public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{

    //Array of all the buttons
    public int display[] = {R.id.pencil_imageButton,  R.id.rect_imageButton, R.id.new_imageButton, R.id.open_imageButton, R.id.eraser_imageButton, R.id.save_imageButton, R.id.brush_imageButton, R.id.marker_imageButton, R.id.line_imageButton};
    private boolean menuOpen = false;
    // Used for the Drawing and color paint
    private DrawingView drawView;
    private ImageButton currPaint;
    private View lastView;
    private static int numOfOptions = 9;
    Vibrator vb;
    boolean erase = false;

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
            updateSelectView(view);

            if (view.getId() == R.id.pencil_imageButton) {
                drawView.changeStrokeWidth(1);
                drawView.setCurrentMode("DRAW");
                erase = false;
                lastView = view;
            } else if (view.getId() == R.id.marker_imageButton) {
                drawView.changeStrokeWidth(8);
                drawView.setCurrentMode("DRAW");
                erase = false;
                lastView = view;
            } else if (view.getId() == R.id.brush_imageButton) {
                drawView.changeStrokeWidth(20);
                drawView.setCurrentMode("DRAW");
                erase = false;
                lastView = view;

            } else if (view.getId() == R.id.line_imageButton) {
                drawView.setCurrentMode("LINE");
                erase = false;
                lastView = view;
            } else if (view.getId() == R.id.rect_imageButton) {
                drawView.setCurrentMode("RECT");
                erase = false;
                lastView = view;
            } else if (view.getId() == R.id.eraser_imageButton) {
                drawView.setCurrentMode("DRAW");
                erase = true;
                drawView.eraser(); //Selects eraser
            } else if (view.getId() == R.id.new_imageButton) {
                confirmPrompt();
                drawView.newSheet();
                erase = false;
                if(lastView!= null) updateSelectView(lastView);
                else updateSelectView(findViewById(R.id.pencil_imageButton));
            }

            showPallet();
        }else {
            if (menuOpen) menuOpen = false;
            else menuOpen = true;
            displayButtons();
        }

    }

    /**
     * This takes the view from on click to show which option is clicked.
     * Highlights the button with white
     * @param v
     */
    public void updateSelectView(View v){

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
     * Sets color of the brush
     * Changes color in UI to show that it has been selected.
     * @param view
     */
    public void paintClicked(View view){
        //user chosen color
        if(view!=currPaint && !erase){
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

    /**
     * Hides or shows the options by clicking the menu button
     */
    private void displayButtons(){

        setButtonSizeByScreen();
        if(menuOpen) {
            findViewById(R.id.optionsLayout).setVisibility(View.VISIBLE);
            showPallet();

        }else {
            findViewById(R.id.optionsLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.swatches).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * A helper method to hide the pallet if eraser is chosen.
     */
    private void showPallet(){
        if(erase)findViewById(R.id.swatches).setVisibility(View.INVISIBLE);
        else findViewById(R.id.swatches).setVisibility(View.VISIBLE);

    }
    private void setOnClicks(){
        findViewById(R.id.menuButton).setOnClickListener(this);
        for (int id : display) {
            findViewById(id).setOnClickListener(this);
        }

    }

    private void confirmPrompt(){}

    private void setButtonSizeByScreen(){

        LinearLayout options = (LinearLayout) findViewById(R.id.optionsLayout);
        int buttonSize = Math.round(ScreenHeight()/numOfOptions);

        //findViewById(R.id.pencil_imageButton).setLayoutParams();

    }

    private float ScreenHeight(){
        LinearLayout options = (LinearLayout) findViewById(R.id.optionsLayout);
        Resources r = options.getResources();
        DisplayMetrics d = r.getDisplayMetrics();

        return d.heightPixels;
    }

}
