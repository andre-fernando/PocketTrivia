package com.andre_fernando.pockettrivia.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.andre_fernando.pockettrivia.data.db.ResultsContentProvider;
import com.andre_fernando.pockettrivia.helpers.DbHelper;

import java.lang.ref.WeakReference;

/**
 * Async task for database communication.
 */
public class DbRequestAsync extends AsyncTask<Void,Void,Cursor> {
    private WeakReference<Context> weakContext;
    private int code, score_id;
    private ContentValues valuesToInsert;

    //region Constructors

    /**
     * The DbRequestAsync Task is used to send request to the
     * SqLite database in the background
     *
     * Constructor to be used when requesting or
     * deleting all the scores
     * @param code int from DbHelper that corresponds to a method
     * @param context Context is needed to call the content resolver
     */
    public DbRequestAsync(int code, Context context) {
        if (code == DbHelper.INSERT_RESULT || code == DbHelper.REMOVE_AT_ID){
            throw new IllegalArgumentException(" Code not applicable with this constructor");
        }
        this.code = code;
        weakContext = new WeakReference<>(context);
    }

    /**
     * The DbRequestAsync Task is used to send request to the
     * SqLite database in the background
     *
     * Constructor to be used when requesting to delete a specific
     * score at score_id
     * @param context Context is needed to call the content resolver
     * @param score_id Score id to be deleted
     */
    @SuppressWarnings("unused")
    public DbRequestAsync(Context context, int score_id) {
        this.code = DbHelper.REMOVE_AT_ID;
        this.score_id = score_id;
        weakContext = new WeakReference<>(context);
    }

    /**
     * The DbRequestAsync Task is used to send request to the
     * SqLite database in the background
     *
     * Constructor to be used when inserting content values into the
     * database.
     * @param context Context is needed to call the content resolver
     * @param valuesToInsert ContentValues to be inserted
     */
    public DbRequestAsync(Context context, ContentValues valuesToInsert) {
        this.code = DbHelper.INSERT_RESULT;
        this.valuesToInsert = valuesToInsert;
        weakContext = new WeakReference<>(context);
    }

    //endregion

    @Override
    protected Cursor doInBackground(Void... voids) {
        switch (code){
            case DbHelper.GET_ALL_RESULTS:
                return weakContext.get().getContentResolver().query(
                        ResultsContentProvider.BASE_CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

            case DbHelper.INSERT_RESULT:
                weakContext.get().getContentResolver().
                        insert(ResultsContentProvider.BASE_CONTENT_URI,valuesToInsert);
                return null;

            case DbHelper.REMOVE_ALL:
                weakContext.get().getContentResolver().delete(
                        ResultsContentProvider.BASE_CONTENT_URI,
                        null,
                        null
                );
                return null;

            case DbHelper.REMOVE_AT_ID:
                Uri uri = ResultsContentProvider.BASE_CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(score_id)).build();
                weakContext.get().getContentResolver().delete(
                        uri,
                        null,
                        null
                );
                return null;

            default:
                return null;
        }
    }
}
