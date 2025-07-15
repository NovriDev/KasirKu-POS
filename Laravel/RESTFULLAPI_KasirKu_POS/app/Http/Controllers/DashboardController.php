<?php

namespace App\Http\Controllers;

use App\Models\Purchase;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function summary(Request $request)
    {
        $filter = $request->query('filter', 'day');
        $query = Purchase::query();

        if ($filter === 'day') {
            $query->whereDate('created_at', today());
        } elseif ($filter === 'week') {
            $query->whereBetween('created_at', [now()->startOfWeek(), now()->endOfWeek()]);
        } elseif ($filter === 'month') {
            $query->whereMonth('created_at', now()->month);
        }

        $totalSales = $query->count();
        $totalIncome = $query->sum('total_amount');
        $activeCustomers = $query->distinct('customer_id')->count('customer_id');

        return response()->json([
            'total_sales' => $totalSales,
            'active_customers' => $activeCustomers,
            'income' => $totalIncome
        ]);
    }

    public function topProducts()
    {
        $topProducts = DB::table('purchase_items')
            ->join('products', 'purchase_items.product_id', '=', 'products.id')
            ->select('products.id', 'products.name', DB::raw('SUM(purchase_items.quantity) as total_sold'))
            ->groupBy('products.id', 'products.name')
            ->orderByDesc('total_sold')
            ->limit(3)
            ->get();

        return response()->json([
            'status' => true,
            'data' => $topProducts
        ]);
    }
}
