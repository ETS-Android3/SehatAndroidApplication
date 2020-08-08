package com.sehat.tracker.models;

public class AllStats {

    private String totalValue;


    private String increase;

    public AllStats(String totalValue, String increase) {
        this.totalValue = totalValue;
        this.increase = increase;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getIncrease() {
        return increase;
    }

    public void setIncrease(String increase) {
        this.increase = increase;
    }
}
