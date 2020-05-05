package com.example.covid.database;

import android.os.Parcel;
import android.os.Parcelable;

// A class to load entries of default database
public class DetailData implements Parcelable {

    private String country;
    private String state;
    private String dataDate;
    private String lastUpdate;
    private int confirmed;
    private int deaths;
    private int recovered;
    private double latitude;
    private double longitude;

    public DetailData() {

    }

    public DetailData(String country, String state, String dataDate, String lastUpdate, int confirmed, int deaths, int recovered, double latitude, double longitude) {
        this.country = country;
        this.state = state;
        this.dataDate = dataDate;
        this.lastUpdate = lastUpdate;
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return this.country;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public String getDataDate() {
        return this.dataDate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdate() {
        return this.lastUpdate;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getConfirmed() { return this.confirmed; }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLatitude() { return this.latitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLongitude() { return this.longitude; }

    public int getCurrent() {
        return this.confirmed - this.deaths - this.recovered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.country);
        dest.writeString(this.state);
        dest.writeString(this.dataDate);
        dest.writeString(this.lastUpdate);
        dest.writeInt(this.confirmed);
        dest.writeInt(this.deaths);
        dest.writeInt(this.recovered);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected DetailData(Parcel in) {
        this.country = in.readString();
        this.state = in.readString();
        this.dataDate = in.readString();
        this.lastUpdate = in.readString();
        this.confirmed = in.readInt();
        this.deaths = in.readInt();
        this.recovered = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<DetailData> CREATOR = new Creator<DetailData>() {  // 3
        @Override
        public DetailData createFromParcel(Parcel in) {
            return new DetailData(in);
        }

        @Override
        public DetailData[] newArray(int size) {
            return new DetailData[size];
        }
    };
}
