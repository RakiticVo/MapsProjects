package com.example.mapsprojects.viewModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.mapsprojects.reponse.UserReponse;
import com.example.mapsprojects.view.MainActivity;

import java.util.List;

public class UserViewModel extends ViewModel {
    private SharedPreferences sharedPreferences;
    public void checkUser(List<UserReponse> users, String username, String password, Activity activity, Boolean cb_checked){
        sharedPreferences = activity.getSharedPreferences("dataLogin", Context.MODE_PRIVATE);
        for (int i=0 ; i<users.size(); i++) {
            if (username.equals(users.get(i).getUserName())) {
                Toast.makeText(activity, "Log in success", Toast.LENGTH_SHORT).show();
                if (cb_checked == true){
                    // Nếu có check thì lưu tài khoản và mật khẩu
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("checked", true);
                    editor.commit();
                }
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }else {
                Toast.makeText(activity, "Log in fail", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
