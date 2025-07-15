<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Purchase extends Model
{
    protected $fillable = ['customer_id', 'total_amount', 'payment_method'];

    public function items()
    {
        return $this->hasMany(PurchaseItem::class);
    }


    public function customer()
    {
        return $this->belongsTo(Customer::class);
    }
}
