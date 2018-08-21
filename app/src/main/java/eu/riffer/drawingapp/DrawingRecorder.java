package eu.riffer.drawingapp;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DrawingRecorder {
    List<PointF> points;


    DrawingRecorder() {
        points = new ArrayList<PointF>();
    }


    public void save() {
    }

    public void clear() {
        points.clear();
    }

    public void add(float x, float y) {
        points.add(new PointF(x, y));
    }
}
