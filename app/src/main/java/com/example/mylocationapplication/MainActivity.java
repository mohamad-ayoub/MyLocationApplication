package com.example.mylocationapplication;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int ASK_FOR_PERMISSION_REQUEST_CODE = 111;
    TextView tvLastUpdate, tvLat, tvLon, tvAlt;
    MapView mapView;
    GoogleMap googleMap = null;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean requestingLocationUpdates = true;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        tvLat = findViewById(R.id.tvLatitude);
        tvLon = findViewById(R.id.tvLongituse);
        tvAlt = findViewById(R.id.tvAltitude);
        mapView = findViewById(R.id.mapView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //locationRequest = LocationRequest.create().setInterval(3000).setFastestInterval(1000).setSmallestDisplacement(5).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 10000).setMinUpdateIntervalMillis(2000).setMinUpdateDistanceMeters(1).build();
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    tvLastUpdate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS")));
                    tvLat.setText(location.getLatitude() + "");
                    tvLon.setText(location.getLongitude() + "");
                    tvAlt.setText(location.getAltitude() + "");

                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    /*if (googleMap != null) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(currentLatLng)
                                .title("Marker"));
                    }

                     */
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f));
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f));
                    Location sh = new Location("");
                    sh.setLatitude(32.80587810229593);
                    sh.setLongitude(35.16976778088804);
                    Log.d("TAG", "onLocationResult: "+location.distanceTo(sh)/1000);


                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, ASK_FOR_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();

                    if (location != null) {
                        tvLat.setText(location.getLatitude() + "");
                        tvLon.setText(location.getLongitude() + "");
                        tvAlt.setText(location.getAltitude() + "");
                    }
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ASK_FOR_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Denided", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            updateUi();
        }
    }

    private void updateUi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();

                    if (location != null) {
                        tvLat.setText(location.getLatitude() + "");
                        tvLon.setText(location.getLongitude() + "");
                        tvAlt.setText(location.getAltitude() + "");
                    }
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallBack,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(32.80587810229593, 35.16976778088804))
                .title("Marker");
        Marker shfaram = googleMap.addMarker(marker);
        //32.80587810229593, 35.16976778088804
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.80587810229593, 35.16976778088804), 15.0f));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.equals(shfaram)) {
                    marker.remove();
                }
                return false;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Marker"));

                googleMap.addCircle(new CircleOptions().center(latLng).radius(50).strokeColor(Color.YELLOW).fillColor(Color.GREEN));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}