package eu.riffer.drawingapp;

import java.util.ArrayList;
import java.util.List;

public class StrokeList {
    private List<Stroke> strokes;

    StrokeList() {
        strokes = new ArrayList<Stroke>();
    }

    void add(Stroke stroke) {
        strokes.add(stroke);
    }

    List<Stroke> getStrokes() {
        return strokes;
    }

    public int size() {
        return strokes.size();
    }

    public void removeLast() {
        int size = strokes.size();
        if (size > 0) {
            strokes.remove(size-1);
        }
    }
}
