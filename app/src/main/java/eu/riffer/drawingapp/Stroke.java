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
        PointF p = new PointF();
        p.x  = x;
        p.y = y;
        points.add(p);
    }

    public int size() {
        return points.size();
    }

    public float getDistance() {
        if (points.size() > 1) {
            float distance = 0;
            float lastX = points.get(0).x;
            float lastY = points.get(0).y;
            boolean first = true;
            for (PointF p : points) {
                if (first) {
                    first = false;
                } else {
                    float deltaX = Math.abs(lastX - p.x);
                    float deltaY = Math.abs(lastY - p.y);
                    distance += deltaX;
                    distance += deltaY;
                    lastX = p.x;
                    lastY = p.y;
                }
            }
            return distance;

        } else {
            return 0;
        }
    }
}
