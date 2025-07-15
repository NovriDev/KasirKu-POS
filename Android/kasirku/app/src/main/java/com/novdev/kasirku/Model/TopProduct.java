package com.novdev.kasirku.Model;

import com.google.gson.annotations.SerializedName;

public class TopProduct {
    private int id;
    private String name;
    @SerializedName("total_sold")
    private int totalSold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }
}