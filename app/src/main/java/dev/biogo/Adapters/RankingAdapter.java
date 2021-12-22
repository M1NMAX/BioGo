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

import dev.biogo.Models.User;
import dev.biogo.R;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private int resource;
    private OnItemListener mOnItemListener;

    public RankingAdapter(Context context, ArrayList<User> users, int resource, OnItemListener mOnItemListener){
        this.context = context;
        this.users = users;
        this.resource = resource;
        this.mOnItemListener = mOnItemListener;
    }



    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent,false);

        return new RankingViewHolder(view,mOnItemListener );
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        holder.ranking.append(String.valueOf(user.getRanking()));
        holder.xp.setText(user.getXp() + " XP");
        Picasso.get().load(Uri.parse(user.getProfileImgUri())).into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemListener onItemListener;
        ImageView profilePic;
        TextView username, ranking, xp;

        public RankingViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.rankingListView_profilePic);
            username = itemView.findViewById(R.id.rankingListView_username);
            ranking = itemView.findViewById(R.id.rankingListView_ranking);
            xp = itemView.findViewById(R.id.rankingListView_xp);
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
