package com.alphaCoaching.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder> {

    public FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
    private OnPdfItemClick onPdfItemClick;
    private ArrayList<PDFModel> pdfList;

    public PDFAdapter(@NonNull ArrayList<PDFModel> options, OnPdfItemClick onPdfItemClick) {
        pdfList = options;
        this.onPdfItemClick = onPdfItemClick;
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new PDFAdapter.PDFViewHolder(view, onPdfItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder holder, int position) {
        PDFModel model = pdfList.get(position);
        holder.pdfName.setText(model.getPDFName());
        holder.pdfSubject.setText(UserSharedPreferenceManager.getUserSubject(getAppContext(), model.getSubject()));
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
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
                    onPdfItemClick.onItemClick(pdfList.get(position), position);
                }
            });

        }

    }

    public interface OnPdfItemClick {
        void onItemClick(PDFModel snapshot, int position);
    }


}
