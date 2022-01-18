package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import dev.biogo.Adapters.CustomInfoWindowAdapter;
import dev.biogo.Helpers.DateHelper;
import dev.biogo.Models.Photo;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, SensorEventListener, OnMapReadyCallback {
    private static final String TAG = "MainActivity";

    private DatabaseReference mDataBase;
    private ArrayList<Photo> photosList;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    BottomNavigationView bottomNavigationView;

    private FirebaseUser user;

    //Map related vars
    private GoogleMap map;
    private SupportMapFragment supportMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted;
    private boolean mapFlag = true;


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

        //Firebase Database
        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        //User from firebase
        user = FirebaseAuth.getInstance().getCurrentUser();


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
        Log.i("Click", "onNavigationItemSelected: ");

        switch (item.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                supportMap.getView().setVisibility(View.GONE);
                return true;
            case R.id.explore:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();
                supportMap.getView().setVisibility(View.GONE);
                return true;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                supportMap.getView().setVisibility(View.GONE);
                return true;
            case R.id.googleMap:
                if(mapFlag) {
                    try {
                        createMapMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        //Markers InfoWindow OnClick
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("markeeer", "onInfoWindowClick: " + marker.getTitle());

                //Intent goToPhotoIntent = new Intent(getApplicationContext(), PhotoActivity.class);
                //startActivity(goToPhotoIntent);

                Query imagesQuery = mDataBase.child("images").child(marker.getTitle());


                imagesQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Photo photo = dataSnapshot.getValue(Photo.class);
                        Log.d("markeeer", "onDataChange: " + photo.getCreatedAt() );

                        Intent goToPhotoIntent = new Intent(getApplicationContext(),PhotoActivity.class);
                        goToPhotoIntent.putExtra("photoData", photo);
                        startActivity(goToPhotoIntent);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("onCancelled", "onCancelled: ", error.toException());
                    }
                });
            }
        });


        updateLocation();
        getLocation();

        photosList = new ArrayList<>();

        if(mDataBase != null) {
            //Get photos latlng
            Query imagesQuery = mDataBase.child("images");

            imagesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    photosList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Photo photo = snapshot.getValue(Photo.class);
                        if (photo != null) {
                            Log.d("yooooo", "onMapReady: " + " Photo Added");
                            photo.setId(snapshot.getKey());
                            photosList.add(photo);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("yooooo", "onMapReady: " + "Database error");
                }

            });


        }
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

    private void startLocationUpdateFused() {
        checkLocationPermission();
        if (locationPermissionGranted) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void createMapMarkers() throws IOException {
        //mapFlag = false;

        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.this));

        for(int i=0; i<photosList.size();i++){
            double locationLat = Double.parseDouble(photosList.get(i).getLat());
            double locationLng = Double.parseDouble(photosList.get(i).getLng());
            LatLng location = new LatLng(locationLat, locationLng);

            float color = BitmapDescriptorFactory.HUE_RED;
            if(photosList.get(i).getOwnerId().equals(user.getUid())){
                color = BitmapDescriptorFactory.HUE_AZURE;
            }
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(photosList.get(i).getId())
                    .snippet(photosList.get(i).getSpecieName()+ "\n" +
                            photosList.get(i).getOwnerName() + "\n" +
                            photosList.get(i).getCreatedAt())
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            );
            marker.showInfoWindow();
        }
    }
}