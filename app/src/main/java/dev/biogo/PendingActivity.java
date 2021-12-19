package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Models.Photo;

public class PendingActivity extends AppCompatActivity implements CatalogAdapter.OnItemListener {
    private static final String TAG = "PendingActivity";
    private DatabaseReference mDataBase;
    private RecyclerView catalogRView;
    private FirebaseUser firebaseUser;
    private ArrayList<Photo> photosList;
    private CatalogAdapter catalogListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        catalogRView = findViewById(R.id.catalogRView);
        catalogRView.setHasFixedSize(true);
        catalogRView.setLayoutManager(new LinearLayoutManager(this));

        photosList = new ArrayList<>();

        catalogListAdapter = new CatalogAdapter(this, photosList, this);
        catalogRView.setAdapter(catalogListAdapter);

        Query imagesQuery = mDataBase.child("images").orderByChild("classification").equalTo(ClassificationEnum.PENDING.toString());

        imagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                photosList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Photo photo = snapshot.getValue(Photo.class);
                    photo.setId(snapshot.getKey());
                    if(photo.getOwnerId().equals(firebaseUser.getUid()))
                        photosList.add(photo);
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
        Toast.makeText(this, photo.getSpecieName(), Toast.LENGTH_LONG).show();

        Intent photoIntent = new Intent(this, PhotoActivity.class);
        photoIntent.putExtra("photoData", photo);
        startActivity(photoIntent);

    }
}