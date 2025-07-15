<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\CustomerController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\HistoryController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\PurchaseController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::prefix('auth')->controller(AuthController::class)->group(function () {
    Route::post('/register', 'register');
    Route::post('/verify-otp', 'verifyOtp');
    Route::post('/login', 'login');
    Route::post('/verify-otp-login', 'verifyOtpLogin');
});

Route::middleware('auth:sanctum')->group(function () {
    Route::apiResource('products', ProductController::class);
    Route::get('/purchase/history-items', [HistoryController::class, 'historyItems']);
    Route::get('/dashboard/summary', [DashboardController::class, 'summary']);
    Route::get('/customers', [CustomerController::class, 'index']);
    Route::get('/customer-purchases', [CustomerController::class, 'getCustomerPurchases']);
    Route::post('/purchases-history', [HistoryController::class, 'store']);
    Route::post('/purchases', [PurchaseController::class, 'store']);
    Route::post('/add-customer', [CustomerController::class, 'store']);
    Route::get('/top-products', [DashboardController::class, 'topProducts']);
});
