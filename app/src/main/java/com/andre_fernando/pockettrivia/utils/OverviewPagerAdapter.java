package com.andre_fernando.pockettrivia.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.ui.OverviewFragment;
import com.andre_fernando.pockettrivia.ui.QuestionFragment;

import java.util.ArrayList;

/**
 * This Pager Adapter showcases the overview of a
 * finished quiz. It allows the user to see the final
 * score and recap all the questions.
 */
public class OverviewPagerAdapter extends FragmentPagerAdapter {
    private final int score;
    private final int total_questions;
    private final CharSequence time;
    private final ArrayList<Question> questionsList;

    /**
     * This is the constructor for the OverviewPagerAdapter
     * @param fm Support Fragment Manager
     * @param list the list of answered questions for the recap
     * @param score the final score for the overview fragment
     * @param total_questions total number of questions for the overview fragment
     * @param time the total time taken for the quiz for the overview fragment
     */
    public OverviewPagerAdapter(FragmentManager fm, ArrayList<Question> list,
                                int score, int total_questions, CharSequence time) {
        super(fm);
        this.questionsList=list;
        this.score=score;
        this.total_questions= total_questions;
        this.time= time;
    }

    @Override
    public Fragment getItem(int position) {
        //This makes the last fragment the overview fragment
        if (position==questionsList.size()){
            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle bundle= new Bundle();
            //I have used the Json tags as keys to avoid creating excess static variables.
            bundle.putInt(JsonTags.Results,score);
            bundle.putInt(JsonTags.Question,total_questions);
            bundle.putCharSequence(JsonTags.Type,time);
            overviewFragment.setArguments(bundle);
            return overviewFragment;
        }

        //This will show a recap of all the questions
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(JsonTags.Question,questionsList.get(position));
        questionFragment.setArguments(bundle);
        return questionFragment;
    }

    @Override
    public int getCount() {
        return questionsList.size()+1;
    }
}
