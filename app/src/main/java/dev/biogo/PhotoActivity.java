package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Enums.RoleEnum;
import dev.biogo.Models.Photo;
import dev.biogo.Models.User;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback  {
    private static final String TAG = "PhotoActivity";
    private FirebaseUser firebaseUser;
    private Photo photo;
    private static final ClassificationEnum[] classificationEnumArray = ClassificationEnum.values();
    private static final CharSequence[] singleItems = new CharSequence[ClassificationEnum.values().length];
    private int checkedItem = singleItems.length - 2;
    private DatabaseReference photoRef;
    private DatabaseReference ownerRef;
    private DatabaseReference currentUserRef;

    private GoogleMap photoMap;
    private SupportMapFragment supportMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //back button
        MaterialToolbar back = findViewById(R.id.appBarPhoto);
        back.setOnClickListener(view -> finish());


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        photoRef = mDatabase.child("images");
        ownerRef = mDatabase.child("users");
        currentUserRef = mDatabase.child("users").child(firebaseUser.getUid());


        Intent i = getIntent();
        photo = (Photo) i.getParcelableExtra("photoData");

        //Fill singleItems with ClassificationEnums
        for (int e = 0; e < classificationEnumArray.length; e++) {
            singleItems[e] = classificationEnumArray[e].toString();
        }


        //dynamically inflate photo activity
        ImageView photo_imgView = findViewById(R.id.photo_imgView);
        Picasso.get().load(Uri.parse(photo.getImgUrl())).into(photo_imgView);

        TextView photo_specieName = findViewById(R.id.photo_specieName);
        photo_specieName.append(photo.getSpecieName());

        TextView photo_ownerName = findViewById(R.id.photo_ownerName);
        photo_ownerName.append(photo.getOwnerName());

        TextView photo_classification = findViewById(R.id.photo_classification);
        photo_classification.append(photo.getClassification());



        TextView photo_createdAt = findViewById(R.id.photo_createdAt);
        photo_createdAt.append(photo.getCreatedAt());


        Button evaluateBtn = findViewById(R.id.evaluateBtn);
        evaluateBtn.setOnClickListener(this);
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    currentUser.setUserId(snapshot.getKey());
                    //hide btn if current user is the owner or is not a moderator
                    if (!currentUser.getRole().equals(RoleEnum.MODERATOR.toString())
                            || photo.getOwnerId().equals(currentUser.getUserId())) {
                        evaluateBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.toException());
            }
        });

        //Map Instance
        supportMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_photo);
        if (supportMap != null && photo.getLat() != null) {
            supportMap.getMapAsync(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.evaluateBtn:

                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                        .setTitle(getResources().getText(R.string.evaluation_dialog_title))
                        .setNeutralButton(getResources().getText(R.string.evaluation_dialog_cancel), (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton(getResources().getText(R.string.evaluation_dialog_save), (dialogInterface, i) -> {
                            //TODO: hide EvaluationBtn
                            //TODO: Update photoActivity UI

                            //Hide dialog
                            dialogInterface.dismiss();
                            //Update photo data in the database
                            String newClassification = (String) singleItems[checkedItem];
                            Map<String, Object> photoUpdate = new HashMap<>();
                            photoUpdate.put("classification", newClassification);
                            photoUpdate.put("evaluateBy", firebaseUser.getDisplayName());
                            photoRef.child(photo.getId()).updateChildren(photoUpdate);

                            //Update owner xp and specie number
                            Map<String, Object> ownerUpdate = new HashMap<>();
                            int xpIncValue = ClassificationEnum.valueOf(newClassification).getValue();
                            ownerUpdate.put("xp", ServerValue.increment(xpIncValue));
                            ownerUpdate.put("species",ServerValue.increment(1));
                            ownerRef.child(photo.getOwnerId()).updateChildren(ownerUpdate);

                        })
                        .setSingleChoiceItems(singleItems, checkedItem, (dialogInterface, i) -> checkedItem = i);

                dialogBuilder.show();

                break;
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.photoMap = googleMap;
        photoMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_photo));
        if (!(photo.getLat() == null)) {
            Double locationLat = Double.parseDouble(photo.getLat());
            Double locationLng = Double.parseDouble(photo.getLng());
            LatLng location = new LatLng(locationLat, locationLng);
            photoMap.addMarker(new MarkerOptions().position(location).title("Location"));
            photoMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationLat, locationLng), 14));
        }
    }

}