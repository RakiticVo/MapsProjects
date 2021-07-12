package com.example.mapsprojects.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mapsprojects.database.LocationDatabase;
import com.example.mapsprojects.model.locationModel;

import java.util.List;


public class LocationViewModel extends AndroidViewModel {
    private static final String TAG = "Log";
    private LocationDatabase database ;
    private LiveData<List<locationModel>> listLocation = null;
    public LocationViewModel(@NonNull  Application application)
    {
        super(application);
        database = LocationDatabase.getInstance(application);
    }
    public void insertLocation (locationModel location)
    {
        database.locationDAO().insertLocation(location);
        Log.e(TAG, "ADD SUCCESS");
    }
    public LiveData<List<locationModel>> getListLocationViewModel()
    {
        return (LiveData<List<locationModel>>) database.locationDAO().getListLocation();
    }

}
