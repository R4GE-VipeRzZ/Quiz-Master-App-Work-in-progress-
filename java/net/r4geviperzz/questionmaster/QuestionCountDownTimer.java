package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

public class QuestionCountDownTimer {

    private ProgressBar leftProgressBar;
    private ProgressBar rightProgressBar;
    private int totalTime;
    private int timeInterval;
    private CountDownTimer countDownTimer;
    private Context context;

    //Constructor to initialize the progress bars, total time and interval
    public QuestionCountDownTimer(Context passedContext, ProgressBar leftProgressBar, ProgressBar rightProgressBar, int totalTime, int timeInterval) {
        this.context = passedContext;
        this.leftProgressBar = leftProgressBar;
        this.rightProgressBar = rightProgressBar;
        this.totalTime = totalTime;
        this.timeInterval = timeInterval;
    }

    //This method starts the countdown timer
    public void startCountDownTimer() {
        countDownTimer = new CountDownTimer(totalTime, timeInterval) {

            //This method is called on each interval during the countdown
            @Override
            public void onTick(long millisUntilFinished) {
                //Log the remaining time
                Log.e("millisUntilFinished", Long.toString(millisUntilFinished));
                // Calculates the remaining time in milliseconds
                final int secondsLeft = (int) millisUntilFinished;

                //Updates the progress bars on the UI thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        leftProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                leftProgressBar.setProgress(secondsLeft);
                                rightProgressBar.setProgress(secondsLeft);
                            }
                        });
                    }
                }).start();
            }

            //This method is called when the countdown timer finishes
            @Override
            public void onFinish() {
                //Updates the progress bars to show 0 on the UI thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        leftProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.vertical_progress_bar_finished);
                                //Sets the progress value for the left progress bar
                                leftProgressBar.setProgress(0);
                                leftProgressBar.setProgressDrawable(drawable);
                                //Sets the progress value for the right progress bar
                                rightProgressBar.setProgress(0);
                                rightProgressBar.setProgressDrawable(drawable);
                            }
                        });
                    }
                }).start();
                //Logs a message to indicate that the timer has finished
                Log.e("timerFinished", "Timer has finished");
            }
        }.start();
    }

    //This method stops the countdown timer
    public void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}






