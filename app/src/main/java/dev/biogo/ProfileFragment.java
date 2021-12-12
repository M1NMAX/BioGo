package dev.biogo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    public ProfileFragment() {
        // Required empty public constructor
    }


    private static final String TAG = "ProfileFragment";
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Firebase
        mDatabase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Catalog button
        Button seeCatalogBtn = view.findViewById(R.id.seeCatalog);
        seeCatalogBtn.setOnClickListener(this);

        //Ranking button
        Button seeRankingBtn = view.findViewById(R.id.seeRanking);
        seeRankingBtn.setOnClickListener(this);

        //User data
        ImageView currentUserProfilePic = view.findViewById(R.id.currentUserProfilePic);
        TextView currentUserName = view.findViewById(R.id.currentUserName);
        TextView currentUserXp = view.findViewById(R.id.currentUserXp);
        TextView currentUserRanking = view.findViewById(R.id.currentUserRanking);
        TextView currentUserNSpecies = view.findViewById(R.id.currentUserNSpecies);


        DatabaseReference userRef = mDatabase.child("users").child(firebaseUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                //update current user profile
                Picasso.get().load(user.getProfileImgUri()).into(currentUserProfilePic);
                currentUserName.setText(user.getUsername());
                currentUserXp.setText(user.getXp() + " XP");
                currentUserRanking.setText(String.valueOf(user.getRanking()));
                currentUserNSpecies.setText(String.valueOf(user.getSpecies()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.toException());
            }
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


        }

    }
}