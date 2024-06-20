package com.example.baby_cry_identfication;

import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AudioDetectionService extends Service {

    private static final int SAMPLE_RATE = 16000;
    private AudioRecord recorder;
    private boolean isRecording = false;
    private int bufferSize;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> recorderHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, getNotification());
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.getChannelId())
                .setContentTitle("Audio Detection Service")
                .setContentText("Detecting baby cries...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true);

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    private void startRecording() {
        isRecording = true;
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
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
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        recorder.startRecording();

        recorderHandler = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                processAudio();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void stopRecording() {
        isRecording = false;
        if (recorderHandler != null) {
            recorderHandler.cancel(true);
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        stopForeground(true);
        stopSelf();
    }

    private void processAudio() {
        byte[] buffer = new byte[bufferSize];
        int bytesRead = recorder.read(buffer, 0, bufferSize);

        if (bytesRead > 0) {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), buffer);
            Request request = new Request.Builder()
                    .url("https://dry-reef-34745-d937e9de61ff.herokuapp.com/predict")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String label = jsonObject.getString("label");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if ("crying".equals(label)) {
                                        sendAlert();
                                    } else {
                                        // Handle non-crying case

                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void sendAlert() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.getChannelId())
                .setContentTitle("Baby Crying Alert")
                .setContentText("Your baby is crying!")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(2, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }
}
