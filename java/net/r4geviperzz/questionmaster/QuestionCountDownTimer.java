package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

public class QuestionCountDownTimer{

    private ProgressBar leftProgressBar;
    private ProgressBar rightProgressBar;
    private int totalTime;
    private int timeInterval;
    private CountDownTimer countDownTimer;
    private Context context;
    private StopTimerSoundListener stopTimerSoundListener;

    //Constructor to initialize the progress bars, total time and interval
    public QuestionCountDownTimer(Context passedContext, ProgressBar leftProgressBar, ProgressBar rightProgressBar, int totalTime, int timeInterval, Timer passedTimerSoundInstance) {
        this.context = passedContext;
        this.leftProgressBar = leftProgressBar;
        this.rightProgressBar = rightProgressBar;
        this.totalTime = totalTime;
        this.timeInterval = timeInterval;
        setStopTimeSoundListener(passedTimerSoundInstance);
    }

    //This method assigns the instance that should be used for the interface, in this case an instance of the
    //TimerSound class should be used as the TimerSound class implements the StopTimerSoundListener so that
    //this class can run the method in the TimerSound class that stops the sound that is playing
    public void setStopTimeSoundListener(StopTimerSoundListener listener) {
        this.stopTimerSoundListener = listener;
    }

    //This method starts the countdown timer
    public void startCountDownTimer() {
        countDownTimer = new CountDownTimer(totalTime, timeInterval) {

            //This method is called on each interval during the countdown
            @Override
            public void onTick(long millisUntilFinished) {
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
                                stopCountDownTimer(false);
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
    public void stopCountDownTimer(Boolean calledBySubmitBtn) {
        //Stops the timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        //Checks the interface has been created and calls its method, so that the timer sound will stop
        if (stopTimerSoundListener != null){
            stopTimerSoundListener.onStopTimerSound(calledBySubmitBtn);
        }
    }
}






