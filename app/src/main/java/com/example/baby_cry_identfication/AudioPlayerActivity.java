package com.example.baby_cry_identfication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
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

public class AudioPlayerActivity extends AppCompatActivity {
    private ListView ShowsList;
    private CustomAdaptor adaptor;
    private ArrayList<Media> SList = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        ShowsList = findViewById(R.id.ShowsList);
        adaptor = new CustomAdaptor(this, SList);
        ShowsList.setAdapter(adaptor);

        SList.add(new Media("عهد الاصدقاء", "اغنية عهد الاصقاء من كرتون عهد الاصدقاء", R.drawable.music2, R.raw.arhdalasdikaarabic,"Ar"));
        SList.add(new Media("onceuponadecember", "  Anastasia once upon a december", R.drawable.music2,R.raw.onceuponadecember,"En"));
        SList.add(new Media("مرة في ديسمبر", "اغنية مرة في ديسمبر انستازيا", R.drawable.music2, R.raw.anastaziaarbic,"Ar"));
        SList.add(new Media("Sleep lullaby", "lullaby to help your baby sleep", R.drawable.music2, R.raw.lullaby,"En"));
        SList.add(new Media("you are my sunshine", "you are my sunshine lullaby for kids   ", R.drawable.music2, R.raw.youaremysunshine,"En"));
        SList.add(new Media("you are my sunshine ", "only instruments", R.drawable.music2,R.raw.story1,"En"));
        SList.add(new Media(" امي ريمي", "اعمية امي امي ريمي", R.drawable.music2,R.raw.remi,"Ar"));
        SList.add(new Media("rockabye", "rockabye baby lullaby", R.drawable.music2, R.raw.rockabyelullaby,"En"));
        SList.add(new Media("relaxing Lullabies", "lullabies to make your baby relaxed", R.drawable.music2, R.raw.relaxinglullabies,"En"));
        SList.add(new Media("اغاني قبل النوم للاطفال", "Description 2", R.drawable.music2, R.raw.arabiclulaby,"Ar"));
        SList.add(new Media("sleep lullaby", "Description 2", R.drawable.music2, R.raw.songofstorm,""));
        SList.add(new Media("songofstorm", "sleep lullaby for baby", R.drawable.music2,R.raw.songofstorm,"En"));
        SList.add(new Media("twinlkl twinkle", "twinkle twinkle little star lullaby", R.drawable.music2,R.raw.twinkletwinklelittlestar,"En"));
        SList.add(new Media("white noise music box", "white noise music box for sleep  ", R.drawable.music2, R.raw.whitnoisemusicbox,"En"));

        adaptor.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public class CustomAdaptor extends ArrayAdapter<Media> {
        private Context context;
        private ArrayList<Media> SList;

        CustomAdaptor(Context context, ArrayList<Media> SList) {
            super(context, R.layout.row, SList);
            this.context = context;
            this.SList = SList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.rowstories, parent, false);

            TextView Title = row.findViewById(R.id.RowAudioTitle);
            TextView Description = row.findViewById(R.id.RowAirYear);
            ImageView Poster = row.findViewById(R.id.audioImage);
            ImageButton playButton = row.findViewById(R.id.AudioButton);
            playButton.setImageResource(android.R.drawable.ic_media_play);
            Button language = row.findViewById(R.id.languageButton);
            language.setText(SList.get(position).getLanguage());

            Media show = SList.get(position);

            Title.setText(show.getTitle());
            Description.setText(show.getDescription());
            Glide.with(context).load(show.getPoster()).into(Poster);

            playButton.setOnClickListener(v -> {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    mediaPlayer = MediaPlayer.create(context, show.getAudioUrl());
                    mediaPlayer.setOnCompletionListener(mp -> {
                        playButton.setImageResource(android.R.drawable.ic_media_play);
                        mediaPlayer.release();
                        mediaPlayer = null;
                    });
                    mediaPlayer.start();
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            });

            return row;
        }
    }
    public class Media {
        private String title;
        private String description;
        private int poster;
        private int audioUrl;
        private String language;

        public Media(String title, String description, int poster, int audioUrl, String language) {
            this.title = title;
            this.description = description;
            this.poster = poster;
            this.audioUrl = audioUrl;
            this.language = language;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getPoster() {
            return poster;
        }

        public int getAudioUrl() {
            return audioUrl;
        }

        public String getLanguage() {
            return language;
        }
    }

}
