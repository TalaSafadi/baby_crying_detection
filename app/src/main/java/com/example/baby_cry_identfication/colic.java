package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class colic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colic);

        WebView video1 = findViewById(R.id.video1);
        WebView video2 = findViewById(R.id.video2);
        WebView video3 = findViewById(R.id.video3);

        WebChromeClient webChromeClient = new WebChromeClient();

        loadYouTubeVideo(video1, "https://www.youtube.com/embed/V3gtc8VWjeM?si=k4CHooDieUfeRi6c");
        loadYouTubeVideo(video2, "https://www.youtube.com/embed/9PhWkpEp8cY?si=kYbg_HNe5CM3Yl4J");
        loadYouTubeVideo(video3, "https://www.youtube.com/embed/fmDtpejQFDs?si=ywN0uC1l7TPHxQi5" );
    }

    private void loadYouTubeVideo(WebView videoWebView, String videoUrl) {
        String videoHtml = "<iframe width=\"100%\" height=\"100%\" src=\"" + videoUrl + "\" title=\"YouTube video player\" frameborder=\"0\"" +
                " allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"" +
                " referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";

        videoWebView.getSettings().setJavaScriptEnabled(true);
        videoWebView.getSettings().setDomStorageEnabled(true);
        videoWebView.setWebChromeClient(new WebChromeClient());
        videoWebView.loadData(videoHtml, "text/html", "utf-8");
    }
}
