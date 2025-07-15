package com.novdev.kasirku.Response;

import com.novdev.kasirku.Model.TopProduct;

import java.util.List;

public class TopProductResponse {
    private boolean status;
    private List<TopProduct> data;

    public boolean isStatus() {
        return status;
    }

    public List<TopProduct> getData() {
        return data;
    }
}
