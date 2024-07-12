package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class whyBabyNotSleeping extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_why_baby_not_sleeping);

        WebView video1 = findViewById(R.id.video1);
        WebView video2 = findViewById(R.id.video2);
        WebView video3 = findViewById(R.id.video3);

        WebChromeClient webChromeClient = new WebChromeClient();

        loadYouTubeVideo(video1, "https://www.youtube.com/embed/SfCxUG1nE84?si=YQrPY41BaJjVsM4K" );
        loadYouTubeVideo(video2, "https://www.youtube.com/embed/se00vkpziuU?si=SpMBXu-l3fgrkdr2");
        loadYouTubeVideo(video3, "https://www.youtube.com/embed/1DLt2Wbth5E?si=5buoxRoqWQRRuHix" );
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
