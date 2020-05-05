package com.example.covid.database;

import java.util.List;

// A class to load certain country's data on all date
public class CountryTotalDailyList {
    private List<CountryTotalDaily> countryTotalDailyList;

    public CountryTotalDailyList() {

    }

    public CountryTotalDailyList(List<CountryTotalDaily> countryTotalDailyList) {
        this.countryTotalDailyList = countryTotalDailyList;
    }

    public List<CountryTotalDaily> getCountryTotalDailyList() {
        return countryTotalDailyList;
    }

    public void setCountryTotalDailyList(List<CountryTotalDaily> countryTotalDailyList) {
        this.countryTotalDailyList = countryTotalDailyList;
    }

    // return this country's data on given date
    public CountryTotalDaily getCountryTotalDailyOnDate(String date) {
        for (CountryTotalDaily countryTotalDaily: countryTotalDailyList) {
            if (countryTotalDaily.getDate().equals(date)) {
                return countryTotalDaily;
            }
        }
        return null;
    }
}
