package dev.biogo;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String profileImgUri;
    private String username;
    private String role;
    private int xp;
    private int ranking;
    private int species;

    public User (){}


    public User(String profileImgUri, String username, String role, int xp, int ranking, int species) {
        this.profileImgUri = profileImgUri;
        this.username = username;
        this.role = role;
        this.xp = xp;
        this.ranking = ranking;
        this.species = species;
    }

    protected User(Parcel in) {
        profileImgUri = in.readString();
        username = in.readString();
        role = in.readString();
        xp = in.readInt();
        ranking = in.readInt();
        species = in.readInt();
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public int getSpecies() {
        return species;
    }

    public void setSpecies(int species) {
        this.species = species;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profileImgUri);
        parcel.writeString(username);
        parcel.writeString(role);
        parcel.writeInt(xp);
        parcel.writeInt(ranking);
        parcel.writeInt(species);
    }
}
