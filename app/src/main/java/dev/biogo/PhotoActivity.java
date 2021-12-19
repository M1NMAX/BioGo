package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Models.Photo;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PhotoActivity";
    private FirebaseUser firebaseUser;
    private Photo photo;
    private Button evaluateBtn;
    private MaterialAlertDialogBuilder dialogBuilder;
    private static final ClassificationEnum[] classificationEnumArray =  ClassificationEnum.values();
    private static final CharSequence[] singleItems = new CharSequence[ClassificationEnum.values().length];
    private int checkedItem = singleItems.length - 2;
    private DatabaseReference photoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        photoRef = FirebaseDatabase.getInstance("https://biogo-54daa-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("images");

        Intent i = getIntent();
        photo = (Photo) i.getParcelableExtra("photoData");

        //Fill singleItems with ClassificationEnums
        for (int e = 0; e < classificationEnumArray.length; e++){
            singleItems[e] = classificationEnumArray[e].toString();

        }



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

        TextView photo_createdAt = findViewById(R.id.photo_createdAt);
        photo_createdAt.append(photo.getCreatedAt());


        //TODO: hide btn if user is the owner or user is not a moderator
        //Evaluation
        evaluateBtn = findViewById(R.id.evaluateBtn);
        evaluateBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.evaluateBtn:

                dialogBuilder = new MaterialAlertDialogBuilder(this)
                        .setTitle(getResources().getText(R.string.evaluation_dialog_title))
                        .setNeutralButton(getResources().getText(R.string.evaluation_dialog_cancel), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton(getResources().getText(R.string.evaluation_dialog_save), (dialogInterface, i)->{
                            dialogInterface.dismiss();
                            Log.d(TAG, "onClick: "+ClassificationEnum.valueOf((String) singleItems[checkedItem]).getValue());
                            //TODO: hide btn  and update user xp
                            //TODO: update photoActivity UI


                            Map<String, Object> update = new HashMap<>();
                            update.put("classification", singleItems[checkedItem]);
                            update.put("evaluateBy", firebaseUser.getDisplayName());
                            photoRef.child(photo.getId()).updateChildren(update);

                        })
                        .setSingleChoiceItems(singleItems, checkedItem,( dialogInterface,  i)->checkedItem=i);

                dialogBuilder.show();

                break;
        }

    }
}