package com.sehat.tracker.models;

public class StateData {

    public String StateName;
    public String TotalCases;
    public String TotalDeaths;
    public String TotalRecovered;
    public String ActiveCases;
    public String NewCases;
    public String NewRecovered;
    public String NewDeaths;

    public StateData(String stateName, String totalCases, String totalDeaths, String totalRecovered, String activeCases, String newCases, String newRecovered, String newDeaths) {
        StateName = stateName;
        TotalCases = totalCases;
        TotalDeaths = totalDeaths;
        TotalRecovered = totalRecovered;
        ActiveCases = activeCases;
        NewCases = newCases;
        NewRecovered = newRecovered;
        NewDeaths = newDeaths;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
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

    public String getNewCases() {
        return NewCases;
    }

    public void setNewCases(String newCases) {
        NewCases = newCases;
    }

    public String getNewRecovered() {
        return NewRecovered;
    }

    public void setNewRecovered(String newRecovered) {
        NewRecovered = newRecovered;
    }

    public String getNewDeaths() {
        return NewDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        NewDeaths = newDeaths;
    }
}
