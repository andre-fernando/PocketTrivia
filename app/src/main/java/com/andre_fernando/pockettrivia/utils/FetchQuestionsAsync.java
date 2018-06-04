package com.andre_fernando.pockettrivia.utils;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.OpenTriviaHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * AsyncTaskLoader to fetch questions.
 */
public class FetchQuestionsAsync extends AsyncTaskLoader<ArrayList<Question>> {
    private final WeakReference<Context> weakReference;
    private final String urlString;

    public FetchQuestionsAsync(Context context, String urlString) {
        super(context);
        weakReference = new WeakReference<>(context);
        this.urlString= urlString;
    }

    @Override
    public ArrayList<Question> loadInBackground() {
        OpenTriviaHelper ApiHelper = new OpenTriviaHelper(weakReference.get());
        return ApiHelper.getData(urlString);
    }


}
