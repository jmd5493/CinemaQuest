package com.example.cinemaquest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class databasehelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "cinema_data.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public databasehelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public long insertUserInfo(String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRId = -1;

        db.beginTransaction();
        try
        {
            ContentValues val = new ContentValues();
            val.put(COLUMN_EMAIL, email);
            val.put(COLUMN_PASSWORD, password);
            newRId = db.insert(TABLE_NAME, null, val);
            if (newRId != -1)
            {
                db.setTransactionSuccessful();
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            db.endTransaction();
        }
        return newRId;
    }

    public Cursor getAllUsers()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getUserByEmailAndPassword(String userEmail, String userPassword)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL = ? AND PASSWORD = ?", new String[]{userEmail, userPassword});
    }
}