package com.andre_fernando.pockettrivia.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.objects.Result;
import com.andre_fernando.pockettrivia.helpers.DbHelper;
import com.andre_fernando.pockettrivia.utils.ResultsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Displays all the saved high scores by the user.
 */
public class ResultsListFragment extends Fragment {
    private Unbinder unbinder;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rv_scores) RecyclerView rv_scores;


    public ResultsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results_list, container, false);

        // ButterKnife Bind views
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    //region Lifecycle Methods

    @Override
    public void onStart() {
        super.onStart();
        DbHelper dbHelper = new DbHelper(getContext());
        ArrayList<Result> list = dbHelper.getAllResults();
        ResultsAdapter adapter = new ResultsAdapter(list);
        rv_scores.setAdapter(adapter);
        rv_scores.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }
    //endregion Lifecycle Methods
}
