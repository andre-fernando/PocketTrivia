package com.andre_fernando.pockettrivia.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.andre_fernando.pockettrivia.ui.MainActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This is a JobService that procures a daily question from the server.
 */
public class DailyTriviaJobService extends JobService {

    private AsyncTask<Void, Void, Question> asyncTask;

    @Override
    public boolean onStartJob(JobParameters job) {
        asyncTask = new FetchDailyTriviaAsync(getBaseContext());
        try {
            //Gets the question
            Question question = asyncTask.execute().get(10, TimeUnit.SECONDS);

            //Saves the question to shared preference
            SharedPreferenceHelper helper = new SharedPreferenceHelper(getBaseContext());
            helper.setDailyTrivia(question);

            //Updates the widget
            Intent intent = new Intent(getApplicationContext(),WidgetIntentService.class);
            intent.setAction(WidgetIntentService.ACTION_UPDATE);
            startService(intent);

            //Pending Intent for notification to launch app
            Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    (int) System.currentTimeMillis(),
                    mainActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            //The Notification
            Notification dailyTriviaNotification = new NotificationCompat
                    .Builder(getBaseContext(),getString(R.string.pocket_trivia_notification_channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(((BitmapDrawable)(Objects.requireNonNull
                            (getDrawable(R.mipmap.ic_launcher)))).getBitmap())
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat.from(getApplicationContext())
                    .notify(0,dailyTriviaNotification);
            return true;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (asyncTask != null){
            asyncTask.cancel(true);
        }
        return true;
    }
}
