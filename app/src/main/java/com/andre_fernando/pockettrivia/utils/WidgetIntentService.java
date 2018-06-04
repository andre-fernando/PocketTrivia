package com.andre_fernando.pockettrivia.utils;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.objects.BooleanQuestion;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.andre_fernando.pockettrivia.ui.DailyTriviaWidget;

/**
 * This is an Intent Service that handles click events from the
 * daily trivia widget.
 */
public class WidgetIntentService extends IntentService {

    public static final String ACTION_CHECK_MULTIPLE = "check option" ;
    public static final String ACTION_CHECK_TRUE = "check true";
    public static final String ACTION_CHECK_FALSE = "check false";
    public static final String ACTION_UPDATE = "update";

    public static final String PARAM_USER_ANSWER = "answer";


    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String ans;
            if (action != null) {
                switch (action){
                    case ACTION_CHECK_MULTIPLE:
                        ans = intent.getStringExtra(PARAM_USER_ANSWER);
                        checkMultiple(ans);
                        break;

                    case ACTION_CHECK_TRUE:
                        checkBoolean(true);
                        break;

                    case ACTION_CHECK_FALSE:
                        checkBoolean(false);
                        break;

                    case ACTION_UPDATE:
                        updateAllWidgets();
                        break;
                }
            }
        }
    }

    //region Helper Methods
    private void checkMultiple(String userSelection){
        SharedPreferenceHelper helper = new SharedPreferenceHelper(getBaseContext());
        Question question=helper.getDailyTrivia();
        ((MultipleChoiceQuestion) question).checkAns(userSelection);
        helper.setDailyTrivia(question);
        updateAllWidgets();
    }

    private void checkBoolean(boolean userSelection){
        SharedPreferenceHelper helper = new SharedPreferenceHelper(getBaseContext());
        Question question=helper.getDailyTrivia();
        ((BooleanQuestion) question).checkAnswer(userSelection);
        helper.setDailyTrivia(question);
        updateAllWidgets();
    }


    /**
     * updates all the widgets
     */
    private void updateAllWidgets(){
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(this,DailyTriviaWidget.class));
        for (int i: appWidgetIds){
            widgetManager.notifyAppWidgetViewDataChanged(i, R.id.layout_multiple_options_widget);
            DailyTriviaWidget.updateAppWidget(this,widgetManager,i);
        }
    }
    //endregion Helper Methods

}
