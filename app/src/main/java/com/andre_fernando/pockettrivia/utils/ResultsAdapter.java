package com.andre_fernando.pockettrivia.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.data.objects.Result;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An Adapter for the high scores.
 */
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private final ArrayList<Result> list;

    public ResultsAdapter(ArrayList<Result> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_results_list, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultsViewHolder holder, int position) {
        Result result = list.get(position);
        holder.tv_name.setText(result.getName());
        holder.tv_time.setText(result.getTime());
        holder.tv_score.setText(result.getScore());
        holder.tv_date.setText(result.getDate());
        holder.tv_totalQuestions.setText(result.getTotalQuestions());
        holder.tv_category.setText(result.getCategory());
        holder.tv_difficulty.setText(result.getDifficulty());
        holder.tv_type.setText(result.getType());

        holder.ib_showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.layout_details.isShown()){
                    holder.layout_details.setVisibility(View.GONE);
                    holder.ib_showDetails.setImageDrawable(holder.open);
                }else {
                    holder.layout_details.setVisibility(View.VISIBLE);
                    holder.ib_showDetails.setImageDrawable(holder.close);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_rv_name) TextView tv_name;
        @BindView(R.id.tv_rv_time) TextView tv_time;
        @BindView(R.id.tv_rv_score) TextView tv_score;
        @BindView(R.id.tv_rv_date_value) TextView tv_date;
        @BindView(R.id.tv_rv_total_questions_value) TextView tv_totalQuestions;
        @BindView(R.id.tv_rv_category_value) TextView tv_category;
        @BindView(R.id.tv_rv_difficulty_value) TextView tv_difficulty;
        @BindView(R.id.tv_rv_type_value) TextView tv_type;
        @BindView(R.id.ib_rv_view_details) ImageButton ib_showDetails;
        @BindView(R.id.layout_rv_details) ConstraintLayout layout_details;

        @BindDrawable(R.drawable.ic_arrow_drop_down_black_24dp) Drawable open;
        @BindDrawable(R.drawable.ic_arrow_drop_up_black_24dp) Drawable close;


        ResultsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
