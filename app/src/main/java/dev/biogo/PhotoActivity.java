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
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Enums.RoleEnum;
import dev.biogo.Helpers.DateHelper;
import dev.biogo.Helpers.LocationHelper;
import dev.biogo.Models.Photo;
import dev.biogo.Models.User;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final String TAG = "PhotoActivity";
    private FirebaseUser firebaseUser;
    private Photo photo;
    private static final CharSequence[] singleItems = {ClassificationEnum.VALID.toString(), ClassificationEnum.INVALID.toString()};
    private int checkedItem = 0;
    private DatabaseReference photoRef;
    private DatabaseReference ownerRef;
    private DatabaseReference currentUserRef;

    private GoogleMap photoMap;
    private SupportMapFragment supportMap;

    private Button evaluateBtn;

    private ImageView photo_statusIcon;
    private TextView photo_statusText;

    //TODO: hide classification if pending or invalid??

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

        //dynamically inflate photo activity
        ImageView photo_imgView = findViewById(R.id.photo_imgView);
        Picasso.get().load(Uri.parse(photo.getImgUrl())).into(photo_imgView);

        TextView photo_specieName = findViewById(R.id.photo_specieName);
        photo_specieName.setText(photo.getSpecieName());

        ImageView userAvatar = findViewById(R.id.photo_ownerAvatar);
        Picasso.get().load(Uri.parse(photo.getUserProfilePic())).into(userAvatar);

        TextView photo_ownerName = findViewById(R.id.photo_ownerName);
        photo_ownerName.setText(photo.getOwnerName());


        //photo_status
        photo_statusIcon = findViewById(R.id.photo_statusIcon);
        photo_statusText = findViewById(R.id.photo_statusText);
        //first update
        updateClassificationUI(photo.getClassification());


        TextView photo_specieScientificName = findViewById(R.id.photo_specieScientificName);
        photo_specieScientificName.append(photo.getApiSpecie().getSpecieScientificName());


        Date date = DateHelper.convertToDate(photo.getCreatedAt(), "yyyy-MM-dd|HH:mm");
        String dateFormat = new SimpleDateFormat("yyyy-MM-dd|HH:mm", Locale.UK).format(date);
        TextView photo_createdAt = findViewById(R.id.photo_createdAt);
        photo_createdAt.setText(dateFormat);


        //Address
        TextView photo_address = findViewById(R.id.photo_address);
        photo_address.setText(LocationHelper.getAddressFromLatLng(this, Double.parseDouble(photo.getLat()), Double.parseDouble(photo.getLng())));


        evaluateBtn = findViewById(R.id.evaluateBtn);
        evaluateBtn.setOnClickListener(this);
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    currentUser.setUserId(snapshot.getKey());
                    //hide btn if current user is the owner or is not a moderator or photo already has classification
                    if (!currentUser.getRole().equals(RoleEnum.MODERATOR.toString())
                            || photo.getOwnerId().equals(currentUser.getUserId())
                            || !photo.getClassification().equals(ClassificationEnum.PENDING.toString())) {
                        evaluateBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.toException());
            }
        });

        Button goSpecie = findViewById(R.id.specieGoButton);
        goSpecie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent specieProfileIntent = new Intent(getApplicationContext(), SpecieActivity.class);
                specieProfileIntent.putExtra("apiSpecie", photo.getApiSpecie());
                //Log.d("taaamos", "onClick: "+ photo.getApiSpecie().getSpecieName());
                startActivity(specieProfileIntent);
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

                            //Hide dialog
                            dialogInterface.dismiss();
                            //Update photo data in the database
                            String newClassification = (String) singleItems[checkedItem];
                            Map<String, Object> photoUpdate = new HashMap<>();
                            photoUpdate.put("classification", newClassification);
                            photoUpdate.put("evaluatedBy", firebaseUser.getDisplayName());
                            photoRef.child(photo.getId()).updateChildren(photoUpdate);

                            //Update owner xp and specie number
                            Map<String, Object> ownerUpdate = new HashMap<>();
                            int xpIncValue = Integer.parseInt(photo.getApiSpecie().getPoints());
                            ownerUpdate.put("xp", ServerValue.increment(xpIncValue));
                            ownerUpdate.put("species", ServerValue.increment(1));
                            ownerRef.child(photo.getOwnerId()).updateChildren(ownerUpdate);


                            //Update UI
                            //hide EvaluationBtn and give user feedback
                            evaluateBtn.setVisibility(View.GONE);
                            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                            photo.setClassification(newClassification);
                            updateClassificationUI(photo.getClassification());


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


    private void updateClassificationUI (String classification){

        String tmp_statusText;
        int tmp_statusIconId;
        if (classification.equals(ClassificationEnum.PENDING.toString())) {
            tmp_statusIconId = R.drawable.ic_baseline_hourglass_empty_24;
            tmp_statusText = getString(R.string.photo_status_pending);
        } else if (classification.equals(ClassificationEnum.INVALID.toString())) {
            tmp_statusIconId = R.drawable.ic_baseline_do_not_disturb_alt_24;
            tmp_statusText = getString(R.string.photo_status_invalid);
        } else {
            tmp_statusIconId = R.drawable.ic_baseline_check_circle_24;
            tmp_statusText = getString(R.string.photo_status_valid);
        }
        photo_statusIcon.setImageResource(tmp_statusIconId);
        photo_statusText.setText(tmp_statusText);

    }

}