package dev.biogo.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dev.biogo.Models.Photo;
import dev.biogo.R;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    Context context;
    ArrayList<Photo> photos;

    public CatalogAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.catalog_list_item, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.specieName.append(photo.getSpecieName());
        holder.classification.append(photo.getClassification());
        Picasso.get().load(Uri.parse(photo.getImgUrl())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView specieName, classification;
        ImageView image;


        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);

            specieName = itemView.findViewById(R.id.catalogListView_specieName);
            classification = itemView.findViewById(R.id.catalogListView_classification);
            image = itemView.findViewById(R.id.catalogListView_image);
        }
    }
}
