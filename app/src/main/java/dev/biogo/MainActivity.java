package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, SensorEventListener, OnMapReadyCallback {
    private static final String TAG = "MainActivity";


    private SensorManager sensorManager;
    private Sensor accelerometer;

    BottomNavigationView bottomNavigationView;


    //Map related vars
    private GoogleMap map;
    private SupportMapFragment supportMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted;



    private Location lastLocationloc;
    private LatLng lastLocation;

    private Location currentLocationLoc;

    private LatLng currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(this);

        Toolbar topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
        if (topToolbar != null) {
            topToolbar.setNavigationOnClickListener((view) -> {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();


        //MAP INSTANCE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    lastLocationloc = currentLocationLoc;
                    currentLocationLoc = location;

                    lastLocation = new LatLng(lastLocationloc.getLatitude(), lastLocationloc.getLongitude());
                    currentLocation = new LatLng(currentLocationLoc.getLatitude(), currentLocationLoc.getLongitude());

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));

                    checkLocationPermission();
                }
            }
        };


        supportMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_main);
        if (supportMap != null) {
            supportMap.getMapAsync(this);
            supportMap.getView().setVisibility(View.GONE);
        }

        getLocation();
        startLocationUpdateFused();
    }

    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "onNavigationItemSelected: ");

        switch (item.getItemId()){
            case R.id.home:
                supportMap.getView().setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.explore:
                supportMap.getView().setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();
                return true;
            case R.id.profile:
                supportMap.getView().setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                return true;
            case R.id.googleMap:
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
                supportMap.getView().setVisibility(View.VISIBLE);
                return true;


        }
        return false;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //alignment on axis
            float absX = Math.abs(x);
            float absY = Math.abs(y);
            float absZ = Math.abs(z);

            if ((absX > 9) && (absX < 11) && (absY > 0) && (absY < 1) && (absZ > 0) && (absZ < 1)) {
                Log.d("CameraX", "onSensorChanged: CameraMode");

                //ACTIVATE CAMERA
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //map
        startLocationUpdateFused();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        updateLocation();
        getLocation();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void getLocation() {

        try {
            Task<Location> locationRes = fusedLocationClient.getLastLocation();
            locationRes.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {

                    currentLocationLoc = task.getResult();
                    if (currentLocationLoc != null) {
                        if(map !=null) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocationLoc.getLatitude(), currentLocationLoc.getLongitude()), 18));
                        }
                    }
                } else {
                    Log.d("Failed", "Get location failed");
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception %s", e.getMessage(), e);
        }


    }



    private void updateLocation() {
        checkLocationPermission();
        if (locationPermissionGranted) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }
    /*private void startLocationUpdates() {
        checkLocationPermission();
        if (locationPermissionGranted) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }

    }

    private void setLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }*/

    private void startLocationUpdateFused() {
        checkLocationPermission();
        if (locationPermissionGranted) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }
}