package dev.biogo.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiSpecie implements Parcelable {

    private String id;
    private String specieName;
    private String specieImage;
    private String points;

    public ApiSpecie(){}

    public ApiSpecie(String id, String specieName, String specieImage, String points) {
        this.id = id;
        this.specieName = specieName;
        this.specieImage = specieImage;
        this.points = points;
    }

    protected ApiSpecie(Parcel in) {
        id = in.readString();
        specieName = in.readString();
        specieImage = in.readString();
        points = in.readString();
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
        parcel.writeString(points);
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

    public String getPoints() {
        return points;
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

    public void setPoints(String points) {
        this.points = points;
    }
}
