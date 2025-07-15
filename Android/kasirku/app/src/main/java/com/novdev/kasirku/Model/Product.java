package com.novdev.kasirku.Model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String image;
    private String name;
    private String sku;
    private double price;
    private int stock_initial;
    private int stock_current;
    private int stockCount = 1;

    // Constructor, Getter & Setter
    public Product(int id, String name, double price, int stockCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockCount = stockCount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return image;
    }

    public void setImageUrl(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock_initial() {
        return stock_initial;
    }

    public void setStock_initial(int stock_initial) {
        this.stock_initial = stock_initial;
    }

    public int getStock_current() {
        return stock_current;
    }

    public void setStock_current(int stock_current) {
        this.stock_current = stock_current;
    }

    public int getStockCount() { return stockCount; }

    public void setStockCount(int stockCount) { this.stockCount = stockCount; }
}
