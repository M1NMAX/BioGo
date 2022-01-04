package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

import dev.biogo.Models.Photo;

public class SubmitPhotoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String lat;
    private String lng;
    private String userId;
    private String userName;
    private String classification;
    private String date;
    private Uri imageUri;

    private GoogleMap photoMap;
    private SupportMapFragment supportMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_photo);
        Intent intent = getIntent();
        setPhotoAttrs(intent);

        ImageView photoImage = (ImageView) findViewById(R.id.submitImage);
        photoImage.setImageURI(null);
        photoImage.setImageURI(imageUri);
        //String photoString = intent.getExtras().getString("Photo");
        //Log.d("Passouuu", "ON: " + photoString);

        //Map Instance
        supportMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_submit_photo);
        if (supportMap != null && lat != null) {
            supportMap.getMapAsync(this);
        }

    }

    public void setPhotoAttrs(Intent intent){
        Bundle extras = intent.getExtras();
        lat = extras.getString("lat");
        lng = extras.getString("lng");
        userId = extras.getString("userId");
        userName = extras.getString("userName");
        classification = extras.getString("classification");
        imageUri = extras.getParcelable("photo");
        Log.d("photoCenas", "setPhotoAttrs: " + lat + " | " + lng);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.photoMap = googleMap;
        photoMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_photo));
        Log.d("mapaa", "onMapReady: NOOO");
        if (!(lat == null)) {
            Log.d("mapaa", "onMapReady: Yaa");
            Double locationLat = Double.parseDouble(lat);
            Double locationLng = Double.parseDouble(lng);
            LatLng location = new LatLng(locationLat, locationLng);
            photoMap.addMarker(new MarkerOptions().position(location).title("Location"));
            photoMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationLat, locationLng), 14));
        }
    }
}