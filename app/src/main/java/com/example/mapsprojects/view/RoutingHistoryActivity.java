package com.example.mapsprojects.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mapsprojects.model.Location_Model;
import com.example.mapsprojects.R;
import com.example.mapsprojects.model.Map_Model;
import com.example.mapsprojects.viewModel.LocationViewModel;
import com.example.mapsprojects.viewModel.MapViewModel;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import java.util.ArrayList;

public class RoutingHistoryActivity extends AppCompatActivity {
    MapView mapView ;
    private MapScene mapScene;
    private MapPolyline mapPolyline ;
    ArrayList<Location_Model> model;
    LocationViewModel locationViewModel ;
    Location_Model Location_Model;
    MapViewModel mapViewModel ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing_history);
        mapView = findViewById(R.id.mapViewHistory);
        mapView.onCreate(savedInstanceState); // Phai co create neu khong bi loi
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        Intent intent = getIntent();
        mapViewModel =  new ViewModelProvider(this).get(MapViewModel.class);
        mapViewModel.init(mapView,getApplicationContext());

        String day = intent.getStringExtra("data");
    //    Log.e("Log", "DAY : " + day);
        model = (ArrayList<Location_Model>) locationViewModel.getListLocationInDay(day);
        try{
            Location_Model = model.get(0);
            Log.e("Log Routing",Location_Model.getLocation());
        }
        catch (Exception e)
        {
        }
        String [] str = Location_Model.getLocation().split(",");
        Log.e("Log Str [] : ", str[1]);
        Double longitude = Double.parseDouble(str[1].trim());
        Double latitude = Double.parseDouble(str[0].trim());
        Toast.makeText(getApplicationContext(), "" + longitude + " | " + latitude , Toast.LENGTH_SHORT).show();
        Map_Model map_model = new Map_Model(latitude,longitude);
        mapViewModel.loadMap(map_model);
     //   Log.e("Log", "" + model.size());
//        loadMap();
        if (model.size() > 0)
        {
            mapScene = mapView.getMapScene();
            mapPolyline = createPolyline(model);
            mapScene.addMapPolyline(mapPolyline);
            Log.e("Log", "VÀO SIZE");
        }

    }
//    private void loadMap()
//    {
//        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
//            @Override
//            public void onLoadScene(@Nullable MapError mapError) {
//                String [] str = Location_Model.getLocation().split(",");
//                if (mapError == null) {
//                    double distanceInMeters = 1000 * 10;
//                    mapView.getCamera().lookAt(
//                            new GeoCoordinates(Double.parseDouble(str[0]), Double.parseDouble(str[1])), distanceInMeters);
//                } else {
//                    Log.d("Log", "Loading map failed: mapError: " + mapError.name());
//                }
//            }
//        });
//    }
    private MapPolyline createPolyline(ArrayList<Location_Model> listLocation ) {
        ArrayList<GeoCoordinates> coordinates = new ArrayList<>();
        for (Location_Model model2 : listLocation)
        {
            String [] str = model2.getLocation().split(",");
            //  Log.e("Log : " , "Location For: " + str[0] + " |  Date : " + str[1]);
            coordinates.add(new GeoCoordinates(Double.parseDouble(str[0]), Double.parseDouble(str[1])));
        }
        GeoPolyline geoPolyline;
        try {
            geoPolyline = new GeoPolyline(coordinates);
        } catch (InstantiationErrorException e) {
            // Thrown when less than two vertices.
            return null;
        }
        float widthInPixels = 20;
        Color lineColor = Color.valueOf(0, 0.56f, 0.54f, 0.63f); // RGBA
        MapPolyline mapPolyline = new MapPolyline(geoPolyline, widthInPixels, lineColor);
        return mapPolyline;
    }

}