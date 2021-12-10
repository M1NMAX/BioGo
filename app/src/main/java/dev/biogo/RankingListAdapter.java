package dev.biogo;

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

public class RankingListAdapter extends ArrayAdapter<User> {
    public RankingListAdapter(@NonNull Context context, int resource, @NonNull List<User> users) {
        super(context, resource, users);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);


        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranking_list_item, parent, false);
        }
        ImageView profilePic = convertView.findViewById(R.id.rankingListView_profilePic);
        TextView username = convertView.findViewById(R.id.rankingListView_username);
        TextView ranking = convertView.findViewById(R.id.rankingListView_ranking);
        TextView xp = convertView.findViewById(R.id.rankingListView_xp);
        TextView medals = convertView.findViewById(R.id.rankingListView_medals);
        TextView trophies = convertView.findViewById(R.id.rankingListView_trophies);

        Picasso.get().load(Uri.parse(user.getProfileImgUri())).into(profilePic);
        username.setText(user.getUsername());
        ranking.setText("Position: "+String.valueOf(user.getStatus().getRanking()));
        xp.setText(String.valueOf(user.getStatus().getXp())+" XP");
        medals.setText(String.valueOf(user.getStatus().getMedals())+" Medals");
        trophies.setText(String.valueOf(user.getStatus().getTrophies())+" Trophies");

        return convertView;
    }
}
