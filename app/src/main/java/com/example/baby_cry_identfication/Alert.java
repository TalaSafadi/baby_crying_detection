package com.example.baby_cry_identfication;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

public class Alert extends Activity {

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        // Find the buttons by their IDs
        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.stop_button);

        // Initialize Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.crying);

        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Set OnClickListener on the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the volume to maximum
                setVolumeToMax();

                // Start vibrating and playing sound when the start button is clicked
                startVibration();
                playSound();
            }
        });

        // Set OnClickListener on the stop button
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop vibrating and pause sound when the stop button is clicked
                stopVibration();
                pauseSound();
            }
        });
    }

    // Set the device volume to maximum
    private void setVolumeToMax() {
        if (audioManager != null) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0
            );
        }
    }

    // Start vibrating continuously
    private void startVibration() {
        if (vibrator != null) {
            // Vibrate with a pattern of 0 milliseconds for vibration and 1000 milliseconds for pause
            vibrator.vibrate(new long[]{0, 1000}, 0);
        }
    }

    // Stop vibrating
    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    // Start playing sound
    private void playSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Pause sound
    private void pauseSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
