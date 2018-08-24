package eu.riffer.drawingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private View mDrawingView;
    private ProgressBar progressBar;
    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressbar);
        drawingView = findViewById(R.id.drawing);

        drawingView.setProgressBar(progressBar);
    }
}
