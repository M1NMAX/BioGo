package dev.biogo;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String profileImgUri;
    private String username;
    private Status status;

    public User (){}

    public User(String profileImgUri, String username, Status status) {
        this.profileImgUri = profileImgUri;
        this.username = username;
        this.status = status;
    }

    protected User(Parcel in) {
        profileImgUri = in.readString();
        username = in.readString();
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

    public void setProfileImgUri (String profileImgUri) {
        this.profileImgUri = profileImgUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "profileImgUri='" + profileImgUri + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profileImgUri);
        parcel.writeString(username);
    }
}
