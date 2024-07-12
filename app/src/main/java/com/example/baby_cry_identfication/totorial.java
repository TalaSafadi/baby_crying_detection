package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class totorial extends AppCompatActivity {

    private ListView videoListView;
    private TutorialAdapter tutorialAdapter;
    private ArrayList<Tutorial> tutorialList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totorial);

        // Initialize views
        videoListView = findViewById(R.id.videoListView);

        // Add items to tutorial list
        tutorialList.add(new Tutorial("App features ", AppFeatures_tutorial.class));
        tutorialList.add(new Tutorial("Alerts & Audio Detection ", Alert_Tutorial.class));
        tutorialList.add(new Tutorial("Sleep tracking",  FingerprintTutorial.class));
        tutorialList.add(new Tutorial("Kids activity",  Kids_activity_Tutorial.class));
        tutorialList.add(new Tutorial("Forget password",  Forget_password_Tutorial.class));





        // Set up custom adapter
        tutorialAdapter = new TutorialAdapter(this, tutorialList);
        videoListView.setAdapter(tutorialAdapter);
    }

    public class TutorialAdapter extends ArrayAdapter<Tutorial> {
        private Context context;
        private ArrayList<Tutorial> tutorialList;

        public TutorialAdapter(Context context, ArrayList<Tutorial> tutorialList) {
            super(context, R.layout.rowstories, tutorialList); // Ensure correct layout file is referenced
            this.context = context;
            this.tutorialList = tutorialList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.rowtutorial, parent, false); // Ensure correct layout file is inflated
            }

            TextView title = convertView.findViewById(R.id.englishtitle);
            Button tutorialButton = convertView.findViewById(R.id.totorial);

            Tutorial tutorial = tutorialList.get(position);

            title.setText(tutorial.getTitle());

            tutorialButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, tutorial.getTargetActivity());
                context.startActivity(intent);
            });

            return convertView;
        }
    }

    public class Tutorial {
        private String title;
        private Class<?> targetActivity;

        public Tutorial(String title, Class<?> targetActivity) {
            this.title = title;
            this.targetActivity = targetActivity;
        }

        public String getTitle() {
            return title;
        }

        public Class<?> getTargetActivity() {
            return targetActivity;
        }
    }
}
