<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class AdminSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('users')->insert([
            [
                'name' => 'NovriMulia',
                'email' => 'novri.mulia123455@gmail.com',
                'password' => Hash::make('password123'),
                'created_at' => now(),
            ]
        ]);
    }
}
