package com.example.mapsprojects.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mapsprojects.Model.locationModel;
import com.example.mapsprojects.R;
import com.example.mapsprojects.ViewModel.LocationDatabase;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing_history);
        mapView = findViewById(R.id.mapViewHistory);
        mapView.onCreate(savedInstanceState); // Phai co create neu khong bi loi
        loadMap();
        Intent intent = getIntent();
        String day = intent.getStringExtra("data");
        Log.e("Log", "DAY : " + day);
        ArrayList<locationModel> model1 = (ArrayList<locationModel>) LocationDatabase.getInstance(getApplicationContext()).locationDAO().getLocationInDay(day);
        Log.e("Log", "" + model1.size());
        if (model1.size() > 0)
        {
            mapScene = mapView.getMapScene();
            mapPolyline = createPolyline(model1);
            mapScene.addMapPolyline(mapPolyline);
            Log.e("Log", "VÀO SIZE");
        }

    }
    private void loadMap()
    {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    double distanceInMeters = 1000 * 10;
                    mapView.getCamera().lookAt(
                            new GeoCoordinates(10.462139, 105.643278), distanceInMeters);
                } else {
                    Log.d("Log", "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }
    private MapPolyline createPolyline(ArrayList<locationModel> listLocation ) {
        ArrayList<GeoCoordinates> coordinates = new ArrayList<>();
        for (locationModel model2 : listLocation)
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