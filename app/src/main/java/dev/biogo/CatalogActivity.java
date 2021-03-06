package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Date;

import dev.biogo.Adapters.CatalogAdapter;
import dev.biogo.Helpers.DateHelper;
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

        //EmptyCatalog
        TextView emptyCatalog = findViewById(R.id.emptyCatalog);



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
                photosList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Photo photo = snapshot.getValue(Photo.class);
                    if(photo != null) {
                        photo.setId(snapshot.getKey());
                        photosList.add(photo);
                    }
                }

                Collections.sort(photosList, Collections.reverseOrder((photo, otherPhoto) -> {
                    String pattern = "yyyy-MM-dd|HH:mm";
                    Date dateOne = DateHelper.convertToDate(photo.getCreatedAt(), pattern);
                    Date dateTwo = DateHelper.convertToDate(otherPhoto.getCreatedAt(), pattern);
                    return dateOne.compareTo(dateTwo);
                }));
                catalogListAdapter.notifyDataSetChanged();
                if (photosList.size() == 0){
                    emptyCatalog.setVisibility(View.VISIBLE);
                    catalogRView.setVisibility(View.GONE);
                }
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