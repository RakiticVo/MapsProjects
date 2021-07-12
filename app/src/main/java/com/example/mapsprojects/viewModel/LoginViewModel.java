package com.example.mapsprojects.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsprojects.reponse.UserReponse;
import com.example.mapsprojects.retrofit.APIService;
import com.example.mapsprojects.retrofit.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    // đây là dữ liệu mà sẽ tìm nạp không đồng bộ
    private MutableLiveData<List<UserReponse>> userList = null;

    // lấy dử liệu
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

}
