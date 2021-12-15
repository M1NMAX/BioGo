package dev.biogo;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {
    private String lat;
    private String lng;
    private String imgUrl;
    private String specieName;
    private String evaluatedBy;
    private ClassificationEnum classification;

    public ImageModel(){}

    public ImageModel(String lat, String lng, String imgUrl, String specieName, String evaluatedBy, ClassificationEnum classification) {
        this.lat = lat;
        this.lng = lng;
        this.imgUrl = imgUrl;
        this.specieName = specieName;
        this.evaluatedBy = evaluatedBy;
        this.classification = classification;
    }

    protected ImageModel(Parcel in) {
        lat = in.readString();
        lng = in.readString();
        imgUrl = in.readString();
        specieName = in.readString();
        evaluatedBy = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

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

    public ClassificationEnum getClassification() {
        return classification;
    }

    public void setClassification(ClassificationEnum classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", specieName='" + specieName + '\'' +
                ", evaluatedBy='" + evaluatedBy + '\'' +
                ", classification=" + classification +
                '}';
    }

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
    }
}
