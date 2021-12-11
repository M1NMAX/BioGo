package dev.biogo;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String profileImgUri;
    private String username;
    private int xp;
    private int ranking;

    public User (){}


    public User(String profileImgUri, String username, int xp, int ranking) {
        this.profileImgUri = profileImgUri;
        this.username = username;
        this.xp = xp;
        this.ranking = ranking;
    }

    protected User(Parcel in) {
        profileImgUri = in.readString();
        username = in.readString();
        xp = in.readInt();
        ranking = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getProfileImgUri() {
        return profileImgUri;
    }

    public void setProfileImgUri(String profileImgUri) {
        this.profileImgUri = profileImgUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profileImgUri);
        parcel.writeString(username);
        parcel.writeInt(xp);
        parcel.writeInt(ranking);
    }
}
