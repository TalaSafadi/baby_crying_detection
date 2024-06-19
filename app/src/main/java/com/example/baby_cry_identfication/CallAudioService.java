package com.example.baby_cry_identfication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

public class CallAudioService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.crying); // Replace with your audio file
        mediaPlayer.setLooping(true); // Loop the audio playback
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_CALL); // Set audio mode to in-call
            audioManager.setSpeakerphoneOn(true); // Turn on the speakerphone

            mediaPlayer.start(); // Start audio playback
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
