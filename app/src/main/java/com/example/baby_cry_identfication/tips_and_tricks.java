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

public class tips_and_tricks extends AppCompatActivity {

    private ListView videoListView;
    private TutorialAdapter tutorialAdapter;
    private ArrayList<Tutorial> tutorialList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_and_tricks);

        // Initialize views
        videoListView = findViewById(R.id.videoListView);

        // Add items to tutorial list
        tutorialList.add(new Tutorial("Why Isn't My Baby Sleeping?","لماذا لا ينام طفلي؟" ,whyBabyNotSleeping.class));
        tutorialList.add(new Tutorial("My Baby Isn't Eating Well","طفلي لا يأكل بشكل جيد" ,Baby_not_Eating_Well.class));
        tutorialList.add(new Tutorial("How to Entertain My Kids","كيفية تسلية أطفالي" ,entertain_kids.class));
        tutorialList.add(new Tutorial("Dealing with Teething","التعامل مع التسنين" ,teething.class));
        tutorialList.add(new Tutorial("Managing Colic","التعامل مع المغص" ,colic.class));
        tutorialList.add(new Tutorial("Bathing Your Baby","استحمام طفلك" ,bathing.class));
        tutorialList.add(new Tutorial("Understanding Baby Milestones","فهم معالم نمو الطفل" ,babyMilestones.class));
        tutorialList.add(new Tutorial("How to Handle Tantrums","كيفية التعامل مع نوبات الغضب" ,handel_tantrumes.class));
        tutorialList.add(new Tutorial("Starting Solid Foods","بدء تقديم الأطعمة الصلبة" ,solidFood.class));
        tutorialList.add(new Tutorial("Babyproofing Your Home","تأمين منزلك للأطفال" ,babyproofying.class));

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
                convertView = layoutInflater.inflate(R.layout.rowtips, parent, false); // Ensure correct layout file is inflated
            }

            TextView En_title = convertView.findViewById(R.id.englishtitle);
            TextView AR_title = convertView.findViewById(R.id.ArabicTitle);


            Button tutorialButton = convertView.findViewById(R.id.totorial);

            Tutorial tutorial = tutorialList.get(position);

            En_title.setText(tutorial.getEn_title());
            AR_title.setText(tutorial.getAr_title());

            tutorialButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, tutorial.getTargetActivity());
                context.startActivity(intent);
            });

            return convertView;
        }
    }

    public class Tutorial {
        private String En_title, Ar_title;

        private Class<?> targetActivity;

        public Tutorial(String En_title, String Ar_title, Class<?> targetActivity) {
            this.En_title = En_title;
            this.Ar_title = Ar_title;
            this.targetActivity = targetActivity;
        }

        public String getEn_title() {
            return En_title;
        }

        public void setEn_title(String en_title) {
            En_title = en_title;
        }

        public String getAr_title() {
            return Ar_title;
        }

        public void setAr_title(String ar_title) {
            Ar_title = ar_title;
        }

        public Class<?> getTargetActivity() {
            return targetActivity;
        }

        public void setTargetActivity(Class<?> targetActivity) {
            this.targetActivity = targetActivity;
        }
    }
}
