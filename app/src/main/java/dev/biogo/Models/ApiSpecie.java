package dev.biogo.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiSpecie implements Parcelable {

    private String id;
    private String specieName;
    private String specieImage;
    private String specieScientificName;
    private String points;
    private String description;

    public ApiSpecie() {
    }

    public ApiSpecie(String id, String specieName, String specieImage, String specieScientificName, String points) {
        this.id = id;
        this.specieName = specieName;
        this.specieImage = specieImage;
        this.specieScientificName = specieScientificName;
        this.points = points;
    }

    protected ApiSpecie(Parcel in) {
        id = in.readString();
        specieName = in.readString();
        specieImage = in.readString();
        specieScientificName = in.readString();
        points = in.readString();
        description = in.readString();
    }

    public static final Creator<ApiSpecie> CREATOR = new Creator<ApiSpecie>() {
        @Override
        public ApiSpecie createFromParcel(Parcel in) {
            return new ApiSpecie(in);
        }

        @Override
        public ApiSpecie[] newArray(int size) {
            return new ApiSpecie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(specieName);
        parcel.writeString(specieImage);
        parcel.writeString(specieScientificName);
        parcel.writeString(points);
        parcel.writeString(description);
    }

    public String getId() {
        return id;
    }

    public String getSpecieName() {
        return specieName;
    }

    public String getSpecieImage() {
        return specieImage;
    }

    public String getSpecieScientificName() {
        return specieScientificName;
    }

    public String getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }

    public static Creator<ApiSpecie> getCREATOR() {
        return CREATOR;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSpecieName(String specieName) {
        this.specieName = specieName;
    }

    public void setSpecieImage(String specieImage) {
        this.specieImage = specieImage;
    }

    public void setSpecieScientificName(String specieScientificName) {
        this.specieScientificName = specieScientificName;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
