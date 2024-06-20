package com.example.baby_cry_identfication;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/")
    Call<String> getHome();
}
