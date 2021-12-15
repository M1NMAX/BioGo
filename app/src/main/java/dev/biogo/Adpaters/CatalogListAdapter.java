package dev.biogo.Adpaters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.biogo.Models.Photo;
import dev.biogo.R;

public class CatalogListAdapter extends ArrayAdapter<Photo> {
    public CatalogListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Photo photo = getItem(position);
        if (convertView == null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.catalog_list_item, parent, false);
        }
        ImageView image = convertView.findViewById(R.id.catalogListView_image);
        TextView title = convertView.findViewById(R.id.catalogListView_title);

        Picasso.get().load(Uri.parse(photo.getImgUrl())).into(image);
        title.setText(photo.getClassification().toString());



        return convertView;
    }
}
