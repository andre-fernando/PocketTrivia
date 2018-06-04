package com.andre_fernando.pockettrivia.ui;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.objects.BooleanQuestion;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.andre_fernando.pockettrivia.utils.QuizActivityFragmentCommunicator;
import com.andre_fernando.pockettrivia.utils.WidgetIntentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A fragment to display a question
 *
 * Attributions
 *
 * Audio Files:
 *
 * The "correct_bell.wav" originally named "395807__magedu__buzzer-boardgame-01.wav" is
 * licenced under the Creative Commons (CC BY 3.0  https://creativecommons.org/licenses/by/3.0/)
 * and was created by Martin Dürr (https://freesound.org/people/magedu/)
 * (https://twitter.com/magedu).
 *
 * The "wrong_buzzer.wav" originally named "54047__guitarguy1985__buzzer.wav" is licenced
 * under the Creative Commons (CC0 1.0  https://creativecommons.org/publicdomain/zero/1.0/) and
 * was created by Freesound.org user guitarguy1985 (https://freesound.org/people/guitarguy1985/)
 *
 */
@SuppressWarnings("WeakerAccess")
public class QuestionFragment extends Fragment {

    private Unbinder unbinder;
    private Question question;
    private QuizActivityFragmentCommunicator listener;
    private MediaPlayer mediaPlayer;

    //region ButterKnife Binds

    @BindView(R.id.tv_category) TextView tv_category;
    @BindView(R.id.tv_question) TextView tv_question;
    @BindView(R.id.tv_daily_trivia) TextView tv_daily_trivia;

    //For multiple Choice
    @BindView(R.id.layoutMultipleChoice) LinearLayout layoutMultipleChoice;
    @BindView(R.id.bt_option1) Button bt_option1;
    @BindView(R.id.bt_option2) Button bt_option2;
    @BindView(R.id.bt_option3) Button bt_option3;
    @BindView(R.id.bt_option4) Button bt_option4;

    //For boolean
    @BindView(R.id.layoutBoolean) LinearLayout layoutBoolean;

    //For Solution
    @BindView(R.id.layoutAnswer) LinearLayout layoutAnswer;
    @BindView(R.id.tv_answer_header) TextView tv_answerHeader;
    @BindView(R.id.tv_correct_answer) TextView tv_correctAnswer;
    @BindView(R.id.tv_your_answer_is) TextView tv_yourAnswerIs;
    @BindView(R.id.tv_user_selection) TextView tv_userSelection;


    //endregion ButterKnife Binds

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        unbinder = ButterKnife.bind(this,view);//Butter knife Bind

        //Load the details only the first time
        if (savedInstanceState == null){
            initFragment();
        }
        return view;
    }

    //region Init Methods

    private void initFragment(){
        if (getArguments() != null) {
            question = getArguments().getParcelable(JsonTags.Question);
            if (question != null) {

                if (question.isAnswered()){
                    initAnsweredQuestion();
                }else {
                    if(question.isMultiple()){
                        initMultipleChoiceQuestion();
                    }else {
                        initBooleanQuestion();
                    }
                }
            }
        }
    }

    private void initBasicInfo(){
        tv_category.setText(question.getQuestionCategory());
        tv_question.setText((question.getQuestionString()));
        if (question.isDailyTrivia()){
            tv_daily_trivia.setVisibility(View.VISIBLE);
        }
    }

    private void initMultipleChoiceQuestion(){
        initBasicInfo();
        layoutMultipleChoice.setVisibility(View.VISIBLE);
        bt_option1.setText( ((MultipleChoiceQuestion) question).getOption(0) );
        bt_option2.setText( ((MultipleChoiceQuestion) question).getOption(1) );
        bt_option3.setText( ((MultipleChoiceQuestion) question).getOption(2) );
        bt_option4.setText( ((MultipleChoiceQuestion) question).getOption(3) );
    }


    private void initBooleanQuestion(){
        initBasicInfo();
        layoutBoolean.setVisibility(View.VISIBLE);
    }

    private void initAnsweredQuestion(){
        initBasicInfo();
        String heading = question.isCorrect() ? "Correct": "Wrong";
        tv_answerHeader.setText(heading);
        if (question.isMultiple()){
            tv_correctAnswer.setText(((MultipleChoiceQuestion) question).getAnswerMultiple());
            if (!question.isCorrect()){
                showUserSelection(((MultipleChoiceQuestion) question).getUserSelection());
            }
        }else{
            tv_correctAnswer.setText(((BooleanQuestion)question).getAnswerBoolean());
        }
        layoutAnswer.setVisibility(View.VISIBLE);
    }

    //endregion Init Methods

    //region Click Handlers

    @OnClick({R.id.bt_true,R.id.bt_false})
    public void onClickAnswerBoolean(Button button){
        if (button.getId() == R.id.bt_true){
            showAnswerBoolean(true);
        } else if (button.getId() == R.id.bt_false){
            showAnswerBoolean(false);
        }
    }

    @OnClick({R.id.bt_option1,R.id.bt_option2, R.id.bt_option3, R.id.bt_option4})
    public void onClickAnswerMultiple(Button button){
        int id = button.getId();
        switch (id){
            case R.id.bt_option1:
                showAnswerMultiple(0);
                break;

            case R.id.bt_option2:
                showAnswerMultiple(1);
                break;

            case R.id.bt_option3:
                showAnswerMultiple(2);
                break;

            case R.id.bt_option4:
                showAnswerMultiple(3);
                break;
        }
    }

    //endregion Click Handlers

    //region Helper Methods

    private void showAnswerMultiple(int buttonNumber){
        boolean isAnswerCorrect = ((MultipleChoiceQuestion)question).checkAns(buttonNumber);
        String heading;
        if (isAnswerCorrect){
            heading = getString(R.string.correct_heading);
            mediaPlayer = MediaPlayer.create(getContext(),R.raw.correct_bell);
            mediaPlayer.start();
        }else {
            heading = getString(R.string.wrong_heading);
            mediaPlayer = MediaPlayer.create(getContext(),R.raw.wrong_buzzer);
            mediaPlayer.start();
        }
        tv_answerHeader.setText(heading);
        tv_correctAnswer.setText(((MultipleChoiceQuestion) question).getAnswerMultiple());
        if (!question.isCorrect()){
            showUserSelection(((MultipleChoiceQuestion) question).getUserSelection());
        }

        layoutMultipleChoice.setVisibility(View.GONE);
        layoutAnswer.setVisibility(View.VISIBLE);
        if (question.isDailyTrivia()){
            SharedPreferenceHelper helper = new SharedPreferenceHelper(getContext());
            helper.setDailyTrivia(question);
            updateWidget();
        }else {
            listener.processAnswer(question.getQuestionNumber(),isAnswerCorrect,
                    ((MultipleChoiceQuestion)question).getUserSelection());
        }

    }

    private void showAnswerBoolean(boolean booleanSelected){
        boolean isAnswerCorrect = ((BooleanQuestion)question).checkAnswer(booleanSelected);
        String heading;
        if (isAnswerCorrect){
            heading = getString(R.string.correct_heading);
            mediaPlayer = MediaPlayer.create(getContext(),R.raw.correct_bell);
            mediaPlayer.start();
        }else {
            heading = getString(R.string.wrong_heading);
            mediaPlayer = MediaPlayer.create(getContext(),R.raw.wrong_buzzer);
            mediaPlayer.start();
        }
        tv_answerHeader.setText(heading);
        tv_correctAnswer.setText(((BooleanQuestion)question).getAnswerBoolean());
        layoutBoolean.setVisibility(View.GONE);
        layoutAnswer.setVisibility(View.VISIBLE);

        if (question.isDailyTrivia()){
            SharedPreferenceHelper helper = new SharedPreferenceHelper(getContext());
            helper.setDailyTrivia(question);
            updateWidget();
        }else {
            listener.processAnswer(question.getQuestionNumber(),isAnswerCorrect,
                    ((BooleanQuestion)question).getUserSelection());
        }
    }

    private void showUserSelection(String userSelection){
        tv_yourAnswerIs.setVisibility(View.VISIBLE);
        tv_userSelection.setText(userSelection);
        tv_userSelection.setVisibility(View.VISIBLE);
    }

    private void updateWidget(){
        Context context = getActivity();
        if (context != null){
            Intent intent = new Intent(context,WidgetIntentService.class);
            intent.setAction(WidgetIntentService.ACTION_UPDATE);
            context.startService(intent);
        }

    }

    //endregion Helper Methods

    //region Lifecycle Methods

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) mediaPlayer.release();//releases the media player
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();//unbinds the butter knife views
    }

    // Check if interface is implemented
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ((context instanceof QuizActivityFragmentCommunicator)){
            listener = (QuizActivityFragmentCommunicator) context;
        }
    }

    //endregion Lifecycle Methods
}
