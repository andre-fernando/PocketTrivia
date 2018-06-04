package com.andre_fernando.pockettrivia.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ResultsDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ResultsDB";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Results";
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String NAME = "name";
    public static final String SCORE = "score";
    public static final String TOTAL_QUESTIONS = "total_questions";
    public static final String TIME = "time";
    public static final String CATEGORY = "category";
    public static final String DIFFICULTY = "difficulty";
    public static final String TYPE = "type";


    ResultsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createTableQuery =
    "CREATE TABLE "+TABLE_NAME+         "("+
                    ID +                " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    DATE+               " TEXT,"+
                    NAME +              " TEXT,"+
                    SCORE +             " INTEGER,"+
                    TOTAL_QUESTIONS +   " INTEGER,"+
                    TIME +              " TEXT,"+
                    CATEGORY +          " TEXT,"+
                    DIFFICULTY +        " TEXT,"+
                    TYPE +              " TEXT)";

        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
