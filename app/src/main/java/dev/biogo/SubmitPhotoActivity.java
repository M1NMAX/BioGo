package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

import dev.biogo.Models.Photo;

public class SubmitPhotoActivity extends AppCompatActivity {
    private String lat;
    private String lng;
    private String userId;
    private String userName;
    private String classification;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_photo);
        Intent intent = getIntent();
        setPhotoAttrs(intent);

        //String photoString = intent.getExtras().getString("Photo");
        //Log.d("Passouuu", "ON: " + photoString);

    }

    public void setPhotoAttrs(Intent intent){
        Bundle extras = intent.getExtras();
        lat = extras.getString("lat");
        lng = extras.getString("lng");
        userId = extras.getString("userId");
        userName = extras.getString("userName");
        classification = extras.getString("classification");
        Log.d("photoCenas", "setPhotoAttrs: " + lat + " | " + lng);

    }
}