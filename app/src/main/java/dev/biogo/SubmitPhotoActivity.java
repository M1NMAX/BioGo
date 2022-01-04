package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Models.Photo;

public class SubmitPhotoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference mDataBase;

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
        Log.d("submitiing", "ON: " + date);

        EditText editTextDate = (EditText) findViewById(R.id.photoDate);
        editTextDate.setText(date);

        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("submitiing", "onClick: SUBMITING");

                ProgressDialog pd = new ProgressDialog(SubmitPhotoActivity.this);
                pd.setMessage("Uploading");
                pd.show();
                if (imageUri != null) {
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                            .child("image/").child(createImageName());
                    fileRef.putFile(imageUri).addOnCompleteListener(task ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {



                                //Save image data in the database
                                //Not with the info in inputs yet
                                Photo photo = new Photo(lat, lng, imageUri.toString(), "N/A", "N/A",
                                        userId, userName, classification,
                                        date);
                                mDataBase.child("images").push().setValue(photo);

                                pd.dismiss();
                                Toast.makeText(SubmitPhotoActivity.this, "Image uploaded Successfully", Toast.LENGTH_LONG).show();

                                finish();
                            }));
                }
            }

        });


        //Firebase Database
        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();


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
        date = extras.getString("date");
        Log.d("photoCenas", "setPhotoAttrs: " + lat + " | " + lng);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.photoMap = googleMap;
        photoMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_photo));
        if (!(lat == null)) {
            Double locationLat = Double.parseDouble(lat);
            Double locationLng = Double.parseDouble(lng);
            LatLng location = new LatLng(locationLat, locationLng);
            photoMap.addMarker(new MarkerOptions().position(location).title("Location"));
            photoMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationLat, locationLng), 14));
        }
    }

    private String createImageName() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK).format(new Date());
    }
}