package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class PlayerProfileActivity extends AppCompatActivity {


    private static final String TAG ="PlayerProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        Intent i = getIntent();
        User otherUser = (User) i.getParcelableExtra("userData");
        otherUser.setStatus(i.getParcelableExtra("userStatus"));



        //dynamically inflate other Player  profile
        ImageView userAvatar = findViewById(R.id.userAvatar);
        Picasso.get().load(Uri.parse(otherUser.getProfileImgUri())).into(userAvatar);

        TextView username = findViewById(R.id.username);
        username.setText(otherUser.getUsername());

        TextView otherPlayerXp = findViewById(R.id.otherPlayerXp);
        otherPlayerXp.setText(otherUser.getStatus().getXp()+" XP");

        TextView otherPlayerTrophies = findViewById(R.id.otherPlayerTrophies);
        otherPlayerTrophies.setText(String.valueOf(otherUser.getStatus().getTrophies()));

        TextView otherPlayerMedals = findViewById(R.id.otherPlayerMedals);
        otherPlayerMedals.setText(String.valueOf(otherUser.getStatus().getMedals()));

        TextView otherPlayerRanking = findViewById(R.id.otherPlayerRanking);
        otherPlayerRanking.setText(String.valueOf(otherUser.getStatus().getRanking()));

        Button seeOtherCatalogBtn = findViewById(R.id.seeOtherPlayerCatalogBtn);
        seeOtherCatalogBtn.setText("See "+otherUser.getUsername()+"'s catalog");




    }
}