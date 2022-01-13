package dev.biogo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import dev.biogo.Adapters.CatalogAdapter;
import dev.biogo.Helpers.DateHelper;
import dev.biogo.Models.Photo;

public class SearchFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference mDataBase;
    private FirebaseUser user;


    private ArrayList<Photo> photosList;
    private RecyclerView recentlyRecyclerView;
    private CatalogAdapter catalogAdapter;
    private EditText searchInput;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = (EditText) view.findViewById(R.id.search_string);


        //Firebase Database
        mDataBase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();



        Button searchButton = (Button) view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        //RecyclerView Search
        recentlyRecyclerView = view.findViewById(R.id.searchRecyclerView);
        recentlyRecyclerView.setHasFixedSize(true);
        recentlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        photosList = new ArrayList<>();

        catalogAdapter = new CatalogAdapter(getContext(), photosList,R.layout.catalog_list_item_horizontal, position -> {
            Photo photo = photosList.get(position);
            Intent photoIntent = new Intent(getActivity(), PhotoActivity.class);
            photoIntent.putExtra("photoData", photo);
            startActivity(photoIntent);
        });
        recentlyRecyclerView.setAdapter(catalogAdapter);

        //MINI TESTE
        //Query imagesQuery = mDataBase.child("images").orderByChild("ownerId").equalTo(user.getUid());

        Query imagesQuery = mDataBase.child("images").limitToFirst(10);


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
                catalogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("onCancelled", "onCancelled: ", error.toException());
            }
        });



        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.search_button) {
            String searchInputStr = removerAcentos(searchInput.getText().toString()).toLowerCase();
            Query imagesQuery = mDataBase.child("images").orderByChild("specieNameLower").startAt(searchInputStr)
                    .endAt(searchInputStr + "\uf8ff");



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
                    catalogAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("onCancelled", "onCancelled: ", error.toException());
                }
            });

        }else{
            Intent intent = new Intent(getActivity(), PhotoActivity.class);
            startActivity(intent);
        }
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

}