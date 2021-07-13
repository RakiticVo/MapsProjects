package com.example.mapsprojects.viewModel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsprojects.reponse.UserReponse;
import com.example.mapsprojects.retrofit.APIService;
import com.example.mapsprojects.retrofit.APIUtils;
import com.example.mapsprojects.service.GetLocationService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceViewModel extends ViewModel {
    public void startGetLocationService(Activity activity, BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter("ACT_LOC");
        // Đăng ký BR
        activity.registerReceiver(receiver, filter);
//        Toast.makeText(this, "registerReceiver success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, GetLocationService.class);
        activity.startService(intent);
    }
}
