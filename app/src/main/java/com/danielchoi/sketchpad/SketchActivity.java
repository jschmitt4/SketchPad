package com.danielchoi.sketchpad;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.UUID;

public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{

    //Array of all the buttons
    public int display[] = {R.id.util_Option1, R.id.util_Option2, R.id.shape_Option1, R.id.shape_Option2,R.id.size_Option2, R.id.shape_Option1, R.id.new_imageButton, R.id.eraser_imageButton, R.id.save_imageButton};
    private boolean menuOpen = false;
    // Used for the Drawing and color paint
    private DrawingView drawView;
    private ImageButton currPaint;
    private View lastView;
    private int buttonSize;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 999;
    Vibrator vb;
    private String lastColor = "#FF000000";
    boolean erase = false;
    boolean longClick = false;
    boolean utilSwitched = false;
    boolean shapeSwitched = false;
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
        selectView = (ImageButton) findViewById(R.id.util_Option1);
        selectView.startAnimation(selectAnimationShake);
        hideAllExpansion();
        displayButtons();
        setOnClicks();
        checkLocation();

    }

    @Override
    public void onClick(View view) {
        vb.vibrate(10);
        erase = false;

        if(view.getId() != R.id.menuButton){
            updateSelectView(view);

            if (view.getId() == R.id.util_Option1) {
                if(!longClick){
                    if(!utilSwitched)setPencil();
                    else setMarker();

                    lastView = view;
                    hideAllExpansion();
                }longClick = false;


            } else if (view.getId() == R.id.util_Option2) {
                if(utilSwitched)setPencil();
                else setMarker();

                lastView = findViewById(R.id.util_Option1);
                swap(view.getId());
                hideAllExpansion();

            }  else if (view.getId() == R.id.shape_Option1) {
                if(!longClick) {
                    if(!shapeSwitched)setLine();
                    else setRect();

                    lastView = view;
                    hideAllExpansion();
                }longClick = false;

            } else if (view.getId() == R.id.shape_Option2) {
                if(shapeSwitched)setLine();
                else setRect();
                lastView = findViewById(R.id.shape_Option1);
                swap(view.getId());
                hideAllExpansion();
            } else if (view.getId() == R.id.eraser_imageButton) {
                drawView.setCurrentMode("DRAW");
                erase = true;
                drawView.eraser(); //Selects eraser
                hideAllExpansion();

            } else if (view.getId() == R.id.new_imageButton) {
                hideAllExpansion();
                confirmClear();
                if(lastView!= null) updateSelectView(lastView);
                else updateSelectView(findViewById(R.id.util_Option1));

            } else if(view.getId() == R.id.save_imageButton){
                hideAllExpansion();
                savePrompt();
                if(lastView!= null) updateSelectView(lastView);
                else updateSelectView(findViewById(R.id.util_Option1));
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
            }else {
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
            lastColor = view.getTag().toString();
            drawView.setColor(lastColor);

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
            findViewById(R.id.backdrop).setVisibility(View.VISIBLE);
            showPallet();

        }else {
            findViewById(R.id.optionsLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.swatches).setVisibility(View.INVISIBLE);
            findViewById(R.id.backdrop).setVisibility(View.INVISIBLE);
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
        findViewById(R.id.util_Option1).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClick = true;
                expand(view);
                return false;
            }
        });

        findViewById(R.id.shape_Option1).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClick = true;
                expand(view);
                return false;
            }
        });

    }

    private void confirmClear(){

        AlertDialog.Builder clearDialog = new AlertDialog.Builder(this);
        clearDialog.setTitle("Clear Canvas");
        clearDialog.setMessage("Are you sure you wanna clear your canvas?");
        clearDialog.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                drawView.newSheet();

            }
        });
        clearDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        clearDialog.show();
    }
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
     * This is called on request to check for a permission.
     * If granted calls saveImage()
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {saveImage();}
                return;
            }
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
     * Gets the screen size from the canvas.
     * This dynamically changes the size of the buttons so that it is correct on all screens
     */
    private void setButtonSizeByScreen(){
        float height = View.MeasureSpec.getSize(drawView.getScreenHeight());
        buttonSize = Math.round(height/(10));
        for(int i: display) {
            findViewById(i).setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));
        }

        /**
         * This sets the backdrop to be aligned with the drawingview and list options.
         * This is done so even if I also the above layout's background it does change it.
         */
        TextView tv = (TextView) findViewById(R.id.backdrop);
        tv.setLayoutParams(new RelativeLayout.LayoutParams(buttonSize, (buttonSize * 6)));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_END, R.id.drawingView);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.drawingView);

    }

    private void setPencil(){
        drawView.setColor(lastColor);
        drawView.changeStrokeWidth(2);
        drawView.setCurrentMode("PENCIL");

    }

    private void setMarker(){
        drawView.setColor(lastColor);
        drawView.changeStrokeWidth(10);
        drawView.setCurrentMode("MARKER");
        findViewById(R.id.utility_LL).setBackgroundColor(Color.parseColor("#009999"));
    }

    private void setLine(){
        drawView.setColor(lastColor);
        drawView.setCurrentMode("LINE");
    }

    private void setRect(){
        drawView.setColor(lastColor);
        drawView.setCurrentMode("RECT");

    }

    /**
     * This expands the view on long Click
     * @param v
     */
    private void expand(View v){
        if(v.getId() == R.id.util_Option1){
            findViewById(R.id.util_Option2).setVisibility(View.VISIBLE);
            findViewById(R.id.utility_LL).setBackgroundColor(Color.parseColor("#009999"));
            findViewById(R.id.shapes_LL).setBackgroundColor(Color.parseColor("#00000000"));

        }else if(v.getId() == R.id.shape_Option1){
            findViewById(R.id.shape_Option2).setVisibility(View.VISIBLE);
            findViewById(R.id.shapes_LL).setBackgroundColor(Color.parseColor("#009999"));
            findViewById(R.id.utility_LL).setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    /**
     * This method, swaps the images for the slide out buttons
     * @param id
     */
    private void swap(int id){ //2

        if(id == R.id.util_Option2) {
            if(!utilSwitched){
                ImageButton ib = (ImageButton) findViewById(id);
                ib.setImageResource(R.drawable.pencil);
                ImageButton ib2 = (ImageButton) findViewById(R.id.util_Option1);
                ib2.setImageResource(R.drawable.marker);
                updateSelectView(ib2);
                utilSwitched = true;
            }else{
                ImageButton ib = (ImageButton) findViewById(id);
                ib.setImageResource(R.drawable.marker);
                ImageButton ib2 = (ImageButton) findViewById(R.id.util_Option1);
                ib2.setImageResource(R.drawable.pencil);

                utilSwitched = false;
                updateSelectView(ib2);
            }

        }else if(id == R.id.shape_Option2){
            if(!shapeSwitched){
                ImageButton ib = (ImageButton) findViewById(id);
                ib.setImageResource(R.drawable.line);
                ImageButton ib2 = (ImageButton) findViewById(R.id.shape_Option1);
                ib2.setImageResource(R.drawable.rectangle);
                updateSelectView(ib2);
                shapeSwitched = true;
            }else{
                ImageButton ib = (ImageButton) findViewById(id);
                ib.setImageResource(R.drawable.rectangle);
                ImageButton ib2 = (ImageButton) findViewById(R.id.shape_Option1);
                ib2.setImageResource(R.drawable.line);

                shapeSwitched = false;
                updateSelectView(ib2);
            }
        }
    }

    /**
     * This hides all the "exapansion" from the long presses
     */
    private void hideAllExpansion(){
        findViewById(R.id.util_Option2).setVisibility(View.INVISIBLE);
        findViewById(R.id.shape_Option2).setVisibility(View.INVISIBLE);

        findViewById(R.id.utility_LL).setBackgroundColor(Color.parseColor("#00000000"));
        findViewById(R.id.shapes_LL).setBackgroundColor(Color.parseColor("#00000000"));
    }

    private void checkLocation(){}

}
