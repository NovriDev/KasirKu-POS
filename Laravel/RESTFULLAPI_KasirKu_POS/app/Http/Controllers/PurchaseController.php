<?php

namespace App\Http\Controllers;

use App\Models\Product;
use App\Models\Purchase;
use App\Models\PurchaseItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class PurchaseController extends Controller
{
    public function store(Request $request)
{
    $request->validate([
        'customer_id' => 'nullable|exists:customers,id',
        'total_amount' => 'required|numeric',
        'payment_method' => 'required|string',
        'items' => 'required|array',
        'items.*.product_id' => 'required|exists:products,id',
        'items.*.quantity' => 'required|integer|min:1',
        'items.*.subtotal' => 'required|numeric',
    ]);

    DB::beginTransaction();
    try {
        $purchase = Purchase::create([
            'customer_id' => $request->customer_id,
            'total_amount' => $request->total_amount,
            'payment_method' => $request->payment_method,
        ]);

        foreach ($request->items as $item) {
            $purchase->items()->create($item);

            $product = Product::find($item['product_id']);
            $product->stock_current = $product->stock_current - $item['quantity'];
            $product->save();
        }

        DB::commit();
        return response()->json(['message' => 'Transaksi berhasil']);
    } catch (\Exception $e) {
        DB::rollBack();
        return response()->json(['message' => 'Gagal: ' . $e->getMessage()], 500);
    }
}

}
