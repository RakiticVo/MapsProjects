package com.example.mapsprojects.retrofit;

public class APIUtils {
    public static final String baseUrl = "http://192.168.1.37/mapsproject/";

    public static APIService connectRetrofit(){
        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }
}
