package com.alphaCoaching.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PDFAdapter extends FirestoreRecyclerAdapter<PDFModel, PDFAdapter.PDFViewHolder> {

    public FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
    private OnPdfItemClick onPdfItemClick;

    public PDFAdapter(@NonNull FirestoreRecyclerOptions<PDFModel> options, OnPdfItemClick onPdfItemClick) {
        super(options);
        this.onPdfItemClick = onPdfItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull PDFViewHolder holder, int position, @NonNull PDFModel model) {


        //  holder.pdfSubject.setText(model.getSubject());
        holder.pdfName.setText(model.getPDFName());
        String subject = model.getSubject();
        DocumentReference documentReference = mFireBaseDB.collection("subjects").document(subject);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        holder.pdfSubject.setText((String) document.get("name"));
                        Log.d("PdfListActivity", document.getId() + "   " + document.get("name"));
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new PDFAdapter.PDFViewHolder(view, onPdfItemClick);
    }

    public class PDFViewHolder extends RecyclerView.ViewHolder {
        private TextView pdfName;
        private TextView pdfSubject;
        OnPdfItemClick onPdfItemClick;

        public PDFViewHolder(@NonNull View itemView, OnPdfItemClick onPdfItemClick) {
            super(itemView);
            pdfName = itemView.findViewById(R.id.pdfName);
            pdfSubject = itemView.findViewById(R.id.pdfSubject);
            this.onPdfItemClick = onPdfItemClick;
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onPdfItemClick != null) {
                    onPdfItemClick.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });

        }
//
//        @Override
//        public void onClick(View view) {
//            onPdfItemClick.onItemClick(getItem(getAdapterPosition()),getAdapterPosition());
//        }


    }

    public interface OnPdfItemClick {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }


}
