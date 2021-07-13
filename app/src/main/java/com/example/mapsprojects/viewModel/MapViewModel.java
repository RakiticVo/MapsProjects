package com.example.mapsprojects.viewModel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;

import com.example.mapsprojects.R;
import com.example.mapsprojects.model.Map_Model;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapViewModel extends ViewModel {
   private MapView mapView ;

   private double bearingInDegrees ;
   private double tilInDegrees ;
   private GeoCoordinates cameraCoordinates ;
   private double distanceInMeters;
   private MapCamera.OrientationUpdate cameraOrientation ;
   private Location location ;
   private Context context ;
   private MapPolyline routePolyline;
   private SearchEngine searchEngine;

   private RoutingEngine routingEngine; // công cụ định tuyến
   private List<Waypoint> waypoints = new ArrayList<>(); // Danh sách các điểm tham chiếu
   private List<MapMarker> waypointMarkers = new ArrayList<>(); //
   LocationIndicator locationIndicator = new LocationIndicator();
   public void init(MapView mapView , Context context){
        this.mapView = mapView ;
        this.context = context;
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
   }
   public void loadMap(Map_Model map_model)
   {
      //   GeoCoordinates geoCoordinates = new GeoCoordinates(10.46171 , 105.64354);
      GeoCoordinates geoCoordinates;
      try {
         bearingInDegrees = -360 ;
         tilInDegrees = 0 ;
         distanceInMeters = 1000;
         cameraOrientation = new MapCamera.OrientationUpdate(bearingInDegrees, tilInDegrees);
         Log.e("LogMap", "Location Map View : " + map_model.getLatitude() + " | Longitude : " + map_model.getLongitude());
         cameraCoordinates = new GeoCoordinates(map_model.getLatitude() , map_model.getLongitude());
          mapView.removeLifecycleListener(locationIndicator);
         mapView.getCamera().lookAt(cameraCoordinates, cameraOrientation, distanceInMeters);
         waypoints.add(new Waypoint(cameraCoordinates));
         geoCoordinates = new GeoCoordinates(map_model.getLatitude() , map_model.getLongitude());
         mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
               if (mapError == null) {
                  double distanceInMeters = 1000;
                  mapView.getCamera().lookAt(
                          geoCoordinates,distanceInMeters);
                  addLocationIndicator(geoCoordinates, LocationIndicator.IndicatorStyle.NAVIGATION);
               } else {
                  Log.d("LogMap", "Loading map failed: mapError: " + mapError.name());
               }
            }
         });

      }catch (Exception e)
      {
        // getLocation();
      }
   }
   // Get Location


   public void searchLocation(View v , String address)
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
                    MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.location);
                    Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                    MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);
                    mapView.getMapScene().addMapMarker(mapMarker);
                    waypointMarkers.add(mapMarker);
                    waypoints.add(new Waypoint(geoCoordinates));
                    calculateRoute();
                    TextView textView = new TextView(context);
                    textView.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                    textView.setText(result.getTitle());
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setBackgroundResource(R.color.blue);
                    linearLayout.setPadding(10,10,10,10);
                    linearLayout.addView(textView);
                    mapView.pinView(linearLayout, result.getGeoCoordinates());
                }
            }
        });
   }
   private GeoCoordinates getScreenCenter()
   {
      int screenWidthInPixel = mapView.getWidth();
      int screenHeightInPixel = mapView.getHeight();
      Point2D point = new Point2D(screenWidthInPixel * 0.5 , screenHeightInPixel * 0.5);
      return mapView.viewToGeoCoordinates(point);
   }

   private void addLocationIndicator(GeoCoordinates geoCoordinates,
                                     LocationIndicator.IndicatorStyle indicatorStyle) {

      locationIndicator.setLocationIndicatorStyle(indicatorStyle);
      com.here.sdk.core.Location location = new com.here.sdk.core.Location.Builder()
              .setCoordinates(geoCoordinates)
              .setTimestamp(new Date())
              .build();
      locationIndicator.updateLocation(location);
      mapView.addLifecycleListener(locationIndicator);
   }
   @RequiresApi(api = Build.VERSION_CODES.O)
   private void drawRoute(Route route) {
      GeoPolyline routeGeoPolyline ;
      Log.e("LogMap", "VAO DRAWROUTE");
      try {
         routeGeoPolyline = new GeoPolyline(route.getPolyline());
      } catch (InstantiationErrorException e) {
         return;
      }
      Color fillColor = Color.valueOf(0, 0.56f, 0.54f, 0.63f);
      routePolyline = new MapPolyline(routeGeoPolyline , 20 ,fillColor);
      mapView.getMapScene().addMapPolyline(routePolyline);
      Toast.makeText(context, "Your destination is " + route.getLengthInMeters() + " meters away !!! ", Toast.LENGTH_LONG).show();
   }
   public void calculateRoute()
   {
       Log.e("LogMap", "VAO CalculateRoute: " + waypointMarkers.size() + " | " + waypoints.size());
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
                 public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> list) {
                    if (routingError == null)
                    {
                       Route route = list.get(0);
                       Log.e("Log", "VAO on RouteCalculated : " + list);
                       drawRoute(route);
                    }
                    else {
                       Toast.makeText(context.getApplicationContext(), "Error Route !!!" , Toast.LENGTH_SHORT).show();
                    }
                 }
              }
      );

   }
}
