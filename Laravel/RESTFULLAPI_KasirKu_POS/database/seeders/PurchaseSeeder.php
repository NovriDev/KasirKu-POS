<?php

namespace Database\Seeders;

use App\Models\Purchase;
use App\Models\PurchaseItem;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class PurchaseSeeder extends Seeder
{
    public function run()
    {
        $purchase = Purchase::create([
            'customer_id' => 1,  // "Umum"
            'total_amount' => 35000,
            'payment_method' => 'CASH',
        ]);

        $purchase2 = Purchase::create([
            'customer_id' => 2,
            'total_amount' => 35000,
            'payment_method' => 'CASH',
        ]);

        PurchaseItem::create([
            'purchase_id' => $purchase->id,
            'product_id' => 1,  // Kopi Hitam
            'quantity' => 1,
            'subtotal' => 15000,
        ]);

        PurchaseItem::create([
            'purchase_id' => $purchase2->id,
            'product_id' => 2,  // Teh Manis
            'quantity' => 2,
            'subtotal' => 20000,
        ]);
    }
}
