package dev.biogo;

import android.net.Uri;

public class User {
    private String profileImgUri;
    private String username;
    private Status status;

    public User (){}

    public User(String profileImgUri, String username, Status status) {
        this.profileImgUri = profileImgUri;
        this.username = username;
        this.status = status;
    }

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


}
