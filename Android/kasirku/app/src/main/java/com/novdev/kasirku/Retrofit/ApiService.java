package com.novdev.kasirku.Retrofit;

import com.google.gson.JsonObject;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.PurchaseItemModel;
import com.novdev.kasirku.Model.TopProduct;
import com.novdev.kasirku.Request.LoginRequest;
import com.novdev.kasirku.Request.OtpVerifyRequest;
import com.novdev.kasirku.Request.RegisterRequest;
import com.novdev.kasirku.Response.CustomerResponse;
import com.novdev.kasirku.Response.TopProductResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //    Auth Service
    @POST("auth/register")
    Call<JsonObject> register(@Body RegisterRequest request);
    @POST("auth/login")
    Call<JsonObject> login(@Body LoginRequest request);
    @POST("auth/verify-otp")
    Call<JsonObject> verifyOtp(@Body OtpVerifyRequest request);
    @POST("auth/verify-otp-login")
    Call<JsonObject> verifyOtpLogin(@Body OtpVerifyRequest request);

    //     Product Service
    @GET("products")
    Call<List<Product>> getProducts(@Header("Authorization") String token);
    @POST("products")
    @Multipart
    Call<JsonObject> createProduct(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("name") RequestBody name, @Part("sku") RequestBody sku,
                                   @Part("price") RequestBody price, @Part("stock_initial") RequestBody stockInit,
                                   @Part("stock_current") RequestBody stockCurrent);
    @GET("products/{id}")
    Call<JsonObject> getProduct(@Header("Authorization") String token, @Path("id") int id);
    @PUT("products/{id}")
    @Multipart
    Call<JsonObject> updateProduct(@Header("Authorization") String token, @Path("id") int id, @PartMap Map<String, RequestBody> partMap, @Part MultipartBody.Part image);
    @DELETE("products/{id}")
    Call<JsonObject> deleteProduct(@Header("Authorization") String token, @Path("id") int id);

    //    History
    @GET("purchase/history-items")
    Call<List<PurchaseItemModel>> getPurchaseItemHistory(@Header("Authorization") String token);
    @POST("purchases-history")
    Call<JsonObject> postPurchaseHistory(@Header("Authorization") String token, @Body Map<String, Object> body);

    //     Dashboard
    @GET("dashboard/summary")
    Call<JsonObject> getDashboardSummary(@Query("filter") String filter, @Header("Authorization") String token);
    @GET("top-products")
    Call<TopProductResponse> getTopProducts(@Header("Authorization") String token);

    //     Customer
    @GET("customers")
    Call<CustomerResponse> getCustomers(@Header("Authorization") String token);
    @GET("customer-purchases")
    Call<JsonObject> getCustomerPurchases(@Header("Authorization") String token);
    @FormUrlEncoded
    @POST("add-customer")
    Call<JsonObject> createCustomer(
            @Header("Authorization") String token,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("address") String address
    );

    //      Purchase
    @POST("purchases")
    Call<JsonObject> postPurchases(@Header("Authorization") String token, @Body Map<String, Object> body);
}
