package com.example.baby_cry_identfication;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SleepTrackerActivity extends AppCompatActivity {

    private EditText dateInput;
    private EditText sleepHoursInput;
    private Button dateButton;
    private Button addButton;
    private LineChart sleepChart;
    private FirebaseFirestore db;
    private String userEmail;
    private List<String> sleepDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);

        dateInput = findViewById(R.id.dateInput);
        sleepHoursInput = findViewById(R.id.sleepHoursInput);
        dateButton = findViewById(R.id.dateButton);
        addButton = findViewById(R.id.addButton);
        sleepChart = findViewById(R.id.sleepChart);
        db = FirebaseFirestore.getInstance();

        // Retrieve the user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("email", null);

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
                        dateInput.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addSleepData() {
        String date = dateInput.getText().toString();
        String sleepHours = sleepHoursInput.getText().toString();
        if (userEmail != null && !date.isEmpty() && !sleepHours.isEmpty()) {
            db.collection("Parents").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        List<String> sleepData = (List<String>) document.get("sleep");
                        if (sleepData == null) {
                            sleepData = new ArrayList<>();
                        }
                        sleepData.add(date + ", " + sleepHours + " hours");

                        db.collection("Parents").document(userEmail)
                                .update("sleep", sleepData)
                                .addOnSuccessListener(aVoid -> loadSleepData())
                                .addOnFailureListener(e -> e.printStackTrace());
                    }
                }
            });
        } else {
            Toast.makeText(SleepTrackerActivity.this, "Please enter both date and hours of sleep", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSleepData() {
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
                                            float hours = Float.parseFloat(parts[1].replace(" hours", ""));
                                            entries.add(new Entry(i, hours));
                                            sleepDates.add(date);
                                        }
                                    }
                                    displayChart(entries);
                                }
                            }
                        }
                    });
        }
    }

    private void displayChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Sleep Data");
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
