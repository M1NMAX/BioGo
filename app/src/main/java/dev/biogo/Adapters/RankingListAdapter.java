package dev.biogo.Adapters;

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

import dev.biogo.R;
import dev.biogo.Models.User;

public class RankingListAdapter extends ArrayAdapter<User> {
    public RankingListAdapter(@NonNull Context context, int resource, @NonNull List<User> users) {
        super(context, resource, users);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranking_list_item, parent, false);
        }
        ImageView profilePic = convertView.findViewById(R.id.rankingListView_profilePic);
        TextView username = convertView.findViewById(R.id.rankingListView_username);
        TextView ranking = convertView.findViewById(R.id.rankingListView_ranking);
        TextView xp = convertView.findViewById(R.id.rankingListView_xp);


        Picasso.get().load(Uri.parse(user.getProfileImgUri())).into(profilePic);
        username.setText(user.getUsername());
        ranking.setText("Position: " + user.getRanking());
        xp.setText(user.getXp() + " XP");

        return convertView;
    }
}
