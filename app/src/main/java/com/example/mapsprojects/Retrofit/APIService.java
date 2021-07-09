package com.example.mapsprojects.Retrofit;

import com.example.mapsprojects.Model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIService {
    // baseUrl chỉ lấy 1 phần domain của API
    String baseUrl = "http://192.168.1.37/mapsproject/";

    APIService apiService = RetrofitClient.getClient(baseUrl).create(APIService.class);

    @GET("getdata.php") // tiếp tục lấy phần còn lại của domain để ghép vs baseUrl
        // để tạo ra một domain hoàn chỉnh
        // http://192.168.1.37/mapsproject/getdata.php
    Call<List<User>> getData();

    @PUT("updatedata.php") // tiếp tục lấy phần còn lại của domain để ghép vs baseUrl
        // để tạo ra một domain hoàn chỉnh
        // http://192.168.1.37/mapsproject/updatedata.php?id=1&currentLocation=465,431
    Call<String> updateCurrentLocation(@Query("id") int id, @Query("currentLocation") String currentLocation);

    @POST("postdata.php")
        // ví dụ: http://192.168.1.37/mapsproject/postdata.php?userName=Google%20Maps&location=465,431&dateSet=08/07/2021
    Call<String> postLocation(@Query("userName") String userName,
                              @Query("location") String location,
                              @Query("dateSet") String dateSet);
}
