package dev.biogo.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private String lat;
    private String lng;
    private String imgUrl;
    private String specieName;
    private String evaluatedBy;
    private String ownerId;
    private String ownerName;
    private String classification;
    private String createdAt;

    public Photo(){}

    public Photo(String lat, String lng, String imgUrl, String specieName, String evaluatedBy, String ownerId, String ownerName, String classification, String createdAt) {
        this.lat = lat;
        this.lng = lng;
        this.imgUrl = imgUrl;
        this.specieName = specieName;
        this.evaluatedBy = evaluatedBy;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.classification = classification;
        this.createdAt = createdAt;
    }

    protected Photo(Parcel in) {
        lat = in.readString();
        lng = in.readString();
        imgUrl = in.readString();
        specieName = in.readString();
        evaluatedBy = in.readString();
        ownerId = in.readString();
        ownerName = in.readString();
        classification = in.readString();
        createdAt = in.readString();
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSpecieName() {
        return specieName;
    }

    public void setSpecieName(String specieName) {
        this.specieName = specieName;
    }

    public String getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(String evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", specieName='" + specieName + '\'' +
                ", evaluatedBy='" + evaluatedBy + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", classification='" + classification + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
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
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(imgUrl);
        parcel.writeString(specieName);
        parcel.writeString(evaluatedBy);
        parcel.writeString(ownerId);
        parcel.writeString(ownerName);
        parcel.writeString(classification);
        parcel.writeString(createdAt);
    }
}
