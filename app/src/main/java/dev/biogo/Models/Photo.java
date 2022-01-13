package dev.biogo.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.Exclude;

import java.text.Normalizer;

public class Photo implements Parcelable {
    @Exclude
    private String id;
    private String lat;
    private String lng;
    private String imgUrl;
    private String specieName;
    private String specieNameLower;
    private String evaluatedBy;
    private String ownerId;
    private String ownerName;
    private String classification;
    private String createdAt;
    private String userProfilePic;

    public Photo(){}

    public Photo(String lat, String lng, String imgUrl, String specieName, String specieNameLower, String evaluatedBy, String ownerId, String ownerName, String classification, String createdAt, String userProfilePic) {
        this.lat = lat;
        this.lng = lng;
        this.imgUrl = imgUrl;
        this.specieName = specieName;
        this.specieNameLower = specieNameLower;
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
        specieNameLower = in.readString();
        evaluatedBy = in.readString();
        ownerId = in.readString();
        ownerName = in.readString();
        classification = in.readString();
        createdAt = in.readString();
        userProfilePic = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(imgUrl);
        parcel.writeString(specieName);
        parcel.writeString(specieNameLower);
        parcel.writeString(evaluatedBy);
        parcel.writeString(ownerId);
        parcel.writeString(ownerName);
        parcel.writeString(classification);
        parcel.writeString(createdAt);
        parcel.writeString(userProfilePic);
    }

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

    public String getSpecieNameLower() {
        return specieNameLower;
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

    public void setSpecieNameLower(String specieNameLower) {
        this.specieNameLower = specieNameLower;
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
