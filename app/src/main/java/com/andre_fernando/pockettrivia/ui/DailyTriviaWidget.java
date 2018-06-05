package com.andre_fernando.pockettrivia.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.objects.BooleanQuestion;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.andre_fernando.pockettrivia.utils.DailyTriviaJobDispatcher;
import com.andre_fernando.pockettrivia.utils.FetchDailyTriviaAsync;
import com.andre_fernando.pockettrivia.utils.NetworkCheck;
import com.andre_fernando.pockettrivia.utils.WidgetAdapterService;
import com.andre_fernando.pockettrivia.utils.WidgetIntentService;

import java.util.concurrent.ExecutionException;

/**
 * Implementation of App Widget functionality.
 */
public class DailyTriviaWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.daily_trivia_widget);

        SharedPreferenceHelper helper = new SharedPreferenceHelper(context);

        if (!helper.isInitialized()){//If the user opens the widget before the app.
            if (NetworkCheck.isConnected(context)){
                try {
                    Question initDailyQuestion = new FetchDailyTriviaAsync(context).get();
                    helper.setDailyTrivia(initDailyQuestion);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
                return;
            }
            DailyTriviaJobDispatcher.init(context);
        }

        Question question = helper.getDailyTrivia();

        String question_string = question.getQuestionString();
        views.setTextViewText(R.id.tv_widget_question,question_string);
        if (question_string.length() >70){
            views.setTextViewTextSize(R.id.tv_widget_question, TypedValue.COMPLEX_UNIT_SP,16);
        } else {
            views.setTextViewTextSize(R.id.tv_widget_question, TypedValue.COMPLEX_UNIT_SP,24);
        }
        //Set pending Intent for question and daily trivia
        Intent launchApp = new Intent(context,MainActivity.class);
        PendingIntent launchAppPending =PendingIntent.getActivity
                (context,0,launchApp,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_widget_question,launchAppPending);
        views.setOnClickPendingIntent(R.id.tv_daily_trivia,launchAppPending);


        if (question.isAnswered()){//Show answer
            //Disable Layouts not in use
            views.setViewVisibility(R.id.layoutBooleanWidget,View.GONE);
            views.setViewVisibility(R.id.lv_options_widget, View.GONE);

            //Shows if the user got the answer correct
            String header = question.isCorrect() ?
                    context.getString(R.string.correct_heading):
                    context.getString(R.string.wrong_heading);
            views.setTextViewText(R.id.tv_answer_header_widget,header);

            //Displays the correct answer
            String correctAns;
            if (question.isMultiple()){
                correctAns = ((MultipleChoiceQuestion) question).getAnswerMultiple();
                views.setTextViewText(R.id.tv_user_selection_widget,
                        ((MultipleChoiceQuestion) question).getUserSelection());
                if (!question.isCorrect()){// if answer wrong show user selection.
                    views.setViewVisibility(R.id.tv_user_selection_widget, View.VISIBLE);
                    views.setViewVisibility(R.id.tv_your_answer_is_widget,View.VISIBLE);
                }else {//If the answer is correct don't show user selection.
                    views.setViewVisibility(R.id.tv_user_selection_widget, View.GONE);
                    views.setViewVisibility(R.id.tv_your_answer_is_widget,View.GONE);
                }
            }else {// If it is boolean the user selection does not need to be shown.
                correctAns = ((BooleanQuestion) question).getAnswerBoolean();
                views.setViewVisibility(R.id.tv_user_selection_widget, View.GONE);
                views.setViewVisibility(R.id.tv_your_answer_is_widget,View.GONE);
            }
            views.setTextViewText(R.id.tv_correct_answer_widget,correctAns);
            views.setViewVisibility(R.id.layoutAnswerWidget, View.VISIBLE);
        } else {
            if (question.isMultiple()){ //Show multiple options
                //Disable Layouts not in use
                views.setViewVisibility(R.id.layoutBooleanWidget,View.GONE);
                views.setViewVisibility(R.id.layoutAnswerWidget, View.GONE);

                //Sets visibility of list view
                views.setViewVisibility(R.id.lv_options_widget, View.VISIBLE);

                //Set Remote Adapter
                Intent remoteAdapter = new Intent(context, WidgetAdapterService.class);
                views.setRemoteAdapter(R.id.lv_options_widget,remoteAdapter);

                //Updates the List View Items
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.lv_options_widget);

                //Set Pending Intent Template
                Intent intent = new Intent(context,WidgetIntentService.class);
                PendingIntent pendingIntent = PendingIntent.getService
                    (context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                views.setPendingIntentTemplate(R.id.lv_options_widget,pendingIntent);

            } else {  //Show boolean options
                //Disable Layouts not in use
                views.setViewVisibility(R.id.layoutAnswerWidget, View.GONE);
                views.setViewVisibility(R.id.lv_options_widget, View.GONE);

                //Sets visibility of Boolean Layout
                views.setViewVisibility(R.id.layoutBooleanWidget,View.VISIBLE);

                //Sets pending Intent for True
                Intent intent_true = new Intent(context, WidgetIntentService.class);
                intent_true.setAction(WidgetIntentService.ACTION_CHECK_TRUE);
                PendingIntent pendingIntent_true = PendingIntent.getService
                        (context,0,intent_true,PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.bt_true_widget,pendingIntent_true);

                //Sets pending Intent for False
                Intent intent_false = new Intent(context, WidgetIntentService.class);
                intent_false.setAction(WidgetIntentService.ACTION_CHECK_FALSE);
                PendingIntent pendingIntent_false = PendingIntent.getService
                        (context,0,intent_false,PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.bt_false_widget,pendingIntent_false);
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

