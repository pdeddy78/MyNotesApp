package com.example.latihan.mynotesapp;

import android.database.Cursor;

public interface LoadNotesCallback {
    void preExecute();
    void postExecute(Cursor notes);
}
