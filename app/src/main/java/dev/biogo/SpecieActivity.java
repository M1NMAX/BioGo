package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import dev.biogo.Models.ApiSpecie;
import dev.biogo.Models.Photo;

public class SpecieActivity extends AppCompatActivity {
    private ApiSpecie specie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specie);

        Intent i = getIntent();
        specie = (ApiSpecie) i.getParcelableExtra("apiSpecie");

        //back button
        MaterialToolbar back = findViewById(R.id.appBarSpecie);
        back.setOnClickListener(view -> finish());

        //dynamically inflate photo activity
        ImageView specie_imgView = findViewById(R.id.specieImage);
        Picasso.get().load(specie.getSpecieImage()).into(specie_imgView);

        TextView specie_name_tvView = findViewById(R.id.specie_name);
        specie_name_tvView.setText(specie.getSpecieName());

        TextView specie_points_tvView = findViewById(R.id.specie_points);
        specie_points_tvView.setText(specie.getPoints());


    }
}