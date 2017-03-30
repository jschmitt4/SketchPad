package com.danielchoi.sketchpad;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by JT on 3/14/2017.
 */

public class DrawingView extends View {
    /**
     * Instance Variables
     */
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint, paintLine, rectPaint;
    //initial color
    private int paintColor = 0xFF000000;
    private int white = Color.parseColor("#FFFFFF");
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    public float defaultStrokeWidth = 2;
    public float strokeWidth;
    public int screenHeight;
    public Rect rect;
    private DisplayMetrics dm;
    public enum Mode {PENCIL, MARKER, LINE, RECT, ERASER};
    private Mode currentMode = Mode.PENCIL;
    boolean alias = true;

    //line/rectangle start and finish coordinates.
    private int xStart;
    private int yStart;
    private int xFinish;
    private int yFinish;

    /**
     * Constructors to set up the widget
     * @param context
     */
    public DrawingView (Context context){
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }

    /**
     * This is the setup for path tool of the app
     * IE: Pencil, Marker, & Brush
     */
    private void setupDrawing() {
        //get drawing area setup for interaction

        rect = new Rect();
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(getAlias());
        // Sets up a temporary brush size
        dm = getResources().getDisplayMetrics();
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,defaultStrokeWidth,dm);
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //instantiate canvas paint object
        //Paint flag that enables dithering when blitting.
        //Enabling this flag applies a dither to any blit operation where the target's colour space is more constrained than the source
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        screenHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Detect user touch to draw
        float touchX = event.getX();
        float touchY = event.getY();

        if(currentMode == Mode.PENCIL || currentMode == Mode.MARKER || currentMode == Mode.ERASER) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        }else if(currentMode == Mode.LINE){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    xFinish = (int) event.getX();
                    yFinish = (int) event.getY();
                    drawCanvas.drawLine(xStart,yStart,xFinish,yFinish,drawPaint);
                    break;
                default:
                    return false;
            }
        }else if(currentMode == Mode.RECT){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    xFinish = (int) event.getX();
                    yFinish = (int) event.getY();
                    rect.set(xStart, yStart, xFinish, yFinish);
                    drawCanvas.drawRect(rect, drawPaint);
                    break;
                default:
                    return false;
            }
        }
        invalidate();
        return true;
    }
    public void setColor(String newColor){
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void changeStrokeWidth(int dp){
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,dm);
        drawPaint.setStrokeWidth(strokeWidth);
    }

    public void newSheet(){
        drawCanvas.drawColor(white);
        invalidate();
    }

    public int getScreenHeight(){
        return screenHeight;
    }

    public void setCurrentMode(String m){
        switch (m) {
            case "DRAW":
                currentMode = Mode.PENCIL;
                drawPaint.setStrokeCap(Paint.Cap.ROUND);
                drawPaint.setStyle(Paint.Style.STROKE);
                break;
            case "MARKER":
                currentMode = Mode.MARKER;
                drawPaint.setStrokeCap(Paint.Cap.SQUARE);
                drawPaint.setStyle(Paint.Style.STROKE);
                break;
            case "LINE":
                currentMode = Mode.LINE;
                drawPaint.setStrokeCap(Paint.Cap.ROUND);
                drawPaint.setStyle(Paint.Style.STROKE);
                break;
            case "RECT":
                currentMode = Mode.RECT;
                drawPaint.setStrokeCap(Paint.Cap.SQUARE);
                drawPaint.setStyle(Paint.Style.FILL);
                break;
            case "ERASER":
                currentMode = Mode.ERASER;
                drawPaint.setColor(white);
                drawPaint.setStrokeCap(Paint.Cap.SQUARE);
                break;
            default:
                currentMode = Mode.PENCIL;
                drawPaint.setStrokeCap(Paint.Cap.ROUND);
                drawPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    /**
     * setAlias is a setter method for SketchActivity to toggle Anti Aliasing on/off based on
     * the Anti Aliasing button being clicked in SketchActivity. setAlias also calls "setAntiAlias"
     * to update the aliasing to be used.
     * @param aa
     */
    public void setAlias(boolean aa){
        alias = aa;
        drawPaint.setAntiAlias(alias);
    }

    /**
     * getAlias is a getter method that simply returns the boolean stored in the alias variable.
     * This is most helpful in SketchActivity where logic is deciding whether anti aliasing should be
     * used or not.
     * @return alias
     */
    public Boolean getAlias(){
        return alias;
    }

}
