package com.danielchoi.sketchpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by JT on 3/14/2017.
 */

public class DrawingView extends View {
    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        //get drawing area setup for interaction
    }
}
