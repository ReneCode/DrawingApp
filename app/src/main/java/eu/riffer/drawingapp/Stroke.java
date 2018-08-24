package eu.riffer.drawingapp;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Stroke {
    private List<Point> points;

    Stroke() {
        points = new ArrayList<Point>();
    }

    void setPoints(List<Point> newPoints) {
        points = newPoints;
    }

    List<Point> getPoints() {
        return points;
    }

    public void add(int x, int y) {
        points.add(new Point(x, y));
    }

    public int size() {
        return points.size();
    }
}
