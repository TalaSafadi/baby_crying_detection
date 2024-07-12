package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private CustomAdaptor adaptor;
    private ArrayList<Activities> activitiesList = new ArrayList<>();
    private TextView userNameMain;
    private ImageButton Menu;
    Button profileBtn;
    Button homeBtn;
    private Button logout;
    private DrawerLayout drawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        listView = findViewById(R.id.listViewMain);
        userNameMain = findViewById(R.id.UserNameMain);
        Button tutorialButton = findViewById(R.id.totorialgo);
        ImageButton sleepButton = findViewById(R.id.sleepbutton);
        ImageButton AudioButton = findViewById(R.id.audioDetector);
        ImageButton profileButton = findViewById(R.id.profile);
        Menu = findViewById(R.id.MenuButton);
        profileBtn = findViewById(R.id.ProfilePage);
        homeBtn = findViewById(R.id.homePage);
        logout = findViewById(R.id.logout);
        drawerLayout = findViewById(R.id.MenuMianPage);





        // Set up custom adapter
        adaptor = new CustomAdaptor(this, activitiesList);
        listView.setAdapter(adaptor);

        // Add items to activities list
        activitiesList.add(new Activities("Music", "for kids and infents", R.drawable.dino_egg, "For Infants", AudioPlayerActivity.class));
        activitiesList.add(new Activities("Books", "Bed time stories", R.drawable.dino_egg, "For Toddlers", StoryAudio.class));
        activitiesList.add(new Activities("Video Cartoon", "entertaining cartoon", R.drawable.dino_egg, "For Kids", StoryVideo.class));

        adaptor.notifyDataSetChanged();

        // Set click listeners for buttons
        tutorialButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, totorial.class)));
        sleepButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Alert.class)));
        AudioButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AudioRecordingActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, profile.class)));
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigationDrawer();
            }
        });
    }

    private void openNavigationDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.MenuMianPage);
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            drawerLayout.openDrawer(Gravity.RIGHT);
        }
    }
    public class CustomAdaptor extends ArrayAdapter<Activities> {
        private Context context;
        private ArrayList<Activities> activitiesList;

        public CustomAdaptor(Context context, ArrayList<Activities> activitiesList) {
            super(context, R.layout.row, activitiesList);
            this.context = context;
            this.activitiesList = activitiesList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row2, parent, false);
            }

            TextView title = convertView.findViewById(R.id.RowActivityTitle);
            TextView description = convertView.findViewById(R.id.RowAirYear);
            ImageView poster = convertView.findViewById(R.id.audioImage);
           // Button languageButton = convertView.findViewById(R.id.languageButton);
            Button goToActivityButton = convertView.findViewById(R.id.goToActivityButton);

            Activities activity = activitiesList.get(position);

            title.setText(activity.getTitle());
            description.setText(activity.getDescription());
            Glide.with(context).load(activity.getPoster()).into(poster);

            goToActivityButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, activity.getTargetActivity());
                context.startActivity(intent);
            });

            return convertView;
        }
    }
}
