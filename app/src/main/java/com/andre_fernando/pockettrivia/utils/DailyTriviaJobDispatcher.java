package com.andre_fernando.pockettrivia.utils;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;


public class DailyTriviaJobDispatcher {

    public static void init(Context context){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(
                new GooglePlayDriver(context));
        Job dailyTriviaJob = dispatcher.newJobBuilder()
                .setService(DailyTriviaJobService.class)
                .setTag(DailyTriviaJobService.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //.setTrigger(Trigger.executionWindow(86400,88200))//Daily (24hrs)
                .setTrigger(Trigger.executionWindow(600,1000))//10 min For Testing Purposes
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(dailyTriviaJob);
    }
}
