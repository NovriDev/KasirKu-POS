<?php

namespace App\Http\Controllers;

use App\Models\Purchase;
use App\Models\PurchaseItem;
use Illuminate\Http\Request;

class HistoryController extends Controller
{
    public function historyItems()
    {
        $items = PurchaseItem::with('purchase.customer', 'product')->get();

        return $items->map(function ($item) {
            return [
                'product_name' => $item->product->name,
                'subtotal' => $item->subtotal,
                'quantity' => $item->quantity,
                'customer_name' => $item->purchase->customer->name ?? 'Umum',
            ];
        });
    }

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

        $purchase = Purchase::create([
            'customer_id' => $request->customer_id,
            'total_amount' => $request->total_amount,
            'payment_method' => $request->payment_method,
        ]);

        foreach ($request->items as $item) {
            $purchase->items()->create([
                'product_id' => $item['product_id'],
                'quantity' => $item['quantity'],
                'subtotal' => $item['subtotal'],
            ]);
        }

        return response()->json(['message' => 'Pembayaran berhasil', 'purchase' => $purchase]);
    }
}
