package eu.riffer.drawingapp;

import android.support.v7.widget.Toolbar;
import android.view.View;

import android.content.Context;
import android.util.AttributeSet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.widget.ProgressBar;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xe3660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private DrawingRecorder recoder;

    public DrawingView(Context context, AttributeSet attribeSet) {
        super(context, attribeSet);

//        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
//        progressBar.setProgress(34);

        recoder = new DrawingRecorder();

        setupDrawing();
    }

    private void setupDrawing() {
        this.drawPath = new Path();
        this.drawPaint = new Paint();

        drawPaint.setColor(this.paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        this.canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = Math.round(event.getX());
        float touchY = Math.round(event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recoder.clear();
                recoder.add(touchX, touchY);
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                recoder.add(touchX, touchY);
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                recoder.save();
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }
}
