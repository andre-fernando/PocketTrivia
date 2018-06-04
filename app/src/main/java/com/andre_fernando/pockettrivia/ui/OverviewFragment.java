package com.andre_fernando.pockettrivia.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.andre_fernando.pockettrivia.utils.QuizActivityFragmentCommunicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Displays the score and time post quiz completion.
 */
@SuppressWarnings("WeakerAccess")
public class OverviewFragment extends Fragment {
    private Unbinder unbinder;
    private QuizActivityFragmentCommunicator listener;

    private int score,total_questions;
    private CharSequence time;

    @BindView(R.id.et_username) EditText et_username;
    @BindView(R.id.tv_score_result) TextView tv_score_result;
    @BindView(R.id.tv_time_result) TextView tv_time_result;
    @BindView(R.id.tv_total_questions_result) TextView tv_total_questions_result;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        //ButterKnife Bind
        unbinder = ButterKnife.bind(this,view);

        //Init the fragment only if savedInstanceState is null
        if (savedInstanceState == null){
            initFragment();
        }

        return view;
    }

    //region Init

    private void initFragment(){
        if (getArguments()!=null){
            //I have used the Json tags as keys to avoid creating excess static variables.
            score = getArguments().getInt(JsonTags.Results);
            total_questions = getArguments().getInt(JsonTags.Question);
            time = getArguments().getCharSequence(JsonTags.Type);
        }
        tv_score_result.setText(getScore());
        tv_total_questions_result.setText(getTotalQuestions());
        tv_time_result.setText(time);


        SharedPreferenceHelper helper = new SharedPreferenceHelper(getContext());
        if (helper.isLoggedIn()){
            et_username.setText(helper.getUserName());
        } else {
            et_username.setHint("Enter Name");
        }

    }

    @OnClick(R.id.bt_save_score)
    public void onClickSaveScore(){
        String name = et_username.getText().toString();
        if (name.length()<1){
            name = "User";
        }
        listener.saveHighScore(name);
    }

    //endregion Init

    //region Getters

    private String getScore(){
        return Integer.toString(score);
    }

    private String getTotalQuestions(){
        return Integer.toString(total_questions);
    }

    //endregion Getters

    //region Lifecycle Methods
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QuizActivityFragmentCommunicator){
            listener = (QuizActivityFragmentCommunicator) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement QuizActivityFragmentCommunicator.");
        }
    }

    //endregion Lifecycle Methods
}
