package eu.riffer.drawingapp;

import android.graphics.Point;
import android.graphics.PointF;
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
    private int maxPointDistance = 400;

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
        drawPaint.setStrokeWidth(10);
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
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startStroke(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                continueStroke(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                finishStroke(touchX, touchY);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private Path drawStokeToPath(Stroke stroke) {
        Path path = new Path();
        boolean first = true;
        for (PointF point : stroke.getPoints()) {
            if (first) {
                path.moveTo(point.x, point.y);
                first = false;
            } else {
                path.lineTo(point.x, point.y);
            }
        }
        return path;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private void startStroke(float x, float y) {
        currentStroke = new Stroke();
        currentStroke.add(x, y);
        drawPath.reset();
        drawPath.moveTo(x, y);
        progressBar.setMax(maxPointDistance);
    }

    private void continueStroke(float x, float y) {
        int distance = (int)currentStroke.getDistance();
        if (distance < maxPointDistance) {
            currentStroke.add(x, y);
            drawPath.lineTo(x, y);
            progressBar.setProgress(distance);
        }
    }

    private void finishStroke(float x, float y) {
        // last touch is allready in currentStroke

        // start async exchange Task
        new ExchangeStrokeTask().execute(currentStroke);
        currentStroke = null;
        progressBar.setProgress(0);
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
                    Path newPath = drawStokeToPath(newStroke);
                    // paint into bitmap (drawCanvas)
                    drawCanvas.drawPath(newPath, drawPaint);
                    invalidate();
                }
            };
            int waitMs = 2000;
            handler.postDelayed(runable, waitMs);

        }
    }
}
