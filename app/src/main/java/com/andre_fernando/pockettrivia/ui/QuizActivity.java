package com.andre_fernando.pockettrivia.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.enums.Queries;
import com.andre_fernando.pockettrivia.data.objects.BooleanQuestion;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.data.objects.Result;
import com.andre_fernando.pockettrivia.helpers.DbHelper;
import com.andre_fernando.pockettrivia.utils.FetchQuestionsAsync;
import com.andre_fernando.pockettrivia.utils.OverviewPagerAdapter;
import com.andre_fernando.pockettrivia.utils.QuizActivityFragmentCommunicator;
import com.andre_fernando.pockettrivia.utils.QuizPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity holds all the questions for the quiz
 * and the scoreboard.
 */
@SuppressWarnings("WeakerAccess")
public class QuizActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Question>>,
                    QuizActivityFragmentCommunicator {


    //region Class variables
    private String urlString, quizDifficulty, quizType;
    private boolean isCompleted,hasSavedInstance;
    private ArrayList<Question> quizQuestionList;
    private int totalQuestions, count_answeredQuestions, score, quizCategoryId;
    private CharSequence elapsedTime;

    //endregion Class variables

    //region ButterKnife Binds
    @BindView(R.id.quiz_layout) LinearLayout layoutQuiz;
    @BindView(R.id.tv_total_question) TextView tv_countAnsweredQuestions;
    @BindView(R.id.tv_score)TextView tv_score;
    @BindView(R.id.ch_timer) Chronometer ch_timer;
    @BindView(R.id.quiz_frame) ViewPager quizPagerView;
    @BindView(R.id.av_loading_screen) ImageView LoadingScreenView;

    //endregion ButterKnife Binds

    //region Keys Save Instance state
    private final String key_quizCategoryId = "quiz category id";  //NON-NLS
    private final String key_quizDifficulty = "quiz difficulty";  //NON-NLS
    private final String key_quizType = "quiz type";  //NON-NLS
    private final String key_isCompleted = "isCompleted";  //NON-NLS
    private final String key_quizQuestionList = "quiz question list";  //NON-NLS
    private final String key_count_answeredQuestions = "count of answered questions";  //NON-NLS
    private final String key_score = "quiz score";  //NON-NLS
    private final String key_timerBase = "base";  //NON-NLS
    private final String key_pagerAdapterState = "pager adapter state"; //NON-NLS
    //endregion Keys Save Instance state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        hasSavedInstance = savedInstanceState != null;
        if (hasSavedInstance){
            recoverInstanceState(savedInstanceState);
        }else {
            //get data from intent
            Intent received = getIntent();
            urlString = received.getStringExtra(JsonTags.Results);
            quizCategoryId = received.getIntExtra(Queries.Category,0);
            quizDifficulty = toCamelCase(received.getStringExtra(Queries.Difficulty));
            quizType = toCamelCase(received.getStringExtra(Queries.Type));
        }
    }

    //region Scoreboard Methods

    private void initScoreboard(int count ,int score,boolean isCompleted){
        totalQuestions=quizQuestionList.size();
        count_answeredQuestions=count;
        this.score=score;
        tv_countAnsweredQuestions.setText(getAnsweredCount());
        tv_score.setText(getScore());
        if (!hasSavedInstance){//Runs only if not recovered
            ch_timer.setBase(SystemClock.elapsedRealtime());
            ch_timer.start();
        }
        this.isCompleted = isCompleted;
    }

    private void updateScoreboard(boolean isCorrect){
        count_answeredQuestions++;
        if (isCorrect) score++;
        tv_countAnsweredQuestions.setText(getAnsweredCount());
        tv_score.setText(getScore());
        if (count_answeredQuestions==totalQuestions){// When quiz is over
            showQuizOverview();
        }

    }

    private String getScore(){
        return Integer.toString(score);
    }

    private String getAnsweredCount(){
        return count_answeredQuestions + "/" + totalQuestions;
    }

    //endregion Scoreboard Methods

    //region Quiz Overview Methods

    private void showQuizOverview(){
        ch_timer.stop();
        elapsedTime=ch_timer.getText();
        isCompleted = true;
        new Handler().postDelayed(new Runnable() {//Delay for the sound effect to play and see the last answer
            @Override
            public void run() {
                OverviewPagerAdapter overviewPagerAdapter = new OverviewPagerAdapter(
                        getSupportFragmentManager(),quizQuestionList,
                        score,totalQuestions,elapsedTime);
                quizPagerView.setAdapter(overviewPagerAdapter);
                quizPagerView.setOffscreenPageLimit(2);
                quizPagerView.setCurrentItem(quizQuestionList.size());//sets the pager to the last page
            }
        },2000);
    }


    private String getCategoryName(){
        String[] categoryNames = this.getResources().getStringArray(R.array.category_names);
        return categoryNames[quizCategoryId];
    }

    private String getDate(){
        Date today = Calendar.getInstance().getTime();
        //noinspection HardCodedStringLiteral
        SimpleDateFormat formatter = new SimpleDateFormat("EEE dd MMM yyyy", Locale.UK);
        return formatter.format(today);
    }

    /**
     * This method converts a String into the camel case
     * @param text String to be formatted
     * @return Formatted String
     */
    private String toCamelCase(String text){
        if (text == null) return "";

        int position = 0;
        StringBuilder builder = new StringBuilder(text);
        boolean capitaliseNext = true;// Starts true to capitalise the first letter

        while(position < text.length()){
            char x = builder.charAt(position);
            if (capitaliseNext && Character.isLetter(x)){
                builder.setCharAt(position,Character.toUpperCase(x));
                capitaliseNext = false;
            }
            //Checks for spaces or full stops
            if (!capitaliseNext && ((Character.isWhitespace(x)) || (x=='.'))){
                capitaliseNext = true;
            }
            position++;
        }
        return builder.toString();
    }

    //endregion Quiz Overview Methods

    //region LifeCycle Methods

    @Override
    protected void onStart() {
        super.onStart();
        //Run only if not recovered
        if (!hasSavedInstance) getSupportLoaderManager()
                .restartLoader(16, null, this).forceLoad();
    }

    @Override
    public void onBackPressed() {
        if (isCompleted){
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.ad_confirm_exit_title)
                    .setMessage(R.string.ad_confirm_exit_message)
                    .setNegativeButton(R.string.ad_exit_bt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(
                                    new Intent(QuizActivity.this, MainActivity.class));
                            QuizActivity.this.finish();
                        }
                    })
                    .setPositiveButton(R.string.ad_Continue_bt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case 16908332://For some reason R.id.home was not working
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(key_quizCategoryId,quizCategoryId);
        bundle.putString(key_quizDifficulty,quizDifficulty);
        bundle.putString(key_quizType,quizType);
        bundle.putBoolean(key_isCompleted,isCompleted);
        bundle.putParcelableArrayList(key_quizQuestionList,quizQuestionList);
        bundle.putInt(key_count_answeredQuestions,count_answeredQuestions);
        bundle.putInt(key_score,score);
        ch_timer.stop();
        long elapsed_time = SystemClock.elapsedRealtime()-ch_timer.getBase();
        bundle.putLong(key_timerBase,elapsed_time);
        bundle.putInt(key_pagerAdapterState,quizPagerView.getCurrentItem());
        super.onSaveInstanceState(bundle);
    }

    private void recoverInstanceState(Bundle bundle){
        quizCategoryId = bundle.getInt(key_quizCategoryId);
        quizDifficulty = bundle.getString(key_quizDifficulty);
        quizType = bundle.getString(key_quizType);
        isCompleted = bundle.getBoolean(key_isCompleted);
        quizQuestionList = bundle.getParcelableArrayList(key_quizQuestionList);
        count_answeredQuestions =bundle.getInt(key_count_answeredQuestions);
        score = bundle.getInt(key_score);
        ch_timer.setBase(SystemClock.elapsedRealtime()-bundle.getLong(key_timerBase));
        ch_timer.start();
        int adapterPosition = bundle.getInt(key_pagerAdapterState);
        if (isCompleted){//if complete shows OverviewPagerAdapter
            OverviewPagerAdapter overviewPagerAdapter = new OverviewPagerAdapter(
                    getSupportFragmentManager(),quizQuestionList,
                    score,totalQuestions,elapsedTime);
            quizPagerView.setAdapter(overviewPagerAdapter);
        }else {// if not complete show QuizPagerAdapter
            FragmentPagerAdapter quizPagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(),
                    quizQuestionList);
            quizPagerView.setAdapter(quizPagerAdapter);
        }
        quizPagerView.setOffscreenPageLimit(2);
        quizPagerView.setCurrentItem(adapterPosition);
        initScoreboard(count_answeredQuestions,score,isCompleted);
        LoadingScreenView.setVisibility(View.GONE);
        quizPagerView.setVisibility(View.VISIBLE);
    }

    //endregion Lifecycle Methods

    //region Loader Methods
    @NonNull
    @Override
    public Loader<ArrayList<Question>> onCreateLoader(int id, @Nullable Bundle args) {

        return new FetchQuestionsAsync(this,urlString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Question>> loader, ArrayList<Question> data) {
        if (data.size()>0) {

            quizQuestionList = data;

            initScoreboard(0,0,false);
            FragmentPagerAdapter quizPagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(),
                                                                        quizQuestionList);
            quizPagerView.setAdapter(quizPagerAdapter);
            quizPagerView.setOffscreenPageLimit(2);

            LoadingScreenView.setVisibility(View.GONE);
            quizPagerView.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, R.string.server_is_offline, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Question>> loader) {

    }

    //endregion Loader methods

    //region Interface Implementation

    @Override
    public void processAnswer(int questionNumber, boolean isCorrect,
                              String userSelection) {

        quizQuestionList.get(questionNumber-1).setAnswered(true);
        quizQuestionList.get(questionNumber-1).setCorrect(isCorrect);
        if (quizQuestionList.get(questionNumber-1).isMultiple()){
            ((MultipleChoiceQuestion)quizQuestionList.get(questionNumber-1))
                    .setUserSelection(userSelection);
        }else {
            ((BooleanQuestion)quizQuestionList.get(questionNumber-1))
                    .setUserSelection(Boolean.valueOf(userSelection));
        }
        updateScoreboard(isCorrect);
    }


    @Override
    public void saveHighScore(String name) {

        Result result = new Result(score,totalQuestions,name,getDate(),
                elapsedTime.toString(),getCategoryName(),quizDifficulty,quizType);

        DbHelper dbHelper = new DbHelper(this);
        dbHelper.insertResult(result);
        onBackPressed();
    }

    //endregion End of Interface Implementation
}
