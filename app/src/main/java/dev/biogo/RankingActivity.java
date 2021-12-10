package dev.biogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {
    private static final String TAG = "RankingActivity";
    private DatabaseReference mDatabase;
    ListView rankingListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        mDatabase = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        rankingListView = findViewById(R.id.rankingListView);
        ArrayList<User> rankingList = new ArrayList<>();
        RankingListAdapter rankingListAdapter = new RankingListAdapter(this, R.layout.ranking_list_item, rankingList);
        rankingListView.setAdapter(rankingListAdapter);



        DatabaseReference usersRef = mDatabase.child("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rankingList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren() ){
                    User user = snapshot.getValue(User.class);

                    rankingList.add(user);
                    Log.i(TAG, "onDataChange: "+user.getStatus().getXp());
                }
                Log.d(TAG, "onDataChange: "+rankingList.size());
                rankingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

    }
}