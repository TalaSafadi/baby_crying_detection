package com.example.baby_cry_identfication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.baby_cry_identfication.Alert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioRecordingActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private Button startButton;
    private TextView resultTextView;
    private AudioRecord recorder;
    private boolean isRecording = false;
    private int bufferSize;
    private static final int SAMPLE_RATE = 16000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> recorderHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detection);

        startButton = findViewById(R.id.startButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Request RECORD_AUDIO permission
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // Initialize notification channel (for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NotificationHelper.getChannelId(),
                    "Baby Crying Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionToRecordAccepted) {
                    if (isRecording) {
                        stopRecording();
                    } else {
                        startRecording();
                    }
                } else {
                    ActivityCompat.requestPermissions(AudioRecordingActivity.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
                }
            }
        });
    }

    private void startRecording() {
        isRecording = true;
        startButton.setText("Stop Recording");
        resultTextView.setText("Recording...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        recorder.startRecording();

        // Schedule recorder task to run every 5 seconds
        recorderHandler = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                processAudio();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void stopRecording() {
        isRecording = false;
        startButton.setText("Start Recording");
        resultTextView.setText("Processing...");
        if (recorderHandler != null) {
            recorderHandler.cancel(true);
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void processAudio() {
        byte[] buffer = new byte[bufferSize];
        int bytesRead = recorder.read(buffer, 0, bufferSize);

        if (bytesRead > 0) {
            try {
                writeAudioDataToWavFile(buffer);
                File wavFile = new File(getExternalFilesDir(null), "audio.wav");

                OkHttpClient client = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse("audio/wav"), wavFile);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "audio.wav", fileBody)
                        .build();

                Request request = new Request.Builder()
                        .url("https://dry-reef-34745-d937e9de61ff.herokuapp.com/predict")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                resultTextView.setText("Network error occurred. Please try again.");
                            }
                        });
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
                                            openAlertActivity();
                                        } else {
                                            resultTextView.setText("No crying detected: " + label);
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        resultTextView.setText("Error processing response: " + e.getMessage());
                                    }
                                });
                            }
                        } else {
                            final String errorBody = response.body().string();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextView.setText("Error: Unsuccessful response from server. Code: " + response.code() + " Message: " + errorBody);
                                }
                            });
                        }
                    }
                });
            } catch (IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextView.setText("Error writing audio file: " + e.getMessage());
                    }
                });
            }
        }
    }

    private void writeWavHeader(FileOutputStream out, int totalAudioLen, int totalDataLen, int longSampleRate, int channels, int byteRate) throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * RECORDER_BPP / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private void writeAudioDataToWavFile(byte[] pcmData) throws IOException {
        File wavFile = new File(getExternalFilesDir(null), "audio.wav");

        int totalAudioLen = pcmData.length;
        int totalDataLen = totalAudioLen + 36;
        int longSampleRate = SAMPLE_RATE;
        int channels = 1;
        int byteRate = 16 * SAMPLE_RATE * channels / 8;

        try (FileOutputStream out = new FileOutputStream(wavFile)) {
            writeWavHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            out.write(pcmData);
        }
    }

    private void sendAlert() {
        Intent intent = new Intent(this, AudioRecordingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, NotificationHelper.getChannelId())
                    .setContentTitle("Baby Crying Detected")
                    .setContentText("A baby cry has been detected!")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        handler.post(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText("Crying detected!");
            }
        });
    }


    private void openAlertActivity() {
        Intent alertIntent = new Intent(this, Alert.class);
        startActivity(alertIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToRecordAccepted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private static final int RECORDER_BPP = 16;
}
