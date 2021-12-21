package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dev.biogo.Models.User;

public class PlayerProfileActivity extends AppCompatActivity{


    //private static final String TAG = "PlayerProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        Intent i = getIntent();
        User otherUser =  i.getParcelableExtra("userData");


        //dynamically inflate other Player  profile
        ImageView userAvatar = findViewById(R.id.userAvatar);
        Picasso.get().load(Uri.parse(otherUser.getProfileImgUri())).into(userAvatar);

        TextView username = findViewById(R.id.username);
        username.setText(otherUser.getUsername());

        TextView otherPlayerXp = findViewById(R.id.otherPlayerXp);
        otherPlayerXp.setText(otherUser.getXp() + " XP");


        TextView otherPlayerRanking = findViewById(R.id.otherPlayerRanking);
        otherPlayerRanking.setText(String.valueOf(otherUser.getRanking()));

        TextView otherPlayerNSpecies = findViewById(R.id.otherPlayerNSpecies);
        otherPlayerNSpecies.setText(String.valueOf(otherUser.getSpecies()));

        Button seeOtherPlayerCatalogBtn = findViewById(R.id.seeOtherPlayerCatalogBtn);
        seeOtherPlayerCatalogBtn.setText("See " + otherUser.getUsername() + "'s catalog");

        seeOtherPlayerCatalogBtn.setOnClickListener(view -> {
            Intent otherPlayerCatalogIntent = new Intent(PlayerProfileActivity.this, CatalogActivity.class);
            otherPlayerCatalogIntent.putExtra("otherUserData", otherUser);
            startActivity(otherPlayerCatalogIntent);

        });



    }


}