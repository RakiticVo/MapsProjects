package com.example.mapsprojects.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsprojects.MainActivity;
import com.example.mapsprojects.Model.User;
import com.example.mapsprojects.R;
import com.example.mapsprojects.ViewModel.RetrofitAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    EditText edt_username, edt_password;
    CheckBox cb_remember;
    TextView tv_result;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermision();
        declareView();
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);

        // lấy giá trị sharedPreferences và tự động đăng nhập
        if (sharedPreferences.getBoolean("checked", false) == true){
            startActivity(new Intent(this, MainActivity.class));
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
        if (username.equals("Google Maps") && password.equals("123")){
            Toast.makeText(this, "Log in success", Toast.LENGTH_SHORT).show();
            if (cb_remember.isChecked()){
                // Nếu có check thì lưu tài khoản và mật khẩu
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("checked", true);
                editor.commit();
            }
            startActivity(new Intent(this, MainActivity.class));
        }else Toast.makeText(this, "Log in fail", Toast.LENGTH_SHORT).show();
    }

    public void onCancelClick(View view) {
        edt_password.setText("");
        edt_username.setText("");
    }
}