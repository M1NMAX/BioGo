package dev.biogo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import dev.biogo.Adapters.CatalogAdapter;
import dev.biogo.Adapters.RankingAdapter;
import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Helpers.DateHelper;
import dev.biogo.Models.Photo;
import dev.biogo.Models.User;


public class HomeFragment extends Fragment implements View.OnClickListener {


    public HomeFragment() {
        // Required empty public constructor
    }


    private static final String TAG = "HomeFragment";
    ArrayList<User> usersList;
    private boolean isOpen = true;
    private FloatingActionButton fabTakePhoto;
    private FloatingActionButton fabUploadPhoto;
    private Uri imageUri;
    private DatabaseReference mDataBase;
    private FirebaseUser user;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;


    ImageView imageView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //Firebase Database
        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        //FAB
        FloatingActionButton fabMain = view.findViewById(R.id.fabMain);
        fabTakePhoto = view.findViewById(R.id.fabTakePhoto);
        fabUploadPhoto = view.findViewById(R.id.fabUploadPhoto);
        fabMain.setOnClickListener(this);
        fabUploadPhoto.setOnClickListener(this);
        fabTakePhoto.setOnClickListener(this);


        //User data
        ImageView userAvatar = view.findViewById(R.id.userAvatar);
        TextView username = view.findViewById(R.id.username);


        //User data from  firebaseAuth
        user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        String name = user.getDisplayName();
        username.setText(name);
        Picasso.get().load(photoUrl).into(userAvatar);

        //imageView = view.findViewById(R.id.playerProfileView1);

        //Ranking Section
        Button seeRankingBtn = view.findViewById(R.id.seeRanking);
        seeRankingBtn.setOnClickListener(this);

        RecyclerView rankingRecyclerView = view.findViewById(R.id.homeFragment_rankingRecyclerView);
        rankingRecyclerView.setHasFixedSize(true);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        usersList = new ArrayList<>();

        RankingAdapter rankingAdapter = new RankingAdapter(getContext(), usersList, R.layout.ranking_list_item_vertical, position -> {
            User otherUser =  usersList.get(position);
            Intent playerProfileIntent = new Intent(getActivity(), PlayerProfileActivity.class);
            playerProfileIntent.putExtra("userData", otherUser);
            startActivity(playerProfileIntent);
        });
        rankingRecyclerView.setAdapter(rankingAdapter);
        Query usersQuery = mDataBase.child("users").orderByChild("ranking").limitToFirst(3);
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User userForRanking = snapshot.getValue(User.class);
                    if (userForRanking != null) {
                        userForRanking.setUserId(snapshot.getKey());
                        usersList.add(userForRanking);
                    }
                }
                rankingAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: ", error.toException());
            }
        });

        //End Ranking Section

        //Recently added  Section
        Button seeCatalogBtn = view.findViewById(R.id.seeCatalog);
        seeCatalogBtn.setOnClickListener(this);

        RecyclerView recentlyRecyclerView = view.findViewById(R.id.recentlyRecyclerView);
        recentlyRecyclerView.setHasFixedSize(true);
        recentlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Photo> photosList = new ArrayList<>();

        CatalogAdapter catalogAdapter = new CatalogAdapter(getContext(), photosList,R.layout.catalog_list_item_horizontal, position -> {
            Photo photo = photosList.get(position);
            Intent photoIntent = new Intent(getActivity(), PhotoActivity.class);
            photoIntent.putExtra("photoData", photo);
            startActivity(photoIntent);
        });
        recentlyRecyclerView.setAdapter(catalogAdapter);

        Query imagesQuery = mDataBase.child("images").orderByChild("ownerId").equalTo(user.getUid());

        imagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                photosList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Photo photo = snapshot.getValue(Photo.class);
                    if(photo != null) {
                        photo.setId(snapshot.getKey());
                        photosList.add(photo);
                    }
                }
                Collections.sort(photosList, Collections.reverseOrder((photo, otherPhoto) -> {
                    String pattern = "EE MMM dd HH:mm:ss z yyyy";
                    Date dateOne = DateHelper.convertToDate(photo.getCreatedAt(), pattern);
                    Date dateTwo = DateHelper.convertToDate(otherPhoto.getCreatedAt(), pattern);
                    return dateOne.compareTo(dateTwo);
                }));
                catalogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: ", error.toException());
            }
        });
        //End Recently added  Section


        return view;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seeCatalog:
                Intent catalogIntent = new Intent(getActivity(), CatalogActivity.class);
                startActivity(catalogIntent);
                break;

            case R.id.seeRanking:
                Intent rankingIntent = new Intent(getActivity(), RankingActivity.class);
                startActivity(rankingIntent);
                break;

            case R.id.fabMain:
                if (isOpen) {
                    fabTakePhoto.show();
                    fabUploadPhoto.show();
                    isOpen = false;

                } else {
                    fabTakePhoto.hide();
                    fabUploadPhoto.hide();
                    isOpen = true;
                }
                break;
            case R.id.fabUploadPhoto:
                openImage();
                break;
            case R.id.fabTakePhoto:
                takePhoto();
                break;
        }


    }

    ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            getCurrentLocation();
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap bitmap;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                            // imageView.setImageBitmap(bitmap);
                            getCurrentLocation();
                        } catch (IOException e) {
                            Log.w(TAG, "onActivityResult: ", e);
                        }
                    }
                }
            }
    );


    private void openImage() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        imageActivityResultLauncher.launch(imageIntent);
    }

    private String createImageName() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK).format(new Date());
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                    .child("image/").child(createImageName());
            fileRef.putFile(imageUri).addOnCompleteListener(task ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        //Convert current Latitude and Longitude to String
                        String lat = String.valueOf(currentLocation.getLatitude());
                        String lng = String.valueOf(currentLocation.getLongitude());

                        //Save image data in the database
                        Photo photo = new Photo(lat, lng, uri.toString(), "N/A", "N/A",
                                user.getUid(), user.getDisplayName(), ClassificationEnum.PENDING.toString(),
                                new Date().toString());
                        mDataBase.child("images").push().setValue(photo);

                        pd.dismiss();
                        Toast.makeText(getContext(), "Image uploaded Successfully", Toast.LENGTH_LONG).show();


                    }));
        }

    }


    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;
            try {
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile(createImageName(), ".jpg", storageDir);
            } catch (IOException ex) {
                Log.w(TAG, "takePhoto: " + ex.getMessage(), ex);
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(getContext(), "dev.biogo", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                takePictureActivityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    private void getCurrentLocation() {

        try {
            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            Task<Location> locationRes = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken());
            locationRes.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    currentLocation = task.getResult();
                    if (currentLocation != null)
                        uploadImage();
                    Log.d(TAG, "getLocation: " + currentLocation);

                } else {
                    Log.d(TAG, "Get location failed");
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception %s", e.getMessage(), e);
        }
    }


}