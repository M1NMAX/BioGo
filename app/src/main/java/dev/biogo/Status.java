package dev.biogo;

import android.os.Parcel;
import android.os.Parcelable;

public class Status implements Parcelable {
    private int medals, ranking, trophies, xp;

    public Status(){}

    public Status(int medals, int ranking, int trophies, int xp) {
        this.medals = medals;
        this.ranking = ranking;
        this.trophies = trophies;
        this.xp = xp;
    }

    protected Status(Parcel in) {
        medals = in.readInt();
        ranking = in.readInt();
        trophies = in.readInt();
        xp = in.readInt();
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

    public int getMedals() {
        return medals;
    }

    public int getRanking() {
        return ranking;
    }

    public int getTrophies() {
        return trophies;
    }

    public int getXp() {
        return xp;
    }

    @Override
    public String toString() {
        return "Status{" +
                "medals=" + medals +
                ", ranking=" + ranking +
                ", trophies=" + trophies +
                ", xp=" + xp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(medals);
        parcel.writeInt(ranking);
        parcel.writeInt(trophies);
        parcel.writeInt(xp);
    }
}
