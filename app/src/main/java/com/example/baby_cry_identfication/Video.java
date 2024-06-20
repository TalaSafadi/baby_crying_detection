package com.example.baby_cry_identfication;

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
