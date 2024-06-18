package com.example.baby_cry_identfication;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SleepTrackerActivity extends AppCompatActivity {

    private EditText goingToSleepInput;
    private EditText wakingUpInput;
    private Button dateButton;
    private Button addButton;
    private LineChart sleepChart;
    private FirebaseFirestore db;
    private String userEmail;
    private List<String> sleepDates;
    private TextView sleepDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker_improve);

        // Initialize the UI components
        goingToSleepInput = findViewById(R.id.going_to_sleep);
        wakingUpInput = findViewById(R.id.wakingUp);
        dateButton = findViewById(R.id.dateButton2);
        addButton = findViewById(R.id.addButton2);
        sleepChart = findViewById(R.id.sleepChart);
        sleepDuration = findViewById(R.id.sleepTime);
        db = FirebaseFirestore.getInstance();

        // Retrieve the user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("email", null);

        // Set click listeners
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSleepData();
            }
        });

        loadSleepData();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SleepTrackerActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                        dateButton.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addSleepData() {
        String date = dateButton.getText().toString();
        String goingToSleep = goingToSleepInput.getText().toString();
        String wakingUp = wakingUpInput.getText().toString();

        if (userEmail != null && !date.isEmpty() && !goingToSleep.isEmpty() && !wakingUp.isEmpty()) {
            db.collection("Parents").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        List<String> sleepData = (List<String>) document.get("sleep");
                        if (sleepData == null) {
                            sleepData = new ArrayList<>();
                        }
                        sleepData.add(date + ", " + goingToSleep + " to " + wakingUp);

                        db.collection("Parents").document(userEmail)
                                .update("sleep", sleepData)
                                .addOnSuccessListener(aVoid -> {
                                    loadSleepData();
                                })
                                .addOnFailureListener(e -> e.printStackTrace());
                    }
                }
            });
        } else {
            Toast.makeText(SleepTrackerActivity.this, "Please enter date, going to sleep time, and waking up time", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSleepData() {
        sleepDuration.setText(" sleep duration");

        if (userEmail != null) {
            db.collection("Parents").document(userEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                DocumentSnapshot document = task.getResult();
                                List<String> sleepData = (List<String>) document.get("sleep");
                                if (sleepData != null) {
                                    List<Entry> entries = new ArrayList<>();
                                    sleepDates = new ArrayList<>();
                                    for (int i = 0; i < sleepData.size(); i++) {
                                        String[] parts = sleepData.get(i).split(", ");
                                        if (parts.length == 2) {
                                            String date = parts[0];
                                            String[] times = parts[1].split(" to ");
                                            if (times.length == 2) {
                                                float sleepHours = calculateSleepHours(times[0], times[1]);
                                                entries.add(new Entry(i, sleepHours));
                                                sleepDates.add(date);
                                            }
                                        }
                                    }
                                    displayChart(entries);
                                }
                            }
                        }
                    });
        }
    }

    private float calculateSleepHours(String goingToSleep, String wakingUp) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            // Parse the times
            long sleepTimeMillis = format.parse(goingToSleep).getTime();
            long wakeTimeMillis = format.parse(wakingUp).getTime();

            // Calculate the duration in milliseconds
            long durationMillis;
            if (wakeTimeMillis >= sleepTimeMillis) {
                durationMillis = wakeTimeMillis - sleepTimeMillis;
                float hours = durationMillis / (1000f * 60 * 60); // convert milliseconds to hours
                return hours;
            } else {
                durationMillis = (wakeTimeMillis + (24 * 60 * 60 * 1000) - sleepTimeMillis)  ; // Add one day if wake time is before sleep time
                float hours = durationMillis / (1000f * 60 * 60); // convert milliseconds to hours
                hours=hours-12;
                return hours;
            }

            // Convert milliseconds to hours




        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private void displayChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Sleep Data");
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4.5f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(dataSet.getColor()); // Set the value text color to the same as the line color

        // Set custom value formatter for data points
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                float value = entry.getY();  // Assuming value is float representing total sleep duration in hours

                int hours = (int) value;  // Get the integer part as hours
                int minutes = (int) ((value - hours) * 60);  // Convert the fractional part to minutes
                sleepDuration.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));

                return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);

            }
        });

        LineData lineData = new LineData(dataSet);
        sleepChart.setData(lineData);

        XAxis xAxis = sleepChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(sleepDates));
        xAxis.setLabelRotationAngle(90);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setGranularityEnabled(true);

        sleepChart.invalidate(); // refresh the chart
    }


}
