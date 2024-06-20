package com.example.baby_cry_identfication;

public class Activities {
    private String title;
    private String description;
    private String language;
    private int poster;
    private Class<?> targetActivity;

    public Activities(String title, String description, int poster, String language, Class<?> targetActivity) {
        this.title = title;
        this.description = description;
        this.poster = poster;
        this.language = language;
        this.targetActivity = targetActivity;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class<?> targetActivity) {
        this.targetActivity = targetActivity;
    }
}
