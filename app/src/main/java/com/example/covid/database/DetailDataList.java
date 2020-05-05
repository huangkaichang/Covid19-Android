package com.example.covid.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//A class of list of DetailData objects
public class DetailDataList {
    private List<DetailData> detail;

    public DetailDataList() {

    }

    public DetailDataList(List<DetailData> detail) {
        this.detail = detail;
    }

    public List<DetailData> getData() {
        return detail != null ? detail : new ArrayList<DetailData>();
    }

    public int getConfirmed(String date) {
        int confirmed = 0;
        for (DetailData detailData : detail) {
            if (detailData.getDataDate().equals(date)) {
                confirmed += detailData.getConfirmed();
            }
        }
        return confirmed;
    }

    public int getDeath(String date) {
        int death = 0;
        for (DetailData detailData : detail) {
            if (detailData.getDataDate().equals(date)) {
                death += detailData.getDeaths();
            }
        }
        return death;
    }

    public int getRecovered(String date) {
        int recovered = 0;
        for (DetailData detailData : detail) {
            if (detailData.getDataDate().equals(date)) {
                recovered += detailData.getRecovered();
            }
        }
        return recovered;
    }

    public int getCurrent(String date) {
        return getConfirmed(date) - getDeath(date) - getRecovered(date);
    }

    // Given a date, return all countries' confirmed, deaths, recovered, current data
    public List<Country> getCountryList(String date) {
        //Result to be returned
        List<Country> countryObjectList = new ArrayList<>();

        Map<String, Map<String, List<Integer>>> country = new HashMap<>();
        for (DetailData detailData : detail) {
            if (detailData.getDataDate().equals(date)) {
                String curCountry = detailData.getCountry();
                String curState = detailData.getState();

                //Store the state data to a HashMap with the form of StateName: {confirmed, death, recovered, current}
                Map<String, List<Integer>> state = new HashMap<>();
                List<Integer> stateData = new ArrayList<>();
                stateData.add(detailData.getConfirmed());
                stateData.add(detailData.getDeaths());
                stateData.add(detailData.getRecovered());
                stateData.add(detailData.getCurrent());
                state.put(curState, stateData);
                if (!country.containsKey(curCountry)) {
                    country.put(curCountry, state);
                } else {
                    country.get(curCountry).put(curState, stateData);
                }
            }
        }

        //From hashmap, covert the data to list of Country object
        for (Map.Entry<String, Map<String, List<Integer>>> entry1: country.entrySet()) {
            String countryName = entry1.getKey();
            int country_confirmed = 0;
            int country_deaths = 0;
            int country_recovered = 0;
            int country_current = 0;
            List<State> stateObjectList = new ArrayList<>();
            Map<String, List<Integer>> stateMap = entry1.getValue();
            for(Map.Entry<String, List<Integer>> entry2: stateMap.entrySet()) {
                String stateName = entry2.getKey();
                int confirmed = entry2.getValue().get(0);
                country_confirmed += confirmed;
                int deaths = entry2.getValue().get(1);
                country_deaths += deaths;
                int recovered = entry2.getValue().get(2);
                country_recovered += recovered;
                int current = entry2.getValue().get(3);
                country_current += current;
                State stateObject = new State(stateName, date, confirmed, deaths, recovered, current);
                stateObjectList.add(stateObject);
            }
            Country countryObject = new Country(countryName, country_confirmed, country_deaths, country_recovered, country_current, stateObjectList);
            countryObjectList.add(countryObject);
        }
        Collections.sort(countryObjectList);
        return countryObjectList;
    }

    public List<State> getStateList(String country, String state) {
        List<State> stateObjectList = new ArrayList<>();
        for (DetailData detailData: detail) {
            if (detailData.getCountry().equals(country) && detailData.getState().equals(state)) {
                stateObjectList.add(new State(detailData.getState(), detailData.getDataDate(), detailData.getConfirmed(), detailData.getDeaths(), detailData.getRecovered(), detailData.getCurrent()));
            }
        }
        return stateObjectList;
    }

    public List<Integer> getDailyWorldConfirmedList(String[] dateList) {
        List<Integer> dailyWorldConfirmedList = new ArrayList<>();
        for (String date: dateList) {
            dailyWorldConfirmedList.add(getConfirmed(date));
        }
        return dailyWorldConfirmedList;
    }

    public List<Integer> getDailyWorldDeathsList(String[] dateList) {
        List<Integer> dailyWorldDeathsList = new ArrayList<>();
        for (String date: dateList) {
            dailyWorldDeathsList.add(getDeath(date));
        }
        return dailyWorldDeathsList;
    }

    public List<Integer> getDailyWorldRecoveredList(String[] dateList) {
        List<Integer> dailyWorldRecoveredList = new ArrayList<>();
        for (String date: dateList) {
            dailyWorldRecoveredList.add(getRecovered(date));
        }
        return dailyWorldRecoveredList;
    }

    public List<Integer> getDailyWorldCurrentList(String[] dateList) {
        List<Integer> dailyWorldCurrentList = new ArrayList<>();
        for (String date: dateList) {
            dailyWorldCurrentList.add(getCurrent(date));
        }
        return dailyWorldCurrentList;
    }

    public List<Integer> getDataOnDate(String date) {
        List<Integer> dataOnDateList = new ArrayList<>();
        int confirmed = 0;
        int deaths = 0;
        int recovered = 0;
        int current = 0;
        for (DetailData detailData : detail) {
            if (detailData.getDataDate().equals(date)) {
                confirmed += detailData.getConfirmed();
                deaths += detailData.getDeaths();
                recovered += detailData.getRecovered();
                current += detailData.getCurrent();
            }
        }
        dataOnDateList.add(confirmed);
        dataOnDateList.add(deaths);
        dataOnDateList.add(recovered);
        dataOnDateList.add(current);
        return dataOnDateList;
    }
}
