package eu.riffer.drawingapp;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class Stroke {
    private List<PointF> points;

    Stroke() {
        points = new ArrayList<PointF>();
    }

    void setPoints(List<PointF> newPoints) {
        points = newPoints;
    }

    List<PointF> getPoints() {
        return points;
    }

    public void add(float x, float y) {
        points.add(new PointF(x, y));
    }

    public int size() {
        return points.size();
    }
}
