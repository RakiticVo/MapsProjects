package com.example.mapsprojects.ViewModel;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class GetLocationService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    public GetLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Khi được Service được gọi sẽ lấy vị trí hiện tại của người dùng và gửi nó đến MainActivity thông qua BR
                Log.d("mylog", "Lat is: " + locationResult.getLastLocation().getLatitude() + ", "
                        + "Lng is: " + locationResult.getLastLocation().getLongitude());
                Intent intent = new Intent("ACT_LOC");
//                intent.putExtra("latitude", locationResult.getLastLocation().getLatitude());
//                intent.putExtra("longitude", locationResult.getLastLocation().getLongitude());
                intent.putExtra("lastLocation", locationResult.getLastLocation());
                sendBroadcast(intent);
            }
        };
    }

    // Hàm được dùng để có thể được khởi động lại nếu Service bị kill
    // Hàm này được goi khi có một thành phần khác gọi đên Service bằng lệnh startService().
    // Khi phương thức này được thực hiện, dịch vụ được khởi động và có thể chạy trong background vô thời hạn.
    // Khi công việc hoàn thành bạn nên stop bằng cách gọi stopService() từ một thành phần khác,
    // hoặc cho chính Service gọi stopSelf()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    // Hàm update vị trí liên tục
    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}