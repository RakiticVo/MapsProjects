package com.example.mapsprojects.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapsprojects.MainActivity;
import com.example.mapsprojects.R;

public class LoginActivity extends AppCompatActivity {

    EditText edt_username, edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        declareView();
    }

    void declareView(){
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        edt_username.setText("Google Maps");
        edt_password.setText("123");
    }

    public void onLoginClick(View view) {
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();
        if (username.equals("Google Maps") && password.equals("123")){
            Toast.makeText(this, "Log in success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }else Toast.makeText(this, "Log in fail", Toast.LENGTH_SHORT).show();
    }

    public void onCancelClick(View view) {
        edt_password.setText("");
        edt_username.setText("");
    }
}