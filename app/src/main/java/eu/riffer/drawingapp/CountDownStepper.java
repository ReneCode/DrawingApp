package eu.riffer.drawingapp;

import android.os.Handler;

public class CountDownStepper {
    private int mSteps;
    private int currentStep;
    private long delayMs;

    public CountDownStepper(long millisInFuture, int steps) {
        mSteps = steps;
        if (steps <= 0) {
            steps = 1;
        }
        delayMs = millisInFuture / (long)steps;
    }

    public void start() {
        currentStep = 0;
        next();
    }

    private Boolean next() {
        if (currentStep < mSteps) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    onRunableFinished();
                }
            };
            Handler h = new Handler();
            h.postDelayed(r, delayMs);
            return true;
        } else {
            return false;
        }
    }

    private void onRunableFinished() {
        onStep(currentStep);
        currentStep++;
        next();
    }

    public void onStep(int remainingSteps) {
    }

}
