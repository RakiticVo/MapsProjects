package com.example.mapsprojects.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Location")
public class Location_Model {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String location;
    private String date ;
    public Location_Model(String location, String date) {
        this.location = location;
        this.date = date ;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


}
