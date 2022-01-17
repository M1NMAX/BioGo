package dev.biogo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import dev.biogo.Enums.ClassificationEnum;
import dev.biogo.Helpers.DateHelper;
import dev.biogo.Helpers.LocationHelper;
import dev.biogo.Models.ApiSpecie;
import dev.biogo.Models.Photo;
import dev.biogo.Models.User;
import dev.biogo.PhotoActivity;
import dev.biogo.R;

public class ApiSpeciesAdapter extends RecyclerView.Adapter<ApiSpeciesAdapter.ApiSpeciesViewHolder>  {

    private Context context;
    private ArrayList<ApiSpecie> apiSpeciesList;
    private int resource;
    private ApiSpeciesAdapter.OnItemListener mOnItemListener;

    public ApiSpeciesAdapter(Context context, ArrayList<ApiSpecie> apiSpeciesList, int resource, ApiSpeciesAdapter.OnItemListener mOnItemListener) {
        this.context = context;
        this.apiSpeciesList = apiSpeciesList;
        this.resource = resource;
        this.mOnItemListener = mOnItemListener;
    }

    @NonNull
    public ApiSpeciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ApiSpeciesViewHolder(view, mOnItemListener);
    }


    public void onBindViewHolder(@NonNull ApiSpeciesAdapter.ApiSpeciesViewHolder holder, int position) {
        ApiSpecie apiSpecie = apiSpeciesList.get(position);
        holder.specieName.setText(apiSpecie.getSpecieName());
        holder.points.setText(apiSpecie.getPoints());
        //Picasso.get().load(Uri.parse(apiSpecie.getSpecieImage())).into(holder.image);
        Picasso.get().load(apiSpecie.getSpecieImage()).into(holder.image);
        /**try{
            URL url = new URL(apiSpecie.getSpecieImage());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            holder.image.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }**/


    }

    public int getItemCount() {
        return apiSpeciesList.size();
    }


    public class ApiSpeciesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ApiSpeciesAdapter.OnItemListener onItemListener;
        TextView specieName, points;
        ImageView image;


        public ApiSpeciesViewHolder(@NonNull View itemView, ApiSpeciesAdapter.OnItemListener onItemListener) {
            super(itemView);

            specieName = itemView.findViewById(R.id.apiSearchListView_specieName);
            points = itemView.findViewById(R.id.apiSearchListView_points);
            image = itemView.findViewById(R.id.apiSearchListView_image);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.OnItemClick(getAdapterPosition());

        }
    }

    public interface OnItemListener {
        void OnItemClick(int position);
    }
}
