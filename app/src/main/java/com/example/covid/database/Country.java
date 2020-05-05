package com.example.covid.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

// Country classloader,
public class Country implements Comparable<Country>, Parcelable {
    private String country;
    private int confirmed;
    private int deaths;
    private int recovered;
    private int current;
    private List<State> stateList;

    public Country() {

    }

    public Country(String country, int confirmed, int deaths, int recovered, int current, List<State> stateList) {
        this.country = country;
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.current = current;
        this.stateList = stateList;
    }

    public String getCountry() {
        return this.country;
    }

    public int getConfirmed() {
        return this.confirmed;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getRecovered() {
        return this.recovered;
    }

    public int getCurrent() {
        return this.current;
    }

    public List<State> getStateList() {
        return this.stateList;
    }

    public int compareTo(Country country) {
        int result = country.getConfirmed() - this.confirmed;
        if (0 == result) {
            result = country.getCurrent() - this.current;
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.country);
        dest.writeInt(this.confirmed);
        dest.writeInt(this.deaths);
        dest.writeInt(this.recovered);
        dest.writeInt(this.current);
        dest.writeTypedList(this.stateList);
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    private Country(Parcel in) {
        this.country = in.readString();
        this.confirmed = in.readInt();
        this.deaths = in.readInt();
        this.recovered = in.readInt();
        this.current = in.readInt();
        stateList = new ArrayList<>();

//        in.readTypedList(stateList, State.CREATOR);
        this.stateList = in.createTypedArrayList(State.CREATOR);
    }
}
