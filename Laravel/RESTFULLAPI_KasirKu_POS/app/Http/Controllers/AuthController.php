<?php

namespace App\Http\Controllers;

use App\Mail\OTPSent;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $valid = Validator::make($request->all(), [
            'name' => 'required|string',
            'email' => 'required|email|unique:users',
            'password' => 'required|string|min:8|confirmed',
        ]);

        if ($valid->fails()) {
            return response()->json(['errors' => $valid->errors()], 422);
        }

        $otp = rand(100000, 999999);

        // Buat Admin dengan OTP untuk verifikasi register
        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'otp' => $otp,
            'otp_expires_at' => now()->addMinutes(10),
        ]);

        // Kirim OTP ke Email
        Mail::to($user->email)->send(new OTPSent($otp));

        return response()->json(['message' => 'Registrasi berhasil, cek email untuk OTP']);
    }

    public function verifyOtp(Request $request)
    {
        $user = User::where('email', $request->email)->first();

        if (!$user || $user->otp !== $request->otp) {
            return response()->json(['message' => 'OTP salah'], 400);
        }

        if (now()->gt(Carbon::parse($user->otp_expires_at))) {
            return response()->json(['message' => 'OTP kadaluarsa'], 400);
        }

        $user->otp = null;
        $user->otp_expires_at = null;
        $user->save();

        $token = $user->createToken('api-token')->plainTextToken;
        return response()->json(['token' => $token, 'user' => $user]);
    }

    public function login(Request $request)
    {
        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return response()->json(['message' => 'Login gagal'], 401);
        }

        // Buat OTP Baru untuk verifikasi login
        $otp = rand(100000, 999999);
        $user->otp = $otp;
        $user->otp_expires_at = now()->addMinutes(10);
        $user->save();

        // Kirim OTP ke Email
        Mail::to($user->email)->send(new OTPSent($otp));

        return response()->json(['message' => 'OTP dikirim, silakan verifikasi']);
    }

    public function verifyOtpLogin(Request $request)
    {
        $user = User::where('email', $request->email)->first();

        if (!$user || $user->otp !== $request->otp) {
            return response()->json(['message' => 'OTP salah'], 400);
        }

        if (now()->gt(Carbon::parse($user->otp_expires_at))) {
            return response()->json(['message' => 'OTP kadaluarsa'], 400);
        }

        // Clear OTP setelah verifikasi
        $user->otp = null;
        $user->otp_expires_at = null;
        $user->save();

        // Kirim Token API
        $token = $user->createToken('api-token')->plainTextToken;
        return response()->json(['token' => $token, 'user' => $user]);
    }
}
