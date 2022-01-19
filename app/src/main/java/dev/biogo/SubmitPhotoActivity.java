package dev.biogo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Models.ApiSpecie;
import dev.biogo.Models.Photo;

public class SubmitPhotoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference mDataBase;


    private Photo photo;
    private ApiSpecie apiSpecie;

    private GoogleMap photoMap;
    private SupportMapFragment supportMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_photo);

        //Back btn
         MaterialToolbar back = findViewById(R.id.appBarSubmitPhoto);
         back.setOnClickListener((view)-> finish());

         //Cancel btn
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener((view)->finish());


        Intent intent = getIntent();
        setPhotoAttrs(intent);

        if(intent.getExtras().getParcelable("apiSpecie") != null){
            Log.d("apiSpeciee", "onCreate: ADICIONADAA");
        }
        //get photo uri
        Uri imageUri = Uri.parse(photo.getImgUrl());

        ImageView photoImage = (ImageView) findViewById(R.id.submitImage);
        photoImage.setImageURI(null);
        photoImage.setImageURI(imageUri);
        //String photoString = intent.getExtras().getString("Photo");

        EditText editTextDate = (EditText) findViewById(R.id.photoDate);
        editTextDate.setText(photo.getCreatedAt());

        TextView specieName = (TextView) findViewById(R.id.specieName);
        EditText dateInput = (EditText) findViewById(R.id.photoDate);

        //onActivityResult
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            ApiSpecie apiSpecie = data.getParcelableExtra("apiSpecie");

                            //Get specie Description from wikipedia
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            String url ="https://en.wikipedia.org/w/rest.php/v1/search/page?q=" +apiSpecie.getSpecieName() + "&limit=1";

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponseObj = new JSONObject (response);
                                                JSONArray firstPage = jsonResponseObj.getJSONArray("pages");
                                                JSONObject descriptionJson = (JSONObject) firstPage.get(0);
                                                Object descriptionObj = descriptionJson.get("excerpt");
                                                String description = descriptionObj.toString().replaceAll("<[^>]*>", "");

                                                apiSpecie.setDescription(description);
                                            } catch (JSONException e) {
                                                Log.d("apierror", "onResponse: JSON ERROR");
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("apierror", "onErrorResponse: API Error");
                                }
                            });

                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);


                            //Get specie Points
                            String urlPoints ="https://api.inaturalist.org/v1/taxa/" + apiSpecie.getId();

                            StringRequest stringRequestPoints = new StringRequest(Request.Method.GET, urlPoints,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponseObj = new JSONObject (response);
                                                JSONArray array = jsonResponseObj.getJSONArray("results");
                                                JSONObject array0 = array.getJSONObject(0);
                                                Object obsCount = array0.get("observations_count");
                                                String counts = obsCount.toString();
                                                apiSpecie.setPoints(calculatePoints(counts));
                                            } catch (JSONException e) {
                                                Log.d("apierror", "onResponse: JSON ERROR");
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("apierror", "onErrorResponse: API Error");
                                }
                            });

                            // Add the request to the RequestQueue.
                            queue.add(stringRequestPoints);

                            photo.setApiSpecie(apiSpecie);
                            specieName.setText(apiSpecie.getSpecieName());
                            specieName.setTextColor(Color.parseColor("#243631"));
                        }
                    }
                });

        Button goToApiSearch = (Button) findViewById(R.id.go_to_api_search);
        goToApiSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ApiSpecieSearchActivity.class);
                someActivityResultLauncher.launch(intent);

            }
        });



        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(SubmitPhotoActivity.this);
                pd.setMessage("Uploading");
                pd.show();
                if (imageUri != null) {
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                            .child("image/").child(createImageName());
                    fileRef.putFile(imageUri).addOnCompleteListener(task ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                photo.setCreatedAt(dateInput.getText().toString());
                                String specie = specieName.getText().toString();
                                photo.setSpecieName(specie);
                                photo.setSpecieNameLower(removerAcentos(specie).toLowerCase());
                                photo.setImgUrl(uri.toString());
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
        if (supportMap != null && photo.getLat() != null) {
            supportMap.getMapAsync(this);
        }

    }


    public void setPhotoAttrs(Intent intent){
        Bundle extras = intent.getExtras();
        photo = extras.getParcelable("photo");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.photoMap = googleMap;
        photoMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_photo));
        if (!(photo.getLat() == null)) {
            Double locationLat = Double.parseDouble(photo.getLat());
            Double locationLng = Double.parseDouble(photo.getLng());
            LatLng location = new LatLng(locationLat, locationLng);
            photoMap.addMarker(new MarkerOptions().position(location).title("Photo taken here"));
            photoMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationLat, locationLng), 14));
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Photo taken here");

                //Assing to photo
                photo.setLat(String.valueOf(latLng.latitude));
                photo.setLng(String.valueOf(latLng.longitude));

                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                googleMap.addMarker(markerOptions);
            }
        });
    }

    private String createImageName() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK).format(new Date());
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String calculatePoints(String count){
        int countInt = Integer.parseInt(count);
        int points;
        if(countInt < 10000){
            points = 20;
        }else if(countInt < 30000){
            points = 10;
        }else{
            points = 5;
        }
        return String.valueOf(points);
    }
}