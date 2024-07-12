package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class solidFood extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solid_food);

        WebView video1 = findViewById(R.id.video1);
        WebView video2 = findViewById(R.id.video2);
        WebView video3 = findViewById(R.id.video3);

        WebChromeClient webChromeClient = new WebChromeClient();

        loadYouTubeVideo(video1, "hhttps://www.youtube.com/embed/LAfn4s8Jcps?si=oa_ULJrzyINYu1OJ");
        loadYouTubeVideo(video2, "https://www.youtube.com/embed/l-n-dvAfpDQ?si=kR-kolJCRd_A2V9W");
        loadYouTubeVideo(video3, "https://www.youtube.com/embed/c-RtcxjcsGo?si=6X9dJ2dsE3kbXoQi" );
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
