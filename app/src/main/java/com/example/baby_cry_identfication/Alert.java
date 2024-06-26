package com.example.baby_cry_identfication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Alert extends Activity {

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 2;

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

        // Start vibration, sound and SMS/Call timer on activity start
        startVibration();
        setVolumeToMax();
        playSound();
        startSMSAndCallTimer();

        // Set OnClickListener on the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start vibrating, sound and SMS/Call timer when the start button is clicked
                startVibration();
                setVolumeToMax();
                playSound();
                startSMSAndCallTimer();
            }
        });

        // Set OnClickListener on the stop button
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop vibrating and sound when the stop button is clicked
                stopVibration();
                stopSound();
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

    // Stop playing sound
    private void stopSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startSMSAndCallTimer() {
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                sendSMS();
                makePhoneCall();
            }
        }.start();
    }

    private void sendSMS() {
        String phoneNumber = "+970599506228";
        String message = "Baby has been crying for a long time, parents cannot be reached. Please check on the baby. " + getLocation();

        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("Alert", "SMS sent to: " + phoneNumber);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } else {
            // Request permissions if not granted
            String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, PERMISSION_REQUEST_SEND_SMS);
        }
    }

    // Make phone call
    private void makePhoneCall() {
        String phoneNumber = "+970599506228";

        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
        } else {
            String dial = "tel:" + phoneNumber;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(dial));
            callIntent.putExtra("android.telecom.extra.START_CALL_WITH_SPEAKERPHONE", true); // Start call with speakerphone on
            startActivity(callIntent);
        }
    }

    private String getLocation() {
        // Initialize location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationString = "";

        if (locationManager != null &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get last known location
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                locationString = "My location is: " + latitude + ", " + longitude;
            } else {
                locationString = "Location not available";
            }
        } else {
            locationString = "Location permission not granted";
        }

        return locationString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
                Toast.makeText(getApplicationContext(), "Permission denied. SMS not sent.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied. Cannot make phone call.", Toast.LENGTH_LONG).show();
            }
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
