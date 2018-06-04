package com.andre_fernando.pockettrivia.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;

/**
 * This RemoteViewsService helps populate the list
 * for the multiple choices in the widget.
 */
public class WidgetAdapterService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteOptionsAdapter(this.getApplicationContext());
    }

    class RemoteOptionsAdapter implements RemoteViewsService.RemoteViewsFactory{

        private final Context context;
        private String[] options;

        RemoteOptionsAdapter(Context context) {
            this.context = context;
            Question question = new SharedPreferenceHelper(context).getDailyTrivia();
            options = ((MultipleChoiceQuestion)question).getShuffledOptions();
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            Question question = new SharedPreferenceHelper(context).getDailyTrivia();
            options = ((MultipleChoiceQuestion)question).getShuffledOptions();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews row = new RemoteViews(context.getPackageName(),
                    R.layout.adapter_remote_options);

            row.setTextViewText(R.id.bt_remote_option,options[position]);

            //Check answer with Fill In Intent
            Intent intent = new Intent(context,WidgetIntentService.class);
            intent.putExtra(WidgetIntentService.PARAM_USER_ANSWER, options[position]);
            intent.setAction(WidgetIntentService.ACTION_CHECK_MULTIPLE);
            row.setOnClickFillInIntent(R.id.bt_remote_option,intent);

            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
