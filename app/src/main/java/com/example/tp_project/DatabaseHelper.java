package com.example.tp_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// DatabaseHelper class
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameHistory.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYER_1 = "player1";
    public static final String COLUMN_PLAYER_2 = "player2";
    public static final String COLUMN_WINNER = "winner";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYER_1 + " TEXT, " +
                COLUMN_PLAYER_2 + " TEXT, " +
                COLUMN_WINNER + " TEXT)";
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error creating database table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error upgrading database", e);
        }
    }

    public void insertGame(String player1, String player2, String winner) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLAYER_1, player1);
            values.put(COLUMN_PLAYER_2, player2);
            values.put(COLUMN_WINNER, winner);

            long result = db.insert(TABLE_NAME, null, values);
            if (result == -1) {
                Log.e("DatabaseHelper", "Error inserting game data");
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error inserting game into database", e);
        }
    }

    // Retrieve game history
    public Cursor getHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error querying game history", e);
            return null;
        }
    }
    public boolean deleteGame(int gameId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(gameId)});
            return result > 0;
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error deleting game", e);
            return false;
        }
    }


    public boolean updateGameWinner(int gameId, String newWinner) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WINNER, newWinner);

        try {
            int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(gameId)});
            return result > 0;
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error updating game winner", e);
            return false;
        }
    }
    @Override
    public synchronized void close() {

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error closing database", e);
        }
        super.close();
    }
}
