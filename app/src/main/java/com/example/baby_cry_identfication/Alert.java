package com.example.baby_cry_identfication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Alert extends Activity {

    private Vibrator vibrator;
    private static final String TAG = "ProfileActivity";
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String email, childName, emergencyContactName, emergencyContactNumber;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 2;
    private CountDownTimer smsTimer;
    private CountDownTimer callTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        db = FirebaseFirestore.getInstance();

        // Find the buttons by their IDs
        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.stop_button);
        mediaPlayer = MediaPlayer.create(this, R.raw.crying);
        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        if (email != null) {
            fetchUserProfile();
        } else {
            Toast.makeText(this, "No email found in shared preferences", Toast.LENGTH_SHORT).show();
        }

        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        startVibration();
        setVolumeToMax();
        playSound();
        startSMSAndCallTimer();

        // Set OnClickListener on the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start vibrating and start SMS and call timer
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
                stopVibration();
                stopSound();
                stopSMSAndCallTimers();
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
        smsTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                sendSMS();
                startCallTimer();
            }
        }.start();
    }

    private void startCallTimer() {
        callTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                makePhoneCall();
            }
        }.start();
    }

    private void stopSMSAndCallTimers() {
        if (smsTimer != null) {
            smsTimer.cancel();
        }
        if (callTimer != null) {
            callTimer.cancel();
        }
    }

    private void sendSMS() {
        String phoneNumber = emergencyContactNumber;
        String message = "This is the Angel Tears App. MR/MRS " + emergencyContactName + " have been listed as the emergency contact";
        String message2 = "The baby " + childName + " has been crying for a prolonged period without any response from the parents. ";
        String message3 = " If the alert persists after this message, the app will place a call to this number to grab your attention to the situation. ";

        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            smsManager.sendTextMessage(phoneNumber, null, message2, null, null);
            smsManager.sendTextMessage(phoneNumber, null, message3, null, null);
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
        String phoneNumber = emergencyContactNumber;

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
            } else {
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

    private void fetchUserProfile() {
        Log.d(TAG, "Email used for fetching profile: " + email); // Log the email

        if (email != null && !email.isEmpty()) {
            db.collection("Parents").document(email).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Populate the EditTexts with the data
                                    populateProfileFields(document.getData());
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No email found in shared preferences", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateProfileFields(Map<String, Object> data) {
        childName = (String) data.get("ChildName");
        emergencyContactName = (String) data.get("Emergency_contactName");
        emergencyContactNumber = (String) data.get("Emergency_contactNumber");
    }
}
