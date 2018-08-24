package eu.riffer.drawingapp;

import android.graphics.PointF;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class StrokeTest {
    @Test
    public void constructor() {
        Stroke stroke = new Stroke();

        List<PointF> points = stroke.getPoints();

        assertEquals(0, points.size());
        assertEquals(0, stroke.size());
    }

    @Test
    public void add() {
        Stroke stroke = new Stroke();
        stroke.add(4, 5);

        List<PointF> points = stroke.getPoints();

        assertEquals(1, points.size());
        assertEquals(4, points.get(0).x, 0.001);
        assertEquals(5, points.get(0).y, 0.001);
    }

    @Test
    public void distance_onePoint() {
        Stroke stroke = new Stroke();
        stroke.add(4, 5);

        float distance = stroke.getDistance();
        assertEquals(0, distance, 0.001);
    }

    @Test
    public void distance_twoPoints() {
        Stroke stroke = new Stroke();
        stroke.add(4, 5);
        stroke.add(14, 5);

        float distance = stroke.getDistance();
        assertEquals(10, distance, 0.001);
    }

    @Test
    public void distance_threePoints() {
        Stroke stroke = new Stroke();
        stroke.add(4, 5);
        stroke.add(14, 5);
        stroke.add(14, 25);

        float distance = stroke.getDistance();
        assertEquals(30, distance, 0.001);
    }

}
