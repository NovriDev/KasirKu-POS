package com.novdev.kasirku.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.novdev.kasirku.Model.TokenModel;

public class TokenDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "authToken.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tokens";

    public TokenDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, token TEXT, created_at INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveToken(String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // Hapus token lama
        ContentValues values = new ContentValues();
        values.put("token", token);
        values.put("created_at", System.currentTimeMillis());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public TokenModel getToken() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 1", null);
        TokenModel token = null;
        if (cursor.moveToFirst()) {
            token = new TokenModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2)
            );
        }
        cursor.close();
        db.close();
        return token;
    }

    public void deleteToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}

