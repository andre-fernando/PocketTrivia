package com.andre_fernando.pockettrivia.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.OpenTriviaHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Async Task to fetch daily trivia question.
 */
public class FetchDailyTriviaAsync extends AsyncTask<Void,Void,Question>{
    private final WeakReference<Context> weakContext;

    public FetchDailyTriviaAsync(Context weakContext) {
        this.weakContext = new WeakReference<>(weakContext);
    }

    @Override
    protected Question doInBackground(Void... voids) {
        OpenTriviaHelper helper = new OpenTriviaHelper(weakContext.get());
        ArrayList<Question> list = helper.getData(OpenTriviaHelper.dailyTriviaUrl);
        return list.get(0);
    }
}
