package dev.biogo;

public class ImageModel {
    public enum Classification {
        VALID,
        PENDING,
        NOT_VALID,
    }
    private String lat;
    private String lng;
    private String imgUrl;
    private String specieName;
    private Classification classification;

    public ImageModel(){}

    public ImageModel( String lat, String lng, String imgUrl, String specieName, Classification classification) {
        this.lat = lat;
        this.lng = lng;
        this.imgUrl = imgUrl;
        this.specieName = specieName;
        this.classification = classification;
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

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", specieName='" + specieName + '\'' +
                ", classification=" + classification +
                '}';
    }
}
