package com.example.mapsprojects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsprojects.View.LoginActivity;
import com.example.mapsprojects.ViewModel.GetLocationService;

public class MainActivity extends AppCompatActivity {

    LocationBroadcastReceiver receiver;

    TextView tv_test_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declare();
        receiver = new LocationBroadcastReceiver();

        // Yêu cầu bật Vị trí
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!locationManager.isLocationEnabled()) {
                buildAlertMessageNoLocation();
            }
        }
//        else {
//            startLocService();
//        }
    }

    // Hàm chuyển đến cài đặt Vị trí
    private void buildAlertMessageNoLocation() {
        new AlertDialog.Builder(this)
                .setMessage("Your Location seems to be disabled, do you want to enable it?")
                .setPositiveButton("Settings", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Hàm khai báo
    void declare(){
        tv_test_location = findViewById(R.id.tv_test_location);
    }

    // Hàm gọi Service
    void startLocService() {
        IntentFilter filter = new IntentFilter("ACT_LOC");
        // Đăng ký BR
        registerReceiver(receiver, filter);
//        Toast.makeText(this, "registerReceiver success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, GetLocationService.class);
        startService(intent);
    }

    // Hàm tạo ra một BR
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ACT_LOC")) {
//                double lat = intent.getDoubleExtra("latitude", 0f);
//                double longitude = intent.getDoubleExtra("longitude", 0f);
                Location location = intent.getParcelableExtra("lastLocation");
                tv_test_location.setText("Vị trí: " + location.getLatitude() + "--" + location.getLongitude());
                Toast.makeText(context, "Vị trí: " + location.getLatitude() + "--" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hàm cho nút Log out
    public void onLogoutClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checked",false);
        editor.commit();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(MainActivity.this, GetLocationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, GetLocationService.class));
    }
}