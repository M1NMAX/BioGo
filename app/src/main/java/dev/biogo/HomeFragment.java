package dev.biogo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = "HomeFragment";
    private boolean isOpen = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    private static final int IMAGE_REQUEST_CODE = 2;
    FloatingActionButton fabAddPhoto;
    FloatingActionButton fabUploadPhoto;
    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        String name = user.getDisplayName();
        username.setText(name);
        Picasso.get().load(photoUrl).into(userAvatar);

        // Player profile view
        ImageView player1 = view.findViewById(R.id.playerProfileView1);
        player1.setOnClickListener((view1)->{
            Intent playerProfileIntent = new Intent(getActivity(), PlayerProfileActivity.class);
            startActivity(playerProfileIntent);
        });


        return view;
    }

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
                Toast.makeText(getContext(), "goog", Toast.LENGTH_LONG).show();
                break;
        }


    }

    private void openImage() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            imageUri = data.getData();
            uploadImage();

        }

    }



    private void uploadImage() {
        ProgressDialog pd= new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null){
            StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                    .child("image/").child(String.valueOf(System.currentTimeMillis()));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri uri) {
                            String url = uri.toString();
                            Log.d(TAG, "onSuccess: "+url);
                            pd.dismiss();
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });
        }

    }
}