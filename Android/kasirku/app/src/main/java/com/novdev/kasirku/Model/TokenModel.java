package com.novdev.kasirku.Model;

public class TokenModel {
    private int id;
    private String token;
    private long createdAt;

    public TokenModel(int id, String token, long createdAt) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
