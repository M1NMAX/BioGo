package dev.biogo;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private static final String TAG = "ProfileFragment";
    DatabaseReference mDatabase;
    User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = mDatabase.child("users").child(cUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                Log.d(TAG, "onDataChange: "+currentUser.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.toException());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Catalog button
        Button seeCatalogBtn = view.findViewById(R.id.seeCatalog);
        seeCatalogBtn.setOnClickListener(this);

        //Ranking button
        Button seeRankingBtn = view.findViewById(R.id.seeRanking);
        seeRankingBtn.setOnClickListener(this);

        //User data
        ImageView userAvatar = view.findViewById(R.id.userAvatar);
        TextView username = view.findViewById(R.id.username);

        Log.d(TAG, "onCreateView: "+currentUser);

        //User data from  firebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        String name = user.getDisplayName();

        username.setText(name);
        Picasso.get().load(photoUrl).into(userAvatar);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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