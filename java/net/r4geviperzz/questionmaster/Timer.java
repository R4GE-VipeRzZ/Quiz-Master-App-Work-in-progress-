package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.media.MediaPlayer;

public class Timer implements StopTimerSoundListener{
    private MediaPlayer mediaPlayer;
    private Thread thread;
    private boolean isRunning = false;
    private boolean shouldStop = false;
    private Float volumeValue;
    private String tickSoundName;
    private String submitSoundName;
    private Context context;
    //This class variable is used so the clicking the submit button doesn't run the
    //code again if it has already been ran due to the timer running out
    private Boolean timerHasStopped;

    public Timer(Context context, DBHelper passedDBHelper) {
        this.context = context;

        this.volumeValue = passedDBHelper.getTimerVolumeValue("1");
        this.tickSoundName = passedDBHelper.getTimerTickSound("1");
        this.submitSoundName = passedDBHelper.getTimerFinishSound("1");

        //Loads the sound from the raw resources folder
        int tickSoundResId = context.getResources().getIdentifier(tickSoundName, "raw", context.getPackageName());
        this.mediaPlayer = MediaPlayer.create(context, tickSoundResId);
        this.timerHasStopped = false;
    }

    //Implements the interface method that will run when the method is called in the interface
    @Override
    public void onStopTimerSound(Boolean passedCalledBySubmitBtn){
        //Calls the method to stop the sound
        stop(passedCalledBySubmitBtn);
    }

    public void start() {
        // Checks if the thread is null or has finished running
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Sets the media player to loop the sound
                    mediaPlayer.setLooping(true);
                    // Sets the flag to indicate the thread is running
                    isRunning = true;
                    // Continuously plays the sound while the thread is running
                    while (isRunning) {
                        // Moves the media player to the start of the sound
                        try {
                            mediaPlayer.seekTo(0);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        // Sets the sounds volume with 0.0 been silent and 1.0 been max volume
                        mediaPlayer.setVolume(volumeValue, volumeValue);
                        // Starts playing the sound
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        // Set up onCompletion listener to restart the sound immediately
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (isRunning) {
                                    try {
                                        mediaPlayer.seekTo(0);
                                        mediaPlayer.start();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        try {
                            // Thread.sleep is used to synchronise the looping of the audio file with the background
                            // thread that is playing it. Without the sleep, the background thread would loop the audio
                            // file as quickly as possible, causing the audio file to overlap itself
                            // Sleep for the duration of the sound
                            Thread.sleep(mediaPlayer.getDuration());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Check if the thread should stop
                        if (shouldStop) {
                            break;
                        }
                    }
                }
            });
            // Starts the thread
            thread.start();
        }
    }

    public void stop(Boolean passedCalledBySubmitBtn) {
        if (timerHasStopped == false){
            //Sets the flag to indicate the thread should stop
            shouldStop = true;
            if (mediaPlayer != null) {
                //Stops the current mediaPlayer object
                mediaPlayer.stop();
                //Release the current mediaPlayer object
                mediaPlayer.release();
                mediaPlayer = null;
            }

            //This if statement is used to stop the submit button from triggering the timer end sound, this is needed
            //as if the submit button is pressed to end the timer early then the timer end sound doesn't need to be played
            //and if the submit button is being pressed after the timer ends then the timer end sound shouldn't play
            if (passedCalledBySubmitBtn == false) {
                //Create a new MediaPlayer object to play the finish sound
                int finishSoundResId = context.getResources().getIdentifier(submitSoundName, "raw", context.getPackageName());
                final MediaPlayer mediaPlayer2 = MediaPlayer.create(context, finishSoundResId );
                //Sets the sounds volume with 0.0 been silent and 1.0 been max volume
                mediaPlayer2.setVolume(volumeValue, volumeValue);
                mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer2.release();
                    }
                });

                //Start the mediaPlayer2 in a new thread
                new Thread(new Runnable() {
                    public void run() {
                        mediaPlayer2.start();
                    }
                }).start();
            }

            timerHasStopped = true;
        }
    }
}
