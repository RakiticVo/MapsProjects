package com.example.mapsprojects.viewModel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsprojects.reponse.UserReponse;
import com.example.mapsprojects.retrofit.APIService;
import com.example.mapsprojects.retrofit.APIUtils;
import com.example.mapsprojects.service.GetLocationService;
import com.example.mapsprojects.view.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private MutableLiveData<String> stringResultUpdate = null;
    private MutableLiveData<String> stringResultPost = null;
    private MutableLiveData<List<UserReponse>> userList = null;
    private MutableLiveData<Location> locationResult = null;
    private IntentFilter intentFilter;

    // lấy dữ liệu
    public LiveData<List<UserReponse>> getUsers(){
        if (userList == null){
            userList = new MutableLiveData<List<UserReponse>>();
            // tải list user không đồng bộ từ máy chủ trong phương thức này
            APIService service = APIUtils.connectRetrofit();
            service.getData().enqueue(new Callback<List<UserReponse>>() {
                @Override
                public void onResponse(Call<List<UserReponse>> call, Response<List<UserReponse>> response) {
                    userList.setValue(response.body());
                }
                @Override
                public void onFailure(Call<List<UserReponse>> call, Throwable t) {

                }
            });
        }
        return  userList;
    }

    // Cập nhật dữ liệu trên Server
    public LiveData<String> getResultUpdate(int id, String currentLocation){
        Log.e("TAG10", "Update success \n" + "id: " + id + "\nlocation: " + currentLocation);
        stringResultUpdate = new MutableLiveData<String>();
        // tải list user không đồng bộ từ máy chủ trong phương thức này
        APIService service = APIUtils.connectRetrofit();
        service.updateCurrentLocation(id, currentLocation).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                stringResultUpdate.setValue(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                stringResultUpdate.setValue(t.getMessage());
            }
        });
        return stringResultUpdate;
    }

    // Thêm lịch sử vị trí trên Server
    public LiveData<String> getResultPost(String userName, String location, String dateSet){
        stringResultPost = new MutableLiveData<String>();
        // tải list user không đồng bộ từ máy chủ trong phương thức này
        APIService service = APIUtils.connectRetrofit();
        service.postLocation(userName, location, dateSet).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                stringResultPost.setValue(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                stringResultPost.setValue(t.getMessage());
            }
        });
        return  stringResultPost;
    }

    public void startGetLocationService(Activity activity, BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter("ACT_LOC");
        // Đăng ký BR
        activity.registerReceiver(receiver, filter);
//        Toast.makeText(this, "registerReceiver success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, GetLocationService.class);
        activity.startService(intent);
    }
}
