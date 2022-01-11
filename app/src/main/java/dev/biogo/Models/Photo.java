package dev.biogo.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Photo implements Parcelable {
    @Exclude
    private String id;
    private String lat;
    private String lng;
    private String imgUrl;
    private String specieName;
    private String evaluatedBy;
    private String ownerId;
    private String ownerName;
    private String classification;
    private String createdAt;
    private String userProfilePic;

    public Photo(){}

    public Photo(String lat, String lng, String imgUrl, String specieName, String evaluatedBy, String ownerId, String ownerName, String classification, String createdAt, String userProfilePic) {
        this.lat = lat;
        this.lng = lng;
        this.imgUrl = imgUrl;
        this.specieName = specieName;
        this.evaluatedBy = evaluatedBy;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.classification = classification;
        this.createdAt = createdAt;
        this.userProfilePic = userProfilePic;
    }

    protected Photo(Parcel in) {
        id = in.readString();
        lat = in.readString();
        lng = in.readString();
        imgUrl = in.readString();
        specieName = in.readString();
        evaluatedBy = in.readString();
        ownerId = in.readString();
        ownerName = in.readString();
        classification = in.readString();
        createdAt = in.readString();
        userProfilePic = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(imgUrl);
        dest.writeString(specieName);
        dest.writeString(evaluatedBy);
        dest.writeString(ownerId);
        dest.writeString(ownerName);
        dest.writeString(classification);
        dest.writeString(createdAt);
        dest.writeString(userProfilePic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getSpecieName() {
        return specieName;
    }

    public String getEvaluatedBy() {
        return evaluatedBy;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getClassification() {
        return classification;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public static Creator<Photo> getCREATOR() {
        return CREATOR;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setSpecieName(String specieName) {
        this.specieName = specieName;
    }

    public void setEvaluatedBy(String evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }
}
