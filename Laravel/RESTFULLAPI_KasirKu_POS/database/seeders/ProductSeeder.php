<?php

namespace Database\Seeders;

use App\Models\Product;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class ProductSeeder extends Seeder
{
    public function run(): void
    {
        Product::insert([
            [
                'name' => 'Minuman Botol Teh',
                'sku' => 'MB1002',
                'price' => 5000,
                'stock_initial' => 100,
                'stock_current' => 100,
                'image' => null,
            ],
            [
                'name' => 'Minuman Botol Pocari',
                'sku' => 'MB1003',
                'price' => 5000,
                'stock_initial' => 100,
                'stock_current' => 100,
                'image' => null,
            ],

        ]);
    }
}
