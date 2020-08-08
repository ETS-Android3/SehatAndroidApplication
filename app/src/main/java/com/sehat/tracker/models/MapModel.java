package com.sehat.tracker.models;

public class MapModel {
    public String latitude;
    public String longitude;
    public String condition;
    public String value;
    public String name;

    public MapModel(String latitude, String longitude, String condition, String value, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name=name;
        this.value = value;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCondition() {
        return condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}