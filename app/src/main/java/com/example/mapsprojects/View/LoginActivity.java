package com.example.mapsprojects.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapsprojects.MainActivity;
import com.example.mapsprojects.R;

public class LoginActivity extends AppCompatActivity {

    EditText edt_username, edt_password;
    CheckBox cb_remember;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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