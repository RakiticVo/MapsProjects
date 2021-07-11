package com.example.mapsprojects.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mapsprojects.model.locationModel;

@Database(entities = {locationModel.class} , version = 1)
public abstract class LocationDatabase extends RoomDatabase {
      private static final String DATABASE_NAME = "location.db";
      private static LocationDatabase instances ;
      public static synchronized LocationDatabase getInstance(Context context)
      {
          if (instances == null)
          {
              instances = Room.databaseBuilder(context.getApplicationContext() , LocationDatabase.class , DATABASE_NAME)
                      .allowMainThreadQueries()
                      .build();
          }
          return instances;
      }
      public abstract LocationDAO locationDAO ();

}
