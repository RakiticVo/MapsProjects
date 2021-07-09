package com.example.mapsprojects.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mapsprojects.Model.locationModel;
import com.example.mapsprojects.R;
import com.example.mapsprojects.ViewModel.LocationDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {
    ImageView imgBack ;
    EditText edResult;
    Button btnDate , btnHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        imgBack = findViewById(R.id.imgBack);
        edResult = findViewById(R.id.edKetQua);
        btnDate = findViewById(R.id.btnDate);
        btnHistory = findViewById(R.id.btnSearchHistory);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edResult.getText().toString().trim()))
                {
                    getListLocation(edResult.getText().toString().trim());
                }
                else {
                    Toast.makeText(getApplicationContext(), "PLEASE CHOOSE THE DAY YOU WANT TO SEE HISTORY !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void getListLocation(String day)
    {
        ArrayList<locationModel> model1 = (ArrayList<locationModel>) LocationDatabase.getInstance(getApplicationContext()).locationDAO().getListLocation();
        if (model1.size() > 0 )
        {
            Intent intent = new Intent(HistoryActivity.this , RoutingHistoryActivity.class);
            intent.putExtra("data" , day);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "LỊCH SỬ KHÔNG TỒN TẠI" , Toast.LENGTH_SHORT).show();
        }
    }
    private void displayDatePicker()
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String [] date = df.format(Calendar.getInstance().getTime()).split("/");
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                String day = String.valueOf(dayOfMonth);
                String month = String.valueOf(monthOfYear + 1 );
                if (dayOfMonth < 10)
                {
                    day = "0"+ day;
                }
                if (monthOfYear < 10)
                {
                    month = "0" + month;
                }
                edResult.setText(day + "/" + month+ "/" + year);
            }
        };
        Log.e("Log", date[2] + date[1] + date[0]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, dateSetListener,
                Integer.parseInt(date[2]),  Integer.parseInt(date[1])-1, Integer.parseInt(date[0]));
        datePickerDialog.show();
    }
}