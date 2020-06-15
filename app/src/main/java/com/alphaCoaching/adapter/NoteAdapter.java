package com.alphaCoaching.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.Note;
import com.alphaCoaching.Model.QuizTaken;
import com.alphaCoaching.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private OnItemClickListener listener;
    private MyAdapterListener onClickListener;
    private Context mContext;
    private ArrayList<QuizTaken> quizTakenList;
    private ArrayList<Note> quizList;

    public NoteAdapter(Context context, ArrayList<Note> quizList, ArrayList<QuizTaken> quizTakenList) {
//        super(null);
        mContext = context;
        this.quizList = quizList;
        this.quizTakenList = quizTakenList;
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cat_item_layout, parent, false);
        return new NoteAdapter.NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note model = quizList.get(position);
        if (isQuizTaken(model.getId())) {
            holder.detail.setVisibility(View.VISIBLE);
            holder.attainQuiz.setVisibility(View.GONE);
        } else {
            holder.detail.setVisibility(View.GONE);
            holder.attainQuiz.setVisibility(View.VISIBLE);
        }

        holder.textQuizName.setText(model.getQuizName());
        holder.textQuesNumber.setText(model.getQuestionNumber() + " questions");
        holder.textQuizTime.setText(model.getQuizTime() + " minutes");
    }

    private boolean isQuizTaken(String id) {
        for (int i = 0; i < quizTakenList.size(); i++) {
            String takenId = quizTakenList.get(i).getQuizId();
            if (id.equals(takenId)) {
                return true;
            }
        }
        return false;
    }

    private String getQuizTakenId(String quizId) {
        for (int i = 0; i < quizTakenList.size(); i++) {
            if (quizTakenList.get(i).getQuizId().equals(quizId)) {
                return quizTakenList.get(i).getId();
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private TextView textQuizName;
        @SuppressLint("StaticFieldLeak")
        private TextView textQuesNumber;
        @SuppressLint("StaticFieldLeak")
        private TextView textQuizTime;
        @SuppressLint("StaticFieldLeak")
        private View view;
        private Button detail, attainQuiz;

        NoteHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textQuizName = itemView.findViewById(R.id.quiz_name);
            textQuesNumber = itemView.findViewById(R.id.ques_num);
            textQuizTime = itemView.findViewById(R.id.quiz_time);
            detail = itemView.findViewById(R.id.detail);
            attainQuiz = itemView.findViewById(R.id.attainQuizButton);


            detail.setOnClickListener(view -> {
                long position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener.buttonOnClick(view, getQuizTakenId(quizList.get(getAdapterPosition()).getId()), quizList.get(getAdapterPosition()).getId());
                }
            });


            attainQuiz.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(isQuizTaken(quizList.get(getAdapterPosition()).getId()), quizList.get(position));
                }
            });
        }
    }

    public interface MyAdapterListener {
        void buttonOnClick(View v, String quizTakenId, String quizId);

    }

    public interface OnItemClickListener {
        void onItemClick(boolean isQuizTaken, Note model);

    }

    public void setonButtonClickListener(MyAdapterListener myAdapterListener) {
        this.onClickListener = myAdapterListener;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}