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

import dev.biogo.Helpers.DateHelper;
import dev.biogo.Models.Photo;
import dev.biogo.R;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    private Context context;
    private ArrayList<Photo> photos;
    private int resource;
    private OnItemListener mOnItemListener;

    public CatalogAdapter(Context context, ArrayList<Photo> photos, int resource,OnItemListener mOnItemListener) {
        this.context = context;
        this.photos = photos;
        this.resource = resource;
        this.mOnItemListener = mOnItemListener;
    }



    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new CatalogViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.specieName.setText(photo.getSpecieName());
        holder.classification.setText(photo.getClassification());
        holder.date.setText(DateHelper.getTimeAgo(DateHelper.convertToLong(photo.getCreatedAt(), "EE MMM dd HH:mm:ss z yyyy")));
        Picasso.get().load(Uri.parse(photo.getImgUrl())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }


    public class CatalogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        OnItemListener onItemListener;
        TextView specieName, classification, date;
        ImageView image;


        public CatalogViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            specieName = itemView.findViewById(R.id.catalogListView_specieName);
            classification = itemView.findViewById(R.id.catalogListView_classification);
            date = itemView.findViewById(R.id.catalogListView_date);
            image = itemView.findViewById(R.id.catalogListView_image);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.OnItemClick(getAdapterPosition());

        }
    }

    public interface OnItemListener{
         void OnItemClick(int position);
    }
}
