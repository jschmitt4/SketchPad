package com.danielchoi.sketchpad;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

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
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    public float defaultStrokeWidth = 2;
    public float strokeWidth;
    private DisplayMetrics dm;
    static enum Mode {DRAW, LINE, RECT};
    private Mode currentMode = Mode.DRAW;

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
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint); //This draws the path from the drawPaint's setup
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Detect user touch to draw
        float touchX = event.getX();
        float touchY = event.getY();

        if(currentMode == Mode.DRAW) {
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


        }else if(currentMode == Mode.RECT){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    xFinish = (int) event.getX();
                    yFinish = (int) event.getY();
                    Rect rect = new Rect();
                    rect.set(xStart, yStart, xFinish, yFinish);
                    drawCanvas.drawRect(rect, drawPaint);
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        }
        invalidate();
        return true;
    }
    public void setColor(String newColor){
        //set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void changeStrokeWidth(int dp){
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,dm);
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setColor(paintColor);
    }

    public void eraser(){
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,dm);
        drawPaint.setColor(Color.parseColor("#FFFFFF"));
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    public void newSheet(){
        drawCanvas.drawColor(Color.parseColor("#FFFFFF"));
        invalidate();

    }

    public void setCurrentMode(String m){
        switch (m) {
            case "DRAW":
                currentMode = Mode.DRAW;
                break;
            case "LINE":
                currentMode = Mode.LINE;
                break;
            case "RECT":
                currentMode = Mode.RECT;
                break;
            default:
                currentMode = Mode.DRAW;
                break;
        }
    }

}
