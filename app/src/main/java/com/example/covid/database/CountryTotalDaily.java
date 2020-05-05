package com.example.covid.database;

// A class to load certain country's data on certain date
public class CountryTotalDaily {
    private int confirmed;
    private int deaths;
    private int recovered;
    private int current;
    private String date;

    public CountryTotalDaily() {

    }

    public CountryTotalDaily(int confirmed, int deaths, int recovered, int current, String date) {
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.current = current;
        this.date = date;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
