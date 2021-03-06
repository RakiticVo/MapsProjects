package com.example.mapsprojects.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapsprojects.R;
import com.example.mapsprojects.reponse.UserReponse;
import com.example.mapsprojects.viewModel.APIRetrofitViewModel;
import com.example.mapsprojects.viewModel.ServiceViewModel;
import com.example.mapsprojects.viewModel.UserViewModel;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    EditText edt_username, edt_password;
    CheckBox cb_remember;
    SharedPreferences sharedPreferences;
    private APIRetrofitViewModel apiRetrofitViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermision();
        declareView();
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        apiRetrofitViewModel = ViewModelProviders.of(this).get(APIRetrofitViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        // lấy giá trị sharedPreferences và tự động đăng nhập
        if (sharedPreferences.getBoolean("checked", false) == true){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        edt_username.setText(sharedPreferences.getString("username", ""));
        edt_password.setText(sharedPreferences.getString("password", ""));
        cb_remember.setChecked(sharedPreferences.getBoolean("checked", false));
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    void declareView(){
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        cb_remember = findViewById(R.id.cb_remember);
    }

    public void onLoginClick(View view) {
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();
        Boolean cb_checked = cb_remember.isChecked();
        // Get data từ Server
//        Log.e("TAG6", "onLoginClick: " + loginViewModel.getUsers().toString());
        apiRetrofitViewModel.getUsers().observe(LoginActivity.this, new Observer<List<UserReponse>>() {
            @Override
            public void onChanged(List<UserReponse> users) {
                if (users != null){
                    userViewModel.checkUser(users, username, password, LoginActivity.this, cb_checked);
//                    Log.e("TAG6", "onChanged: " + users.size());
//                    for (int i=0 ; i<users.size(); i++) {
//                        if (username.equals(users.get(i).getUserName())) {
//                            Toast.makeText(getApplicationContext(), "Log in success", Toast.LENGTH_SHORT).show();
//                            if (cb_remember.isChecked()){
//                                // Nếu có check thì lưu tài khoản và mật khẩu
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("username", username);
//                                editor.putString("password", password);
//                                editor.putBoolean("checked", true);
//                                editor.commit();
//                            }
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                            finish();
//                        }else {
//                            Toast.makeText(getApplicationContext(), "Log in fail", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                }else {
                    Log.e("TAG6", "Failed: " + users.size());
                }
            }
        });
//        APIService service = APIUtils.connectRetrofit();
//        service.getData().enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                List<User> api_user = response.body();
//                if (api_user != null) {
//                    for (int i = 0; i < api_user.size(); i++) {
//                        if (username.equals(api_user.get(i).getUserName())){
//                            Toast.makeText(getApplicationContext(), "Log in success", Toast.LENGTH_SHORT).show();
//                            if (cb_remember.isChecked()){
//                                // Nếu có check thì lưu tài khoản và mật khẩu
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("username", username);
//                                editor.putString("password", password);
//                                editor.putBoolean("checked", true);
//                                editor.commit();
//                            }
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                            finish();
//                        }else Toast.makeText(getApplicationContext(), "Log in fail", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                Log.e("TAG5", "Failed" + t.getMessage());
//            }
//        });
    }

    public void onCancelClick(View view) {
        edt_password.setText("");
        edt_username.setText("");
    }

    public void onExitClick(View view) {
        finish();
    }
}