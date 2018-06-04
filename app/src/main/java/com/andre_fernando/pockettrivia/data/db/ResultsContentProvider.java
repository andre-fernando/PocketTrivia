package com.andre_fernando.pockettrivia.data.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ResultsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.andre_fernando.pockettrivia";

    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://"+AUTHORITY+"/"+ ResultsDB.TABLE_NAME);

    private static final int CODE_RESULTS_ALL = 400;
    private static final int CODE_RESULT_AT_ID = 401;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,ResultsDB.TABLE_NAME, CODE_RESULTS_ALL);
        uriMatcher.addURI(AUTHORITY,ResultsDB.TABLE_NAME+"/*", CODE_RESULT_AT_ID);
        return uriMatcher;
    }

    private ResultsDB resultsdb;

    @Override
    public boolean onCreate() {
        resultsdb = new ResultsDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = resultsdb.getReadableDatabase();
        switch (uriMatcher.match(uri)){
            case CODE_RESULTS_ALL:
                return db.rawQuery("SELECT * FROM "+ResultsDB.TABLE_NAME,null);

            case CODE_RESULT_AT_ID:
                String id = uri.getPathSegments().get(1);
                String query = "SELECT * FROM "+ResultsDB.TABLE_NAME
                                +"WHERE "+ResultsDB.ID+"="+id;
                return db.rawQuery(query,null);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case CODE_RESULTS_ALL:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ResultsDB.TABLE_NAME;

            case CODE_RESULT_AT_ID:
                String id = uri.getPathSegments().get(1);
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ResultsDB.TABLE_NAME
                        +"."+id;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = resultsdb.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case CODE_RESULTS_ALL:
                long id = db.insert(ResultsDB.TABLE_NAME, null, values);
                if (id>0){
                    return ContentUris.withAppendedId(BASE_CONTENT_URI,id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

            case CODE_RESULT_AT_ID:
                throw new UnsupportedOperationException("Not allowed to insert in a specific spot: " + uri);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = resultsdb.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case CODE_RESULTS_ALL:
                return db.delete(ResultsDB.TABLE_NAME,null,null);

            case CODE_RESULT_AT_ID:
                String id = uri.getPathSegments().get(1);
                return db.delete(ResultsDB.TABLE_NAME,ResultsDB.ID +"="+id,null);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not allowed: " + uri);
    }
}
