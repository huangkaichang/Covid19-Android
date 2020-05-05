package com.example.covid.database;

import android.os.Parcel;
import android.os.Parcelable;

public class State implements Comparable<State>, Parcelable {
    private String state;
    private String date;
    private int confirmed;
    private int deaths;
    private int recovered;
    private int current;

    public State() {

    }

    public State(String state, String date, int confirmed, int deaths, int recovered, int current) {
        this.state = state;
        this.date = date;
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.current = current;
    }

    public String getState() { return this.state; }

    public String getDate() { return this.date; }

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

    public int compareTo(State state) {
        int result = state.getConfirmed() - this.confirmed;
        if (0 == result) {
        result = state.getCurrent() - this.current;
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.state);
        dest.writeString(this.date);
        dest.writeInt(this.confirmed);
        dest.writeInt(this.deaths);
        dest.writeInt(this.recovered);
        dest.writeInt(this.current);
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };

    private State(Parcel in) {
        this.state = in.readString();
        this.date = in.readString();
        this.confirmed = in.readInt();
        this.deaths = in.readInt();
        this.recovered = in.readInt();
        this.current = in.readInt();
    }
}
