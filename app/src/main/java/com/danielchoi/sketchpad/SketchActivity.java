package com.danielchoi.sketchpad;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ResourceCursorTreeAdapter;
import static android.R.attr.antialias;
import android.widget.Toast;
import java.net.URI;
import java.util.UUID;
import static android.R.attr.onClick;

public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{

    //Array of all the buttons
    public int display[] = {
        R.id.pencil_imageButton,
        R.id.rect_imageButton,
        R.id.new_imageButton,
        R.id.open_imageButton,
        R.id.eraser_imageButton,
        R.id.save_imageButton,
        R.id.brush_imageButton,
        R.id.marker_imageButton,
        R.id.line_imageButton,
        R.id.aliasing_imageButton,
    };
    private boolean menuOpen = false;
    // Used for the Drawing and color paint
    private DrawingView drawView;
    private ImageButton currPaint;
    private View lastView;
    private static int numOfOptions = 9;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 999;
    Vibrator vb;
    boolean erase = false;
    boolean aliasing = true;
    ImageButton selectView;
    Animation selectAnimationShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        drawView = (DrawingView)findViewById(R.id.drawingView);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paintSwatchLayoutRow2); // Paint Swatch Layout
        currPaint = (ImageButton)paintLayout.getChildAt(5); //Black color
        currPaint.setImageResource(R.drawable.paint_pressed);
        selectAnimationShake = AnimationUtils.loadAnimation(this, R.anim.select);
        selectView = (ImageButton) findViewById(R.id.pencil_imageButton);
        selectView.startAnimation(selectAnimationShake);
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
                drawView.changeStrokeWidth(10);
                drawView.setCurrentMode("DRAW");
                erase = false;
                lastView = view;
            } else if (view.getId() == R.id.brush_imageButton) {
                drawView.changeStrokeWidth(25);
                drawView.setCurrentMode("DRAW");
                erase = false;
                lastView = view;
            } else if (view.getId() == R.id.line_imageButton) {
                drawView.setCurrentMode("LINE");
                erase = false;
                lastView = view;
                Toast.makeText(this, "Line Button", Toast.LENGTH_SHORT).show();
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
            } else if (view.getId() == R.id.aliasing_imageButton) {
                if(!aliasing){
                    aliasing = true;
                    Toast.makeText(this, "Aliasing True", Toast.LENGTH_SHORT).show();
                } else {
                    aliasing = false;
                    Toast.makeText(this, "Aliasing False", Toast.LENGTH_SHORT).show();
                }

            } else if(view.getId() == R.id.save_imageButton){
                savePrompt();
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
                selectView = (ImageButton) findViewById(id);
                selectView.startAnimation(selectAnimationShake);
            }else{
                selectView = (ImageButton) findViewById(id);
                selectView.clearAnimation();
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
    /**
     * This is the alert prompt when the user wants to save the file
     */
    private void savePrompt(){
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save Sketch");
        saveDialog.setMessage("Save sketch to your device?");
        saveDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                drawView.setDrawingCacheEnabled(true);
                checkSavePermission();

            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        saveDialog.show();
    }

    /**
     * Android 6.0 and up
     * They require a check of permission during run time for "Dangerous Permissions"
     * This calls the check for permission. If granted calls the saveImage()
     */
    public void checkSavePermission(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE);
        }else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            saveImage();
        }else{
            Toast.makeText(getApplicationContext(), "Sketch failed to save.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method saves the file.
     *
     */
    private void saveImage(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()); // DATE HERE
        ContentResolver cr = this.getContentResolver();


        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), drawView.getDrawingCache(),
                UUID.randomUUID().toString()+".png", "sketch");
        Log.i("ID?",imgSaved);

        if(imgSaved != null){
            Toast.makeText(getApplicationContext(), "Sketch Saved", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getApplicationContext(), "Sketch failed to save.", Toast.LENGTH_SHORT).show();
        }
        drawView.destroyDrawingCache();
    }

    /**
     * This is called on request to check for a permission.
     * If granted calls saveImage()
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {saveImage();}
                return;
            }
        }
    }

    /**
     * Gets the screen size from the canvas.
     * This dynamically changes the size of the buttons so that it is correct on all screens
     */
    private void setButtonSizeByScreen(){
        float height = View.MeasureSpec.getSize(drawView.getScreenHeight());
        int buttonSize = Math.round(height/(numOfOptions));
        for(int i: display) {
            findViewById(i).setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));
        }
    }

}
