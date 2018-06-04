package com.andre_fernando.pockettrivia.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.helpers.OpenTriviaHelper;
import com.andre_fernando.pockettrivia.utils.MainActivityFragmentCommunicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Displays the options to configure the quiz type
 * and start the quiz.
 */
@SuppressWarnings("WeakerAccess")
public class StartQuizFragment extends Fragment {
    private Unbinder unbinder;
    // Listener
    private MainActivityFragmentCommunicator listener;

    // Spinner Binds
    @BindView(R.id.sp_number_of_questions) Spinner sp_number_of_questions;
    @BindView(R.id.sp_category) Spinner sp_category;
    @BindView(R.id.sp_difficulty) Spinner sp_difficulty;
    @BindView(R.id.sp_type) Spinner sp_type;


    public StartQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_quiz, container, false);

        //ButterKnife Binder
        unbinder = ButterKnife.bind(this, view);

        //region Spinner Adapters
        Context context = getContext();
        if (context!=null){
            // Number of Questions
            ArrayAdapter<CharSequence> numberQuestionsAdapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.sp_number_questions,// List of info
                    R.layout.spinner_item // The Layout for spinner
            );
            numberQuestionsAdapter.setDropDownViewResource(R.layout.spinner_item);
            sp_number_of_questions.setAdapter(numberQuestionsAdapter);
            sp_number_of_questions.setGravity(Gravity.END);

            //Category
            ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.category_names,
                    R.layout.spinner_item
            );
            categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
            sp_category.setAdapter(categoryAdapter);
            sp_category.setGravity(Gravity.END);


            //Difficulty
            ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.difficulties,
                    R.layout.spinner_item
            );
            difficultyAdapter.setDropDownViewResource(R.layout.spinner_item);
            sp_difficulty.setAdapter(difficultyAdapter);
            sp_difficulty.setGravity(Gravity.END);

            //Type
            ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.types,
                    R.layout.spinner_item
            );
            typeAdapter.setDropDownViewResource(R.layout.spinner_item);
            sp_type.setAdapter(typeAdapter);
            sp_type.setGravity(Gravity.END);
        }
        //endregion Spinner Adapters

        // Inflate the layout for this fragment
        return view;
    }


    @OnClick(R.id.bt_start_quiz)
    public void startQuiz(){
        String number_of_questions, difficulty, type;
        int categoryId = sp_category.getSelectedItemPosition();
        number_of_questions = sp_number_of_questions.getSelectedItem().toString().toLowerCase();
        difficulty = sp_difficulty.getSelectedItem().toString().toLowerCase();
        type = sp_type.getSelectedItem().toString().toLowerCase();
        OpenTriviaHelper helper = new OpenTriviaHelper(getContext());
        String urlString = helper.getAssembledURL
                (number_of_questions,categoryId,difficulty,type);
        listener.startQuiz(urlString,categoryId, difficulty, type);
    }

    //region Lifecycle Methods

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }



    // Init the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityFragmentCommunicator){
            listener = (MainActivityFragmentCommunicator) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MainActivityFragmentCommunicator.");
        }
    }
    //endregion Lifecycle Methods
}
