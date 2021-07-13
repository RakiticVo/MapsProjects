package com.example.mapsprojects.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mapsprojects.model.Location_Model;

import java.util.List;

@Dao
public interface LocationDAO  {

    @Insert
    void insertLocation(Location_Model Location_Model);

    @Query("SELECT * FROM Location")
    List<Location_Model> getListLocation();
    @Query("SELECT * FROM Location WHERE date = :day")
    List<Location_Model> getLocationInDay(String day);





}
