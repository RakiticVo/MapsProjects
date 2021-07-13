package com.example.mapsprojects.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mapsprojects.database.LocationDatabase;
import com.example.mapsprojects.model.Location_Model;

import java.util.List;


public class LocationViewModel extends AndroidViewModel {
    private static final String TAG = "Log";
    private LocationDatabase database ;
    private LiveData<List<Location_Model>> listLocation = null;
    public LocationViewModel(@NonNull  Application application)
    {
        super(application);
        database = LocationDatabase.getInstance(application);
    }
    public void insertLocation (Location_Model location)
    {
        database.locationDAO().insertLocation(location);
        Log.e(TAG, "ADD SUCCESS");
    }
    public List<Location_Model> getListLocationViewModel()
    {
        return  database.locationDAO().getListLocation();
    }
    public List<Location_Model> getListLocationInDay(String day)
    {
        return database.locationDAO().getLocationInDay(day);
    }

}
