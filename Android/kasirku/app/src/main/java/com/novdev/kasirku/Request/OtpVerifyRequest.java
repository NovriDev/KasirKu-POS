package com.novdev.kasirku.Request;

public class OtpVerifyRequest {
    private String email;
    private String otp;

    public OtpVerifyRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
