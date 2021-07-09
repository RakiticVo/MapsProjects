package com.example.mapsprojects.ViewModel;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mapsprojects.Model.locationModel;

import java.util.List;

@Dao
public interface LocationDAO  {

    @Insert
    void insertUser(locationModel locationModel);

    @Query("SELECT * FROM Location")
    List<locationModel> getListLocation();
    @Query("SELECT * FROM Location WHERE date = :day")
    List<locationModel> getLocationInDay(String day);





}
