package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.biogo.Adpaters.CatalogListAdpater;

public class CatalogActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "CatalogActivity";
    private DatabaseReference mDataBase;
    private ListView catalogListView;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        catalogListView = findViewById(R.id.catalogListView);
        ArrayList<ImageModel> catalogList = new ArrayList<>();
        CatalogListAdpater catalogListAdpater = new CatalogListAdpater(this, R.layout.catalog_list_item, catalogList);
        catalogListView.setAdapter(catalogListAdpater);

        DatabaseReference imagesRef = mDataBase.child("images").child(firebaseUser.getUid());

        imagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageModel imageModel = snapshot.getValue(ImageModel.class);
                    catalogList.add(imageModel);
                }
                catalogListAdpater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: ", error.toException());
            }
        });
        catalogListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ImageModel imageModel = (ImageModel) adapterView.getItemAtPosition(i);
        Toast.makeText(this, imageModel.getSpecieName(), Toast.LENGTH_LONG).show();

        Intent photoIntent = new Intent(this, PhotoActivity.class);
        photoIntent.putExtra("photoData", imageModel);
        startActivity(photoIntent);
    }
}