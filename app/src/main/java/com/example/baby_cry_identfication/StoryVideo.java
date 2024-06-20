package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StoryVideo extends AppCompatActivity {
    private ListView videoListView;
    private CustomVideoAdapter adapter;
    private ArrayList<Video> videoList = new ArrayList<>();
    private HashMap<Integer, Integer> videoPositions = new HashMap<>(); // Save video positions
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_video);

        videoListView = findViewById(R.id.videoListView);
        adapter = new CustomVideoAdapter(this, videoList);
        videoListView.setAdapter(adapter);

        // Add videos to the list
        videoList.add(new Video("صاحب المطحنه والحمار_ حكايات لا تنسى", "https://www.youtube.com/embed/iHyB39lpkqc?si=h3hC6HAJCdy1OVYV"));
        videoList.add(new Video("فاتنة إبنة الحاكم المغرورة - 10 - في جعبتي حكاية", "https://www.youtube.com/embed/mwYSm8YotN4?si=K18NwMo20ANsw2SH"));
        videoList.add(new Video("الصياد الفاشل والعجوز الشريرة - 11 - في جعبتي حكاية", "https://www.youtube.com/embed/Nmdgx4EGWKE?si=EQN6AVbTEZZLShHP"));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            // Exit fullscreen mode
            customViewCallback.onCustomViewHidden();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save video positions
        for (int i = 0; i < videoList.size(); i++) {
            Video video = videoList.get(i);
            int position = adapter.getVideoPosition(i);
            videoPositions.put(i, position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore video positions
        for (int i = 0; i < videoList.size(); i++) {
            Integer position = videoPositions.get(i);
            if (position != null) {
                adapter.setVideoPosition(i, position);
            }
        }
    }

    public class CustomVideoAdapter extends ArrayAdapter<Video> {
        private Context context;
        private ArrayList<Video> videoList;
        private WebChromeClient webChromeClient;

        public CustomVideoAdapter(Context context, ArrayList<Video> videoList) {
            super(context, R.layout.raw_videos, videoList);
            this.context = context;
            this.videoList = videoList;
            this.webChromeClient = new WebChromeClient() {
                @Override
                public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                    if (customView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }

                    customView = view;
                    customViewCallback = callback;
                    FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                    decor.addView(customView, new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                @Override
                public void onHideCustomView() {
                    FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                    decor.removeView(customView);
                    customView = null;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    customViewCallback.onCustomViewHidden();
                }
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.raw_videos, parent, false);

            TextView titleTextView = rowView.findViewById(R.id.videoTitle);
            WebView videoWebView = rowView.findViewById(R.id.webView);

            Video video = videoList.get(position);

            titleTextView.setText(video.getTitle());
            String videoHtml = "<iframe width=\"100%\" height=\"100%\" src=\"" + video.getVideoUrl() + "\" title=\"YouTube video player\" frameborder=\"0\"" +
                    " allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"" +
                    " referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";

            videoWebView.getSettings().setJavaScriptEnabled(true);
            videoWebView.getSettings().setDomStorageEnabled(true);
            videoWebView.setWebChromeClient(webChromeClient);
            videoWebView.loadData(videoHtml, "text/html", "utf-8");

            return rowView;
        }

        public int getVideoPosition(int position) {
            // Placeholder for getting the video position (not supported by WebView directly)
            return 0;
        }

        public void setVideoPosition(int position, int videoPosition) {
            // Placeholder for setting the video position (not supported by WebView directly)
        }
    }

    public class Video {
        private String title;
        private String videoUrl;

        public Video(String title, String videoUrl) {
            this.title = title;
            this.videoUrl = videoUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getVideoUrl() {
            return videoUrl;
        }
    }
}
