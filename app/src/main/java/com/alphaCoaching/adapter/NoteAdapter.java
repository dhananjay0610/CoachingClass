package com.alphaCoaching.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.model.Note;
import com.alphaCoaching.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note,NoteAdapter.NoteHolder> {
    private  OnItemClickListener listener;
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position,@NonNull Note model) {
       holder.textQuizName.setText("Quiz Name= "+model.getQuizName());
       holder.textQuesNumber.setText("Question No.= "+String.valueOf(model.getQuestionNumber()));
        holder.textQuizTime.setText("Quiz Time="+String.valueOf(model.getQuizTime()));
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout, parent,false);
        return new NoteAdapter. NoteHolder(view);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
                private  TextView textQuizName;
        @SuppressLint("StaticFieldLeak")
        private  TextView textQuesNumber;
        @SuppressLint("StaticFieldLeak")
        private  TextView textQuizTime;
        @SuppressLint("StaticFieldLeak")
        private  View view;

         NoteHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textQuizName=itemView.findViewById(R.id.quiz_name);
            textQuesNumber=itemView.findViewById(R.id.ques_num);
            textQuizTime=itemView.findViewById(R.id.quiz_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  int position=getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
