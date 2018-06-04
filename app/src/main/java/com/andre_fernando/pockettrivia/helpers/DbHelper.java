package com.andre_fernando.pockettrivia.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.andre_fernando.pockettrivia.data.db.ResultsDB;
import com.andre_fernando.pockettrivia.data.objects.Result;
import com.andre_fernando.pockettrivia.utils.DbRequestAsync;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This is a helper class that interacts with the database
 * and the activities.
 */
public class DbHelper {
    private final Context context;//Context is used for the Content Resolver

    public static final int GET_ALL_RESULTS = 11;
    public static final int INSERT_RESULT = 12;
    public static final int REMOVE_ALL =13;
    public static final int REMOVE_AT_ID = 14;

    /**
     * Constructor
     * @param context Activity Context
     */
    public DbHelper(Context context) {
        this.context = context;
    }

    //region Public Methods

    public void insertResult(Result result){
        ContentValues values = new ContentValues();
        values.put(ResultsDB.DATE, result.getDate());
        values.put(ResultsDB.NAME, result.getName());
        values.put(ResultsDB.SCORE, result.getScore());
        values.put(ResultsDB.TOTAL_QUESTIONS, result.getTotalQuestions());
        values.put(ResultsDB.TIME, result.getTime());
        values.put(ResultsDB.CATEGORY, result.getCategory());
        values.put(ResultsDB.DIFFICULTY, result.getDifficulty());
        values.put(ResultsDB.TYPE, result.getType());

        new DbRequestAsync(context,values).execute();
    }

    public ArrayList<Result> getAllResults(){
        Cursor cursor;
        try {
            cursor = new DbRequestAsync(GET_ALL_RESULTS,context).execute().get();
            return convertCursor(cursor);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.e(DbHelper.class.getSimpleName(),
                    "getAllResults: Failed to procure high scores");
            return new ArrayList<>();
        }
    }

    public void removeAllResults(){
        new DbRequestAsync(REMOVE_ALL,context).execute();
    }

    //endregion Public Methods

    //region Private Methods

    private ArrayList<Result> convertCursor(Cursor cursor){
        int id, score, totalQuestions;
        String name, date, time, category, difficulty, type ;
        ArrayList<Result> toReturn = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                id = getInt(cursor, ResultsDB.ID);
                score = getInt(cursor, ResultsDB.SCORE);
                totalQuestions = getInt(cursor,ResultsDB.TOTAL_QUESTIONS);
                name = getString(cursor, ResultsDB.NAME);
                date = getString(cursor, ResultsDB.DATE);
                time = getString(cursor, ResultsDB.TIME);
                category = getString(cursor, ResultsDB.CATEGORY);
                difficulty = getString(cursor, ResultsDB.DIFFICULTY);
                type = getString(cursor, ResultsDB.TYPE);
                toReturn.add(new Result(id,score,totalQuestions, name, date,
                        time,category,difficulty,type));

            }while (cursor.moveToNext());
            cursor.close();
        }
        return toReturn;
    }

    private String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private int getInt(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    //endregion Private Methods
}
