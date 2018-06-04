package com.andre_fernando.pockettrivia.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.objects.Question;
import com.andre_fernando.pockettrivia.ui.QuestionFragment;

import java.util.ArrayList;

/**
 * This pager adapter shows an ongoing quiz questions.
 */
public class QuizPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Question> quizList;

    public QuizPagerAdapter(FragmentManager fm, ArrayList<Question> list) {
        super(fm);
        quizList=list;
    }

    @Override
    public Fragment getItem(int position) {
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(JsonTags.Question,quizList.get(position));
        questionFragment.setArguments(bundle);
        return questionFragment;
    }

    @Override
    public int getCount() {
        return quizList.size();
    }

}
