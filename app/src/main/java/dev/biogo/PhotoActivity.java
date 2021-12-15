package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dev.biogo.Models.Photo;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Intent i = getIntent();
        Photo photo = (Photo) i.getParcelableExtra("photoData");

        //dynamically inflate photo activity

        ImageView photo_imgView = findViewById(R.id.photo_imgView);
        Picasso.get().load(Uri.parse(photo.getImgUrl())).into(photo_imgView);

        TextView photo_specieName = findViewById(R.id.photo_specieName);
        photo_specieName.append(photo.getSpecieName());

        TextView photo_ownerName = findViewById(R.id.photo_ownerName);
        photo_ownerName.append(photo.getOwnerName());

        TextView photo_classification = findViewById(R.id.photo_classification);
        photo_classification.append(photo.getClassification());

        TextView photo_location = findViewById(R.id.photo_location);
        photo_location.append(photo.getLat()+" Lat, "+photo.getLng()+" Lng");


    }
}