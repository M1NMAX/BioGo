package dev.biogo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment implements View.OnClickListener {


    public HomeFragment() {
        // Required empty public constructor
    }


    private static final String TAG = "HomeFragment";
    private boolean isOpen = true;
    private FloatingActionButton fabAddPhoto;
    private FloatingActionButton fabUploadPhoto;
    private Uri imageUri;
    private DatabaseReference mDataBase;
    private FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Firebase Database
        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        //FAB
        FloatingActionButton fabMain = view.findViewById(R.id.fabMain);
        fabAddPhoto = view.findViewById(R.id.fabAddPhoto);
        fabUploadPhoto = view.findViewById(R.id.fabUploadPhoto);
        fabMain.setOnClickListener(this);
        fabUploadPhoto.setOnClickListener(this);

        //User data
        ImageView userAvatar = view.findViewById(R.id.userAvatar);
        TextView username = view.findViewById(R.id.username);

        //Catalog button
        Button seeCatalogBtn = view.findViewById(R.id.seeCatalog);
        seeCatalogBtn.setOnClickListener(this);

        //Ranking button
        Button seeRankingBtn = view.findViewById(R.id.seeRanking);
        seeRankingBtn.setOnClickListener(this);

        //User data from  firebaseAuth
        user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        String name = user.getDisplayName();
        username.setText(name);
        Picasso.get().load(photoUrl).into(userAvatar);

        // Player profile view
        ImageView player1 = view.findViewById(R.id.playerProfileView1);
        player1.setOnClickListener((view1) -> {
            Intent playerProfileIntent = new Intent(getActivity(), PlayerProfileActivity.class);
            startActivity(playerProfileIntent);
        });


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
                    fabAddPhoto.show();
                    fabUploadPhoto.show();
                    isOpen = false;

                } else {
                    fabAddPhoto.hide();
                    fabUploadPhoto.hide();
                    isOpen = true;
                }
                break;
            case R.id.fabUploadPhoto:
                openImage();
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
                            uploadImage();
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

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                    .child("image/").child(String.valueOf(System.currentTimeMillis()));
            fileRef.putFile(imageUri).addOnCompleteListener(task ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        //Save image data in the database
                        ImageModel image = new ImageModel("0", "0", uri.toString(), "N/A", "N/A", ClassificationEnum.PENDING);
                        mDataBase.child("images").child(user.getUid()).push().setValue(image);

                        pd.dismiss();
                        Toast.makeText(getContext(), "Image uploaded Successfully", Toast.LENGTH_LONG).show();

                    }));
        }

    }
}