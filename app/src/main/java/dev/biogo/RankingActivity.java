package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


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
import java.util.Comparator;

import dev.biogo.Adapters.RankingAdapter;
import dev.biogo.Models.User;

public class RankingActivity extends AppCompatActivity implements RankingAdapter.OnItemListener {
    private static final String TAG = "RankingActivity";
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private RecyclerView rankingRecyclerView;
    private  ArrayList<User> usersList;
    private MaterialToolbar back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //back button
        back = findViewById(R.id.appBarRanking);
        back.setOnClickListener(view -> finish());


        mDatabase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setHasFixedSize(true);
        rankingRecyclerView.setLayoutManager( new LinearLayoutManager(this));

        usersList = new ArrayList<>();
        RankingAdapter rankingAdapter = new RankingAdapter(this, usersList,R.layout.ranking_list_item, this);
        rankingRecyclerView.setAdapter(rankingAdapter);


        Query usersQuery = mDatabase.child("users").orderByChild("xp");
        DatabaseReference userRef = mDatabase.child("users");
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = (int) dataSnapshot.getChildrenCount();
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //You can remove the current auth user
                    User user = snapshot.getValue(User.class);
                    if(user != null) {
                        user.setUserId(snapshot.getKey());
                        userRef.child(user.getUserId()).child("ranking").setValue(counter);
                        //Update UI
                        user.setRanking(counter);
                        usersList.add(user);
                        counter--;
                    }

                }
                Collections.sort(usersList, new Comparator<User>() {
                    @Override
                    public int compare(User user, User otherUser) {
                        return user.getRanking() - otherUser.getRanking();
                    }
                });
                rankingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled", error.toException());
            }
        });


    }



    @Override
    public void OnItemClick(int position) {
        User user = (User) usersList.get(position);
        Intent playerProfileIntent = new Intent(this, PlayerProfileActivity.class);
        playerProfileIntent.putExtra("userData", user);
        startActivity(playerProfileIntent);
    }
}