package com.example.mapsprojects.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapsprojects.model.Map_Model;
import com.example.mapsprojects.reponse.UserReponse;
import com.example.mapsprojects.model.Location_Model;
import com.example.mapsprojects.R;
import com.example.mapsprojects.service.GetLocationService;
import com.example.mapsprojects.viewModel.LocationViewModel;
import com.example.mapsprojects.viewModel.APIRetrofitViewModel;
import com.example.mapsprojects.viewModel.MapViewModel;
import com.example.mapsprojects.viewModel.ServiceViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LocationBroadcastReceiver receiver;
    private MapView mapView;
    private Button btnSearch , btnHistory;
    private EditText edAddress;
    MapViewModel mapViewModel ;
    FusedLocationProviderClient fusedLocationProviderClient;
    private android.location.Location location;
    private int count = 0 ;
    private APIRetrofitViewModel apiRetrofitViewModel;
    private ServiceViewModel serviceViewModel;
    private LocationViewModel locationViewModel ;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch = findViewById(R.id.btnSearch);
        mapView = findViewById(R.id.viewMap);
        btnSearch = findViewById(R.id.btnSearch);
        edAddress = findViewById(R.id.edSearch);
        btnHistory = findViewById(R.id.btnHistory);
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        apiRetrofitViewModel = ViewModelProviders.of(this).get(APIRetrofitViewModel.class);
        serviceViewModel = ViewModelProviders.of(this).get(ServiceViewModel.class);
        mapView.onCreate(savedInstanceState); // Phai co create neu khong bi loi
        mapViewModel =  new ViewModelProvider(this).get(MapViewModel.class);
        mapViewModel.init(mapView, getApplicationContext());
        receiver = new LocationBroadcastReceiver();
        serviceViewModel.startGetLocationService(this, receiver);
        // Yêu cầu bật Vị trí
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!locationManager.isLocationEnabled()) {
                buildAlertMessageNoLocation();
            }
        }
        getLocation();

        event();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                Location mlocation = task.getResult();
                location = mlocation;
                mapViewModel.loadMap(new Map_Model(mlocation.getLatitude(), mlocation.getLongitude()));
                Log.e("Log", "Location : Latitude  " + location.getLatitude() + " | Longitude : " + location.getLongitude());
            }
        });
    }
    private void event()
    {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edAddress.getText().toString();
                if (!address.equals(""))
                {
                    mapViewModel.searchLocation(v , address);
                }
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , HistoryActivity.class);
                startActivity(intent);
            }
        });
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

//    // Hàm gọi Service
//    void startLocService() {
//        IntentFilter filter = new IntentFilter("ACT_LOC");
//        // Đăng ký BR
//        registerReceiver(receiver, filter);
////        Toast.makeText(this, "registerReceiver success", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(MainActivity.this, GetLocationService.class);
//        startService(intent);
//    }


    // Hàm tạo ra một BR
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ACT_LOC")) {
                Location mlocation = intent.getParcelableExtra("lastLocation");
                location = mlocation;
                GeoCoordinates geoCoordinates = new GeoCoordinates(location.getLatitude() , location.getLongitude());
                //   loadMap();
                MapImage mapImage = MapImageFactory.fromResource(getApplicationContext().getResources(), R.drawable.location);
                Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);
                mapViewModel.loadMap(new Map_Model(location.getLatitude(), location.getLongitude()));
                // Get data từ Server
                apiRetrofitViewModel.getUsers().observe(MainActivity.this, new Observer<List<UserReponse>>() {
                    @Override
                    public void onChanged(List<UserReponse> users) {
                        if (users != null) {
                            String username = sharedPreferences.getString("username", "1");
                            String currentLocation = mlocation.getLatitude() + "," + mlocation.getLongitude();
                            for (int i = 0; i < users.size(); i++) {
                                if (username.equals(users.get(i).getUserName())) {
                                    int id = users.get(i).getId();
                                    count++;
                                    Log.e("TAG9", "count:" + count);
                                    if (count == 2) {
                                        Log.e("TAG9", "Update success" + "id: " + id + "user: " + username + "location: " + currentLocation);
                                        apiRetrofitViewModel.getResultUpdate(id, currentLocation).observe(MainActivity.this, new Observer<String>() {
                                            @Override
                                            public void onChanged(String s) {
                                                Log.e("TAG9", "onChanged: " + s);
                                            }
                                        });
                                        addLocationServer(currentLocation, username);
                                        count = 0;
                                    }
                                }else{
                                    Log.e("TAG6", "Failed: " + users.size());
                                }
                            }
                        }
                    }
                });
                // Them dữ liệu Vị trí vào Room
                String nlocation =  mlocation.getLatitude() + "," + mlocation.getLongitude();
                addLocationRoom(nlocation);
                Toast.makeText(context, "Vị trí: " + location.getLatitude() + "--" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void addLocationRoom(String currentLocation)
    {
        Log.e("Log", "VAO ");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(currentLocation)) {
            return;
        }
        Location_Model model = new Location_Model(currentLocation, date);
        Log.e("Log", "Location : " + model.getLocation());
        locationViewModel.insertLocation(model);
    }

    // Hàm cho nút Log out
    public void onLogoutClick(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checked",false);
        editor.commit();
        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }


    private void addLocationServer(String currentLocation, String userName) {
        Log.e("Log", "VAO ");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(currentLocation)) {
            return;
        }
        Location_Model model = new Location_Model(currentLocation, date);
        Log.e("Log", "Location : " + model.getLocation());
        locationViewModel.insertLocation(model);
        //Toast.makeText(this, "Add Location successfully", Toast.LENGTH_SHORT).show();
        // Create new Location in table Route on Server
        apiRetrofitViewModel.getResultPost(userName, currentLocation, date).observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("TAG11", "Success: " + s );
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startLocService();
        serviceViewModel.startGetLocationService(this, receiver);
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