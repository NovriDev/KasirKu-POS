package com.novdev.kasirku.Model;

public class Customer {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private int totalPurchases;

    public Customer(int id, String name, int totalPurchases) {
        this.id = id;
        this.name = name;
        this.totalPurchases = totalPurchases;
    }

    // Getter & Setter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public int getTotalPurchases() {
        return totalPurchases;
    }
}
