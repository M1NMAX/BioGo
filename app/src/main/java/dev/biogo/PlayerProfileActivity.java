package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class PlayerProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        //User data
        ImageView userAvatar = findViewById(R.id.userAvatar);
        TextView username = findViewById(R.id.username);

        //Rank
        //TextView points = findViewById(R.id.points);

        //Retrive user data from firebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        String name = user.getDisplayName();

        username.setText(name);
        Picasso.get().load(photoUrl).into(userAvatar);
    }
}