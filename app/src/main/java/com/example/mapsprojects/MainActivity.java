package com.example.mapsprojects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsprojects.Model.User;
import com.example.mapsprojects.Model.locationModel;
import com.example.mapsprojects.View.HistoryActivity;
import com.example.mapsprojects.View.LoginActivity;
import com.example.mapsprojects.ViewModel.APIService;
import com.example.mapsprojects.ViewModel.GetLocationService;
import com.example.mapsprojects.ViewModel.LocationDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapCamera;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.AvoidanceOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.OptimizationMode;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RouteOptions;
import com.here.sdk.routing.RouteTextOptions;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;
import com.here.sdk.search.Place;
import com.here.sdk.search.SearchCallback;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchError;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.TextQuery;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    LocationBroadcastReceiver receiver;
    private MapView mapView;
    private Button btnSearch , btnHistory;
    private EditText edAddress;
    private SearchEngine searchEngine;
    FusedLocationProviderClient fusedLocationProviderClient;
    private RoutingEngine routingEngine; // công cụ định tuyến
    private List<Waypoint> waypoints = new ArrayList<>(); // Danh sách các điểm tham chiếu
    private List<MapMarker> waypointMarkers = new ArrayList<>(); //
    private MapPolyline routePolyline;
    LocationIndicator locationIndicator = new LocationIndicator();
    private android.location.Location location;
    private MapCamera.OrientationUpdate cameraOrientation ;
    private double bearingInDegrees ;
    private double tilInDegrees ;
    private GeoCoordinates cameraCoordinates ;
    private double distanceInMeters;
    private int count = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch = findViewById(R.id.btnSearch);
        mapView = findViewById(R.id.viewMap);
        btnSearch = findViewById(R.id.btnSearch);
        edAddress = findViewById(R.id.edSearch);
        btnHistory = findViewById(R.id.btnHistory);

        mapView.onCreate(savedInstanceState); // Phai co create neu khong bi loi

        receiver = new LocationBroadcastReceiver();
        // Yêu cầu bật Vị trí
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!locationManager.isLocationEnabled()) {
                buildAlertMessageNoLocation();
            }
        }
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            e.printStackTrace();
        }
        try {
            searchEngine = new SearchEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
        }
        event();

    }

    private void event()
    {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edAddress.getText().toString();
                if (!address.equals(""))
                {
                    searchLocation(v, address);
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

    // Hàm gọi Service
    void startLocService() {
        IntentFilter filter = new IntentFilter("ACT_LOC");
        // Đăng ký BR
        registerReceiver(receiver, filter);
//        Toast.makeText(this, "registerReceiver success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, GetLocationService.class);
        startService(intent);
    }

    // Get Location
    private void getLocation() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Location> task) {
                    Location mlocation = task.getResult();
                    location = mlocation;
                    loadMap();
                    Log.e("Log", "Location : Latitude  " + location.getLatitude() + " | Longitude : " + location.getLongitude());
                }
            });
    }

    // Hàm tạo ra một BR
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ACT_LOC")) {
//                double lat = intent.getDoubleExtra("latitude", 0f);
//                double longitude = intent.getDoubleExtra("longitude", 0f);
                Location mlocation = intent.getParcelableExtra("lastLocation");
                location = mlocation;
//                tv_test_location.setText("Vị trí: " + location.getLatitude() + "--" + location.getLongitude());
                GeoCoordinates geoCoordinates = new GeoCoordinates(location.getLatitude() , location.getLongitude());
                //   loadMap();
                MapImage mapImage = MapImageFactory.fromResource(getApplicationContext().getResources(), R.drawable.location);
                Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);
              //  mapView.getMapScene().addMapMarker(mapMarker);
                waypointMarkers.add(mapMarker);
                waypoints.add(new Waypoint(geoCoordinates));
                mapView.removeLifecycleListener(locationIndicator);
                loadMap();
                final String[] userName = {""};
                final int[] id = {0};
                // Get data từ Server
                APIService.apiService.getData().enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        List<User> api_user = response.body();
                        if (api_user != null){
                            for (int i = 0; i<api_user.size(); i++){
                                id[0] = api_user.get(i).getId();
                                userName[0] = api_user.get(i).getUserName();
                            }
                        }
                        count ++ ;
                        if (count == 2)
                        {
                            String currentLocation = mlocation.getLatitude() + "," + mlocation.getLongitude();
                            // Update Current Location on Server
                            APIService.apiService.updateCurrentLocation(id[0], currentLocation).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.e("TAG5", "Success" + response.body());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("TAG5", "Failed" + t.getMessage());
                                }
                            });
                            addLocationRoom(currentLocation, userName[0]);
                            count = 0;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Log.e("TAG5", "Failed" + t.getMessage());
                    }
                });
                Toast.makeText(context, "Vị trí: " + location.getLatitude() + "--" + location.getLongitude(), Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void loadMap()
    {
     //   GeoCoordinates geoCoordinates = new GeoCoordinates(10.46171 , 105.64354);
        GeoCoordinates geoCoordinates;
        try {
            bearingInDegrees = -360 ;
            tilInDegrees = 0 ;
            distanceInMeters = 1000;
            cameraOrientation = new MapCamera.OrientationUpdate(bearingInDegrees, tilInDegrees);
            cameraCoordinates = new GeoCoordinates(location.getLatitude() , location.getLongitude());
            mapView.getCamera().lookAt(cameraCoordinates, cameraOrientation, distanceInMeters);
             geoCoordinates = new GeoCoordinates(location.getLatitude() , location.getLongitude());
            mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
                @Override
                public void onLoadScene(@Nullable MapError mapError) {
                    if (mapError == null) {
                        double distanceInMeters = 1000;
                        mapView.getCamera().lookAt(
                                //        new GeoCoordinates(10.46384,  105.6441), distanceInMeters);
                                geoCoordinates,distanceInMeters);
                        addLocationIndicator(geoCoordinates,LocationIndicator.IndicatorStyle.NAVIGATION);
                    } else {
                        Log.d("Log", "Loading map failed: mapError: " + mapError.name());
                    }
                }
            });
        }catch (Exception e)
        {
            getLocation();
        }
    }
    private void searchLocation(View view , String address)
    {
        int maxItems = 30;
        SearchOptions searchOptions = new SearchOptions(LanguageCode.VI_VN, maxItems);
        TextQuery query = new TextQuery(address, getScreenCenter());
        searchEngine.search(query, searchOptions, new SearchCallback() {
            @Override
            public void onSearchCompleted(@Nullable SearchError searchError, @Nullable List<Place> list) {
                for (Place result : list)
                {
                    GeoCoordinates geoCoordinates = result.getGeoCoordinates();
                    MapImage mapImage = MapImageFactory.fromResource(getApplicationContext().getResources(), R.drawable.location);
                    Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                    MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);
                    mapView.getMapScene().addMapMarker(mapMarker);
                    waypointMarkers.add(mapMarker);
                    waypoints.add(new Waypoint(geoCoordinates));
                    calculateRoute();
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                    textView.setText(result.getTitle());
                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                    linearLayout.setBackgroundResource(R.color.blue);
                    linearLayout.setPadding(10,10,10,10);
                    linearLayout.addView(textView);
                    mapView.pinView(linearLayout, result.getGeoCoordinates());

                }
            }
        });
    }
    // Tính quản dường
    public void calculateRoute()
    {
        Log.e("Log", "VAO : " + waypointMarkers.size() + " | " + waypoints.size());
        RouteOptions routeOptions = new RouteOptions();
        routeOptions.alternatives = 3 ;
        routeOptions.optimizationMode = OptimizationMode.FASTEST;
        CarOptions options = new CarOptions(routeOptions, new RouteTextOptions(), new AvoidanceOptions());
        routingEngine.calculateRoute(
                waypoints,
                options,
                new CalculateRouteCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable  List<Route> list) {
                        if (routingError == null)
                        {
                            Route route = list.get(0);
                            Log.e("Log", "VAO on RouteCalculated : " + list);
                            drawRoute(route);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error Route !!!" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );

    }
    private GeoCoordinates getScreenCenter()
    {
        int screenWidthInPixel = mapView.getWidth();
        int screenHeightInPixel = mapView.getHeight();
        Point2D point = new Point2D(screenWidthInPixel * 0.5 , screenHeightInPixel * 0.5);
        return mapView.viewToGeoCoordinates(point);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawRoute(Route route) {
        GeoPolyline routeGeoPolyline ;
        Log.e("Log", "VAO DRAWROUTE");
        try {
            routeGeoPolyline = new GeoPolyline(route.getPolyline());
        } catch (InstantiationErrorException e) {
            return;
        }
        Color fillColor = Color.valueOf(0, 0.56f, 0.54f, 0.63f);
        routePolyline = new MapPolyline(routeGeoPolyline , 20 ,fillColor);
        mapView.getMapScene().addMapPolyline(routePolyline);
        Toast.makeText(getApplicationContext(), "Your destination is " + route.getLengthInMeters() + " meters away !!! ", Toast.LENGTH_LONG).show();
    }

    // Hàm cho nút Log out
    public void onLogoutClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checked",false);
        editor.commit();
        startActivity(new Intent(this, LoginActivity.class));
    }
    private void addLocationIndicator(GeoCoordinates geoCoordinates,
                                      LocationIndicator.IndicatorStyle indicatorStyle) {

        locationIndicator.setLocationIndicatorStyle(indicatorStyle);
        com.here.sdk.core.Location location = new com.here.sdk.core.Location.Builder()
                .setCoordinates(geoCoordinates)
                .setTimestamp(new Date())
                .build();
        locationIndicator.updateLocation(location);
        // A LocationIndicator listens to the lifecycle of the map view,
        // therefore, for example, it will get destroyed when the map view gets destroyed.
        mapView.addLifecycleListener(locationIndicator);
    }

    private void addLocationRoom(String currentLocation, String userName) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(currentLocation)) {
            return;
        }
        locationModel model = new locationModel(currentLocation, date);
        LocationDatabase.getInstance(this).locationDAO().insertUser(model);
        //Toast.makeText(this, "Add Location successfully", Toast.LENGTH_SHORT).show();
        // Create new Location in table Route on Server
        APIService.apiService.postLocation(userName, currentLocation, date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAG5", "Success" + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG5", "Failed" + t.getMessage());
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