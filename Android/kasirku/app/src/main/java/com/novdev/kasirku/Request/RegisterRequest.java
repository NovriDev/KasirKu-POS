package com.novdev.kasirku.Request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    @SerializedName("password_confirmation")
    private String password_confirmation;

    public RegisterRequest(String name, String email, String password, String password_confirmation) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }
}
