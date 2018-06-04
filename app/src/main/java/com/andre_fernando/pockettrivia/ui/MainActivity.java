package com.andre_fernando.pockettrivia.ui;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.enums.Queries;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.DbHelper;
import com.andre_fernando.pockettrivia.helpers.OpenTriviaHelper;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.andre_fernando.pockettrivia.utils.DailyTriviaJobDispatcher;
import com.andre_fernando.pockettrivia.utils.FetchQuestionsAsync;
import com.andre_fernando.pockettrivia.utils.MainActivityFragmentCommunicator;
import com.andre_fernando.pockettrivia.utils.NetworkCheck;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragmentCommunicator,
        LoaderManager.LoaderCallbacks<ArrayList<Question>> {

    private Question dailyTrivia;
    private SharedPreferenceHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup the add
        MobileAds.initialize(this,"ca-app-pub-3940256099942544/6300978111");
        AdView adBanner= findViewById(R.id.ad_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adBanner.loadAd(adRequest);

        //Add notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.daily_trivia_notification_name);
            String description = getString(R.string.daily_trivia_notification_description);
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.pocket_trivia_notification_channel_id),
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        //Init the Shared preference helper class
        helper = new SharedPreferenceHelper(this);
    }

    //region Lifecycle & Menu Methods

    @Override
    protected void onStart() {
        super.onStart();

        if (!helper.isInitialized()){
            if (NetworkCheck.isConnected(this)){
                getSupportLoaderManager().restartLoader(19, null, this).forceLoad();
            }else {
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
            DailyTriviaJobDispatcher.init(this);
        } else {//if already initialized we can get the question from the shared preference
            dailyTrivia = helper.getDailyTrivia();
            initDailyTrivia();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.m_start_quiz:
                // Switch the fragment to setup quiz
                addFragment(new StartQuizFragment());
                return true;

            case R.id.m_sign_in:
                // Show sign in feature
                addFragment(new SignInFragment());
                return true;

            case R.id.m_results:
                // Show results fragment
                addFragment(new ResultsListFragment());
                return true;

            case R.id.m_clear_data:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.ad_confirm_clear_data_title)
                        .setMessage(R.string.ad_confirm_clear_data_message)
                        .setNegativeButton(R.string.ad_dismiss_bt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.ad_clear_data_bt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (helper.isLoggedIn()) helper.removeUsername();
                                new DbHelper(MainActivity.this).removeAllResults();
                                Toast.makeText(MainActivity.this, "All data cleared.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
                return true;

            case R.id.m_about:
                // Show about fragment
                addFragment(new AboutFragment());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //endregion Lifecycle & Menu Methods

    //region Helper Methods

    private void initDailyTrivia(){
        //Setup Question Fragment
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(JsonTags.Question,dailyTrivia);
        questionFragment.setArguments(bundle);

        //Display Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFrameLayout,questionFragment,getString(R.string.daily_trivia))
                .setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_the_right)
                .commit();

    }


    private void addFragment(Fragment fragment){

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_the_right,
                        R.anim.enter_from_left,R.anim.exit_to_the_right)
                .add(R.id.mainFrameLayout, fragment)
                .addToBackStack(getString(R.string.daily_trivia))
                .commit();
    }

    //endregion Init Methods

    //region Implementation of MainActivityFragmentCommunicator
    @Override
    public void startQuiz(String url, int categoryId, String difficulty, String type) {
        Intent intent = new Intent(this,QuizActivity.class);
        intent.putExtra(JsonTags.Results,url);
        intent.putExtra(Queries.Category,categoryId);
        intent.putExtra(Queries.Difficulty,difficulty);
        intent.putExtra(Queries.Type,type);
        if (NetworkCheck.isConnected(this)){
            startActivity(intent);
        }else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    //endregion Implementation of MainActivityFragmentCommunicator

    //region Loader methods

    @NonNull
    @Override
    public Loader<ArrayList<Question>> onCreateLoader(int id, @Nullable Bundle args) {
        return new FetchQuestionsAsync(this, OpenTriviaHelper.dailyTriviaUrl);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Question>> loader, ArrayList<Question> data) {
        if (data != null){
            dailyTrivia = data.get(0);
            dailyTrivia.setDailyTrivia(true);
            helper.setDailyTrivia(dailyTrivia);
            initDailyTrivia();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Question>> loader) {

    }

    //endregion Loader Methods

}
