package com.alphaCoaching.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.Note;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {
    private OnItemClickListener listener;
    private MyAdapterListener onClickListener;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {
        holder.textQuizName.setText("Quiz Name= " + model.getQuizName());
        holder.textQuesNumber.setText("Question No.= " + (model.getQuestionNumber()));
        holder.textQuizTime.setText("Quiz Time=" + (model.getQuizTime()));
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        holder.documentId = snapshot.getId();

        if (UserSharedPreferenceManager.getQuizTakenStatus(getAppContext(), snapshot.getId())) {
//            do nothing
            holder.detail.setVisibility(View.VISIBLE);
        } else {
            String id = snapshot.getId();
            FirebaseAuth fireAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = fireAuth.getCurrentUser();
            assert currentUser != null;
            String user_Uuid = currentUser.getUid();
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            CollectionReference yourCollRef = rootRef.collection("quizTaken");
            Query query = yourCollRef.whereEqualTo("quizId", id)
                    .whereEqualTo("userId", user_Uuid);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(task.getResult()).size() == 0) {
                        UserSharedPreferenceManager.storeQuizTakenStatus(getAppContext(), false, id);
                        holder.detail.setVisibility(View.GONE);
                    } else {
                        UserSharedPreferenceManager.storeQuizTakenStatus(getAppContext(), true, id);
                        holder.detail.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout, parent, false);
        return new NoteAdapter.NoteHolder(view);
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
        Button detail;
        private String documentId;

        NoteHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textQuizName = itemView.findViewById(R.id.quiz_name);
            textQuesNumber = itemView.findViewById(R.id.ques_num);
            textQuizTime = itemView.findViewById(R.id.quiz_time);
            detail = itemView.findViewById(R.id.detail);

            detail.setOnClickListener(view -> onClickListener.buttonOnClick(view, getAdapterPosition(), getSnapshots().getSnapshot(getAdapterPosition())));
            itemView.setOnClickListener(v -> {
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
