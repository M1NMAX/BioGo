package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.biogo.Adapters.CatalogAdapter;
import dev.biogo.Models.Photo;
import dev.biogo.Models.User;

public class CatalogActivity extends AppCompatActivity implements CatalogAdapter.OnItemListener {
    private static final String TAG = "CatalogActivity";
    private ArrayList<Photo> photosList;
    private CatalogAdapter catalogListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //back button
        MaterialToolbar back = findViewById(R.id.appBarCatalog);
        back.setOnClickListener((view)-> finish());

        String username, userId;


        Intent i = getIntent();
        if (i.hasExtra("otherUserData")) {
            User otherUser = i.getParcelableExtra("otherUserData");
            username = otherUser.getUsername();
            userId = otherUser.getUserId();
        }else {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            username = firebaseUser != null ? firebaseUser.getDisplayName() : null;
            userId = firebaseUser != null ? firebaseUser.getUid() : null;

        }
        DatabaseReference mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();


        TextView catalogOwner = findViewById(R.id.catalogOwner);
        catalogOwner.setText(username + "'s catalog");


        RecyclerView catalogRView = findViewById(R.id.catalogRView);
        catalogRView.setHasFixedSize(true);
        catalogRView.setLayoutManager(new LinearLayoutManager(this));

        photosList = new ArrayList<>();

        catalogListAdapter = new CatalogAdapter(this, photosList, R.layout.catalog_list_item,this);
        catalogRView.setAdapter(catalogListAdapter);

        Query imagesQuery = mDataBase.child("images").orderByChild("ownerId").equalTo(userId);

        imagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Photo photo = snapshot.getValue(Photo.class);
                    if(photo != null) {
                        photo.setId(snapshot.getKey());
                        photosList.add(photo);
                    }
                }
                catalogListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: ", error.toException());
            }
        });



    }


    @Override
    public void OnItemClick(int position) {
        Photo photo = photosList.get(position);
        Intent photoIntent = new Intent(this, PhotoActivity.class);
        photoIntent.putExtra("photoData", photo);
        startActivity(photoIntent);

    }
}