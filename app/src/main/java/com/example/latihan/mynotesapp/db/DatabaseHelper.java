package com.example.latihan.mynotesapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns._ID;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.DATE;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.TITLE;
import static com.example.latihan.mynotesapp.db.DatabaseContract.NoteColumns.TABLE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "dbnoteapp";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_NOTE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            TABLE_NAME,
            _ID,
            TITLE,
            DESCRIPTION,
            DATE
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
