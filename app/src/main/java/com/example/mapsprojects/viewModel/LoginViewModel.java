package com.example.mapsprojects.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsprojects.model.User;
import com.example.mapsprojects.retrofit.APIService;
import com.example.mapsprojects.retrofit.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    // đây là dữ liệu mà sẽ tìm nạp không đồng bộ
    private MutableLiveData<List<User>> userList = null;

    // lấy dử liệu
    public LiveData<List<User>> getUsers(){
        if (userList == null){
            userList = new MutableLiveData<List<User>>();
            // tải list user không đồng bộ từ máy chủ trong phương thức này
            APIService service = APIUtils.connectRetrofit();
            service.getData().enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    userList.setValue(response.body());
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
        }
        return  userList;
    }

}
