package com.example.baby_cry_identfication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VoiceRecorder extends Service {

    private static final String CHANNEL_ID = "VoiceRecorderServiceChannel";
    private static final int SAMPLE_RATE = 16000;
    private static final String UPLOAD_URL = "https://your-backend-url.com/upload";
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private static final int CHUNK_DURATION_MS = 5000; // 5 seconds

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ExecutorService uploadExecutorService = Executors.newSingleThreadExecutor();
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());
        startRecording();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Voice Recorder Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Voice Recorder")
                .setContentText("Recording in progress...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }

    private void startRecording() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        audioRecord.startRecording();
        isRecording = true;

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[BUFFER_SIZE];
                long nextSendTime = System.currentTimeMillis() + CHUNK_DURATION_MS;

                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        synchronized (byteArrayOutputStream) {
                            byteArrayOutputStream.write(buffer, 0, read);
                        }
                    }
                    if (System.currentTimeMillis() >= nextSendTime) {
                        byte[] audioChunk;
                        synchronized (byteArrayOutputStream) {
                            audioChunk = byteArrayOutputStream.toByteArray();
                            byteArrayOutputStream.reset();
                        }
                        uploadExecutorService.execute(new UploadTask(audioChunk));
                        nextSendTime = System.currentTimeMillis() + CHUNK_DURATION_MS;
                    }
                }
            }
        });
    }

    private class UploadTask implements Runnable {
        private final byte[] audioData;

        public UploadTask(byte[] audioData) {
            this.audioData = audioData;
        }

        @Override
        public void run() {
            sendAudioToBackend(audioData);
        }
    }

    private void sendAudioToBackend(byte[] audioData) {
        try {
            URL url = new URL(UPLOAD_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "audio/wav");
            urlConnection.getOutputStream().write(audioData);
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("VoiceRecorderService", "Audio data sent successfully");
            } else {
                Log.e("VoiceRecorderService", "Failed to send audio data. Response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e("VoiceRecorderService", "Error sending audio data", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
        }
    }
}
