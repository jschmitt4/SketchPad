package com.danielchoi.sketchpad;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.UUID;


public class SketchActivity extends AppCompatActivity
implements View.OnClickListener{
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 999;
    Vibrator vb;
    ImageButton selectView;
    Animation selectAnimationShake;
    Resources r;
    public int display[], expandableOptions[];
    private DrawingView drawView;
    private ImageButton currPaint;
    private View lastView;
    private String lastColor;
    private int clearColor, menuColor, strokeSize, buttonSize;
    private boolean erase, menuOpen,longClick, utilSwitched, shapeSwitched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
        intializeAndSetup();
        hideAllExpansion();
        displayButtons();
        setOnClicks();
    }

    @Override
    public void onClick(View view) {
        vb.vibrate(10);

        if(view.getId() != R.id.menuButton){
            erase = false;
            updateSelectView(view);

            if (view.getId() == R.id.util_Option1) {
                if(!longClick){
                    if(!utilSwitched)setPencil();
                    else setMarker();
                    hideAllExpansion();
                    lastView = view;
                }longClick = false;

            } else if (view.getId() == R.id.util_Option2) {
                if(utilSwitched)setPencil();
                else setMarker();
                hideAllExpansion();
                lastView = findViewById(R.id.util_Option1);
                swap(view.getId());

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

            }else if (view.getId() == R.id.size_Option1) {
                if(!longClick) {
                    setStrokeView();
                    hideAllExpansion();
                    setDefaultSelect();
                }longClick = false;

            }else if (view.getId() == R.id.eraser_imageButton) {
                erase = true;
                lastView = view;
                drawView.setCurrentMode("ERASER"); //Selects eraser
                hideAllExpansion();

            } else if (view.getId() == R.id.new_imageButton) {
                hideAllExpansion();
                confirmClear();
                setDefaultSelect();

            } else if (view.getId() == R.id.aliasing_imageButton) {
                ImageButton ib = (ImageButton) findViewById(R.id.aliasing_imageButton);
                if(!drawView.getAlias()){
                    drawView.setAlias(true);
                    ib.setImageResource(R.drawable.aa);
                } else {
                    drawView.setAlias(false);
                    ib.setImageResource(R.drawable.noaa);
                }
                hideAllExpansion();
                setDefaultSelect();
            } else if(view.getId() == R.id.save_imageButton){
                hideAllExpansion();
                savePrompt();
                setDefaultSelect();
            }
            showPallet();

        }else {
            if (menuOpen) menuOpen = false;
            else menuOpen = true;
            displayButtons();
        }
    }

    /**
     * Sets last view if the first select is not a utility/shape/eraser
     */
    private void setDefaultSelect(){
        if(lastView!= null) updateSelectView(lastView);
        else updateSelectView(findViewById(R.id.util_Option1));
    }

    /**
     * Sets the onClick Listeners and on LongClick Listeners
     */
    private void setOnClicks(){
        findViewById(R.id.menuButton).setOnClickListener(this);

        for(int id : display) findViewById(id).setOnClickListener(this);

        for(int idExpand: expandableOptions) {
            findViewById(idExpand).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClick = true;
                    expand(view);
                    return false;
                }
            });
        }
        findViewById(R.id.size_Option1).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClick = true;
                if(strokeSize>=0 && strokeSize < 10)strokeSize = 10;
                else if (strokeSize >=10 && strokeSize < 20)strokeSize = 20;
                else if (strokeSize >=20 && strokeSize < 30)strokeSize = 30;
                else if (strokeSize >=30 && strokeSize < 40)strokeSize = 40;
                else strokeSize = 0;

                setStrokeView();
                return false;
            }
        });
    }

    private void setPencil(){
        drawView.setColor(lastColor);
        drawView.setCurrentMode("PENCIL");
    }

    private void setMarker(){
        drawView.setColor(lastColor);
        drawView.setCurrentMode("MARKER");
        findViewById(R.id.utility_LL).setBackgroundColor(menuColor);
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
     * Alertbox to confirm if the user wants to clear the screen
     * If so clear
     */
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE);

        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) saveImage();

        else Toast.makeText(getApplicationContext(), "Sketch failed to save.", Toast.LENGTH_SHORT).show();
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) saveImage();
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

        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), drawView.getDrawingCache(),
                UUID.randomUUID().toString()+".png", "sketch");

        if(imgSaved != null)Toast.makeText(getApplicationContext(), "Sketch Saved", Toast.LENGTH_SHORT).show();

        else Toast.makeText(getApplicationContext(), "Sketch failed to save.", Toast.LENGTH_SHORT).show();

        drawView.destroyDrawingCache();
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

    /**
     * Gets the screen size from the canvas.
     * This dynamically changes the size of the buttons so that it is correct on all screens
     */
    private void setButtonSizeByScreen(){
        int height = View.MeasureSpec.getSize(drawView.getScreenHeight());
        Log.i("HEIGHT",""+height);
        buttonSize = Math.round(height/(10));//In px
        r = getResources();

        for(int i: display) {
            if(i == R.id.size_Option1){
                setStrokeView();
            }else findViewById(i).setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));
        }

         //This sets the backdrop to be aligned with the drawingview and list options.
         //This is done so even if I also the above layout's background it does change it.
        TextView tv = (TextView) findViewById(R.id.backdrop);
        tv.setLayoutParams(new RelativeLayout.LayoutParams(buttonSize, (buttonSize * 7)));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_END, R.id.drawingView);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.drawingView);

    }

    /**
     * Updates the stroke size in image view dynamically
     * based on screen size
     */
    private void setStrokeView(){
        ImageButton strokeView = (ImageButton)findViewById(R.id.size_Option1);
        strokeView.setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));

        strokeSize+=1 ;
        int strokePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeSize, r.getDisplayMetrics()));
        int padding = (buttonSize-strokePx)/2;

        if(buttonSize-strokePx < 0 ){
            strokeSize = 2;
            strokePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeSize, r.getDisplayMetrics()));
            padding = (buttonSize-strokePx)/2;
        }

        strokeView.setPadding(padding, padding, padding, padding);
        drawView.changeStrokeWidth(strokeSize);

    }

    /**
     * This expands the view on long Click
     * @param v
     */
    private void expand(View v){
        if(v.getId() == R.id.util_Option1){
            findViewById(R.id.util_Option2).setVisibility(View.VISIBLE);
            findViewById(R.id.utility_LL).setBackgroundColor(menuColor);
            findViewById(R.id.shapes_LL).setBackgroundColor(clearColor);
            findViewById(R.id.size_LL).setBackgroundColor(clearColor);

        }else if(v.getId() == R.id.shape_Option1){
            findViewById(R.id.shape_Option2).setVisibility(View.VISIBLE);
            findViewById(R.id.shapes_LL).setBackgroundColor(menuColor);
            findViewById(R.id.utility_LL).setBackgroundColor(clearColor);
            findViewById(R.id.size_LL).setBackgroundColor(clearColor);

        }
    }

    /**
     * This method, swaps the images for the slide out buttons
     * @param id
     */
    private void swap(int id){
        ImageButton ib = (ImageButton) findViewById(id);

        if(id == R.id.util_Option2) {
            ImageButton ib2;
            if(!utilSwitched){
                ib.setImageResource(R.drawable.pencil);
                ib2 = (ImageButton) findViewById(R.id.util_Option1);
                ib2.setImageResource(R.drawable.marker);
                utilSwitched = true;
            }else{
                ib.setImageResource(R.drawable.marker);
                ib2 = (ImageButton) findViewById(R.id.util_Option1);
                ib2.setImageResource(R.drawable.pencil);
                utilSwitched = false;
            }
            updateSelectView(ib2);
        }else if(id == R.id.shape_Option2){
            ImageButton ib2;
            if(!shapeSwitched){
                ib.setImageResource(R.drawable.line);
                ib2 = (ImageButton) findViewById(R.id.shape_Option1);
                ib2.setImageResource(R.drawable.rectangle);
                shapeSwitched = true;
            }else{
                ib.setImageResource(R.drawable.rectangle);
                ib2 = (ImageButton) findViewById(R.id.shape_Option1);
                ib2.setImageResource(R.drawable.line);
                shapeSwitched = false;
            }
            updateSelectView(ib2);
        }
    }


    /**
     * This hides all the "exapansion" from the long presses
     */
    private void hideAllExpansion(){
        findViewById(R.id.util_Option2).setVisibility(View.INVISIBLE);
        findViewById(R.id.shape_Option2).setVisibility(View.INVISIBLE);

        findViewById(R.id.utility_LL).setBackgroundColor(clearColor);
        findViewById(R.id.shapes_LL).setBackgroundColor(clearColor);
        findViewById(R.id.size_LL).setBackgroundColor(clearColor);
    }

    /**
     * Confirmation alert to close out
     * onBackPressed
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder backDialog = new AlertDialog.Builder(this);
        backDialog.setTitle("Go Back");
        backDialog.setMessage("Are you sure you want to go back?\nYou will loose all your progress");
        backDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent startIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(startIntent);
            }
        });
        backDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        backDialog.show();
    }

    /**
     * Sets up all the variables
     * Just to clean up the on create/top of the code
     */
    private void intializeAndSetup(){
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        lastColor = "#FF000000";
        clearColor = Color.parseColor("#00000000");
        menuColor = Color.parseColor("#009999");
        erase = false;
        longClick = false;
        utilSwitched = false;
        shapeSwitched = false;
        menuOpen = false;
        strokeSize = 0;
        display = new int [] {
                    R.id.util_Option1,      R.id.util_Option2,      R.id.shape_Option1,
                    R.id.shape_Option2,     R.id.size_Option1,      R.id.new_imageButton,   R.id.eraser_imageButton,
                    R.id.save_imageButton,  R.id.aliasing_imageButton};
        expandableOptions = new int [] {
                R.id.util_Option1, R.id.shape_Option1};

        drawView = (DrawingView)findViewById(R.id.drawingView);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paintSwatchLayoutRow2); // Paint Swatch Layout
        currPaint = (ImageButton)paintLayout.getChildAt(5); //Black color
        currPaint.setImageResource(R.drawable.paint_pressed);
        selectAnimationShake = AnimationUtils.loadAnimation(this, R.anim.select);
        selectView = (ImageButton) findViewById(R.id.util_Option1);
        updateSelectView(selectView);


    }
}
