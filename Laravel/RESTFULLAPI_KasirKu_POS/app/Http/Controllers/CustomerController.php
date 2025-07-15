<?php

namespace App\Http\Controllers;

use App\Models\Customer;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class CustomerController extends Controller
{
    public function index()
    {
        $customers = Customer::select('id', 'name')->get();

        return response()->json([
            'success' => true,
            'data' => $customers
        ]);
    }

    public function getCustomerPurchases()
    {
        $customers = Customer::withCount('purchases')->get();

        return response()->json([
            'message' => 'Berhasil ambil data',
            'data' => $customers->map(function ($c) {
                return [
                    'id' => $c->id,
                    'name' => $c->name,
                    'total_purchases' => $c->purchases_count,
                ];
            }),
        ]);
    }

    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name'    => 'required|string|max:255',
            'email'   => 'nullable|email|unique:customers,email',
            'phone'   => 'nullable|string|max:20',
            'address' => 'nullable|string|max:255',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validasi gagal',
                'errors'  => $validator->errors(),
            ], 422);
        }

        $customer = Customer::create([
            'name'    => $request->name,
            'email'   => $request->email,
            'phone'   => $request->phone,
            'address' => $request->address,
        ]);

        return response()->json([
            'message' => 'Customer berhasil ditambahkan',
            'data'    => $customer,
        ], 201);
    }
}
