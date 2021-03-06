package com.example.mapsprojects.reponse;

public class UserReponse {
    private int id;
    private String userName, currentLocation;

    public UserReponse(int id, String userName, String currentLocation) {
        this.id = id;
        this.userName = userName;
        this.currentLocation = currentLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}
