package com.alphaCoaching.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.QuizTaken;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.activity.WholeClassResultList;

import java.util.ArrayList;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class WholeClassResultAdapter extends RecyclerView.Adapter<WholeClassResultAdapter.AdapterHolder> {
    private ArrayList<QuizTaken> quizTakenList;
    private int maxScore;

    public WholeClassResultAdapter(ArrayList<QuizTaken> quizTakenList, int score) {
        this.quizTakenList = quizTakenList;
        maxScore = score;
    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getAppContext()).inflate(R.layout.whole_class_result_layout, parent, false);
        return new WholeClassResultAdapter.AdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
        QuizTaken quizTaken = quizTakenList.get(position);

        holder.userScore.setText(String.valueOf(quizTaken.getScore()));
        if (quizTakenList.get(position).getUserId().equals(UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID))) {
            holder.userName.setText(quizTaken.getUserName() + " (You)");
        } else {
            holder.userName.setText(quizTaken.getUserName());
        }
        if (quizTakenList.get(position).getScore() == maxScore) {
            holder.userStatus.setText("TOPPER");
            holder.userStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return quizTakenList.size();
    }

    class AdapterHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private TextView userName;
        @SuppressLint("StaticFieldLeak")
        private TextView userScore;
        private TextView userStatus;

        private View view;

        AdapterHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            userName = itemView.findViewById(R.id.user_name);
            userScore = itemView.findViewById(R.id.user_score);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
