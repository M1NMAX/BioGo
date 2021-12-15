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

import dev.biogo.ImageModel;
import dev.biogo.R;

public class CatalogListAdpater extends ArrayAdapter<ImageModel> {
    public CatalogListAdpater(@NonNull Context context, int resource, @NonNull List<ImageModel> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ImageModel imageModel = getItem(position);
        if (convertView == null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.catalog_list_item, parent, false);
        }
        ImageView image = convertView.findViewById(R.id.catalogListView_image);
        TextView title = convertView.findViewById(R.id.catalogListView_title);

        Picasso.get().load(Uri.parse(imageModel.getImgUrl())).into(image);
        title.setText(imageModel.getClassification().toString());



        return convertView;
    }
}
