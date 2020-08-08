package com.sehat.tracker.models;

public class TabData {

    public String CountryName;
    public String TotalCases;
    public String TotalDeaths;
    public String TotalRecovered;
    public String ActiveCases;
    public String NewCases;
    public String NewDeaths;


    public TabData(String countryName, String totalCases, String totalDeaths, String totalRecovered, String activeCases, String newCases, String newDeaths) {
        CountryName = countryName;
        TotalCases = totalCases;
        TotalDeaths = totalDeaths;
        TotalRecovered = totalRecovered;
        ActiveCases = activeCases;
        NewCases = newCases;
        NewDeaths = newDeaths;
    }

    public String getNewCases() {
        return NewCases;
    }

    public void setNewCases(String newCases) {
        NewCases = newCases;
    }

    public String getNewDeaths() {
        return NewDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        NewDeaths = newDeaths;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getTotalCases() {
        return TotalCases;
    }

    public void setTotalCases(String totalCases) {
        TotalCases = totalCases;
    }

    public String getTotalDeaths() {
        return TotalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        TotalDeaths = totalDeaths;
    }

    public String getTotalRecovered() {
        return TotalRecovered;
    }

    public void setTotalRecovered(String totalRecovered) {
        TotalRecovered = totalRecovered;
    }

    public String getActiveCases() {
        return ActiveCases;
    }

    public void setActiveCases(String activeCases) {
        ActiveCases = activeCases;
    }
}
