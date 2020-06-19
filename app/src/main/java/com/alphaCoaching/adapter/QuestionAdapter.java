package com.alphaCoaching.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.QuestionModel;
import com.alphaCoaching.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class QuestionAdapter extends FirestoreRecyclerAdapter<QuestionModel, QuestionAdapter.viewHolder> {
    private OnItemClickListener listener;
    private MyAdapterListener onClickListener;
    public QuestionAdapter(@NonNull FirestoreRecyclerOptions<QuestionModel> options/*,MyAdapterListener listener*/) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull QuestionModel model) {
        holder.textQuizName.setText("Quiz Name= " + model.getQuizName());
        holder.textQuesNumber.setText("Question No.= " + (model.getQuestionNumber()));
        holder.textQuizTime.setText("Quiz Time=" + (model.getQuizTime()));
        holder.detail.setSystemUiVisibility(View.GONE);
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        holder.documentId = snapshot.getId();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_question_review, parent, false);
        return new QuestionAdapter.viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private TextView textQuizName;
        @SuppressLint("StaticFieldLeak")
        private TextView textQuesNumber;
        @SuppressLint("StaticFieldLeak")
        private TextView textQuizTime;
        @SuppressLint("StaticFieldLeak")
        private View view;
        Button detail;
        private String documentId;

        viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textQuizName = itemView.findViewById(R.id.quiz_name);
            textQuesNumber = itemView.findViewById(R.id.ques_num);
            textQuizTime = itemView.findViewById(R.id.quiz_time);
            detail = itemView.findViewById(R.id.detail);
            detail.setOnClickListener(view -> {
                onClickListener.buttonOnClick(view, getAdapterPosition(), getSnapshots().getSnapshot(getAdapterPosition()));
            });

            itemView.setOnClickListener((View v) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface MyAdapterListener {
        void buttonOnClick(View v, int position, DocumentSnapshot documentSnapshot);

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setonButtonClickListener(MyAdapterListener myAdapterListener) {
        this.onClickListener = myAdapterListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}