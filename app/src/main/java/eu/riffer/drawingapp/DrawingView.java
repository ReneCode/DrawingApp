package eu.riffer.drawingapp;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
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

    private ProgressBar progressBar;
    private int maxPointCount = 50;

    private Stroke currentStroke;

    public DrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

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
        int touchX = Math.round(event.getX());
        int touchY = Math.round(event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startStroke(touchX, touchY);
                drawPath.moveTo(touchX, touchY);
                progressBar.setMax(maxPointCount);
                break;
            case MotionEvent.ACTION_MOVE:
                int count = strokeSize();
                if (count < maxPointCount) {
                    continueStroke(touchX, touchY);
                    drawPath.lineTo(touchX, touchY);
                    progressBar.setProgress(count);
                }
                break;
            case MotionEvent.ACTION_UP:
                finishStroke(touchX, touchY);
                progressBar.setProgress(0);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private void drawStokeToPath(Stroke stroke, Path path) {
        path.reset();
        boolean first = true;
        for (Point point : stroke.getPoints()) {
            if (first) {
                path.moveTo(point.x, point.y);
                first = false;
            } else {
                path.lineTo(point.x, point.y);
            }
        }
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private void startStroke(int x, int y) {
        currentStroke = new Stroke();
        currentStroke.add(x, y);
    }

    private void continueStroke(int x, int y) {
        currentStroke.add(x, y);
    }

    private void finishStroke(int x, int y) {
        currentStroke.add(x, y);
        // start async exchange Task
        new ExchangeStrokeTask().execute(currentStroke);
        currentStroke = null;
    }

    private int strokeSize() {
        return currentStroke.size();
    }

    // -----------------


    public class ExchangeStrokeTask extends AsyncTask<Stroke, Void, Stroke> {

        @Override
        protected Stroke doInBackground(Stroke... strokes) {
            // got an array of strokes - but we only use one - the first one
            Stroke orginalStroke = strokes[0];
            // send orginal Stroke to backend
            return ApiUtility.setStroke(orginalStroke);
        }

        @Override
        protected void onPostExecute(final Stroke newStroke) {
            // task has finished
            // got new / exchanged Stroke from the backend

            // https://stackoverflow.com/questions/1877417/how-to-set-a-timer-in-android
            // delay the exchange of the orginal and new stroke
            Handler handler = new Handler();
            Runnable runable = new Runnable() {
                @Override
                public void run() {
                    // paint stroke into path
                    drawStokeToPath(newStroke, drawPath);
                    // paint path into bitmap
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    invalidate();
                }
            };
            int waitMs = 1000;
            handler.postDelayed(runable, waitMs);

        }
    }
}
